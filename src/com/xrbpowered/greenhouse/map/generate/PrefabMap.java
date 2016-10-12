package com.xrbpowered.greenhouse.map.generate;

import static com.xrbpowered.greenhouse.map.generate.PrefabType.*;
import static com.xrbpowered.greenhouse.map.generate.StyleMapTile.Style.*;

import java.util.Random;

import com.xrbpowered.greenhouse.map.generate.StyleMapTile.Style;

public class PrefabMap implements AbstractMap<PrefabTile> {

	private final Random random;
	private final StyleMap map;
	private final PrefabTile[][] pmap;
	private final int sizex, sizey;
	
	public PrefabMap(StyleMap map, Random random) {
		this.random = random;
		this.map = map;
		this.sizex = map.sizex()*2+2;
		this.sizey = map.sizey()*2+2;
		pmap = new PrefabTile[sizex][sizey];
		
		for(int x=-1; x<map.sizex(); x++)
			for(int y=-1; y<map.sizey(); y++) {
				convertTile(x, y);
			}
		for(int x=0; x<map.sizex(); x++)
			for(int y=0; y<map.sizey(); y++) {
				setEntity(x, y);
			}
		for(int cx=1; cx<sizex-1; cx++)
			for(int cy=1; cy<sizey-1; cy++) {
				randomVariant(cx, cy);
			}
	}

	private Style style(int x, int y) {
		if(x>=0 && y>=0 && x<map.sizex()-1 && y<map.sizey()-1) {
			Style s = map.get(x, y).style;
			if(s==null || s==fixedWall)
				s = defWall;
			return s;
		}
		else
			return defWall;
	}
	
	private boolean matches(Style[] s, Style t00, Style t10, Style t01, Style t11) {
		if(t00!=null && t00!=s[0])
			return false;
		if(t10!=null && t10!=s[1])
			return false;
		if(t01!=null && t01!=s[2])
			return false;
		if(t11!=null && t11!=s[3])
			return false;
		return true;
	}
	
	private PrefabTile makePrefab(PrefabType prefab, int rotate) {
		if(prefab==null)
			return null;
		else
			return new PrefabTile(prefab, rotate<0 ? random.nextInt(4) : rotate);
	}
	
	private void prefabQuad(int cx, int cy, PrefabType p00, int r00, PrefabType p10, int r10, PrefabType p01, int r01, PrefabType p11, int r11) {
		pmap[cx][cy] = makePrefab(p00, r00);
		pmap[cx+1][cy] = makePrefab(p10, r10);
		pmap[cx][cy+1] = makePrefab(p01, r01);
		pmap[cx+1][cy+1] = makePrefab(p11, r11);
	}
	
	private void randomVariant(int cx, int cy) {
		if(pmap[cx][cy]==null)
			return;
		if(pmap[cx][cy].prefab==colPod) {
			pmap[cx][cy].prefab = (random.nextInt(3)==0) ? mid : colPod;
		}
		else if(pmap[cx][cy].prefab==npass) {
			pmap[cx][cy].prefab = random.nextInt(3)==0 ? npassView : npass;
		}
		else if(pmap[cx][cy].prefab==wall) {
			pmap[cx][cy].prefab = random.nextInt(5)==0 ? wallPod : wall;
		}
		else if(pmap[cx][cy].prefab==midLamp) {
			pmap[cx][cy].prefab = random.nextInt(7)>1 ? midLamp : mid;
		}
		else if(pmap[cx][cy].prefab==nmid) {
			pmap[cx][cy].prefab = random.nextInt(5)>1 && !hasAdjacent(cx, cy, nentry) ? nmidLamp : nmid;
		}
	}
	
	private boolean hasAdjacent(int cx, int cy, PrefabType prefab) {
		return pmap[cx-1][cy]!=null && pmap[cx-1][cy].prefab==prefab
				|| pmap[cx+1][cy]!=null && pmap[cx+1][cy].prefab==prefab
				|| pmap[cx][cy-1]!=null && pmap[cx][cy-1].prefab==prefab
				|| pmap[cx][cy+1]!=null && pmap[cx][cy+1].prefab==prefab;
	}
	
