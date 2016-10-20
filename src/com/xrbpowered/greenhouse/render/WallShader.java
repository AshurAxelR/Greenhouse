package com.xrbpowered.greenhouse.render;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector2f;

import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.scene.Scene;

public class WallShader extends GreenhouseLightShader {

	private static final String[] SAMLER_NAMES = {"texDiffuse", "texNormal", "texSpecMask", "texMtlMask", "mtlStack"};

	public WallShader(GreenhouseEnvironment env, Scene scene) {
		super(env, scene, StandardShader.standardVertexInfo, "shaders/wallt_v.glsl", "shaders/wallt_f.glsl");
	}
	
	protected WallShader(GreenhouseEnvironment env, Scene scene, String pathVS, String pathFS) {
		super(env, scene, StandardShader.standardVertexInfo, pathVS, pathFS);
	}

	@Override
	protected int bindAttribLocations() {
		return PrefabComponent.bindShader(this, super.bindAttribLocations());
	}
	
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		initSamplers(SAMLER_NAMES);
	};
	
	public void setMaterialTiling(Vector2f tiling) {
		uniform(GL20.glGetUniformLocation(pId, "mtlTiling"), tiling);
	}
	
}
