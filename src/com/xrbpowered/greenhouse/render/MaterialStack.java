package com.xrbpowered.greenhouse.render;

import java.awt.Color;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.xrbpowered.gl.Client;
import com.xrbpowered.gl.res.textures.AbstractTextureCache;
import com.xrbpowered.gl.res.textures.ArrayTexture;
import com.xrbpowered.gl.res.textures.BufferTexture;
import com.xrbpowered.gl.res.textures.Texture;
import com.xrbpowered.gl.ui.AbstractLoadScreen;

public class MaterialStack extends ArrayTexture {

	public static final String[] MATERIAL_NAMES = {
			"paint", // 02, FD
			"concrete", // 04, FB
			"metal_shiny", // 06, F9
			"metal_old", // 08, F7
			"floor_tiles", // F5
			"water" // F3
		};
	
	public static MaterialStack createMaterialStack(AbstractLoadScreen loading) {
		return new MaterialStack(1024, 1024, MATERIAL_NAMES, loading);
	}

	private static final String FORMAT_DIFFUSE = "materials/%s.jpg";
	private static final String FORMAT_NORMAL = "materials/%s_n.jpg";
	private static final String FORMAT_SPEC_MASK = "materials/%s_s.png";
	
	private AbstractTextureCache<Integer> masks;
	
	public MaterialStack(int w, int h, String[] mtlNames, AbstractLoadScreen loading) {
		super(w, h, mtlNames.length*3);
		for(String name : mtlNames) {
			append(String.format(FORMAT_DIFFUSE, name));
			if(loading!=null)
				loading.addProgress(1);
			append(String.format(FORMAT_NORMAL, name));
			if(loading!=null)
				loading.addProgress(1);
			append(String.format(FORMAT_SPEC_MASK, name));
			if(loading!=null)
				loading.addProgress(1);
		}
		finish(true, true);
		masks = new AbstractTextureCache<Integer>() {
			@Override
			protected Texture createForKey(Integer key) {
				return BufferTexture.createPlainColor(4, 4, new Color(key, 0, 255));
			}
		};
	}
	
	public void setAnisotropy(int anisotropy) {
		// TODO globally change on setting update
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, texId);
		GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);
		Client.checkError();
	}
	
	public Texture mask(int mtlId) {
		return masks.get(mtlId);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		masks.destroy();
	}
	
	public static int getMaxProgress() {
		return MATERIAL_NAMES.length*3;
	}
	
}
