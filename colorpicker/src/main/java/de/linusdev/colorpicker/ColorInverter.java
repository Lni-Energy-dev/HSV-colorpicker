package de.linusdev.colorpicker;

import android.graphics.Color;

public class ColorInverter {

    /**
     * inverts a color (not the alpha value)
     * @param color
     * @return
     */
    public static int invertColor(int color){
        return Color.argb(Color.alpha(color), 255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color));
    }

    /**
     * inverts a color (not the alpha value)
     * @param color
     * @return
     */
    public static int invertColor2(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        hsv[0] -= 135f;

        if(hsv[0] < 0){
            hsv[0] = 360f + hsv[0];
        }

        hsv[1] = 100 - hsv[1];
        hsv[2] = Math.max(0f, hsv[2] - 0.45f);

        return Color.HSVToColor(Color.alpha(color), hsv);
    }

    public static int invertColorGray(int color){
        /*int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        return Color.argb(255, 255 - (red + green + blue)/3, 255 - (red + green + blue)/3, 255 - (red + green + blue)/3);*/
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        hsv[1] = 0;
        hsv[2] = 1.0f - hsv[2];

        return Color.HSVToColor(Color.alpha(color), hsv);
    }
}
