package game;

import java.awt.*;
import java.awt.image.BufferedImage;

import game.Utils.FSM;

public class Player {
    public PieceColor pieceColor;
    public Piece[][] board = new Piece[8][8];

    FSM fsm = new FSM("DORMANT");
    int grabX, grabY;
    Piece pieceGrabbed;

    MoveMap moveMap = null;

    public Player(Piece[][] gameBoard, PieceColor color) {
        pieceColor = color;
        updateBoard(gameBoard);
    }

    public void flipBoard() {
        for(int y = 0; y < 4; y += 1) {
            for(int x = 0; x < 8; x += 1) {
                Piece temp = board[y][x];
                board[y][x] = board[7 - y][7 - x];
                board[7 - y][7 - x] = temp;
            }
        }
    }

    public void updateBoard(Piece[][] gameBoard) {
        for(int y = 0; y < 8; y += 1) {
            for(int x = 0; x < 8; x += 1) {
                board[y][x] = gameBoard[y][x];
            }
        }

        if(pieceColor == PieceColor.BLACK) flipBoard();
        moveMap = new MoveMap(board, pieceColor);
    }


    //
    // @update
    //
    public PlayerMove update() {
        PlayerMove theMove = null;

        switch(fsm.currState) {
            case "DORMANT": {
                boolean isPressed  = Input.isMouseButtonPressed(Input.MOUSE_BUTTON_LEFT);
                if(isPressed && isGrabOK()) {
                    grabPiece();
                    fsm.set("MOVING_PIECE");
                }

            } break;
            case "MOVING_PIECE": {
                boolean isReleased = Input.isMouseButtonReleased(Input.MOUSE_BUTTON_LEFT);
                if(isReleased) {
                    if(isMoveOK()) {
                        theMove = movePiece();
                    }
                    else {
                        returnPiece();
                    }

                    fsm.set("DORMANT");
                }

            } break;
            default: {
                fsm.invalidState();
            }
        }

        return theMove;
    }

    boolean checkMouseOnBoard() {
        int mx = Input.getMouseX();
        int my = Input.getMouseY();

        boolean ok;
        ok  = (mx >= Game.BOARD_X && mx < Game.BOARD_X + Game.BOARD_DIM);
        ok &= (my >= Game.BOARD_Y && my < Game.BOARD_Y + Game.BOARD_DIM);

        return ok;
    }

    boolean isGrabOK() {
        if(!checkMouseOnBoard()) return false;

        int x = (Input.getMouseX() - Game.BOARD_X)/Game.BOARD_TILE_SIZE;
        int y = (Input.getMouseY() - Game.BOARD_Y)/Game.BOARD_TILE_SIZE;

        Piece piece = board[y][x];
        if(piece == null) return false;

        boolean isColorOK = piece.color == pieceColor;
        if(!isColorOK) return false;

        return true;
    }

    void grabPiece() {
        grabX = (Input.getMouseX() - Game.BOARD_X)/Game.BOARD_TILE_SIZE;
        grabY = (Input.getMouseY() - Game.BOARD_Y)/Game.BOARD_TILE_SIZE;

        pieceGrabbed = board[grabY][grabX];
        board[grabY][grabX] = null;
    }

    void returnPiece() {
        board[grabY][grabX] = pieceGrabbed;
    }

    boolean isMoveOK() {
        if(!checkMouseOnBoard()) return false;

        int newX = (Input.getMouseX() - Game.BOARD_X)/Game.BOARD_TILE_SIZE;
        int newY = (Input.getMouseY() - Game.BOARD_Y)/Game.BOARD_TILE_SIZE;

        if(newX == grabX && newY == grabY) return false;

        MoveList moveList = moveMap.getMoves(grabX, grabY);
        Move move = moveList.getMove(newX, newY);

        if(move == null) return false;

        return true;
    }


