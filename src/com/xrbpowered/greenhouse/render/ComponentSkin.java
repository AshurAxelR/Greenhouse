package com.xrbpowered.greenhouse.render;

import com.xrbpowered.gl.res.shaders.Shader;

public interface ComponentSkin {

	public void use(int renderPass, Shader shader);
	public void destroy();
	
}
