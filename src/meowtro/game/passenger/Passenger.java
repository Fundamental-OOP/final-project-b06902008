package meowtro.game.passenger;

import java.io.FileInputStream;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import meowtro.Position;
import meowtro.game.Game;
import meowtro.game.Region;
import meowtro.metro_system.*;
import meowtro.metro_system.station.Station;
import meowtro.metro_system.train.Car;
import meowtro.metro_system.train.Locomotive;
import meowtro.timeSystem.TimeLine;

public class Passenger {
    
    protected enum State {
        WALKING,
        AT_STATION,
        TRAVELING,
        ARRIVED
    }

    protected Region birthRegion = null;
    protected Position position = null;
    protected long spawnTime = 0;
    protected static long lifeTimeLimit = Long.parseLong(Game.getConfig().get("passenger.life.time.limit").strip());
    protected Station destinationStation = null;
    protected double walkingSpeed = Double.parseDouble(Game.getConfig().get("passenger.walking.speed"));
    protected static int expectedTimePerStation = Integer.parseInt(Game.getConfig().get("passenger.expected.time.per.station"));
    protected Station currentStation = null;
    protected Car currentCar = null;
    protected int traveledStationCount = 0;
    protected State state;
    protected int index = 0;
    protected static int nextIndex = 0;
    
    protected static int getNextIndex() {
        return (Passenger.nextIndex++);
    }

    public Position getPosition() {
        return this.position;
    }
    public void setPosition(Position position) {
        this.position = position;
        this.setImagePosition(this.position, this.imageSize/2);
    }

    protected double imageSize = 10;
    public double getImageSize() {
        return this.imageSize;
    }
    protected ImageView image;
    private void setImage(String iconPath) {
        try {
            Image img = new Image(new FileInputStream(iconPath));
            this.image = new ImageView(img);
            this.image.setPickOnBounds(true);
            this.image.setFitHeight(this.imageSize);
            this.image.setFitWidth(this.imageSize);
            setImagePosition(this.position, this.imageSize/2);
        } catch (Exception e) {
            System.out.println("Image doesn't exist!");
        }
    }
    public ImageView getImage() {
        return this.image;
    }
    public void setImagePosition(Position position, double shiftSize) {
        this.image.setLayoutX(position.j-shiftSize);
        this.image.setLayoutY(position.i-shiftSize);
    }
    public void tuneImageSize(double size) {
        this.image.setFitHeight(size);
        this.image.setFitWidth(size);
    }

    public Passenger(Region birthRegion, Position position, Station destinationStation) {
        this.birthRegion = birthRegion;
        this.position = position;
        this.spawnTime = TimeLine.getInstance().getCurrentTotalTimeUnit();
        this.destinationStation = destinationStation;
        this.index = Passenger.getNextIndex();
        this.state = State.WALKING;
        if (Game.DEBUG) {
            System.out.println(this.toString() + " constructed at " + position.toString() + ", dest: " + destinationStation.toString());
        }
        setImage(this.destinationStation.getIconPath());
    }
    
    public Station findClosestStationInRegion(Region region) {
        double closestDistance = Double.MAX_VALUE;
        Station closestStation = null;
        for (Station station: this.birthRegion.getStations()) {
            double currentDistance = station.getPosition().l2distance(this.position);
            if (currentDistance < closestDistance) {
                closestDistance = currentDistance;
                closestStation = station;
            }
        }
        return closestStation;
    }

    public int findShortestPath(Station station1, Station station2) {
        return ShortestPathCalculator.findShortestPath(station1, station2);
    }

    public void selfExplode() {
        if (this.state == State.AT_STATION)
            this.currentStation.removePassenger(this);
        else if (this.state == State.TRAVELING)
            this.currentCar.removePassenger(this);
        this.die(false);

        if (Game.DEBUG)
            System.out.println(this.toString() + " self exploded");
    }
    
    public void arriveDestination() {
        if (Game.DEBUG)
            System.out.println(this.toString() + " arrive destination");
        
        this.state = State.ARRIVED;
        int ticket = Integer.parseInt(Game.getConfig().get("passenger.ticket.per.station")) * (this.traveledStationCount - 1);
        Game.setBalance(Game.getBalance() + ticket);
        this.die(true);
    }

    private void die(boolean arrivedDestination) {
        // explosion animate here
        this.birthRegion.removePassenger(this, arrivedDestination);
    }

    public void enterStation(Station station) {
        // this.position = station.getPosition();
        this.currentCar = null;
        this.traveledStationCount += 1;
        this.state = State.AT_STATION;
        
        // arrive station
        if (station == this.destinationStation) {
            this.arriveDestination();
        }
        // enter station and wait
        else {
            this.currentStation = station;
            station.insertPassenger(this, -1);
        }
    }

