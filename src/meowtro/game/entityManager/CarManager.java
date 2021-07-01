package meowtro.game.entityManager;

import meowtro.game.Game;
import meowtro.metro_system.train.Car;
import meowtro.metro_system.train.Locomotive;

public class CarManager extends EntityManager {
    private Game game;

    public CarManager(Game game) {
        this.game = game;
    }

    public void build(Locomotive locomotive, int cost) {
        if (Game.getBalance() >= cost) {
            Game.setBalance(Game.getBalance()-cost);
            new Car(locomotive);
        } else {
            // Game.showText("Not Enough Money!");
        }
    }

    public void destroy(Car car) {
        car.destroy();
    }
}
