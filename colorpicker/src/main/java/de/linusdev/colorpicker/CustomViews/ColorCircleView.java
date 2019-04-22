package de.linusdev.colorpicker.CustomViews;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import de.linusdev.colorpicker.ColorInverter;

public class ColorCircleView extends View {


    public static final int POINTER_COLOR_INVERT = -1;
    public static final int POINTER_COLOR_BLACK = 0xff000000;

    public static final float POINTER_SIZE_DEFAULT = 10f;
    public static final float POINTER_THICKNESS_DEFAULT = 5f;

    private float scale = 0.5f;
    private float borderWidth = 6f;

    private Paint mPaint;
    private Paint mPathPaint;
    private Path mPath;

    private Bitmap hsvBitmap = null;


    private float lastTouchX = -1;
    private float lastTouchY = -1;

    private float canvasCenterX = -1;
    private float canvasCenterY = -1;

    //these two are do NOT represent the real Height or Width!
    private float width = -1;
    private float height = -1;

    //these two do NOT represent the real Height or Width!
    private float hsvCircleHeight = -1;
    private float hsvCircleWidth = -1;


    private float pointerSize = POINTER_SIZE_DEFAULT;
    private float pointerThickness = POINTER_THICKNESS_DEFAULT;
    private int pointerColor = POINTER_COLOR_INVERT;

    private boolean firstDraw = true;

    private OnTouchListener onTouchListener = null;
    private OnDrawListener onDrawListener = null;

    private boolean landscape = false;

