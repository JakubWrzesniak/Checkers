package Player;

import Game.Game;
import Pawn.*;
import Player.Player;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UserPlayer extends Player<Pawn[][], Pair<Point, Point>> {

    public UserPlayer(String name, Color color) {
        super(name, color);
    }

    @Override
    public Pair<Point, Point> getMove(Game<Pawn[][], Pair<Point, Point>> game) {
        AtomicReference<Pair<Point, Point>> action = new AtomicReference<>();
        do {
            if(action.get() != null){
                System.out.println("Action is not possible");
            }
            var source      = readPosition("Source Point: ");
            var destination = readPosition("Destination Point: ");
            action.set(new ImmutablePair<>(source, destination));
        } while (game.actions(game.getState()).stream().noneMatch(a -> a.getFirst().equals(action.get())));
        return action.get();
    }
}
