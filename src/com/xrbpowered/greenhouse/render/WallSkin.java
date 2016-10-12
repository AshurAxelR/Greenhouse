package com.xrbpowered.greenhouse.render;

import org.lwjgl.util.vector.Vector2f;

import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.res.textures.TextureCache;
import com.xrbpowered.gl.ui.AbstractLoadScreen;

public class WallSkin implements ComponentSkin {

	public static final String FORMAT_DIFFUSE = "%s.png";
	public static final String FORMAT_NORMAL = "%s_n.png";
	public static final String FORMAT_SPEC_MASK = "%s_s.png";
	public static final String FORMAT_MTL_MASK = "%s_m.png";
	
	public static WallSkin load(TextureCache textures, String name, AbstractLoadScreen loading) {
		return load(textures, name, null, null, null, null, loading);
	}
	
	public static WallSkin load(TextureCache textures, String name, Texture diffuse, Texture normal, Texture specularMask, Texture materialMask, AbstractLoadScreen loading) {
		Texture texDiffuse = (diffuse!=null) ? diffuse : textures.get(String.format(FORMAT_DIFFUSE, name));
		if(loading!=null)
			loading.addProgress(1);
		Texture texNormal = (normal!=null) ? normal : textures.get(String.format(FORMAT_NORMAL, name));
		if(loading!=null)
			loading.addProgress(1);
		Texture texSpecularMask = (specularMask!=null) ? specularMask : textures.get(String.format(FORMAT_SPEC_MASK, name));
		if(loading!=null)
			loading.addProgress(1);
		Texture texMaterialMask = (materialMask!=null) ? materialMask : textures.get(String.format(FORMAT_MTL_MASK, name), true, false);
		if(loading!=null)
			loading.addProgress(1);
		return new WallSkin(texDiffuse, texNormal, texSpecularMask, texMaterialMask);
	}
	
	public Texture diffuse;
	public Texture normal;
	public Texture specularMask;
	public Texture materialMask;
	
	public Vector2f materialTiling = new Vector2f(1f, 1f);
	
	public WallSkin(Texture diffuse, Texture normal, Texture specularMask, Texture materialMask) {
		this.diffuse = diffuse==null ? defaults.diffuse : diffuse;
		this.normal = normal==null ? defaults.normal : normal;
		this.specularMask = specularMask==null ? defaults.specularMask : specularMask;
		this.materialMask = materialMask==null ? defaults.materialMask : materialMask;
	}
	
	public WallSkin(WallSkin src) {
		this.diffuse = src.diffuse;
		this.normal = src.normal;
		this.specularMask = src.specularMask;
		this.materialMask = src.materialMask;
		this.materialTiling = src.materialTiling;
	}
	
	public WallSkin setMaterialTiling(float u, float v) {
		this.materialTiling = new Vector2f(u, v);
		return this;
	}
	
	@Override
	public void use(int renderPass, GreenhouseShader shader) {
		((WallShader) shader).setMaterialTiling(materialTiling);
		diffuse.bind(0);
		normal.bind(1);
		specularMask.bind(2);
		materialMask.bind(3);
	}

	@Override
	public void destroy() {
	}
	
	public static WallSkin defaults = null;

}
