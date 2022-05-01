import java.util.ArrayList;
import java.util.List;

public class State<T> {
    private Player player;
    private T state;

    public State(Player player, T state){
        this.player = player;
        this.state = state;
    }

    public Player getPlayer() {
        return player;
    }

    public T getState() {
        return state;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setState(T state) {
        this.state = state;
    }
}
