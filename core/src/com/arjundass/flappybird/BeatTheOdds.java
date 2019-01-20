package com.arjundass.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.audio.*;

import java.util.Random;

public class BeatTheOdds extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background1, background2, star1, star2, gameover, title, names;
    Music intro, theme, jump, game_over_voice;
    //ShapeRenderer shapeRenderer;

    Texture[] odd;
    int flapState = 0;
    int tapped = 1;
    int timeFrame = 0;
    float oddY = 0;
    float velocity = 0;
    int touched_ground = 0;
    int numberJumps = 0;
    Circle oddCircle;

    int score = 0;
    int scoringTubes = 0;
    float moving_floor_x = 0;
    float moving_stars_x = 0;
    float floor_vel = 6;
    float star_vel = 1;
    BitmapFont font;

    int gameState = 0;
    float gravity = 2;

    Texture topTube, bottomTube, tree1, tree2;
    float gap = 2400;
    float maxTubeOffset;
    Random randomNumberGenerator;

    float tubeVelocity = 6;
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset  = new float[numberOfTubes];
    float distanceBetweenTubes;

    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    //gdx drawing to textures
    // Texture moving_ground = new Texture(Gdx.graphics.getWidth(), height of the ground)
    // draw to the empty texture

    @Override
    public void create () {
        batch = new SpriteBatch();
        background1 = new Texture("odd_background.png");
        background2 = new Texture("odd_background.png");
        star1 = new Texture("star.png");
        star2 = new Texture("star.png");
        gameover = new Texture("better_game_over.png");
        title = new Texture("title.png");
        names = new Texture("names.png");
        Gdx.app.log("Height", String.valueOf(Gdx.graphics.getHeight()));
        gap = (float) (Gdx.graphics.getHeight()/3.2); // Mathematically Calculated gap 23 Percent
        // music = Gdx.audio.newMusic() //just try writing the name (Gdx.files.internal("data/mymusic.mp3"));
        intro = Gdx.audio.newMusic(Gdx.files.internal("Battle-City-NES-Music-Game-Start.mp3"));
        theme = Gdx.audio.newMusic(Gdx.files.internal("1-Min-Music-Lightfox-Pluck.mp3"));
        jump = Gdx.audio.newMusic(Gdx.files.internal("jump.mp3"));
        game_over_voice = Gdx.audio.newMusic(Gdx.files.internal("game-over-voice.mp3"));
        // music.play(); find libdgx music documentation

        // shapeRenderer = new ShapeRenderer(); // allow us to render shape
        oddCircle = new Circle();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        odd = new Texture[6];
        odd[0] = new Texture("odd_1.PNG");
        odd[1] = new Texture("odd_2.PNG");
        odd[2] = new Texture ("odd_jump.png");
        odd[3] = new Texture ("odd_ouch.PNG");
        odd[4] = new Texture ("odd_smile.png");
        odd[5] = new Texture ("odd_squish.png");

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");

        tree1 = new Texture("tree1.png");
        tree2 = new Texture("tree2.png");

        maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 200;
        randomNumberGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() / 1;

        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

        intro.play();
        startGame();

    }

    public void startGame(){
        oddY = 900;//Gdx.graphics.getWidth()/2 - birds[0].getWidth()/2;
        for(int i=0; i<numberOfTubes; i++){

            tubeOffset[i] = (randomNumberGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - gap);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth()  + (i * distanceBetweenTubes);

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void render () {

        //create the background before anything else
        batch.begin();

        moving_floor_x -= floor_vel;
        moving_stars_x -= star_vel;
        batch.draw(background1, moving_floor_x, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background2, moving_floor_x+Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(star1, moving_stars_x, 1150,  288*6, 265*6);
        batch.draw(star2, moving_stars_x+Gdx.graphics.getWidth(), 1150, 288*6, 265*6);

        if (tapped == 1) {
            batch.draw(title, Gdx.graphics.getWidth()/2 - (288*3)/2, (Gdx.graphics.getHeight()/2 - (252)*3/2)+400, 288*3, 252*3);
        }
        else {
            theme.play();
            theme.setLooping(true);
        }
        moving_floor_x = moving_floor_x%Gdx.graphics.getWidth();
        moving_stars_x = moving_stars_x%Gdx.graphics.getWidth();

        // Render method is gonna run again and again
        if(gameState == 1) {

            if(tubeX[scoringTubes] < Gdx.graphics.getWidth()/2){
                score++;

                if (score%5 == 0) {
                    floor_vel *= 1.1;
                    star_vel *= 1.1;
                    tubeVelocity *= 1.1;
                }

                Gdx.app.log("Score", String.valueOf(score));
                if(scoringTubes < numberOfTubes - 1){
                    scoringTubes++;
                }
                else{
                    scoringTubes = 0;
                }
            }

            // Decreasing the velocity in negative to bring it up
            if(Gdx.input.justTouched()){
                //jump.play();
                tapped = 0;
                numberJumps += 1;
                if (numberJumps < 3 || touched_ground == 1) {
                    if (touched_ground == 1) {
                        oddY += 1;
                        velocity = -45;
                        numberJumps = 0;
                    }
                    else {
                        velocity = -45;
                    }
                }
                //Gdx.app.log("Touched!", "Touched");
            }
            // Increasing the velocity each time the render method is invoked
            // lapse time

            for(int i=0; i<numberOfTubes; i++) {

                if(tubeX[i] < -topTube.getWidth()){
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomNumberGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - gap);
                }else{
                    tubeX[i] = tubeX[i] - tubeVelocity;

                }

                //batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight()/2 + gap / 2 + tubeOffset[i]);
                //batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap/2 - bottomTube.getHeight() + tubeOffset[i]);
                batch.draw(tree1, tubeX[i], 880-30, tree1.getWidth()*3, tree1.getHeight()*3);
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], 880-30, 67*3, 119*3);
                //topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                //bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap/2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            }



            if(oddY > 900){
                velocity = velocity + gravity;
                oddY -=velocity;
                touched_ground = 0;
                //batch.draw(birds[4], Gdx.graphics.getWidth()/2 - birds[4].getWidth()/2, birdY, 119*2, 119*2);
            }
            else{
                //gameState = 2;
                oddY = 900;
                touched_ground = 1;
                //batch.draw(birds[1], Gdx.graphics.getWidth()/2 - birds[1].getWidth()/2, birdY, 119*2, 119*2);
                //birds[0].getWidth()/2;
            } //refreshing the screen wrong

        }
        else if(gameState == 0){

            if(Gdx.input.justTouched()){
                gameState = 1;
            }
        } else if(gameState == 2){
            game_over_voice.play();
            batch.draw(gameover, Gdx.graphics.getWidth()/2 - (gameover.getWidth()*3)/2, (Gdx.graphics.getHeight()/2 - (gameover.getHeight())*3/2)+400, 288*3, 201*3);
            batch.draw(names, Gdx.graphics.getWidth()/2 - (names.getWidth()*3)/2, (Gdx.graphics.getHeight()/2 - (names.getHeight())*3/2)-1000, 288*3, 233*3);
            if(Gdx.input.justTouched()){
                gameState = 1;
                startGame();
                score = 0;
                scoringTubes = 0;
                velocity = 0;
            }
        }

        timeFrame += 1;
        if(timeFrame%25 == 0) {
            timeFrame = 0;
            if (flapState == 0) {
                flapState = 1;
            } else {
                flapState = 0;
            }
        }

        batch.draw(odd[flapState], Gdx.graphics.getWidth()/2 - odd[flapState].getWidth()/2, oddY, 119*1, 119*1); // adding the two dimensions batch.draw
        font.draw(batch, String.valueOf(score), 100, 200);
        oddCircle.set(Gdx.graphics.getWidth() / 2, oddY + odd[flapState].getHeight() / 2, 97 / 2);
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.BLUE);
        //shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for(int i=0;i<numberOfTubes; i++){
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight()/2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap/2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            if(Intersector.overlaps(oddCircle, topTubeRectangles[i]) || Intersector.overlaps(oddCircle, bottomTubeRectangles[i])){
                gameState = 2;
            }

        }
        batch.end();
        //shapeRenderer.end();
    }
}
