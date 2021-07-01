package meowtro.button;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import meowtro.game.Game;
import meowtro.game.entityManager.RailwayManager;
import meowtro.game.onClickEvent.RailwayBuilder;
import meowtro.metro_system.railway.Line;

public class RailwayButton extends MyButton {
    private Game game;
    private RailwayManager railwayManager;
    private Line line;

    public RailwayButton(int cost, Game game, RailwayManager railwayManager, Line line, String iconPath) {
        this.cost = cost;
        this.btn = new Button();
        this.game = game;
        this.railwayManager = railwayManager;
        this.line = line;
        try {
            ImageView image = new ImageView(new Image(new FileInputStream(iconPath)));
            image.setFitHeight(30);
            image.setFitWidth(30);
            btn.setGraphic(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        btn.setLayoutX(100);
        btn.setLayoutY(150);
        btn.setOnAction(
            new EventHandler<ActionEvent>() {    
                @Override
                public void handle(ActionEvent event) {
                    onClick();
                }
            }
        );
    }
    public void onClick() {
        RailwayBuilder b = new RailwayBuilder(this.railwayManager, this.game, this.line, this.cost);
        this.game.setNowEvent(b);
    }
}
