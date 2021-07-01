package meowtro.game;
import meowtro.eventSystem.*;
import meowtro.eventSystem.disasterEvent.EarthQuakeEvent;
import meowtro.eventSystem.disasterEvent.FireEvent;
import meowtro.eventSystem.holidayEvent.RushHourEvent;
import meowtro.eventSystem.holidayEvent.NewYearEvent;
import java.util.List;
import java.util.ArrayList;
import meowtro.game.gameMode.*;
public class GameFactory {
    private List<Event> creatEvents(Config config, City city){
        String eventsSetting = config.get("eventSystem.allEvents");
        assert eventsSetting != null; 
        String[] eventsStr = eventsSetting.split("\\r?\\n");
        List<Event> allEvents = new ArrayList<>();
        for(int i=0; i<eventsStr.length; i++){
            if(eventsStr[i].strip().length()==0){
                continue;
            }
            String[] eventInfo = eventsStr[i].strip().split("\\$");
            System.out.println(eventInfo[0]);
            if(eventInfo[0].equals("FireEvent")){
                allEvents.add(new FireEvent(city, eventInfo[1], Double.parseDouble(eventInfo[2])));
            }
            else if(eventInfo[0].equals("EarthQuakeEvent")){
                allEvents.add(new EarthQuakeEvent(city, eventInfo[1], Double.parseDouble(eventInfo[2])));
            }
            else if(eventInfo[0].equals("RushHourEvent")){
                allEvents.add(new RushHourEvent(city, eventInfo[1], Double.parseDouble(eventInfo[2])));
            }
            else if(eventInfo[0].equals("NewYearEvent")){
                allEvents.add(new NewYearEvent(city, eventInfo[1], Double.parseDouble(eventInfo[2])));
            }
            else{
                System.out.println(eventInfo[0]+"Wrong event config:\n"+eventsStr[i]);
            }
        }
        return allEvents;

    }
    private  GameTerminateChecker createTerminater(Config config){
        GameTerminateChecker gameTerminatChecker = null;
        String modeStr = config.get("game.mode");
        assert modeStr != null;
        String[] modeInfo = modeStr.split("\\$");
        if(modeInfo[0].equals("SpeedRunMode")){
            gameTerminatChecker = new SpeedRunMode(modeInfo[1]);
        }
        else if (modeInfo[0].equals("MaxProfitMode")){
            gameTerminatChecker = new MaxProfitMode(Integer.parseInt(modeInfo[1]));
        }
        else{
            System.out.println("Wrong Game Mode");
        }
        System.out.println("GameMode:" + modeInfo[0]);
        return gameTerminatChecker;
    }
    public Game createGame(Config config) {
        GameTerminateChecker gameTerminatChecker = createTerminater(config);
        Game game = new Game(config, gameTerminatChecker);
        City city = new City();
        game.setCity(city);
        SatisfactionSettle ss = new SatisfactionSettle(city,
            Double.parseDouble(config.get("satisfaction.money.weight")),
            Double.parseDouble(config.get("satisfaction.spawn.weight")),
            Integer.parseInt(config.get("satisfaction.standard.value")),
            Integer.parseInt(config.get("satisfaction.standard.bonus")));
        
        if (config.get("eventSystem.allEvents") != null){
            EventTrigger eventTrigger = new EventTrigger(creatEvents(config, city), ss);
            game.setEventTrigger(eventTrigger);
        }
        return game;
    }

    /******* MAIN *******/
    public static void main(String[] args) {
        
    }

}
