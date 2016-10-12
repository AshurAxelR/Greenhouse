package com.xrbpowered.greenhouse.preview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.xrbpowered.greenhouse.map.generate.StyleMap;
import com.xrbpowered.greenhouse.map.generate.StyleMapTile;

public class PreviewStyleMap extends Preview<StyleMapTile, StyleMap> {

	public static int tileSize = 24;
	
	private static final Color[] TILE_COLORS = {
		Color.BLACK, Color.WHITE, new Color(0x440000), new Color(0xffdd99)
	};

	private static final Color[] ENTITY_COLORS = {
			new Color(0x00dd00), new Color(0x00aaff)
		};

	public PreviewStyleMap() {
		super(tileSize);
	}
	
	@Override
	protected StyleMap getMap(MapSet mapSet) {
		return mapSet.styleMap;
	}
	
	@Override
	protected Color getTileColor(StyleMapTile v) {
		return TILE_COLORS[v.style.ordinal()];
	}
	
	@Override
	protected StyleMapTile renderTile(Graphics2D g2, StyleMap map, int x, int y) {
		StyleMapTile v = super.renderTile(g2, map, x, y);
		
		if(map.getStartPoint()!=null &&  x==map.getStartPoint().x && y==map.getStartPoint().y) {
			PreviewBlockMap.renderStart(g2, sx, sy, tileSize);
		}
		else if(v.terminal!=null) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(ENTITY_COLORS[v.terminal.ordinal()]);
			g2.fillPolygon(
				new int[] {sx+tileSize/5, sx+tileSize/2, sx+tileSize*4/5, sx+tileSize/2},
				new int[] {sy+tileSize/2, sy+tileSize/5, sy+tileSize/2, sy+tileSize*4/5}, 4);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			if(v.termDir!=null) {
				switch(v.termDir) {
					case N:
						g2.fillRect(sx, sy, tileSize, 2);
						break;
					case S:
						g2.fillRect(sx, sy+tileSize-2, tileSize, 2);
						break;
					case W:
						g2.fillRect(sx, sy, 2, tileSize);
						break;
					case E:
						g2.fillRect(sx+tileSize-2, sy, 2, tileSize);
						break;
				}
			}
		}
		
		return v;
	}

}
