package GameTreeAlgorithm;

import Game.Game;
import Game.State;
import Game.CheckerV2;
import Pawn.Pawn;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MiniMax <T,D> implements GameTreeAlgorithm<T,D>{

    int initialDepth;

    public MiniMax(int initialDepth){
        this.initialDepth = initialDepth;
    }
    public LinkedList<D> nextAction(State<T, D> state, Game<T, D> game){
        Map<Long, LinkedList<D>> results = new HashMap<>();
        for(var a : game.actions(state)){
            var v = minValue(game.result(state, a), game, initialDepth);
            results.put(v, a);
        }
        return results.get(results.keySet().stream().max(Long::compareTo).get());
    }

    public static <T, D>  long maxValue(State<T,D> state, Game<T,D> game, int depth){
        if(depth <= 0 || game.isEnd(state)){
            return game.utility(state);
        }
        var v = Long.MIN_VALUE;
        for(var a : game.actions(state)){
            v = Math.max(v, minValue(game.result(state, a), game, depth - 1));
        }
        return v;
    }

    public static <T, D> long minValue(State<T,D> state, Game<T,D> game, int depth){
        if(depth <=0 || game.isEnd(state)) {
            return game.utility(state);
        }
        var v = Long.MAX_VALUE;
        for(var a : game.actions(state)){
            v = Math.min(v, maxValue(game.result(state, a), game, depth - 1));
        }
        return v;
    }
}
