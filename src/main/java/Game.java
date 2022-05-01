import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public interface Game<T, D> {
    State<T> startState();
    boolean isEnd(State<T> state);
    float utility(State<T> state);
    List<LinkedList<D>> actions(State<T> state);
    Player player(State<T> state);
    T succ(T state, D action);
}
