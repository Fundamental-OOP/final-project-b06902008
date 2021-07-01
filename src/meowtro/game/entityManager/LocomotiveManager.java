package meowtro.game.entityManager;

import meowtro.Position;
import meowtro.game.Game;
import meowtro.metro_system.Direction;
import meowtro.metro_system.railway.Railway;
import meowtro.metro_system.train.Locomotive;

public class LocomotiveManager extends EntityManager {
    private Game game;

    public LocomotiveManager(Game game) {
        this.game = game;
    }

    public void build(Railway railway, Position position, int cost) {
        if (Game.getBalance() >= cost) {
            Game.setBalance(Game.getBalance()-cost);
            System.out.println(position);
            Locomotive locomotive = new Locomotive(railway, position, Direction.FORWARD, this.colorMap.get(railway.getLine().getColor()), this.game);
            locomotive.setManager(this);
            railway.getLine().addLocomotive(locomotive);
        } else {
            System.out.println("Not Enough Money!");
            // Game.showText("Not Enough Money!");
        }
    }

    public void upgrade(Locomotive locomotive) {
        if (Game.getBalance() >= locomotive.getUpgradeCost()) {
            if (locomotive.getLevel() < locomotive.getMaxLevel()) {
                Game.setBalance(Game.getBalance()-locomotive.getUpgradeCost());
                locomotive.setLevel(locomotive.getLevel()+1);
            } else {
                // Game.showText("Already Max Level!!");
            }

        } else {
            // Game.showText("Not Enough Money!");
        }
    }

    public void destroy(Locomotive locomotive) {
        locomotive.destroy();
    }
}
