package com.xrbpowered.greenhouse.render;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.shaders.SceneShader;
import com.xrbpowered.gl.res.shaders.StandardShader;
import com.xrbpowered.gl.scene.Scene;

public class MapShader extends SceneShader {

	public MapShader(Scene scene) {
		super(scene, StandardShader.standardVertexInfo, "wallt_v.glsl", "blank_fog_f.glsl");
	}

	@Override
	protected int bindAttribLocations() {
		return PrefabComponent.bindShader(this, super.bindAttribLocations());
	}
	
	public void setPlainColor(Vector4f color) {
		GL20.glUseProgram(pId);
		uniform(GL20.glGetUniformLocation(pId, "color"), color);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogFar"), 0f);
	}	
	
	public void setFog(float near, Vector4f nearColor, float far, Vector4f farColor) {
		GL20.glUseProgram(pId);
		uniform(GL20.glGetUniformLocation(pId, "color"), nearColor);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogNear"), near);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogFar"), far);
		uniform(GL20.glGetUniformLocation(pId, "fogColor"), farColor);
		GL20.glUseProgram(0);
	}

}
