package com.xrbpowered.greenhouse.map.generate;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

public class BlockMap implements AbstractMap<Integer> {

	public static final int UNKNOWN = 0;
	public static final int NO_PASS = 1;
//	public static final int PASS = 2;
	
	private final int sizex, sizey;
	public final int[][] map;
	
	private Point startPoint = null;

	public BlockMap(int sizex, int sizey) {
		this.sizex = sizex;
		this.sizey = sizey;
		map = new int[sizex][sizey];
		for(int x=0; x<sizex; x++)
			for(int y=0; y<sizey; y++) {
				if(x==0 || y==0 || x==sizex-1 || y==sizey-1)
					map[x][y] = NO_PASS;
				else
					map[x][y] = UNKNOWN;
			}
	}
	
	public Integer get(int x, int y) {
		return map[x][y];
	}
	
	@Override
	public int sizex() {
		return sizex;
	}
	
	public int sizey() {
		return sizey;
	};

	public void setStartPoint(Point startPoint) {
		this.startPoint = new Point(startPoint);
	}
	
	public Point getStartPoint() {
		return startPoint;
	}
	
	public void finish(int value) {
		for(int x=0; x<sizex; x++)
			for(int y=0; y<sizey; y++) {
				if(map[x][y]==UNKNOWN)
					map[x][y] = value;
			}
	}
	
	public void paintDistanceFromOrigin(Point start) {
		for(int x=0; x<sizex; x++)
			for(int y=0; y<sizey; y++) {
				if(map[x][y]>NO_PASS)
					map[x][y] = UNKNOWN;
			}
		
		LinkedList<Point> tokens = new LinkedList<>();
		tokens.add(new Point(start));
		int v = BlockMap.NO_PASS+1;
		while(!tokens.isEmpty()) {
			Point p = tokens.removeFirst();
			if(map[p.x][p.y]!=UNKNOWN)
				continue;
			map[p.x][p.y] = v;
			v++;
			tokens.add(new Point(p.x-1, p.y));
			tokens.add(new Point(p.x+1, p.y));
			tokens.add(new Point(p.x, p.y-1));
			tokens.add(new Point(p.x, p.y+1));
		}
	}
	
	// use this method to generate block map
	public static BlockMap generateDefault(Random random) {
		TopologyMap topology = new TopologyMap();
		topology.generate(random);
		return TopologyExpander.expand(topology, random);
	}
}
