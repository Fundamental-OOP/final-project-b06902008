package meowtro.game.obstacle;

import java.util.List;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import meowtro.Position;
import meowtro.game.Game;

import java.io.File;
import java.io.IOException;

public class River extends Obstacle {

    final static public Color color = new Color(
        Integer.parseInt(Game.getConfig().get("obstacle.river.rgb").split("\\.", 0)[0]),
        Integer.parseInt(Game.getConfig().get("obstacle.river.rgb").split("\\.", 0)[1]),
        Integer.parseInt(Game.getConfig().get("obstacle.river.rgb").split("\\.", 0)[2])
    );

    public River(BufferedImage background, Color color) {
        super(background, color);
        if (Game.DEBUG) {
            System.out.println("River constructed");
        }
    }

    public River(List<List<Boolean>> positions) {
        super(positions);
        if (Game.DEBUG) {
            System.out.println("River constructed");
        }
    }

    public int getAdditionalCost() {
        return Integer.parseInt(Game.getConfig().get("price.bridge"));
    }

    /****** MAIN ******/
    public static void main(String[] args) {
        // read image
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("image/map_1.png"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // test River
        String[] rgbStr = Game.getConfig().get("obstacle.river.rgb").split("\\.", 0);
        Color color = new Color(Integer.parseInt(rgbStr[0]), Integer.parseInt(rgbStr[1]), Integer.parseInt(rgbStr[2]));
        River river = new River(image, color);
        System.out.println("(200, 1000)[Green]: " + river.isBlocked(new Position(200, 1000)));
        System.out.println("(300, 1200)[Gray]: " + river.isBlocked(new Position(300, 1200)));
        System.out.println("(400, 900)[Blue]: " + river.isBlocked(new Position(400, 900)));
        System.out.println("(400, 700)[White]: " + river.isBlocked(new Position(400, 700)));
        System.out.println("(210, 576)[Border]: " + river.isBlocked(new Position(210, 576)));
    }
    
}
