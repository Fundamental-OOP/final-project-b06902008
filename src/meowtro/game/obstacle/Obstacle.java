package meowtro.game.obstacle;

import java.util.ArrayList;
import java.util.List;

import meowtro.Position;

import java.awt.image.BufferedImage;
import java.awt.Color;

public abstract class Obstacle {
    
    protected List<List<Boolean>> positions = new ArrayList<List<Boolean>>();
    
    public Obstacle(BufferedImage background, Color color) {
        // iterate all pixels, check if match color
        for (int r = 0; r < background.getHeight(); r++) {
            this.positions.add(new ArrayList<Boolean>());
            for (int c = 0; c < background.getWidth(); c++) {
                int pixel = background.getRGB(c, r);
                Color pixelColor = new Color(pixel);
                this.positions.get(r).add(color.equals(pixelColor)? true : false);
            }
        }
    }

    public Obstacle(List<List<Boolean>> positions) {
        this.positions = positions;
    }

    public int getAdditionalCost() {return 0;}

    public Boolean isBlocked(Position position) {
        int roundedI = (int) Math.round(position.i);
        int roundedJ = (int) Math.round(position.j);
        return this.positions.get(roundedI).get(roundedJ);
    }

    public List<List<Boolean>> getPositions(){
        return positions; 
    }
    
}
