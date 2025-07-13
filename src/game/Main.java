package game;

public class Main {
    public static void main(String[] args) {
        Window.init("Chess Game", Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        Renderer.init();
        Asset.loadAll();
        FrameControl.init();
        Game.init();

        while(true) {
            FrameControl.sync(Game.TARGET_FPS);
            Input.poll();
            Game.update();
            Game.render();
        }
    }
}
