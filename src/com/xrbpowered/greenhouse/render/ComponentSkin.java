package com.xrbpowered.greenhouse.render;

public interface ComponentSkin {

	public void use(int renderPass, GreenhouseShader shader);
	public void destroy();
	
}
