package net.server;

import game.FrameControl;
import game.Renderer;
import game.Window;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Window.init("Server", 700, 900);

        Renderer.init();
        FrameControl.init();

        Server server = new Server();

        while(true) {
            FrameControl.sync(120);

            server.processCurrentPacket();

            Renderer.clearBackground(Color.black);
            Renderer.frameFlip();
        }
    }
}
