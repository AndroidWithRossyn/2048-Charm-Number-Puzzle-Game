package com.utsavsoft.mergetiles.game2048.maingamecode;

import android.graphics.Canvas;

import com.utsavsoft.mergetiles.game2048.tiles.Position;
import com.utsavsoft.mergetiles.game2048.tiles.Tile;

import java.util.ArrayList;
import java.util.Random;

public final class GameBoardView {

    private static final int GAME_MODE_SOLID_TILE = 1;
    private static final int GAME_MODE_SHUFFLE = 2;
    private static final int NUM_SOLID_LIVES = 10;

    private Tile[][] tempBoard;
    private Tile[][] board;
    private Tile[][] oldBoard;
    private final Position[][] positions;
    private final int boardRows, boardCols, exponent, gameMode, winningValue;

    Random rand = new Random();

    private boolean gameOver = false;
    private boolean gameWon = false;
    private boolean isMoving = false;
    private boolean spawnNeeded = false;
    private boolean canUndo;
    private boolean boardIsInitialized;
    private boolean tutorialIsPlaying;
    private ArrayList<Tile> movingTiles;
    private final GameViewCell callback;
    private long currentScore, oldScore;

    public GameBoardView(int rows, int cols, int exponentValue, GameViewCell callback, int gameMode) {
        exponent = exponentValue;
        boardRows = rows;
        boardCols = cols;
        board = new Tile[rows][cols];
        positions = new Position[rows][cols];
        this.callback = callback;
        currentScore = 0;
        boardIsInitialized = false;
        this.gameMode = gameMode;
        this.winningValue = (int) Math.pow(exponent, 11);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Tile getTile(int x, int y) {
        return board[x][y];
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int getExponent() {
        return exponent;
    }

    public int getRows() {
        return boardRows;
    }

    public int getCols() {
        return boardCols;
    }

    public void setPositions(int matrixX, int matrixY, int positionX, int positionY) {
        Position position = new Position(positionX, positionY);
        positions[matrixX][matrixY] = position;
    }

    public void setPlayerWon() {
        gameWon = true;
    }

    public int getWinningValue() {
        return winningValue;
    }

    public void initBoard() {
        if (!boardIsInitialized) {
            addRandom();
            addRandom();
            movingTiles = new ArrayList<>();
            boardIsInitialized = true;
        }
    }

    public void initTutorialBoard() {
        tutorialIsPlaying = true;
        board[0][0] = new Tile(exponent, positions[0][0], this);
        board[1][2] = new Tile(exponent, positions[1][2], this);
        movingTiles = new ArrayList<>();
        boardIsInitialized = true;
    }

    public void setTutorialFinished() {
        tutorialIsPlaying = false;
        resetGame();
    }

    public void draw(Canvas canvas) {
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (board[x][y] != null) {
                    board[x][y].draw(canvas);
                }
            }
        }
    }

