package game;




public enum Piece {
    WHITE_KING   ("WHITE_KING",   PieceColor.WHITE, PieceKind.KING),
    WHITE_QUEEN  ("WHITE_QUEEN",  PieceColor.WHITE, PieceKind.QUEEN),
    WHITE_ROOK   ("WHITE_ROOK",   PieceColor.WHITE, PieceKind.ROOK),
    WHITE_BISHOP ("WHITE_BISHOP", PieceColor.WHITE, PieceKind.BISHOP),
    WHITE_KNIGHT ("WHITE_KNIGHT", PieceColor.WHITE, PieceKind.KNIGHT),
    WHITE_PAWN   ("WHITE_PAWN",   PieceColor.WHITE, PieceKind.PAWN),

    BLACK_KING   ("BLACK_KING",   PieceColor.BLACK, PieceKind.KING),
    BLACK_QUEEN  ("BLACK_QUEEN",  PieceColor.BLACK, PieceKind.QUEEN),
    BLACK_ROOK   ("BLACK_ROOK",   PieceColor.BLACK, PieceKind.ROOK),
    BLACK_BISHOP ("BLACK_BISHOP", PieceColor.BLACK, PieceKind.BISHOP),
    BLACK_KNIGHT ("BLACK_KNIGHT", PieceColor.BLACK, PieceKind.KNIGHT),
    BLACK_PAWN   ("BLACK_PAWN",   PieceColor.BLACK, PieceKind.PAWN);

    public final String     name;
    public final PieceColor color;
    public final PieceKind  kind;

    Piece(String name, PieceColor color, PieceKind kind) {
        this.name  = name;
        this.color = color;
        this.kind  = kind;
    }
}
