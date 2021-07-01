package meowtro.button;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PauseButton extends MyButton {
    private AnimationTimer timer;

    public PauseButton(int cost, String iconPath, AnimationTimer timer) {
        this.cost = cost;
        this.btn = new Button();
        this.timer = timer;
        try {
            ImageView image = new ImageView(new Image(new FileInputStream(iconPath)));
            image.setFitHeight(30);
            image.setFitWidth(30);
            btn.setGraphic(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        btn.setLayoutX(400);
        btn.setLayoutY(100);
        btn.setPrefSize(30, 30);
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
        this.timer.stop();
    }

}
