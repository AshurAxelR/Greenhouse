package com.xrbpowered.greenhouse.render;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.collider.ColliderEdge;
import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.gl.scene.Scene;

public class TileActor extends Actor {

	public static final int LIGHT_RANGE = 1;
	private static final int MAX_LIGHTS = (LIGHT_RANGE*2+1)*(LIGHT_RANGE*2+1);
	
	private static final Vector3f LIGHT_ATT = new Vector3f(1f, 0.14f, 0.07f);
	private static final Vector3f[] LIGHT_ATTS;
	static {
		LIGHT_ATTS = new Vector3f[MAX_LIGHTS];
		for(int i=0; i<LIGHT_ATTS.length; i++)
			LIGHT_ATTS[i] = LIGHT_ATT;
	}
	
	public Vector3f lightPosition = null;
	public Vector4f lightColor = null;
	public int lightIndex = -1;
	
	public ArrayList<PrefabActor> prefabs = new ArrayList<>();

	public static ArrayList<ColliderEdge> allColliders = new ArrayList<>();
	public static float[] getColliderLineData() {
		int num = allColliders.size();
		float[] data = new float[num*4];
		int offs = 0;
		for(int i=0; i<num; i++) {
			ColliderEdge e = allColliders.get(i);
			data[offs] = e.pivot.x;
			data[offs+1] = e.pivot.y;
			data[offs+2] = e.pivot.x+e.delta.x;
			data[offs+3] = e.pivot.y+e.delta.y;
			offs += 4;
		}
		return data;
	}
	
/*	public int numLights = 0;
	public int[] lightIndices = new int[MAX_LIGHTS];
	public Vector3f[] lightPositions = new Vector3f[MAX_LIGHTS];
	public Vector4f[] lightColors = new Vector4f[MAX_LIGHTS];*/

	public TileActor(Scene scene, float x, float z) {
		super(scene);
		position = new Vector3f(x, 0f, z);
		updateTransform();
	}
	
	public void addActor(Prefab prefab, Vector3f position, Vector3f rotation) {
		PrefabActor actor = new PrefabActor(scene, prefab);
		actor.position = position;
		actor.rotation = rotation;
		actor.updateTransform();
		prefabs.add(actor);
		for(ColliderEdge edge : prefab.colliders) {
			ColliderEdge e = new ColliderEdge(edge);
			e.transformWithActor(actor);
			allColliders.add(e);
		}
	}
	
/*	public void setLights() {
		StandardShader.environment.setPointLights(numLights, lightPositions, lightColors, LIGHT_ATTS);
	}*/
	
}
