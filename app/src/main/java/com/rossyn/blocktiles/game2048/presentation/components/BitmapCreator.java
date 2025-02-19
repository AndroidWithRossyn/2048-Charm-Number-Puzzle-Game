package com.rossyn.blocktiles.game2048.presentation.components;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.rossyn.blocktiles.game2048.R;

import java.util.ArrayList;


public final class BitmapCreator {

    private final Context context;
    public static int cellDefaultHeight, cellDefaultWidth, exponent;

    private final Paint mPaint;
    private final Rect textBounds = new Rect();
    private static final ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();


    public BitmapCreator(Context context) {
        this.context = context.getApplicationContext();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public int getCellDefaultWidth() {
        return cellDefaultWidth;
    }

    public int getCellDefaultHeight() {
        return cellDefaultHeight;
    }

    public Bitmap createBlockTile() {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.block_shape);
        Bitmap bitmap = Bitmap.createBitmap(cellDefaultWidth, cellDefaultHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (drawable != null) {
            drawable.setBounds(0, 0, cellDefaultWidth, cellDefaultHeight);
            drawable.draw(canvas);
        }
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
        }

        while (textBounds.width() > cellDefaultWidth - 20 && textSize >= 10) {
            textSize -= 10;
            mPaint.setTextSize(textSize);
            mPaint.getTextBounds(text, 0, text.length(), textBounds);
        }


        drawable.draw(canvas);
        float x = (cellDefaultWidth - textBounds.width()) / 2f - textBounds.left;
        float y = (cellDefaultHeight + textBounds.height()) / 2f - textBounds.bottom;
        canvas.drawText(text, x, y, mPaint);

        bitmapArrayList.add(bitmap);
    }

    public Bitmap getBitmap(long value) {
        if (value == 1) {
            return createBlockTile();
        }

        double val = Math.log(value) / Math.log(exponent);
        int index = (int) Math.round(val) - 1;

        if (bitmapArrayList.isEmpty()) {
            for (int i = 0; i < 12; i++) {
                createBitmap(i);
            }
        }
        if (index >= bitmapArrayList.size()) {
            createBitmap(index);
        }

        return bitmapArrayList.get(index);
    }

    public void clearBitmapArray() {
        bitmapArrayList.clear();
    }

    public Drawable createDrawable(int index) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cell_shape);
        if (drawable != null) {
            int color;
            switch (index) {
                case 0:
                    color = ContextCompat.getColor(context, R.color.value2);
                    break;
                case 1:
                    color = ContextCompat.getColor(context, R.color.value4);
                    break;
                case 2:
                    color = ContextCompat.getColor(context, R.color.value8);
                    break;
                case 3:
                    color = ContextCompat.getColor(context, R.color.value16);
                    break;
                case 4:
                    color = ContextCompat.getColor(context, R.color.value32);
                    break;
                case 5:
                    color = ContextCompat.getColor(context, R.color.value64);
                    break;
                case 6:
                    color = ContextCompat.getColor(context, R.color.value128);
                    break;
                case 7:
                    color = ContextCompat.getColor(context, R.color.value256);
                    break;
                case 8:
                    color = ContextCompat.getColor(context, R.color.value512);
                    break;
                case 9:
                    color = ContextCompat.getColor(context, R.color.value1024);
                    break;
                case 10:
                    color = ContextCompat.getColor(context, R.color.value2048);
                    break;
                default:
                    color = ContextCompat.getColor(context, R.color.valueOther);
                    break;
            }

            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER));
        }
        return drawable;
    }
}
