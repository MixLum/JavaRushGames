package com.javarush.games.spaceinvaders;

import com.javarush.engine.cell.*;
import com.javarush.games.spaceinvaders.gameobjects.Bullet;
import com.javarush.games.spaceinvaders.gameobjects.EnemyFleet;
import com.javarush.games.spaceinvaders.gameobjects.PlayerShip;
import com.javarush.games.spaceinvaders.gameobjects.Star;

import java.util.ArrayList;
import java.util.List;

public class SpaceInvadersGame extends Game {
    public static final int STATUS_BAR_HEIGHT = 1;
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64 + STATUS_BAR_HEIGHT;
    private List<Star> stars;
    private EnemyFleet enemyFleet;
    public static final int COMPLEXITY = 5;
    private List<Bullet> enemyBullets;
    private List<Bullet> playerBullets;
    private PlayerShip playerShip;
    private boolean isGameStopped;
    private int animationsCount;
    private static final int PLAYER_BULLETS_MAX = 1;
    private int score;

    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        showGrid(false);
        createGame();
    }

    private void showStartScreen() {
        isGameStopped = true;
        showMessageDialog(Color.TRANSPARENT, "Press <SPACE> to start", Color.RED, 50);
    }

    private void createGame() {
        createStars();
        enemyFleet = new EnemyFleet();
        enemyBullets = new ArrayList<Bullet>();
        playerBullets = new ArrayList<Bullet>();
        playerShip = new PlayerShip();
        isGameStopped = false;
        animationsCount = 0;
        drawScene();
        setTurnTimer(40);
        score = 0;
    }

    private void drawScene() {
        drawField();
        playerShip.draw(this);
        for (Bullet enemyBullet : enemyBullets) {
            enemyBullet.draw(this);
        }
        for (Bullet playerBullet : playerBullets) {
            playerBullet.draw(this);
        }
        enemyFleet.draw(this);
    }

    private void drawField() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                setCellValueEx(i, j, Color.BLACK, "");
            }
        }

        for (
                Star star : stars) {
            star.draw(this);
        }

    }

    private void createStars() {
        stars = new ArrayList<Star>();
        for (int i = 0; i < 8; i++) {
            stars.add(new Star(this.getRandomNumber(WIDTH), this.getRandomNumber(HEIGHT - HEIGHT / 4) + 1));
        }
    }

    public void setCellValueEx(int x, int y, Color color, String string) {
        if (x < 0 || x >= WIDTH - 1 || y < 0 || y >= HEIGHT - 1) {
            return;
        }
        if (y >= STATUS_BAR_HEIGHT) {
            super.setCellValueEx(x, y, color, string);
        } else {
            super.setCellValueEx(x, y, Color.DARKMAGENTA, string);

        }
    }

    public void onKeyPress(Key key) {
        if (key == Key.SPACE && isGameStopped) {
            createGame();
        } else if (key == Key.LEFT) {
            playerShip.setDirection(Direction.LEFT);
        } else if (key == Key.RIGHT) {
            playerShip.setDirection(Direction.RIGHT);
        }
        if (key == Key.SPACE) {
            Bullet newBullet = playerShip.fire();
            if (newBullet != null && playerBullets.size() < PLAYER_BULLETS_MAX) {
                playerBullets.add(newBullet);
            }
        }
    }

    public void onKeyReleased(Key key) {
        if (key == Key.LEFT && playerShip.getDirection() == Direction.LEFT) {
            playerShip.setDirection(Direction.UP);
        }
        if (key == Key.RIGHT && playerShip.getDirection() == Direction.RIGHT) {
            playerShip.setDirection(Direction.UP);
        }
    }

    public void onTurn(int step) {
        moveSpaceObjects();
        check();
        Bullet newBullet = enemyFleet.fire(this);
        if (newBullet != null) {
            enemyBullets.add(newBullet);
        }
        setScore(score);
        drawScene();
    }

    private void moveSpaceObjects() {
        enemyFleet.move();
        for (Bullet enemyBullet : enemyBullets) {
            enemyBullet.move();
        }
        for (Bullet playerBullet : playerBullets) {
            playerBullet.move();
        }
        playerShip.move();
    }

    private void removeDeadBullets() {
        List<Bullet> bulletsToDel = new ArrayList<>(enemyBullets);
        for (Bullet bullet : bulletsToDel) {
            if (bullet.y >= HEIGHT - 1 || !bullet.isAlive) {
                enemyBullets.remove(bullet);
            }
        }
        bulletsToDel = new ArrayList<>(playerBullets);
        for (Bullet bullet : bulletsToDel) {
            if (!bullet.isAlive || bullet.y + bullet.height < 0) {
                playerBullets.remove(bullet);
            }
        }
    }

    private void check() {
        playerShip.verifyHit(enemyBullets);
        score += enemyFleet.verifyHit(playerBullets);
        enemyFleet.deleteHiddenShips();
        removeDeadBullets();
        if (!playerShip.isAlive) {
            stopGameWithDelay();
        }
        if (enemyFleet.getBottomBorder() >= playerShip.y) {
            playerShip.kill();
        }
        if (enemyFleet.getShipsCount() == 0) {
            playerShip.win();
            stopGameWithDelay();
        }

    }

    private void stopGame(boolean isWin) {
        isGameStopped = true;
        stopTurnTimer();
        if (isWin) {
            showMessageDialog(Color.TRANSPARENT, "You win!", Color.GREEN, 75);
        } else {
            showMessageDialog(Color.TRANSPARENT, "Game over!", Color.RED, 75);
        }
    }

    private void stopGameWithDelay() {
        animationsCount++;
        if (animationsCount >= 10) {
            stopGame(playerShip.isAlive);
        }
    }
}
