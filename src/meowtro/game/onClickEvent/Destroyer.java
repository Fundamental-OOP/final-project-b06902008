package meowtro.game.onClickEvent;

import meowtro.Position;
import meowtro.game.Game;
import meowtro.metro_system.station.Station;

public class Destroyer extends OnClickEvent {
    public Destroyer(Game game) {
        this.name = "destroyer";
        this.game = game;
    }
    public void conduct(Position position) {
        this.game.setNowEvent(new WaitForClick(this.game));
    }
    public void conduct(Station station) {
        station.getManager().destroy(station);
        this.game.setNowEvent(new WaitForClick(this.game));
    }
}
