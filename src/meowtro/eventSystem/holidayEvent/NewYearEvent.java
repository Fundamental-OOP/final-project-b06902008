package meowtro.eventSystem.holidayEvent;
import meowtro.game.*;
import java.util.*;

public class NewYearEvent extends HolidayEvent{
    public NewYearEvent(City city, String happenedTimeString, double growthRate){
        super(city, happenedTimeString, growthRate);
        this.name = "NewYearEvent";
    }
    public void trigger(){
        List<Region> allRegions = this.city.getRegions();
        for(Region r: allRegions){
            double curRate = r.getSpawnRate();
            r.setSpawnRate(curRate*this.growthRate);
        }       
    }
}
