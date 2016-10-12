package com.xrbpowered.greenhouse.map;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.Client;
import com.xrbpowered.greenhouse.GreenhouseClient;
import com.xrbpowered.greenhouse.map.generate.BlockMap;
import com.xrbpowered.greenhouse.map.generate.TopologyExpander;
import com.xrbpowered.greenhouse.map.generate.TopologyMap;
import com.xrbpowered.greenhouse.render.TileActor;
import com.xrbpowered.utils.RandomUtils;

public class MapConverter {

	private static final Vector3f ROT_0 = new Vector3f(0f, 0f, 0f);
	private static final Vector3f ROT_90 = new Vector3f(0f, (float) Math.PI/2f, 0f);
	private static final Vector3f ROT_180 = new Vector3f(0f, (float) Math.PI, 0f);
	private static final Vector3f ROT_270 = new Vector3f(0f, (float) -Math.PI/2f, 0f);
	private static final Vector3f[] ROT = {ROT_0, ROT_90, ROT_180, ROT_270};
	private static final Random random = new Random();
	
	private BlockMap map = null;
	private GreenhouseMap renderMap = null;
	public int sizex, sizey;
	
	private final GreenhouseClient client;
	
	public MapConverter(GreenhouseClient client) {
		this.client = client;
	}
	
	public GreenhouseMap generate(int size) {
		Client.timestamp(null);
		TopologyMap topology = new TopologyMap(size, size);
		topology.generate(random);
		map = TopologyExpander.expand(topology, random);
		
		sizex = map.sizex();
		sizey = map.sizey();
		renderMap = new GreenhouseMap(sizex, sizey);
		client.teleportPawn(getX(map.getStartPoint().x), getZ(map.getStartPoint().y));
		
		TileActor.allColliders.clear();
		for(int x=-1; x<sizex; x++)
			for(int y=-1; y<sizey; y++) {
				TileActor tile = new TileActor(client.getScene(), getX(x), getZ(y));
				convertTile(tile, x, y);
				renderMap.addTile(x+1, y+1, tile);
			}
//		renderMap.calculateLighting();
		Client.timestamp("generate");
		return renderMap;
	}
	
	public static final Vector4f[] LIGHT_COLORS = {
		null,
		null,
		new Vector4f(1f, 0.95f, 0.8f, 1f),
		new Vector4f(1f, 1f, 1f, 1f),
		new Vector4f(0.8f, 0.9f, 1f, 1f),
		new Vector4f(0.8f, 0.9f, 1f, 1f),
		new Vector4f(1f, 0.75f, 0.5f, 1f),
	};
	
	public static final Vector4f CRYSTAL_LIGHT_COLOR = new Vector4f(0.4f, 0.6f, 0.2f, 1f);
	public static final Vector4f CRYSTAL_LIGHT_OFF_COLOR = new Vector4f(0.3f, 0.35f, 0.4f, 1f);
	
	private Vector4f randomLightColor() {
		return RandomUtils.item(random, LIGHT_COLORS);
	}
	
	private float getX(int mapx) {
		return mapx*6f;
	}

	private float getZ(int mapy) {
		return mapy*6f;
	}
	
