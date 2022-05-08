package Pawn;

import Game.CheckerV2;
import Game.CheckersUtils;
import Game.State;
import GameTreeAlgorithm.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QueenPawn extends Pawn {
    public QueenPawn(Color color, String name, CheckerV2 game) {
        super(color, name, game);
    }
    public QueenPawn(Pawn pawn){
        super(pawn);
    }

    @Override
    public List<LinkedList<Pair<Point, Point>>> getCaptures(
            State<Pawn[][], Pair<Point, Point>> state) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<LinkedList<Pair<Point, Point>>> captures = new ArrayList<>();
        List<Pawn> diagonalOpponent = getDiagonalOpponent(state.getState());
        for(Pawn opponent : diagonalOpponent){
            var capturePoints = CheckersUtils.getCapturePoints(state.getState(), getPosition(), opponent.getPosition());
            for(var capturePoint : capturePoints){
                var newAction    = new ImmutablePair<>(getPosition(), capturePoint);
                findNextCaptures(state, captures, newAction);
            }
        }
        return captures;
    }

    @Override
    public List<LinkedList<Pair<Point, Point>>> getMoves(State<Pawn[][], Pair<Point,Point>> state) {
        int x = getPosition().x;
        int y = getPosition().y;
        var board = state.getState();
        var temp = CheckersUtils.getDiagonalFields(getPosition(), board.length);
        var points = temp.stream().filter(point -> {
            if(board[point.x][point.y] == null){
                var pointsBetween = CheckersUtils.getPointsBetween(getPosition(), point);
                return pointsBetween.stream().noneMatch(pos -> {
                    var ppp = board[pos.x][pos.y];
                    return ppp != null && ppp.getColor() == state.getPlayer().getColor();
                });
            }
            return false;
        });
        return points.map(point -> new LinkedList<>(List.of((Pair<Point, Point>) new ImmutablePair<>(getPosition(), point)))).toList();
    }

    @Override
    public String toString() {
        return ANSI_RED + super.name + ANSI_RESET;
    }

    public List<Pawn> getDiagonalOpponent(Pawn[][]board){
        return game.getPlayerPawns(board, game.getOpponent(game.getPlayer(getColor()))).stream().filter( p ->
                Math.abs(p.getPosition().x - getPosition().x) == Math.abs(p.getPosition().y - getPosition().y)).toList();
    }
}
