package com.utsavsoft.mergetiles.game2048.maingamecode;

public class ScoreModel {
    private String boardType;
    private long score;
    private int icon;

    public ScoreModel(String boardType, long score, int icon) {
        this.boardType = boardType;
        this.score = score;
        this.icon = icon;
    }

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getScore() {
        return score;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }

}