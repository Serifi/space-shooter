package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Random;

public class SpaceShooter extends ScreenAdapter {

	//Windowsize
	public static int windowHeight = 800;
	public static int windowWidth = 1600;

	//Texture
	private SpriteBatch batch;
	private BackgroundManager backgroundManager;
	public static Texture spritesheet;
	private BitmapFont font;

	private TextureRegion playerTexture;
	private TextureRegion enemy1Texture;
	private TextureRegion enemy2Texture;
	private TextureRegion enemy3Texture;
	private TextureRegion laserTexture;
	private TextureRegion explosionTexture;

	//Rectangle
	private Rectangle player;
	private Rectangle explosion;
	private Rectangle collision;
	private Array<Rectangle> laser;
	private Array<Rectangle> enemy1;
	private Array<Rectangle> enemy2;
	private Array<Rectangle> enemy3;

	//Sound
	private Sound laserSound;
	private Sound explosionSound;
	private Sound collisionSound;
	private Sound gameoverSound;
	private Music backgroundMusic;

	//Time
	public long lastEnemySpawnTime;
	public long lastLaserSpawnTime;
	public long lastEnemyExplosionTime;
	public long lastPlayerCollisionTime;

	//Counter
	private int score;
	private int shield;
	private int level;

	//Main für Menü
	private Main main;

	//Konstruktor
	public SpaceShooter(Main main) {
		//Main und Batch Verknüpfung
		batch = main.batch;
		this.main = main;

		//Texture
		spritesheet = new Texture("spritesheet.png");
		backgroundManager = new BackgroundManager();
		font = new BitmapFont();
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		playerTexture = new TextureRegion(spritesheet, 0, 30, 175, 70);
		laserTexture = new TextureRegion(spritesheet, 0, 355, 41, 21);
		enemy1Texture = new TextureRegion(spritesheet, 0, 110, 92, 90);
		enemy2Texture = new TextureRegion(spritesheet, 0, 200, 260, 80);
		enemy3Texture = new TextureRegion(spritesheet, 0, 296, 390, 40);
		explosionTexture = new TextureRegion(spritesheet, 0, 380, 75, 72);

		//Sound
		laserSound = Gdx.audio.newSound(Gdx.files.internal("Laser.mp3"));
		explosionSound = Gdx.audio.newSound(Gdx.files.internal("Explosion.mp3"));
		collisionSound = Gdx.audio.newSound(Gdx.files.internal("Collision.mp3"));
		gameoverSound = Gdx.audio.newSound(Gdx.files.internal("GameOver.mp3"));
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Background.mp3"));
		backgroundMusic.setVolume(0.1f);
		backgroundMusic.setLooping(true);
		backgroundMusic.play();

		//Rectangle
		enemy1 = new Array();
		enemy2 = new Array();
		enemy3 = new Array();
		laser = new Array();
		player = new Rectangle();
		createPlayer();
		explosion = new Rectangle();
		collision = new Rectangle();

		//Counter
		score = 0;
		shield = 3;
		level = 1;
	}

	//Methoden
	public void createPlayer(){
		player.width = 175;
		player.height = 70;
		player.x = 50;
		player.y = windowHeight /2f - 70/2f;		//Bildschirmitte der y-Achse starten
	}

	public Rectangle createEnemy(int height, int width){
		Random random = new Random();
		lastEnemySpawnTime = TimeUtils.nanoTime();

		Rectangle rectangle =  new Rectangle();
		rectangle.y = 100 + random.nextInt( 700 - 100 + 1);	//Zufällig auf der y - Achse mit 100px Abstand zum Rand spawnen
		rectangle.x = windowWidth - width;
		rectangle.width = width;
		rectangle.height = height;

		return rectangle;
	}

	public void createLaser(){
		lastLaserSpawnTime = TimeUtils.nanoTime();

		Rectangle rectangle =  new Rectangle();
		rectangle.y = player.y + player.height/2;	//Laser erscheint von der Mitte des Players
		rectangle.x = player.x + player.width;
		rectangle.width = 80;
		rectangle.height = 38;

		laser.add(rectangle);
		laserSound.play(0.1f);
	}

	public void checkHit(Array<Rectangle> enemy){
		for (Rectangle rectangleL : laser) {
			for (Rectangle rectangle : enemy) {
				if(rectangle.overlaps(rectangleL)){
					laser.removeValue(rectangleL, true);
					enemy.removeValue(rectangle, true);
					score++;

					explosionSound.play(0.3f);
					explosion.x = rectangle.x;
					explosion.y = rectangle.y;
					lastEnemyExplosionTime = TimeUtils.millis();
				}
			}
		}
	}

	public void checkCollision(Array<Rectangle> enemy){
		for (Rectangle rectangle : enemy) {
			if(player.overlaps(rectangle)){
				enemy.removeValue(rectangle,true);
				shield--;

				collisionSound.play(0.1f);
				collision.x = rectangle.x;
				collision.y = rectangle.y;
				lastPlayerCollisionTime = TimeUtils.millis();
			}
		}
	}

