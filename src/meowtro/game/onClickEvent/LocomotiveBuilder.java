package meowtro.game.onClickEvent;

import meowtro.Position;
import meowtro.game.Game;
import meowtro.game.entityManager.EntityManager;
import meowtro.metro_system.railway.Railway;

public class LocomotiveBuilder extends OnClickEvent {
    private EntityManager em;

    public LocomotiveBuilder(EntityManager em, Game game, int cost) {
        this.name = "locomotive builder";
        this.game = game;
        this.em = em;
        this.cost = cost;
    }
    public void conduct(Railway railway, Position position) {
        em.build(railway, position, this.cost);
        this.game.setNowEvent(new WaitForClick(this.game));
    }

}
