package meowtro.game.onClickEvent;

import meowtro.game.City;
import meowtro.game.Game;
import meowtro.game.entityManager.EntityManager;
import meowtro.metro_system.railway.Line;
import meowtro.metro_system.station.Station;

public class RailwayBuilder extends OnClickEvent {
    private EntityManager em;
    private City city;
    private Line line;
    public RailwayBuilder(EntityManager em, Game game, Line line, int cost) {
        this.name = "railway builder";
        this.game = game;
        this.city = this.game.getCity();
        this.em = em;
        this.line = line;
        this.cost = cost;
    }
    public void conduct(Station s1, Station s2) {
        em.build(s1, s2, this.line, this.city.getAllStation(), this.city.getObstacles(), this.cost);
        this.game.setNowEvent(new WaitForClick(this.game));
    }

}
