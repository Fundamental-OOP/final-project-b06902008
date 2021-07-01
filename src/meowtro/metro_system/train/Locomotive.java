package meowtro.metro_system.train;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import meowtro.Position;
import meowtro.game.*;
import meowtro.game.entityManager.EntityManager;
import meowtro.game.passenger.Passenger;
import meowtro.metro_system.*;
import meowtro.metro_system.railway.*;
import meowtro.metro_system.station.*;

public class Locomotive {

    private enum State{
        MOVING, 
        ARRIVE_DROP, 
        ARRIVE_GETON, 
    }

    private Station currentStation;  // Station | null
    private int level = 0; 
    private int maxLevel; 
    private int upgradeCost = 0;
    private double currentSpeed; 
    private Map<Integer, Integer> levelToMaxSpeed; 
    private Map<Integer, Integer> levelToMaxCar; 
    private Railway railway; 
    private Position position; 
    private Direction direction; 
    private ArrayList<Car> cars = new ArrayList<Car>(); 
    private Game game;

    private int takePassengerInterval = 10; 
    private int dropPassengerInterval = 10; 
    private int takePassengerCountdown = 0; 
    private int dropPassengerCountdown = 0; 
    private LinkedList<Passenger> getDownQueue = null; 
    private LinkedList<Passenger> getUpQueue = null; 
    private int index = 0;
    private static int nextIndex = 0;
    
    private State state; 
    private int distThres = 8; 

    private static int getNextIndex() {
        return (Locomotive.nextIndex++);
    }
    
    /**
    * Parse game config and set proper value. 
    */
    public void init(){
        this.maxLevel = Integer.valueOf(Game.getConfig().get("metro_system.locomotive.max_level"));
        this.takePassengerCountdown = Integer.valueOf(Game.getConfig().get("metro_system.locomotive.take_passenger_interval")); 
        this.dropPassengerCountdown = Integer.valueOf(Game.getConfig().get("metro_system.locomotive.drop_passenger_interval")); 
        this.distThres = Integer.valueOf(Game.getConfig().get("metro_system.locomotive.dist_thres"));

        this.levelToMaxSpeed = new HashMap<Integer, Integer>(); 
        this.levelToMaxCar = new HashMap<Integer, Integer>(); 

        int level = 0; 
        for (String s: Game.getConfig().get("metro_system.locomotive.level_to_maxspeed").split("_")){
            levelToMaxSpeed.put(level, Integer.valueOf(s)); 
            level += 1; 
        }
        if (level < maxLevel)
            System.out.println("ValueError: metro_system.locomotive.level_to_maxspeed < maxLevel"); 
        level = 0; 
        for (String s: Game.getConfig().get("metro_system.locomotive.level_to_maxcar").split("_")){
            levelToMaxCar.put(level, Integer.valueOf(s)); 
            level += 1; 
        }
        if (level < maxLevel)
            System.out.println("ValueError: metro_system.locomotive.level_to_maxspeed < maxLevel"); 
        
        // get upgrade cost
    }
    public int getUpgradeCost() {
        return this.upgradeCost;
    }

    private Color color;
    public Color getColor() {
        return this.color;
    }
    private int length = 20;
    private int width = 20;
    public int getLength() {
        return this.length;
    }
    private Rectangle image;
    private void setImage(Color color) {
        this.image = new Rectangle();
        this.image.setFill(color);
        if (this.vector.i != 0) {
            this.image.setHeight(this.length);
            this.image.setWidth(this.width);
            setImagePosition(this.position, this.width/2, this.length/2);
        } else if (this.vector.j != 0) {
            this.image.setHeight(this.width);
            this.image.setWidth(this.length);
            setImagePosition(this.position, this.length/2, this.width/2);
        }
        this.image.setOnMouseClicked(
            new EventHandler<MouseEvent>() {    
                @Override
                public void handle(MouseEvent event) {
                    onClick(event);
                }
            }
        );
    }
    private void onClick(MouseEvent event) {
        this.game.locomotiveOnClick(this);
    }
    private void resetImage() {
        if (this.vector.i != 0) {
            this.image.setHeight(this.length);
            this.image.setWidth(this.width);
            setImagePosition(this.position, this.width/2, this.length/2);
        } else if (this.vector.j != 0) {
            this.image.setHeight(this.width);
            this.image.setWidth(this.length);
            setImagePosition(this.position, this.length/2, this.width/2);
        }
    }
    public Rectangle getImage() {
        return this.image;
    }
    public void setImagePosition(Position position, double shiftSizeX, double shiftSizeY) {
        this.image.setLayoutX(position.j-shiftSizeX);
        this.image.setLayoutY(position.i-shiftSizeY);
    }
    public void tuneImageSize(double size) {
        this.image.setHeight(size);
        this.image.setWidth(size);
    }

