import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import meowtro.PlayTime;
import meowtro.Position;
import meowtro.button.CarButton;
import meowtro.button.DestroyButton;
import meowtro.button.FastforwardButton;
import meowtro.button.LocomotiveButton;
import meowtro.button.MyButton;
import meowtro.button.PauseButton;
import meowtro.button.PlayButton;
import meowtro.button.RailwayButton;
import meowtro.button.StationButton;
import meowtro.button.UpgradeButton;
import meowtro.game.Config;
import meowtro.game.Game;
import meowtro.game.GameFactory;
import meowtro.game.Region;
import meowtro.game.entityManager.CarManager;
import meowtro.game.entityManager.LocomotiveManager;
import meowtro.game.entityManager.RailwayManager;
import meowtro.game.entityManager.StationManager;
import meowtro.game.passenger.Passenger;
import meowtro.metro_system.railway.Line;
import meowtro.metro_system.railway.LineColor;
import meowtro.metro_system.railway.Railway;
import meowtro.metro_system.station.Station;
import meowtro.metro_system.train.Car;
import meowtro.metro_system.train.Locomotive;

public class Main_old extends Application {
    private Pane root;
    private List<MyButton> buttons;
    private AnimationTimer timer;
    private AnimationTimer innerTimer;
    private long formerTimeStamp_cmd;
    private long duration_cmd = 99;
    private long formerTimeStamp_animate;
    private long duration_animate;
    
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        GameFactory gameFactory = new GameFactory();
        Config config = new Config("./resources/defaultConfig.properties", "./resources/localconfig/localConfig1.properties");
        Game game = gameFactory.createGame(config);
        
        StationManager sm = new StationManager(game);
        RailwayManager rm = new RailwayManager(game);
        LocomotiveManager lm = new LocomotiveManager(game);
        CarManager cm = new CarManager(game);
        
        game.start(sm);

        this.root = new Pane();
        root.setOnMouseClicked(
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    game.onClick(new Position((int) Math.round(event.getSceneY()), (int) Math.round(event.getSceneX())));
                }
            }
        );

        root.getChildren().add(new ImageView(new Image(new FileInputStream("./image/map_1.png"))));

        ProgressBar satisfactionBar = new ProgressBar(0);
        root.getChildren().add(satisfactionBar);

        StationButton stationButton = new StationButton(10, game, sm, "./image/button/station.png");
        root.getChildren().add(stationButton.getButton());
        
        DestroyButton destroyButton = new DestroyButton(0, game, "./image/button/remove.png");
        root.getChildren().add(destroyButton.getButton());
        
        // TEMPORARILY add a new line after game start
        Line line = new Line(game.getCity(), LineColor.BLUE);
        RailwayButton railwayButton = new RailwayButton(2, game, rm, line, "./image/button/railway.png");
        root.getChildren().add(railwayButton.getButton());

        LocomotiveButton locomotiveButton = new LocomotiveButton(2, game, lm, "./image/button/locomotive.png");
        root.getChildren().add(locomotiveButton.getButton());

        CarButton carButton = new CarButton(1, game, cm, "./image/button/car.png");
        root.getChildren().add(carButton.getButton());

        UpgradeButton upgradeButton = new UpgradeButton(0, game, "./image/button/levelup.png");
        root.getChildren().add(upgradeButton.getButton());

        this.timer = new AnimationTimer() {
            @Override public void handle(long currentNanoTime) {
                duration_animate = PlayTime.duration_animate;
                // implement timer
                if (currentNanoTime-formerTimeStamp_cmd > duration_cmd) {
                    // conduct command
                    for (Region region : game.getCity().getRegions()) {
                        for (Station station : region.getStations()) {
                            if (!root.getChildren().contains(station.getImage())) {
                                root.getChildren().add(station.getImage());
                            }
                        }
                    }
                    for (Line line : game.getCity().getAllLines()) {
                        for (Railway railway : line.getRailways()) {
                            if (!root.getChildren().contains(railway.getImage())) {
                                root.getChildren().add(railway.getImage());
                            }
                            for (Node obstacle : railway.getObstacleImages()) {
                                if (!root.getChildren().contains(obstacle)) {
                                    root.getChildren().add(obstacle);
                                }
                            }
                        }
                    }
                    for (Line line : game.getCity().getAllLines()) {
                        for (Locomotive locomotive : line.getLocomotives()) {
                            if (!root.getChildren().contains(locomotive.getImage())) {
                                root.getChildren().add(locomotive.getImage());
                            }
                            for (Car car : locomotive.getCars()) {
                                if (!root.getChildren().contains(car.getImage())) {
                                    root.getChildren().add(car.getImage());
                                }
                            }
                        }
                    }

                    if (Game.textMessage.size() > 0) {
                        if (root.getChildren().contains(Game.textMessage.peek())) {
                            root.getChildren().remove(Game.textMessage.poll());
                        }
                        if (Game.textMessage.size() > 0) {
                            root.getChildren().add(Game.textMessage.peek());
                        }
                    }

                    satisfactionBar.setProgress(Game.satisfactionBarRate);

                    for (Object o : game.getObjectToBeRemoved()) {
                        if (root.getChildren().contains(o)) {
                            root.getChildren().remove(o);
                        }
                    }
                    game.resetObjectToBeRemoved();
                    // static refresh
                    formerTimeStamp_cmd = currentNanoTime;
                }
            }
        };
        this.timer.start();
        
        this.innerTimer = new AnimationTimer() {
            @Override public void handle(long currentNanoTime) {
                // implement timer
                if (currentNanoTime-formerTimeStamp_animate > duration_animate) {
                    // conduct command
                    game.update();
                    for (Region region : game.getCity().getRegions()) {
                        for (Passenger passenger : region.getPassengers()) {
                            if (!root.getChildren().contains(passenger.getImage())) {
                                root.getChildren().add(passenger.getImage());
                            }
                        }
                    }
                    // animate refresh
                    formerTimeStamp_animate = currentNanoTime;
                }
            }
        };
        this.innerTimer.start();

        PauseButton pauseButton = new PauseButton(0, "./image/button/pause.png", this.innerTimer);
        root.getChildren().add(pauseButton.getButton());

        PlayButton playButton = new PlayButton(0, "./image/button/play.png", this.innerTimer);
        root.getChildren().add(playButton.getButton());

        FastforwardButton ffButton = new FastforwardButton(0, "./image/button/fast_forward.png");
        root.getChildren().add(ffButton.getButton());

        Scene scene = new Scene(root, 640, 480);
        primaryStage.setTitle("MEwoTRO");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {        
        launch(args);
    }
}
