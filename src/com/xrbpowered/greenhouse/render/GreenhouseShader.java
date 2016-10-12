package com.xrbpowered.greenhouse.render;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.shaders.SceneShader;
import com.xrbpowered.gl.res.shaders.VertexInfo;
import com.xrbpowered.gl.scene.Scene;

public class GreenhouseShader extends SceneShader {

	public final GreenhouseEnvironment environment;
	
	protected int ambientColorLocation;
	
	public GreenhouseShader(GreenhouseEnvironment env, Scene scene, VertexInfo info, String pathVS, String pathFS) {
		super(scene, info, pathVS, pathFS);
		this.environment = env;
		env.addShader(this);
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		ambientColorLocation = GL20.glGetUniformLocation(pId, "ambientColor");
	}
	
	public void setFog(float near, float far, Vector4f color) {
		GL20.glUseProgram(pId);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogNear"), near);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogFar"), far);
		uniform(GL20.glGetUniformLocation(pId, "fogColor"), color);
		GL20.glUseProgram(0);
	}

	public void setGlobalLighting(Vector4f ambientColor) {
		GL20.glUseProgram(pId);
		uniform(ambientColorLocation, ambientColor);
		GL20.glUseProgram(0);
	}

	@Override
	public void destroy() {
		super.destroy();
		environment.removeShader(this);
	}
	
}
