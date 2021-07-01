package meowtro.eventSystem;
import meowtro.game.City;
public abstract class Event {
    protected City city;
    protected String name;
    protected String happenedTimeString;  //"MM-DD HH:MM:SS"

    public Event(City city, String happenedTimeString){
        this.city = city;
        this.happenedTimeString = happenedTimeString;
    }
    public abstract void trigger();
    public String getHappenedTime(){
        return this.happenedTimeString;
    }
    public String getEventName(){
        return this.name;
    }
}
