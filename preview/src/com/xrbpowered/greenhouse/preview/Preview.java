package com.xrbpowered.greenhouse.preview;

import java.awt.Color;
import java.awt.Graphics2D;

import com.xrbpowered.greenhouse.map.generate.AbstractMap;

public abstract class Preview<T, M extends AbstractMap<T>> {
	
	public final int tileSize;
	
	public Preview(int tileSize) {
		this.tileSize = tileSize;
	}
	
	protected abstract M getMap(MapSet mapSet);
	protected abstract Color getTileColor(T tile);
	
	protected int sx, sy;
	
	protected T renderTile(Graphics2D g2, M map, int x, int y) {
		sx = x*tileSize;
		sy = y*tileSize;
		T v = map.get(x, y);
		
		Color c = getTileColor(v);
		if(c!=null) {
			g2.setColor(c);
			g2.fillRect(sx, sy, tileSize, tileSize);
		}
		return v;
	}
	
	public void paint(Graphics2D g2, MapSet mapSet) {
		M map = mapSet==null ? null : getMap(mapSet);
		if(map==null)
			return;
		for(int x=0; x<map.sizex(); x++)
			for(int y=0; y<map.sizey(); y++)
				renderTile(g2, map, x, y);
	}
}
