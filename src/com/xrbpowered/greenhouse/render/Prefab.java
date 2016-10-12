package com.xrbpowered.greenhouse.render;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import com.xrbpowered.gl.collider.ColliderEdge;

public class Prefab {

	public PrefabComponent[] components;
	public ArrayList<ColliderEdge> colliders = new ArrayList<>();
	
	public Prefab(String... objPaths) {
		components = new PrefabComponent[objPaths.length];
		for(int i=0; i<objPaths.length; i++) {
			components[i] = new PrefabComponent(objPaths[i], null);
		}
	}
	
	public Prefab(PrefabComponent... components) {
		this.components = components;
	}
	
	public boolean hasComponent(PrefabComponent comp) {
		// TODO optimise using BitSet and comp ids
		for(PrefabComponent c : components) {
			if(c==comp)
				return true;
		}
		return false;
	}
	
	public Prefab addColliders(Vector2f... p) {
		for(int i=0; i<p.length-1; i++) {
			ColliderEdge e = new ColliderEdge(p[i], new Vector2f(p[i+1].x-p[i].x, p[i+1].y-p[i].y));
			colliders.add(e);
		}
		return this;
	}
	
}
