package meowtro.metro_system.railway;

import meowtro.Position;

public interface RailwayRealizer {
    public double parsePositionToAbstractPosition(Position p); 
    public Position parseAbstractPositionToPosition(double abstractPosition); 
}
