package Game;

import Pawn.Pawn;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CheckersUtils {
    public static List<Point> getCapturePoints(Pawn[][]board, Point pawnPoint, Point neighbour){
        var points = new ArrayList<Point>();
        if(getPointsBetween(pawnPoint, neighbour).stream().anyMatch(p -> board[p.x][p.y] != null)){
            return points;
        }
        int xSign = pawnPoint.x - neighbour.x > 0 ? -1 : 1;
        int ySign = pawnPoint.y - neighbour.y > 0 ? -1 : 1;
        int nextX = neighbour.x + xSign;
        int nextY = neighbour.y + ySign;
        while (nextY >= 0 && nextX >= 0 && nextY < board.length && nextX < board[0].length) {
            if (board[nextX][nextY] == null) {
                points.add(new Point(nextX, nextY));
            } else {
                break;
            }
            nextX += xSign;
            nextY += ySign;
        }
        return points;
    }

    public static List<Point> getPointsBetween(Point start, Point end){
        List<Point> points = new ArrayList<>();
        int xSign = start.x < end.x ? 1 : -1;
        int ySign = start.y < end.y  ? 1 : -1;
        int nextX = start.x + xSign;
        int nextY = start.y + ySign;
        while (nextX != end.x && nextY != end.y) {
            points.add(new Point(nextX, nextY));
            nextX += xSign;
            nextY += ySign;
        }
        return points;
    }

    public static Optional<Point> getCapturePoint(Pawn[][] board, Point pawnPoint, Point neighbour){
        int nextX = pawnPoint.x - neighbour.x > 0 ? neighbour.x - 1 : neighbour.x + 1;
        int nextY = pawnPoint.y - neighbour.y > 0 ? neighbour.y - 1 : neighbour.y + 1;
        if(nextY >= 0 && nextX >= 0 && nextY < board[0].length && nextX < board.length && board[nextX][nextY] == null)
            return Optional.of(new Point(nextX, nextY));
        return Optional.empty();
    }

    public static List<Point> getDiagonalFields(Point currentPos, int size){
        ArrayList<Point> availableMoves = new ArrayList<>();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(Math.abs(currentPos.x - i) == Math.abs(currentPos.y - j)){
                    availableMoves.add(new Point(i,j));
                }
            }
        }
        availableMoves.remove(currentPos);
        return availableMoves;
    }

    public static Pawn[][] copyBoard(Pawn[][] board) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Pawn[][] newBoard = new Pawn[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for(int j = 0; j< board.length; j++){
                if(board[i][j] != null)
                    newBoard[i][j] = board[i][j].getClass().getConstructor(Pawn.class).newInstance(board[i][j]);
            }
        }
        return newBoard;
    }
}
