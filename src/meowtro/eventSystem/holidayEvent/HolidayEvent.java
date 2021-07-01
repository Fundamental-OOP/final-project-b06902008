package meowtro.eventSystem.holidayEvent;
import meowtro.eventSystem.Event;
import meowtro.game.City;

public abstract class HolidayEvent extends Event{
    protected double growthRate;
    public HolidayEvent(City city, String happenedTimeString, double growthRate){
        super(city, happenedTimeString);
        this.growthRate = growthRate;
    }
}
