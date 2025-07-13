package game;

import java.util.ArrayList;

class Board {
    Piece[][] state = null;

    public Board(Piece[][] _state) {
        state = new Piece[8][8];

        for(int y = 0; y < 8; y += 1) {
            for(int x = 0; x < 8; x += 1) {
                state[y][x] = _state[y][x];
            }
        }
    }

    public boolean isValid(int x, int y) {
        return (x >= 0 && x < 8 && y >= 0 && y < 8);
    }

    public Piece getAt(int x, int y) {
        return state[y][x];
    }


    public boolean canMove(int x, int y) {
        if(!isValid(x, y)) return false;

        Piece piece = getAt(x, y);
        if(piece != null) return false;

        return true;
    }

    public boolean canCapture(Piece piece, int captureX, int captureY) {
        if(!isValid(captureX, captureY)) return false;

        Piece capturePiece = getAt(captureX, captureY);
        if(capturePiece == null) return false;

        if(piece.color == capturePiece.color) return false;

        return true;
    }
}

enum MoveKind {
    MOVE, PAWN_DOUBLE_MOVE, CAPTURE, CASTLE, EN_PASANT, PROMOTION,
}

class Move {
    public MoveKind kind;
    public int toX, toY;

    public Move(MoveKind _kind, int _toX, int _toY) {
        kind = _kind;
        toX  = _toX;
        toY  = _toY;
    }
}

enum DangerKind {
    ATTACK, XRAY,
}

class Danger {
    public DangerKind kind;
    public int fromX, fromY;
    public int defenderX, defenderY;

    public Danger(DangerKind _kind, int _fromX, int _fromY) {
        kind  = _kind;
        fromX = _fromX;
        fromY = _fromY;
    }

    public Danger(DangerKind _kind, int _fromX, int _fromY, int _defenderX, int _defenderY) {
        kind      = _kind;
        fromX     = _fromX;
        fromY     = _fromY;
        defenderX = _defenderX;
        defenderY = _defenderY;
    }
}


class Tile {
    public ArrayList<Move>   moves   = new ArrayList<>();
    public ArrayList<Danger> dangers = new ArrayList<>();

    public void AddMove(MoveKind kind, int toX, int toY) {
        moves.add(new Move(kind, toX, toY));
    }

    public void AddDanger(DangerKind kind, int fromX, int fromY) {
        dangers.add(new Danger(kind, fromX, fromY));
    }


    public void AddDanger(DangerKind kind, int fromX, int fromY, int defenderX, int defenderY) {
        dangers.add(new Danger(kind, fromX, fromY, defenderX, defenderY));
    }
}


class MoveList {
    ArrayList<Move> moves = new ArrayList<>();

    public void addMove(Move move) { moves.add(move); }

    public Move getMove(int x, int y) {
        for(Move move : moves) {
            if(move.toX == x && move.toY == y) return move;
        }

        return null;
    }
}

class MoveMap {
    Board board = null;
    Tile[][] map = null;

    PieceColor playerColor = null;

    public MoveMap(Piece[][] _board, PieceColor _playerColor) {
        board = new Board(_board);
        map = new Tile[8][8];

        playerColor = _playerColor;

        for(int y = 0; y < 8; y += 1) {
            for(int x = 0; x < 8; x += 1) {
                map[y][x] = new Tile();
            }
        }

        for(int y = 0; y < 8; y += 1) {
            for(int x = 0; x < 8; x += 1) {
                Piece piece = board.getAt(x, y);
                if(piece != null) PieceMove(x, y);
            }
        }
    }

    void PieceMove(int x, int y) {
        Piece piece = board.getAt(x, y);

        switch(piece.kind) {
            case PieceKind.KING:   KingMove(x, y); break;
            case PieceKind.QUEEN:  QueenMove(x, y);  break;
            case PieceKind.ROOK:   RookMove(x, y);   break;
            case PieceKind.BISHOP: BishopMove(x, y); break;
            case PieceKind.KNIGHT: KnightMove(x, y); break;
            case PieceKind.PAWN:   PawnMove(x, y);   break;
        }
    }

