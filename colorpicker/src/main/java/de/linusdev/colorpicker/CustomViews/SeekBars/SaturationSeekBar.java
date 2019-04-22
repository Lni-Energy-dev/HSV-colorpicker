package de.linusdev.colorpicker.CustomViews.SeekBars;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class SaturationSeekBar extends CustomSeekBar {
    public SaturationSeekBar(Context context) {
        super(context);
        init();
    }

    public SaturationSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SaturationSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SaturationSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setMaxProgress(10000);
    }

    public void setColor(int color){

        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);

        hsv[2] = 0f;
        int noSaturation = Color.HSVToColor(255, hsv);
        hsv[2] = 1f;
        int fullSaturation = Color.HSVToColor(255, hsv);
        hsv[2] = 0.5f;
        int halfSaturation = Color.HSVToColor(255, hsv);

        Drawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {noSaturation, fullSaturation});
        ((GradientDrawable)drawable).setStroke(2, Color.BLACK);
        ((GradientDrawable)drawable).setCornerRadius(1000f);

        Drawable thumb = new ShapeDrawable(new ArcShape(0, 360));
        thumb.setColorFilter(halfSaturation, PorterDuff.Mode.ADD);


        setProgressDrawable(drawable, 0.05f, 0.05f, 0.12f);
        setThumb(thumb, 0.36f);
    }
}
