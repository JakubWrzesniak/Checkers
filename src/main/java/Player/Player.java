package Player;

import Game.CheckerV2;
import Game.Game;
import Pawn.*;
import Pawn.Color;

import java.awt.Point;
import java.util.*;
import java.util.List;

public abstract class Player<T, D> {
    private final String     name;
    private final Color color;


    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public abstract D getMove(Game<T,D> game);

    public List<Pawn> generatePawns(int numOfPawns, CheckerV2 game){
        List<Pawn> pawns = new ArrayList<>();
        for(int i = 0; i < numOfPawns; i++){
            pawns.add(new NormalPawn(color, String.format("%s_%02d", name, i + 1), game));
        }
        return pawns;
    }

    Point readPosition(String message){
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
    @Override
    public String toString() {
        return name;
    }
}
