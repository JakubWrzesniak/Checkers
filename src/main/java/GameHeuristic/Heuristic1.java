package GameHeuristic;

import Game.CheckerV2;
import Game.Game;
import Game.State;
import Pawn.*;
import Player.Player;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class Heuristic1 implements HeuristicInterface{

    @Override
    public long utility(Pawn[][] board, Player<Pawn[][], Pair<Point, Point>> player, CheckerV2 game) {
        var playerPawns = game.getPlayerPawns(board, player);
        var opponentPawns = game.getPlayerPawns(board, game.getOpponent(player));
        var playerCaptures = playerPawns.stream().map(p -> {
            try {
                return p.getCaptures(new State<Pawn[][], Pair<Point, Point>>(player, board, 0));
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).flatMap(Collection::stream).toList();
        var opponentCaptures = opponentPawns.stream().map(p -> {
            try {
                return p.getCaptures(new State<Pawn[][], Pair<Point, Point>>(game.getOpponent(player), board, 0));
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).flatMap(Collection::stream).toList();

        var res = (long) playerCaptures.stream().map(p -> p.size() * 10).reduce(Integer::sum).orElse(0)
                + (long) opponentCaptures.stream().map(p -> -p.size() * 10).reduce(Integer::sum).orElse(0)
                + ((game.actions(new State<Pawn[][], Pair<Point, Point>>(player, board, 0)).size() <= 0) ? - 100L : 0L)
                + ((game.actions(new State<Pawn[][], Pair<Point, Point>>(game.getOpponent(player), board, 0)).size() <= 0) ? 100L : 0L)
                + playerPawns.stream().map(p -> p instanceof QueenPawn ? 20 : 10).reduce(Integer::sum).orElse(0)
                + opponentPawns.stream().map(p -> p instanceof QueenPawn ? -20 : -10).reduce(Integer::sum).orElse(0);
        return res;
    }
}
