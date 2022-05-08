package Pawn;

import Game.CheckerV2;
import Game.State;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class Pawn {
    private final Color color;
    protected String name;
    private             Point     position;
    protected final     CheckerV2 game;
    public static final String    ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Pawn(Color color, String name, CheckerV2 game) {
        this.color = color;
        this.name = name;
        this.game = game;
    }

    public Pawn(Pawn pawn){
        this.color = pawn.getColor();
        this.name = pawn.getName();
        this.position = pawn.getPosition();
        this.game = pawn.game;
    }

    public Pawn(Color color, String name, Point startPos, CheckerV2 game) {
        this.color = color;
        this.name = name;
        this.position = startPos;
        this.game = game;
    }

    public abstract List<LinkedList<Pair<Point, Point>>> getCaptures(State<Pawn[][], Pair<Point, Point>> board) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
    public abstract List<LinkedList<Pair<Point, Point>>> getMoves(State<Pawn[][], Pair<Point, Point>> state);

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public static boolean checkIfPawnChangeToQueen(Pawn pawn){
        if(pawn instanceof NormalPawn) {
            if (pawn.getColor() == Color.WHITE) {
                if (pawn.getPosition().y == 7) {
                    return true;
                }
            } else {
                if (pawn.getPosition().y == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void findNextCaptures(State<Pawn[][], Pair<Point, Point>> board, List<LinkedList<Pair<Point, Point>>> captures,
                                  ImmutablePair<Point, Point> newAction) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var movedPawn =  this.getClass().getConstructor(Pawn.class).newInstance(this);
        movedPawn.setPosition(newAction.getRight());
        var nextCaptures = movedPawn.getCaptures(game.succ(board, newAction));
        if (!nextCaptures.isEmpty()) {
            for (var nextCapture : nextCaptures) {
                nextCapture.add(0, newAction);
                captures.add(nextCapture);
            }
        } else {
            captures.add(new LinkedList<>(List.of(newAction)));
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pawn pawn = (Pawn) o;
        return name.equals(pawn.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, name);
    }

    @Override
    public String toString() {
            return name;
    }
}
