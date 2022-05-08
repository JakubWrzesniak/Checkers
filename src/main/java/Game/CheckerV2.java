package Game;

import GameHeuristic.Heuristic1;
import GameHeuristic.Heuristic2;
import GameHeuristic.HeuristicInterface;
import GameTreeAlgorithm.AlphaBeta;
import GameTreeAlgorithm.GameTreeAlgorithm;
import GameTreeAlgorithm.MiniMax;
import Pawn.*;
import Player.*;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

public class CheckerV2 implements Game<Pawn[][], Pair<Point, Point>>{
    public static final  int      CHECKER_SIZE = 8;
    private final Player<Pawn[][], Pair<Point, Point>> [] players;
    private State<Pawn[][], Pair<Point, Point>> state;

    private final HeuristicInterface heuristic;

    @SuppressWarnings({})
    public CheckerV2(Player<Pawn[][], Pair<Point, Point>> player1, Player<Pawn[][], Pair<Point, Point>> player2, HeuristicInterface heuristic){
        var players = new Player[]{player1, player2};
        this.heuristic = heuristic;
        this.players = (Player<Pawn[][], Pair<Point, Point>>[]) players;
        state = startState();
    }

    @Override
    public State<Pawn[][], Pair<Point, Point>> startState() {
       Pawn[][] board        = new Pawn[CHECKER_SIZE][CHECKER_SIZE];
       setPawnsInBoard(players[0], board);
       setPawnsInBoard(players[1], board);
       Player<Pawn[][], Pair<Point, Point>> whitePlayer = players[0].getColor().equals(Color.WHITE) ? players[0] : players[1];
       return new State<Pawn[][], Pair<Point, Point>>(whitePlayer, board);
    }

    @Override
    public boolean isEnd(State<Pawn[][], Pair<Point, Point>> state) {
        return state.getEndCounter() > 15 ||
                Arrays.stream(players).anyMatch(p -> getPlayerPawns(state.getState(), p).isEmpty()) ||
                actions(state).isEmpty();
    }

    @Override
    public long utility(State<Pawn[][], Pair<Point, Point>> state) {
        return heuristic.utility(state.getState(), this.state.getPlayer(), this);
    }

    public long positionValue(Pawn pawn){
        var pos = pawn.getPosition();
        var x = pos.x;
        var y = pos.y;

        if( x == 0 || x == 7 || y == 0 || y == 7 )
            return 10;
        if(x == 1 || x == 6 || y == 1 || y == 6 )
            return 5;
        return 1;
    }