    public ColorCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPath = new Path();
        mPathPaint = new Paint();
        mPathPaint.setStrokeWidth(pointerThickness);
        mPathPaint.setColor(Color.WHITE);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPaint = new Paint();

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscape = true;
        } else {
            landscape = false;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(w == h){

            //calculate the center of the View
            canvasCenterX = (float)w / 2f;
            canvasCenterY = (float)h / 2f;

            width = w - borderWidth;
            height = h - borderWidth;

            //scale it
            hsvCircleHeight = (float)h *scale;
            hsvCircleWidth = (float)w * scale;

            hsvBitmap = Bitmap.createBitmap((int)hsvCircleWidth, (int)hsvCircleHeight, Bitmap.Config.ARGB_8888);
            hsvBitmap.setHasAlpha(true);

            int[] pixels = new int[(int)hsvCircleWidth * (int)hsvCircleHeight + (int)hsvCircleHeight + (int)hsvCircleWidth];

            int rx = 0;
            int ry = 0;
            for (; ry <= (int)hsvCircleHeight; ry++) {
                rx = 0;
                for (; rx <= (int)hsvCircleWidth; rx++) {
                    pixels[(ry) * (int)hsvCircleWidth + rx] = getColorAtPoint(rx, ry, true);
                }
            }

            hsvBitmap.setPixels(pixels, 0, (int)hsvCircleWidth, 0, 0, (int)hsvCircleWidth, (int)hsvCircleHeight);
            hsvBitmap = Bitmap.createScaledBitmap(hsvBitmap, (int)width, (int) height, true);
            onDrawListener.onCalculatedBitmap(hsvBitmap);

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(getWidth() == getHeight()){
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);

            if(hsvBitmap == null) {
                hsvBitmap = Bitmap.createBitmap((int)hsvCircleWidth, (int)hsvCircleHeight, Bitmap.Config.ARGB_8888);
                hsvBitmap.setHasAlpha(true);

                int[] pixels = new int[(int)hsvCircleWidth * (int)hsvCircleHeight + (int)hsvCircleHeight + (int)hsvCircleWidth];

                int rx = 0;
                int ry = 0;
                for (; ry <= (int)hsvCircleHeight; ry++) {
                    rx = 0;
                    for (; rx <= (int)hsvCircleWidth; rx++) {
                        pixels[(ry) * (int)hsvCircleWidth + rx] = getColorAtPoint(rx, ry, true);
                    }
                }

                hsvBitmap.setPixels(pixels, 0, (int)hsvCircleWidth, 0, 0, (int)hsvCircleWidth, (int)hsvCircleHeight);
                hsvBitmap = Bitmap.createScaledBitmap(hsvBitmap, (int)width, (int) height, true);
                onDrawListener.onCalculatedBitmap(hsvBitmap);
            }
            mPaint.setFilterBitmap(true);
            canvas.drawBitmap(hsvBitmap, 0f, 0f, mPaint);
            //draw outline
            mPaint.setStrokeWidth(6f);
            canvas.drawCircle(canvasCenterX, canvasCenterY, width/2f, mPaint);

            //draw pointer
            canvas.drawPath(mPath, mPathPaint);

            if(firstDraw){
                firstDraw = false;
                if(onDrawListener != null){
                    onDrawListener.onDrawFirstTime();
                }
            }else{
                if(onDrawListener != null){
                    onDrawListener.onDraw();
                }
            }

        }else{

            if(landscape){
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = getRootView().getHeight()/2;
                params.width = getRootView().getHeight()/2;
                setLayoutParams(params);
            }else{
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = getWidth();
                setLayoutParams(params);
            }

        }
    }


    private int getColorAtPoint(int rx, int ry, boolean scaled){
        int color;

        float width;
        float height;
        float scale;

        if(scaled){
            width = hsvCircleWidth;
            height = hsvCircleHeight;
            scale = this.scale;
        }else{
            width = this.width;
            height = this.height;
            scale = 1f;
        }

        float xf = getPosition(canvasCenterX*scale, rx) / (width/2f);
        float yf = getPosition(canvasCenterY*scale, ry) / (height/2f);

        float length = (float) Math.sqrt(xf*xf + yf*yf);

        if(length > 1f){
            //outside of the circle
            color = Color.argb(0,0, 0, 0);
        }else{
            //inside the circle
            float hsvColorSpace = (float) Math.toDegrees((Math.atan2(yf, xf)));

            if (hsvColorSpace < 0.0) {
                //fix negative area
                hsvColorSpace += 360.0;
            }
            float[] hsv = {hsvColorSpace, length, 100f};
            color = Color.HSVToColor(hsv);
        }

        return color;
    }

    private int getColorAtPoint_square(int rx, int ry, boolean scaled){
        int color;

        float width;
        float height;
        float scale;

        if(scaled){
            width = hsvCircleWidth;
            height = hsvCircleHeight;
            scale = this.scale;
        }else{
            width = this.width;
            height = this.height;
            scale = 1f;
        }

        float xf = getPosition(canvasCenterX*scale, rx) / (width/2f);
        float yf = getPosition(canvasCenterY*scale, ry) / (height/2f);

        float length = (float) Math.sqrt(xf*xf + yf*yf);

        float hsvColorSpace = (float) Math.toDegrees((Math.atan2(yf, xf)));

        if (hsvColorSpace < 0.0) {
            //fix negative area
            hsvColorSpace += 360.0;
        }
        float[] hsv = {hsvColorSpace, length, 100f};
        color = Color.HSVToColor(hsv);

        return color;
    }

    private void touchEvent(float x, float y){
        mPath.rewind();
        mPath.addCircle(x, y, pointerSize, Path.Direction.CW);
        mPath.addCircle(x, y, 2f, Path.Direction.CW);

        lastTouchX = x;
        lastTouchY = y;

        int color = getColorAtPoint_square((int)x, (int)y, false);

        if(pointerColor == -1) {
            mPathPaint.setColor(ColorInverter.invertColor2(color));
        }else{
            mPathPaint.setColor(pointerColor);
        }

        if(onTouchListener != null){
            onTouchListener.onTouch(x, y, color);
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        float cx = getPosition(canvasCenterX, x);
        float cy = getPosition(canvasCenterY, y);

        float length = (float) Math.sqrt(cx*cx + cy*cy);

        //check if it is inside the circle
        //in case that it is outside we must crop the length to it`s max
        //-> the Vector must have a length of width/2
        if(length > width/2f){
            //normalize the Vector
            cx /= length;
            cy /= length;

            //make the Vector the max length and add the center again so we get the real value
            x = cx*(width/2f) + canvasCenterX;
            y = cy*(width/2f) + canvasCenterY;
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            touchEvent(x, y);

        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            touchEvent(x, y);

        }else if(event.getAction() == MotionEvent.ACTION_UP){
            touchEvent(x, y);
        }

        return true;
    }

    private int getPosition(int center, int pos){
        return pos - center;
    }

    private float getPosition(float center, float pos){
        return pos - center;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setPointerSize(float pointerSize) {
        this.pointerSize = pointerSize;
    }

    public void setPointerThickness(float pointerThickness) {
        this.pointerThickness = pointerThickness;
    }

    public void setPointerColor(int pointerColor){
        this.pointerColor = pointerColor;
    }

    public void setOnDrawListener(OnDrawListener listener){
        this.onDrawListener = listener;
    }

    public interface OnTouchListener{
        void onTouch(float posX, float posY, int color);
    }

    public interface OnDrawListener{
        void onDrawFirstTime();
        void onDraw();
        void onCalculatedBitmap(Bitmap bitmap);
    }
}