    private Position vector;
    private Position newVector;

    public Locomotive(Railway railway, Position position, Direction direction, Color color, Game game){
        init();
        this.railway = railway; 
        this.position = position; 
        this.direction = direction;
        this.color = color;
        this.game = game;
        this.state = State.MOVING; 
        this.index = Locomotive.getNextIndex();
        this.vector = this.railway.getVector(this.position, this.direction, this.length);
        railway.addLocomotive(this);
        if (Game.DEBUG){
            System.out.printf("Locomotive created at %s, railway %s\n", position.toString(), railway.toString());
        }
        setImage(color);
    }

    private EntityManager manager;
    public void setManager(EntityManager manager) {
        this.manager = manager;
    }
    public EntityManager getManager() {
        return this.manager;
    }

    public Position getPosition(){
        return position; 
    }

    public int getMaxSpeed(){
        return levelToMaxSpeed.get(level); 
    }

    public Station getSourceStation(){
        if (direction == Direction.FORWARD){
            return railway.start; 
        }
        return railway.end; 
    }

    public Station getDestinationStation(){
        if (direction == Direction.FORWARD){
            return railway.end; 
        }
        return railway.start; 
    }

    public Direction getDirection(){
        return direction; 
    }

    public Railway getRailway() {
        return this.railway;
    }

    public Line getLine(){
        return railway.getLine(); 
    }

    public double getSpeed(){
        return currentSpeed; 
    }

    public void addCar(Car car){
        this.cars.add(car);
        this.railway.moveCars(this, this.railway.parsePositionToAbstractPosition(this.position));
    }

    public List<Car> getCars() {
        return this.cars;
    }

    public int getMaxCarNumber(){
        return levelToMaxCar.get(level); 
    }

    public void turnAround(){
        if (Game.DEBUG){
            System.out.printf("Turned around\n"); 
        }
        if (direction == Direction.BACKWARD){
            this.direction = Direction.FORWARD; 
        }else{
            this.direction = Direction.BACKWARD; 
        }
    }

    public void setSpeed(double speed){
        this.currentSpeed = speed; 
    }

    public void setLevel(int l){
        this.level = l; 
    }
    public int getLevel(){
        return level; 
    }
    public int getMaxLevel() {
        return this.maxLevel;
    }

    private boolean assignPassengerToCar(Passenger p){
        for (Car c: cars){
            if (!c.isFull()){
                p.enterCar(c);
                return true; 
            }
        }
        return false; 
    }


    public LinkedList<Passenger> getAllPassenger(){
        LinkedList<Passenger> result = new LinkedList<Passenger>(); 
        for (Car c: cars){
            result.addAll(c.getPassengers()); 
        }
        return result; 
    }
    

    public void arrive(Station s){
        this.currentStation = s; 
    }

    public void depart(){
        // if (Game.DEBUG){
        System.out.printf("Loco depart from station %s\n", currentStation.toString());
        // }
        this.takePassengerCountdown = 0; 
        this.dropPassengerCountdown = 0; 
        currentStation.locomotiveDepart(this);
        railway.removeLocomotive(this);
        this.railway = currentStation.getNextRailway(railway); 
        railway.locomotiveDepart(this);
        this.currentStation = null; 
        this.state = State.MOVING; 
        // TODO: handle speed
        this.currentSpeed = 0.2; 
    }

    public Station getCurrentStation() {
        return this.currentStation;
    }

    public Station getNextDstStation(){
        if (currentStation != null){
            return currentStation.getNextRailway(railway).getNextStation(direction); 
        }
        return null; 
    }


