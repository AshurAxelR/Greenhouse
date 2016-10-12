package com.xrbpowered.greenhouse.render;

import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.scene.Scene;

public class GlassShader extends GreenhouseLightShader {

	private static final String[] SAMLER_NAMES = {"texDiffuse", "texNormal", "texSpecMask", "texBlurMask"};

	public GlassShader(GreenhouseEnvironment env, Scene scene) {
		super(env, scene, StandardShader.standardVertexInfo, "wallt_v.glsl", "glass1_f.glsl");
	}

	@Override
	protected int bindAttribLocations() {
		return PrefabComponent.bindShader(this, super.bindAttribLocations());
	}
	
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		initSamplers(SAMLER_NAMES);
	};
	
}
