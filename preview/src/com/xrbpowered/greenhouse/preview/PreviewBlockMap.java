package com.xrbpowered.greenhouse.preview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.xrbpowered.greenhouse.map.generate.BlockMap;

public class PreviewBlockMap extends Preview<Integer, BlockMap> {

	public static int tileSize = 24;

	public PreviewBlockMap() {
		super(tileSize);
	}
	
	@Override
	protected BlockMap getMap(MapSet mapSet) {
		return mapSet.blockMap;
	}
	
	@Override
	protected Color getTileColor(Integer v) {
		if(v==BlockMap.NO_PASS)
			return Color.BLACK;
		else if(v>BlockMap.NO_PASS) {
				int c = 255 - (v - BlockMap.NO_PASS - 1);
				return new Color(255, c>=0 ? 255 : (c>=-255 ? c+256 : 0), c>=0 ? c : 0);
		}
		else
			return Color.LIGHT_GRAY;
	}
	
	@Override
	protected Integer renderTile(Graphics2D g2, BlockMap map, int x, int y) {
		int v = super.renderTile(g2, map, x, y);
		
		if(map.getStartPoint()!=null &&  x==map.getStartPoint().x && y==map.getStartPoint().y) {
			renderStart(g2, sx, sy, tileSize);
		}
		
		return v;
	}
	
	public static void renderStart(Graphics2D g2, int sx, int sy, int tileSize) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(9));
		g2.setColor(new Color(0x99990000, true));
		g2.drawLine(sx+tileSize/3, sy+tileSize/3, sx+tileSize*2/3, sy+tileSize*2/3);
		g2.drawLine(sx+tileSize/3, sy+tileSize*2/3, sx+tileSize*2/3, sy+tileSize/3);
		g2.setStroke(new BasicStroke(5));
		g2.setColor(new Color(0xffff0000));
		g2.drawLine(sx+tileSize/3, sy+tileSize/3, sx+tileSize*2/3, sy+tileSize*2/3);
		g2.drawLine(sx+tileSize/3, sy+tileSize*2/3, sx+tileSize*2/3, sy+tileSize/3);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
}
