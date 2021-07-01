package meowtro.button;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import meowtro.game.Game;
import meowtro.game.entityManager.CarManager;
import meowtro.game.onClickEvent.CarBuilder;

public class CarButton extends MyButton {
    private Game game;
    private CarManager carManager;

    public CarButton(int cost, Game game, CarManager carManager, String iconPath) {
        this.cost = cost;
        this.btn = new Button();
        this.game = game;
        this.carManager = carManager;
        try {
            ImageView image = new ImageView(new Image(new FileInputStream(iconPath)));
            image.setFitHeight(30);
            image.setFitWidth(30);
            btn.setGraphic(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        btn.setLayoutX(100);
        btn.setLayoutY(250);
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
        CarBuilder b = new CarBuilder(this.carManager, this.game, this.cost);
        this.game.setNowEvent(b);
    }
}
