package com.xrbpowered.greenhouse.map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.greenhouse.render.PrefabActor;
import com.xrbpowered.greenhouse.render.PrefabComponent;
import com.xrbpowered.greenhouse.render.TileActor;

public class GreenhouseMap {

	public final int sizex, sizey;
	private TileActor[][] tiles;
	
	public ArrayList<StaticMeshActor> crystalActors = new ArrayList<>();
	public int collectedCrystals = 0;

//	private ArrayList<TileActor> tileList = new ArrayList<>();

	public GreenhouseMap(int sizex, int sizey) {
		this.sizex = sizex;
		this.sizey = sizey;
		this.tiles = new TileActor[sizex+1][sizey+1];
	}
	
	public void addTile(int x, int y, TileActor tile) {
/*		if(tiles[x][y]!=null)
			tileList.remove(tiles[x][y]);
		tileList.add(tile);*/
		tiles[x][y] = tile;
	}
	
	public boolean updateMapLightColors(BufferedImage img, int w, int h) {
		int[] data = new int[w*h*4];
		for(int x=0; x<=sizex-1; x++)
			for(int y=0; y<=sizey-1; y++) {
				TileActor tile = tiles[x][y];
				if(tile==null || tile.lightPosition==null || tile.lightColor==null)
					continue;
				int offs = ((x+1) + (y+1)*w)*4;
				Color col = new Color(tile.lightColor.x, tile.lightColor.y, tile.lightColor.z);
				data[offs+3] = 255;
				data[offs+0] = col.getRed();
				data[offs+1] = col.getGreen();
				data[offs+2] = col.getBlue();
			}
		WritableRaster raster = img.getRaster();
		raster.setPixels(0, 0, w, h, data);
		return true;
	}
	
	public int getInstanceData(float[] instanceData, PrefabComponent comp) {
		int count = 0;
		int offs = 0;
		for(int x=0; x<=sizex; x++)
			for(int y=0; y<=sizey; y++) {
				TileActor tile = tiles[x][y];
				for(PrefabActor a : tile.prefabs) {
					if(a.prefab.hasComponent(comp)) {
						instanceData[offs] = a.position.x;
						instanceData[offs+1] = a.position.y;
						instanceData[offs+2] = a.position.z;
						instanceData[offs+3] = -a.rotation.y;
						offs += 4;
						count++;
					}
				}
			}
		return count;
	}
	
	public TileActor tileFromPosition(float x, float z) {
		int mapx = (int)(x/6f)+1;
		int mapy = (int)(z/6f)+1;
		return tiles[mapx][mapy];
	}
	
}
