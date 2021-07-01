package meowtro.eventSystem.disasterEvent;
import meowtro.game.City;
import java.util.List;
import java.util.ArrayList;
import meowtro.metro_system.railway.*;
import meowtro.game.*;
public class FireEvent extends DisasterEvent {
    public FireEvent(City city, String happenedTimeString, Double remainPortion){
        super(city, happenedTimeString, remainPortion);
        this.name = "FireEvent";
    }
    public void trigger(){
        List<Line> allLines = this.city.getAllLines();
        List<Line> allNotEmptyLines = new ArrayList<>();
        for(Line l : allLines){
            if(l.getRailways().size()>0){
                allNotEmptyLines.add(l);
            }
        }
        int fireLineIdx = Game.randomGenerator.nextInt(allNotEmptyLines.size());
        Line fireLine = allNotEmptyLines.get(fireLineIdx);
        int fireRailwayIdx = Game.randomGenerator.nextInt(fireLine.getRailways().size());
        long curRemainTimeToLive = fireLine.getRailways().get(fireRailwayIdx).getRemainLive();
        fireLine.getRailways().get(fireRailwayIdx).setRemainTimeToLive((long)(this.remainPortion*curRemainTimeToLive));
    }
    
}
