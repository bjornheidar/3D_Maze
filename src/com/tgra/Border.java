package com.tgra;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;

public class Border {
	static FloatBuffer vertexBuffer;
	static FloatBuffer texCoordBuffer;
	static Texture tex = new Texture(Gdx.files.internal("assets/textures/" + "wall.png"));
	
	private int thickness;
	private Point3D position;
	private boolean direction;
	
	public Border(int thickness, float posX, float posZ, boolean direction)
	{
		this.thickness = thickness;
		this.position = new Point3D(posX, 0.0f, posZ);
		this.direction = direction;
		
		vertexBuffer = BufferUtils.newFloatBuffer(24);
		vertexBuffer.put(new float[] {0.0f, 0.0f, -0.1f, 0.0f, 1.5f, -0.1f,
									  1.0f, 0.0f, -0.1f, 1.0f, 1.5f, -0.1f,
									  
									  0.0f, 0.0f, -0.1f, 0.0f, 1.5f, -0.1f,
									  0.0f, 0.0f, 0.1f, 0.0f, 1.5f, 0.1f});
		vertexBuffer.rewind();
		
		texCoordBuffer = BufferUtils.newFloatBuffer(48);
		texCoordBuffer.put(new float[] {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
										0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
										0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
										0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
										0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
										0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f});
		texCoordBuffer.rewind();		
	}
	
	
	public void draw(){
		Gdx.gl11.glPushMatrix();
		
		Gdx.gl11.glTranslatef(position.x, position.y, position.z);
		
		if(direction){
			Gdx.gl11.glRotatef(-90, 0.0f, 1.0f, 0.0f);
		}
		
		Gdx.gl11.glShadeModel(GL11.GL_SMOOTH);
		Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, vertexBuffer);
		
		Gdx.gl11.glEnable(GL11.GL_TEXTURE_2D);
		Gdx.gl11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		tex.bind();  //Gdx.gl11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		Gdx.gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, texCoordBuffer);
		
		Gdx.gl11.glNormal3f(0.0f, 1.0f, 0.0f);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		//small sides
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 4, 4);
		Gdx.gl11.glTranslatef(1.0f, 0.0f, 0.0f);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 4, 4);
		
		Gdx.gl11.glTranslatef(-1.0f, 0.0f, 0.2f);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		
		
		
		Gdx.gl11.glDisable(GL11.GL_TEXTURE_2D);
		Gdx.gl11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		Gdx.gl11.glPopMatrix();
	}
	
	public int getThickness(){
		return this.thickness;
	}
}
