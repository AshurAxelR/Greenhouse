package com.xrbpowered.greenhouse.map.generate;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

public abstract class TopologyExpander {

	// options
	public static int EXPAND_X = 5;
	public static int EXPAND_Y = 5;
	public static float INTER_CHAMBER_WALL_CHANCE = 0.5f;
	public static int PIVOT_SHIFT = 3;
	public static float CHAMBER_CHANCE = 0.75f;
	public static int MIN_CHAMBER_SIZE = 8;
	public static int MAX_CHAMBER_SIZE = 12;
	
	public static BlockMap expand(TopologyMap topology, Random random) {
		int tsizex = topology.sizex();
		int tsizey = topology.sizey();
		BlockMap map = new BlockMap(tsizex*EXPAND_X, tsizey*EXPAND_Y);
		
		// add inter-chamber walls
		for(int x=0; x<tsizex; x++)
			for(int y=0; y<tsizey; y++) {
				if(random.nextFloat()<INTER_CHAMBER_WALL_CHANCE) {
					addLineX(map, x*EXPAND_X, x*EXPAND_X+EXPAND_X-1, y*EXPAND_Y, BlockMap.NO_PASS);
				}
				if(random.nextFloat()<INTER_CHAMBER_WALL_CHANCE) {
					addLineY(map, x*EXPAND_X, y*EXPAND_Y, y*EXPAND_Y+EXPAND_Y-1, BlockMap.NO_PASS);
				}
			}
		
		// generate chamber pivots
		Point[][] pivots = new Point[tsizex][tsizey];
		for(int x=0; x<tsizex; x++)
			for(int y=0; y<tsizey; y++) {
				Point p = new Point();
				
				// shift the pivot off-centre
				int dx = EXPAND_X/2+(random.nextInt(PIVOT_SHIFT+1)-PIVOT_SHIFT/2);
				if(dx<1) dx = 1;
				if(dx>EXPAND_X-2) dx = EXPAND_X-2;
				int dy = EXPAND_Y/2+(random.nextInt(PIVOT_SHIFT+1)-PIVOT_SHIFT/2);
				if(dy<1) dy = 1;
				if(dy>EXPAND_Y-2) dy = EXPAND_Y-2;
				
				p.x = x*EXPAND_X+dx;
				p.y = y*EXPAND_Y+dy;
				pivots[x][y] = p;
				
				if(x==topology.getStartPoint().x && y==topology.getStartPoint().y) {
					map.setStartPoint(p);
				}
			}
		
		// generate chambers
		for(int x=0; x<tsizex; x++)
			for(int y=0; y<tsizey; y++) {
				if(topology.isDeadEnd(x, y) || topology.get(x, y)>0 && random.nextFloat()<CHAMBER_CHANCE) {
					generateChamber(map, random, pivots[x][y],
							random.nextInt(MAX_CHAMBER_SIZE-MIN_CHAMBER_SIZE+1)+MIN_CHAMBER_SIZE);
				}
			}
		
		// add inter-chamber tunnels
		for(int x=0; x<tsizex; x++)
			for(int y=0; y<tsizey; y++) {
				if((topology.get(x, y)&TopologyMap.N_PASS)>0) {
					addLineX(map, pivots[x][y-1].x, pivots[x][y].x, pivots[x][y-1].y, BlockMap.NO_PASS+1);
					addLineY(map, pivots[x][y].x, pivots[x][y-1].y, pivots[x][y].y, BlockMap.NO_PASS+1);
				}
				if((topology.get(x, y)&TopologyMap.W_PASS)>0) {
					addLineY(map, pivots[x-1][y].x, pivots[x-1][y].y, pivots[x][y].y, BlockMap.NO_PASS+1);
					addLineX(map, pivots[x-1][y].x, pivots[x][y].x, pivots[x][y].y, BlockMap.NO_PASS+1);
				}
			}
		
		map.finish(BlockMap.NO_PASS);
		int msizex = map.sizex();
		int msizey = map.sizey();

		// clean-up diagonals
		boolean upd;
		int counter = 0;
		boolean kill = false;
		do {
			upd = false;
			for(int x=1; x<msizex-2; x++)
				for(int y=1; y<msizey-2; y++) {
					int v00 = map.get(x, y);
					int v10 = map.get(x+1, y);
					int v01 = map.get(x, y+1);
					int v11 = map.get(x+1, y+1);
					boolean b00 = v00>BlockMap.NO_PASS;
					boolean b10 = v10>BlockMap.NO_PASS;
					boolean b01 = v01>BlockMap.NO_PASS;
					boolean b11 = v11>BlockMap.NO_PASS;
					if(b00==b11 && b10==b01 && b00!=b10) {
						upd = true;
						if(!b00) {
							if(v01==v10 || kill)
								map.map[x][y] = v10;
							else if(v01>v10)
								map.map[x][y+1] = BlockMap.NO_PASS;
							else
								map.map[x+1][y] = BlockMap.NO_PASS;
						}
						else {
							if(v00==v11 || kill)
								map.map[x+1][y] = v00;
							else if(v00>v11)
								map.map[x][y] = BlockMap.NO_PASS;
							else
								map.map[x+1][y+1] = BlockMap.NO_PASS;
						}
					}
				}
			counter++;
			if(counter>0)
				kill = true;
		} while(upd);
		
		map.paintDistanceFromOrigin(map.getStartPoint());
		map.finish(BlockMap.NO_PASS);
		
		for(int x=1; x<msizex-2; x++)
			for(int y=1; y<msizey-2; y++) {
				int v00 = map.get(x, y);
				int v10 = map.get(x+1, y);
				int v01 = map.get(x, y+1);
				int v11 = map.get(x+1, y+1);
				boolean b00 = v00>BlockMap.NO_PASS;
				boolean b10 = v10>BlockMap.NO_PASS;
				boolean b01 = v01>BlockMap.NO_PASS;
				boolean b11 = v11>BlockMap.NO_PASS;
				if(b00==b11 && b10==b01 && b00!=b10) {
					System.err.println("Check failed!");
					map.map[x][y] = BlockMap.UNKNOWN;
				}
			}
		
		return map;
	}

	public static void generateChamber(BlockMap map, Random random, Point start, int maxSize) {
		int roomSize = 0;
		LinkedList<Point> tokens = new LinkedList<>();
		tokens.add(new Point(start));
		int v = BlockMap.NO_PASS+2;
		while(!tokens.isEmpty()) {
			Point p = tokens.remove(random.nextInt(tokens.size()));
			if(map.map[p.x][p.y]==BlockMap.UNKNOWN) {
				if(roomSize<maxSize && (tokens.size()<2 || random.nextBoolean())) {
					map.map[p.x][p.y] = v;
					v++;
				}
				else {
					map.map[p.x][p.y] = BlockMap.NO_PASS;
					continue;
				}
			}
			else {
				continue;
			}
			roomSize++;
			tokens.add(new Point(p.x-1, p.y));
			tokens.add(new Point(p.x+1, p.y));
			tokens.add(new Point(p.x, p.y-1));
			tokens.add(new Point(p.x, p.y+1));
		}
	}
	
	private static void addLineX(BlockMap map, int x1, int x2, int y, int value) {
		for(int x=Math.min(x1, x2); x<=Math.max(x1, x2); x++) {
			map.map[x][y] = value;
		}
	}

	private static void addLineY(BlockMap map, int x, int y1, int y2, int value) {
		for(int y=Math.min(y1, y2); y<=Math.max(y1, y2); y++) {
			map.map[x][y] = value;
		}
	}
	
}
