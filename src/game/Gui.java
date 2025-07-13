package game;

import java.awt.*;
import java.util.ArrayList;
import game.Utils.Timer;



class GuiElement {
    static final int TYPE_NONE       = 0;
    static final int TYPE_TEXT       = 1;
    static final int TYPE_BUTTON     = 2;
    static final int TYPE_TEXT_FIELD = 3;

    String id;
    int type;

    int posX, posY;
    int sizeX, sizeY;

    Font font;
    String text;
}

public class Gui {
    static ArrayList<GuiElement> guiElements = new ArrayList<>();
    static int currPosX, currPosY;
    static int currSizeX, currSizeY;

    static String currText;
    static Font currFont;

    static String hoverId = null;
    static String hotId = null;
    static String activeId = null;

    static Timer oscilationTimer = new Timer();

    static final Color BG_COLOR       = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    static final Color BG_COLOR_HOVER = new Color(0.4f, 0.4f, 0.4f, 1.0f);
    static final Color BG_COLOR_HOT   = new Color(0.3f, 0.3f, 0.3f, 1.0f);

    static final Color TEXT_COLOR     = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    static final Color TEXT_COLOR_HOT = new Color(1.0f, 1.0f, 0.0f, 1.0f);


    private static GuiElement newGuiElement(String id, int type, String text) {
        GuiElement e = new GuiElement();

        e.id    = id;
        e.type  = type;
        e.posX  = currPosX;
        e.posY  = currPosY;
        e.sizeX = currSizeX;
        e.sizeY = currSizeY;
        e.font  = currFont;
        e.text  = text;

        guiElements.add(e);

        return e;
    }

    public static void   setPosition(int x, int y) { currPosX = x; currPosY = y; }
    public static void   setSize(int x, int y)     { currSizeX = x; currSizeY = y; }
    public static void   setText(String text)      { currText = text; }
    public static void   setFont(Font font)        { currFont = font; }

    static boolean isHover(GuiElement e)  { return e.id == hoverId;  }
    static boolean isHot(GuiElement e)    { return e.id == hotId;    }
    static boolean isActive(GuiElement e) { return e.id == activeId; }

    static void setHover(GuiElement e)  { hoverId  = (e == null) ? null : e.id;  }
    static void setHot(GuiElement e)    { hotId    = (e == null) ? null : e.id;  }
    static void setActive(GuiElement e) { activeId = (e == null) ? null : e.id;  }

    public static void text(String text) {
        GuiElement e = newGuiElement("_internal_id_for_regular_text_", GuiElement.TYPE_TEXT, currText);
        e.text = text;
    }

    public static boolean button(String id) {
        GuiElement e = newGuiElement(id, GuiElement.TYPE_BUTTON, currText);

        int mouseX = Input.getMouseX();
        int mouseY = Input.getMouseY();

        boolean mouseOnButton = ((mouseX >= e.posX && mouseX < e.posX + e.sizeX) &&
                                 (mouseY >= e.posY && mouseY < e.posY + e.sizeY));

        boolean mousePressed  = Input.isMouseButtonPressed(Input.MOUSE_BUTTON_LEFT);
        boolean mouseReleased = Input.isMouseButtonReleased(Input.MOUSE_BUTTON_LEFT);

        boolean isClicked = false;

        if(isHot(e)) {
            if(!mouseOnButton) {
                setHot(null);
            }
            else if(mouseReleased) {
                isClicked = true;
                setHot(null);
            }
        }
        else if(isHover(e)) {
            if(!mouseOnButton) {
                setHover(null);
            }
            else if(mousePressed) {
                setHot(e);
                setHover(null);
            }
        }
        else {
            if(mouseOnButton) setHover(e);
        }

        return isClicked;
    }

