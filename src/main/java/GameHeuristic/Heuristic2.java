package GameHeuristic;

import Game.CheckerV2;
import Pawn.Pawn;
import Player.Player;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;

public class Heuristic2 implements HeuristicInterface{
    @Override
    public long utility(Pawn[][] board, Player<Pawn[][], Pair<Point, Point>> player, CheckerV2 game) {
        var playerPawns = game.getPlayerPawns(board, player);
        var opponentPawns = game.getPlayerPawns(board, game.getOpponent(player));
        return playerPawns.stream().map(game::positionValue).reduce(Long::sum).orElse(0L)
                + opponentPawns.stream().map(p -> - game.positionValue(p)).reduce(Long::sum).orElse(0L);
    }
}
