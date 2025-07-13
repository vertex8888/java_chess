package game;

public class PlayerMove {
    public int fromX, fromY;
    public int toX, toY;

    public PlayerMove() {}

    public PlayerMove(int fx, int fy, int tx, int ty) {
        fromX = fx;
        fromY = fy;
        toX   = tx;
        toY   = ty;
    }
}