	private void convertTile(int x, int y) {
		int cx = x*2+2;
		int cy = y*2+2;

		Style[] s = {style(x, y), style(x+1, y), style(x, y+1), style(x+1, y+1)};
		
		// MVDD would be much faster, but whatever. This is fast enough.
		
		// default pass
		if(matches(s, defPass, defPass, defPass, defPass))
			prefabQuad(cx, cy, midLamp, 0, mid, 0, mid, 0, colPod, -1);
		else if(matches(s, defPass, defWall, defPass, defPass))
			prefabQuad(cx, cy, midLamp, 0, wall, 3, mid, 0, cout, 3);
		else if(matches(s, defPass, defPass, defWall, defPass))
			prefabQuad(cx, cy, midLamp, 0, mid, 0, wall, 2, cout, 1);

		else if(matches(s, defPass, defPass, defPass, defWall))
			prefabQuad(cx, cy, midLamp, 0, mid, 0, mid, 0, cout, 2);
		else if(matches(s, defPass, defWall, defPass, defWall))
			prefabQuad(cx, cy, midLamp, 0, wall, 3, mid, 0, wall, 3);
		else if(matches(s, defPass, defPass, defWall, defWall))
			prefabQuad(cx, cy, midLamp, 0, mid, 0, wall, 2, wall, 2);
		else if(matches(s, defPass, defWall, defWall, defWall))
			prefabQuad(cx, cy, midLamp, 0, wall, 3, wall, 2, cin, 2);

		else if(matches(s, defWall, defPass, defWall, defWall))
			prefabQuad(cx, cy, null, 0, wall, 1, null, 0, cin, 1);
		else if(matches(s, defWall, defWall, defPass, defWall))
			prefabQuad(cx, cy, null, 0, null, 0, wall, 0, cin, 3);
		else if(matches(s, defWall, defWall, defWall, defPass))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, cin, 0);
		else if(matches(s, defWall, defPass, defWall, defPass))
			prefabQuad(cx, cy, null, 0, wall, 1, null, 0, wall, 1);
		else if(matches(s, defWall, defWall, defPass, defPass))
			prefabQuad(cx, cy, null, 0, null, 0, wall, 0, wall, 0);
		else if(matches(s, defWall, defPass, defPass, defPass))
			prefabQuad(cx, cy, null, 0, wall, 1, wall, 0, cout, 0);
		
