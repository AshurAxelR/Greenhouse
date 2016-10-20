package com.xrbpowered.greenhouse.render;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.xrbpowered.gl.res.shaders.Shader;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;

public class GlassSkin implements ComponentSkin {

	public Texture diffuse;
	public Texture normal;
	public Texture specularMask;
	public Texture blurMask;
	
	public Vector2f materialTiling = new Vector2f(1f, 1f);
	
	public GlassSkin() {
		this.diffuse = BufferTexture.createPlainColor(4, 4, new Color(0x44737770, true));
		this.normal = new Texture("glass_n.png");
		this.specularMask = BufferTexture.createPlainColor(4, 4, new Color(0x99ff00));
		this.blurMask = new Texture("glass_blur.png");
	}
	
	@Override
	public void use(int renderPass, Shader shader) {
		if(renderPass==RenderStack.PASS_GLASS1) {
			diffuse.bind(0);
			normal.bind(1);
			specularMask.bind(2);
			blurMask.bind(3);
		}
		else {
			normal.bind(0);
			blurMask.bind(1);
		}
	}

	@Override
	public void destroy() {
		diffuse.destroy();
		normal.destroy();
		specularMask.destroy();
		blurMask.destroy();
	}

}
