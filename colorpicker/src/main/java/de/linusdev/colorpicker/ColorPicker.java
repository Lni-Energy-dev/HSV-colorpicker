package de.linusdev.colorpicker;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import de.linusdev.colorpicker.CustomViews.ColorCircleView;
import de.linusdev.colorpicker.CustomViews.ColorPreView;
import de.linusdev.colorpicker.CustomViews.SeekBars.AlphaSeekBar;
import de.linusdev.colorpicker.CustomViews.SeekBars.CustomSeekBar;
import de.linusdev.colorpicker.CustomViews.SeekBars.SaturationSeekBar;

public class ColorPicker extends DialogFragment {

    private final static String TITLE_KEY = "title";
    private final static String SUBTITLE_KEY = "subtitle";
    private final static String SATURATION_KEY = "saturation";
    private final static String ALPHA_KEY = "alpha";
    private final static String COLOR_KEY = "color";
    private final static String PREFABS_KEY = "prefabs";

    private final static String POINTER_SIZE_KEY = "pointer_size";
    private final static String POINTER_THICKNESS_KEY = "pointer_thickness";
    private final static String POINTER_COLOR_KEY = "pointer_color";

    private final static String AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL = "auto_dismiss";

    private final static float DEFAULT_SATURATION = 1f;
    private final static int DEFAULT_ALPHA = 255;
    private final static int DEFAULT_COLOR = 0xff00ffd7;
    private final static int[] DEFAULT_PREFABS = { Color.BLACK, Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.WHITE};
    private final static int[] DEFAULT_PREFABS_2 = {
            Color.argb(255,   0,   0,   0),
            Color.argb(255, 255, 255, 255),
    };
    private final static boolean DEFAULT_AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL = true;


    private TextView title;
    private TextView subTitle;
    private ColorPreView preView;
    private ProgressBar progressBar;
    private ColorCircleView circleView;
    private RecyclerView prefabs_view;

    private SaturationSeekBar saturationSeekBar;
    private AlphaSeekBar alphaSeekBar;

    private EditText colorEditText;

    private Button okButton;
    private Button cancelButton;

    private float saturation = DEFAULT_SATURATION;
    private int alpha = DEFAULT_ALPHA;
    private int color = DEFAULT_COLOR;

    private int[] prefabs = DEFAULT_PREFABS;

    private boolean autoDismiss = DEFAULT_AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL;

    private boolean landscape = false;

    private OnUserInteractionListener listener = null;

    private static ColorPicker newInstance(@NonNull Bundle args){
        ColorPicker colorPicker = new ColorPicker();

        colorPicker.setArguments(args);


        return colorPicker;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            System.out.println("on create");
            System.out.println(savedInstanceState.toString());
            setArguments(savedInstanceState);
        }
        if(getDialog() != null) {
            checkArguments();
            updateColor();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            landscape = true;
            v = inflater.inflate(R.layout.color_picker, container, false);
        } else {
            landscape = false;
            v = inflater.inflate(R.layout.color_picker, container, false);
        }

