package net.client;

import game.Piece;
import game.PieceColor;
import game.PlayerMove;

import java.net.Socket;

import net.NetTCP;
import net.Packet;
import net.server.ServerInfo;
import net.server.ServerPacket;

public class Client {
    Socket socket;

    PieceColor pieceColor = null;

    boolean gotTurn = false;
    boolean shouldUpdateBoard = false;

    Piece[][] board = new Piece[8][8];


    public Client() {
        socket = NetTCP.createSocket(ServerInfo.HOST_NAME, ServerInfo.PORT);
    }

    public void sendMove(PlayerMove move) {
        NetTCP.sendPacket(socket, ClientPacket.move(move));
    }

    public void processCurrentPacket() {
        Packet currentPacket = NetTCP.receivePacket(socket);
        if(currentPacket == null) return;

        switch (currentPacket.type) {
            case ServerPacket.SERVER_PACKET_TYPE_PLAYER_TURN: {
                gotTurn = true;
                shouldUpdateBoard = true;
                board = ServerPacket.playerTurn(currentPacket);
            } break;
            case ServerPacket.SERVER_PACKET_TYPE_PLAYER_MOVE_OK: {
                shouldUpdateBoard = true;
                board = ServerPacket.playerMoveOk(currentPacket);
            } break;
            case ServerPacket.SERVER_PACKET_TYPE_PLAYER_COLOR_ASSIGN: {
                pieceColor = ServerPacket.playerColorAssign(currentPacket);
            } break;
        }
    }
}

