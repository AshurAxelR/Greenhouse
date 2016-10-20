package com.xrbpowered.greenhouse.render;

import com.xrbpowered.gl.res.shaders.Shader;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.res.textures.TextureCache;
import com.xrbpowered.gl.ui.AbstractLoadScreen;

public class FlatSkin implements ComponentSkin {

	public static final String FORMAT_SEE_THOUGH = "%s_u.png";
	
	public static FlatSkin load(TextureCache textures, String name, AbstractLoadScreen loading) {
		return load(textures, name, null, null, null, null, null, loading);
	}
	
	public static FlatSkin load(TextureCache textures, String name, Texture diffuse, Texture normal, Texture specularMask, Texture materialMask, Texture seeThrough, AbstractLoadScreen loading) {
		WallSkin opaques = WallSkin.load(textures, name, diffuse, normal, specularMask, materialMask, loading);
		Texture texSeeThrough = (seeThrough!=null) ? seeThrough : textures.get(String.format(FORMAT_SEE_THOUGH, name));
		return new FlatSkin(opaques, texSeeThrough);
	}
	
	public final WallSkin opaques;
	public Texture seeThrough;
	
	public FlatSkin(WallSkin opaques, Texture seeThrough) {
		this.opaques = opaques;
		this.seeThrough = seeThrough;
	}
	
	public FlatSkin setMaterialTiling(float u, float v) {
		opaques.setMaterialTiling(u, v);
		return this;
	}

	@Override
	public void use(int renderPass, Shader shader) {
		opaques.use(renderPass, shader);
		seeThrough.bind(5);
	}
	
	@Override
	public void destroy() {
		opaques.destroy();
	}
}
