package net.server;

import game.Game;
import game.Piece;
import game.PieceColor;
import net.NetTCP;

import net.client.ClientPacket;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.Random;

import net.Packet;

import game.PlayerMove;


class Server  {
    ServerSocket serverSocket;

    Socket[] clients = new Socket[2];
    int currentClientTurn = 0;

    Piece[][] board;

    public Server() {
        serverSocket = NetTCP.createServerSocket(ServerInfo.PORT);

        board = Game.createBoard();

        clients[0] = NetTCP.serverSocketAccept(serverSocket);
        clients[1] = NetTCP.serverSocketAccept(serverSocket);

        Random rand = new Random();
        currentClientTurn = rand.nextInt(0, 2);

        Socket whiteClient = clients[currentClientTurn];
        Socket blackClient = clients[(currentClientTurn + 1)%2];

        NetTCP.sendPacket(whiteClient, ServerPacket.playerColorAssign(PieceColor.WHITE));
        NetTCP.sendPacket(blackClient, ServerPacket.playerColorAssign(PieceColor.BLACK));
        NetTCP.sendPacket(whiteClient, ServerPacket.playerTurn(board));

    }

    void processCurrentPacket() {
        Packet currentPacket = null;

        for(Socket client : clients) {
           currentPacket = NetTCP.receivePacket(client);
           if(currentPacket != null) break;
        }

        if(currentPacket == null) return;

        switch(currentPacket.type) {
            case ClientPacket.CLIENT_PACKET_TYPE_MOVE: {
                PlayerMove move = ClientPacket.move(currentPacket);

                board[move.toY][move.toX] = board[move.fromY][move.fromX];
                board[move.fromY][move.fromX] = null;

                Socket currClient = clients[currentClientTurn];
                NetTCP.sendPacket(currClient, ServerPacket.playerMoveOk(board));

                currentClientTurn = (currentClientTurn + 1)%2;
                Socket nextClientOnTurn = clients[currentClientTurn];
                NetTCP.sendPacket(nextClientOnTurn, ServerPacket.playerTurn(board));

            } break;
        }
    }
}