    void PawnMove(int x, int y) {
        Piece pawn = board.getAt(x, y);

        // @todo: bad names, this is relative to the player now....
        int whiteStartY = 6;
        int blackStartY = 1;

        int whiteDirY = -1;
        int blackDirY = 1;

        int whitePromotionY = 0;
        int blackPromotionY = 7;

        boolean isOnStartY = (pawn.color == playerColor ? (y == whiteStartY) : (y == blackStartY));

        int dirY = (pawn.color == playerColor ? whiteDirY : blackDirY);

        boolean closeToPromotion = (pawn.color == playerColor ?
                                    (y + dirY == whitePromotionY) :
                                    (y + dirY == blackPromotionY));


        // Move...
        int firstMoveX = x;
        int firstMoveY = y + 1*dirY;

        int secondMoveX = x;
        int secondMoveY = y + 2*dirY;

        boolean isFirstMoveClear  = board.canMove(firstMoveX,  firstMoveY);
        boolean isSecondMoveClear = board.canMove(secondMoveX, secondMoveY);

        if(isFirstMoveClear) {
            if(closeToPromotion) map[y][x].AddMove(MoveKind.PROMOTION, firstMoveX, firstMoveY);
            else                 map[y][x].AddMove(MoveKind.MOVE,      firstMoveX, firstMoveY);
        }

        if(isOnStartY && isFirstMoveClear && isSecondMoveClear) {
            map[y][x].AddMove(MoveKind.PAWN_DOUBLE_MOVE, secondMoveX, secondMoveY);
        }

        int rightCaptureX = x + 1;
        int rightCaptureY = y + dirY;

        int leftCaptureX = x - 1;
        int leftCaptureY = y + dirY;

        if(board.canCapture(pawn, rightCaptureX, rightCaptureY)) {
            map[y][x].AddMove(MoveKind.CAPTURE, rightCaptureX, rightCaptureY);
        }

        if(board.canCapture(pawn, leftCaptureX, leftCaptureY)) {
            map[y][x].AddMove(MoveKind.CAPTURE, leftCaptureX, leftCaptureY);
        }

        // Danger...
        int rightDangerX = x + 1;
        int leftDangerX  = x - 1;
        int dangerY      = y + dirY;

        if(board.isValid(rightDangerX, dangerY)) {
            map[dangerY][rightDangerX].AddDanger(DangerKind.ATTACK, x, y);
        }

        if(board.isValid(leftDangerX, dangerY)) {
            map[dangerY][leftDangerX].AddDanger(DangerKind.ATTACK, x, y);
        }
    }


    void KnightMove(int x, int y) {
        Piece knight = board.getAt(x, y);

        int[][] offset = { {1, 2}, {2, 1}, {-1, 2}, {2, -1}, {1, -2}, {-2, 1}, {-1, -2}, {-2, -1} };

        for(int i = 0; i < 8; i += 1) {
            int newX = x + offset[i][0];
            int newY = y + offset[i][1];

            // Move
            boolean canMove    = board.canMove(newX, newY);
            boolean canCapture = board.canCapture(knight, newX, newY);

            if     (canCapture) map[y][x].AddMove(MoveKind.CAPTURE, newX, newY);
            else if(canMove)    map[y][x].AddMove(MoveKind.CAPTURE, newX, newY);

            // Danger..
            boolean isValid = board.isValid(newX, newY);
            if(isValid) map[newY][newX].AddDanger(DangerKind.ATTACK, x, y);
        }
    }

    void BishopMove(int x, int y) {
        PieceLine(x, y,  1,  1);
        PieceLine(x, y, -1,  1);
        PieceLine(x, y,  1, -1);
        PieceLine(x, y, -1, -1);
    }

    void RookMove(int x, int y) {
        PieceLine(x, y,  1,  0);
        PieceLine(x, y, -1,  0);
        PieceLine(x, y,  0,  1);
        PieceLine(x, y,  0, -1);
    }

    void QueenMove(int x, int y) {
        BishopMove(x, y);
        RookMove(x, y);
    }

