package com.javarush.games.snake;

import com.javarush.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

import static com.javarush.games.snake.SnakeGame.HEIGHT;
import static com.javarush.games.snake.SnakeGame.WIDTH;

public class Snake {
    private List<GameObject> snakeParts = new ArrayList<>();
    private static final String HEAD_SIGN = "\uD83D\uDC7E";
    private static final String BODY_SIGN = "\u26AB";
    public boolean isAlive = true;
    private Direction direction = Direction.LEFT;

    public Snake(int x, int y) {
        snakeParts.add(new GameObject(x, y));
        snakeParts.add(new GameObject(x + 1, y));
        snakeParts.add(new GameObject(x + 2, y));
    }

    public boolean checkCollision(GameObject gameObject) {
        boolean isCollision = false;
        for (GameObject snakePart : snakeParts) {
            if (snakePart.x == gameObject.x && snakePart.y == gameObject.y) {
                isCollision = true;
                break;
            }
        }
    return isCollision;
    }


    public void move(Apple apple) {
        GameObject newHead = createNewHead();
        if (newHead.x == apple.x && newHead.y == apple.y) {
            apple.isAlive = false;
            snakeParts.add(0, newHead);
            return;
        }
        if (newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT) {
            isAlive = false;
            return;
        }
        if (checkCollision(newHead)) {
            isAlive = false;
            return;
        }
        snakeParts.add(0, newHead);
        removeTail();

    }

    public int getLength() {
        return snakeParts.size();
    }

    public GameObject createNewHead() {
        int deltaX = 0, deltaY = 0;
        switch (direction) {
            case LEFT:
                deltaX = -1;
                break;
            case RIGHT:
                deltaX = 1;
                break;
            case UP:
                deltaY = -1;
                break;
            case DOWN:
                deltaY = 1;
                break;
        }
        GameObject newHead = new GameObject(snakeParts.get(0).x + deltaX, snakeParts.get(0).y + deltaY);
        return newHead;
    }

    public void removeTail() {
        snakeParts.remove(snakeParts.size() - 1);
    }

    public void setDirection(Direction direction) {
        switch (this.direction) {
            case LEFT:
            case RIGHT:
                if (snakeParts.get(0).x == snakeParts.get(1).x) {
                    return;
                }
                break;
            case UP:
            case DOWN:
                if (snakeParts.get(0).y == snakeParts.get(1).y) {
                    return;
                }
                break;
        }
        this.direction = direction;
    }

    public void draw(Game game) {
        Color color;
        if (isAlive) {
            color = Color.BLACK;
        } else {
            color = Color.RED;
        }
        game.setCellValueEx(snakeParts.get(0).x, snakeParts.get(0).y, Color.NONE, HEAD_SIGN, color, 75);
        for (int i = 1; i < snakeParts.size(); i++) {
            game.setCellValueEx(snakeParts.get(i).x, snakeParts.get(i).y, Color.NONE, BODY_SIGN, color, 75);
        }
    }
}
