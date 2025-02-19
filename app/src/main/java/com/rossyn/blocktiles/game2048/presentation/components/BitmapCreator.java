package com.rossyn.blocktiles.game2048.presentation.components;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.rossyn.blocktiles.game2048.presentation.activities.GameActivity;
import com.rossyn.blocktiles.game2048.R;

import java.util.ArrayList;


public final class BitmapCreator {

    private final Context context = GameActivity.getContext();
    public static int cellDefaultHeight, cellDefaultWidth, exponent;

    private final Paint mPaint = new Paint();
    private Rect textBounds = new Rect();
    private static ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

    public int getCellDefaultWidth() {
        return cellDefaultWidth;
    }

    public int getCellDefaultHeight() {
        return cellDefaultHeight;
    }

    public Bitmap createBlockTile() {
        Drawable drawable = context.getDrawable(R.drawable.block_shape);
        Bitmap bitmap = Bitmap.createBitmap(cellDefaultWidth, cellDefaultHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        assert drawable != null;
        drawable.setBounds(0, 0, cellDefaultWidth, cellDefaultHeight);
        drawable.draw(canvas);
        return bitmap;
    }

    public void createBitmap(int index) {
        Drawable drawable = createDrawable(index);
        long value = (long) Math.pow(exponent, index + 1);
        String text = Long.toString(value);
        Bitmap bitmap = Bitmap.createBitmap(cellDefaultWidth, cellDefaultHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);


        Typeface customTypeface = ResourcesCompat.getFont(context, R.font.baloo);
        mPaint.setTypeface(customTypeface);
        mPaint.setColor(Color.WHITE);
        int textSize = 130;
        mPaint.setTextSize(textSize);
        drawable.setBounds(0, 0, cellDefaultWidth, cellDefaultHeight);
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        while (textBounds.height() > cellDefaultWidth / 2.5 && textSize >= 10) {
            textSize -= 20;
            mPaint.setTextSize(textSize);
            mPaint.getTextBounds(text, 0, text.length(), textBounds);
            mPaint.getTextBounds(text, 0, text.length(), textBounds);
        }
        while (textBounds.width() > cellDefaultWidth - 20 && textSize >= 10) {
            textSize -= 10;
            mPaint.setTextSize(textSize);
            mPaint.getTextBounds(text, 0, text.length(), textBounds);
            mPaint.getTextBounds(text, 0, text.length(), textBounds);
        }
        drawable.draw(canvas);
        canvas.drawText(text, (float) cellDefaultWidth / 2 - textBounds.exactCenterX(), (float) cellDefaultHeight / 2 - textBounds.exactCenterY(), mPaint);
        bitmapArrayList.add(bitmap);
    }

    public Bitmap getBitmap(long value) {
        if (value == 1) {
            return createBlockTile();
        }

        double val = Math.log(value) / Math.log(exponent);
        val = Math.round(val);
        int index = (int) (val - 1);


        if (bitmapArrayList.isEmpty()) {
            for (int i = 0; i < 12; i++) {
                createBitmap(i);
            }
        }
        if (index == bitmapArrayList.size()) {
            createBitmap(index);
        }

        return bitmapArrayList.get(index);
    }

    public void clearBitmapArray() {
        bitmapArrayList.clear();
    }

    public Drawable createDrawable(int index) {

        Drawable drawable = context.getDrawable(R.drawable.cell_shape);
        if (drawable != null) {
            switch (index) {

                case 0:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value2), PorterDuff.Mode.SRC_OVER);
                    break;
                case 1:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value4), PorterDuff.Mode.SRC_OVER);
                    break;
                case 2:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value8), PorterDuff.Mode.SRC_OVER);
                    break;
                case 3:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value16), PorterDuff.Mode.SRC_OVER);
                    break;
                case 4:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value32), PorterDuff.Mode.SRC_OVER);
                    break;
                case 5:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value64), PorterDuff.Mode.SRC_OVER);
                    break;
                case 6:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value128), PorterDuff.Mode.SRC_OVER);
                    break;
                case 7:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value256), PorterDuff.Mode.SRC_OVER);
                    break;
                case 8:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value512), PorterDuff.Mode.SRC_OVER);
                    break;
                case 9:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value1024), PorterDuff.Mode.SRC_OVER);
                    break;
                case 10:
                    drawable.setColorFilter(context.getResources().getColor(R.color.value2048), PorterDuff.Mode.SRC_OVER);
                    break;
                default:
                    drawable.setColorFilter(context.getResources().getColor(R.color.valueOther), PorterDuff.Mode.SRC_OVER);
                    break;
            }
        }
        return drawable;
    }


}
