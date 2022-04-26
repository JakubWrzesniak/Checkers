import java.util.Objects;

public class Pawn {
    private final Color color;
    private String name;
    private Type type;

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Pawn(Color color, String name, Type type) {
        this.color = color;
        this.name = name;
        this.type = type;
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

    public void setQueent(){
        this.type = Type.QUEEN;
        this.name = "Q" + name.substring(1);
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
