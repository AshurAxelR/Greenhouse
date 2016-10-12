package com.xrbpowered.greenhouse.render;

import java.awt.Graphics2D;

import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.scene.ShaderEnvironment;
import com.xrbpowered.greenhouse.GreenhouseClient;
import com.xrbpowered.greenhouse.map.GreenhouseMap;

public class GreenhouseEnvironment extends ShaderEnvironment<GreenhouseShader> {

	public static final Vector4f DEFAULT_AMBIENT = new Vector4f(0.1f, 0.1f, 0.1f, 0f);
	public static final Vector4f INNER_AMBIENT = new Vector4f(0.2f, 0.2f, 0.2f, 0f);
	
	private static final int MAP_SIZE = 256;
	
	private BufferTexture mapLightColors = null;

	public GreenhouseEnvironment initLightColors(final GreenhouseClient client) {
		mapLightColors = new BufferTexture(MAP_SIZE, MAP_SIZE, false, false, true) {
			@Override
			protected boolean updateBuffer(Graphics2D g2) {
				GreenhouseMap map = client.getMap();
				if(map==null)
					return false;
				return map.updateMapLightColors(g2, getWidth(), getHeight());
			}
		};
		return this;
	}
	
	public void updateLightColors() {
		if(mapLightColors==null)
			return;
		Texture.unbind(GreenhouseLightShader.LIGHTS_TEXTURE_CHANNEL);
		mapLightColors.update();
		mapLightColors.bind(GreenhouseLightShader.LIGHTS_TEXTURE_CHANNEL);
	}
	
	public void setFog(float near, float far, Vector4f color) {
		for(GreenhouseShader shader : shaders)
			shader.setFog(near, far, color);
	}

	public void setGlobalLighting(Vector4f ambientColor) {
		for(GreenhouseShader shader : shaders)
			shader.setGlobalLighting(ambientColor);
	}
}
