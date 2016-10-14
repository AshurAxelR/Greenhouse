package com.xrbpowered.greenhouse.render;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.collider.ColliderEdge;
import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.gl.scene.Scene;

public class TileActor extends Actor {

	public Vector3f lightPosition = null;
	public Vector4f lightColor = null;
	
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
	
}