    @Override
    public List<LinkedList<Pair<Point, Point>>> actions(State<Pawn[][], Pair<Point, Point>> state) {
        List<Pawn> pawns = getPlayerPawns(state.getState(), state.getPlayer());
        List<LinkedList<Pair<Point, Point>>> captures = pawns.stream().map(p -> {
            try {
                return p.getCaptures(state);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).flatMap(Collection::stream).toList();
        if(captures.isEmpty()){
            return  pawns.stream().map(p ->
                p.getMoves(state)
            ).flatMap(Collection::stream).toList();
        }
        var theLongestMoves = captures.stream().map(List::size).max(Integer::compareTo);
        captures = captures.stream().filter(l -> l.size() == theLongestMoves.get()).toList();
        return captures;
    }
    public List<Pawn> getPlayerPawns(Pawn[][] board, Player<Pawn[][], Pair<Point, Point>> player){
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
    private boolean clearCapturedPoints(Pawn[][]board, Point start, Point end){
        int xSign = start.x < end.x ? 1 : -1;
        int ySign = start.y < end.y  ? 1 : -1;
        int nextX = start.x + xSign;
        int nextY = start.y + ySign;
        while (nextX != end.x && nextY != end.y) {
            if (board[nextX][nextY] != null) {
                board[nextX][nextY] = null;
                return true;
            }
            nextX += xSign;
            nextY += ySign;
        }
        return false;
    }

    public Player<Pawn[][], Pair<Point, Point>> getOpponent(Player<Pawn[][], Pair<Point, Point>> player){
        return players[0].equals(player) ? players[1] : players[0];
    }

    public Player<Pawn[][], Pair<Point, Point>> getPlayer(Color color){
        return  players[0].getColor().equals(color) ? players[0] : players[1];
    }

    @Override
    public Player<Pawn[][], Pair<Point, Point>> player(State<Pawn[][], Pair<Point, Point>> state) {
        return state.getPlayer();
    }

    @Override
    public State<Pawn[][], Pair<Point, Point>> succ(State<Pawn[][], Pair<Point, Point>> state,  Pair<Point, Point> action) {
        Pawn[][] newBoard;
        try {
            newBoard = CheckersUtils.copyBoard(state.getState());
            var oldPawn = newBoard[action.getLeft().x][action.getLeft().y];
            newBoard[action.getLeft().x][action.getLeft().y] = null;
            oldPawn.setPosition(action.getRight());
            if(Pawn.checkIfPawnChangeToQueen(oldPawn)){
                oldPawn = new QueenPawn(oldPawn);
            }
            newBoard[action.getRight().x][action.getRight().y] = oldPawn;
            if(clearCapturedPoints(newBoard, action.getLeft(), action.getRight())) {
                state.setEndCounter(0);
            }
            else if (oldPawn instanceof NormalPawn){
                state.setEndCounter(0);
            }
            else if (oldPawn instanceof QueenPawn) {
                state.setEndCounter(state.getEndCounter() + 1);
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return new State<Pawn[][], Pair<Point, Point>>(state.getPlayer(), newBoard, state.getEndCounter()) ;
    }

    @Override
    public State<Pawn[][], Pair<Point, Point>> getState(){
        return state;
    }

    @Override
    public State<Pawn[][], Pair<Point, Point>> result(State<Pawn[][], Pair<Point, Point>> state,
                                                                LinkedList<Pair<Point, Point>> actions) {
        var newState = new State<Pawn[][], Pair<Point, Point>>(state);
        for(var a : actions){
            newState = succ(newState, a);
        }
        newState.setPlayer(getOpponent(state.getPlayer()));
        return newState;

    }

    @Override
    public Player<Pawn[][], Pair<Point, Point>> getWinner(State<Pawn[][], Pair<Point, Point>> state) {
        for(var p : players){
            if(getPlayerPawns(state.getState(), p).isEmpty())
                return getOpponent(p);
        }
        return null;
    }

    @Override
    public long eval(State<Pawn[][], Pair<Point, Point>> state) {
       return getPlayerPawns(state.getState(), state.getPlayer()).stream().map(p -> p instanceof QueenPawn ? 5L : 2L
       ) .reduce(Long::sum).orElse(0L);
    }

    private boolean ifAllPawnsAreQueen(Pawn[][] state){
        for (Pawn[] pawns : state) {
            for (Pawn pawn : pawns) {
                if (pawn != null && !(pawn instanceof QueenPawn))
                    return false;
            }
        }
        return true;
    }

    private void setPawnsInBoard(Player<Pawn[][], Pair<Point, Point>> player, Pawn[][] board){
        List<Pawn> pawns = player.generatePawns(12, this);
        var pawnIterator = pawns.listIterator();
        while(pawnIterator.hasNext()) {
            if (player.getColor().equals(Color.WHITE)) {
                for (int i = 0; i < 3; i++) {
                    setPawnsOnBoard(board, pawnIterator, i);
                }
            } else {
                for (int i = CHECKER_SIZE - 1; i > CHECKER_SIZE - 4; i--) {
                    setPawnsOnBoard(board, pawnIterator, i);
                }
            }
        }
    }

    private void setPawnsOnBoard(Pawn[][] board, ListIterator<Pawn> pawnIterator, int i) {
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

    public void nextMove(List<LinkedList<Pair<Point, Point>>> actions){
            printBoard(state.getState());
            var player = player(state);
            System.out.println("Player.Player: " + player.getName());
            printActions(actions);
            final var playerAction = player.getMove(this);
            actions = actions.stream().filter(a -> a.getFirst().equals(playerAction)).toList();
            state = succ(state, playerAction);
            actions.forEach(LinkedList::pop);
            if(actions.stream().allMatch(AbstractCollection::isEmpty)) {
                state.setPlayer(getOpponent(state.getPlayer()));
            } else {
                nextMove(actions);
            }
    }

    public void nextMove(){
        while (!isEnd(state)) {
            nextMove(actions(state));
        }
    }

    public void printActions(List<LinkedList<Pair<Point, Point>>> actions){
        System.out.println("Possible Actions:");
        for(var action : actions){
            for(var act : action){
                System.out.print(MessageFormat.format("({0}, {1}) -> ({2}, {3}), ", "ABCDEFGH".charAt(act.getLeft().x), act.getLeft().y + 1, "ABCDEFGH".charAt(act.getRight().x), act.getRight().y + 1));
            }
            System.out.println();
        }
    }

    public void play(){
        nextMove();
        printBoard(state.getState());
        var winner = getWinner(state);
        if(winner != null){
            System.out.println("Winner: " + winner);
        } else {
            System.out.println("Draw");
        }
        for(var p : players){
            if(p instanceof ComputerPlayer){
                System.out.println(MessageFormat.format("""
                    player : {0}
                    time : {1}
                    moves : {2}
                    """, p , ((ComputerPlayer<Pawn[][], Pair<Point, Point>>) p).getSelectMoveTime(), ((ComputerPlayer<Pawn[][], Pair<Point, Point>>) p).getNumberOfMoves()));
            }
        }
    }

    public static void printBoard(Pawn[][] board){
        System.out.print("   ");
        for(int i = 0 ; i < CHECKER_SIZE; i++) {
            System.out.printf("   %s  ", "ABCDEFGHIJK".charAt(i));
        }
        System.out.println();
        for(int i = 0; i < board.length; i++){
            System.out.printf(" %d ", i + 1);
            for(int j = 0 ; j < board[i].length; j++){
                System.out.print("[");
                System.out.print( board[j][i] != null ? board[j][i] : "    ");
                System.out.print("]");
            }
            System.out.printf("  %d \n", i+1);
        }
        System.out.print("   ");
        for(int i = 0 ; i < CHECKER_SIZE; i++) {
            System.out.printf("   %s  ", "ABCDEFGHIJK".charAt(i));
        }
        System.out.println();
    }

    public static void main(String[] args) {

        CheckerV2 game = selectGameConfiguration();
        game.play();
    }

    public static int selectLevel(){
        System.out.println("Select level: ");
        Scanner scanner = new Scanner(System.in);
        var selectedLevel = scanner.nextInt();
        if(selectedLevel < 0){
            System.out.println("Level must be positive:");
            return selectLevel();
        }
        return selectedLevel;
    }

    public static HeuristicInterface selectHeuristic(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                Select game heuristic:
                1 - Pawns and captures
                2 - position""");
        int selectHeuristic = scanner.nextInt();
        if(selectHeuristic < 0 || selectHeuristic > 2){
            System.out.println("Incorrect value");
            return selectHeuristic();
        }
        switch (selectHeuristic){
            case 2:
                return new Heuristic2();
            case 1 :
            default:
                return new Heuristic1();
        }
    }

    public static GameTreeAlgorithm<Pawn[][], Pair<Point, Point>> selectAlgorithm(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                Select game algorithm:
                1 - MiniMax
                2 - AlphaBeta""");
        int selectAlgorithm = scanner.nextInt();
        if(selectAlgorithm < 0 || selectAlgorithm > 2){
            System.out.println("Incorrect value");
            return selectAlgorithm();
        }
        switch (selectAlgorithm){
            case 2:
                return new AlphaBeta<Pawn[][], Pair<Point, Point>>(selectLevel());
            case 1 :
            default:
                return new MiniMax<Pawn[][], Pair<Point, Point>>(selectLevel());
        }
    }

    public static CheckerV2 selectGameConfiguration() {
        System.out.println("""
                Select game mode:
                1 - Player vs Player
                2 - AI vs Player
                3 - Player vs AI
                4 - AI vs AI
                """);
        Scanner                              scanner       = new Scanner(System.in);
        Player<Pawn[][], Pair<Point, Point>> player1;
        Player<Pawn[][], Pair<Point, Point>> player2;
        int                                  selectedValue = scanner.nextInt();
        if (selectedValue <= 0 || selectedValue > 4) {
            System.out.println("Incorrect Value");
            return selectGameConfiguration();
        }
        switch (selectedValue) {
            case 1:
                player1 = new UserPlayer("W", Color.WHITE);
                player2 = new UserPlayer("B", Color.BLACK);
                break;
            case 2:
                player1 = new ComputerPlayer<Pawn[][], Pair<Point, Point>>("W", Color.WHITE,
                        selectAlgorithm());
                player2 = new UserPlayer("B", Color.BLACK);
                break;
            case 3:
                player1 = new ComputerPlayer<Pawn[][], Pair<Point, Point>>("B", Color.BLACK,
                        selectAlgorithm());
                player2 = new UserPlayer("W", Color.WHITE);
                break;
            case 4:
            default:
                player1 = new ComputerPlayer<Pawn[][], Pair<Point, Point>>("W", Color.WHITE,
                        selectAlgorithm());
                player2 = new ComputerPlayer<Pawn[][], Pair<Point, Point>>("B", Color.BLACK,
                        selectAlgorithm());
        }
        return new CheckerV2(player1, player2, selectHeuristic());
    }
}
