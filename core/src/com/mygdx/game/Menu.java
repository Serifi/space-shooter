package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Menu extends ScreenAdapter {
    private Main main;
    private Texture background;
    private OrthographicCamera camera;
    private BitmapFont font;
    private int score;

    public Menu(Main main, int score) {
        this.main = main;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SpaceShooter.windowWidth, SpaceShooter.windowHeight);
        background = new Texture("menu.png");
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.score = score;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        main.batch.setProjectionMatrix(camera.combined);

        if (Gdx.input.isKeyPressed(Input.Keys.R)) main.setScreen(new SpaceShooter(main));
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) System.exit(0);

        main.batch.begin();
        main.batch.setColor(1.0f, 1.0f, 1.0f, 1);
        main.batch.draw(background, 0, 0, 0, 0, SpaceShooter.windowWidth, SpaceShooter.windowHeight);
        GlyphLayout layout = new GlyphLayout(font, "" + score);
        font.getData().setScale(5f);
        font.setColor(0f, 0f, 0f, 1.0f);
        font.draw(main.batch, layout, 400, 360);
        main.batch.end();
    }
}