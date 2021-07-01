package meowtro.metro_system;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

import meowtro.Position;
import meowtro.game.Game;
import meowtro.metro_system.railway.Line;
import meowtro.metro_system.railway.LineColor;
import meowtro.metro_system.railway.Railway;
import meowtro.metro_system.station.Station;


public class ShortestPathCalculator{
    public static int findShortestPath(Station src, Station dst){
        int inf = Integer.MAX_VALUE;
        
        LinkedList<Station> stationsToExplore = new LinkedList<Station>(); 
        LinkedList<Station> stationsExploring = new LinkedList<Station>(); 
        HashSet<Station> stationsExplored = new HashSet<Station>(); 


        // initiallize
        stationsToExplore.add(src); 

        // BFS start
        int dist = 0; 
        while (stationsToExplore.size() > 0){

            // load stations in current level(level num = dist)
            while (stationsToExplore.size() > 0){
                stationsExploring.add(stationsToExplore.removeFirst()); 
            }

            // check whether dst is in this level
            while (stationsExploring.size() > 0){
                Station s = stationsExploring.removeFirst(); 
                if (s == dst){
                    return dist; 
                }
                
                stationsToExplore.addAll(s.getAdjacents()
                                            .stream()
                                            .filter(station -> !stationsExplored.contains(station))
                                            .collect(Collectors.toList())); 
                stationsExplored.add(s); 
            }
            dist += 1; 
        }

        return inf; 
    }


    // public static void main(String[] args){
    //     Game.setToyConfig();
    //     Line l = new Line(null, LineColor.RED); 

    //     Station s1 = new Station(null , new Position(1, 0)); 
    //     Station s2 = new Station(null , new Position(2, 0)); 
    //     Station s3 = new Station(null , new Position(3, 0)); 
    //     Station s4 = new Station(null , new Position(4, 0)); 

    //     Railway r1 = new Railway(s1, s2, l); 
    //     Railway r2 = new Railway(s3, s1, l); 
    //     Railway r3 = new Railway(s4, s2, l); 

    //     // s3 - s1 - s2 - s4

    //     System.out.println(l); 
    //     System.out.println(ShortestPathCalculator.findShortestPath(s3, s4));  // 3
    // }
}