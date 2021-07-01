package meowtro.game.onClickEvent;

import meowtro.game.Game;
import meowtro.metro_system.station.Station;
import meowtro.metro_system.train.Locomotive;

public class Upgrader extends OnClickEvent {
    public Upgrader(Game game) {
        this.name = "upgrader";
        this.game = game;
    }
    public void conduct(Station station) {
        station.getManager().upgrade(station);
        this.game.setNowEvent(new WaitForClick(this.game));
    }
    public void conduct(Locomotive locomotive) {
        locomotive.getManager().upgrade(locomotive);
        this.game.setNowEvent(new WaitForClick(this.game));
    }
}