	public void drawEnemy(Array<Rectangle> enemy, TextureRegion enemyTexture){
		for (Rectangle rectangle : enemy) {
			if(rectangle.x >= windowWidth) enemy.removeValue(rectangle, true);	//Enemy außerhalb Bildschirm entfernen
			batch.draw(enemyTexture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		}
	}

	@Override
	public void render (float delta) {

		//Enemy erzeugen und Level festlegen
		if(score <= 15 && TimeUtils.nanoTime() - lastEnemySpawnTime > 950000000){	//Level 1 / Score 0 - 15 / Kleine & langsame Raketten
			enemy1.add(createEnemy(90, 92));
		} else if(score > 15 && score <= 30 && TimeUtils.nanoTime() - lastEnemySpawnTime > 800000000){	//Level 2 / Score 16 - 30 / Mittelgroße & mittelschnelle Raketten
			enemy2.add(createEnemy(80,260));
		} else if(score > 30 && score <= 50 && TimeUtils.nanoTime() - lastEnemySpawnTime > 800000000){	//Level 3 / Score 31 - 50 / Große & schnelle Raketten
			enemy3.add(createEnemy(40,390));
		}else if(score > 50 && TimeUtils.nanoTime() - lastEnemySpawnTime > 800000000){	//Level 4 / Score > 50 / Alle Raketten
			enemy1.add(createEnemy(90, 92));
			enemy2.add(createEnemy(80,260));
			enemy3.add(createEnemy(40,390));
		}

		//Level
		if (score > 15) level = 2;
		if (score > 30) level = 3;
		if (score > 50) level = 4;

		//Enemy Bewegung mit passender Geschwindigkeit
		for (Rectangle rectangle : enemy1) rectangle.x -= 500 * Gdx.graphics.getDeltaTime();
		for (Rectangle rectangle : enemy2) rectangle.x -= 700 * Gdx.graphics.getDeltaTime();
		for (Rectangle rectangle : enemy3) rectangle.x -= 1000 * Gdx.graphics.getDeltaTime();

		//Input des Players in x & y-Richtung
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) player.y -= 300 * Gdx.graphics.getDeltaTime();
		else if (Gdx.input.isKeyPressed(Input.Keys.UP)) player.y += 300 * Gdx.graphics.getDeltaTime();
		else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.x += 300 * Gdx.graphics.getDeltaTime();
		else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.x -= 300 * Gdx.graphics.getDeltaTime();
		else if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			if(TimeUtils.nanoTime() - lastLaserSpawnTime > 500000000){
				createLaser();
				lastLaserSpawnTime = TimeUtils.nanoTime();
			}
		}

		//Player nicht aus dem Bildschirm lassen
		if (player.y > windowHeight - 70) player.y = windowHeight - 75;
		else if (player.y < 0) player.y = 0;
		else if (player.x > windowWidth + 175) player.x = windowWidth;
		else if (player.x < 0) player.x = 0;

		//Lasertreffer prüfen
		checkHit(enemy1);
		checkHit(enemy2);
		checkHit(enemy3);

		//Kollision mit dem Spieler prüfen
		checkCollision(enemy1);
		checkCollision(enemy2);
		checkCollision(enemy3);

		//Menü und Sound nach Niederlage
		if (shield == 0){
			main.setScreen(new Menu(main, score));
			gameoverSound.play(0.4f);
		}

		//Sreen clear
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		//Background
		backgroundManager.renderBackground(batch);

		//Player
		batch.draw(playerTexture, player.x, player.y, player.width, player.height);

		//Font
		font.setColor(1, 1, 1, 1);
		font.getData().setScale(3f);
		GlyphLayout layout1  = new GlyphLayout(font, "Level: " + level);
		GlyphLayout layout2 = new GlyphLayout(font, "" + score);
		GlyphLayout layout3 = new GlyphLayout(font, "Shield: " + shield);
		font.draw(batch, layout1,20, windowHeight - 20);
		font.draw(batch, layout2,windowWidth/2f, windowHeight - 20);
		font.draw(batch, layout3, windowWidth - (layout3.width + 20), windowHeight - 20);

		//Laser
		for (Rectangle rectangleL  : laser) {
			rectangleL.x += 500 * Gdx.graphics.getDeltaTime();
			if(rectangleL.x >= windowWidth) laser.removeValue(rectangleL, true);		//Laser außerhalb Bildschirm entfernen
			batch.draw(laserTexture, rectangleL.x, rectangleL.y, rectangleL.width, rectangleL.height);
		}

		//Enemy
		drawEnemy(enemy1, enemy1Texture);
		drawEnemy(enemy2, enemy2Texture);
		drawEnemy(enemy3, enemy3Texture);

		//Explosion
		if (TimeUtils.millis() - lastEnemyExplosionTime < 300) batch.draw(explosionTexture, explosion.x, explosion.y, 80, 80);
		if (TimeUtils.millis() - lastPlayerCollisionTime < 300) batch.draw(explosionTexture, collision.x, collision.y, 80, 80);

		batch.end();
	}

	@Override
	public void dispose(){
		batch.dispose();
		spritesheet.dispose();
		laserSound.dispose();
		gameoverSound.dispose();
		collisionSound.dispose();
		explosionSound.dispose();
		backgroundMusic.dispose();
	}
}