    void KingMove(int x, int y) {
        Piece king = board.getAt(x, y);

        int[][] offset = { {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {-1, 1}, {1, -1} };

        for(int i = 0; i < 8; i += 1) {
            int newX = x + offset[i][0];
            int newY = y + offset[i][1];

            // Move
            boolean canMove    = board.canMove(newX, newY);
            boolean canCapture = board.canCapture(king, newX, newY);

            if     (canCapture) map[y][x].AddMove(MoveKind.CAPTURE, newX, newY);
            else if(canMove)    map[y][x].AddMove(MoveKind.CAPTURE, newX, newY);

            // Danger..
            boolean isValid = board.isValid(newX, newY);
            if(isValid) map[newY][newX].AddDanger(DangerKind.ATTACK, x, y);
        }
    }

    void PieceLine(int x, int y, int dx, int dy) {
        Piece piece = board.getAt(x, y);

        int currX = x + dx;
        int currY = y + dy;

        boolean gotFirstCapture = false;
        int firstCaptureX = -1;
        int firstCaptureY = -1;

        while(true) {
            if(gotFirstCapture) {
                if(board.canCapture(piece, currX, currY)) {
                    map[currY][currX].AddDanger(DangerKind.XRAY, x, y, firstCaptureX, firstCaptureY);
                    break;
                }
                else if(board.canMove(currX, currY)) {
                    currX += dx;
                    currY += dy;
                    continue;
                }
                else {
                    break;
                }
            }

            if(board.canCapture(piece, currX, currY)) {
                map[y][x].AddMove(MoveKind.CAPTURE, currX, currY);
                map[currY][currX].AddDanger(DangerKind.ATTACK, x, y);
                firstCaptureX = currX;
                firstCaptureY = currY;
                gotFirstCapture = true;
            }
            else if(board.canMove(currX, currY)) {
                map[y][x].AddMove(MoveKind.MOVE, currX, currY);
                map[currY][currX].AddDanger(DangerKind.ATTACK, x, y);
            }
            else {
                break;
            }

            currX += dx;
            currY += dy;
        }
    }

    int getKingX() {
        for(int x = 0; x < 8; x += 1) {
            for(int y = 0; y < 8; y += 1) {
                Piece piece = board.getAt(x, y);
                if(piece == null) continue;
                if(piece.kind != PieceKind.KING) continue;
                if(piece.color != playerColor) continue;

                return x;
            }
        }

        return -1;
    }

    int getKingY() {
        for(int x = 0; x < 8; x += 1) {
            for(int y = 0; y < 8; y += 1) {
                Piece piece = board.getAt(x, y);
                if(piece == null) continue;
                if(piece.kind != PieceKind.KING) continue;
                if(piece.color != playerColor) continue;

                return y;
            }
        }

        return -1;
    }

    boolean isUnderAttack(Tile tile) {
        for(Danger danger : tile.dangers) {
            if(danger.kind != DangerKind.ATTACK) continue;

            Piece meanPiece = board.getAt(danger.fromX, danger.fromY);
            if(meanPiece.color != playerColor) return true;
        }

        return false;
    }

    Danger getSingleDanger(Tile tile) {
        for(Danger danger : tile.dangers) {
            if(danger.kind != DangerKind.ATTACK) continue;
            return danger;
        }

        return null;
    }


    boolean isUnderDoubleAttack(Tile tile) {
        int dangerCount = 0;
        for(Danger danger : tile.dangers) {
            if(danger.kind != DangerKind.ATTACK) continue;

            Piece meanPiece = board.getAt(danger.fromX, danger.fromY);
            if(meanPiece.color != playerColor) {
                dangerCount += 1;
            }
        }

        return dangerCount >= 2;
    }

    MoveList getMoves(int x, int y) {
        MoveList moveList = new MoveList();

        int kingX = getKingX();
        int kingY = getKingY();

        boolean isCheck       = isUnderAttack(map[kingY][kingX]);
        boolean isDoubleCheck = isUnderDoubleAttack(map[kingY][kingX]);

        if(isDoubleCheck) {
            if(kingX != x || kingY != y) return moveList;

            ArrayList<Move> moves = map[kingY][kingX].moves;

            for(Move move : moves) {
                ArrayList<Danger> dangers = map[move.toY][move.toX].dangers;

                boolean isMoveSafe = true;
                for(Danger danger : dangers) {
                    if(danger.kind != DangerKind.ATTACK) continue;

                    Piece meanPiece = board.getAt(danger.fromX, danger.fromY);
                    if(meanPiece.color != playerColor) {
                        isMoveSafe = false;
                        break;
                    }
                }

                if(isMoveSafe) moveList.addMove(move);
            }

            return moveList;
        }

        if(isCheck) {
            // Get king moves...
            {
                if(kingX == x && kingY == y) {
                    ArrayList<Move> moves = map[kingY][kingX].moves;

                    for(Move move : moves) {
                        ArrayList<Danger> dangers = map[move.toY][move.toX].dangers;

                        boolean isMoveSafe = true;
                        for(Danger danger : dangers) {
                            if(danger.kind != DangerKind.ATTACK) continue;

                            Piece meanPiece = board.getAt(danger.fromX, danger.fromY);
                            if(meanPiece.color != playerColor) {
                                isMoveSafe = false;
                                break;
                            }
                        }

                        if(isMoveSafe) moveList.addMove(move);
                    }

                    return moveList;
                }
            }

            // Get blocking moves....
            {
                ArrayList<Danger> xrays = new ArrayList<>();
                for(Danger danger : map[kingY][kingX].dangers) {
                    if(danger.kind != DangerKind.XRAY) continue;

                    Piece piece = board.getAt(danger.fromX, danger.fromY);
                    if(piece.color == playerColor) continue;

                    xrays.add(danger);
                }

                Danger singleDanger = getSingleDanger(map[kingY][kingX]);
                int dangerX = singleDanger.fromX;
                int dangerY = singleDanger.fromY;

                int dx = Integer.signum(dangerX - kingX);
                int dy = Integer.signum(dangerY - kingY);

                int currX = kingX;
                int currY = kingY;

                // @hack
                Piece dangerPiece = board.getAt(dangerX, dangerY);
                if(dangerPiece.kind == PieceKind.PAWN || dangerPiece.kind == PieceKind.KNIGHT) {
                    currX = dangerX;
                    currY = dangerY;

                    dx = 0;
                    dy = 0;
                }

                while(true) {
                    currX += dx;
                    currY += dy;

                    ArrayList<Danger> dangers = map[currY][currX].dangers;
                    for(Danger danger : dangers) {
                        if(danger.kind != DangerKind.ATTACK) continue;

                        Piece meanPiece = board.getAt(danger.fromX, danger.fromY);
                        if(meanPiece.color != playerColor) continue;
                        if(danger.fromX != x || danger.fromY != y) continue;

                        boolean isXrayed = false;
                        for(Danger xray : xrays) {
                            isXrayed = (xray.defenderX == danger.fromX && xray.defenderY == danger.fromY);
                            if(isXrayed) break;
                        }
                        if(isXrayed) continue;

                        ArrayList<Move> moves = map[danger.fromY][danger.fromX].moves;
                        for(Move move : moves) {
                            if(move.toX == currX && move.toY == currY) {
                                moveList.addMove(move);
                                break;
                            }
                        }
                    }

                    if(currX == dangerX && currY == dangerY) break;
                }

                return moveList;
            }
        }

        // Get moves when we aren't in check...
        {
            {
                Piece piece = board.getAt(x, y);
                if(piece.kind == PieceKind.KING) {
                    ArrayList<Move> moves = map[kingY][kingX].moves;

                    for(Move move : moves) {
                        ArrayList<Danger> dangers = map[move.toY][move.toX].dangers;

                        boolean isMoveSafe = true;
                        for(Danger danger : dangers) {
                            if(danger.kind != DangerKind.ATTACK) continue;

                            Piece meanPiece = board.getAt(danger.fromX, danger.fromY);
                            if(meanPiece.color != playerColor) {
                                isMoveSafe = false;
                                break;
                            }
                        }

                        if(isMoveSafe) moveList.addMove(move);
                    }
                    return moveList;
                }
            }

            boolean isXrayed = false;
            Danger  xray     = null;
            for(Danger danger : map[kingY][kingX].dangers) {
                if(danger.kind != DangerKind.XRAY) continue;

                Piece piece = board.getAt(danger.fromX, danger.fromY);
                if(piece.color == playerColor) continue;

                if(danger.defenderX == x && danger.defenderY == y) {
                    xray = danger;
                    isXrayed = true;
                    break;
                }
            }

            if(!isXrayed) {
                ArrayList<Move> moves = map[y][x].moves;
                for(Move move : moves) moveList.addMove(move);
                return moveList;
            }

            int dx = Integer.signum(xray.fromX - x);
            int dy = Integer.signum(xray.fromY - y);

            int currX = x;
            int currY = y;

            while(true) {
                currX += dx;
                currY += dy;

                ArrayList<Move> moves = map[y][x].moves;
                for(Move move : moves) {
                    if(move.toX == currX && move.toY == currY) moveList.addMove(move);
                }

                if(currX == xray.fromX && currY == xray.fromY) break;
            }
        };

        return moveList;
    }
}

public class Game {
    public static final int SCREEN_WIDTH  = 620;
    public static final int SCREEN_HEIGHT = 768;
    public static final double TARGET_FPS = 60.0;

