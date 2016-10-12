package com.xrbpowered.greenhouse.map.generate;

public class StyleMapTile {

	public enum Style {
		defWall, defPass, fixedWall, narrowPass
	}
	
	public enum Terminal {
		data, oxygen
	}
	
	public final int x, y;
	public final boolean passable;
	public Style style;
	public Terminal terminal = null;
	public TileDirection termDir;
	
	public StyleMapTile(int x, int y, boolean passsable) {
		this.x = x;
		this.y = y;
		this.passable = passsable;
	}

}
