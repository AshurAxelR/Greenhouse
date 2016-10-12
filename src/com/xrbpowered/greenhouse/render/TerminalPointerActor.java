package com.xrbpowered.greenhouse.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.xrbpowered.gl.examples.ExampleClient;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.ui.UIManager;
import com.xrbpowered.gl.ui.UIPointerActor;

public class TerminalPointerActor extends UIPointerActor {

	private static final int SIZE = 32;
	
	public final StaticMeshActor link;
	
	public TerminalPointerActor(UIManager ui, StaticMeshActor link) {
		super(ui, link.scene, SIZE+120, SIZE, true);
		this.link = link;
		pivotx = SIZE/2;
	}
	
	@Override
	public void updateView() {
		super.updateView();
		if(visible && pane.isVisible())
			pane.repaint();
	}
	
	@Override
	protected boolean updateBuffer(Graphics2D g2) {
		g2.setBackground(new Color(1f, 1f, 1f, 0f));
		g2.clearRect(0, 0, SIZE+100, SIZE);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(2f));
		g2.drawPolygon(new int[] {SIZE/2-SIZE/3, SIZE/2, SIZE/2+SIZE/3, SIZE/2}, new int[] {SIZE/2, SIZE/2-SIZE/3, SIZE/2, SIZE/2+SIZE/3}, 4);
		g2.fillPolygon(new int[] {SIZE/2-SIZE/5, SIZE/2, SIZE/2+SIZE/5, SIZE/2}, new int[] {SIZE/2, SIZE/2-SIZE/5, SIZE/2, SIZE/2+SIZE/5}, 4);
		g2.setStroke(new BasicStroke(1f));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.drawLine(SIZE/2+SIZE/3, SIZE/2, SIZE+100, SIZE/2);
		g2.setFont(ExampleClient.LARGE_FONT);
		g2.drawString("CRYSTAL", SIZE, SIZE/2-3);
		g2.setFont(ExampleClient.SMALL_FONT);
		g2.drawString(String.format("%.1fm", dist), SIZE, SIZE/2+15);
		return true;
	}

}
