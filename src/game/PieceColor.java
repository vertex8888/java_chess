package game;

public enum PieceColor {
    WHITE(1), BLACK(2);

    public final int intVal;

    PieceColor(int val) {
        intVal = val;
    }
}
