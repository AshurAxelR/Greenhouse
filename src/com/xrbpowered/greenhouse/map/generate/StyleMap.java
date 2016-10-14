package com.xrbpowered.greenhouse.map.generate;

import java.awt.Point;
import java.util.Random;

import com.xrbpowered.greenhouse.map.generate.StyleMapTile.Style;
import com.xrbpowered.greenhouse.map.generate.StyleMapTile.Terminal;
import com.xrbpowered.utils.RandomUtils;

public class StyleMap implements AbstractMap<StyleMapTile> {

	public boolean addNarrows = false;
	
	private int sizex, sizey;
	private StyleMapTile[][] map;
	private Point startPoint;
	
	public StyleMap(BlockMap blockMap) {
		sizex = blockMap.sizex();
		sizey = blockMap.sizey();
		map = new StyleMapTile[sizex][sizey];
		startPoint = blockMap.getStartPoint();
		for(int x=0; x<sizex; x++)
			for(int y=0; y<sizey; y++) {
				StyleMapTile tile = new StyleMapTile(x, y, blockMap.get(x, y) > BlockMap.NO_PASS);
				if(x==0 || y==0 || x==sizex-1 || y==sizey-1)
					tile.style = Style.fixedWall;
				else
					tile.style = tile.passable ? Style.defPass : Style.defWall;
				map[x][y] = tile;
			}
	}
	
	@Override
	public StyleMapTile get(int x, int y) {
		return map[x][y];
	}
	
	@Override
	public int sizex() {
		return sizex;
	}
	
	@Override
	public int sizey() {
		return sizey;
	}
	
	public Point getStartPoint() {
		return startPoint;
	}
	
	private int countPassable(int x, int y) {
		int count = 0;
		for(TileDirection d : TileDirection.values()) {
			if(map[x+d.dx][y+d.dy].passable)
				count++;
		}
		return count;
	}

	private int countStyle(int x, int y, Style style) {
		int count = 0;
		for(TileDirection d : TileDirection.values()) {
			if(map[x+d.dx][y+d.dy].style==style)
				count++;
		}
		return count;
	}

	private int countStyleDiag(int x, int y, Style style) {
		int count = 0;
		for(int dx=-1; dx<=1; dx++)
			for(int dy=-1; dy<=1; dy++) {
				if(dx!=0 && dy!=0 && map[x+dx][y+dy].style==style)
					count++;
		}
		return count;
	}

	public void decorate(Random random) {
		// add entities
		// FIXME entity probabilities and exclusion zones
		int[] entityWeights = new int[] {20, 3, 0};
		for(int x=1; x<sizex-1; x++)
			for(int y=1; y<sizey-1; y++) {
				if(!map[x][y].passable)
					continue;
				if(x==startPoint.x && y==startPoint.y)
					continue;
				int d0 = random.nextInt(4);
				TileDirection dir = null;
				for(int di=0; di<4; di++) {
					TileDirection d = TileDirection.values()[(d0+di)%4];
					if(!map[x+d.dx][y+d.dy].passable && map[x-d.dx][y-d.dy].passable &&
							(map[x+d.dx][y+d.dy].style==Style.fixedWall || map[x+d.dx][y+d.dy].style==Style.defWall)) {
						dir = d;
						break;
					}
				}
				if(dir==null)
					continue;
				int term = RandomUtils.weighted(random, entityWeights) - 1;
				if(term>=0) {
					map[x][y].terminal = Terminal.values()[term];
					if(map[x][y].terminal!=null) {
						map[x][y].termDir = dir;
						map[x+dir.dx][y+dir.dy].style = Style.fixedWall;
					}
				}
			}
		
		// add narrows
		if(addNarrows) {
			for(int x=1; x<sizex-1; x++)
				for(int y=1; y<sizey-1; y++) {
					if(!map[x][y].passable)
						continue;
					if(x==startPoint.x && y==startPoint.y || map[x][y].terminal!=null)
						continue;
					if(!map[x-1][y].passable && !map[x+1][y].passable && map[x][y-1].passable && map[x][y+1].passable ||
							map[x-1][y].passable && map[x+1][y].passable && !map[x][y-1].passable && !map[x][y+1].passable) {
						if(random.nextInt(3)>0)
							map[x][y].style = Style.narrowPass;
					}
				}
			boolean f;
			do {
				f = false;
				for(int x=1; x<sizex-1; x++)
					for(int y=1; y<sizey-1; y++) {
						if(map[x][y].style==Style.narrowPass)
							continue;
						if(!map[x][y].passable)
							continue;
						if(x==startPoint.x && y==startPoint.y || map[x][y].terminal!=null)
							continue;
						int pass =countPassable(x, y);
						int s = countStyle(x, y, Style.narrowPass);
						if(pass>1 && s-pass==0 && random.nextInt(3)>0 || s>0 &&
								(!map[x-1][y].passable && !map[x+1][y].passable && map[x][y-1].passable && map[x][y+1].passable ||
								map[x-1][y].passable && map[x+1][y].passable && !map[x][y-1].passable && !map[x][y+1].passable)) {
							map[x][y].style = Style.narrowPass;
							f = true;
						}
					}
			} while(f);
			for(int x=1; x<sizex-1; x++)
				for(int y=1; y<sizey-1; y++) {
					if(map[x][y].style!=Style.narrowPass)
						continue;
					if(countStyle(x, y, Style.narrowPass)==0)
						map[x][y].style = Style.defPass;
				}
			for(int x=1; x<sizex-1; x++)
				for(int y=1; y<sizey-1; y++) {
					if(map[x][y].style==Style.defWall && countStyleDiag(x, y, Style.narrowPass)>0) {
						map[x][y].style = Style.fixedWall;
					}
				}
		}
	}
}
