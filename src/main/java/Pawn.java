import java.awt.*;
import java.util.Objects;

public class Pawn {
    private final Color color;
    private String name;
    private Type type;
    private Point position;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Pawn(Color color, String name, Type type) {
        this.color = color;
        this.name = name;
        this.type = type;
    }

    public Pawn(Pawn pawn){
        this.color = pawn.getColor();
        this.name = pawn.getName();
        this.type = pawn.getType();
        this.position = pawn.getPosition();
    }

    public Pawn(Color color, String name, Type type, Point startPos) {
        this.color = color;
        this.name = name;
        this.type = type;
        this.position = startPos;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Type getType(){
        return type;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
        checkIfPawnChangeToQueen();
    }

    public void checkIfPawnChangeToQueen(){
        if(this.getType().equals(Pawn.Type.NORMAL)) {
            if (this.getColor() == Pawn.Color.WHITE) {
                if (getPosition().y == 7) {
            //    if(true) {
                    this.setQueent();
                }
            } else {
                if (getPosition().y == 0) {
           //     if(true) {
                    this.setQueent();
                }
            }
        }
    }

    public void setQueent(){
        this.type = Type.QUEEN;
        this.name = "Q" + name.substring(1);
    }

    public boolean isQueen(){
        return type.equals(Type.QUEEN);
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
        return Objects.hash(color, name, type);
    }

    @Override
    public String toString() {
        if(this.type == Type.QUEEN) {
            return ANSI_RED + name + ANSI_RESET;
        } else {
            return name;
        }
    }

    enum Color{
        WHITE, BLACK
    }

    enum Type{
        NORMAL, QUEEN
    }
}