    public void tryTakePassenger(){
        if (getUpQueue.size() == 0){
            depart();
            return; 
        }
        if (takePassengerCountdown == 0){
            Passenger p = getUpQueue.removeFirst(); 
            boolean success = assignPassengerToCar(p); 
            if (success){
                if (Game.DEBUG){
                    System.out.printf("...Passenger get on locomotive at station %s, %s\n", currentStation.name, position.toString());
                }
                currentStation.removePassenger(p); 
            } else{
                // Cars are full
                if (Game.DEBUG)
                    System.out.printf("...Cars are full or don't have any car\n");
                depart();
            }
            this.takePassengerCountdown = takePassengerInterval; 
        } else{
            this.takePassengerCountdown -= 1; 
        }
    }

    private void dropPassenger(Passenger p){
        for(Car c: cars){
            if (c.getPassengers().contains(p)){
                c.removePassenger(p); 
                p.enterStation(currentStation);
            }
        }

    }

    public void tryDropPassenger(){
        if (getDownQueue.size() == 0){
            this.state = State.ARRIVE_GETON; 
            return; 
        }
        if (takePassengerCountdown == 0){
            while (getDownQueue.size() > 0){
                Passenger p = getDownQueue.removeFirst(); 
                if (p.willingToGetOff(this)){
                    dropPassenger(p); 
                    if (Game.DEBUG){
                        System.out.printf("Passenger get off locomotive at station %s, %s\n", currentStation.toString(), position.toString());
                    }
                    this.takePassengerCountdown = takePassengerInterval; 
                    break; 
                }
            }
            // no passenger want to get down
            this.state = State.ARRIVE_GETON; 
        }else{
            this.takePassengerCountdown -= 1; 
        }
    }

    public void destroy(){
        for (Car c: cars){
            c.destroy();
            this.game.deleteObject(c.getImage());
        }
        this.game.deleteObject(this.getImage());
        cars.clear();
    }

    private boolean isTurning = false;
    public void update(){
        if (state == State.MOVING){
            // update current position
            this.position = railway.moveLocomotive(this);
            
            this.newVector = railway.getVector(this.position, this.direction, this.length);
            if (isTurning) {
                if (Math.round(this.vector.i*1000)==Math.round(this.newVector.i*1000) && Math.round(this.vector.j*1000)==Math.round(this.newVector.j*1000)) {
                    resetImage();
                    isTurning = false;
                }
            } else if (Math.round(this.vector.i)!=Math.round(this.newVector.i) || Math.round(this.vector.j)!=Math.round(this.newVector.j)) {
                isTurning = true;
            }
            this.vector = this.newVector;

            assert this.position != null;
            if (this.vector.i != 0) {
                setImagePosition(this.position, this.width/2, this.length/2);
            } else {
                setImagePosition(this.position, this.length/2, this.width/2);
            }

            // update current station
            if (railway.isArrived(this, railway.getNextStation(direction))){
                railway.getNextStation(direction).locomotiveArrive(this);
                this.currentStation = railway.getNextStation(direction);
                if (Game.DEBUG){
                    System.out.printf("arrived %s, %d waiting\n", currentStation.toString(), currentStation.getPassengerQueue().size()); 
                }
                
                if (currentStation.getNextRailway(railway) == railway){
                    turnAround();
                }
                this.currentSpeed = 0; 
                this.takePassengerCountdown = 0;
                this.dropPassengerCountdown = 0;
                this.getDownQueue = new LinkedList<Passenger>(
                                                getAllPassenger()
                                                .stream()
                                                .filter(p -> p.willingToGetOff(this))
                                                .collect(Collectors.toList())); 
                this.getUpQueue = new LinkedList<Passenger>(
                                                currentStation.getPassengerQueue()
                                                .stream()
                                                .filter(p -> p.willingToGetOn(this))
                                                .collect(Collectors.toList())); 
                this.state = State.ARRIVE_DROP; 
            }
        }
        else if (state == State.ARRIVE_DROP){
            if (Game.DEBUG)
                System.out.printf("Trying Dropping passenger...\n");
            tryDropPassenger(); 
        }
        else if (state == State.ARRIVE_GETON){
            if (Game.DEBUG)
                System.out.printf("Trying taking passenger...\n");
            tryTakePassenger();
        }
        else{
            // error
            if (true)
                System.out.println("Locomotive state error");
        }
    }

    public String toString() {
        return String.format("L%d", this.index);
    }
}
