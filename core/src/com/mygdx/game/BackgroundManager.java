package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

public class BackgroundManager {
    private int counter = 100;   //Maximale Sterne
    private float[] x;
    private float[] y;           //Koordinaten
    private float[] startype;    //Verschiedene Sterne
    private float[] brightness;    //Helligkeit der Sterne
    private float[] starSpeed;   //Geschwindigkeit der Sterne

    private TextureRegion star1;
    private TextureRegion star2;
    private TextureRegion star3;

    private Texture background;
    private int backgroundSpeed;

    BackgroundManager(){
        background = new Texture("background.png");

        star1 = new TextureRegion(SpaceShooter.spritesheet, 0, 0, 30, 30);
        star2 = new TextureRegion(SpaceShooter.spritesheet, 30, 0, 20, 20);
        star3 = new TextureRegion(SpaceShooter.spritesheet, 50, 0, 10, 10);

        x = new float[counter];
        y = new float[counter];
        startype = new float[counter];
        brightness = new float[counter];
        starSpeed = new float[counter];

        Random random = new Random();
        for (int i = 0; i < counter; i++) {
            x[i] = random.nextInt(SpaceShooter.windowWidth);   //Zufällig platzieren
            y[i] = random.nextInt(SpaceShooter.windowHeight);
            startype[i] = random.nextInt(3);    //Zufällig generieren
            brightness[i] = random.nextFloat();
            starSpeed[i] = random.nextFloat()*3;
        }
    }

    public void renderBackground(SpriteBatch spriteBatch){
        if (backgroundSpeed == SpaceShooter.windowWidth) backgroundSpeed = 0;
        spriteBatch.setColor(1.0f, 1.0f, 1.0f, 0.3f);  //Helligkeit des Hintergrundes
        spriteBatch.draw(background, 0, 0, backgroundSpeed, 0, SpaceShooter.windowWidth, SpaceShooter.windowHeight);     //Hintergrund
        backgroundSpeed += 1;

        TextureRegion texture = null;
        for (int i = 0; i < counter; i++) {
            if(startype[i] == 0) texture = star1;   //Zufälligen Stern erzeugen
            if(startype[i] == 1) texture = star2;
            if(startype[i] == 2) texture = star3;

            spriteBatch.setColor(1.0f, 1.0f, 1.0f, brightness[i] * (0.1f + startype[i])/2.0f);  //Helligkeit je nach Sterntyp einstellen
            spriteBatch.draw(texture, x[i], y[i], texture.getRegionWidth(), texture.getRegionHeight()); //Zeichnen

            x[i] -= starSpeed[i] * (0.1f + startype[i]) - 0.1f;  //Sterne von x je nach Sterntyp verschieden schnell fliegen lassen
            if (x[i] < 0){  //Variation
                Random random = new Random();
                x[i] += SpaceShooter.windowWidth;
                y[i] = random.nextInt(SpaceShooter.windowHeight);
                startype[i] = random.nextInt(3);
                brightness[i] = random.nextFloat();
            }
        }
        spriteBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}