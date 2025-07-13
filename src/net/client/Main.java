package net.client;

import db.Database;
import game.*;
import game.Window;

import java.awt.*;

import game.Game;
import game.Utils.FSM;
import game.Utils.Log;

public class Main {

    enum ClientScreen {
        LOGIN,
        GAME,
    };

    static Client client         = null;
    static String clientUsername = "user";
    static String clientPassword = "pass";

    static FSM    fsm    = new FSM("WAITING_FOR_COLOR");
    static Player player = null;

    static ClientScreen clientScreen = ClientScreen.LOGIN;

    public static void main(String[] args) {
        Window.init("Chess Client", Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        Asset.loadAll();
        Database.init();
        Renderer.init();
        FrameControl.init();

        while(true) {
            FrameControl.sync(60);

            Input.poll();

            switch(clientScreen) {
                case ClientScreen.LOGIN: {
                    clientScreenLoginUpdate();
                    clientScreenLoginRender();
                } break;
                case ClientScreen.GAME: {

                    clientScreenGameUpdate();
                    clientScreenGameRender();
                }  break;
            }
        }
    }

    //
    // @game_screen
    //
    static void clientScreenGameUpdate() {
        if(client != null) client.processCurrentPacket();

        switch(fsm.currState) {
            case "WAITING_FOR_COLOR" : {
                if(fsm.enter()) {
                    client = new Client();
                }

                if(client.pieceColor != null) {
                    player = new Player(Game.createBoard(), client.pieceColor);
                    fsm.set("WAITING_FOR_TURN");
                }
            } break;
            case "WAITING_FOR_TURN" : {
                if(client.shouldUpdateBoard) {
                    client.shouldUpdateBoard = false;
                    player.updateBoard(client.board);

                }

                if(client.gotTurn) {
                    client.gotTurn = false;
                    fsm.set("MAKING_MOVE");
                }
            } break;
            case "MAKING_MOVE" : {
                PlayerMove move = player.update();
                if(move != null) {
                    client.sendMove(move);
                    fsm.set("WAITING_FOR_TURN");
                }
            } break;
            default: {
                fsm.invalidState();
            }
        }
    }

    static void clientScreenGameRender() {
        Renderer.clearBackground(Color.black);

        switch(fsm.currState) {
            case "WAITING_FOR_COLOR" : {
                Renderer.drawText(Asset.karminaFont32, "Waiting...", 10, 30, Color.white);
            } break;
            case "WAITING_FOR_TURN" :
            case "MAKING_MOVE": {
                player.render();
            } break;
            default: {
                fsm.invalidState();
            }
        }

        Renderer.frameFlip();
    }

    //
    // @client_screen
    //
    static void clientScreenLoginUpdate() {
        Gui.reset();

        // chess text
        {
            Font font = Asset.karminaFont64;

            String text = "Chess";
            int textWidth = Renderer.getTextWidth(font, text);

            int textX = Game.SCREEN_WIDTH/2 - textWidth/2;
            int textY = (int)(Game.SCREEN_HEIGHT*0.4f);

            Gui.setPosition(textX, textY);
            Gui.setFont(font);
            Gui.text(text);
        };

        // username field and button
        {
            // username field
            Font font = Asset.karminaFont32;

            int fieldWidth  = 300;
            int fieldHeight = font.getSize() + 16;

            int fieldX = Game.SCREEN_WIDTH/2 - fieldWidth/2;
            int fieldY = (int)(Game.SCREEN_HEIGHT*0.45f);

            Gui.setPosition(fieldX, fieldY);
            Gui.setSize(fieldWidth, fieldHeight);
            Gui.setFont(font);
            clientUsername = Gui.textField("idUsernameField", clientUsername);

            Gui.setPosition(fieldX, fieldY + fieldHeight + 10);
            Gui.setSize(fieldWidth, fieldHeight);
            Gui.setFont(font);
            clientPassword = Gui.textField("idPasswordField", clientPassword);

            // login button
            String buttonText = "Login";

            int buttonWidth  = Renderer.getTextWidth(font, buttonText) + font.getSize()*2;
            int buttonHeight = font.getSize() + 16;

            int buttonX = Game.SCREEN_WIDTH/2 - buttonWidth/2;
            int buttonY = fieldY + fieldHeight*2 + 20;

            Gui.setPosition(buttonX, buttonY);
            Gui.setSize(buttonWidth, buttonHeight);
            Gui.setFont(font);
            Gui.setText(buttonText);
            if (Gui.button("idLoginButton")) {
                if(Database.loginUser(clientUsername, clientPassword)) {
                    clientScreen = ClientScreen.GAME;
                }
            }
        };

    }

    static void clientScreenLoginRender() {
        Color bgColor = new Color(0x222222);
        Renderer.clearBackground(bgColor);

        /*
        @todo: You can only render the chess board with a player object.
               I guess I's fine??
        */
        Piece[][] board = Game.createBoard();
        Player tempPlayer = new Player(board, PieceColor.WHITE);
        tempPlayer.render();

        {
            int rectWidth  = (int)(Game.SCREEN_WIDTH*0.75f);
            int rectHeight = (int)(Game.SCREEN_HEIGHT*0.5f);

            int rectX = Game.SCREEN_WIDTH/2 - rectWidth/2;
            int rectY = Game.SCREEN_HEIGHT/2 - rectHeight/2;

            Renderer.drawRect(rectX, rectY, rectWidth, rectHeight, new Color(0.13f, 0.13f, 0.13f, 0.975f));
        };

        Gui.render();

        Renderer.frameFlip();
    }
}
