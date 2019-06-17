package com.xrbpowered.greenhouse.render;

import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.scene.Scene;

public class GlassBlurShader extends GreenhouseShader {

	private static final String[] SAMLER_NAMES = {"texNormal", "texBlurMask", "texBuffer", "texBlur"};

	public GlassBlurShader(GreenhouseEnvironment env, Scene scene) {
		super(env, scene, StandardShader.standardVertexInfo, "shaders/glass2_v.glsl", "shaders/glass2_f.glsl");
	}

	@Override
	protected int bindAttribLocations() {
		return PrefabComponent.bindShader(this, super.bindAttribLocations());
	}
	
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		initSamplers(SAMLER_NAMES);
	}
	
}
