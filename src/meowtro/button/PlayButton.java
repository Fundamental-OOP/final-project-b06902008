package meowtro.button;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import meowtro.PlayTime;

public class PlayButton extends MyButton {
    private AnimationTimer timer;

    public PlayButton(int cost, String iconPath, AnimationTimer timer) {
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
        btn.setLayoutY(200);
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
        this.timer.start();
        if (PlayTime.isFF) {
            PlayTime.duration_animate *= 8;
            PlayTime.isFF = false;
        }
    }

}
