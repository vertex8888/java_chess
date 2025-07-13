package net;

import java.net.InetAddress;
import game.Utils.ByteBuff;
import net.server.ServerInfo;

public class Packet {
    public InetAddress address;
    public int port;
    public int type;

    private ByteBuff dataBuffer;

    public Packet(int _type) {
        type = _type;

        dataBuffer = new ByteBuff(ServerInfo.PACKET_BUFFER_SIZE);
        dataBuffer.writeInt(type);
    }

    public Packet(ByteBuff _dataBuffer, InetAddress _address, int _port) {
        dataBuffer = _dataBuffer;
        address = _address;
        port = _port;

        type = dataBuffer.readInt();
    }

    public byte[] getBytes() { return dataBuffer.getBytes(); }
    public int    getSize()  { return dataBuffer.getSize(); }

    public void writeInt(int value) { dataBuffer.writeInt(value); }
    public int  readInt() { return dataBuffer.readInt(); }
}
