package com.game.utils;

import java.awt.*;

public class Constants {
    //To get user screen size
    private static final Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    public static final int G_WIDTH = (int) dimension.getWidth();
    public static final int G_HEIGHT = (int) dimension.getHeight();

    public static final float TILE_SIZE = 16.0f;
    public static final float METRES_TO_PIXELS = 100f;
    public static final float PIXELS_TO_METRES = 1 / METRES_TO_PIXELS;

    public static float PixelsToMeters(float pixelValue) {return pixelValue * PIXELS_TO_METRES;}
    public static float MetersToPixels(float metersValue) {return metersValue * METRES_TO_PIXELS;}
}
