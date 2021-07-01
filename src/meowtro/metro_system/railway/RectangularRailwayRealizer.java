package meowtro.metro_system.railway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import meowtro.Position;
import meowtro.game.City;
import meowtro.game.Game;
import meowtro.game.obstacle.Obstacle;
import meowtro.metro_system.station.Station;

public class RectangularRailwayRealizer implements RailwayRealizer{

    private static boolean isInitialized = false;
    private static List<List<Boolean>> OccupancyMap;
    private static HashSet<Station> recordedStations = new HashSet<Station>(); 
    private static double nearbyThreshold = 20.0; 

    public List<Position> Nodes = new ArrayList<Position>(); 
    public HashMap<List<Position>, Obstacle> obsticleEndPoints = new HashMap<List<Position>, Obstacle>(); 
    private boolean isValid = false; 
    private boolean isIntersectedWithObstacle = false; 

    private Position start;
    private Position end; 

    
    public RectangularRailwayRealizer(Station startStation, Station endStation, List<Station> allStations, List<Obstacle> obstacles){
        if (startStation == endStation){
            System.out.println("Two station connected to the railway can't be the same");
            isValid = false; 
            return; 
        }
        if (!isInitialized){
            initOccupancyMap(allStations);
        }

        for (Station s: allStations){
            if (!recordedStations.contains(s)){
                addStationToOccupancyMap(s);
            }
        }
        removeStationInOccupancyMap(startStation);
        removeStationInOccupancyMap(endStation);

        this.start = startStation.getPosition();
        this.end = endStation.getPosition();

        this.isValid = false; 
        if (judgeLine()){
            this.isValid = true; 
        }else if (judgeLShape()){
            this.isValid = true; 
        }else if (judgeZShape()){
            this.isValid = true; 
        } 

        if (isValid){
            if (true){
                if (Nodes.size() == 2){
                    System.out.println("Railway Shape: Straight Line");
                }
                else if (Nodes.size() == 3){
                    System.out.println("Railway Shape: L shaped");
                }
                else if (Nodes.size() == 4){
                    System.out.println("Railway Shape: Z shaped");
                }
            }
            System.out.printf("obs.size()= %d\n", obstacles.size());
            judgeObstacles(obstacles); 
            addStationToOccupancyMap(startStation);
            addStationToOccupancyMap(endStation);
        }

        System.out.printf("obstacle endpoints cnt: %d\n", obsticleEndPoints.size()); 
        for (List<Position> l: obsticleEndPoints.keySet()){
            for (Position p: l){
                System.out.printf("%s ", p.toString()); 
            }
            System.out.print("\n"); 
        }
    }

    private void printOccupancyMap(){
        for (List<Boolean> row: OccupancyMap){
            for (boolean b: row){
                if (b)
                    System.out.printf("X"); 
                if (!b)
                    System.out.printf(".");
            }
            System.out.printf("\n"); 
        }
    }

    private static void initOccupancyMap(List<Station> allStations){
        OccupancyMap = new ArrayList<List<Boolean>>(); 
        
        int height = City.getHeight(); 
        int width = City.getWidth(); 

        System.out.printf("OccupancyMap.shape = (%d, %d)\n", height, width); 
        for (int i = 0; i < height; i++){
            OccupancyMap.add(new ArrayList<Boolean>(Collections.nCopies(width, false))); 
        }
        
        assert OccupancyMap.size() == height; 
        for (List<Boolean> row: OccupancyMap){
            assert row.size() == width; 
        }
        isInitialized = true; 
    }

    static void extendOccupancyMapHeight(int height){
        int oldHeight = OccupancyMap.size()-1; 
        int width = OccupancyMap.get(0).size()-1; 
        if (height <= oldHeight){
            return; 
        }
        for (int i = 0; i < height-oldHeight; i++){
            OccupancyMap.add(new ArrayList<Boolean>(Collections.nCopies(width, false))); 
        }
    }

    static void extendOccupancyMapWidth(int width){
        int oldWidth = OccupancyMap.get(0).size()-1; 
        if (width <= oldWidth){
            return; 
        }
        for (List<Boolean> row: OccupancyMap){
            row.addAll(Collections.nCopies(width - oldWidth, false)); 
        }
    }

