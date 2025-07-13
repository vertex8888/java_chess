package net.server;

import game.Piece;
import game.PieceColor;
import game.PieceKind;
import game.Utils.Log;
import game.Utils.ByteBuff;

import java.net.InetAddress;
import net.Packet;

public class ServerPacket {

    public static final int SERVER_PACKET_TYPE_NONE                = 0;
    public static final int SERVER_PACKET_TYPE_PLAYER_TURN         = 1;
    public static final int SERVER_PACKET_TYPE_PLAYER_MOVE_OK      = 2;
    public static final int SERVER_PACKET_TYPE_PLAYER_COLOR_ASSIGN = 3;

    //
    // Packet Player Turn
    //
    public static Packet playerTurn(Piece[][] board) {
        Packet packet = new Packet(SERVER_PACKET_TYPE_PLAYER_TURN);
        packetWriteChessBoard(packet, board);
        return packet;
    }

    public static Piece[][] playerTurn(Packet packet) {
        assert(packet.type == SERVER_PACKET_TYPE_PLAYER_TURN);
        return packetReadChessBoard(packet);
    }

    //
    // Player move ok
    //
    public static Packet playerMoveOk(Piece[][] board) {
        Packet packet = new Packet(SERVER_PACKET_TYPE_PLAYER_MOVE_OK);
        packetWriteChessBoard(packet, board);
        return packet;
    }

    public static Piece[][] playerMoveOk(Packet packet) {
        assert(packet.type == SERVER_PACKET_TYPE_PLAYER_MOVE_OK);
        return packetReadChessBoard(packet);
    }


    //
    // Color assign
    //
    public static Packet playerColorAssign(PieceColor color) {
        Packet packet = new Packet(SERVER_PACKET_TYPE_PLAYER_COLOR_ASSIGN);
        packet.writeInt(color.intVal);

        return packet;
    }

    public static PieceColor playerColorAssign(Packet packet) {
        assert(packet.type == SERVER_PACKET_TYPE_PLAYER_COLOR_ASSIGN);
        int colorInt = packet.readInt();

        PieceColor color = null;
        if(colorInt == 1) color = PieceColor.WHITE;
        if(colorInt == 2) color = PieceColor.BLACK;

        return color;
    }

    //
    //
    //
    private static void packetWriteChessBoard(Packet packet, Piece[][] board) {
        for(int y = 0; y < 8; y += 1) {
            for(int x = 0; x < 8; x += 1) {
                int color = -1;
                int type = -1;

                if(board[y][x] != null) {
                    color = board[y][x].color.intVal;
                    type = board[y][x].kind.intVal;
                }
                packet.writeInt(color);
                packet.writeInt(type);
            }
        }
    }

    private static Piece[][] packetReadChessBoard(Packet packet) {
        Piece[][] board = new Piece[8][8];

        for(int y = 0; y < 8; y += 1) {
            for(int x = 0; x < 8; x += 1) {
                int colorInt = packet.readInt();
                int kindInt  = packet.readInt();

                Piece piece = null;

                if(colorInt != -1 && kindInt != -1) {
                    PieceColor color = null;
                    if(colorInt == 1) color = PieceColor.WHITE;
                    if(colorInt == 2) color = PieceColor.BLACK;

                    PieceKind kind = null;

                    if(kindInt == 1) kind = PieceKind.KING;
                    if(kindInt == 2) kind = PieceKind.QUEEN;
                    if(kindInt == 3) kind = PieceKind.ROOK;
                    if(kindInt == 4) kind = PieceKind.BISHOP;
                    if(kindInt == 5) kind = PieceKind.KNIGHT;
                    if(kindInt == 6) kind = PieceKind.PAWN;

                    if(color == PieceColor.WHITE) {
                        if(kind == PieceKind.KING)   piece = Piece.WHITE_KING;
                        if(kind == PieceKind.QUEEN)  piece = Piece.WHITE_QUEEN;
                        if(kind == PieceKind.ROOK)   piece = Piece.WHITE_ROOK;
                        if(kind == PieceKind.BISHOP) piece = Piece.WHITE_BISHOP;
                        if(kind == PieceKind.KNIGHT) piece = Piece.WHITE_KNIGHT;
                        if(kind == PieceKind.PAWN)   piece = Piece.WHITE_PAWN;
                    }
                    else {
                        if(kind == PieceKind.KING)   piece = Piece.BLACK_KING;
                        if(kind == PieceKind.QUEEN)  piece = Piece.BLACK_QUEEN;
                        if(kind == PieceKind.ROOK)   piece = Piece.BLACK_ROOK;
                        if(kind == PieceKind.BISHOP) piece = Piece.BLACK_BISHOP;
                        if(kind == PieceKind.KNIGHT) piece = Piece.BLACK_KNIGHT;
                        if(kind == PieceKind.PAWN)   piece = Piece.BLACK_PAWN;
                    }
                }

                board[y][x] = piece;
            }
        }

        return board;
    }
}
