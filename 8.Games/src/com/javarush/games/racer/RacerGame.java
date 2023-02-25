package com.javarush.games.racer;

import com.javarush.engine.cell.*;
import com.javarush.games.racer.road.RoadManager;

public class RacerGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH / 2;
    public static final int ROADSIDE_WIDTH = 14;
    private static final int RACE_GOAL_CARS_COUNT = 40;
    private RoadMarking roadMarking;
    private PlayerCar player;
    private RoadManager roadManager;
    private boolean isGameStopped;
    private FinishLine finishLine;
    private ProgressBar progressBar;
    private int score;

    public void initialize() {
        showGrid(false);
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame() {
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        drawScene();
        setTurnTimer(40);
        isGameStopped = false;
        score = 3500;
    }

    private void drawScene() {
        drawField();
        progressBar.draw(this);
        roadMarking.draw(this);
        finishLine.draw(this);
        player.draw(this);
        roadManager.draw(this);
    }

    private void drawField() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (i == CENTER_X) {
                    setCellColor(CENTER_X, j, Color.WHITE);
                } else if (i >= ROADSIDE_WIDTH && i < (WIDTH - ROADSIDE_WIDTH)) {
                    setCellColor(i, j, Color.GRAY);
                } else {
                    setCellColor(i, j, Color.GREEN);
                }
            }
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.TRANSPARENT, "Game over!", Color.CORAL, 50);
        stopTurnTimer();
        player.stop();
    }

    public void setCellColor(int x, int y, Color color) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            super.setCellColor(x, y, color);
        }
    }

    private void moveAll() {
        roadMarking.move(player.speed);
        player.move();
        roadManager.move(player.speed);
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());
    }

    public void onTurn(int step) {
        score -=5;
        setScore(score);
        if (roadManager.isPlayerCrushed(player)) {
            gameOver();
            drawScene();
            return;
        }
        if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT) {
            finishLine.show();
        }
        if (finishLine.isCrossed(player)) {
            win();
            drawScene();
            return;
        }
        moveAll();
        roadManager.generateNewRoadObjects(this);
        drawScene();
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.TRANSPARENT, "You win! Your score - " + score, Color.RED, 30);

        stopTurnTimer();

    }

    public void onKeyPress(Key key) {
        if (key == Key.RIGHT) {
            player.setDirection(Direction.RIGHT);
        }
        if (key == Key.LEFT) {
            player.setDirection(Direction.LEFT);
        }
        if (key == Key.SPACE && isGameStopped) {
            createGame();
        }
        if (key == Key.UP) {
            player.speed = 2;
        }
    }

    public void onKeyReleased(Key key) {
        if (key == Key.RIGHT && player.getDirection() == Direction.RIGHT) {
            player.setDirection(Direction.NONE);
        }
        if (key == Key.LEFT && player.getDirection() == Direction.LEFT) {
            player.setDirection(Direction.NONE);
        }
        if (key == Key.UP) {
            player.speed = 1;
        }
    }
}
