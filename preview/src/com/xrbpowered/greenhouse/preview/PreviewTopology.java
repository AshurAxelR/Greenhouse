package com.xrbpowered.greenhouse.preview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.xrbpowered.greenhouse.map.generate.TopologyMap;

public class PreviewTopology extends Preview<Integer, TopologyMap> {

	public static int tileSize = 64;
	
	public PreviewTopology() {
		super(tileSize);
	}
	
	@Override
	protected TopologyMap getMap(MapSet mapSet) {
		return mapSet.topology;
	}
	
	@Override
	protected Color getTileColor(Integer tile) {
		return Color.WHITE;
	}
	
	@Override
	protected Integer renderTile(Graphics2D g2, TopologyMap map, int x, int y) {
		int v = super.renderTile(g2, map, x, y);

		g2.setStroke(new BasicStroke(7));
		g2.setColor(new Color(0x999999));
		if((v&TopologyMap.E_PASS)!=0) g2.drawLine(sx+tileSize/2, sy+tileSize/2, sx+tileSize, sy+tileSize/2);
		if((v&TopologyMap.S_PASS)!=0) g2.drawLine(sx+tileSize/2, sy+tileSize/2, sx+tileSize/2, sy+tileSize);
		if((v&TopologyMap.W_PASS)!=0) g2.drawLine(sx+tileSize/2, sy+tileSize/2, sx+2, sy+tileSize/2);
		if((v&TopologyMap.N_PASS)!=0) g2.drawLine(sx+tileSize/2, sy+tileSize/2, sx+tileSize/2, sy+2);
		g2.setStroke(new BasicStroke(3));
		g2.setColor(new Color(0xdddddd));
		if((v&TopologyMap.E_PASS)!=0) g2.drawLine(sx+tileSize/2, sy+tileSize/2, sx+tileSize, sy+tileSize/2);
		if((v&TopologyMap.S_PASS)!=0) g2.drawLine(sx+tileSize/2, sy+tileSize/2, sx+tileSize/2, sy+tileSize);
		if((v&TopologyMap.W_PASS)!=0) g2.drawLine(sx+tileSize/2, sy+tileSize/2, sx, sy+tileSize/2);
		if((v&TopologyMap.N_PASS)!=0) g2.drawLine(sx+tileSize/2, sy+tileSize/2, sx+tileSize/2, sy);
		if(x==map.getStartPoint().x && y==map.getStartPoint().y) {
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
		
		return v;
	}
	
}
