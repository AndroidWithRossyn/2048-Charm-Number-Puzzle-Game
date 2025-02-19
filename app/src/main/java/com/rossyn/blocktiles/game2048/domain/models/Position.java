package com.rossyn.blocktiles.game2048.domain.models;

public class Position {
    private final int positionX;
    private final int positionY;
    public Position(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }
    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }


}
