import meowtro.*;
import meowtro.game.*;
import meowtro.game.entityManager.*;
import meowtro.metro_system.Direction;
import meowtro.metro_system.railway.*;
import meowtro.metro_system.station.*;
import meowtro.metro_system.train.Locomotive;

public class Main_test_metro {
    
    public static void main(String[] args) {
        GameFactory gameFactory = new GameFactory();
        Config config = new Config("./resources/defaultConfig.properties", "./resources/localconfig/localConfigTest.properties");
        Game game = gameFactory.createGame(config);

        StationManager stationManager = new StationManager(game);
        RailwayManager railwayManager = new RailwayManager(game);
        game.start(stationManager);

        // add new line, railway, locomotive between two stations
        Line line = new Line(game.getCity(), LineColor.RED);
        game.getCity().addLine(line);
        Station s0 = game.getCity().getRegions().get(0).getStations().get(0);
        Station s1 = game.getCity().getRegions().get(1).getStations().get(0);
        // Railway r0 = new Railway(s0, s1, line, 10000000);
        // Locomotive loco = new Locomotive(r0, new Position(4f, 0f), Direction.BACKWARD);

        // for (int i = 0; i < 15; i++) {
        //     game.update();
        // }
    }

}
