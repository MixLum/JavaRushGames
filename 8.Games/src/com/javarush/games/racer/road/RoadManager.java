package com.javarush.games.racer.road;

import com.javarush.engine.cell.Game;
import com.javarush.games.racer.PlayerCar;
import com.javarush.games.racer.RacerGame;

import java.util.ArrayList;
import java.util.List;

public class RoadManager {
    public static final int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;
    public static final int RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private static final int FIRST_LANE_POSITION = 16;
    private static final int FOURTH_LANE_POSITION = 44;
    private List<RoadObject> items = new ArrayList<>();
    private static final int PLAYER_CAR_DISTANCE = 12;
    private int passedCarsCount = 0;

    public int getPassedCarsCount() {
        return passedCarsCount;
    }

    public void generateNewRoadObjects(Game game) {
        generateThorn(game);
        generateRegularCar(game);
        generateMovingCar(game);
    }

    private RoadObject createRoadObject(RoadObjectType type, int x, int y) {
        if (type == RoadObjectType.THORN) {
            return new Thorn(x, y);
        } else if (type == RoadObjectType.DRUNK_CAR) {
            return new MovingCar(x, y);
        } else {
            return new Car(type, x, y);
        }
    }

    private void addRoadObject(RoadObjectType type, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        int y = -1 * RoadObject.getHeight(type);
        RoadObject newItem = createRoadObject(type, x, y);
        if (newItem != null && isRoadSpaceFree(newItem)) {
            items.add(newItem);
        }
    }

    public void draw(Game game) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).draw(game);
        }
    }

    public void move(int boost) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).move(boost + items.get(i).speed, items);
        }
        deletePassedItems();
    }

    public boolean isPlayerCrushed(PlayerCar playerCar) {
        for (RoadObject item : items) {
            if (item.isCollision(playerCar)) {
                return true;
            }
        }
        return false;
    }

    private void generateThorn(Game game) {
        int n = game.getRandomNumber(100);
        if (n < 10 && !isThornExists()) {
            addRoadObject(RoadObjectType.THORN, game);
        }
    }

    private void generateRegularCar(Game game) {
        int n = game.getRandomNumber(100);
        int carTypeNumber = game.getRandomNumber(4);
        if (n < 30) {
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
        }

    }

    private void generateMovingCar(Game game) {
        int n = game.getRandomNumber(100);
        if (n < 10 && isMovingCarExists() == false) {
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
        }
    }

    private boolean isThornExists() {
        boolean isThornExist = false;
        for (RoadObject item : items) {
            if (item.type == RoadObjectType.THORN) {
                isThornExist = true;
            }
        }
        return isThornExist;
    }

    private boolean isMovingCarExists() {
        boolean isMovingCarExist = false;
        for (RoadObject item : items) {
            if (item.type == RoadObjectType.DRUNK_CAR) {
                isMovingCarExist = true;
            }
        }
        return isMovingCarExist;
    }

    private boolean isRoadSpaceFree(RoadObject object) {
        boolean isFreeSpace = true;
        for (RoadObject item : items) {
            if (item.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE)) {
                isFreeSpace = false;
            }
        }
        return isFreeSpace;
    }

    private void deletePassedItems() {
        List<RoadObject> itemsToDel = new ArrayList<>(items);
        for (RoadObject roadObject : itemsToDel) {
            if (roadObject.y >= RacerGame.HEIGHT) {
                if (roadObject.type != RoadObjectType.THORN) {
                    passedCarsCount++;
                }
                items.remove(roadObject);
            }
        }
    }
}
