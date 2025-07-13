package game;

public enum PieceKind {
    KING(1), QUEEN(2), ROOK(3), BISHOP(4), KNIGHT(5), PAWN(6);

    public final int intVal;

    PieceKind(int val) {
        intVal = val;
    }
}
