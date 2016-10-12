package com.xrbpowered.greenhouse.map.generate;

public enum TileDirection {
	N(0, -1),
	W(-1, 0),
	S(0, 1),
	E(1, 0);

	public final int dx, dy;
	
    private TileDirection(int dx, int dy) {
    	this.dx = dx;
    	this.dy = dy;
    }
}
