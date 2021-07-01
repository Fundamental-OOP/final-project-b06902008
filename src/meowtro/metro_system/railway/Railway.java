package meowtro.metro_system.railway;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineJoin;
import meowtro.Position;
import meowtro.game.Game;
import meowtro.game.obstacle.Obstacle;
import meowtro.metro_system.Direction;
import meowtro.metro_system.station.Station;
import meowtro.metro_system.train.Car;
import meowtro.metro_system.train.Locomotive;

public class Railway {
    public int railwayID = -1; 
    private Line line; 
    public Station start; 
    public Station end; 
    private long remainTimeToLive; 
    private List<Locomotive> locomotives = new ArrayList<Locomotive>(); 
    private ArrayList<RailwayDecorator> railwayDecorators = new ArrayList<RailwayDecorator>(); 
    private RectangularRailwayRealizer realizer; 

    private int fragileThreshold = 240; 
    private long maxLimitedRemainTimeToLive; 
    private int originalPrice = 1000; 

    private double length; 
    private double warmupDist = 32.0; 
    private HashMap<Locomotive, Double> positionsInAbstractLine = new HashMap<Locomotive, Double>();

    private List<Path> obstacleImages = new ArrayList<>();
    public List<Path> getObstacleImages() {
        return this.obstacleImages;
    }

    private Game game;
    private List<Position> turningPositions;
    /**
    * Parse game config and set proper value. 
    */
    public void init(){
        this.fragileThreshold = Integer.valueOf(Game.getConfig().get("metro_system.railway.fragile_threshold")); 
        this.originalPrice = Integer.valueOf(Game.getConfig().get("metro_system.railway.original_price")); 
    }

