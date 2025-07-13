package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Window {
    static JFrame frame;
    static JPanel panel;

    public static void init(String title, int screenWidth, int screenHeight) {
        frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle(title);
        frame.setVisible(true);

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        panel.setBackground(Color.black);
        panel.setFocusable(true);

        // @note: We need the JPanel for the Graphics2D object and input listeners.
        frame.add(panel);
        frame.pack(); // packs the frame size to the game panel size

        panel.addMouseMotionListener(new MouseMotionInput());
        panel.addMouseListener(new MouseButtonInput());
        panel.addKeyListener(new KeyboardInput());
    }

    static WindowInputEvent getNextInputEvent() {
        return WindowInputManager.getNextInputEvent();
    }

    //
    // JPanel Listener Components
    //
    static class KeyboardInput implements KeyListener {
        public void keyTyped(KeyEvent e)    { WindowInputManager.pushKeyTypedEvent(e.getKeyChar()); }
        public void keyPressed(KeyEvent e)  {}
        public void keyReleased(KeyEvent e) {}
    }

    static class MouseMotionInput implements MouseMotionListener {
        public void mouseDragged(MouseEvent e) { WindowInputManager.pushMousePositionEvent(e.getX(), e.getY()); }
        public void mouseMoved(MouseEvent e)   { WindowInputManager.pushMousePositionEvent(e.getX(), e.getY()); }
    }

    static class MouseButtonInput implements MouseListener {
        public void mousePressed(MouseEvent e)  { WindowInputManager.pushMouseButtonEvent(WindowInputEventType.MOUSE_BUTTON_PRESSED,  e.getButton()); }
        public void mouseReleased(MouseEvent e) { WindowInputManager.pushMouseButtonEvent(WindowInputEventType.MOUSE_BUTTON_RELEASED, e.getButton()); }

        // @note: Unused...
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
    }
}

//
// @note: We store all the input events detected by the JPanel-Listener-Component into
// one ArrayList that we process later. The *PROCESS LATER* is the important part, since that
// allows us to process the input whenever we want during our frame. So when input gets
// processed it's on our frame's time, not the JPanel-Listener-Component time.
// ...
// The input is sync-ed with our frame and is processed at *ONE SPECIFIC POINT* during it.
// This allows us to store the input of the previous frame and make stuff like detecting
// when mouse buttons and keyboard keys are pressed and released pretty easy.
//
class WindowInputEventType {
    static final int NONE                  = 0;
    static final int MOUSE_BUTTON_PRESSED  = 1;
    static final int MOUSE_BUTTON_RELEASED = 2;
    static final int MOUSE_NEW_POSITION    = 3;

    static final int KEY_TYPED             = 4;
}

class WindowInputEvent {
    int type;
    int mouseButton;
    int mouseX, mouseY;
    char key;
}

class WindowInputManager {
    static ArrayList<WindowInputEvent> inputEvents = new ArrayList<WindowInputEvent>();

    static WindowInputEvent getNextInputEvent() {
        if(inputEvents.isEmpty()) return null;
        WindowInputEvent e = inputEvents.removeFirst();
        return e;
    }

    static void pushInputEvent(WindowInputEvent e) { inputEvents.add(e); }

    static void pushMouseButtonEvent(int eventType, int button) {
        WindowInputEvent e  = new WindowInputEvent();
        e.type        = eventType;
        e.mouseButton = button;

        pushInputEvent(e);
    }

    static void pushMousePositionEvent(int x, int y) {
        WindowInputEvent e = new WindowInputEvent();
        e.type       = WindowInputEventType.MOUSE_NEW_POSITION;
        e.mouseX     = x;
        e.mouseY     = y;

        pushInputEvent(e);
    }

    static void pushKeyTypedEvent(char key) {
        WindowInputEvent e = new WindowInputEvent();
        e.type = WindowInputEventType.KEY_TYPED;
        e.key  = key;

        pushInputEvent(e);
    }
}


