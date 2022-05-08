package Pawn;

import Game.CheckerV2;
import Game.CheckersUtils;
import Game.State;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class NormalPawn extends Pawn{
    public NormalPawn(Color color, String name, CheckerV2 game) {
        super(color, name, game);
    }

    public NormalPawn(Pawn pawn){
        super(pawn);
    }

    @Override
    public List<LinkedList<Pair<Point, Point>>> getCaptures(State<Pawn[][], Pair<Point, Point>> board) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<LinkedList<Pair<Point, Point>>> captures = new ArrayList<>();
        List<Pawn> neighbours = getNeighboursOpponent(board.getState());
        for (Pawn neighbor : neighbours) {
            var capturePoint = CheckersUtils.getCapturePoint(board.getState(), getPosition(), neighbor.getPosition());
            if (capturePoint.isPresent()) {
                var newAction    = new ImmutablePair<>(getPosition(), capturePoint.get());
                super.findNextCaptures(board, captures, newAction);
            }
        }
        return captures;
    }

    @Override
    public List<LinkedList<Pair<Point, Point>>> getMoves(State<Pawn[][], Pair<Point, Point>> state) {
        int x = getPosition().x;
        int y = getPosition().y;
        var board = state.getState();
        var pointsList = getColor().equals(Color.WHITE) ?  Stream.of(new Point(x + 1, y + 1), new Point(x - 1, y + 1)) : Stream.of(new Point(x + 1, y - 1), new Point(x - 1, y - 1));
        var points = pointsList.filter(point -> {
            if(point.x < 0 || point.x >= board.length || point.y < 0 || point.y >= board[0].length ) return false;
            return board[point.x][point.y] == null;
        });
        return points.map(point -> new LinkedList<>(List.of((Pair<Point, Point>) new ImmutablePair<>(getPosition(), point)))).toList();
    }

    private List<Pawn> getNeighboursOpponent(Pawn[][]board){
        List<Pawn> opponentPawns = game.getPlayerPawns(board, game.getOpponent(game.getPlayer(getColor())));
        return  opponentPawns.stream().filter(p -> {
            Point pawnPosition = p.getPosition();
            int x = pawnPosition.x;
            int y = pawnPosition.y;
            List<Point> possiblePoints = List.of(new Point(x + 1, y+ 1), new Point(x - 1, y + 1), new Point(x + 1, y - 1), new Point(x - 1, y - 1));
            return possiblePoints.contains(getPosition());
        }).toList();
    }
}
