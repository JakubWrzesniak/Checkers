package GameHeuristic;

import Game.*;
import Pawn.Pawn;
import Player.Player;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;

public interface HeuristicInterface {
    long utility(Pawn[][] board, Player<Pawn[][], Pair<Point, Point>> player, CheckerV2 game);
}