    PlayerMove movePiece() {
        int newX = (Input.getMouseX() - Game.BOARD_X)/Game.BOARD_TILE_SIZE;
        int newY = (Input.getMouseY() - Game.BOARD_Y)/Game.BOARD_TILE_SIZE;

        board[newY][newX] = pieceGrabbed;

        if(pieceColor == PieceColor.BLACK) {
            grabX = 7 - grabX;
            grabY = 7 - grabY;

            newX = 7 - newX;
            newY = 7 - newY;
        }

        PlayerMove move = new PlayerMove(grabX, grabY, newX, newY);
        return move;
    }

    //
    // @render
    //
    public void render() {
        Color bgColor = new Color(0x222222);
        Renderer.clearBackground(bgColor);

        for(int tileY = 0; tileY < 8; tileY += 1) {
            for(int tileX = 0; tileX < 8; tileX += 1) {

                Color tileDarkColor  = new Color(0xA37754);
                Color tileLightColor = new Color(0xF3EBD7);

                Color tileColor = tileLightColor;
                if(tileY % 2 == 0) {
                    if(tileX % 2 != 0) tileColor = tileDarkColor;
                }
                else {
                    if(tileX % 2 == 0) tileColor = tileDarkColor;
                }

                int posX = Game.BOARD_X + tileX*Game.BOARD_TILE_SIZE;
                int posY = Game.BOARD_Y + tileY*Game.BOARD_TILE_SIZE;

                Renderer.drawRect(posX, posY, Game.BOARD_TILE_SIZE, Game.BOARD_TILE_SIZE, tileColor);
            }
        }

        for(int tileY = 0; tileY < 8; tileY += 1) {
            for(int tileX = 0; tileX < 8; tileX += 1) {
                Piece piece = board[tileY][tileX];
                if(piece == null) continue;

                BufferedImage img = Asset.getPieceImage(piece);

                float scale = (float)Game.BOARD_TILE_SIZE/(float)img.getWidth();
                scale *= 1.5f;

                float imgWidth  = (float)img.getWidth()*scale;
                float imgHeight = (float)img.getHeight()*scale;

                float posX = Game.BOARD_X + tileX*Game.BOARD_TILE_SIZE + Game.BOARD_TILE_SIZE/2 - imgWidth/2;
                float posY = Game.BOARD_Y + tileY*Game.BOARD_TILE_SIZE + Game.BOARD_TILE_SIZE/2 - imgHeight/2;

                float offsetY = 0;
                if(piece.kind == PieceKind.KING)   offsetY = -14;
                if(piece.kind == PieceKind.QUEEN)  offsetY = -4;
                if(piece.kind == PieceKind.ROOK)   offsetY = -5;
                if(piece.kind == PieceKind.BISHOP) offsetY = -7;
                if(piece.kind == PieceKind.KNIGHT) offsetY = -2;

                Renderer.drawImage(img, posX, posY + offsetY, scale, 0.0f);
            }
        }

        if(fsm.check("MOVING_PIECE")) {
            MoveList moveList = moveMap.getMoves(grabX, grabY);
            for(Move move : moveList.moves) {
                float indicatorX = Game.BOARD_X + (float)move.toX*Game.BOARD_TILE_SIZE;
                float indicatorY = Game.BOARD_Y + (float)move.toY*Game.BOARD_TILE_SIZE;

                Renderer.drawRect(indicatorX + 25, indicatorY + 25, 20, 20, new Color(0xFF0000));
            }

            BufferedImage img = Asset.getPieceImage(pieceGrabbed);

            float scale = (float)Game.BOARD_TILE_SIZE/(float)img.getWidth();
            scale *= 1.5f;

            float imgWidth  = (float)img.getWidth()*scale;
            float imgHeight = (float)img.getHeight()*scale;

            float posX = Input.getMouseX() - imgWidth/2;
            float posY = Input.getMouseY() - imgHeight/2;

            Renderer.drawImage(img, posX, posY, scale, 0.0f);
        }
    }
}
