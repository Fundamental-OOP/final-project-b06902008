package meowtro.metro_system.station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import meowtro.Position;
import meowtro.game.*;
import meowtro.game.entityManager.EntityManager;
import meowtro.game.passenger.Passenger;
import meowtro.metro_system.railway.Line;
import meowtro.metro_system.railway.Railway;
import meowtro.metro_system.train.Locomotive;

// for testing
import java.io.FileInputStream;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


public class Station {
    public String name = "s"; 
    private City city; 
    private ArrayList<Railway> railways = new ArrayList<Railway>(); 
    private ArrayList<Passenger> queue = new ArrayList<Passenger>(); 
    private HashSet<Line> lines = new HashSet<Line>(); 
    private ArrayList<Locomotive> arrivedLocomotives = new ArrayList<Locomotive>(); 
    private int level = 0;
    private int upgradeCost = 0;
    private int maxLevel = 6;
    private Position position; 
    private Region region;
    private int maxLineNum = 6; 
    private int index = 0;
    private static int nextIndex = 0;
    private String iconPath;
    private ImageView image;
    private EntityManager manager;

    public int getIndex() {
        return this.index;
    }
    private static int getNextIndex() {
        return (Station.nextIndex++);
    }

    /**
    * Parse game config and set proper value. 
    */
    public void init(){
        this.maxLineNum = Integer.valueOf(Game.getConfig().get("metro_system.station.max_line_num"));
        // get upgrade cost
        // get max level
    }

    public int getLevel() {
        return this.level;
    }
    public int getMaxLevel() {
        return this.maxLevel;
    }
    public int getUpgradeCost() {
        return this.upgradeCost;
    }
    public void upgrade() {
        this.level ++;
    }

    private double stationSize = 30;
    public double getStationSize() {
        return this.stationSize;
    }
    public String getIconPath() {
        return this.iconPath;
    }
    private double imageSize = 30;
    public double getImageSize() {
        return this.imageSize;
    }
    private void setImage() {
        try {
            Image img = new Image(new FileInputStream(this.iconPath));
            this.image = new ImageView(img);
            this.image.setPickOnBounds(true);
            this.image.setLayoutX(this.position.j-this.imageSize/2);
            this.image.setLayoutY(this.position.i-this.imageSize/2);
            this.image.setFitHeight(this.imageSize);
            this.image.setFitWidth(this.imageSize);
            this.image.setOnMouseClicked(
                new EventHandler<MouseEvent>() {    
                    @Override
                    public void handle(MouseEvent event) {
                        onClick();
                    }
                }
            );

        } catch (Exception e) {
            System.out.println("Image doesn't exist!");
        }
    }
    private void onClick() {
        // stationOnClick in Game
        this.city.getGame().stationOnClick(this);
    }
    public ImageView getImage() {
        return this.image;
    }

    public EntityManager getManager() {
        return this.manager;
    }
    public void setManager(EntityManager manager) {
        this.manager = manager;
    }

    public Station(City city, Position p, String iconPath){
        init();
        this.position = p;
        this.level = 0; 
        this.city = city;
        this.index = Station.getNextIndex();
        
        if (Game.DEBUG) {
            System.out.println(this.toString() + " built at " + city.getRegionByPosition(p).toString());
        }

        this.region = this.city.getRegionByPosition(this.position);
        this.iconPath = iconPath;
        setImage();
    }

    private int getMaxLineNum(){
        return maxLineNum;
    }

    private int getMaxQueueSize(){
        return level + 8; 
    }

    public Station(Position p){
        this.position = p; 
        this.level = 0; 
    }

    public void setRegion(Region r){
        this.region = r; 
    }
    public Region getRegion() {
        return this.region;
    }

    public void addRailway(Railway r){
        this.railways.add(r); 
        this.lines.add(r.getLine()); 
        assert lines.size() <= getMaxLineNum(); 
    }

    public void removeRailway(Railway r){
        railways.remove(r); 
    }

    public List<Railway> getRailways(){
        return railways; 
    }

    public List<Railway> getRailwaysWithLine(Line l){
        ArrayList<Railway> result = new ArrayList<Railway>(); 
        for (Railway r: getRailways()){
            if (r.getLine() == l){
                result.add(r); 
            }
        }
        return result; 
    }

    public void removeLine(Line l){
        if (lines.contains(l)){
            for (Railway r: railways){
                if (r.getLine() == l){
                    railways.remove(r); 
                }
            }
        }
        lines.remove(l); 
    }

    public List<Line> getLines(){
        return new ArrayList<Line>(lines); 
    }

    public List<Passenger> getPassengerQueue(){
        return queue; 
    }

    public boolean isAdjTo(Station s){
        return getAdjacents().contains(s); 
    }

    public List<Station> getAdjacents(){
        ArrayList<Station> adjList = new ArrayList<Station>(); 
        for (Railway r: railways){
            Station adj = r.getAdjacent(this); 
            if (adj != null){
                adjList.add(adj); 
            }
        }
        return adjList; 
    }