    public static String textField(String id, String textBuffer) {
        GuiElement e = newGuiElement(id, GuiElement.TYPE_TEXT_FIELD, textBuffer);

        int mouseX = Input.getMouseX();
        int mouseY = Input.getMouseY();

        boolean mouseOnField = ((mouseX >= e.posX && mouseX < e.posX + e.sizeX) &&
                                 (mouseY >= e.posY && mouseY < e.posY + e.sizeY));

        boolean mousePressed  = Input.isMouseButtonPressed(Input.MOUSE_BUTTON_LEFT);
        boolean mouseReleased = Input.isMouseButtonReleased(Input.MOUSE_BUTTON_LEFT);


        if(isActive(e)) {
            if(!mouseOnField && mousePressed) {
                setActive(null);
            }
            else {
                char key = Input.getKeyTyped();
                while(key != 0) {
                    char backspaceKeyCode = 8;
                    char enterKeyCode     = 13;

                    if(key == enterKeyCode) {
                        setActive(null);
                    }
                    else if(key == backspaceKeyCode) {
                        if(e.text.length() > 1) {
                            e.text = e.text.substring(0, e.text.length() - 1);
                        }
                        else {
                            e.text = "";
                        }
                    }
                    else {
                        e.text += key;
                    }

                    key = Input.getKeyTyped();
                }
            }
        }
        else if(isHot(e)) {
            if(!mouseOnField) {
                setHot(null);
            }
            else if(mouseReleased) {
                setActive(e);
                oscilationTimer.start();
                setHot(null);
            }
        }
        else if(isHover(e)) {
            if(!mouseOnField) {
                setHover(null);
            }
            else if(mousePressed) {
                setHot(e);
                setHover(null);
            }
        }
        else {
            if(mouseOnField) setHover(e);
        }

        return e.text;
    }

    public static void render() {
        for(GuiElement e : guiElements) {
            switch(e.type) {
                case GuiElement.TYPE_TEXT: {
                    Renderer.drawText(e.font, e.text, e.posX, e.posY, Color.white);
                } break;
                case GuiElement.TYPE_TEXT_FIELD: {
                    Color rectColor = BG_COLOR;
                    Color textColor = TEXT_COLOR;

                    if(isHover(e)) {
                        rectColor = BG_COLOR_HOVER;
                    }
                    else if(isHot(e) || isActive(e)){
                        rectColor = BG_COLOR_HOT;
                        textColor = TEXT_COLOR_HOT;
                    }

                    Renderer.drawRect(e.posX, e.posY, e.sizeX, e.sizeY, rectColor);

                    int textWidth = Renderer.getTextWidth(e.font, e.text);
                    int textX = e.posX + 10;

                    int textHeight = Renderer.getTextHeight(e.font, e.text);
                    int textY = e.posY + e.sizeY/2 - textHeight/2 + textHeight;
                    Renderer.drawText(e.font, e.text, textX, textY, textColor);

                    double oscilationValue = Math.sin(oscilationTimer.getTime()*2.0*Math.PI);
                    boolean shouldShowCursor = isActive(e) && oscilationValue > -0.25;
                    if(shouldShowCursor) {
                        int cursorWidth  = e.font.getSize()/3;
                        int cursorHeight = e.font.getSize();

                        int cursorX      = textX + textWidth + 1;
                        int cursorY      = e.posY + e.sizeY/2 - cursorHeight/2;

                        Renderer.drawRect(cursorX, cursorY, cursorWidth, cursorHeight, textColor);
                    }
                } break;
                case GuiElement.TYPE_BUTTON: {
                    Color rectColor = BG_COLOR;
                    Color textColor = TEXT_COLOR;

                    if(isHover(e)) {
                        rectColor = BG_COLOR_HOVER;
                    }
                    else if(isHot(e) || isActive(e)){
                        rectColor = BG_COLOR_HOT;
                        textColor = TEXT_COLOR_HOT;
                    }

                    Renderer.drawRect(e.posX, e.posY, e.sizeX, e.sizeY, rectColor);

                    int textWidth = Renderer.getTextWidth(e.font, e.text);
                    int textX = e.posX + e.sizeX/2 - textWidth/2;

                    int textHeight = Renderer.getTextHeight(e.font, e.text);
                    int textY = e.posY + e.sizeY/2 - textHeight/2 + textHeight;
                    Renderer.drawText(e.font, e.text, textX, textY, textColor);
                } break;
            }
        }
    }

    public static void reset() { guiElements.clear(); }
}
