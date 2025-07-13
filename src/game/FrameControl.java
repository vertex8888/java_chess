package game;


public class FrameControl {
    static long startTimeNano;

    static long lastFrameSleepMS;
    static double lastFrameTime;
    static double timePassed;

    public static void init() {
        startTimeNano = System.nanoTime();
    }

    public static void sync(double targetFPS) {
        long timeElapsedNano = System.nanoTime() - startTimeNano;

        double frameTime = (double)timeElapsedNano/1_000_000_000.0; // nanoseconds to just seconds

        double targetFrameTime = 1.0/targetFPS;
        double sleepTime       = targetFrameTime - frameTime;
        long   sleepMS         = (long)(sleepTime*1000.0);

        sleepMS -= 1;
        if(sleepMS < 0) sleepMS = 0;
        Utils.CurrentThread.sleep(sleepMS);

        boolean passedTargetFrameTime = false;
        while(!passedTargetFrameTime) {
            timeElapsedNano       = System.nanoTime() - startTimeNano;
            frameTime             = (double)timeElapsedNano / 1_000_000_000.0;
            passedTargetFrameTime = (frameTime > targetFrameTime);
        }

        lastFrameSleepMS = sleepMS;
        lastFrameTime    = frameTime;

        timePassed += frameTime;

        startTimeNano = System.nanoTime();
    }

    public static double getTime() {
        return timePassed;
    }

    public static String getInfoString() {
        int fps  = (int)(1.0/lastFrameTime);
        String strFrameTimeMS = String.format("%.04f", lastFrameTime*1000.0);
        String str = fps + " fps("+strFrameTimeMS+" ms | "+lastFrameSleepMS+")";
        return str;
    }
}