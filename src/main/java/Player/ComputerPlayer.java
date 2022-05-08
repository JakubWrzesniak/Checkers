package Player;

import Game.Game;
import GameTreeAlgorithm.AlphaBeta;
import GameTreeAlgorithm.GameTreeAlgorithm;
import Pawn.Color;
import Pawn.Pawn;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.time.Duration;
import java.time.LocalTime;

public class ComputerPlayer<T, D> extends Player<T, D>{
    private final GameTreeAlgorithm<T,D> gameTreeAlgorithm;
    private long selectMoveTime = 0;
    private int numberOfMoves = 0;

    public ComputerPlayer(String name, Color color, GameTreeAlgorithm<T,D> gameTreeAlgorithm) {
        super(name, color);
        this.gameTreeAlgorithm = gameTreeAlgorithm;
    }

    @Override
    public D getMove(Game<T,D> game) {
        LocalTime startTime = LocalTime.now();
        var move = gameTreeAlgorithm.nextAction(game.getState(), game).pop();
        LocalTime endTime = LocalTime.now();
        selectMoveTime += Duration.between(startTime, endTime).toMillis();
        numberOfMoves++;
        return move;
    }

    public long getSelectMoveTime() {
        return selectMoveTime;
    }

    public int getNumberOfMoves(){
        return numberOfMoves;
    }
}
