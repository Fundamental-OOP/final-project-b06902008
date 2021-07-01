package meowtro.button;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import meowtro.game.Game;
import meowtro.game.onClickEvent.Destroyer;

public class DestroyButton extends MyButton {
    private Game game;

    public DestroyButton(int cost, Game game, String iconPath) {
        this.cost = cost;
        this.btn = new Button();
        this.game = game;
        try {
            ImageView image = new ImageView(new Image(new FileInputStream(iconPath)));
            image.setFitHeight(30);
            image.setFitWidth(30);
            btn.setGraphic(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        btn.setLayoutX(100);
        btn.setLayoutY(100);
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
        Destroyer d = new Destroyer(this.game);
        game.setNowEvent(d);
    }
}
