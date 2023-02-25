package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

import java.util.ArrayList;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    public void initialize() {
        setScreenSize(SIDE, SIDE);
        countMinesOnField = 0;
        createGame();
    }

    private void createGame() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {

                boolean isMine = getRandomNumber(10) >= 9 ? true : false;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[j][i] = new GameObject(i, j, isMine);
                setCellColor(i, j, Color.ORCHID);
                setCellValue(i, j, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BISQUE, "Game over! Your score - " + score, Color.BLACK, 25);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BISQUE, "You win! Your score - " + score, Color.BLACK, 32);

    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        score = 0;
        setScore(score);
        createGame();
    }

    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
        } else {
            openTile(x, y);
        }
    }

    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void markTile(int x, int y) {
        if (gameField[y][x].isOpen || (countFlags == 0 && !gameField[y][x].isFlag)) {
            return;
        }
        if (isGameStopped) {
            return;
        }
        if (!gameField[y][x].isFlag) {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        } else {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.ORCHID);
        }

    }

    private void openTile(int x, int y) {
        if (x < 0 || x >= SIDE || y < 0 || y >= SIDE) {
            return;
        }
        if (gameField[y][x].isOpen || gameField[y][x].isFlag || isGameStopped) {
            return;
        }
        gameField[y][x].isOpen = true;
        if (!gameField[y][x].isMine && gameField[y][x].isOpen) {
            score+=5;
            setScore(score);
        }
        countClosedTiles--;
        if (countClosedTiles == countMinesOnField && !gameField[y][x].isMine) {
            win();
        }
        if (gameField[y][x].isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }
        setCellColor(x, y, Color.BEIGE);
        if (!gameField[y][x].isMine && gameField[y][x].countMineNeighbors == 0) {
            ArrayList<GameObject> neigbours = getNeighbors(gameField[y][x]);
            for (GameObject neigbour : neigbours) {
                if (!neigbour.isOpen) {
                    openTile(neigbour.x, neigbour.y);
                }
            }
            setCellValue(x, y, "");
        }
        if (gameField[y][x].isMine) {
            setCellValue(x, y, MINE);
        }
        if (!gameField[y][x].isMine && gameField[y][x].countMineNeighbors != 0) {
            setCellNumber(x, y, gameField[y][x].countMineNeighbors);
        }
    }

    private void countMineNeighbors() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (!gameField[j][i].isMine) {
                    ArrayList<GameObject> neigbours = getNeighbors(gameField[j][i]);
                    for (GameObject neigbour : neigbours) {
                        if (neigbour.isMine) {
                            gameField[j][i].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private ArrayList<GameObject> getNeighbors(GameObject gameObject) {
        ArrayList<GameObject> neighbors = new ArrayList<>();
        int x1 = (gameObject.x - 1 < 0) ? 0 : gameObject.x - 1;
        int x2 = (gameObject.x + 1 < SIDE - 1) ? gameObject.x + 1 : SIDE - 1;
        int y1 = (gameObject.y - 1 < 0) ? 0 : gameObject.y - 1;
        int y2 = (gameObject.y + 1 < SIDE - 1) ? gameObject.y + 1 : SIDE - 1;
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                boolean isCenter = (i == gameObject.x && j == gameObject.y);
                if (!isCenter) {
                    neighbors.add(gameField[j][i]);
                }
            }
        }
        return neighbors;
    }
}