    static void addStationToOccupancyMap(Station s){
        Position p = s.getPosition(); 

        for (int i = (int)(p.i - nearbyThreshold); i <= (int)(p.i + nearbyThreshold); i++){
            for (int j = (int)(p.j - nearbyThreshold); j <= (int)(p.j + nearbyThreshold); j++){
                if (i >= 0 && i < OccupancyMap.size()){
                    if (j >= 0 && j < OccupancyMap.get(i).size()){
                        OccupancyMap.get(i).set(j, true); 
                    }
                }
            }
        }
        recordedStations.add(s); 
    }

    static void removeStationInOccupancyMap(Station s){
        Position p = s.getPosition(); 
        for (int i = (int)(p.i - nearbyThreshold); i <= (int)(p.i + nearbyThreshold); i++){
            for (int j = (int)(p.j - nearbyThreshold); j <= (int)(p.j + nearbyThreshold); j++){
                if (i > 0 && i < OccupancyMap.size()){
                    if (j > 0 && j < OccupancyMap.get(0).size()){
                        OccupancyMap.get(i).set(j, false); 
                    }
                }
            }
        }
    }

    private boolean isValidLine(Position a, Position b){
        int discriminant = ((int)a.i - (int)b.i) * ((int)a.j - (int)b.j); 
        boolean valid = false; 
        assert discriminant == 0; 
        if (discriminant == 0){
            valid = true; 
            if (a.i == b.i){
                int i = (int) a.i; 
                for (int j = (int)Math.min(a.j, b.j); j <= (int)Math.max(a.j, b.j); j++){
                    if (OccupancyMap.get(i).get(j)){
                        valid = false; 
                        break; 
                    }
                }
            }else{
                int j = (int) a.j; 
                for (int i = (int)Math.min(a.i, b.i); i <= (int)Math.max(a.i, b.i); i++){
                    if (OccupancyMap.get(i).get(j)){
                        valid = false; 
                        break; 
                    }
                }
            }
        }
        return valid; 
    }

    private boolean judgeLine(){
        double discriminant = (start.i - end.i) * (start.j - end.j);
        if (discriminant == 0 && isValidLine(start, end)){
            this.Nodes.add(this.start); 
            this.Nodes.add(this.end); 
            return true; 
        }
        return false; 
    }

    private boolean judgeLShape(){
        Position a = start; 
        Position b = end;
        // if (start.j > end.j){
        //     a = end; 
        //     b = start; 
        // } 

        Position turningPoint; 
        turningPoint = new Position(b.i, a.j); 
        if (isValidLine(a, turningPoint) && isValidLine(turningPoint, b)){
            this.Nodes.add(a); 
            this.Nodes.add(turningPoint); 
            this.Nodes.add(b); 
            return true; 
        }
        turningPoint = new Position(a.i, b.j); 
        if (isValidLine(a, turningPoint) && isValidLine(turningPoint, b)){
            this.Nodes.add(a); 
            this.Nodes.add(turningPoint); 
            this.Nodes.add(b); 
            return true; 
        }
        return false; 
    }

    private List<Boolean> ORAlongAxis(List<List<Boolean>> oMap, int axis){
        assert axis == 0 || axis == 1; 
        List<Boolean> result = new ArrayList<Boolean>(); 
        if (axis == 1){
            for (List<Boolean> row: oMap){
                boolean value = false; 
                for (boolean o: row){
                    if (o == true){
                        value = true; 
                        break; 
                    }
                }
                result.add(value); 
            }
        }else if (axis == 0){
            int height = oMap.size(); 
            int width = oMap.get(0).size(); 
            for (int j = 0; j < width; j++){
                boolean value = false; 
                for (int i = 0; i < height; i++){
                    if (oMap.get(i).get(j) == true){
                        value = true; 
                        break; 
                    }
                }
                result.add(value); 
            }
        }
        return result; 
    }

