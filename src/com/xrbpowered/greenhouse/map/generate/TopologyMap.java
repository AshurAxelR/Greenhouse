package com.xrbpowered.greenhouse.map.generate;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TopologyMap implements AbstractMap<Integer> {

	// options
	public static int SIZE_X = 5;
	public static int SIZE_Y = 5;
	public static float SPREAD_CHANCE = 0.6f;
	public static float FORCED_CUT_CHANCE = 0.3f;
	public static float TARGET_COVERAGE = 0.8f;	

	// passability bits
	public static final int SOLID = 0;
	public static final int N_PASS = 4; // [0, -1]
	public static final int S_PASS = 1; // [0, 1]
	public static final int W_PASS = 8; // [-1, 0]
	public static final int E_PASS = 2; // [1, 0]

	private int sizex, sizey;
	private final int[][] map;
	private Point startPoint; 
	
	private List<Point> tokens;
	private int coverage;

	public TopologyMap() {
		this(SIZE_X, SIZE_Y);
	}
	
	public TopologyMap(int sizex, int sizey) {
		this.sizex = sizex;
		this.sizey = sizey;
		this.map = new int[sizex][sizey];
		for(int x=0; x<sizex; x++)
			for(int y=0; y<sizey; y++) {
				map[x][y] = SOLID;
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
	}
	
	public boolean isDeadEnd(int x, int y) {
		return map[x][y]==N_PASS || map[x][y]==S_PASS || map[x][y]==W_PASS || map[x][y]==E_PASS;
	}
	
	public Point getStartPoint() {
		return startPoint;
	}
	
	public void generate(Random random) {
		generate(random, getRandomPoint(random));
	}

	public void generate(Random random, Point startPoint) {
		tokens = new LinkedList<>();
		addStartPoint(startPoint);
		for(;;) {
			spreadTokens(random);
			if(isCoverageSatisfied())
				return;
			addNewCut(random);
		}
	}
	
	public Point getRandomPoint(Random random) {
		return new Point(random.nextInt(sizex), random.nextInt(sizey));
	}
	
	private boolean addToken(Point p) {
		if(map[p.x][p.y]==0) {
			coverage++;
			tokens.add(p);
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean cut(Point from, int pass, boolean forced) {
		if((map[from.x][from.y]&pass)>0)
			return false;
		switch(pass) {
			case N_PASS:
				if(from.y>0 && (addToken(new Point(from.x, from.y-1)) || forced)) {
					map[from.x][from.y] |= N_PASS;
					map[from.x][from.y-1] |= S_PASS;
					return true;
				}
				else {
					return false;
				}
			case S_PASS:
				if(from.y<sizey-1 && (addToken(new Point(from.x, from.y+1)) || forced)) {
					map[from.x][from.y] |= S_PASS;
					map[from.x][from.y+1] |= N_PASS;
					return true;
				}
				else {
					return false;
				}
			case W_PASS:
				if(from.x>0 && (addToken(new Point(from.x-1, from.y)) || forced)) {
					map[from.x][from.y] |= W_PASS;
					map[from.x-1][from.y] |= E_PASS;
					return true;
				}
				else {
					return false;
				}
			case E_PASS:
				if(from.x<sizex-1 && (addToken(new Point(from.x+1, from.y)) || forced)) {
					map[from.x][from.y] |= E_PASS;
					map[from.x+1][from.y] |= W_PASS;
					return true;
				}
				else {
					return false;
				}
			default:
				return false;
		}
	}
	
	private void addStartPoint(Point startPoint) {
		this.startPoint = new Point(startPoint);
		coverage = 1;
		cut(startPoint, N_PASS, false);
		cut(startPoint, S_PASS, false);
		cut(startPoint, W_PASS, false);
		cut(startPoint, E_PASS, false);
	}
	
	private void spreadTokens(Random random) {
		while(!tokens.isEmpty()) {
			Point p = tokens.remove(random.nextInt(tokens.size()));
			if(random.nextFloat()<SPREAD_CHANCE)
				cut(p, N_PASS, random.nextFloat()<FORCED_CUT_CHANCE);
			if(random.nextFloat()<SPREAD_CHANCE)
				cut(p, S_PASS, random.nextFloat()<FORCED_CUT_CHANCE);
			if(random.nextFloat()<SPREAD_CHANCE)
				cut(p, W_PASS, random.nextFloat()<FORCED_CUT_CHANCE);
			if(random.nextFloat()<SPREAD_CHANCE)
				cut(p, E_PASS, random.nextFloat()<FORCED_CUT_CHANCE);
		}
	}
	
	private boolean isCoverageSatisfied() {
		return ((float)coverage / (float)(sizex*sizey)) >= TARGET_COVERAGE;
	}
	
	private boolean addNewCut(Random random) {
		Point p0 = getRandomPoint(random);
		Point p = new Point();
		for(int ix=0; ix<sizex; ix++)
			for(int iy=0; iy<sizey; iy++) {
				p.x = (p0.x+ix) % sizex;
				p.y = (p0.y+iy) % sizey;
				if(map[p.x][p.y]>0) {
					if(cut(p, N_PASS, false))
						return true;
					if(cut(p, S_PASS, false))
						return true;
					if(cut(p, W_PASS, false))
						return true;
					if(cut(p, E_PASS, false))
						return true;
				}
			}
		return false;
	}
	
}
