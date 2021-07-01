package meowtro.game.onClickEvent;

import meowtro.game.Game;

public class WaitForClick extends OnClickEvent {
    public WaitForClick(Game game) {
        this.name = "default";
        this.game = game;
    }
    public void conduct(double x, double y) {    
        this.game.setNowEvent(new WaitForClick(this.game));
    }
}
