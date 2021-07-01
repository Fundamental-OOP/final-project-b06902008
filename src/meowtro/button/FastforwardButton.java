package meowtro.button;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import meowtro.PlayTime;

public class FastforwardButton extends MyButton {
    public FastforwardButton(int cost, String iconPath) {
        this.cost = cost;
        this.btn = new Button();
        try {
            ImageView image = new ImageView(new Image(new FileInputStream(iconPath)));
            image.setFitHeight(30);
            image.setFitWidth(30);
            btn.setGraphic(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        btn.setLayoutX(400);
        btn.setLayoutY(300);
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
        if (!PlayTime.isFF) {
            PlayTime.duration_animate /= 8;
            PlayTime.isFF = true;
        }
    }

}
