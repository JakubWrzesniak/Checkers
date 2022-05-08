package Game;

import Player.Player;

public class State<T, D> {
    private Player<T, D> player;
    private T      state;
    private int endCounter;

    public State(Player<T, D> player, T state){
        this.player = player;
        this.state = state;
        this.endCounter = 0;
    }

    public State(Player<T, D> player, T state, int endCounter){
        this.player = player;
        this.state = state;
        this.endCounter = endCounter;
    }

    public State(State<T, D> state){
        this.player = state.getPlayer();
        this.state = state.getState();
        this.endCounter = state.getEndCounter();
    }


    public Player<T, D> getPlayer() {
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

    public Integer getEndCounter() {
        return endCounter;
    }

    public void setEndCounter(Integer endCounter) {
        this.endCounter = endCounter;
    }
}
