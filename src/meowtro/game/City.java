package meowtro.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.awt.Color;
import javax.imageio.ImageIO;

import meowtro.Position;
import meowtro.game.obstacle.Obstacle;
import meowtro.metro_system.*;
import meowtro.metro_system.railway.Line;
import meowtro.metro_system.station.Station;

public class City {
    
    private Game game;
    public Game getGame() {
        return this.game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    private List<Region> regions = new ArrayList<Region>();
    private List<Obstacle> obstacles = new ArrayList<Obstacle>();
    private List<Line> lines = new ArrayList<Line>();
    private int totalTransportedPassengerCount = 0;
    private static int width = 0;
    private static int height = 0;
    
    public City() {

        // read image
        BufferedImage background = null;
        try {
            background = ImageIO.read(new File(Game.getConfig().get("image.path")));
            this.width = background.getWidth();
            this.height = background.getHeight();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // iterate the whole image to extract pixels of the same color
        Hashtable<Color, List<Position>> color2pixels = new Hashtable<Color, List<Position>>();
        for (int i = 0; i < background.getHeight(); i++) {
            for (int j = 0; j < background.getWidth(); j++) {
                int pixel = background.getRGB(j, i);
                Color pixelColor = new Color(pixel);
                if (!color2pixels.containsKey(pixelColor))
                    color2pixels.put(pixelColor, new ArrayList<Position>());
                color2pixels.get(pixelColor).add(new Position(i, j));
            }
        }

        // obstacle colors
        Hashtable<Color, Class<? extends Obstacle>> color2obstacle = new Hashtable<Color, Class<? extends Obstacle>>();
        for (String key: Game.getConfig().getAllKeys()) {
            if (key.startsWith("obstacle.") && key.endsWith(".rgb")) {
                // get color for the obstacle
                String[] rgbStr = Game.getConfig().get(key).split("\\.", 0);
                Color color = new Color(Integer.parseInt(rgbStr[0]), Integer.parseInt(rgbStr[1]), Integer.parseInt(rgbStr[2]));
                // get obstacle name
                String obstacleName = key.split("\\.", 0)[1];
                obstacleName = this.getClass().getPackage().getName() + ".obstacle." + obstacleName.substring(0, 1).toUpperCase() + obstacleName.substring(1);
                // add <color, obstacle class> to color2obstacle
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends Obstacle> obstacleClass = (Class<? extends Obstacle>)Class.forName(obstacleName);
                    color2obstacle.put(color, obstacleClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // iterate every color
        Iterator<Entry<Color, List<Position>>> iter = color2pixels.entrySet().iterator();
        double areaThreshold = Double.parseDouble(Game.getConfig().get("region.background.ratio.threshold")) * background.getWidth() * background.getHeight();
        while (iter.hasNext()) {
            Entry<Color, List<Position>> colorPixelsPair = iter.next();
            // skip colors with too less pixels (edges)
            if (colorPixelsPair.getValue().size() < areaThreshold)
                continue;
            
            // construct obstacle or region
            List<List<Boolean>> positions = this.positionList2Boolean2DList(colorPixelsPair.getValue(), background.getWidth(), background.getHeight());

            if (color2obstacle.containsKey(colorPixelsPair.getKey())) {
                // construct obstacle
                Class<? extends Obstacle> obstacleClass = color2obstacle.get(colorPixelsPair.getKey());
                try {
                    this.obstacles.add(obstacleClass.getConstructor(List.class).newInstance(positions));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }         

            else {
                // construct region
                this.regions.add(new Region(positions, this));
            }
        }

        if (Game.DEBUG)
            System.out.println(String.format("City constructed (%d obstacles, %d regions)", this.obstacles.size(), this.regions.size()));
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    private List<List<Boolean>> positionList2Boolean2DList(List<Position> positionsList, int width, int height) {
        // initialize boolean 2d list
        List<List<Boolean>> positions = new ArrayList<List<Boolean>>();
        for (int i = 0; i < height; i++) {
            positions.add(new ArrayList<Boolean>());
            for (int j = 0; j < width; j++) {
                positions.get(i).add(false);
            }
        }
        // set positions in positionsList to true
        for (Position position: positionsList)
            positions.get((int) Math.round(position.i)).set((int) Math.round(position.j), true);
        
        return positions;
    }

    public List<Region> getRegions() {
        return this.regions;
    }

    public Region getRegionByPosition(Position position) {
        for (Region region: this.regions) {
            if (region.containPosition(position))
                return region;
        }
        return null;
    }

    public Station getRandomStationFromDifferentRegion(Region region) {
        List<Region> regionsCopy = new ArrayList<Region>(this.regions);
        Collections.shuffle(regionsCopy);
        for (Region r: regionsCopy) {
            if (r == region)
                continue;
            if (r.getStations().size() != 0) {
                List<Station> stations = r.getStations();
                return stations.get(Game.randomGenerator.nextInt(stations.size()));
            }
        }
        // return null if no other regions have station
        return null;
    }

    public List<Region> getNRandomRegions(int n) {
        assert this.regions.size() >= n;
        List<Region> toShuffle = new ArrayList<Region>(this.regions);
        Collections.shuffle(toShuffle);
        return toShuffle.subList(0, n);
    }

    public void addTotalTransportedPassengerCount() {
        this.totalTransportedPassengerCount += 1;
    }

    public void addLine(Line newLine) {
        if (!this.lines.contains(newLine)) {
            this.lines.add(newLine);
        }
    }
    
    public List<Line> getAllLines(){
        return this.lines;
    }

    public void removeLine(Line line) {
        this.lines.remove(line);
    }

    public void removeStation(Station station) {
        Region stationRegion = this.getRegionByPosition(station.getPosition());
        stationRegion.removeStation(station);
        this.game.deleteObject(station.getImage());
    }

    public List<Station> getAllStation() {
        ArrayList<Station> result = new ArrayList<Station>(); 
        for (Region r: regions){
            result.addAll(r.getStations()); 
        }
        return result; 
    }

    public List<Obstacle> getObstacles() {
        return this.obstacles;
    }

    public List<Obstacle> blockedBy(Station station1, Station station2) {
        // TODO: blocked by
        return null;
    }

    public int getGlobalSatisfaction() {
        // compute the average of all region satisfactions
        int totalSatisfaction = 0;
        for (Region region: this.regions)
            totalSatisfaction += region.getRegionSatisfaction();
        int globalSatisfaction = (int)(Math.round(totalSatisfaction / this.regions.size()));
        
        if (Game.DEBUG)
            System.out.println("GlobalSatisfaction: = " + globalSatisfaction);
        return globalSatisfaction;
    }

    public void update() {
        for (Region region: this.regions)
            region.update();
            
        for (Line line: this.lines)
            line.update();
    }

    /****** MAIN ******/
    public static void main(String[] args) {

    }

}
