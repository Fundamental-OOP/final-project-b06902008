package meowtro.game.onClickEvent;

import meowtro.game.Game;
import meowtro.game.entityManager.EntityManager;
import meowtro.metro_system.train.Locomotive;

public class CarBuilder extends OnClickEvent {
    private EntityManager em;

    public CarBuilder(EntityManager em, Game game, int cost) {
        this.name = "car builder";
        this.game = game;
        this.em = em;
        this.cost = cost;
    }
    public void conduct(Locomotive locomotive) {
        em.build(locomotive, this.cost);
        this.game.setNowEvent(new WaitForClick(this.game));
    }

}