		// narrow pass
		else if(matches(s, narrowPass, narrowPass, narrowPass, defWall))
			prefabQuad(cx, cy, nmid, 0, null, 0, null, 0, nout, 2);
		else if(matches(s, narrowPass, narrowPass, defWall, narrowPass))
			prefabQuad(cx, cy, nmid, 0, null, 0, null, 0, nout, 1);
		else if(matches(s, narrowPass, narrowPass, defWall, defWall))
			prefabQuad(cx, cy, nmid, 0, null, 0, null, 0, npass, 2);
		else if(matches(s, narrowPass, defWall, narrowPass, narrowPass))
			prefabQuad(cx, cy, nmid, 0, null, 0, null, 0, nout, 3);
		else if(matches(s, narrowPass, defWall, narrowPass, defWall))
			prefabQuad(cx, cy, nmid, 0, null, 0, null, 0, npass, 3);
		else if(matches(s, narrowPass, defWall, defWall, defWall))
			prefabQuad(cx, cy, nmid, 0, null, 0, null, 0, nin, 2);
		else if(matches(s, defWall, narrowPass, narrowPass, narrowPass))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, nout, 0);
		else if(matches(s, defWall, narrowPass, defWall, narrowPass))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, npass, 1);
		else if(matches(s, defWall, narrowPass, defWall, defWall))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, nin, 1);
		else if(matches(s, defWall, defWall, narrowPass, narrowPass))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, npass, 0);
		else if(matches(s, defWall, defWall, narrowPass, defWall))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, nin, 3);
		else if(matches(s, defWall, defWall, defWall, narrowPass))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, nin, 0);
		
		// narrow pass entry
		else if(matches(s, narrowPass, defPass, null, defPass))
			prefabQuad(cx, cy, nmid, 0, nentry, 1, null, 0, newL, 1);
		else if(matches(s, narrowPass, defPass, null, defWall))
			prefabQuad(cx, cy, nmid, 0, nentry, 1, null, 0, necL, 1);
		else if(matches(s, narrowPass, null, defPass, defPass))
			prefabQuad(cx, cy, nmid, 0, null, 0, nentry, 0, newR, 0);
		else if(matches(s, narrowPass, null, defPass, defWall))
			prefabQuad(cx, cy, nmid, 0, null, 0, nentry, 0, necR, 0);
		else if(matches(s, narrowPass, defPass, null, narrowPass))
			prefabQuad(cx, cy, nmid, 0, nentry, 1, null, 0, nein, 1);
		else if(matches(s, narrowPass, null, defPass, narrowPass))
			prefabQuad(cx, cy, nmid, 0, null, 0, nentry, 0, nein, 3);
		
		else if(matches(s, defPass, narrowPass, defPass, null))
			prefabQuad(cx, cy, midLamp, 0, nentry, 3, mid, 0, newR, 3);
		else if(matches(s, defPass, narrowPass, defWall, null))
			prefabQuad(cx, cy, midLamp, 0, nentry, 3, wall, 2, necR, 3);
		else if(matches(s, defPass, defPass, narrowPass, null))
			prefabQuad(cx, cy, midLamp, 0, mid, 0, nentry, 2, newL, 2);
		else if(matches(s, defPass, defWall, narrowPass, null))
			prefabQuad(cx, cy, midLamp, 0, wall, 3, nentry, 2, necL, 2);
		else if(matches(s, defPass, narrowPass, narrowPass, null))
			prefabQuad(cx, cy, midLamp, 0, nentry, 3, nentry, 2, nein, 2);
		
		else if(matches(s, defWall, narrowPass, defPass, defPass))
			prefabQuad(cx, cy, null, 0, null, 0, wall, 0, newL, 0);
		else if(matches(s, defWall, narrowPass, defWall, defPass))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, necL, 0);
		else if(matches(s, defWall, defPass, narrowPass, defPass))
			prefabQuad(cx, cy, null, 0, wall, 1, null, 0, newR, 1);
		else if(matches(s, defWall, defWall, narrowPass, defPass))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, necR, 1);
		else if(matches(s, defWall, narrowPass, narrowPass, defPass))
			prefabQuad(cx, cy, null, 0, null, 0, null, 0, nein, 0);
		
		else if(matches(s, defPass, defPass, null, narrowPass))
			prefabQuad(cx, cy, midLamp, 0, mid, 0, wall, 2, newR, 2);
		else if(matches(s, defPass, null, defPass, narrowPass))
			prefabQuad(cx, cy, midLamp, 0, wall, 3, mid, 0, newL, 3);
		else if(matches(s, defWall, defPass, null, narrowPass))
			prefabQuad(cx, cy, null, 0, wall, 1, null, 0, necR, 2);
		else if(matches(s, defWall, null, defPass, narrowPass))
			prefabQuad(cx, cy, null, 0, null, 0, wall, 0, necL, 3);
	}
	
	private void setEntity(int x, int y) {
		int cx = x*2+2;
		int cy = y*2+2;
		if(map.get(x, y).terminal!=null) {
			pmap[cx][cy].prefab = objData;
			TileDirection dir = map.get(x, y).termDir;
			pmap[cx][cy].rotate = dir.ordinal();
			pmap[cx+dir.dx][cy+dir.dy] = null;
		}
	}
	
	@Override
	public PrefabTile get(int x, int y) {
		return pmap[x][y];
	}

	@Override
	public int sizex() {
		return sizex;
	}

	@Override
	public int sizey() {
		return sizey;
	}

}
