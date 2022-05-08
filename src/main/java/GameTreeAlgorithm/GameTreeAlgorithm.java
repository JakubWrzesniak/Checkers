package GameTreeAlgorithm;

import Game.Game;
import Game.State;

import java.util.LinkedList;

public interface GameTreeAlgorithm<T, D> {
    LinkedList<D> nextAction(State<T,D> state, Game<T,D> game);
}