    public static final int BOARD_DIM       = 600;
    public static final int BOARD_X         = SCREEN_WIDTH/2 - BOARD_DIM/2;
    public static final int BOARD_Y         = SCREEN_HEIGHT/2 - BOARD_DIM/2;
    public static final int BOARD_TILE_SIZE = BOARD_DIM/8;

    static Piece[][] board;

    static Player whitePlayer, blackPlayer;
    static PieceColor currColor;

    public static Piece[][] createBoard() {
        Piece[][] boardResult = {
            {Piece.BLACK_ROOK, Piece.BLACK_KNIGHT, Piece.BLACK_BISHOP, Piece.BLACK_QUEEN, Piece.BLACK_KING, Piece.BLACK_BISHOP, Piece.BLACK_KNIGHT, Piece.BLACK_ROOK},
            {Piece.BLACK_PAWN, Piece.BLACK_PAWN,   Piece.BLACK_PAWN,   Piece.BLACK_PAWN,  Piece.BLACK_PAWN, Piece.BLACK_PAWN,   Piece.BLACK_PAWN,   Piece.BLACK_PAWN},
            {null,             null,               null,               null,              null,             null,               null,               null            },
            {null,             null,               null,               null,              null,             null,               null,               null            },
            {null,             null,               null,               null,              null,             null,               null,               null            },
            {null,             null,               null,               null,              null,             null,               null,               null            },
            {Piece.WHITE_PAWN, Piece.WHITE_PAWN,   Piece.WHITE_PAWN,   Piece.WHITE_PAWN,  Piece.WHITE_PAWN, Piece.WHITE_PAWN,   Piece.WHITE_PAWN,   Piece.WHITE_PAWN},
            {Piece.WHITE_ROOK, Piece.WHITE_KNIGHT, Piece.WHITE_BISHOP, Piece.WHITE_QUEEN, Piece.WHITE_KING, Piece.WHITE_BISHOP, Piece.WHITE_KNIGHT, Piece.WHITE_ROOK}
        };

        return boardResult;
    }

    static void initBoard() {
        board = createBoard();

        blackPlayer = new Player(board, PieceColor.BLACK);
        whitePlayer = new Player(board, PieceColor.WHITE);
        currColor   = PieceColor.WHITE;
    }

    static void init() {
        initBoard();
    }

    static void doMove(PlayerMove move) {
        board[move.toY][move.toX] = board[move.fromY][move.fromX];
        board[move.fromY][move.fromX] = null;
    }


    static void update() {
        Player currPlayer = whitePlayer;
        if(currColor == PieceColor.BLACK) currPlayer = blackPlayer;

        PlayerMove move = currPlayer.update();
        if(move != null) {
            doMove(move);

            whitePlayer.updateBoard(board);
            blackPlayer.updateBoard(board);

            if(currColor == PieceColor.WHITE) currColor = PieceColor.BLACK;
            else                              currColor = PieceColor.WHITE;
        }
    }

    static void render() {
        Player currPlayer = whitePlayer;
        if(currColor == PieceColor.BLACK) currPlayer = blackPlayer;

        currPlayer.render();

        Gui.render();

        Renderer.frameFlip();
    }
}
