package com.xrbpowered.greenhouse.render;

import com.xrbpowered.gl.scene.Scene;

public class FlatShader extends WallShader {

	private static final String[] SAMLER_NAMES = {"texDiffuse", "texNormal", "texSpecMask", "texMtlMask", "mtlStack", "texSeeThrough"};

	public FlatShader(GreenhouseEnvironment env, Scene scene) {
		super(env, scene, "shaders/wallt_v.glsl", "shaders/flat_f.glsl");
	}

	protected void storeUniformLocations() {
		super.storeUniformLocations();
		initSamplers(SAMLER_NAMES);
	};
}
