package com.javarush.games.game2048;

import com.javarush.engine.cell.*;

public class Game2048 extends Game {

    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score;

    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLUE, "You win!", Color.BLACK, 75);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED, "Game over!", Color.BLACK, 75);
    }

    private int getMaxTileValue() {
        int maxValue = 0;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] > maxValue) {
                    maxValue = gameField[i][j];
                }
            }
        }
        return maxValue;
    }

    private boolean canUserMove() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == 0) {
                    return true;
                }
            }
        }
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if ((i + 1 < SIDE && gameField[i][j] == gameField[i + 1][j]) || (i - 1 > 0 && gameField[i][j] == gameField[i - 1][j]) ||
                        (j - 1 > 0 && gameField[i][j] == gameField[i][j - 1]) || (j + 1 < SIDE && gameField[i][j] == gameField[i][j + 1])) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onKeyPress(Key key) {
        if (isGameStopped && key != Key.SPACE) {
            return;
        }

        if (isGameStopped && key == Key.SPACE) {
            isGameStopped = false;
            createGame();
            drawScene();
        }

        if (!canUserMove()) {
            gameOver();
            return;
        }
        boolean isKeyPressed = false;
        if (key == Key.LEFT) {
            moveLeft();
            isKeyPressed = true;
        } else if (key == Key.RIGHT) {
            moveRight();
            isKeyPressed = true;
        } else if (key == Key.UP) {
            moveUp();
            isKeyPressed = true;
        } else if (key == Key.DOWN) {
            moveDown();
            isKeyPressed = true;
        }
        if (isKeyPressed) {
            drawScene();
        }
    }

    private void moveLeft() {
        boolean isCompressed = false;
        boolean isRowCompressed;
        boolean isMerged = false;
        boolean isRowMerged;
        for (int i = 0; i < SIDE; i++) {
            isRowCompressed = compressRow(gameField[i]);
            isRowMerged = mergeRow(gameField[i]);
            if (isRowMerged) {
                isMerged = true;
                isCompressed = compressRow(gameField[i]);
            }
            if (isRowCompressed) {
                isCompressed = true;
            }
        }
        if (isCompressed || isMerged) {
            createNewNumber();
        }

    }

    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private void rotateClockwise() {
        int[][] tempArray = new int[SIDE][SIDE];
        for (int i = 0; i < SIDE / 2; i++) {
            for (int j = i; j < SIDE - i - 1; j++) {
                int tmp = gameField[i][j];
                tempArray[i][j] = gameField[SIDE - j - 1][i];
                tempArray[SIDE - j - 1][i] = gameField[SIDE - i - 1][SIDE - j - 1];
                tempArray[SIDE - i - 1][SIDE - j - 1] = gameField[j][SIDE - i - 1];
                tempArray[j][SIDE - i - 1] = tmp;
            }
        }
        gameField = tempArray;
    }

    private boolean compressRow(int[] row) {
        boolean isCompressed = false;
        for (int i = 0; i < row.length - 1; i++) {
            for (int j = i + 1; j < row.length; j++) {
                if (row[i] == 0 && row[j] != 0) {
                    row[i] = row[j];
                    row[j] = 0;
                    isCompressed = true;
                }
            }
        }
        return isCompressed;
    }

    private boolean mergeRow(int[] row) {
        boolean isMerged = false;
        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] != 0 && row[i + 1] == row[i]) {
                row[i] *= 2;
                score += row[i];
                setScore(score);
                row[i + 1] = 0;
                isMerged = true;
            }
        }
        return isMerged;
    }

    private void setCellColoredNumber(int x, int y, int value) {
        setCellValueEx(x, y, getColorByValue(value), String.valueOf(value == 0 ? "" : value));
    }

    private Color getColorByValue(int value) {
        Color color = Color.NONE;
        switch (value) {
            case 0:
                color = Color.BEIGE;
                break;
            case 2:
                color = Color.YELLOW;
                break;
            case 4:
                color = Color.CORAL;
                break;
            case 8:
                color = Color.DARKOLIVEGREEN;
                break;
            case 16:
                color = Color.THISTLE;
                break;
            case 32:
                color = Color.DARKMAGENTA;
                break;
            case 64:
                color = Color.PALETURQUOISE;
                break;
            case 128:
                color = Color.DARKORCHID;
                break;
            case 256:
                color = Color.THISTLE;
                break;
            case 512:
                color = Color.CHOCOLATE;
                break;
            case 1024:
                color = Color.MOCCASIN;
                break;
            case 2048:
                color = Color.ORANGE;
                break;
        }
        return color;
    }

    private void createNewNumber() {
        int x = getRandomNumber(SIDE);
        int y = getRandomNumber(SIDE);
        int value = getRandomNumber(10) >= 9 ? 4 : 2;
        if (gameField[y][x] != 0) {
            createNewNumber();
        } else gameField[y][x] = value;
        if (getMaxTileValue() == 2048) {
            win();
        }
    }

    private void createGame() {
        gameField = new int[SIDE][SIDE];
        score = 0;
        setScore(score);
        createNewNumber();
        createNewNumber();
    }

    private void drawScene() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellColoredNumber(i, j, gameField[j][i]);
            }
        }
    }
}

