import jdk.jshell.spi.ExecutionControl;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.Point;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;

public class Checkers {
    public static final int CHECKER_PLAYER_PAWNS = 12;
    public static final  int      CHECKER_SIZE = 8;
    private static final String[] ALPHABET     = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    private final        Pawn[][] board        = new Pawn[8][8];
    private final Player[] players = new Player[2];
    private int currentPlayerNumber;

    private final static Scanner scanner = new Scanner(System.in);

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Checkers(Player player1, Player player2){
        players[0] = player1;
        players[1] = player2;
        setPawnsInBoard(player1);
        setPawnsInBoard(player2);
    }

    public void play() throws ExecutionControl.NotImplementedException, InvalidAlgorithmParameterException {
        if(players[0].getColor().equals(players[1].getColor())) throw new IllegalArgumentException("Players cannot have the same colors");
        currentPlayerNumber = players[0].getColor().equals(Pawn.Color.WHITE) ? 0 : 1;
        while (true) {
            printBoard();
            System.out.println(players[currentPlayerNumber].getName());
            nextMove(players[currentPlayerNumber]);
            currentPlayerNumber = getOpponent();
        }
    }

    private int getOpponent(){
        return currentPlayerNumber == 0 ? 1 : 0;
    }

    public void nextMove(Player player) throws ExecutionControl.NotImplementedException, InvalidAlgorithmParameterException {
        if (player.getpLayerType().equals(Player.PLayerType.USER))
            userMove(player);
        else
            throw new ExecutionControl.NotImplementedException("Computer mode is not implemented yet");
    }

    private static Point getPawnPoint(Pawn[][] board, Pawn pawn){
        for(int i = 0 ; i < board.length; i++){
            for (int  j = 0 ; j < board[i].length; j++){
                if(board[i][j] != null && board[i][j].equals(pawn))
                    return new Point(j,  i);
            }
        }
        return null;
    }

    public List<Triple<Pawn,Point, Point>> getPawnToBeTaken(Player player, Pawn[][] board){
        List<Triple<Pawn,Point, Point>> pawnToBeTaken = new ArrayList<>();
        for(Pawn pawn : player.getPawns()){
            var pawnPoint = Objects.requireNonNull(getPawnPoint(board, pawn));
            var pawnNeighbours = getNeighbours(pawnPoint);
            for(Point neighbour : pawnNeighbours) {
                Pawn neighborPawn = board[neighbour.y][neighbour.x];
                if (neighborPawn != null && !player.getPawns().contains(neighborPawn)) {
                    int nextX = pawnPoint.x - neighbour.x > 0 ? neighbour.x - 1 : neighbour.x + 1;
                    int nextY = pawnPoint.y - neighbour.y > 0 ? neighbour.y - 1 : neighbour.y + 1;
                    if(nextY >= 0 && nextX >= 0 && nextY < 8 && nextX < 8 &&  board[nextY][nextX] == null)
                        pawnToBeTaken.add(new ImmutableTriple<>(neighborPawn, pawnPoint, new Point(nextX, nextY)));
                }
            }
        }
        return pawnToBeTaken;
    }

    public void checkIfPawnChangeToQueen(Pawn[][] board, Pawn pawn){
        if(pawn.getType().equals(Pawn.Type.NORMAL)) {
            var pawnPoss = getPawnPoint(board, pawn);
            assert pawnPoss != null;
            if (pawn.getColor() == Pawn.Color.WHITE) {
                if (pawnPoss.y == CHECKER_SIZE - 1) {
                    pawn.setQueent();
                }
            } else {
                if (pawnPoss.y == 0) {
                    pawn.setQueent();
                }
            }
        }

    }

    private void userMove(Player player) throws InvalidAlgorithmParameterException {
        while(true) {
            var source      = readPosition("Source Point: ");
            var destination = readPosition("Destination Point: ");
            var currentPawn = board[source.y][source.x];
            if (currentPawn == null || !player.getPawns().contains(currentPawn)) {
                System.out.println("Invalid pawn");
                continue;
            }
            List<Triple<Pawn, Point, Point>> pawnToBeTaken = getPawnToBeTaken(player, board);
            if(!pawnToBeTaken.isEmpty()){
                var filteredPawn = pawnToBeTaken.stream().filter(p -> p.getMiddle().equals(source) && p.getRight().equals(destination)).toList();
                if(filteredPawn.isEmpty()){
                    System.out.println("You have to take one of the pawns: " + pawnToBeTaken.stream().map(Triple::getLeft).toList());
                    continue;
                } else if (filteredPawn.size() != 1){
                    throw new InvalidAlgorithmParameterException();
                } else {
                    Pawn takenPawn = filteredPawn.get(0).getLeft();
                    Point takenPawnPos = getPawnPoint(board, takenPawn);
                    board[destination.y][destination.x] = currentPawn;
                    board[source.y][source.x] = null;
                    assert takenPawnPos != null;
                    board[takenPawnPos.y][takenPawnPos.x] = null;
                    players[getOpponent()].takePawn(takenPawn);
                    checkIfPawnChangeToQueen(board, currentPawn);
                    return;
                }
            }
            if (getPossibleMoves(player, board, source).contains(destination)) {
                board[destination.y][destination.x] = currentPawn;
                board[source.y][source.x] = null;
                checkIfPawnChangeToQueen(board, currentPawn);
                return;
            } else {
                System.out.println("Move is not possible. Try Again!");
            }
        }
    }

