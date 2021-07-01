package meowtro.button;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import meowtro.game.Game;
import meowtro.game.entityManager.StationManager;
import meowtro.game.onClickEvent.StationBuilder;

public class StationButton extends MyButton {
    private Game game;
    private StationManager stationManager;

    public StationButton(int cost, Game game, StationManager stationManager, String iconPath) {
        this.cost = cost;
        this.btn = new Button();
        this.game = game;
        this.stationManager = stationManager;
        try {
            ImageView image = new ImageView(new Image(new FileInputStream(iconPath)));
            image.setFitHeight(30);
            image.setFitWidth(30);
            btn.setGraphic(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        btn.setLayoutX(100);
        btn.setLayoutY(50);
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
        StationBuilder b = new StationBuilder(this.stationManager, this.game, this.cost);
        this.game.setNowEvent(b);
    }

}
