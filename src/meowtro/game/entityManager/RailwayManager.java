package meowtro.game.entityManager;

import java.util.ArrayList;
import java.util.List;

import meowtro.game.Game;
import meowtro.game.obstacle.Obstacle;
import meowtro.metro_system.railway.Line;
import meowtro.metro_system.railway.Railway;
import meowtro.metro_system.station.Station;

public class RailwayManager extends EntityManager{
    private Game game;
    private long maxTimeToLive;
    public RailwayManager(Game game){
        this.game = game;
        this.maxTimeToLive = Long.parseLong(Game.getConfig().get("metro_system.railway.maxTimeToLive"));
    }
    List<Railway> railways = new ArrayList<Railway>(); 
    public void build(Station s1, Station s2, Line line, List<Station> allStations, List<Obstacle> obstacles, int cost){
        if (s1.isAdjTo(s2) || s2.isAdjTo(s1)){
            return;
        }
        if (line.getRailways().size() != 0){
            if ((!line.getStations().contains(s1)) && (!line.getStations().contains(s2))){
                return; 
            }
        }
        if (Game.getBalance() >= cost) {
            Game.setBalance(Game.getBalance()-cost);
            new Railway(s1, s2, line, this.maxTimeToLive, allStations, obstacles, this.game, this.colorMap.get(line.getColor()));
        } else {
            Game.showText("Not Enough Money!!");
        }
    }
}
