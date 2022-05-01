import jdk.jshell.spi.ExecutionControl;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.lang.reflect.Array;
import java.security.InvalidAlgorithmParameterException;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckerV2 implements Game<Pawn[][], Pair<Point, Point>>{
    public static final  int      CHECKER_SIZE = 8;
    private final Player[] players;
    private Integer endCounter = 0;

    public CheckerV2(Player player1, Player player2){
        this.players = new Player[]{player1, player2};
    }

    @Override
    public State<Pawn[][]> startState() {
       Pawn[][] board        = new Pawn[CHECKER_SIZE][CHECKER_SIZE];
       setPawnsInBoard(players[0], board);
       setPawnsInBoard(players[1], board);
       Player whitePlayer = players[0].getColor().equals(Pawn.Color.WHITE) ? players[0] : players[1];
       return new State<>(whitePlayer, board);
    }

    @Override
    public boolean isEnd(State<Pawn[][]> state) {
        return endCounter > 15 ||
                Arrays.stream(players).anyMatch(p -> getPlayerPawns(state.getState(), p).isEmpty()) ||
                actions(state).isEmpty();
    }

    @Override
    public float utility(State<Pawn[][]> state) {
        return 0;
    }

    @Override
    public List<LinkedList<Pair<Point, Point>>> actions(State<Pawn[][]> state) {
        List<Pawn> pawns = getPlayerPawns(state.getState(), state.getPlayer());
        List<LinkedList<Pair<Point, Point>>> captures = pawns.stream().map(p -> getCaptures(state.getState(), p)).flatMap(Collection::stream).toList();
        if(captures.isEmpty()){
           return  pawns.stream().map(p -> {
                int x = p.getPosition().x;
                int y = p.getPosition().y;
                Stream<Point> points;
                if(p.isQueen()) {
                    var temp = getDiagonalFields(p.getPosition());
                    points = temp.stream().filter(point -> {
                        if(state.getState()[point.x][point.y] == null){
                            var pointsBetween = getPointsBetween(p.getPosition(), point);
                            return pointsBetween.stream().noneMatch(pos -> {
                                var ppp = state.getState()[pos.x][pos.y];
                                return ppp != null && ppp.getColor() == state.getPlayer().getColor();
                            });
                        }
                        return false;
                    });
                }
                else{
                    points = Stream.of(new Point(x + 1, y+ 1), new Point(x - 1, y + 1), new Point(x + 1, y - 1), new Point(x - 1, y - 1)) .filter(point -> {
                        if(point.x < 0 || point.x >= CHECKER_SIZE || point.y < 0 || point.y >= CHECKER_SIZE) return false;
                        return state.getState()[point.x][point.y] == null;
                    });
                };
                return  points.map(point -> new LinkedList<>(List.of((Pair<Point, Point>) new ImmutablePair<>(p.getPosition(), point)))).toList();
            }).flatMap(Collection::stream).toList();
        } else {
            var theLongestMoves = captures.stream().map(List::size).max(Integer::compareTo);
            captures = captures.stream().filter(l -> l.size() == theLongestMoves.get()).toList();
        }
        return captures;
    }

    private static List<Point> getDiagonalFields(Point currentPos){
        ArrayList<Point> availableMoves = new ArrayList<>();
        for(int i = 0; i < CHECKER_SIZE; i++){
            for(int j = 0; j < CHECKER_SIZE; j++){
                if(Math.abs(currentPos.x - i) == Math.abs(currentPos.y - j)){
                    availableMoves.add(new Point(i,j));
                }
            }
        }
        availableMoves.remove(currentPos);
        return availableMoves;
    }

    public List<Pawn> getPlayerPawns(Pawn[][] board, Player player){
        List<Pawn> pawns = new ArrayList<>();
        for(var row: board){
            for(Pawn pawn : row){
                if (pawn != null && pawn.getColor().equals(player.getColor())){
                    pawns.add(pawn);
                }
            }
        }
        return pawns;
    }
    private List<LinkedList<Pair<Point, Point>>> getCaptures(Pawn[][] board, Pawn pawn){
        List<LinkedList<Pair<Point, Point>>> captures = new ArrayList<>();
        if(!pawn.isQueen()) {
            List<Pawn> neighbours = getNeighboursOpponent(board, pawn);
            for (Pawn neighbor : neighbours) {
                var capturePoint = getCapturePoint(board, pawn.getPosition(), neighbor.getPosition());
                if (capturePoint.isPresent()) {
                    var newAction    = new ImmutablePair<>(pawn.getPosition(), capturePoint.get());
                    findNextCaptures(board, pawn, captures, newAction);
                }
            }
        } else {
            List<Pawn> diagonalOpponent = getDiagonalOpponent(board, pawn);
            for(Pawn opponent : diagonalOpponent){
                var capturePoints = getCapturePoints(board, pawn.getPosition(), opponent.getPosition());
                for(var capturePoint : capturePoints){
                    var newAction    = new ImmutablePair<>(pawn.getPosition(), capturePoint);
                    findNextCaptures(board, pawn, captures, newAction);
                }
            }
        }
        return captures;
    }

    private void findNextCaptures(Pawn[][] board, Pawn pawn, List<LinkedList<Pair<Point, Point>>> captures,
                                  ImmutablePair<Point, Point> newAction) {
        var movedPawn = new Pawn(pawn);
        movedPawn.setPosition(newAction.getRight());
        var nextCaptures = getCaptures(succ(board, newAction), movedPawn);
        if (!nextCaptures.isEmpty()) {
            for (var nextCapture : nextCaptures) {
                nextCapture.add(0, newAction);
                captures.add(nextCapture);
            }
        } else {
            captures.add(new LinkedList<>(List.of(newAction)));
        }
    }


    private List<Pawn> getNeighboursOpponent(Pawn[][]board, Pawn pawn){
        List<Pawn> opponentPawns = getPlayerPawns(board, getOpponent(getPlayer(pawn.getColor())));
        return  opponentPawns.stream().filter(p -> {
            Point pawnPosition = p.getPosition();
            int x = pawnPosition.x;
            int y = pawnPosition.y;
            List<Point> possiblePoints = List.of(new Point(x + 1, y+ 1), new Point(x - 1, y + 1), new Point(x + 1, y - 1), new Point(x - 1, y - 1));
            return possiblePoints.contains(pawn.getPosition());
        }).toList();
    }

    private List<Pawn> getDiagonalOpponent(Pawn[][]board, Pawn pawn){
        return getPlayerPawns(board, getOpponent(getPlayer(pawn.getColor()))).stream().filter( p ->
                Math.abs(p.getPosition().x - pawn.getPosition().x) == Math.abs(p.getPosition().y - pawn.getPosition().y)).toList();
    }

    private Optional<Point> getCapturePoint(Pawn[][]board, Point pawnPoint, Point neighbour){
        int nextX = pawnPoint.x - neighbour.x > 0 ? neighbour.x - 1 : neighbour.x + 1;
        int nextY = pawnPoint.y - neighbour.y > 0 ? neighbour.y - 1 : neighbour.y + 1;
        if(nextY >= 0 && nextX >= 0 && nextY < CHECKER_SIZE && nextX < CHECKER_SIZE && board[nextX][nextY] == null)
            return Optional.of(new Point(nextX, nextY));
        return Optional.empty();
    }

    private List<Point> getCapturePoints(Pawn[][]board, Point pawnPoint, Point neighbour){
        var points = new ArrayList<Point>();
        if(pawnPoint.equals(new Point(3,6))){
            System.out.println("ToON");
        }
        if(getPointsBetween(pawnPoint, neighbour).stream().anyMatch(p -> board[p.x][p.y] != null)){
            return points;
        }

        int xSign = pawnPoint.x - neighbour.x > 0 ? -1 : 1;
        int ySign = pawnPoint.y - neighbour.y > 0 ? -1 : 1;
        int nextX = neighbour.x + xSign;
        int nextY = neighbour.y + ySign;
        while (nextY >= 0 && nextX >= 0 && nextY < CHECKER_SIZE && nextX < CHECKER_SIZE) {
            if (board[nextX][nextY] == null) {
                points.add(new Point(nextX, nextY));
            }
            nextX += xSign;
            nextY += ySign;
        }
        return points;
    }

    private void clearCapturedPoints(Pawn[][]board, Point start, Point end){
        int xSign = start.x < end.x ? 1 : -1;
        int ySign = start.y < end.y  ? 1 : -1;
        int nextX = start.x + xSign;
        int nextY = start.y + ySign;
        while (nextX != end.x && nextY != end.y) {
            if (board[nextX][nextY] != null) {
                board[nextX][nextY] = null;
                return;
            }
            nextX += xSign;
            nextY += ySign;
        }
    }

    private Player getOpponent(Player player){
        return players[0].equals(player) ? players[1] : players[0];
    }

    private Player getPlayer(Pawn.Color color){
        return  players[0].getColor().equals(color) ? players[0] : players[1];
    }

    @Override
    public Player player(State<Pawn[][]> state) {
        return state.getPlayer();
    }

    @Override
    public Pawn[][] succ(Pawn[][] state,  Pair<Point, Point> action) {
        Pawn[][] newBoard = new Pawn[state.length][state[0].length];
        for (int i = 0; i < state.length; i++) {
            for(int j = 0; j< state.length; j++){
                if(state[i][j] != null)
                    newBoard[i][j] = new Pawn(state[i][j]);
            }
        }
        var oldPawn = newBoard[action.getLeft().x][action.getLeft().y];
        newBoard[action.getLeft().x][action.getLeft().y] = null;
        oldPawn.setPosition(action.getRight());
        newBoard[action.getRight().x][action.getRight().y] = oldPawn;
        clearCapturedPoints(newBoard, action.getLeft(), action.getRight());
        return newBoard;
    }

    private boolean ifAllPawnAreQueen(Pawn[][] state){
        for (Pawn[] pawns : state) {
            for (Pawn pawn : pawns) {
                if (pawn != null && !pawn.isQueen())
                    return false;
            }
        }
        return true;
    }

    public void setPawnsInBoard(Player player, Pawn[][] board){
        List<Pawn> pawns = player.generatePawns();
        var pawnIterator = pawns.listIterator();
        while(pawnIterator.hasNext()) {
            if (player.getColor().equals(Pawn.Color.WHITE)) {
                for (int i = 0; i < 3; i++) {
                    if (i % 2 == 0) {
                        for (int j = 0; j < CHECKER_SIZE; j++) {
                            if (j % 2 == 1) {
                                board[j][i] = pawnIterator.next();
                                board[j][i].setPosition(new Point(j,i));
                            }
                        }
                    } else {
                        for (int j = 0; j < CHECKER_SIZE; j++) {
                            if (j % 2 == 0) {
                                board[j][i] = pawnIterator.next();
                                board[j][i].setPosition(new Point(j,i));
                            }
                        }
                    }
                }
            } else {
                for (int i = CHECKER_SIZE - 1; i > CHECKER_SIZE - 4; i--) {
                    if (i % 2 == 0) {
                        for (int j = 0; j < CHECKER_SIZE; j++) {
                            if (j % 2 == 1) {
                                board[j][i] = pawnIterator.next();
                                board[j][i].setPosition(new Point(j,i));
                            }
                        }
                    } else {
                        for (int j = 0; j < CHECKER_SIZE; j++) {
                            if (j % 2 == 0) {
                                board[j][i] = pawnIterator.next();
                                board[j][i].setPosition(new Point(j,i));
                            }
                        }
                    }
                }
            }
        }
    }
    public void play(){
        var state = startState();
        while (!isEnd(state)){
            printBoard(state.getState());
            var player = player(state);
            System.out.println("Player: " + player.getName());
            var actions = actions(state);
            System.out.println("Possible Actions:");
            for(var action : actions){
                for(var act : action){
                    System.out.print(MessageFormat.format("({0}, {1}) -> ({2}, {3}), ", "ABCDEFGH".charAt(act.getLeft().x), act.getLeft().y + 1, "ABCDEFGH".charAt(act.getRight().x), act.getRight().y + 1));
                }
                System.out.println();
            }
            var playerAction = player.getAction();
            for(var actionList: actions){
                if(actionList.pop().equals(playerAction)){
                    var newBoard = succ(state.getState(), playerAction);
                    state = new State<>(player, newBoard);
                    while (!actionList.isEmpty()){
                        printBoard(newBoard);
                        var nextAction = actionList.pop();
                        playerAction = player.getAction();
                        while (!playerAction.equals(nextAction)){
                            playerAction = player.getAction();
                        }
                        newBoard = succ(state.getState(), playerAction);
                        state = new State<>(player, newBoard);
                    }
                    state.setPlayer(getOpponent(state.getPlayer()));
                    if(ifAllPawnAreQueen(state.getState())){
                        endCounter++;
                    }
                }
            }
        }
    }

    public List<Point> getPointsBetween(Point start, Point end){
        List<Point> points = new ArrayList<>();
        int xSign = start.x < end.x ? 1 : -1;
        int ySign = start.y < end.y  ? 1 : -1;
        int nextX = start.x + xSign;
        int nextY = start.y + ySign;
        while (nextX != end.x && nextY != end.y) {
            points.add(new Point(nextX, nextY));
            nextX += xSign;
            nextY += ySign;
        }
        return points;
    }

    public void printBoard(Pawn[][] board){
        System.out.print("    ");
        for(int i = 0 ; i < CHECKER_SIZE; i++) {
            System.out.printf("     %s     ", "ABCDEFGHIJK".charAt(i));
        }
        System.out.println();
        for(int i = 0; i < board.length; i++){
            System.out.printf("  %d ", i + 1);
            for(int j = 0 ; j < board[i].length; j++){
                System.out.print(" [ ");
                System.out.print( board[j][i] != null ? board[j][i] : "     ");
                System.out.print(" ] ");
            }
            System.out.printf("  %d \n", i+1);
        }
        System.out.print("    ");
        for(int i = 0 ; i < CHECKER_SIZE; i++) {
            System.out.printf("     %s     ", "ABCDEFGHIJK".charAt(i));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Player player1 = new Player("P1", Pawn.Color.WHITE, Player.PLayerType.USER);
        Player player2 = new Player("P2", Pawn.Color.BLACK, Player.PLayerType.USER);
        CheckerV2 game = new CheckerV2(player1, player2);
        game.play();
    }
}
