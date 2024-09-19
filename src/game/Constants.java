package src.game;

import java.awt.*;

public class Constants {
    public static final Boolean logDebug = true;

    public static final int BUFF_SIZE_GENERAL = 1024;
    public static final double EPSILON = 0.0001;
    public static final double EPSILON_SQ = EPSILON * EPSILON;
    public static final int FPS = 60;
    public static final double DT = 1 / (double) FPS;
    public static final String SHADERS_FOLDER = "src/shaders";

    public static final Dimension SCREEN_SIZE = new Dimension(900, 400);
    public static final boolean OPTIMIZE_TIME_STEPPER = true;
    public static final boolean V_SYNC = false;
}
