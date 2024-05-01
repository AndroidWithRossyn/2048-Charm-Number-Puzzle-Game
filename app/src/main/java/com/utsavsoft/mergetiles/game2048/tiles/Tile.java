package com.utsavsoft.mergetiles.game2048.tiles;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.utsavsoft.mergetiles.game2048.maingamecode.GameBoardView;


public class Tile {
    private GameBoardView callback;

    private long value;
    private int winningValue, solidLives;
    private int currentPositionX, currentPositionY;
    private int desPositionX, desPositionY;
    private int currentCellHeight, currentCellWidth;
    private int defaultCellHeight, defaultCellWidth;
    private Position currentPosition, desPosition;
    private boolean isMoving = false;
    private boolean increased = false;
    private boolean isSolid = false;
    private boolean isSolidGone = false;
    private BitmapCreator bitmapCreator = new BitmapCreator();


    public Tile(long value, Position position, GameBoardView callback) {
        this.value = value;
        this.callback = callback;
        this.winningValue = callback.getWinningValue();

        defaultCellHeight = currentCellHeight = bitmapCreator.getCellDefaultHeight();
        defaultCellWidth = currentCellWidth = bitmapCreator.getCellDefaultWidth();

        currentPosition = desPosition = position;
        currentPositionX = desPositionX = currentPosition.getPositionX();
        currentPositionY = desPositionY = currentPosition.getPositionY();
        currentCellHeight = (defaultCellHeight / 5);
        currentCellWidth = (defaultCellWidth / 5);
    }

    public Tile(int value, Position position, GameBoardView callback, int solidLives) {
        this.value = value;
        this.callback = callback;

        defaultCellHeight = currentCellHeight = bitmapCreator.getCellDefaultHeight();
        defaultCellWidth = currentCellWidth = bitmapCreator.getCellDefaultWidth();

        currentPosition = desPosition = position;
        currentPositionX = desPositionX = currentPosition.getPositionX();
        currentPositionY = desPositionY = currentPosition.getPositionY();
        currentCellHeight = (defaultCellHeight / 5);
        currentCellWidth = (defaultCellWidth / 5);

        this.isSolid = true;
        this.solidLives = solidLives;
    }


    public long getValue() {
        return value;
    }

    public Position getPosition() {
        return currentPosition;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public void decreaseLiveCount() {
        this.solidLives--;
    }

    public void move(Position position) {
        this.desPosition = position;
        desPositionX = desPosition.getPositionX();
        desPositionY = desPosition.getPositionY();
        this.isMoving = true;
    }

    public boolean notAlreadyIncreased() {
        return !increased;
    }

    public void setIncreased(boolean state) {
        increased = state;
    }

    public Tile copyTile() {
        return new Tile(this.value, this.currentPosition, this.callback);
    }

    public void increaseValue() {
        this.value *= callback.getExponent();
        currentCellHeight = (int) (defaultCellHeight * 1.4);
        currentCellWidth = (int) (defaultCellWidth * 1.4);
        increased = false;
        if (value == winningValue) {
            callback.setPlayerWon();
        }
    }

    public void update() {
        if (isMoving) {
            updatePosition();
        }
        if (isSolid && solidLives == 1) {
            removeSolidBlock();
            return;
        }
        updateSize();

    }

    public void updatePosition() {
        int movingSpeed = 100;

        if (currentPositionX < desPositionX) {
            if (currentPositionX + movingSpeed > desPositionX) {
                currentPosition = desPosition;
                currentPositionX = currentPosition.getPositionX();
            } else {
                currentPositionX += movingSpeed;
            }
        } else if (currentPositionX > desPositionX) {
            if (currentPositionX - movingSpeed < desPositionX) {
                currentPosition = desPosition;
                currentPositionX = currentPosition.getPositionX();
            } else {
                currentPositionX -= movingSpeed;
            }
        }
        if (currentPositionY < desPositionY) {
            if (currentPositionY + movingSpeed > desPositionY) {
                currentPosition = desPosition;
                currentPositionY = currentPosition.getPositionY();
            } else {
                currentPositionY += movingSpeed;
            }
        } else if (currentPositionY > desPositionY) {
            if (currentPositionY - movingSpeed < desPositionY) {
                currentPosition = desPosition;
                currentPositionY = currentPosition.getPositionY();
            } else {
                currentPositionY -= movingSpeed;
            }
        }
    }

    public void updateSize() {
        int sizeSpeed = 20;

        if (currentCellHeight < defaultCellHeight || currentCellWidth < defaultCellWidth) {
            if (currentCellHeight + sizeSpeed > defaultCellHeight || currentCellWidth + sizeSpeed > defaultCellWidth) {
                currentCellHeight = defaultCellHeight;
                currentCellWidth = defaultCellWidth;

            } else {
                currentCellHeight += sizeSpeed;
                currentCellWidth += sizeSpeed;
            }
        }
        if (currentCellHeight > defaultCellHeight || currentCellWidth > defaultCellWidth) {
            if (currentCellHeight - sizeSpeed < defaultCellHeight || currentCellWidth - sizeSpeed < defaultCellWidth) {
                currentCellHeight = defaultCellHeight;
                currentCellWidth = defaultCellWidth;

            } else {
                currentCellHeight -= sizeSpeed;
                currentCellWidth -= sizeSpeed;
            }
        }

    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = bitmapCreator.getBitmap(value);
        bitmap = Bitmap.createScaledBitmap(bitmap, currentCellWidth, currentCellHeight, false);
        canvas.drawBitmap(bitmap, (int) (currentPositionX + (double) (defaultCellWidth / callback.getExponent() - currentCellWidth / callback.getExponent()))
                , (int) (currentPositionY + (double) (defaultCellHeight / callback.getExponent() - currentCellHeight / callback.getExponent())), null);
        if (isMoving && currentPosition == desPosition && currentCellWidth == defaultCellWidth) {
            isMoving = false;
            if (increased) {
                callback.updateScore(this.getValue());
                increaseValue();
            }
            callback.finishedMoving(this);
        }
    }

    public boolean needsToUpdate() {

        return currentPosition != desPosition || currentCellWidth != defaultCellWidth;
    }

    public Boolean isSolidGone() {

        return isSolidGone;
    }

    void removeSolidBlock() {

        int sizeSpeed = 20;
        if (currentCellHeight - sizeSpeed <= 0 || currentCellWidth - sizeSpeed <= 0) {
            isSolidGone = true;
        }
        currentCellHeight = currentCellHeight - sizeSpeed;
        currentCellWidth = currentCellHeight - sizeSpeed;
    }
}
