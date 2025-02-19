package com.rossyn.blocktiles.game2048.domain.models;

import android.graphics.drawable.Drawable;

public class BoardType {

    public int rows;
    public int cols;
    public String typeString;
    public Drawable drawable;

    public BoardType(int rows, int cols, Drawable drawable) {
        this.rows = rows;
        this.cols = cols;
        this.drawable = drawable;
        typeString = rows + "x" + cols;
    }

    public String getTypeString() {
        return typeString;
    }
}
