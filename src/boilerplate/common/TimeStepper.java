package boilerplate.common;

import boilerplate.utility.Logging;
import boilerplate.utility.MathUtils;

public class TimeStepper {
    /**
     * Proper time stepper.
     * if game has optimised boolean toggled, thread sleeps for half of dt once stepped.
     */
    public static void startTimeStepper(double staticDeltaTime, GameBase game) {
        startTimeStepper(staticDeltaTime, game, BoilerplateConstants.OPTIMIZE_TIME_STEPPER);
    }

    public static void startTimeStepper(double staticDeltaTime, GameBase game, Boolean tryOptimise) {
        final double halfDt = staticDeltaTime * 0.5;  // in seconds
        double accumulator = 0;
        double lastFrame = System.nanoTime();

        game.createCapabilitiesAndOpen();
        Logging.debug("Starting time stepper with a dt of %s", staticDeltaTime);

        while (!game.shouldClose()) {
            double t = System.nanoTime();
            accumulator += MathUtils.nanoToSecond(t - lastFrame);
            accumulator = Math.min(1, accumulator);  // min of 1 fps
            lastFrame = t;

            while (accumulator >= staticDeltaTime) {
                accumulator -= staticDeltaTime;

                try {
                    double tStart = System.nanoTime();
                    game.mainLoop(staticDeltaTime);
                    double loopTime = MathUtils.nanoToSecond(System.nanoTime() - tStart);
                    if (tryOptimise && accumulator + loopTime < halfDt) {  // only sleep if there is enough time
                        Thread.sleep((long) Math.floor(halfDt * 1_000));  // give it a little break *-*
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException("Program closed while thread was asleep (between frames)");
                }
            }
        }
        game.close();
    }
}
