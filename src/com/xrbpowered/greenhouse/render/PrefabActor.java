package com.xrbpowered.greenhouse.render;

import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.gl.scene.Scene;

public class PrefabActor extends Actor {

	public final Prefab prefab;
	
	public PrefabActor(Scene scene, Prefab prefab) {
		super(scene);
		this.prefab = prefab;
	}

	// TODO depth sort
	
}
