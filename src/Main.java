import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import meowtro.game.Config;
import meowtro.game.Game;
import meowtro.game.GameFactory;
import meowtro.game.entityManager.CarManager;
import meowtro.game.entityManager.LocomotiveManager;
import meowtro.game.entityManager.RailwayManager;
import meowtro.game.entityManager.StationManager;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameFactory gameFactory = new GameFactory();
        Config config = new Config("./resources/defaultConfig.properties", "./resources/localconfig/localConfig1.properties");
        Game game = gameFactory.createGame(config);
        
        StationManager statoionManager = new StationManager(game);
        RailwayManager railwayManager = new RailwayManager(game);
        LocomotiveManager locomotiveManager = new LocomotiveManager(game);
        CarManager carManager = new CarManager(game);
        
        


        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gameFrame.fxml"));
            // fxmlLoader.setController(new GameFrameController());
            
            Parent root = (Parent) fxmlLoader.load();

            GameFrameController controller = fxmlLoader.getController();
            Scene scene = new Scene(root);
            controller.setGame(game);
            controller.setManager(statoionManager, railwayManager, locomotiveManager, carManager);
            controller.addButton();
            controller.setTimerAndButton();
            game.start(statoionManager);
            primaryStage.setTitle("MEowTRO");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}