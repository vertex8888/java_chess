package game;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.HashMap;

import game.Utils.FileIO;

public class Asset {
    static HashMap<String, BufferedImage> pieceImages = new HashMap<>();

    public static Font karminaFont24;
    public static Font karminaFont32;
    public static Font karminaFont64;

    public static void loadAll() {
        // Load piece imgs...
        pieceImages.put("WHITE_KING",   FileIO.loadImage("res/pieces_cartoon/chess-king-white.png"));
        pieceImages.put("WHITE_QUEEN",  FileIO.loadImage("res/pieces_cartoon/chess-queen-white.png"));
        pieceImages.put("WHITE_ROOK",   FileIO.loadImage("res/pieces_cartoon/chess-rook-white.png"));
        pieceImages.put("WHITE_BISHOP", FileIO.loadImage("res/pieces_cartoon/chess-bishop-white.png"));
        pieceImages.put("WHITE_KNIGHT", FileIO.loadImage("res/pieces_cartoon/chess-knight-white.png"));
        pieceImages.put("WHITE_PAWN",   FileIO.loadImage("res/pieces_cartoon/chess-pawn-white.png"));

        pieceImages.put("BLACK_KING",   FileIO.loadImage("res/pieces_cartoon/chess-king-black.png"));
        pieceImages.put("BLACK_QUEEN",  FileIO.loadImage("res/pieces_cartoon/chess-queen-black.png"));
        pieceImages.put("BLACK_ROOK",   FileIO.loadImage("res/pieces_cartoon/chess-rook-black.png"));
        pieceImages.put("BLACK_BISHOP", FileIO.loadImage("res/pieces_cartoon/chess-bishop-black.png"));
        pieceImages.put("BLACK_KNIGHT", FileIO.loadImage("res/pieces_cartoon/chess-knight-black.png"));
        pieceImages.put("BLACK_PAWN",   FileIO.loadImage("res/pieces_cartoon/chess-pawn-black.png"));

        // Load fonts...
        karminaFont24 = FileIO.loadFont("res/fonts/karmina.ttf", 24.0f);
        karminaFont32 = FileIO.loadFont("res/fonts/karmina.ttf", 32.0f);
        karminaFont64 = FileIO.loadFont("res/fonts/karmina.ttf", 64.0f);
    }

    public static BufferedImage getPieceImage(Piece piece) {
        BufferedImage img = pieceImages.get(piece.name);
        return img;
    }
}
