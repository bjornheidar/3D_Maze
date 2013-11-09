package com.tgra;

import java.awt.GraphicsDevice;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;


public class First3D_Core implements ApplicationListener
{
	private final float SIZE = 20;
	
	Camera cam;
	
	private Cube cube;
	
	private Floor floor;
	private List<Border> walls;
	private List<Border> adjacentWalls;
	
	float rotationAngle = 0.0f;
	
	float elapsedTime;
	long idleTime;
	long deadTime;
	private boolean dead = false;
	
	@Override
	public void create()
	{
		this.floor = new Floor("sand_floor.png");
		this.cube = new Cube("Wood_Box_Texture.jpg");
		this.walls = new ArrayList<Border>();
		this.adjacentWalls = new ArrayList<Border>();
		
		Gdx.gl11.glEnable(GL11.GL_LIGHTING);
		Gdx.gl11.glEnable(GL11.GL_LIGHT0);
		Gdx.gl11.glEnable(GL11.GL_LIGHT1);
		//Gdx.gl11.glEnable(GL11.GL_LIGHT2);
		//Gdx.gl11.glEnable(GL11.GL_LIGHT3);
		Gdx.gl11.glEnable(GL11.GL_DEPTH_TEST);
		
		Gdx.gl11.glEnable(GL11.GL_NORMALIZE);
		
		Gdx.gl11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
		Gdx.gl11.glLoadIdentity();
		Gdx.glu.gluPerspective(Gdx.gl11, 90, 1.333333f, 0.02f, 30.0f);

		Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		
		initBorders();
		initMaze("maze1.txt");

		//cam = new Camera(new Point3D(1.0f, 1.0f, 19.0f), new Point3D((float)SIZE / 2, 1.0f, (float)SIZE / 2), new Vector3D(0.0f, 1.0f, 0.0f));
		
		elapsedTime = 0.0f;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	
	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		elapsedTime += deltaTime;
		
		rotationAngle += 90.0f * deltaTime;

		if(Gdx.input.isKeyPressed(Input.Keys.W))
		{
			cam.pitch(-90.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S))
		{
			cam.pitch(90.0f * deltaTime);
		}
		/*if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
		{
			cam.yaw(-90.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
		{
			cam.yaw(90.0f * deltaTime);
		}*/
		
		//the player dies if he sinks too deep into the quicksand
		if(cam.eye.y < 0.5f){
			dead = true;
			if(System.currentTimeMillis() - deadTime > 5000){
				Gdx.app.exit();
			}
		}
		
		//the player starts sinking if he stands idle for too long
		/*if(System.currentTimeMillis() - idleTime > 1500 && cam.eye.y > 0.1f){
			cam.eye.y -= 0.0025f;
			deadTime = System.currentTimeMillis();
		}*/
		
		if(!dead){
			if(Gdx.input.isKeyPressed(Input.Keys.UP))
			{ 
				idleTime = System.currentTimeMillis();
				cam.eye.y = 1.0f;
				cam.slide(0.0f, 0.0f, -5.0f * deltaTime);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
			{
				idleTime = System.currentTimeMillis();
				cam.eye.y = 1.0f;
				cam.slide(0.0f, 0.0f, 5.0f * deltaTime);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
			{
				//cam.slide(-10.0f * deltaTime, 0.0f, 0.0f);
				cam.yaw(-180.0f * deltaTime);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			{
				//cam.slide(10.0f * deltaTime, 0.0f, 0.0f);
				cam.yaw(180.0f * deltaTime);
			}
		}
		
		//Collision detection and handling
		for(Border w : adjacentWalls){
			if(w.getOrientation()){
				//Larger side
				if(w.getPos().z - 0.1f <= cam.eye.z && cam.eye.z <= w.getPos().z + 1.1f){
					if(w.getPos().x - 0.15f < cam.eye.x && cam.eye.x < w.getPos().x){
						cam.eye.x = w.getPos().x - 0.15f;
					}
					else if(w.getPos().x - 0.1f < cam.eye.x && cam.eye.x < w.getPos().x + 0.16f){
						cam.eye.x = w.getPos().x + 0.15f;
					}
				}
				
				//the smaller sides of the walls
				if((w.getPos().z - 0.16f < cam.eye.z && cam.eye.z < w.getPos().z - 0.1f) && 
						(w.getPos().x - 0.11f < cam.eye.x && cam.eye.x < w.getPos().x + 0.11f)){
					cam.eye.z = w.getPos().z - 0.15f;
					System.out.println("low Side");
				}
				else if((cam.eye.z < w.getPos().z + 1.25f && w.getPos().z + 1.1f < cam.eye.z) && 
						(w.getPos().x - 0.11f < cam.eye.x && cam.eye.x < w.getPos().x + 0.11f)){
					cam.eye.z = w.getPos().z + 1.15f;
					System.out.println("high Side");
				}
			}
			else{
				if(w.getPos().x <= cam.eye.x && cam.eye.x <= w.getPos().x + 1.0f){
					if(w.getPos().z - 0.15f < cam.eye.z && cam.eye.z < w.getPos().z){
						cam.eye.z = w.getPos().z - 0.15f;
					}
					else if(w.getPos().z < cam.eye.z && cam.eye.z < w.getPos().z + 0.15f){
						cam.eye.z = w.getPos().z + 0.15f;
					}
				}
			}
		}
		

		if(Gdx.input.isKeyPressed(Input.Keys.R))
		{
			cam.slide(0.0f, 10.0f * deltaTime, 0.0f);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.F))
		{
			cam.slide(0.0f, -10.0f * deltaTime, 0.0f);
		}
		
		checkAdjWalls();
	}
	
	private void checkAdjWalls(){
		adjacentWalls.clear();
		for(Border w : walls){
			if(w.getPos().dis(cam.eye) < 1.5f){
				adjacentWalls.add(w);
			}
		}
	}

	private void display()
	{
		Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

		cam.setModelViewMatrix();
		

		float[] lightDiffuse = {0.8f, 0.8f, 0.8f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, lightDiffuse, 0);

		float[] lightPosition = {cam.eye.x, cam.eye.y+2, cam.eye.z, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition, 0);

		/*float[] lightDiffuse1 = {0.5f, 0.5f, 0.5f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, lightDiffuse1, 0);

		float[] lightPosition1 = {cam.eye.x, cam.eye.y, cam.eye.z, 0.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition1, 0);*/
		
		/*float[] lightAmbience = {1.5f, 1.5f, 1.5f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_AMBIENT, lightAmbience, 0);
		
		float[] lightPostition1 = {cam.eye.x, cam.eye.y+1, cam.eye.z, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPostition1, 0);
		
		/*float[] lightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT3, GL11.GL_SPECULAR, lightSpecular, 0);
		
		float[] lightPostition3 = {cam.eye.x, cam.eye.y, cam.eye.z, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT3, GL11.GL_POSITION, lightPostition3, 0);
		
		float[] materialSpecular = {0.5f, 0.5f, 0.5f, 1.0f};
		Gdx.gl11.glMaterialfv(GL11.GL_FRONT, GL11.GL_SPECULAR, materialSpecular, 0);*/
		
		/*float[] materialAmbience = {0.3f, 0.3f, 0.3f, 1.0f};
		Gdx.gl11.glMaterialfv(GL11.GL_FRONT, GL11.GL_AMBIENT, materialAmbience, 0);

		/*float[] materialDiffuse = {0.2f, 0.2f, 0.2f, 1.0f};
		Gdx.gl11.glMaterialfv(GL11.GL_FRONT, GL11.GL_DIFFUSE, materialDiffuse, 0);*/

		//movCube.display();

		Gdx.gl11.glPushMatrix();
		Gdx.gl11.glTranslatef(1.0f, 2.0f, 1.0f);
		Gdx.gl11.glRotatef(rotationAngle, 0.0f, 1.0f, 0.0f);
		//this.cube.draw();
		Gdx.gl11.glPopMatrix();
		
		this.drawFloor();
		this.drawBorder();
		//this.drawWalls();
		
	}
	
	private void drawFloor(){
		for(float fx = 0.0f; fx < SIZE; fx += 1.0){
			for(float fz = 0.0f; fz < SIZE; fz += 1.0){
				Gdx.gl11.glPushMatrix();
				Gdx.gl11.glTranslatef(fx, 0.0f, fz);
				floor.draw();
				Gdx.gl11.glPopMatrix();
			}
		}
	}
	
	private void initBorders() {
		for(float fx = 0.0f; fx < SIZE; fx++){
			this.walls.add(new Border(fx, -0.1f, false, new boolean [] {false, false, false, true}));
			this.walls.add(new Border(fx, SIZE+0.1f, false, new boolean [] {true, false, false, false}));
			this.walls.add(new Border(-0.1f, fx, true, new boolean [] {true, false, false, false}));
			this.walls.add(new Border(SIZE+0.1f, fx, true, new boolean [] {false, false, false, true}));
		}
	}
	
	private void initMaze(String filename){
		String[] in = Gdx.files.internal("assets/mazes/" + filename).readString().split("\n");
		
		Array<Point3D> startingPoints = new Array<Point3D>();
		Array<Point3D> endPoints = new Array<Point3D>();
		
		for(String s : in){
			try{
				System.out.println(s.substring(0, 4));
				if(s.substring(0,4).equalsIgnoreCase("wall")){
					String[] w = s.split(",");
					this.walls.add(new Border(Float.parseFloat(w[1]), Float.parseFloat(w[2]), Boolean.parseBoolean(w[3].trim())));
				}
				else if(s.substring(0,8).equalsIgnoreCase("starting")){
					String [] w = s.split(",");
					
					startingPoints.add(new Point3D(Float.parseFloat(w[1]), 1.0f, Float.parseFloat(w[2].trim())));
				}
				else if(s.substring(0,8).equalsIgnoreCase("finish")){
					String [] w = s.split(",");
					
					endPoints.add(new Point3D(Float.parseFloat(w[1]), 1.0f, Float.parseFloat(w[2].trim())));
				}
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		Random r = new Random();
		cam = new Camera(startingPoints.get(r.nextInt(startingPoints.size)), 
				new Point3D((float)SIZE / 2, 1.0f, (float)SIZE / 2), new Vector3D(0.0f, 1.0f, 0.0f));
	}
	
	private void drawBorder(){
		for(Border w : walls){
			w.draw();
		}
	}
	
	
	@Override
	public void render() {
		update();
		display();
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

}
