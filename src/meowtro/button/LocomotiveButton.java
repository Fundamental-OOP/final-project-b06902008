package meowtro.button;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import meowtro.game.Game;
import meowtro.game.entityManager.LocomotiveManager;
import meowtro.game.onClickEvent.LocomotiveBuilder;

public class LocomotiveButton extends MyButton {
    private Game game;
    private LocomotiveManager locomotiveManager;

    public LocomotiveButton(int cost, Game game, LocomotiveManager locomotiveManager, String iconPath) {
        this.cost = cost;
        this.btn = new Button();
        this.game = game;
        this.locomotiveManager = locomotiveManager;
        try {
            ImageView image = new ImageView(new Image(new FileInputStream(iconPath)));
            image.setFitHeight(30);
            image.setFitWidth(30);
            btn.setGraphic(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        btn.setLayoutX(100);
        btn.setLayoutY(200);
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
        LocomotiveBuilder b = new LocomotiveBuilder(this.locomotiveManager, this.game, this.cost);
        this.game.setNowEvent(b);
    }
}
