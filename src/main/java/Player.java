import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String     name;
    private final List<Pawn> pawns;
    private final Pawn.Color color;
    private final PLayerType pLayerType;


    public Player(String name, List<Pawn> pawns, Pawn.Color color, PLayerType pLayerType) {
        this.name = name;
        this.pawns = pawns;
        this.color = color;
        this.pLayerType = pLayerType;
    }

    public Player(String name, Pawn.Color color, PLayerType pLayerType) {
        this(name, new ArrayList<>(), color, pLayerType);
        generatePawns();
    }

    public String getName() {
        return name;
    }

    public Pawn.Color getColor() {
        return color;
    }

    public List<Pawn> getPawns() {
        return pawns;
    }

    public PLayerType getpLayerType() {
        return pLayerType;
    }

    public void generatePawns(){
        for(int i = 0; i < Checkers.CHECKER_PLAYER_PAWNS; i++){
            pawns.add(new Pawn(color,String.format("%s_%02d", name, i), Pawn.Type.NORMAL));
        }
    }

    public void takePawn(Pawn pawn){
        pawns.remove(pawn);
    }

    enum PLayerType {
        USER, COMPUTER
    }
}
