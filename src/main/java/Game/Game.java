package Game;

import Player.Player;

import java.util.LinkedList;
import java.util.List;

public interface Game<T, D> {
    State<T, D> startState();
    boolean isEnd(State<T, D> state);
    long utility(State<T, D> state);
    List<LinkedList<D>> actions(State<T, D> state);
    Player<T, D> player(State<T, D> state);
    State<T, D> succ(State<T, D> state, D action);
    State<T, D> getState();
    State<T,D> result(State<T,D> state, LinkedList<D> actions);
    Player<T, D> getWinner(State<T,D> state);
    long eval(State<T,D> state);
}