    private boolean judgeZShape(){
        // search along axis 1
        int startIdx = (int) ((start.j + end.j) / 2); 
        int maxOffset = (int) (Math.abs(start.j - end.j) / 2) - (int)(nearbyThreshold * 1.5); 
        int offset = 0; 
        List<Boolean> oMap = ORAlongAxis(OccupancyMap, 0); 
        int resultIdx = -1; 
        Position mid1 = null; 
        Position mid2 = null; 
        while (offset < maxOffset && resultIdx < 0){
            for (int sign = -1; sign <= 1; sign+=2){
                int index = startIdx + offset * sign;
                if (oMap.get(index) == false){
                    mid1 = new Position(start.i, index); 
                    mid2 = new Position(end.i, index); 
                    if (isValidLine(start, mid1) && isValidLine(mid1, mid2) && isValidLine(mid2, end)){
                        // found it
                        resultIdx = index; 
                        break; 
                    }
                }
            }
            offset ++; 
        }
        if (resultIdx >= 0){
            Nodes.add(start); 
            Nodes.add(mid1); 
            Nodes.add(mid2); 
            Nodes.add(end); 
            return true; 
        }
        // search along axis 0
        startIdx = (int) (Math.abs(start.i + end.i) / 2); 
        maxOffset = (int) (Math.abs(start.i - end.i) / 2) - (int)(nearbyThreshold * 1.5); 
        offset = 0; 
        oMap = ORAlongAxis(OccupancyMap, 1); 
        resultIdx = -1; 
        while (offset < maxOffset && resultIdx < 0){
            for (int i = -1; i <= 1; i+=2){
                int index = startIdx + offset * i; 
                if (oMap.get(index) == false){
                    mid1 = new Position(index, start.j); 
                    mid2 = new Position(index, end.j); 
                    if (isValidLine(start, mid1) && isValidLine(mid1, mid2) && isValidLine(mid2, end)){
                        // found it
                        resultIdx = index; 
                        break; 
                    }
                }
            }
            offset ++; 
        }
        if (resultIdx >= 0){
            Nodes.add(start); 
            Nodes.add(mid1); 
            Nodes.add(mid2); 
            Nodes.add(end); 
            return true; 
        }
        return false; 
    }

    private void judgeLineIntersectedWithObstacle(Position a, Position b, List<Obstacle> obstacles){
        double discriminant = (start.i - end.i) * (start.j - end.j); 
        assert discriminant == 0; 
        
        if (start.i == end.i){
            int i = (int) start.i; 
            boolean isRecordingIntersection = false; 
            List<Position> ObstacleStartEndPair = null; 
            Obstacle currentObstacle = null; 
            for (int j = (int)Math.min(start.j, end.j); j <= (int)Math.max(start.j, end.j); j++){
                for (Obstacle obs: obstacles){
                    if (obs.getPositions().get(i).get(j)){
                        if (currentObstacle == null && !isRecordingIntersection){
                            isRecordingIntersection = true; 
                            currentObstacle = obs; 
                            ObstacleStartEndPair = new ArrayList<Position>(); 
                            ObstacleStartEndPair.add(new Position(i, j)); 
                            break; 
                        }else if (currentObstacle != null && currentObstacle != obs){
                            ObstacleStartEndPair.add(new Position(i, j)); 
                            assert (ObstacleStartEndPair.size() == 2); 
                            obsticleEndPoints.put(ObstacleStartEndPair, currentObstacle); 
                            ObstacleStartEndPair = new ArrayList<Position>(); 
                            ObstacleStartEndPair.add(new Position(i, j)); 
                            currentObstacle = obs; 
                        }
                    }else{
                        if (isRecordingIntersection){
                            isRecordingIntersection = false; 
                            ObstacleStartEndPair.add(new Position(i, j)); 
                            assert (ObstacleStartEndPair.size() == 2); 
                            obsticleEndPoints.put(ObstacleStartEndPair, currentObstacle); 
                            ObstacleStartEndPair = null; 
                            currentObstacle = null; 
                        }
                    }
                }
            }
            if (isRecordingIntersection){
                isRecordingIntersection = false; 
                ObstacleStartEndPair.add(new Position(i, (int)Math.max(start.j, end.j))); 
                assert (ObstacleStartEndPair.size() == 2); 
                obsticleEndPoints.put(ObstacleStartEndPair, currentObstacle); 
                ObstacleStartEndPair = null; 
                currentObstacle = null; 
            }
        }else{
            int j = (int) start.j; 
            boolean isRecordingIntersection = false; 
            List<Position> ObstacleStartEndPair = null; 
            Obstacle currentObstacle = null; 
            for (int i = (int)Math.min(start.i, end.i); i <= (int)Math.max(start.i, end.i); i++){
                for (Obstacle obs: obstacles){
                    if (obs.getPositions().get(i).get(j)){
                        if (currentObstacle == null && !isRecordingIntersection){
                            isRecordingIntersection = true; 
                            currentObstacle = obs; 
                            ObstacleStartEndPair = new ArrayList<Position>(); 
                            ObstacleStartEndPair.add(new Position(i, j)); 
                            break; 
                        }else if (currentObstacle != null && currentObstacle != obs){
                            ObstacleStartEndPair.add(new Position(i, j)); 
                            assert (ObstacleStartEndPair.size() == 2); 
                            obsticleEndPoints.put(ObstacleStartEndPair, currentObstacle); 
                            ObstacleStartEndPair = new ArrayList<Position>(); 
                            ObstacleStartEndPair.add(new Position(i, j)); 
                            currentObstacle = obs; 
                        }
                    }else{
                        if (isRecordingIntersection){
                            isRecordingIntersection = false; 
                            ObstacleStartEndPair.add(new Position(i, j)); 
                            assert (ObstacleStartEndPair.size() == 2); 
                            obsticleEndPoints.put(ObstacleStartEndPair, currentObstacle); 
                            ObstacleStartEndPair = null; 
                            currentObstacle = null; 
                        }
                    }
                }
            }
            if (isRecordingIntersection){
                isRecordingIntersection = false; 
                ObstacleStartEndPair.add(new Position((int)Math.max(start.i, end.i), j)); 
                assert (ObstacleStartEndPair.size() == 2); 
                obsticleEndPoints.put(ObstacleStartEndPair, currentObstacle); 
                ObstacleStartEndPair = null; 
                currentObstacle = null; 
            }
        } 
    }

