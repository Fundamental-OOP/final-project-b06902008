package meowtro.eventSystem.disasterEvent;
import meowtro.eventSystem.Event;
import meowtro.game.City;

public abstract class DisasterEvent extends Event{
    // protected int numOfDestroyRailway;
    protected double remainPortion;
    public DisasterEvent(City city, String happenedTimeString, double remainPortion){
        super(city, happenedTimeString);
        this.remainPortion = remainPortion;
        // this.numOfDestroyRailway = numOfDestroyRailway;
    }
    
}
