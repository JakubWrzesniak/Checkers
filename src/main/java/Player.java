import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Player {
    private final String     name;
    private final Pawn.Color color;
    private final PLayerType pLayerType;


    public Player(String name, Pawn.Color color, PLayerType pLayerType) {
        this.name = name;
        this.color = color;
        this.pLayerType = pLayerType;
    }

    public String getName() {
        return name;
    }

    public Pawn.Color getColor() {
        return color;
    }

    public PLayerType getpLayerType() {
        return pLayerType;
    }

    public Pair<Point, Point> getAction(){
        var source      = readPosition("Source Point: ");
        var destination = readPosition("Destination Point: ");
        return new ImmutablePair<>(source, destination);
    }

    public List<Pawn> generatePawns(){
        List<Pawn> pawns = new ArrayList<>();
        for(int i = 1; i < 13; i++){
            pawns.add(new Pawn(color,String.format("%s_%02d", name, i), Pawn.Type.NORMAL));
        }
        return pawns;
    }

    private Point readPosition(String message){
        int x;
        int y;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(message);
            var source = scanner.nextLine().split("");
            if (source.length != 2) {
                System.out.println("Incorrect input length");
                continue;
            }
            if(Character.isDigit(source[0].charAt(0))){
                x =  source[1].toUpperCase().charAt(0) - 65;
                y = Integer.parseInt(source[0]) - 1;
            } else {
                x = source[0].toUpperCase().charAt(0) - 65;
                y = Integer.parseInt(source[1]) - 1;
            }
            if( y >= 8 || x >= 8){
                System.out.println("Incorrect value " + Arrays.toString(source));
                continue;
            }
            return new Point(x, y);
        }
    }

    enum PLayerType {
        USER, COMPUTER
    }
}
