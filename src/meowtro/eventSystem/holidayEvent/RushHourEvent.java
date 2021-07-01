package meowtro.eventSystem.holidayEvent;
import meowtro.game.*;
import java.util.*;

public class RushHourEvent extends HolidayEvent {
    public RushHourEvent(City city, String happenedTimeString, double growthRate){
        super(city, happenedTimeString, growthRate);
        this.name = "RushHourEvent";
    }
    public void trigger(){
        List<Region> allRegions = this.city.getRegions();
        int rushIdx = Game.randomGenerator.nextInt(allRegions.size());
        double curRate = allRegions.get(rushIdx).getSpawnRate();
        allRegions.get(rushIdx).setSpawnRate(curRate*this.growthRate);
    }
}