    private void judgeObstacles(List<Obstacle> obstacles){
        if (obstacles.size() == 0){
            this.isIntersectedWithObstacle = false; 
            return;
        }
        for (int i = 0; i < Nodes.size()-1; i++){
            judgeLineIntersectedWithObstacle(Nodes.get(i), Nodes.get(i+1), obstacles); 
        }
    }

    public boolean isValidRailway(){
        return isValid; 
    }

    public boolean isIntersectedWithObstacle(){
        return isIntersectedWithObstacle;
    }

    @Override
    public double parsePositionToAbstractPosition(Position p) {
        return (Math.abs(p.i-start.i) + Math.abs(p.j-start.j));
    }

    @Override
    public Position parseAbstractPositionToPosition(double abstractPosition) {
        double l_i = 0;
        double l_j = 0;
        for (int i = 1; i < Nodes.size(); i++) {
            l_i = Math.abs(Nodes.get(i).i-Nodes.get(i-1).i);
            l_j = Math.abs(Nodes.get(i).j-Nodes.get(i-1).j);
            if (abstractPosition > (l_i+l_j)) {
                abstractPosition -= (l_i+l_j);
            } else {
                if (l_i == 0.0) {
                    return new Position(Nodes.get(i-1).i, Nodes.get(i-1).j+(abstractPosition*(Nodes.get(i).j-Nodes.get(i-1).j)/l_j));
                } else {
                    return new Position(Nodes.get(i-1).i+(abstractPosition*(Nodes.get(i).i-Nodes.get(i-1).i)/l_i), Nodes.get(i-1).j);
                }
            }
        }
        if (l_i == 0.0) {
            return new Position(Nodes.get(Nodes.size()-2).i, Nodes.get(Nodes.size()-2).j+(abstractPosition*(Nodes.get(Nodes.size()-1).j-Nodes.get(Nodes.size()-2).j)/l_j));
        } else {
            return new Position(Nodes.get(Nodes.size()-2).i+(abstractPosition*(Nodes.get(Nodes.size()-1).i-Nodes.get(Nodes.size()-2).i)/l_i), Nodes.get(Nodes.size()-2).j);
        }
        // System.out.println(abstractPosition);
        // return null;
    }
    
}
