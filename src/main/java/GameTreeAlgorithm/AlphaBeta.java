package GameTreeAlgorithm;

import Game.Game;
import Game.State;

import java.util.*;

public class AlphaBeta<T,D> implements  GameTreeAlgorithm<T,D>{
    private final int startDepth;

    public AlphaBeta(int depth){
        this.startDepth = depth;
    }

    public LinkedList<D> nextAction(State<T,D> state, Game<T,D> game){
        Map<Long, LinkedList<D>> results = new HashMap<>();
        for(var a : game.actions(state)){
            var v = alphaBetaMin(game.result(state, a), game, startDepth, Long.MIN_VALUE, Long.MAX_VALUE);
            results.put(v, a);
        }
        return results.get(results.keySet().stream().max(Long::compareTo).get());
    }

    public long alphaBetaMin(State<T,D> state, Game<T,D>game, int depth, Long alpha, Long betha){
        if(depth <= 0 || game.isEnd(state)){
            return game.utility(state);
        }
        var v = Long.MAX_VALUE;
        var actions = new ArrayList<>(game.actions(state));
        actions.sort((p1, p2) -> (int) (game.eval(game.result(state, p1)) - game.eval(game.result(state, p2))));
        for(var a : actions){
            v = Math.min(v, alphaBetaMax(game.result(state, a), game, depth - 1, alpha, betha));
            if( v <= alpha) {
                return v;
            }
            betha = Math.min(betha, v);
        }
        return betha;
    }

    public long alphaBetaMax(State<T,D> state, Game<T,D>game, int depth, Long alpha, Long betha){
        if(depth <= 0 || game.isEnd(state)){
            return game.utility(state);
        }
        var v = Long.MIN_VALUE;
        var actions = new ArrayList<>(game.actions(state));
        actions.sort((p1, p2) -> (int) (game.eval(game.result(state, p2)) - game.eval(game.result(state, p1))));
        for(var a : actions){
            v = Math.max(v, alphaBetaMin(game.result(state, a), game, depth - 1, alpha, betha));
            if(v >= betha){
                return v;
            }
            alpha = Math.max(alpha, v);
        }
        return alpha;
    }
}
