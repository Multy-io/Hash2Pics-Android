package io.multy.hash2pics;

/**
 * Created by vadimicus on 25.06.2018.
 */



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

public class Hash2Pics extends AppCompatImageView {

    private int size;

    private RectF rect;
    private Path path;


    private int index = 1;
    private int maxValue;
    private String textToSet = null;

    private float density;
//    public byte[] hexAddress;

    ArrayList<Integer> keys = new ArrayList<>();
    ArrayList<String> colors = new ArrayList<>();

    public Hash2Pics(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();

    }

    public Hash2Pics(Context context){
        super(context);
        initializeView();
    }

    public Hash2Pics(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initializeView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH){
        this.size = w;
        super.onSizeChanged(w,h,oldW,oldH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        int screenHeight = MeasureSpec.getSize(heightMeasureSpec);
        rect.set(0, 0, screenWidth, screenHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        path.addRoundRect(rect, rect.centerY(), rect.centerY(), Path.Direction.CW);
        canvas.clipPath(path);

        Paint backgroundPaint = new Paint();


        if (keys.size() == 0){
            backgroundPaint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.height() /2, backgroundPaint);
        } else {

            backgroundPaint.setColor(Color.parseColor(getColor(getNextValue())));

            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.height() /2, backgroundPaint);

            //Rect1
            drawRect(canvas, 1);

            //Rect2
            drawRect(canvas, 2);

            //Rect3
            drawRect(canvas, 3);


            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);

            int textSize = (int) (100f * density);
            textPaint.setTextSize(textSize);

            float blurRadius = 5f * density;
            float shadowY = 3f * density;



            textPaint.setShadowLayer(blurRadius, 0, shadowY, Color.BLACK);



            float measureX = textPaint.measureText(textToSet);
            canvas.drawText(textToSet, (size-measureX)/2, (size/2 + textSize/3), textPaint);


            colors.clear();
            fitColors();
        }

        super.onDraw(canvas);
    }



    private void drawRect(Canvas canvas, int shapeIndex){

        Paint paint = new Paint();
        paint.setColor(Color.parseColor(getColor(getNextValue())));

        Matrix m =  new Matrix();


        int degrees = (int) (getNextProportrionValue() * 360.0f);
        m.postRotate(degrees, size/2 , size/2);

        //This is translate. Depends on the shape index
        float translateMultiplier = 0f;

        switch (shapeIndex){
            case 2:
                translateMultiplier = -1.0f;
                break;
            case 3:
                translateMultiplier = 1.0f;
                break;
        }

        float txURaw = getNextProportrionValue() * (float)size;
        float txRaw = txURaw > (float)size/2 ? ((float)size /2 - txURaw) : txURaw;
        float tx = 0.6f * (float) size * translateMultiplier + txRaw;

        float tyURaw = getNextProportrionValue() * (float) size;
        float tyRaw = tyURaw > ((float) size/2) ? ((float)size /2 - tyURaw) : tyURaw;
        float ty = 0.6f * (float) size * translateMultiplier + tyRaw;

        m.postTranslate(tx, ty);
        Bitmap bitRect = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);

        Canvas canBit = new Canvas(bitRect);
        canBit.drawRect(0,0, size,size, paint);

        canvas.drawBitmap(bitRect, m, paint);
    }

    private void initializeView(){
        density = getResources().getDisplayMetrics().density;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        rect = new RectF();
        path = new Path();

        fitColors();
    }

    private void fitColors(){
//        String[] rawColors = getResources().getStringArray();
//
//        for (int i = 0; i < rawColors.length; i ++){
//            colors.add(rawColors[i]);
//        }
        colors.add("#01888C");
        colors.add("#FC7500");
        colors.add("#034F5D");
        colors.add("#F73F01");
        colors.add("#FC1960");
        colors.add("#C7144C");
        colors.add("#F3C100");
        colors.add("#1598F2");
        colors.add("#2465E1");
        colors.add("#F19E02");
    }

    public void setAvatar(String address){

        byte[] hexAddress = address.getBytes(Charset.forName("UTF-8"));


        textToSet = address.substring(address.length() - 2, address.length()).toUpperCase();

        index = 1;
        maxValue = 1;
        keys.clear();
        for (int i = 0; i < hexAddress.length; i++){
            keys.add(new Byte(hexAddress[i]).intValue());
        }

        maxValue = Collections.max(keys);
        invalidate();
    }


    private float getNextProportrionValue(){
        float out = getNextValue()/(float) maxValue;
        return out;
    }

    private int getNextValue(){

        if (index == keys.size() && keys.size() > 1){
            index = 1;
        }

        int out = keys.get(index);
        index ++;
        return out;
    }

    private String getColor(int value){

        value = Math.abs(value);
        if (colors!= null){
            int idx = value % colors.size();

            String color = colors.get(idx);
            colors.remove(idx);
            return color;
        }
        else return "#000000";
    }
}
