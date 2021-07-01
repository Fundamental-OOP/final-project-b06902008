package meowtro.game.gameMode;
import meowtro.game.Game;

public class MaxProfitMode implements GameTerminateChecker {
    private int maxProfit;
    public MaxProfitMode(int maxProfit){
        this.maxProfit = maxProfit;
    }
    public boolean gameIsEnded(){
        if(Game.getBalance()>=this.maxProfit){
            return true;
        }
        return false;
    }
    public String getGameMode(){
        return "MaxProfitMode";
    }
}
