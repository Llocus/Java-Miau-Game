package com.victorstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.victorstudios.main.Game;
import com.victorstudios.world.Camera;

public class BulletShot extends Entity{
	
	private double dx;
	private double dy;
	private double spd=4;
	
	private int life=20,curLife=0;
	
	public BulletShot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void tick() {
		x+=dx*spd;
		y+=dy*spd;
		curLife++;
		if(curLife == life) {
			Game.bullets.remove(this);
			return;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}

}
