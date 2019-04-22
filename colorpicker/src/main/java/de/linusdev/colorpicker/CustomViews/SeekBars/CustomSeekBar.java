package de.linusdev.colorpicker.CustomViews.SeekBars;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class CustomSeekBar extends View {

    private int progressBarHeight = 10;
    private int progressOffsetLeft = 100;
    private int progressOffsetRight = 100;
    private float progressLeftPercent = -1f;
    private float progressRightPercent = -1f;
    private float progressHeightPercent = -1f;
    private boolean progressUsePercentForSize = false;
    private Drawable progressDrawable;


    private int thumbHeight = 30;
    private int thumbWidth = 30;
    private float thumbPercentHeight = -1f;
    private float thumbPercentWidth = -1f;
    private boolean thumbUsePercentForSize = false;
    private boolean thumbWidthEqualtoHeight = false;
    private Drawable thumb;

    private int maxProgress = 100;
    private int progress = 0;
    private float progressToPixel = 0f;
    private float pixelToProgress = 0f;

    private OnProgressChangeListener changeListener = null;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(progressDrawable != null) {

            if(progressUsePercentForSize){
                progressOffsetLeft = (int) ((float)getWidth()*progressLeftPercent);
                progressOffsetRight = (int) ((float)getWidth()*progressRightPercent);
                progressBarHeight = (int) ((float)getHeight()*progressHeightPercent);
            }

            progressDrawable.setBounds(progressOffsetLeft, getHeight() / 2 - progressBarHeight / 2, getWidth() - progressOffsetRight, getHeight() / 2 + progressBarHeight / 2);
            progressDrawable.draw(canvas);
        }

        if(thumb != null){

            if(thumbUsePercentForSize){
                thumbHeight = (int)((float)getHeight()*thumbPercentHeight);
                if(thumbWidthEqualtoHeight){
                    thumbWidth = thumbHeight;
                }else {
                    thumbWidth = (int) ((float) getWidth() * thumbPercentWidth);
                }
            }

            progressToPixel = (float)(getWidth() - progressOffsetLeft - progressOffsetRight) / maxProgress;
            thumb.setBounds(progressOffsetLeft + (int) (progress*progressToPixel)  - thumbWidth/2, getHeight()/2 - thumbHeight/2, getWidth() - ((int)((maxProgress - progress)*progressToPixel) - thumbWidth/2) - progressOffsetRight, getHeight()/2 + thumbHeight/2);
            thumb.draw(canvas);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pixelToProgress = maxProgress / (float)(getWidth() - progressOffsetLeft - progressOffsetRight);
        float x = event.getX();

        progress = Math.min(maxProgress, Math.max(0, (int) (x*pixelToProgress)));

        invalidate();

        if(changeListener != null) {
            changeListener.progressChanged(progress);
        }
        return true;
    }


    public void setMaxProgress(int max){
        this.maxProgress = max;
        invalidate();
    }

    public void setProgress(int progress){
        this.progress = progress;
        invalidate();
    }

    public void setProgressDrawable(Drawable progressDrawable, int left, int right, int height){
        this.progressDrawable = progressDrawable;
        this.progressBarHeight = height;
        this.progressOffsetLeft = left;
        this.progressOffsetRight = right;
        this.progressUsePercentForSize = false;

        System.out.println("setProgressDrawable");
        System.out.println("height: " + height + " ,left: " + left);
        System.out.println("getHeight: " + getHeight() + " ,getWidth: " + getWidth());

        invalidate();
    }

    /**
     *
     * @param progressDrawable
     * @param leftPercent from 0.0 to 1.0
     * @param rightPercent from 0.0 to 1.0
     * @param heightPercent from 0.0 to 1.0
     */
    public void setProgressDrawable(Drawable progressDrawable, float leftPercent, float rightPercent, float heightPercent){
        this.progressDrawable = progressDrawable;
        this.progressLeftPercent = leftPercent;
        this.progressRightPercent = rightPercent;
        this.progressHeightPercent = heightPercent;
        this.progressUsePercentForSize = true;
    }

    public void setThumb(Drawable thumb, int width, int height){
        this.thumb = thumb;
        this.thumbWidth = width;
        this.thumbHeight = height;
        invalidate();
    }

    /**
     *
     * @param thumb
     * @param widthPercent from 0.0 to 1.0
     * @param heightPercent from 0.0 to 1.0
     */
    public void setThumb(Drawable thumb, float widthPercent, float heightPercent){
        this.thumb = thumb;
        this.thumbPercentWidth = widthPercent;
        this.thumbPercentHeight = heightPercent;
        this.thumbUsePercentForSize = true;
    }

    /**
     *  Width will have the same value as the Height
     * @param thumb
     * @param heightPercent from 0.0 to 1.0
     */
    public void setThumb(Drawable thumb, float heightPercent){
        this.thumb = thumb;
        this.thumbPercentHeight = heightPercent;
        this.thumbUsePercentForSize = true;
        this.thumbWidthEqualtoHeight = true;
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener){
        this.changeListener = listener;
    }

    public interface OnProgressChangeListener{
        void progressChanged(int progress);
    }
}
