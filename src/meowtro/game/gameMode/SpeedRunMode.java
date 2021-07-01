package meowtro.game.gameMode;
import meowtro.timeSystem.TimeLine;

public class SpeedRunMode implements GameTerminateChecker {
    private String duration; //"YY-MM-DD HH:MM:SS"
    public SpeedRunMode(String duration){
        this.duration = duration;
    }
    public boolean gameIsEnded(){
        TimeLine timer = TimeLine.getInstance();
        if(timer.greaterOrEqualThanTime(duration)){
            return true;
        }
        return false;
    }
    public String getGameMode(){
        return "SpeedRunMode";
    }
}
