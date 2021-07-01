package meowtro.metro_system.train;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import meowtro.Position;
import meowtro.game.*;
import meowtro.game.passenger.Passenger;

public class Car {
    private int level = 1; 
    private int maxLevel; 
    private int capacity = 8; 
    private List<Passenger> passengers = new ArrayList<Passenger>(); 
    private Locomotive locomotive;
    private Position position; 

    /**
    * Parse game config and set proper value. 
    */
    private void init(){
        // parse config
        this.capacity = Integer.valueOf(Game.getConfig().get("metro_system.car.car_capacity")); 
        this.maxLevel = Integer.valueOf(Game.getConfig().get("metro_system.car.max_level")); 
    }

    private int length = capacity*100/20;
    private int width = 20;
    public int getLength() {
        return this.length;
    }
    private Rectangle image = null;
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

    public Car(Locomotive locomotive){
        init(); 
        this.locomotive = locomotive;
        this.locomotive.addCar(this);
        this.vector = this.locomotive.getRailway().getVector(this.position, this.locomotive.getDirection(), this.length);
        setImage(this.locomotive.getColor());
    }

    public int getLevel(){
        return level; 
    }

    public void setLevel(int level){
        this.level = level; 
    }

    public void addPassenger(Passenger p){
        if (passengers.size() < capacity){
            passengers.add(p); 
        }else{
            if (Game.DEBUG)
                System.out.printf("This car is full with capacity %d\n", capacity);
        }
    }

    public void removePassenger(Passenger p){
        passengers.remove(p); 
    }

    public List<Passenger> getPassengers(){
        return passengers; 
    }

    public Locomotive getLocomotive(){
        return locomotive; 
    }

    private void setPassengerImage() {
        List<Position> seats = new ArrayList<>();
        if (this.vector.i != 0) {
            seats.add(new Position(this.position.i-this.length/2, this.position.j-this.width/2));
            seats.add(new Position(this.position.i-this.length/4, this.position.j-this.width/2));
            seats.add(new Position(this.position.i, this.position.j-this.width/2));
            seats.add(new Position(this.position.i+this.length/4, this.position.j-this.width/2));
            seats.add(new Position(this.position.i-this.length/2, this.position.j));
            seats.add(new Position(this.position.i-this.length/4, this.position.j));
            seats.add(new Position(this.position.i, this.position.j));
            seats.add(new Position(this.position.i+this.length/4, this.position.j));
        } else {
            seats.add(new Position(this.position.i-this.width/2, this.position.j-this.length/2));
            seats.add(new Position(this.position.i-this.width/2, this.position.j-this.length/4));
            seats.add(new Position(this.position.i-this.width/2, this.position.j));
            seats.add(new Position(this.position.i-this.width/2, this.position.j+this.length/4));
            seats.add(new Position(this.position.i, this.position.j-this.length/2));
            seats.add(new Position(this.position.i, this.position.j-this.length/4));
            seats.add(new Position(this.position.i, this.position.j));
            seats.add(new Position(this.position.i, this.position.j+this.length/4));
        }
        for (int i = 0; i < this.passengers.size(); i++) {
            this.passengers.get(i).setImagePosition(seats.get(i), 0);
        }
    }

    private Position vector;
    private Position newVector;
    private boolean isTurning = false;

    public void setPosition(Position position) {
        this.position = position;
        if (this.image != null) {
            this.newVector = this.locomotive.getRailway().getVector(this.position, this.locomotive.getDirection(), this.length);
            if (isTurning) {
                if (Math.round(this.vector.i*1000)==Math.round(this.newVector.i*1000) && Math.round(this.vector.j*1000)==Math.round(this.newVector.j*1000)) {
                    resetImage();
                    isTurning = false;
                }
            } else if (Math.round(this.vector.i)!=Math.round(this.newVector.i) || Math.round(this.vector.j)!=Math.round(this.newVector.j)) {
                isTurning = true;
            }
            this.vector = this.newVector;
            if (this.vector.i != 0) {
                setImagePosition(this.position, this.width/2, this.length/2);
            } else {
                setImagePosition(this.position, this.length/2, this.width/2);
            }
            setPassengerImage();
        }
    }

    public void destroy(){
        if (passengers != null) {
            for (int i = passengers.size()-1; i >= 0; i--) {
                passengers.get(i).selfExplode();
            }
            passengers.clear();
        }
    }

    public boolean isFull() {
        assert passengers.size() <= capacity; 
        return passengers.size() == capacity;
    }
}