    public void enterCar(Car car) {
        car.addPassenger(this);
        this.currentCar = car;
        this.currentStation = null;
        this.state = State.TRAVELING;
    }

    public int evaluateSatisfaction() {
        if (this.state != State.ARRIVED)
            return 0;
        double timeSpent = (double)(TimeLine.getInstance().getCurrentTotalTimeUnit() - this.spawnTime);
        double expectedTravelTime = (double)(Passenger.expectedTimePerStation * this.traveledStationCount);
        return (int) Math.round(expectedTravelTime / timeSpent);
    }

    private void walkTowardClosestStation() {
        Station closestStation = this.findClosestStationInRegion(this.birthRegion);
        // do not move if no station in region
        if (closestStation == null)
            return;

        // move
        Position closestStationPosition = closestStation.getPosition();
        double distance = this.position.l2distance(closestStationPosition);
        if ((distance-(this.destinationStation.getStationSize()*0.7)) < this.walkingSpeed) {
            // enter station if able to reach
            this.enterStation(closestStation);
        }
        else {
            // otherwise, walk toward
            double ratio = this.walkingSpeed / distance;
            double newPositionI = this.position.i + (closestStationPosition.i - this.position.i) * ratio;
            double newPositionJ = this.position.j + (closestStationPosition.j - this.position.j) * ratio;
            this.position = new Position(newPositionI, newPositionJ);
            setImagePosition(this.position, this.imageSize/2);
        }

        if (Game.DEBUG) {
            if (this.state == State.WALKING)
                System.out.println(this.toString() + " moving toward " + closestStation.toString() + " now at " + this.position.toString());
        }
    }

    public boolean willingToGetOn(Locomotive locomotive) {
        // calculate the shortest path of all candidate paths
        List<Station> adjacentStation = locomotive.getCurrentStation().getAdjacents();
        int shortestPath = Integer.MAX_VALUE;
        for (Station adjStation: adjacentStation) {
            int pathThroughAdj2dest = ShortestPathCalculator.findShortestPath(adjStation, this.destinationStation);
            if (pathThroughAdj2dest < shortestPath)
                shortestPath = pathThroughAdj2dest;
        }

        // calculate the shortest path of current path
        int currentShortestPath = ShortestPathCalculator.findShortestPath(locomotive.getNextDstStation(), this.destinationStation);
        return (currentShortestPath <= shortestPath);
    }

    public boolean willingToGetOff(Locomotive locomotive){
        if (locomotive.getCurrentStation() == destinationStation){
            return true; 
        }
        return !willingToGetOn(locomotive);  
    }

    private long dyingAnimationTimeUnit = 100;
    public void dying(long timeUnitToDie) {
        if (timeUnitToDie >= 30) {
            this.image.setOpacity((0.1*timeUnitToDie)%1);
        } else if (timeUnitToDie >= 22) {
            this.birthRegion.getCity().getGame().deleteObject(this.image);
            setImage("./image/explosion/tank_explosion2.png");
            setImagePosition(this.position, this.imageSize*0.5/2);
            tuneImageSize(this.imageSize*0.5);
        } else if (timeUnitToDie >= 12) {
            this.birthRegion.getCity().getGame().deleteObject(this.image);
            setImage("./image/explosion/tank_explosion3.png");
            setImagePosition(this.position, this.imageSize*0.7/2);
            tuneImageSize(this.imageSize*0.7);
        } else {
            this.birthRegion.getCity().getGame().deleteObject(this.image);
            setImage("./image/explosion/tank_explosion4.png");
            setImagePosition(this.position, this.imageSize*1.0/2);
            tuneImageSize(this.imageSize*1.0);
        }
    }

    private long timeToLive = Passenger.lifeTimeLimit;
    public void setTimeToLive(Long ttl) {
        this.timeToLive = ttl + TimeLine.getInstance().getCurrentTotalTimeUnit() - this.spawnTime;
    }
    public void update() {
        // self explode if exceed life time limit
        if (TimeLine.getInstance().getCurrentTotalTimeUnit() - this.spawnTime > this.timeToLive) {
            this.selfExplode();
            return;
        } else if (TimeLine.getInstance().getCurrentTotalTimeUnit() - this.spawnTime > this.timeToLive - this.dyingAnimationTimeUnit) {
            this.dying(this.timeToLive - TimeLine.getInstance().getCurrentTotalTimeUnit() + this.spawnTime);
        }

        // walking passenger
        if (this.state == State.WALKING) {
            if (Game.DEBUG)
                System.out.printf("passenger_%d update walking%n", this.index);
            this.walkTowardClosestStation();
        }
    }

    @Override
    public String toString() {
        return String.format("P%d(%d)", this.index, this.spawnTime);
    }
}