    private Point readPosition(String message){
        int x;
        int y;
        while (true) {
            System.out.println(message);
            var source = scanner.nextLine().split("");
            if (source.length != 2) {
                System.out.println("Incorrect input length");
                continue;
            }
            if(Character.isDigit(source[0].charAt(0))){
                x =  source[1].toUpperCase().charAt(0) - 65;
                y = Integer.parseInt(source[0]) - 1;
            } else {
                x = source[0].toUpperCase().charAt(0) - 65;
                y = Integer.parseInt(source[1]) - 1;
            }
            if( y >= CHECKER_SIZE || x >= CHECKER_SIZE){
                System.out.println("Incorrect value " + Arrays.toString(source));
                continue;
            }
            return new Point(x, y);
        }
    }

    private static List<Point> getPossibleMoves(Player player, Pawn[][] board, Point currentPos){
        Pawn currentPawn = board[currentPos.y][currentPos.x];
        List<Point> results = new ArrayList<>();
        if (currentPawn == null) return results;
        results = currentPawn.getType().equals(Pawn.Type.NORMAL) ? getFrontNeighbours(player, currentPos) : getDiagonalFields(currentPos);
        return results.stream().filter(f -> board[f.y][f.x] == null).toList();
    }

    private static List<Point> getNeighbours(Point currentPos){
        ArrayList<Point> availableMoves = new ArrayList<>();
        if(isValidPos(currentPos.x - 1, currentPos.y - 1)) availableMoves.add(new Point(currentPos.x - 1, currentPos.y - 1));
        if(isValidPos(currentPos.x - 1, currentPos.y + 1)) availableMoves.add(new Point(currentPos.x - 1, currentPos.y + 1));
        if(isValidPos(currentPos.x + 1, currentPos.y - 1)) availableMoves.add(new Point(currentPos.x + 1, currentPos.y - 1));
        if(isValidPos(currentPos.x + 1, currentPos.y + 1)) availableMoves.add(new Point(currentPos.x + 1, currentPos.y + 1));
        return availableMoves;
    }

    private static List<Point> getFrontNeighbours(Player player, Point currentPos){
        if(player.getColor().equals(Pawn.Color.WHITE)){
            return getNeighbours(currentPos).stream().filter(p -> p.y > currentPos.y).toList();
        } else{
            return getNeighbours(currentPos).stream().filter(p -> p.y < currentPos.y).toList();
        }
    }

    private static List<Point> getDiagonalFields(Point currentPos){
        ArrayList<Point> availableMoves = new ArrayList<>();
        for(int i = 0; i < CHECKER_SIZE; i++){
            for(int j = 0; j < CHECKER_SIZE; j++){
                if(currentPos.x - i == currentPos.y - j){
                    availableMoves.add(new Point(i,j));
                }
            }
        }
        return availableMoves;
    }

    private static boolean isValidPos(int x, int y){
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public void setPawnsInBoard(Player player){
        var pawnIterator = player.getPawns().listIterator();
        while(pawnIterator.hasNext()) {
            if (player.getColor().equals(Pawn.Color.WHITE)) {
                for (int i = 0; i < 3; i++) {
                    if (i % 2 == 0) {
                        for (int j = 0; j < CHECKER_SIZE; j++) {
                            if (j % 2 == 1) {
                                board[i][j] = pawnIterator.next();
                            }
                        }
                    } else {
                        for (int j = 0; j < CHECKER_SIZE; j++) {
                            if (j % 2 == 0) {
                                board[i][j] = pawnIterator.next();
                            }
                        }
                    }
                }
            } else {
                for (int i = CHECKER_SIZE - 1; i > CHECKER_SIZE - 4; i--) {
                    if (i % 2 == 0) {
                        for (int j = 0; j < CHECKER_SIZE; j++) {
                            if (j % 2 == 1) {
                                board[i][j] = pawnIterator.next();
                            }
                        }
                    } else {
                        for (int j = 0; j < CHECKER_SIZE; j++) {
                            if (j % 2 == 0) {
                                board[i][j] = pawnIterator.next();
                            }
                        }
                    }
                }
            }
        }
    }

    public void printBoard(){
        System.out.print("    ");
        for(int i = 0 ; i < CHECKER_SIZE; i++) {
            System.out.printf("     %s     ", ALPHABET[i]);
        }
        System.out.println();
        for(int i = 0; i < board.length; i++){
            System.out.printf("  %d ", i+1);
            for(var val : board[i]){
                System.out.print(" [ ");
                System.out.print( val != null ? val : "     ");
                System.out.print(" ] ");
            }
            System.out.printf("  %d \n", i+1);
        }
        System.out.print("    ");
        for(int i = 0 ; i < CHECKER_SIZE; i++) {
            System.out.printf("     %s     ", ALPHABET[i]);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Player player1 = new Player("P1", Pawn.Color.WHITE, Player.PLayerType.USER);
        Player player2 = new Player("P2", Pawn.Color.BLACK, Player.PLayerType.USER);
        var checkers = new Checkers(player1, player2);
        try {
            checkers.play();
        } catch (ExecutionControl.NotImplementedException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

}
