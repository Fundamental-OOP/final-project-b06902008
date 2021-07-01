import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main_outline extends Application {
    private Pane root;
    private List<Button> buttons;
    private AnimationTimer timer;
    private AnimationTimer innerTimer;
    private long formerTimeStamp_cmd;
    private long duration_cmd = 999999;
    private long formerTimeStamp_animate;
    private long duration_animate = 999999;
    
    @Override
    public void start(Stage primaryStage) {

        this.root = new Pane();
        root.setOnMouseClicked(
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.printf("(%f,%f)", event.getSceneX(), event.getSceneY());
                }
            }
        );
        
        for (int i = 0; i < this.buttons.size(); i++) {
            this.buttons.get(i).setLayoutX(100);
            this.buttons.get(i).setLayoutY(i*50);
            this.buttons.get(i).setOnAction(
                new EventHandler<ActionEvent>() {    
                    @Override
                    public void handle(ActionEvent event) {
                        // do things
                    }
                }
            );
            this.root.getChildren().add(this.buttons.get(i));
        }

        Button pause = new Button();
        pause.setText("Pause");
        pause.setLayoutX(100);
        pause.setLayoutY(200);
        pause.setOnAction(
            new EventHandler<ActionEvent>() {    
                @Override
                public void handle(ActionEvent event) {
                    innerTimer.stop();
                }
            }
        );
        root.getChildren().add(pause);

        Button play = new Button();
        play.setText("Play");
        play.setLayoutX(100);
        play.setLayoutY(300);
        play.setOnAction(
            new EventHandler<ActionEvent>() {    
                @Override
                public void handle(ActionEvent event) {
                    innerTimer.start();
                }
            }
        );
        root.getChildren().add(play);

        Scene scene = new Scene(root, 1920, 1080);
        primaryStage.setTitle("MEwoTRO");
        primaryStage.setScene(scene);
        primaryStage.show();

        this.timer = new AnimationTimer() {
            @Override public void handle(long currentNanoTime) {
                System.out.printf("Command Loop Time: %d %n", currentNanoTime);
                // implement timer
                if (currentNanoTime-formerTimeStamp_cmd > duration_cmd) {
                    // conduct command
                    // static refresh
                    formerTimeStamp_cmd = currentNanoTime;
                }
            }
        };
        this.timer.start();
        
        this.innerTimer = new AnimationTimer() {
            @Override public void handle(long currentNanoTime) {
                System.out.printf("Animation Loop Time: %d %n", currentNanoTime);
                // implement timer
                if (currentNanoTime-formerTimeStamp_animate > duration_animate) {
                    // conduct command
                    // static refresh
                    formerTimeStamp_animate = currentNanoTime;
                }
            }
        };
        this.innerTimer.start();
    }
    
    public static void main(String[] args) {
	    launch(args);
    }
}