    public Railway getNextRailway(Railway srcRailway){
        Line currentLine = srcRailway.getLine(); 
        for (Railway r: railways){
            if (r != srcRailway && r.getLine() == currentLine){
                return r; 
            }
        }
        // if can't find next railway, turn around
        return srcRailway; 
    }

    public void setPosition(Position p){
        this.position = p; 
    }

    public Position getPosition(){
        return position; 
    }

    public void insertPassenger(Passenger p, int index){
        // index = 0 or -1

        if (Game.DEBUG)
            System.out.printf("queue size: %d, max queue size: %d%n", queue.size(), getMaxQueueSize());
        if (queue.size() >= getMaxQueueSize()){
            // p.selfExplode();
            p.setTimeToLive((long) 30);
            return;
        }

        if (index < 0){
            index = queue.size() + index + 1; 
        }
        assert index >= 0 && index <= queue.size(); 
        queue.add(index, p);
    }

    public List<Locomotive> getArrivedLocomotives(){
        return arrivedLocomotives; 
    }

    public boolean isEndStationInLine(Line l){
        int lineCnt = 0; 
        for (Railway r: railways){
            if (r.getLine() == l){
                lineCnt += 1; 
            }
        }
        assert lineCnt >= 0 || lineCnt <= 2; 
        if (lineCnt == 1){
            return true; 
        }
        return false; 
    }

    public void locomotiveArrive(Locomotive l){
        this.arrivedLocomotives.add(l);
    }

    public void locomotiveDepart(Locomotive l){
        this.arrivedLocomotives.remove(l); 
    }

    public void destroy(){
        for (int i = queue.size() - 1; i >= 0; i--) {
            queue.get(i).setTimeToLive((long) 30);
        }
        // queue.get(i).selfExplode();
        
        List<Line> linesCopy = new ArrayList<Line>(lines);
        for (int i = linesCopy.size() - 1; i >= 0; i--)
            linesCopy.get(i).destroyAll();

        city.removeStation(this);
    }

    private int maxColumnOfQueue = 4;
    public void updateQueuedPassengerPosition() {
        if (Game.DEBUG)
            System.out.printf("station_%d queue size: %d%n", this.index, this.queue.size());
            
        Position startPosition = new Position(this.position.j-this.imageSize/2, this.position.i-this.imageSize/2);
        double translationX = this.stationSize+3;
        double translationY = 0;
        for (int i = 0; i < this.queue.size(); i++) {
            Passenger passenger = this.queue.get(i);
            passenger.setPosition(new Position(startPosition.j + translationY, startPosition.i + translationX));
            // passenger.setImagePosition(new Position(startPosition.j + translationY, startPosition.i + translationX), passenger.getImageSize()/2);
            translationX += passenger.getImageSize();
            if (i%maxColumnOfQueue == maxColumnOfQueue-1) {
                translationX = this.stationSize+3;
                translationY += passenger.getImageSize();
            }
        }
    }

    public void update(){
        // for each arrived
        updateQueuedPassengerPosition();

        if (Game.DEBUG) {
            System.out.println(this.toString() + " [" + this.queueStr() + "]");
        }
    }

    // public static void main(String[] args) {
    //     Game.setToyConfig();
    //     Line l = new Line(null, LineColor.RED); 

    //     // read image
    //     BufferedImage image = null;
    //     try {
    //         image = ImageIO.read(new File("../image/map_1.png"));
    //     } catch (IOException e) {
    //         System.err.println(e.getMessage());
    //     }

    //     // test City
    //     // City city = new City(image);
    //     Region r = new Region(null, null); 

    //     Station s1 = new Station(null , new Position(200, 0)); 
    //     Station s2 = new Station(null , new Position(300, 0)); 
    //     Station s3 = new Station(null , new Position(100, 0)); 
    //     Station s4 = new Station(null , new Position(400, 0)); 
    //     s1.name = "s1"; 
    //     s2.name = "s2"; 
    //     s3.name = "s3"; 
    //     s4.name = "s4"; 

    //     r.addStation(s1);
    //     r.addStation(s2);
    //     r.addStation(s3);
    //     r.addStation(s4);

    //     Railway r1 = new Railway(s1, s2, l); 
    //     Railway r2 = new Railway(s3, s1, l); 
    //     Railway r3 = new Railway(s4, s2, l); 

    //     System.out.printf("%d %d %d\n", r1.railwayID, r2.railwayID, r3.railwayID); 

    //     // <s3> -r1- <s1> -r2- <s2> -r3- <s4>
    //     Passenger p = new Passenger(r, new Position(4, 0), s4); 
    //     Locomotive loco = new Locomotive(r3, new Position(4, 0), Direction.BACKWARD); 
    //     loco.addCar(new Car(loco));

    //     for (int i = 0; i < 200; i++){
    //         l.update(); 
    //         p.update(); 
    //     }
    // }


    public void removePassenger(Passenger p) {
        if (queue.contains(p)){
            queue.remove(p);
        }
    }

    @Override
    public String toString() {
        return String.format("S%d %s", this.index, this.position.toString());
    }

    private String queueStr() {
        String str = "";
        for (Passenger p: this.queue)
            str += (p.toString() + " ");
        return str;
    }
}
