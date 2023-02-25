package com.javarush.games.spaceinvaders.gameobjects;

import com.javarush.engine.cell.Game;
import com.javarush.games.spaceinvaders.Direction;
import com.javarush.games.spaceinvaders.ShapeMatrix;
import com.javarush.games.spaceinvaders.SpaceInvadersGame;

import java.util.ArrayList;
import java.util.List;

public class EnemyFleet {
    private static final int ROWS_COUNT = 3;
    private static final int COLUMNS_COUNT = 10;
    private static final int STEP = ShapeMatrix.ENEMY.length + 1;
    private List<EnemyShip> ships;
    private Direction direction = Direction.RIGHT;

    private void createShips() {
        ships = new ArrayList<EnemyShip>();
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                ships.add(new EnemyShip(i * STEP, j * STEP + 12));
            }
        }
        ships.add(new Boss(STEP * COLUMNS_COUNT / 2 - ShapeMatrix.BOSS_ANIMATION_FIRST.length / 2 - 1, 5));
    }

    private double getSpeed() {
        double speed = 3.0 / ships.size();
        if (speed < 2.0) {
            return speed;
        }else return  2.0;
    }

    private double getLeftBorder() {
        double leftBorder = SpaceInvadersGame.WIDTH;
        for (int i = 0; i < ships.size(); i++) {
            double currentX = ships.get(i).x;
            if (currentX < leftBorder) {
                leftBorder = currentX;
            }
        }
        return leftBorder;
    }

    private double getRightBorder() {
        double rightBorder = 0;
        for (int i = 0; i < ships.size(); i++) {
            double currentRightX = ships.get(i).x + ships.get(i).width;
            if (currentRightX > rightBorder) {
                rightBorder = currentRightX;
            }
        }
        return rightBorder;
    }

    public double getBottomBorder() {
        double bottomBorder = 0;
        for (EnemyShip ship : ships) {
            double shipBottom = ship.y + ship.height;
            if (shipBottom > bottomBorder) {
                bottomBorder = shipBottom;
            }
        }
        return bottomBorder;
    }

    public int getShipsCount() {
        return ships.size();
    }

    public EnemyFleet() {
        createShips();
    }

    public void draw(Game game) {
        for (EnemyShip ship : ships) {
            ship.draw(game);
        }
    }

    public void move() {
        if (ships.isEmpty()) {
            return;
        }
        boolean isDirectionChanged = false;
        if (direction == Direction.LEFT && getLeftBorder() < 0) {
            direction = Direction.RIGHT;
            isDirectionChanged = true;
        }
        if (direction == Direction.RIGHT && getRightBorder() > SpaceInvadersGame.WIDTH) {
            direction = Direction.LEFT;
            isDirectionChanged = true;
        }
        if (isDirectionChanged) {
            for (EnemyShip ship : ships) {
                ship.move(Direction.DOWN, getSpeed());
            }
        } else {
            for (EnemyShip ship : ships) {
                ship.move(direction, getSpeed());
            }
        }
    }

    public Bullet fire(Game game) {
        if (ships.isEmpty()) {
            return null;
        }
        int fireProbability = game.getRandomNumber(100 / SpaceInvadersGame.COMPLEXITY);
        if (fireProbability > 0) {
            return null;
        }
        int shipToFire = game.getRandomNumber(ships.size());
        return ships.get(shipToFire).fire();
    }

    public int verifyHit(List<Bullet> bullets) {
        int score = 0;
        if (bullets.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < bullets.size(); i++) {
            for (int j = 0; j < ships.size(); j++) {
                Bullet currentBullet = bullets.get(i);
                EnemyShip currentEnemyShip = ships.get(j);
                if (currentBullet.isAlive && currentEnemyShip.isAlive && currentBullet.isCollision(currentEnemyShip)) {
                    currentBullet.kill();
                    currentEnemyShip.kill();
                    score += currentEnemyShip.score;
                }
            }
        }
        return score;
    }

    public void deleteHiddenShips() {
        List<EnemyShip> shipsToDel = new ArrayList<>(ships);
        for (EnemyShip enemyShip : shipsToDel) {
            if (!enemyShip.isVisible()) {
                ships.remove(enemyShip);
            }
        }
    }
}
