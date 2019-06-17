package com.xrbpowered.greenhouse.render;

import java.awt.Color;
import java.awt.RenderingHints;

import com.xrbpowered.gl.examples.ExampleClient;
import com.xrbpowered.gl.ui.AbstractLoadScreen;

public class LoadScreen extends AbstractLoadScreen {

	public LoadScreen(int maxProgress) {
		super(maxProgress, 256, 64, Color.BLACK);
	}
	
	protected boolean updateBuffer(java.awt.Graphics2D g2, int w, int h, float time, float progress) {
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, w, h);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(ExampleClient.SMALL_FONT);
		g2.setColor(Color.WHITE);
		g2.drawString("Loading...", 0, h/2);
		if(progress>0f) {
			g2.setColor(new Color(0x333333));
			g2.fillRect(0, h/2+8, 256, 4);
			g2.setColor(Color.WHITE);
			g2.fillRect(0, h/2+8, (int)(progress*256f), 4);
			return true;
		}
		else {
			return false;
		}
	}

}
