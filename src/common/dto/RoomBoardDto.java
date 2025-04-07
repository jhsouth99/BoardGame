package common.dto;

import java.io.Serializable;

public class RoomBoardDto implements Serializable {
	public String roomName;
    public String[] tiles;
    public int width;
    public int height;
    
    public RoomBoardDto(String roomName, String[] tiles) {

    	this.roomName = roomName;
    	this.tiles = tiles;
    }
}
