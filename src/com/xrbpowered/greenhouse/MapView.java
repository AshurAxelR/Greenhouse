package com.xrbpowered.greenhouse;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.xrbpowered.gl.InputHandler;
import com.xrbpowered.gl.Renderer;
import com.xrbpowered.gl.examples.BlurBackground;
import com.xrbpowered.gl.res.buffers.RenderTarget;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.Projection;
import com.xrbpowered.gl.ui.UIPointerActor;
import com.xrbpowered.greenhouse.render.TerminalPointerActor;

public class MapView implements InputHandler, Renderer {

	public final GreenhouseClient client;
	protected BlurBackground background;
	
	private CameraActor camera;
	private Controller controller;
	private UIPointerActor playerPointer;
	
	public MapView(GreenhouseClient client, BlurBackground background) {
		this.client = client;
		this.background = background;
		camera = new CameraActor(client.getScene()).setProjection(projectionMatrix());
		controller = new Controller() {
			protected void applyVelocity(Vector3f position, Vector4f v) {
				position.x += v.x;
				position.z += v.z;
			}
		}.setActor(camera).setLookController(true);
		controller.moveSpeed = 90f;
		controller.rotateSpeed = 0f;
		
		playerPointer = new UIPointerActor(client.getUI(), client.getScene(), 32, 32, false) {
			@Override
			protected boolean updateBuffer(Graphics2D g2) {
				g2.setBackground(new Color(1f, 1f, 1f, 0f));
				g2.clearRect(0, 0, 32, 32);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				g2.setColor(Color.WHITE);
				g2.setStroke(new BasicStroke(3f));
				g2.drawLine(4, 4, 28, 28);
				g2.drawLine(4, 28, 28, 4);
				return true;
			}
		};
		playerPointer.pane.setVisible(false);
	}
	
	protected Matrix4f projectionMatrix() {
		return Projection.perspective(GreenhouseClient.settings.fov, client.getAspectRatio(), 1f, 600.0f);
	}
	
	public void resizeBackground() {
		background.resizeBuffers();
		background.requestUpdate();
		camera.setProjection(projectionMatrix());
	}
	
	public void start() {
		background.setUpdatePerFrame(true);
		background.startTween(1f, 0.5f);
		CameraActor mainCamera = client.getScene().activeCamera;
		camera.position = new Vector3f(mainCamera.position.x+20f, mainCamera.position.y+45f, mainCamera.position.z+20f);
		camera.rotation = new Vector3f(-(float)Math.PI/3f, (float)Math.PI/4f, 0f);
		camera.updateTransform();
		playerPointer.position = mainCamera.position;
		playerPointer.updateTransform();
		playerPointer.pane.setVisible(true);
	}
	
	@Override
	public void updateTime(float dt) {
		background.updateTime(dt);
	}
	
	@Override
	public void redraw(RenderTarget target) {
		background.redraw(target);
		CameraActor mainCamera = client.getScene().activeCamera; 
		client.getScene().activeCamera = camera;
		
		client.renderStack.renderMap(target, client.getMap());
		
		for(TerminalPointerActor pointer : client.pointerActors) {
			float maxDist = pointer.maxDist;
			pointer.maxDist = -1f;
			pointer.drawText = false;
			pointer.updateView();
			pointer.maxDist = maxDist;
			pointer.drawText = true;
		}
		playerPointer.updateView();
		
		client.getScene().activeCamera = mainCamera;
	}
	
	protected void updateControllers(float dt) {
		controller.update(dt);
	}
	
	protected void keyDown(int key) {
		switch(Keyboard.getEventKey()) {
			case Keyboard.KEY_ESCAPE:
				background.setUpdatePerFrame(false);
				playerPointer.pane.setVisible(false);
				client.showMenu();
				break;
			case Keyboard.KEY_TAB:
				background.setUpdatePerFrame(false);
				playerPointer.pane.setVisible(false);
				client.hideMenu();
				break;
		}
	}
	
	@Override
	public void processInput(float dt) {
		updateControllers(dt);
		while(Keyboard.next()) {
			if(!Keyboard.getEventKeyState())
				continue;
			keyDown(Keyboard.getEventKey());
		}
	}
	
}
