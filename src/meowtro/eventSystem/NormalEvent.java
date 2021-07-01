package meowtro.eventSystem;
// import meowtro.timeSystem.TimeLine;
import meowtro.game.City;

public class NormalEvent extends Event{
    public NormalEvent(City city, String happenedTimeString){
        super(city, happenedTimeString);
        this.name = "NormalEvent";
    }
    public void trigger(){
        // TimeLine curreTimeLine = TimeLine.getInstance();

    }
}
