package com.xrbpowered.greenhouse.map.generate;

public interface AbstractMap<T> {

	public T get(int x, int y);
	public int sizex();
	public int sizey();
	
}
