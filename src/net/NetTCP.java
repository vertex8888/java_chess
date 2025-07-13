package net;

import game.Utils.ByteBuff;
import net.server.ServerInfo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class NetTCP {
    public static InetAddress getInetAddress(String name) {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addr;
    }

    public static Socket createSocket(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(getInetAddress(host), port);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }

    public static ServerSocket createServerSocket(int port) {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }

    public static Socket serverSocketAccept(ServerSocket serverSocket) {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }

    public static Packet receivePacket(Socket socket) {
        Packet packet = null;

        InputStream in = null;
        try {
            in = socket.getInputStream();
        }
        catch(Exception e) {}

        int bytesAvailable = 0;
        try {
            bytesAvailable = in.available();
        }
        catch(Exception e) {}

        int packetBytesCount = ServerInfo.PACKET_BUFFER_SIZE;
        if(bytesAvailable >= packetBytesCount) {
            byte[] bytes = new byte[packetBytesCount];
            int bytesRead = 0;

            try {
                bytesRead = in.read(bytes, 0, packetBytesCount);
            }
            catch(Exception e) {}

            if(bytesRead == packetBytesCount) {
                ByteBuff buff = new ByteBuff(bytes, packetBytesCount);
                packet = new Packet(buff, null, 0);
            }
        }

        return packet;
    }

    public static void sendPacket(Socket socket, Packet packet) {
        OutputStream out = null;
        try {
            out = socket.getOutputStream();
        }
        catch(Exception e) {}

        try {
            out.write(packet.getBytes(), 0, packet.getSize());
            out.flush();
        }
        catch(Exception e) {}
    }
}