    public void update() {
        boolean updating = false;
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (board[x][y] != null) {
                    board[x][y].update();
                    if (board[x][y].isSolidGone()) {
                        board[x][y] = null;
                        break;
                    }
                    if (board[x][y].needsToUpdate())
                        updating = true;
                }
            }
        }
        if (!updating) {
            checkIfGameOver();
        }
    }

    void addRandom() {
        int count = 0;
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (getTile(x, y) == null)
                    count++;
            }
        }
        int number = rand.nextInt(count);
        count = 0;
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (getTile(x, y) == null) {
                    if (count == number) {
                        board[x][y] = new Tile(exponent, positions[x][y], this);
                        return;
                    }
                    count++;
                }
            }
        }

    }


    void up() {
        saveBoardState();
        if (!isMoving) {
            isMoving = true;
            tempBoard = new Tile[boardRows][boardCols];

            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y];
                        for (int k = x - 1; k >= 0; k--) {
                            if (tempBoard[k][y] == null) {
                                tempBoard[k][y] = board[x][y];
                                if (tempBoard[k + 1][y] == board[x][y]) {
                                    tempBoard[k + 1][y] = null;
                                }
                            } else if (tempBoard[k][y].getValue() == board[x][y].getValue() && tempBoard[k][y].notAlreadyIncreased() && board[x][y].getValue() != 1) {
                                tempBoard[k][y] = board[x][y];
                                tempBoard[k][y].setIncreased(true);
                                if (tempBoard[k + 1][y] == board[x][y]) {
                                    tempBoard[k + 1][y] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = tempBoard;
        }
    }

    void left() {
        saveBoardState();
        if (!isMoving) {
            isMoving = true;
            tempBoard = new Tile[boardRows][boardCols];

            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y];
                        for (int k = y - 1; k >= 0; k--) {
                            if (tempBoard[x][k] == null) {
                                tempBoard[x][k] = board[x][y];
                                if (tempBoard[x][k + 1] == board[x][y]) {
                                    tempBoard[x][k + 1] = null;
                                }
                            } else if (tempBoard[x][k].getValue() == board[x][y].getValue() && tempBoard[x][k].notAlreadyIncreased() && board[x][y].getValue() != 1) {
                                tempBoard[x][k] = board[x][y];
                                tempBoard[x][k].setIncreased(true);
                                if (tempBoard[x][k + 1] == board[x][y]) {
                                    tempBoard[x][k + 1] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = tempBoard;
        }
    }

    void down() {
        saveBoardState();
        if (!isMoving) {
            isMoving = true;
            tempBoard = new Tile[boardRows][boardCols];

            for (int x = boardRows - 1; x >= 0; x--) {
                for (int y = 0; y < boardCols; y++) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y];
                        for (int k = x + 1; k < boardRows; k++) {
                            if (tempBoard[k][y] == null) {
                                tempBoard[k][y] = board[x][y];
                                if (tempBoard[k - 1][y] == board[x][y]) {
                                    tempBoard[k - 1][y] = null;
                                }
                            } else if (tempBoard[k][y].getValue() == board[x][y].getValue() && tempBoard[k][y].notAlreadyIncreased() && board[x][y].getValue() != 1) {
                                tempBoard[k][y] = board[x][y];
                                tempBoard[k][y].setIncreased(true);
                                if (tempBoard[k - 1][y] == board[x][y]) {
                                    tempBoard[k - 1][y] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = tempBoard;
        }
    }

    void right() {
        saveBoardState();
        if (!isMoving) {
            isMoving = true;
            tempBoard = new Tile[boardRows][boardCols];

            for (int x = 0; x < boardRows; x++) {
                for (int y = boardCols - 1; y >= 0; y--) {
                    if (board[x][y] != null) {
                        tempBoard[x][y] = board[x][y];
                        for (int k = y + 1; k < boardCols; k++) {
                            if (tempBoard[x][k] == null) {
                                tempBoard[x][k] = board[x][y];
                                if (tempBoard[x][k - 1] == board[x][y]) {
                                    tempBoard[x][k - 1] = null;
                                }
                            } else if (tempBoard[x][k].getValue() == board[x][y].getValue() && tempBoard[x][k].notAlreadyIncreased() && board[x][y].getValue() != 1) {
                                tempBoard[x][k] = board[x][y];
                                tempBoard[x][k].setIncreased(true);
                                if (tempBoard[x][k - 1] == board[x][y]) {
                                    tempBoard[x][k - 1] = null;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            moveTiles();
            board = tempBoard;
        }
    }


    public void moveTiles() {
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                Tile t = tempBoard[x][y];
                if (t != null) {
                    if (t.getPosition() != positions[x][y]) {
                        movingTiles.add(t);
                        t.move(positions[x][y]);
                    }
                }
            }
        }
        if (movingTiles.isEmpty()) {
            isMoving = false;
        } else
            spawnNeeded = true;
    }


    public void spawn() {
        if (spawnNeeded) {
            addRandom();
            spawnNeeded = false;
        }
    }

    public void finishedMoving(Tile t) {
        movingTiles.remove(t);
        if (movingTiles.isEmpty()) {
            callback.playSwipe();
            callback.updateScore(currentScore);
            isMoving = false;
            spawn();
            if (gameMode == GAME_MODE_SHUFFLE && !tutorialIsPlaying)
                shuffleBoard();
            if (gameMode == GAME_MODE_SOLID_TILE && !tutorialIsPlaying) {
                decreaseSolidLives();
                addRandomSolidTile();
            }
        }
    }

    public void checkIfGameOver() {
        gameOver = true;
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (board[x][y] == null) {
                    gameOver = false;
                    break;
                }
            }
        }
        if (gameOver) {
            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if ((x > 0 && board[x - 1][y].getValue() == board[x][y].getValue() && board[x][y].getValue() != 1) ||
                            (x < boardRows - 1 && board[x + 1][y].getValue() == board[x][y].getValue()) && board[x][y].getValue() != 1 ||
                            (y > 0 && board[x][y - 1].getValue() == board[x][y].getValue()) && board[x][y].getValue() != 1 ||
                            (y < boardCols - 1 && board[x][y + 1].getValue() == board[x][y].getValue() && board[x][y].getValue() != 1)) {
                        gameOver = false;
                        break;
                    }
                }
            }
        }
    }


    public void updateScore(long value) {
        if (tutorialIsPlaying) {
            callback.thirdScreenTutorial();
        } else {
            double val = Math.log(value) / Math.log(exponent);
            val = Math.round(val) + 1;
            int score = (int) Math.pow(2, val);
            currentScore += score;
        }
    }


    public void saveBoardState() {
        canUndo = true;
        oldBoard = new Tile[boardRows][boardCols];
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (board[x][y] != null) {
                    oldBoard[x][y] = board[x][y].copyTile();
                }
            }
        }
        oldScore = currentScore;
    }

    public void undoMove() {
        if (canUndo) {
            board = oldBoard;
            currentScore = oldScore;
            canUndo = false;
        }
    }

    public void resetGame() {
        gameOver = false;
        gameWon = false;
        canUndo = false;
        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                board[x][y] = null;
            }
        }
        currentScore = 0;
        addRandom();
        addRandom();
    }


    public void shuffleBoard() {
        int num = rand.nextInt(100);

        if (num <= 5 && num >= 0) {
            callback.ShowShufflingMsg();
            startShuffle();
        }
    }

    public void startShuffle() {

        Tile[][] newBoard = new Tile[boardRows][boardCols];
        int randX, randY;
        boolean moved;

        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (getTile(x, y) != null) {

                    moved = false;
                    while (!moved) {
                        randX = rand.nextInt(boardRows);
                        randY = rand.nextInt(boardCols);

                        if (newBoard[randX][randY] == null) {
                            newBoard[randX][randY] = new Tile(board[x][y].getValue(), positions[randX][randY], this);
                            moved = true;
                        }
                    }

                }
            }
        }
        board = newBoard;
    }

    public void addRandomSolidTile() {

        int num = rand.nextInt(100);

        if (num >= 0 && num <= 5) {

            int count = 0;
            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if (getTile(x, y) == null)
                        count++;
                }
            }
            int number = rand.nextInt(count);
            count = 0;
            for (int x = 0; x < boardRows; x++) {
                for (int y = 0; y < boardCols; y++) {
                    if (getTile(x, y) == null) {
                        if (count == number) {
                            board[x][y] = new Tile(1, positions[x][y], this, NUM_SOLID_LIVES); //here we need to send a special value and set a bitmap to look like a solid block, value should be a special one that can never be merged with
                            return;
                        }
                        count++;
                    }
                }
            }
        }
    }

    public void decreaseSolidLives() {

        for (int x = 0; x < boardRows; x++) {
            for (int y = 0; y < boardCols; y++) {
                if (board[x][y] != null && board[x][y].isSolid())
                    board[x][y].decreaseLiveCount();
            }
        }
    }

}