	private void convertTile(TileActor tile, int x, int y) {
		float cx = getX(x);
		float cz = getZ(y);

		boolean p10 = y>=0 && x<sizex-1 && map.map[x+1][y]>BlockMap.NO_PASS;
		boolean p01 = x>=0 && y<sizey-1 && map.map[x][y+1]>BlockMap.NO_PASS;
		boolean p11 = x<sizex-1 && y<sizey-1 && map.map[x+1][y+1]>BlockMap.NO_PASS;
		if(x>=0 && y>=0 && map.map[x][y]>BlockMap.NO_PASS) {
			tile.lightPosition = new Vector3f(cx, 2.5f, cz);
			tile.lightColor = randomLightColor();
			
			if(random.nextInt(8)==0 && x!=map.getStartPoint().x && y!=map.getStartPoint().y) {
				renderMap.crystalActors.add(client.makeCrystalActor(new Vector3f(getX(x), 1f, getZ(y)), random.nextFloat()));
				tile.lightColor = CRYSTAL_LIGHT_COLOR;
			}
						
			tile.addActor(tile.lightColor==null ? client.meshSetMid : client.meshSetMidLight, new Vector3f(cx, 0f, cz), ROT_0); // RandomUtils.item(random, ROT));
			
			if(p11) {
				if(p01 && p10) {
					tile.addActor(client.meshSetMid, new Vector3f(cx+3f, 0f, cz), ROT_0);
					tile.addActor(client.meshSetMid, new Vector3f(cx, 0f, cz+3f), ROT_0);
					tile.addActor(random.nextInt(3)==0 ? client.meshSetMid : client.meshSetColPod, new Vector3f(cx+3f, 0f, cz+3f), RandomUtils.item(random, ROT));
				}
				else if(p01) {
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz), ROT_270);
					tile.addActor(client.meshSetMid, new Vector3f(cx, 0f, cz+3f), ROT_0);
					tile.addActor(client.meshSetCOut, new Vector3f(cx+3f, 0f, cz+3f), ROT_270);
				}
				else if(p10) {
					tile.addActor(client.meshSetMid, new Vector3f(cx+3f, 0f, cz), ROT_0);
					tile.addActor(client.meshSetWall, new Vector3f(cx, 0f, cz+3f), ROT_180);
					tile.addActor(client.meshSetCOut, new Vector3f(cx+3f, 0f, cz+3f), ROT_90);
				}
			}
			else {
				if(p01 && p10) {
					tile.addActor(client.meshSetMid, new Vector3f(cx+3f, 0f, cz), ROT_0);
					tile.addActor(client.meshSetMid, new Vector3f(cx, 0f, cz+3f), ROT_0);
					tile.addActor(client.meshSetCOut, new Vector3f(cx+3f, 0f, cz+3f), ROT_180);
				}
				else if(p01) {
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz), ROT_270);
					tile.addActor(client.meshSetMid, new Vector3f(cx, 0f, cz+3f), ROT_0);
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz+3f), ROT_270);
				}
				else if(p10) {
					tile.addActor(client.meshSetMid, new Vector3f(cx+3f, 0f, cz), ROT_0);
					tile.addActor(client.meshSetWall, new Vector3f(cx, 0f, cz+3f), ROT_180);
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz+3f), ROT_180);
				}
				else {
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz), ROT_270);
					tile.addActor(client.meshSetWall, new Vector3f(cx, 0f, cz+3f), ROT_180);
					tile.addActor(client.meshSetCIn, new Vector3f(cx+3f, 0f, cz+3f), ROT_180);
				}
			}
		}
		else {
			if(!p11) {
				if(!p01 && p10) {
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz), ROT_90);
					tile.addActor(client.meshSetCIn, new Vector3f(cx+3f, 0f, cz+3f), ROT_90);
				}
				else if(!p10 && p01) {
					tile.addActor(client.meshSetWall, new Vector3f(cx, 0f, cz+3f), ROT_0);
					tile.addActor(client.meshSetCIn, new Vector3f(cx+3f, 0f, cz+3f), ROT_270);
				}
			}
			else {
				if(!p01 && !p10) {
					tile.addActor(client.meshSetCIn, new Vector3f(cx+3f, 0f, cz+3f), ROT_0);
				}
				else if(!p01) {
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz), ROT_90);
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz+3f), ROT_90);
				}
				else if(!p10) {
					tile.addActor(client.meshSetWall, new Vector3f(cx, 0f, cz+3f), ROT_0);
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz+3f), ROT_0);
				}
				else {
					tile.addActor(client.meshSetWall, new Vector3f(cx+3f, 0f, cz), ROT_90);
					tile.addActor(client.meshSetWall, new Vector3f(cx, 0f, cz+3f), ROT_0);
					tile.addActor(client.meshSetCOut, new Vector3f(cx+3f, 0f, cz+3f), ROT_0);
				}
			}
		}
	}

}
