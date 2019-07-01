package com.clarksonjing.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture gameover;
	//ShapeRenderer shapeRenderer;
	int score=0;
	BitmapFont font;



	Texture[] birds;
	int flyState=0;
	int gameState=0;
	float birdY=0;
	float velocity=0;
	float gravity=2;//加速下滑
	Circle birdCircle;

	Texture topTube;
	Texture bottomTube;
	float gap=400;
	float maxTubeOffset;
	Random randomGenerator;
	int numberOfTubes=4;
	float[] tubeOffset=new float[numberOfTubes];
	float[] tubeX=new float[numberOfTubes];
	float tubeVelocity;
	float distanceBetweenTubes;
    Rectangle[] topRectangles;
	Rectangle[] bottomRectangles;
	int scoringTube=0;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background=new Texture("bg.png");
		birds=new Texture[2];
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");


		gameover = new Texture("gameover.png");

		//shapeRenderer=new ShapeRenderer();
		birdCircle=new Circle();
		topRectangles=new Rectangle[numberOfTubes];
		bottomRectangles=new Rectangle[numberOfTubes];

		topTube=new Texture("toptube.png");
		bottomTube=new Texture("bottomtube.png");

		maxTubeOffset=Gdx.graphics.getHeight()/2-gap/2-100;
		randomGenerator= new Random();
		tubeVelocity=4;
		distanceBetweenTubes=Gdx.graphics.getWidth()/2;// you can choose 3/4

		font=new BitmapFont();
		font.setColor(Color.BLACK);
		font.getData().setScale(5);

		startGame();


	}
	public void startGame()
	{
		birdY=Gdx.graphics.getHeight()/2-birds[0].getHeight()/2;
		for(int i=0;i<numberOfTubes;i++)
		{

			tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()- gap -200);
			tubeX[i]=Gdx.graphics.getWidth()/2-topTube.getWidth()/2+Gdx.graphics.getWidth()+i*distanceBetweenTubes;// TO MAKE SURE THE RIGHT POSITION.
			topRectangles[i]=new Rectangle();
			bottomRectangles[i]=new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState ==1)
		{
			if(tubeX[scoringTube]<Gdx.graphics.getWidth()/2)
			{
				score++;
				if(scoringTube < numberOfTubes-1)
					scoringTube++;
				else
					scoringTube=0;

			}
			if(Gdx.input.justTouched())
			{
				velocity= -30;
			}
			for(int i=0;i<numberOfTubes;i++)
			{
				if(tubeX[i]<-topTube.getWidth())
				{
					tubeX[i]+=numberOfTubes*distanceBetweenTubes;
					tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()- gap -200);
				}

				else
				{
					tubeX[i]=tubeX[i]-tubeVelocity;

				}
				batch.draw(topTube,tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i]);
				batch.draw(bottomTube,tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i]);
				topRectangles[i]=new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i],topTube.getWidth(),topTube.getHeight());
				bottomRectangles[i]=new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());


			}



			if(birdY>0)
			{
				velocity+=gravity;
				birdY-=velocity;
			}
			else
			{
				gameState=2;
			}

		}
		else
		if(gameState==0)
		{
			if(Gdx.input.justTouched())
				gameState=1;
		}
		else
		if(gameState==2)
		{
			batch.draw(gameover,Gdx.graphics.getWidth()/2-gameover.getWidth()/2,Gdx.graphics.getHeight()/2-gameover.getHeight()/2);
			if(Gdx.input.justTouched())
			{
				gameState=1;
				startGame();
				score=0;
				scoringTube=1;
				velocity=0;
			}

		}

		if(flyState==0)
			flyState=1;
		else
		   flyState=0;



		batch.draw(birds[flyState],Gdx.graphics.getWidth()/2-birds[flyState].getWidth()/2,birdY);
		font.draw(batch,String.valueOf(score),100,100);


		birdCircle.set(Gdx.graphics.getWidth()/2,birdY+birds[flyState].getHeight()/2,birds[flyState].getWidth()/2);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
		for(int i=0;i<numberOfTubes;i++)
		{
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
			if(Intersector.overlaps(birdCircle,topRectangles[i])||Intersector.overlaps(birdCircle,bottomRectangles[i]))
				gameState=2;//over

		}
	//	shapeRenderer.end();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
