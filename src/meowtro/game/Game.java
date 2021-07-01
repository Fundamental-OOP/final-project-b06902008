package meowtro.game;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javafx.scene.text.Text;
import meowtro.metro_system.railway.Railway;
import meowtro.metro_system.station.Station;
import meowtro.metro_system.train.Locomotive;
import meowtro.game.entityManager.StationManager;
import meowtro.game.onClickEvent.OnClickEvent;
import meowtro.game.onClickEvent.WaitForClick;
import meowtro.timeSystem.TimeLine;
import meowtro.Position;
import meowtro.eventSystem.*;
import meowtro.game.gameMode.*;
public class Game {
    
    private static Config config = null;
    private City city = null;
    public City getCity() {
        return this.city;
    }
    // private Stack<OnClickEvent> onClickEventStack = new Stack<OnClickEvent>();
    private EventTrigger eventTrigger = null;
    private GameTerminateChecker gameTerminatChecker = null;
    // private History history = null;
    // private ReplayVideoPage replayVideoPage = null;
    // private ExitPage exitPage = null;
    private double globalSatisfaction = 0;
    private static int balance = 0;
    public static Random randomGenerator = new Random();
    public static boolean DEBUG = true;
    public static String DEBUG_hash = "loco";
    private boolean gameIsEnded = false;

    private int maxStationNum;
    public double getGlobalSatisfaction(){
        return this.globalSatisfaction;
    }
    public int getMaxStationNum() {
        return this.maxStationNum;
    }
    public boolean getGameIsEnded(){
        return this.gameIsEnded;
    }
    private ArrayList<String> iconPaths;
    public ArrayList<String> getIconPaths() {
        return this.iconPaths;
    }
    // public void setTerminateChecker(GameTerminateChecker gameTerminatChecker ){
    //     this.gameTerminatChecker = gameTerminatChecker;
    // }
    public static ArrayList<String> listFiles(String dir) {
        File file = null;
        ArrayList<String> files = new ArrayList<>();
        try {
            file = new File(dir);
            for (String s : file.list()) {
                files.add(dir+"/"+s);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
        return files;
    }

    public Game(Config config, GameTerminateChecker gameTerminatChecker) {
        Game.config = config;
        Game.setBalance(Integer.parseInt(config.get("balance.default")));
        Game.randomGenerator.setSeed(Long.parseLong(config.get("game.random.seed")));
        this.startTimeLine();
        this.gameTerminatChecker = gameTerminatChecker;
        this.setNowEvent(new WaitForClick(this));
    }

    public static int getBalance() {
        return Game.balance;
    }
    public static void setBalance(int newBalance) {
        Game.balance = newBalance;
    }
    public void setEventTrigger(EventTrigger eventTrigger){
        this.eventTrigger = eventTrigger;
    }


    public void setCity(City city) {
        this.city = city;
        this.iconPaths = listFiles("./image/icon/");
        this.maxStationNum = this.iconPaths.size();
        this.city.setGame(this);
    }

    public static Config getConfig() {
        return Game.config;
    }

    public static void debug() {
        Game.DEBUG = true;
    }

    public void startTimeLine() {
        TimeLine.getInstance().reset();
    }

    public void saveRecord() {
        // TODO: save record
    }

    public void generatePopupMessage(String Message) {
        // TODO: generate popup message
    }

    public void update() {

        TimeLine.getInstance().update();

        if (Game.DEBUG) {
            System.out.println("------------------------------------------------");
            System.out.println(TimeLine.getInstance().toString());
        }

        // on click events
        // while (!this.onClickEventStack.empty()) {
        //     // TODO: onclick event
        // }

        // TODO: check event
        if (eventTrigger != null)
            this.eventTrigger.trigger();

        this.city.update();
        this.globalSatisfaction = this.city.getGlobalSatisfaction();
        this.gameIsEnded = this.gameTerminatChecker.gameIsEnded();
        // if(gameIsEnded){
        //     System.out.println("\n\n\nGame Is Ended\n\n\n");
        // }

    }

    public void start(StationManager stationManager) {
        // construct station at two randomly selected region
        List<Region> regions2AddStation = this.city.getNRandomRegions(2);
        for (Region region: regions2AddStation) {
            Position newStationPosition = region.getRandomPositionInRegion();
            stationManager.build(this.city, newStationPosition,0);
        }

        // // run game
        // while (true) {
        //     this.update();
        // }
    }

    public static void setToyConfig(){
        config = new Config("../resources/defaultConfig.properties", "../resources/defaultConfig.properties"); 
    }

    private OnClickEvent nowEvent;
    public OnClickEvent getNowEvent() {
        return this.nowEvent;
    }
    public void setNowEvent(OnClickEvent event) {
        this.nowEvent = event;
    }

    public void onClick(Position position) {
        if (!this.nowEvent.getName().equals("railway builder")) {
            tmpStation = null;
        }
        this.nowEvent.conduct(position);
    }

    private List<Object> objectToBeRemoved = new ArrayList<>();
    public List<Object> getObjectToBeRemoved() {
        return this.objectToBeRemoved;
    }
    public void deleteObject(Object o) {
        this.objectToBeRemoved.add(o);
    }
    public void resetObjectToBeRemoved() {
        this.objectToBeRemoved.clear();
    }

    private Station tmpStation = null;
    public void stationOnClick(Station station) {
        if (this.nowEvent.getName().equals("railway builder")) {
            if (tmpStation == null) {
                tmpStation = station;
            } else {
                this.nowEvent.conduct(tmpStation, station);
                tmpStation = null;
            }
        } else {
            this.nowEvent.conduct(station);
            tmpStation = null;
        }
    }

    public void railwayOnClick(Railway railway, Position position) {
        this.nowEvent.conduct(railway, position);
        tmpStation = null;
    }

    public void locomotiveOnClick(Locomotive locomotive) {
        this.nowEvent.conduct(locomotive);
        tmpStation = null;
    }

    public static int textDuration = 50;
    public static Queue<Text> textMessage = new LinkedList<>();
    public static void showText(String s) {
        for (int i = 0; i < textDuration; i++) {
            Text t = new Text();
            t.setText(s);
            t.setX(100);
            t.setY(400);
            textMessage.offer(t);
        }
    }

    public static double satisfactionBarRate = 1.0;
}
