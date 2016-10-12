package com.xrbpowered.greenhouse.render;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.shaders.VertexInfo;
import com.xrbpowered.gl.scene.Scene;

public class GreenhouseLightShader extends GreenhouseShader {

	public static final String LIGHTS_SAMPLER_NAME = "texLightColors";
	public static final int LIGHTS_TEXTURE_CHANNEL = 8;
	
	private static final Vector4f NO_LIGHT = new Vector4f(0f, 0f, 0f, 0f);
	
	private int localLightOffsetLoction;
	private int localLightColorLoction;
	private int localLightRangeLoction;
	
	public GreenhouseLightShader(GreenhouseEnvironment env, Scene scene, VertexInfo info, String pathVS, String pathFS) {
		super(env, scene, info, pathVS, pathFS);
	}

	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		localLightOffsetLoction = GL20.glGetUniformLocation(pId, "localLightOffset");
		localLightColorLoction = GL20.glGetUniformLocation(pId, "localLightColor");
		localLightRangeLoction = GL20.glGetUniformLocation(pId, "localLightRange");
		GL20.glUseProgram(pId);
		GL20.glUniform1i(GL20.glGetUniformLocation(pId, LIGHTS_SAMPLER_NAME), LIGHTS_TEXTURE_CHANNEL);
		GL20.glUseProgram(0);
	}
	
	@Override
	public void setGlobalLighting(Vector4f ambientColor) {
		GL20.glUseProgram(pId);
		uniform(ambientColorLocation, ambientColor);
		uniform(localLightColorLoction, NO_LIGHT);
		GL20.glUseProgram(0);
	}
	
	public void setLocalLighting(Vector4f ambientColor, Vector3f localLightOffset, Vector4f localLightColor, float range) {
		uniform(ambientColorLocation, ambientColor);
		uniform(localLightOffsetLoction, localLightOffset);
		uniform(localLightColorLoction, localLightColor);
		GL20.glUniform1f(localLightRangeLoction, range);
	}

}