    private Path image;
    public void setImage(Color color) {
        this.image = new Path();
        this.image.getElements().add(new MoveTo(this.turningPositions.get(0).j, this.turningPositions.get(0).i));
        for (int i = 1; i < this.turningPositions.size(); i++) {
            this.image.getElements().add(new LineTo(this.turningPositions.get(i).j, this.turningPositions.get(i).i));
        }
        this.image.setStrokeWidth(5);
        this.image.setStrokeLineJoin(StrokeLineJoin.ROUND);
        this.image.setStroke(color);
        this.image.setOnMouseClicked(
            new EventHandler<MouseEvent>() {    
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("Click rail way");
                    onClick(event);
                }
            }
        );
    }
    public Path getImage() {
        return this.image;
    }
    private void onClick(MouseEvent event) {
        this.game.railwayOnClick(this, new Position(event.getY(), event.getX()));
    }

    public Railway(Station s1, Station s2, Line line, long maxLimitedRemainTimeToLive, List<Station> allStations, List<Obstacle> obstacles, Game game, Color color){
        if (s1 == s2){
            return; 
        }
        init();
        this.maxLimitedRemainTimeToLive = maxLimitedRemainTimeToLive;
        this.remainTimeToLive = maxLimitedRemainTimeToLive;
        boolean DEBUG = true; 
        this.line = line;
        this.game = game;

        if (s1.getAdjacents().contains(s2) || s2.getAdjacents().contains(s1)){
            if (DEBUG){
                System.out.println("Can't have two railways between two stations!");
            }
            return;
        }

        List<Railway> s1AdjRailways = s1.getRailwaysWithLine(line); 
        List<Railway> s2AdjRailways = s2.getRailwaysWithLine(line); 

        assert s1AdjRailways.size() <= 2 && s2AdjRailways.size() <= 2; 

        if ((s1AdjRailways.size()==1 && s2AdjRailways.size()>=2)||(s1AdjRailways.size()>=2 && s2AdjRailways.size()==1)) {
            if (DEBUG){
                System.out.println("Can't create loop!");
            }
            return;
        }

        // if (s1AdjRailways.size() >= 1 && s2AdjRailways.size() >= 1){
        //     // add a shortcut railway and destroy old
        //     assert s1.isEndStationInLine(line) && s2.isEndStationInLine(line); 
        //     for (Railway r: line.getRailwaysBetweenStations(s1, s2)){
        //         r.destroy();
        //     }
        //     s1AdjRailways = s1.getRailwaysWithLine(line); 
        //     s2AdjRailways = s2.getRailwaysWithLine(line); 
            
        //     // --S1 --this- S2---
        //     assert s1AdjRailways.size() <= 1 && s2AdjRailways.size() <= 1; 
        //     if (s1AdjRailways.size() == 1 && s2AdjRailways.size() == 1){
        //         Railway r1 = s1AdjRailways.get(0); 
        //         Railway r2 = s2AdjRailways.get(0); 
        //         if (r1.end == s1 && r2.start == s2){
        //             this.end = s2; 
        //             this.start = s1; 
        //         }else if (r1.start == s1 && r2.end == s2){
        //             this.end = s1; 
        //             this.start = s2; 
        //         }else{
        //             // error
        //             if (DEBUG){
        //                 System.out.println("Create shortcut railway error: mismatched directions");
        //             }
        //             return; 
        //         }
        //     }
        // }
        if (s1.isEndStationInLine(line) && s2.isEndStationInLine(line)){
            System.out.println("Circular line is illegal");
        }
        else if (s1AdjRailways.size() == 0 && s2AdjRailways.size() == 0){
            // brand new line
            assert line.getRailways().size() == 0; 
            this.start = s1;
            this.end = s2;
        }
        else if (s1AdjRailways.size() == 1 || s2AdjRailways.size() == 1){
            // extend from one end
            Station endStationOfThisLine = null; 
            Station NewEndStation = null; 
            Railway endRailway = null; 
            if (s1AdjRailways.size() == 1){
                endStationOfThisLine = s1; 
                NewEndStation = s2; 
                endRailway = s1AdjRailways.get(0);
            }else{
                endStationOfThisLine = s2;
                NewEndStation = s1; 
                endRailway = s2AdjRailways.get(0);
            }

            if (endRailway.start == endStationOfThisLine){
                this.end = endStationOfThisLine; 
                this.start = NewEndStation; 
            }
            else if (endRailway.end == endStationOfThisLine){
                this.end = NewEndStation; 
                this.start = endStationOfThisLine; 
            }
        }else {
            if (DEBUG)
                System.out.println("construct Railway() error");
            return; 
        }

        this.realizer = new RectangularRailwayRealizer(start, end, allStations, obstacles);
        this.turningPositions = realizer.Nodes;
        
        if (this.realizer.isIntersectedWithObstacle()) {
            for (Entry<List<Position>, Obstacle> mapElement : this.realizer.obsticleEndPoints.entrySet()) {
                List<Position> positionPair = mapElement.getKey();
                Game.setBalance(Game.getBalance()-mapElement.getValue().getAdditionalCost());
                Path o = new Path();
                o.getElements().add(new MoveTo(positionPair.get(0).j, positionPair.get(0).i));
                o.getElements().add(new LineTo(positionPair.get(1).j, positionPair.get(1).i));
                o.setStrokeWidth(3);
                o.setStrokeLineJoin(StrokeLineJoin.ROUND);
                this.obstacleImages.add(o);
            }
        }

        if ((start == null && end == null) || !realizer.isValidRailway()){
            if (DEBUG)
                System.out.println("construct Railway() error");
            return;
        }

        if (start != null){
            start.addRailway(this);
        }
        if (end != null){
            end.addRailway(this);
        }

        setImage(color);
        this.length = computeLength(); 
        this.warmupDist = Math.min(length / 2, 64.0); 
        line.addRailway(this);
    }

    public long getRemainLive(){
        return this.remainTimeToLive;
    }
    public Station getAdjacent(Station sourceStation){
        if (sourceStation != start && sourceStation != end){
            return null; 
        }
        if (line.isCircular()){
            if (sourceStation == start){
                if (line.getDirections().contains(Direction.FORWARD))
                    return end; 
            }else{
                if (line.getDirections().contains(Direction.BACKWARD))
                    return start; 
            }
            return null; 
        }else{
            if (sourceStation == start){
                return end; 
            }else{
                return start; 
            }
        }
    }

    public Line getLine(){
        return line; 
    }

    public double parsePositionToAbstractPosition(Position p){
        return realizer.parsePositionToAbstractPosition(p); 
    }

    private Position parseAbstractPositionToPosition(double abstractPosition){
        return realizer.parseAbstractPositionToPosition(abstractPosition);
    }

    public Position getVector(Position p, Direction d, int length) {
        p = parseAbstractPositionToPosition(parsePositionToAbstractPosition(p));
        Position p_head;
        if (d == Direction.BACKWARD) {
            p_head = parseAbstractPositionToPosition(parsePositionToAbstractPosition(p) - (length/2));
        } else {
            p_head = parseAbstractPositionToPosition(parsePositionToAbstractPosition(p) + (length/2));
        }
        return new Position(p_head.i-p.i, p_head.j-p.j);
    }

    public void addLocomotive(Locomotive l){
        locomotives.add(l); 
        positionsInAbstractLine.put(l, (double) parsePositionToAbstractPosition(l.getPosition())); 
    }

    public void locomotiveDepart(Locomotive l){
        locomotives.add(l); 
        if (l.getDirection() == Direction.FORWARD){
            positionsInAbstractLine.put(l, 0.0); 
        }else{
            positionsInAbstractLine.put(l, length); 
        }
    }


    public void removeLocomotive(Locomotive l){
        if (locomotives.contains(l))
            locomotives.remove(l); 
        if (positionsInAbstractLine.containsKey(l))
            positionsInAbstractLine.remove(l); 
    }

    public void setRemainTimeToLive(long remainTime){
        this.remainTimeToLive = remainTime; 
        // this.maxLimitedRemainTimeToLive = remainTime; 
    }

    public boolean isFragile(){
        return remainTimeToLive < fragileThreshold; 
    }

    public void fix(){
        // TODO
    }

    public int getRemainPrice(){
        // if (remainTimeToLive == Integer.MAX_VALUE){
        //     return originalPrice; 
        // }
        return (int)(remainTimeToLive / maxLimitedRemainTimeToLive) * originalPrice; 
    }

    public Station getNextStation(Direction directionToward){
        if (directionToward == Direction.FORWARD){
            return end; 
        }
        return start; 
    }

    public void destroy(){
        boolean DEBUG = true; 
        if (DEBUG){
            System.out.printf("Railway %d in Line %s destroyed\n", railwayID, line.getColor()); 
        }
        for (int i = locomotives.size()-1; i>= 0; i--) {
            locomotives.get(i).destroy();
            removeLocomotive(locomotives.get(i));   
        }
        if (start != null){
            start.removeRailway(this);
        }
        if (end != null){
            end.removeRailway(this);
        }
        this.game.deleteObject(this.image);
        line.removeRailways(this);
        for (Path o : this.obstacleImages) {
            this.game.deleteObject(o);
        }
    }

    private double computeLength(){
        return Math.abs(end.getPosition().i-start.getPosition().i) + Math.abs(end.getPosition().j-start.getPosition().j);
    }

    public void moveCars(Locomotive l, double abstractPosition) {
        if (l.getDirection() == Direction.FORWARD) {
            abstractPosition -= (l.getLength()/2+2);
        } else {
            abstractPosition += (l.getLength()/2+2);
        }
        
        for (Car car : l.getCars()) {
            if (l.getDirection() == Direction.FORWARD) {
                double newAbstractPosition = abstractPosition - car.getLength()/2;
                newAbstractPosition = Math.max(newAbstractPosition, 0);
                car.setPosition(parseAbstractPositionToPosition(newAbstractPosition));
                abstractPosition -= (car.getLength()+2);
            } else {
                double newAbstractPosition = abstractPosition + car.getLength()/2;
                newAbstractPosition = Math.min(newAbstractPosition, length);
                car.setPosition(parseAbstractPositionToPosition(newAbstractPosition));
                abstractPosition += car.getLength()+2;
            }
        }
    }

    public Position moveLocomotive(Locomotive l){
        if (l == null){
            if (Game.DEBUG){
                System.out.println("move null Locomotive");
            }
            return null; 
        }
        double speed = l.getSpeed(); 
        int maxSpeed = l.getMaxSpeed(); 
        
        int orientation = 1; 
        if (l.getDirection() == Direction.BACKWARD){
            orientation = -1; 
        }
        
        double newAbstractPosition = positionsInAbstractLine.get(l) + (speed * orientation);

        moveCars(l, newAbstractPosition);

        newAbstractPosition = Math.min(newAbstractPosition, length);
        newAbstractPosition = Math.max(newAbstractPosition, 0);
        positionsInAbstractLine.put(l, newAbstractPosition);
        if (Game.DEBUG_hash.equals("loco")){
            System.out.printf("Move %s to %f/%f in railway %s with %d passengers\n", l.toString(), newAbstractPosition, length, this.toString(), l.getAllPassenger().size());
        }

        l.setSpeed(maxSpeed * Math.min(Math.min(newAbstractPosition + 1, length-newAbstractPosition + 1) / warmupDist, 1.0));   
        return parseAbstractPositionToPosition(newAbstractPosition); 
    }

    public String toString(){
        return String.format("%s:%d", line.getColor().toString(), railwayID); 
    }

    public void update(){
        // just for check; normally this shouldn't happen
        if (start == null && end == null){
            destroy();
        }

        // LinkedList<Locomotive> updateQueue = new LinkedList<Locomotive>(locomotives); 
        // while (!updateQueue.isEmpty()){
        //     Locomotive l = updateQueue.removeFirst();
        //     l.update();
        // }
    }


    public boolean isArrived(Locomotive locomotive, Station nextStation) {
        double abstractPosition = positionsInAbstractLine.get(locomotive); 
        if (nextStation == end){
            return abstractPosition >= length;
        }
        return abstractPosition <= 0;
    }
}
