package meowtro.game.passenger;

import meowtro.Position;
import meowtro.game.Region;
import meowtro.metro_system.station.Station;

public class CutInLineElder extends Passenger {
    
    public CutInLineElder(Region birthRegion, Position position, Station destinationStation) {
        super(birthRegion, position, destinationStation);
    }

    @Override
    public void enterStation(Station station) {
        this.position = station.getPosition();
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
            station.insertPassenger(this, 0);
        }
    }

    @Override
    public String toString() {
        return String.format("C%d", this.index);
    }
}
