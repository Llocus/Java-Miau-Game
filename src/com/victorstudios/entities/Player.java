package com.victorstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.victorstudios.graficos.Spritesheet;
import com.victorstudios.main.Game;
import com.victorstudios.world.Camera;
import com.victorstudios.world.World;

public class Player extends Entity{
	
	public boolean right,up,left,down;
	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = right_dir;
	public double speed = 1;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] upPlayer;
	private BufferedImage[] downPlayer;
	private BufferedImage[] paradoRightPlayer;
	private BufferedImage[] paradoLeftPlayer;
	private BufferedImage[] paradoUpPlayer;
	private BufferedImage[] paradoDownPlayer;
	
	private BufferedImage playerDamage;
	
	private boolean arma = false;
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean shot = false, mouseShot = false;
	
	public double life = 100, maxLife=100;
	public int mx,my;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		upPlayer = new BufferedImage[4];
		downPlayer = new BufferedImage[4];
		paradoRightPlayer = new BufferedImage[1];
		paradoLeftPlayer = new BufferedImage[1];
		paradoUpPlayer = new BufferedImage[1];
		paradoDownPlayer = new BufferedImage[1];
		
		for(int i = 0; i < 4; i++) {
		rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
		leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
		upPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 48, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
		downPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 64, 16, 16);
		}
		paradoRightPlayer[0] = Game.spritesheet.getSprite(48, 32, 16, 16);
		paradoLeftPlayer[0] = Game.spritesheet.getSprite(64, 32, 16, 16);
		paradoUpPlayer[0] = Game.spritesheet.getSprite(32, 32, 16, 16);
		paradoDownPlayer[0] = Game.spritesheet.getSprite(80, 32, 16, 16);
	}
	
	public void tick() {
		moved = false;
		if(right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
			
		} else if (left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
			
		} if(up && World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			dir = up_dir;
			y-=speed;
			
		}else if (down && World.isFree(this.getX(),(int)(y+speed))) {
			moved = true;
			dir = down_dir;
			y+=speed;
			
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
			
		}
		
		checkItems();
		
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if(shot) {
			shot = false;
			if(arma && ammo > 0) {
				
			ammo--;
			
			int dx = 0;
			int px = 0;
			int py = 7;
			if(dir == right_dir) {
				px = 6;
				dx = 1;
			} else {
				px = 6;
				dx = -1;
			}
			
			BulletShot bullet = new BulletShot(this.getX()+px,this.getY()+py,3,3,null,dx,0);
			Game.bullets.add(bullet);
			}	
			
		}
		
		if(mouseShot) {
			mouseShot = false;

			
			if(arma && ammo > 0) {
				
			ammo--;
			
			int px = 0, py = 7;
			double angle = 0;
			if(dir == right_dir) {
				px = 6;
				angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
			} else {
				px = 6;
				angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
			}
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			
			BulletShot bullet = new BulletShot(this.getX()+px,this.getY()+py,3,3,null,dx,dy);
			Game.bullets.add(bullet);
			}
		}
		
		if(life <= 0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2),0,World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2),0,World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void checkItems() {
		for(int i=0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Lifepack) {
				if(Entity.isColidding(this, e)) {
					life+=8;
					if(life >= 100)
						life = 100;
					Game.entities.remove(i);
					return;
				}
			}
			if(e instanceof Bullet) {
				if(Entity.isColidding(this, e)) {
					ammo+=30;
					if(ammo >= 100)
						ammo = 100;
					System.out.println("Balas: " + ammo);
					Game.entities.remove(i);
					return;
				}
			}
			if(e instanceof Weapon) {
				if(Entity.isColidding(this, e)) {
					arma = true;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
		if(dir == right_dir) {
			if(!right)
				g.drawImage(paradoRightPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
			else
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(arma) {
					if(arma && right) {g.drawImage(Entity.GUN_RIGHT, this.getX()+1 - Camera.x, this.getY() - Camera.y, null);} else {g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x, this.getY() - Camera.y, null);}
				}
		} else if(dir == left_dir) {
			if(!left)
				g.drawImage(paradoLeftPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
			else 
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(arma) {
					if(arma && left) {g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x, this.getY() - Camera.y, null);} else {g.drawImage(Entity.GUN_LEFT, this.getX()+1 - Camera.x, this.getY() - Camera.y, null);}
				}
		} else if(dir == up_dir) {
			if(!up)
				g.drawImage(paradoUpPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
			else 
				g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} else if(dir == down_dir) {
			if(!down)
				g.drawImage(paradoDownPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
			else 
				g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(arma) {
				if(arma && down) {g.drawImage(Entity.GUN_DOWN, this.getX() - Camera.x, this.getY() - Camera.y, null);} else {g.drawImage(Entity.GUN_ParadoBaixo, this.getX() - Camera.x, this.getY() - Camera.y, null);}
				//if(arma && !right) {g.drawImage(Entity.GUN_ParadoBaixo, this.getX()+1 - Camera.x, this.getY() - Camera.y, null);} else {g.drawImage(Entity.GUN_ParadoBaixo, this.getX() - Camera.x, this.getY() - Camera.y, null);}
			}
		}
		} else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
		
		
	}
	
}
