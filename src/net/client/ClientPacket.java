package net.client;

import game.PlayerMove;
import game.Utils.ByteBuff;
import net.server.ServerInfo;
import net.Packet;

public class ClientPacket {
    public static final int CLIENT_PACKET_TYPE_NONE = 0;
    public static final int CLIENT_PACKET_TYPE_MOVE = 1;

    public static Packet move(PlayerMove move) {
        Packet packet = new Packet(CLIENT_PACKET_TYPE_MOVE);

        packet.writeInt(move.fromX);
        packet.writeInt(move.fromY);
        packet.writeInt(move.toX);
        packet.writeInt(move.toY);

        return packet;
    }

    public static PlayerMove move(Packet packet) {
        assert(packet.type == CLIENT_PACKET_TYPE_MOVE);

        PlayerMove move = new PlayerMove();
        move.fromX = packet.readInt();
        move.fromY = packet.readInt();
        move.toX   = packet.readInt();
        move.toY   = packet.readInt();

        return move;
    }
}