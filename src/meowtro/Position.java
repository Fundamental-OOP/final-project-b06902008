package meowtro;

public class Position {
    
    public double i = 0;
    public double j = 0;
    
    public Position(int i, int j) {
        this.i = i;
        this.j = j;
    }
    public Position(double i, double j) {
        this.i = i;
        this.j = j;
    }

    public double l2distance(Position other) {
        return Math.sqrt((this.i - other.i) * (this.i - other.i) + (this.j - other.j) * (this.j - other.j));
    }

    public String toString(){
        return String.format("(%.1f, %.1f)", i, j); 
    }

}
