package client.view.minigame.rhythm;

public class Note {
    int lane;
    int y;
    boolean isHit = false;
    boolean isMissed = false;
    
    public Note(int lane, int y) {
        this.lane = lane;
        this.y = y;
    }
}
