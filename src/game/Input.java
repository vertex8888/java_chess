package game;

import java.util.ArrayList;

public class Input {
    // @note: The mapping that the Java.Swing MouseEvent uses for mouse buttons.
    public static int MOUSE_BUTTON_NONE   = 0;
    public static int MOUSE_BUTTON_LEFT   = 1;
    public static int MOUSE_BUTTON_MIDDLE = 2;
    public static int MOUSE_BUTTON_RIGHT  = 3;
    public static int MOUSE_BUTTON_COUNT  = 4;

    private static boolean[] mouseStatePrev = new boolean[MOUSE_BUTTON_COUNT];
    private static boolean[] mouseState     = new boolean[MOUSE_BUTTON_COUNT];

    private static ArrayList<Character> keysTyped = new ArrayList<>();

    private static int mouseX = 0;
    private static int mouseY = 0;

    public static void poll() {
        // Save old input
        for(int i = 0; i < MOUSE_BUTTON_COUNT; i += 1) {
            mouseStatePrev[i] = mouseState[i];
        }

        // Process current input
        WindowInputEvent e = Window.getNextInputEvent();
        while(e != null) {
            switch(e.type) {
                case WindowInputEventType.MOUSE_BUTTON_PRESSED: {
                    mouseState[e.mouseButton] = true;
                } break;
                case WindowInputEventType.MOUSE_BUTTON_RELEASED: {
                    mouseState[e.mouseButton] = false;
                } break;
                case WindowInputEventType.MOUSE_NEW_POSITION: {
                    mouseX = e.mouseX;
                    mouseY = e.mouseY;
                } break;
                case WindowInputEventType.KEY_TYPED: {
                    keysTyped.add(e.key);
                } break;
            }

            e = Window.getNextInputEvent();
        }

    }

    public static char getKeyTyped() {
        if(keysTyped.isEmpty()) return 0;
        return keysTyped.removeFirst();
    }

    public static int getMouseX() { return mouseX; }
    public static int getMouseY() { return mouseY; }

    public static boolean isMouseButtonUp(int button)       { return(!mouseState[button]); }
    public static boolean isMouseButtonDown(int button)     { return(mouseState[button]); }
    public static boolean isMouseButtonPressed(int button)  { return(!mouseStatePrev[button] && mouseState[button]); }
    public static boolean isMouseButtonReleased(int button) { return(mouseStatePrev[button] && !mouseState[button]); }
}
