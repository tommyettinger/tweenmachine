/*
 * Copyright (c) 2020-2024 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tommyettinger.tweenmachine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TweenEquationGraphing extends ApplicationAdapter {
    static final boolean WRITE = true;

    private static final int width = 300, height = 420;

    TweenEquation current;
    BitmapFont font;
    ShapeDrawer sd;
    SpriteBatch batch;
    StretchViewport view;
    GlyphLayout name;
    Array<TweenEquation> equations;
    int index;
    double[] k = new double[]{1.0, 1.0};
    Color gradient = new Color(1f, 1f, 1f, 1f);

    @Override
    public void create() {
        TextureRegion fontRegion = new TextureRegion(new Texture("Cozette-standard.png"));
        font = new BitmapFont(Gdx.files.internal("Cozette-standard.fnt"), fontRegion);
        font.getData().markupEnabled = true;
        view = new StretchViewport(width, height);
        batch = new SpriteBatch();
        sd = new ShapeDrawer(batch, new TextureRegion(fontRegion, 510, 510, 1, 1));

        TweenEquation elasticMildOut =
                new TweenEquation("ElasticMild.OUT", TweenEquations.elasticOutFunction(2f, 10f, 7, 0.9f));

        equations = TweenEquations.getTweenEquations();

        index = equations.size - 1;
        current = equations.get(index);
        name = new GlyphLayout(font, "[BLACK]" + current.tag, Color.WHITE, width, Align.center, false);
        if(WRITE){
            System.out.println("<table>\n<tr><th>Graph A</th><th>Name A</th><th>Graph B</th><th>Name B</th><th>Graph C</th><th>Name C</th></tr>");
            int i = 0;
            for (; i < equations.size; i++) {
                if((i % 3) == 0) System.out.println("<tr>");
                current = equations.get(index = i);
                name.setText(font, "[BLACK]" + current.tag, Color.WHITE, width, Align.center, false);
                render();
                Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, width, height);
                PixmapIO.writePNG(Gdx.files.local("out/eqs/" + current.tag + ".png"), pixmap, 9, true);
                pixmap.dispose();
                System.out.println("<td><img src=\"eqs/"+current.tag+".png\" alt=\""+current.tag+"\" /></td><td>"+current.tag+"</td>");
                if((i % 3) == 2) System.out.println("</tr>");
            }
            if((i % 3) == 0)
                System.out.println("</table>");
            else
                System.out.println("</tr>\n</table>");
            Gdx.app.exit();
        }
    }

    @Override
    public void render() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            current = equations.get(index = (index + 1) % equations.size);
            name.setText(font, "[BLACK]"+ current.tag, Color.WHITE, width, Align.center, false);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            current = equations.get(index = (index + equations.size - 1) % equations.size);
            name.setText(font, "[BLACK]"+ current.tag, Color.WHITE, width, Align.center, false);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            k[0] = Math.max(0.001, k[0] + Gdx.graphics.getDeltaTime() * (UIUtils.shift() ? -1 : 1));
            name.setText(font, String.format("[BLACK]a=%.8f, b=%.8f", k[0], k[1]), Color.WHITE, width, Align.center, false);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.B)){
            k[1] = Math.max(0.001, k[1] + Gdx.graphics.getDeltaTime() * (UIUtils.shift() ? -1 : 1));
            name.setText(font, String.format("[BLACK]a=%.8f, b=%.8f", k[0], k[1]), Color.WHITE, width, Align.center, false);
        }
        //print
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            System.out.printf("a = %.8f , b = %.8f \n", k[0], k[1]);
        }
        ScreenUtils.clear(Color.WHITE);
//        batch.setProjectionMatrix(view.getCamera().combined);
        batch.begin();

        // horizontal graph lines
        for (int i = 20; i < height; i+=40) {
            sd.line(0, i, width, i, Color.CYAN, 1f);
        }
        // vertical graph lines
        for (int i = 20; i < width; i+=40) {
            sd.line(i, 0, i, height, Color.CYAN, 1f);
        }
        // x-axis
        sd.line(0, 100, width, 100, Color.NAVY, 1f);
        // y-axis
        sd.line(60, 0, 60, height, Color.NAVY, 1f);
        // line where y == 1
        sd.line(0, 300, width, 300, Color.LIGHT_GRAY, 1f);
        // line where x == 1
        sd.line(260, 0, 260, height, Color.LIGHT_GRAY, 1f);

        float h0, h1 = 101;
        for (int i = 0; i <= 200; i++) {
            float f = i / 200f;
            h0 = h1;
            h1 = current.compute(101, 301, f);
            gradient.fromHsv(270 + 90 * f, 1f, 1f);
            sd.setColor(gradient);
            sd.line(59 + i, h0, 60 + i, h1, 3f);
        }

        float level = -0.4f;
        for (int i = 27; i <= 400; i+=40) {
            font.draw(batch, String.format("[#444444FF]%4.1f", level), 32, i);
            level += 0.2f;
        }
        level = 0f;
        for (int i = 46; i <= 260; i+=40) {
            font.draw(batch, String.format("[#444444FF]%4.1f", level), i, 82);
            level += 0.2f;
        }
        font.draw(batch, name, 0, height - 16f);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        view.update(width, height, true);
        view.apply(true);
    }

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Graphing Interpolations");
        config.useVsync(true);
        config.setResizable(false);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
        config.setWindowedMode(width, height);
        config.disableAudio(true);
        new Lwjgl3Application(new TweenEquationGraphing(), config);
    }
}
