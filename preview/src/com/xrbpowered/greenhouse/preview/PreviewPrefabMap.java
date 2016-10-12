package com.xrbpowered.greenhouse.preview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.xrbpowered.greenhouse.map.generate.PrefabMap;
import com.xrbpowered.greenhouse.map.generate.PrefabTile;
import com.xrbpowered.greenhouse.map.generate.PrefabType;
import com.xrbpowered.utils.assets.AssetManager;
import com.xrbpowered.utils.assets.CPAssetManager;

public class PreviewPrefabMap extends Preview<PrefabTile, PrefabMap> {

	public static int tileSize = 16;

	public static BufferedImage[] images = null;
	
	public PreviewPrefabMap() {
		super(tileSize);
		if(images==null) {
			AssetManager assets = new CPAssetManager("assets", null);
			PrefabType[] v = PrefabType.values();
			int n = v.length;
			images = new BufferedImage[n]; 
			for(int i=0; i<n; i++) {
				try {
					images[i] = assets.loadImage(v[i].name()+".png");
				}
				catch(IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}
	
	@Override
	protected PrefabMap getMap(MapSet mapSet) {
		return mapSet.prefabMap;
	}
	
	@Override
	protected Color getTileColor(PrefabTile v) {
		return null;
	}
	
	@Override
	protected PrefabTile renderTile(Graphics2D g2, PrefabMap map, int x, int y) {
		PrefabTile v = super.renderTile(g2, map, x, y);
		// TODO rotate
		if(v!=null && v.prefab!=null) {
			AffineTransform t = new AffineTransform();
			t.translate(sx+tileSize/2, sy+tileSize/2);
			t.rotate(-v.rotate*Math.PI/2.0);
			BufferedImage img = images[v.prefab.ordinal()];
			t.translate(-img.getWidth()/2, -img.getHeight()/2);
			g2.drawImage(img, t, null);
		}
		return v;
	}
	
}
