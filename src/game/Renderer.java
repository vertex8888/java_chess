package game;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Renderer {
    private static BufferedImage frameBuffer;
    private static Graphics2D    frameBufferGraphics;
    private static Graphics2D    panelGraphics;


    public static void init() {
        panelGraphics = (Graphics2D) Window.panel.getGraphics();

        int w = Window.panel.getWidth();
        int h = Window.panel.getHeight();

        frameBuffer         = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        frameBufferGraphics = (Graphics2D) frameBuffer.getGraphics();

        frameBufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        frameBufferGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        frameBufferGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        //frameBufferGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        //frameBufferGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static void frameFlip() {
        panelGraphics.drawImage(frameBuffer, 0, 0, null);
    }

    public static void clearBackground(Color color) {
        drawRect(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight(), color);
    }

    public static void drawRect(float x, float y, float width, float height, Color color) {
        frameBufferGraphics.setColor(color);
        frameBufferGraphics.fillRect((int)x, (int)y, (int)width, (int)height);
    }

    public static void drawImage(BufferedImage img, float x, float y, float scale, float rotation) {
        AffineTransform xform = new AffineTransform();
        xform.translate(x, y);
        xform.scale(scale, scale);

        float imgCenterX = (float)img.getWidth()*0.5f;
        float imgCenterY = (float)img.getHeight()*0.5f;
        xform.rotate(rotation, imgCenterX, imgCenterY);

        frameBufferGraphics.drawImage(img, xform, null);
    }

    public static int getTextWidthThatIgnoresSpacesAtEnd(Font font, String text) {
        FontRenderContext frc = frameBufferGraphics.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, text);
        return (int)gv.getPixelBounds(null, 0, 0).getWidth();
    }


    public static int getTextWidth(Font font, String text) {
        int w1 = getTextWidthThatIgnoresSpacesAtEnd(font, text + "G");
        int w2 = getTextWidthThatIgnoresSpacesAtEnd(font, "G");
        return w1 - w2;
    }

    public static int getTextHeight(Font font, String text) {
        FontRenderContext frc = frameBufferGraphics.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, "G");
        return (int)gv.getPixelBounds(null, 0, 0).getHeight();
    }

    public static void drawText(Font font, String string, float x, float y, Color color) {
        frameBufferGraphics.setColor(color);
        frameBufferGraphics.setFont(font);
        frameBufferGraphics.drawString(string, x, y);
    }
}

