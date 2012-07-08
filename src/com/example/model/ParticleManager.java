package com.example.model;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.telephony.CellLocation;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;

public class ParticleManager {

    public static float nw, nh, nx, ny, particleWidth, particleHeight;
    public static int nbX, nbY, fps = 0;
    public static double mouseX = 0, mouseY = 0;
    public static int nParticles, gridResolution, rad;

    List<Particle> particles = new ArrayList<Particle>();
    public static List<Particle> neighbors = new ArrayList<Particle>();
    public static List<cell> grid = new ArrayList<cell>();
    private Paint mp;
    public static boolean mousePressed;

    public void Run(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        // ----- reset grid -----
        for (int i = 0, l = nbX * nbY; i < l; i++) grid.get(i).len = 0;
        // ----- simulation passes -----
        for (int i = 0; i < nParticles; i++) particles.get(i).pass1();
        for (int i = 0; i < nParticles; i++) particles.get(i).pass2();
        for (int i = 0; i < nParticles; i++) particles.get(i).pass3(canvas, mp);
        fps++;
    }


    public void init(double width, double height) {
        mp = new Paint();
        mp.setColor(Color.BLUE);
        mp.setStrokeWidth(2);
        mp.setStyle(Paint.Style.STROKE);

        // ----- entry parameters -----
        nParticles = 500;
        gridResolution = 20;
        rad = 20;

        particleWidth = 5;
        particleHeight = 5;

        // ---- canvas dimensions ----
        nw = (float) width;
        nh = (float) height;

        nbX = Math.round(nw / gridResolution) + 1;
        nbY = Math.round(nh / gridResolution) + 1;
        // ----- init grid (static for better performance) -----
        for (int i = 0; i < nbX * nbY; i++) {
            grid.add(new cell());
        }
        // ----- create particles -----
        for (int i = 0; i < nParticles; i++) {
            particles.add(new Particle(Math.random() * nw, Math.random() * nh));
        }

    }

}