        //Make the Dialog round AND MAKES IT BIGGER
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_round_corners);
        getDialog().setCancelable(false);



        title = v.findViewById(R.id.title);
        subTitle = v.findViewById(R.id.subtitle);
        preView = v.findViewById(R.id.preview_imageView);
        progressBar = v.findViewById(R.id.progressBar);
        circleView = v.findViewById(R.id.circle_view);
        prefabs_view = v.findViewById(R.id.color_prefabs);

        saturationSeekBar = v.findViewById(R.id.saturation_seekBar);
        alphaSeekBar = v.findViewById(R.id.alpha_seekBar);

        colorEditText = v.findViewById(R.id.color_editText);

        okButton = v.findViewById(R.id.select_button);
        cancelButton = v.findViewById(R.id.cancel_button);

        checkArguments();


        circleView.setOnDrawListener(new ColorCircleView.OnDrawListener() {
            @Override
            public void onDrawFirstTime() {

            }

            @Override
            public void onDraw() {
                updateColor();
            }

            @Override
            public void onCalculatedBitmap(Bitmap bitmap) {

            }
        });

        if(false){
            prefabs_view.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
            prefabs_view.setAdapter(new ColorPrefabsAdapter(prefabs));
        }else {
            prefabs_view.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
            prefabs_view.setAdapter(new ColorPrefabsAdapter(prefabs));
        }


        if(listener != null){
            listener.onColorChanged(getDialog(), updateColor());
        }else{
            updateColor();
        }

        ((ColorPrefabsAdapter)(prefabs_view.getAdapter())).setClickListener(new ColorPrefabsAdapter.ClickListener() {
            @Override
            public void onColorClicked(int color) {
                if(listener != null) {
                    listener.onColorChanged(getDialog(), setHSVByColor(color));
                }else{
                    updateColor();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoDismiss) getDialog().dismiss();
                if(listener != null){
                    listener.onCancel(getDialog());
                }else{
                    updateColor();
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoDismiss) getDialog().dismiss();
                if(listener != null){
                    listener.onColorSelected(getDialog(), updateColor());
                }else{
                    updateColor();
                }
            }
        });

        saturationSeekBar.setOnProgressChangeListener(new CustomSeekBar.OnProgressChangeListener() {
            @Override
            public void progressChanged(int progress) {
                saturation = (float)progress / 10000f;
                if(listener != null){
                    listener.onColorChanged(getDialog(), updateColor());
                }else{
                    updateColor();
                }
            }
        });

        alphaSeekBar.setOnProgressChangeListener(new CustomSeekBar.OnProgressChangeListener() {
            @Override
            public void progressChanged(int progress) {
                alpha = progress;
                if(listener != null){
                    listener.onColorChanged(getDialog(), updateColor());
                }else{
                    updateColor();
                }
            }
        });


        circleView.setOnTouchListener(new ColorCircleView.OnTouchListener() {
            @Override
            public void onTouch(float posX, float posY, final int c) {
                if(getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        color = c;
                        if(listener != null){
                            listener.onColorChanged(getDialog(), updateColor());
                        }else{
                            updateColor();
                        }
                    }
                });
            }
        });

        View.OnLongClickListener copyToClipboard = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(getContext() == null) return false;

                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                if(clipboardManager == null) return false;

                ClipData clip = ClipData.newPlainText(colorEditText.getText(), colorEditText.getText());
                clipboardManager.setPrimaryClip(clip);

                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_LONG).show();

                return true;
            }
        };

        colorEditText.setEnabled(false);
        colorEditText.setOnLongClickListener(copyToClipboard);


        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void checkArguments(){
        Bundle args = getArguments();
        if(args == null)return;

        if(args.containsKey(TITLE_KEY)) {
            title.setText(getArguments().getString(TITLE_KEY));
        }else{
            title.setVisibility(View.GONE);
        }

        if(args.containsKey(SUBTITLE_KEY)) {
            subTitle.setText(getArguments().getString(SUBTITLE_KEY));
        }else{
            subTitle.setVisibility(View.GONE);
        }

        if(args.containsKey(SATURATION_KEY)) {
            saturation = args.getFloat(SATURATION_KEY);
        }else{
            saturation = DEFAULT_SATURATION;
        }

        if(args.containsKey(ALPHA_KEY)) {
            alpha = args.getInt(ALPHA_KEY);
        }else{
            alpha = DEFAULT_ALPHA;
        }

        if(args.containsKey(COLOR_KEY)) {
            color = args.getInt(COLOR_KEY);
        }else{
            color = DEFAULT_COLOR;
        }

        if(args.containsKey(PREFABS_KEY)) {
            prefabs = args.getIntArray(PREFABS_KEY);
        }else{
            prefabs = DEFAULT_PREFABS;
        }

        if(args.containsKey(POINTER_COLOR_KEY)) {
            circleView.setPointerColor(args.getInt(POINTER_COLOR_KEY));
        }else{
            circleView.setPointerColor(ColorCircleView.POINTER_COLOR_INVERT);
        }

        if(args.containsKey(POINTER_SIZE_KEY)) {
            circleView.setPointerSize(args.getFloat(POINTER_SIZE_KEY));
        }else{
            circleView.setPointerSize(ColorCircleView.POINTER_SIZE_DEFAULT);
        }

        if(args.containsKey(POINTER_THICKNESS_KEY)) {
            circleView.setPointerThickness(args.getFloat(POINTER_THICKNESS_KEY));
        }else{
            circleView.setPointerThickness(ColorCircleView.POINTER_THICKNESS_DEFAULT);
        }

        if(args.containsKey(AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL)){
            autoDismiss = args.getBoolean(AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL);
        }else{
            autoDismiss = DEFAULT_AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL;
        }
    }

    private int updateColor(){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        hsv[2] = saturation;

        int outColor = Color.HSVToColor(alpha, hsv);

        preView.setColor(outColor);


        alphaSeekBar.setProgress(alpha);
        saturationSeekBar.setProgress((int)(saturation*10000f));
        saturationSeekBar.setColor(color);
        alphaSeekBar.setColor(outColor);

        String alpha = Integer.toHexString(Color.alpha(outColor));
        if(alpha.length() == 1) alpha = "0" + alpha;

        String red = Integer.toHexString(Color.red(outColor));
        if(red.length() == 1) red = "0" + red;

        String green = Integer.toHexString(Color.green(outColor));
        if(green.length() == 1) green = "0" + green;

        String blue = Integer.toHexString(Color.blue(outColor));
        if(blue.length() == 1) blue = "0" + blue;

        String hexColor = "#" + alpha + red + green + blue ;
        colorEditText.setText(hexColor);

        return outColor;
    }

    private int setHSVByColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        saturation = hsv[2];
        alpha = Color.alpha(color);

        alphaSeekBar.setProgress(alpha);
        saturationSeekBar.setProgress((int)(saturation*10000f));

        this.color = color;

        return updateColor();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        System.out.println("TETXIZUFhgdsapiufjnhdöloksfmnlöojdskfg");

        Bundle args = getArguments();
        if(args == null)return;

        if(args.containsKey(TITLE_KEY)) {
            outState.putString(TITLE_KEY, getArguments().getString(TITLE_KEY));
        }

        if(args.containsKey(SUBTITLE_KEY)) {
            outState.putString(SUBTITLE_KEY, getArguments().getString(SUBTITLE_KEY));
        }


        outState.putFloat(SATURATION_KEY, saturation);



        outState.putInt(ALPHA_KEY, alpha);



        outState.putInt(COLOR_KEY, color);



        outState.putIntArray(PREFABS_KEY, prefabs);


        if(args.containsKey(POINTER_COLOR_KEY)) {
            outState.putInt(POINTER_COLOR_KEY, args.getInt(POINTER_COLOR_KEY));
        }

        if(args.containsKey(POINTER_SIZE_KEY)) {
            outState.putFloat(POINTER_COLOR_KEY, args.getFloat(POINTER_COLOR_KEY));
        }

        if(args.containsKey(POINTER_THICKNESS_KEY)) {
            outState.putFloat(POINTER_COLOR_KEY, args.getFloat(POINTER_COLOR_KEY));
        }

        if(args.containsKey(AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL)){
            outState.putBoolean(AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL, args.getBoolean(AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL));
        }

        super.onSaveInstanceState(outState);
    }

    public void setOnUserInteractionListener(OnUserInteractionListener listener){
        this.listener = listener;
    }

    public static class Builder{

        private Bundle args;

        public Builder(){
            args = new Bundle();
        }

        /**
         * Sets the Title of the Dialog,
         * leave it empty if u don`t need one
         * @param title to set
         * @return this
         */
        public Builder setTitle(String title){
            args.putString(TITLE_KEY,title);
            return this;
        }

        /**
         * Sets the Subtitle of the Dialog,
         * leave it empty if u don`t need one
         * @param subTitle
         * @return this
         */
        public Builder setSubTitle(String subTitle){
            args.putString(SUBTITLE_KEY, subTitle);
            return this;
        }

        /**
         * Sets the saturation of the Color showing
         * up when the dialog is opened
         * @param saturation as a Float from 0f to 1f
         * @return this
         */
        public Builder setSaturation(float saturation){
            args.putFloat(SATURATION_KEY,saturation);
            return this;
        }

        /**
         * Sets the Alpha of the Color showing up
         * when the dialog is opened
         * @param alpha as an Integer from 0(transparent) to 255(not transparent)
         * @return this
         */
        public Builder setAlpha(int alpha){
            args.putInt(ALPHA_KEY, alpha);
            return this;
        }

        /**
         * Sets the Color showing up when the
         * dialog is opened, also sets the alpha
         * and saturation
         * @param color with alpha(!), red, green, blue. for example 0xff00ff00
         * @return this
         */
        public Builder setColor(int color){
            args.putInt(COLOR_KEY, color);

            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);

            this.setSaturation(hsv[2]);
            this.setAlpha(Color.alpha(color));

            return this;
        }

        /**
         * Sets the Prefabs the user can select
         * @param prefabs Integer Array, every color with alpha(!), red, green, blue. for example 0xff00ff00
         * @return this
         */
        public Builder setPrefabs(int[] prefabs){
            args.putIntArray(PREFABS_KEY, prefabs);
            return this;
        }

        /**
         * Sets the size of the pointer showing up when the user selects the Color in the HSV-Color-Wheel
         * @param size as a Float
         * @return this
         */
        public Builder setPointerSize(float size){
            args.putFloat(POINTER_SIZE_KEY, size);
            return this;
        }

        /**
         * Sets the thickness of the pointer showing up when the user selects the Color in the HSV-Color-Wheel
         * @param thickness as a Float
         * @return this
         */
        public Builder setPointerThickness(float thickness){
            args.putFloat(POINTER_THICKNESS_KEY, thickness);
            return this;
        }

        /**
         * Sets the pointerColor of the pointer showing up when the user selects the Color in the HSV-Color-Wheel
         * @param pointerColor with alpha(!), red, green, blue. for example 0xff000000
         * @return this
         */
        public Builder setPointerColor(int pointerColor){
            args.putInt(POINTER_COLOR_KEY, pointerColor);
            return this;
        }

        /**
         * Sets if the Dialog should dismiss itself when a color is selected or the cancel button was pressed
         * @param autoDismiss
         * @return this
         */
        public Builder setAutoDismissOnSelectColorAndOnCancel(boolean autoDismiss){
            args.putBoolean(AUTO_DISMISS_ON_SELECT_COLOR_OR_ON_CANCEL, autoDismiss);
            return this;
        }

        /**
         * finally builds the ColorPicker
         * @return ColorPicker with the given Arguments you set before
         */
        public ColorPicker buildAWall(){
            return ColorPicker.newInstance(args);
        }

        /**
         * finally builds the ColorPicker
         * @param listener the OnUserInteractionListener which should be applied to the ColorPicker
         * @return ColorPicker with the given Arguments you set before
         */
        public ColorPicker buildAWall(OnUserInteractionListener listener){
            ColorPicker colorPicker = ColorPicker.newInstance(args);
            colorPicker.setOnUserInteractionListener(listener);
            return colorPicker;
        }
    }

    public interface OnUserInteractionListener{
        void onCancel(Dialog dialog);
        void onColorChanged(Dialog dialog, int color);
        void onColorSelected(Dialog dialog, int color);
    }
}
