package com.example.model;


import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;

public class Particle {
    public double x, y, pprevx, pprevy, velx, vely, q, vxl, vyl;

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;
        this.pprevx = x;
        this.pprevy = y;
        this.velx = 0;
        this.vely = 0;
    }

    private void AddORPush(int index, Particle particle, List<Particle> particles) {
        if (index <= particles.size() - 1)
            particles.set(index, particle);
        else {
            particles.add(particle);
        }

    }

    public void pass1() {
        // ----- maintain spatial hashing grid ------
        int index = (int) (Math.round(this.y / ParticleManager.gridResolution) * ParticleManager.nbX + Math.round(this.x / ParticleManager.gridResolution));
        cell g = ParticleManager.grid.get(index);
        AddORPush(g.len++, this, g.neighborsParticles);
        // ----- mouse pressed -----
        if (ParticleManager.mousePressed) {
            double vx = this.x - ParticleManager.mouseX;
            double vy = this.y - ParticleManager.mouseY;
            double vlen = Math.sqrt(vx * vx + vy * vy);
            if (vlen >= 1 && vlen < 60) {
                this.velx += 0.5 * ParticleManager.rad * (vx / vlen) / vlen;
                this.vely += 0.5 * ParticleManager.rad * (vy / vlen) / vlen;
            }
        }
        // apply gravity
        this.vely += 0.01;
        // save previous position
        this.pprevx = this.x;
        this.pprevy = this.y;
        // advance to predicted position
        this.x += this.velx;
        this.y += this.vely;
    }

    // ----- Double Density Relaxation Algorithm -----
    public void pass2() {
        double pressure = 0, presnear = 0;
        int nl = 0;
        // ----- get grid position -----
        double xc = Math.round(this.x / ParticleManager.gridResolution);
        double yc = Math.round(this.y / ParticleManager.gridResolution);
        // ----- 3 x 3 grid cells -----
        for (int xd = -1; xd < 2; xd++) {
            for (int yd = -1; yd < 2; yd++) {
                int indx =(int) ((yc + yd) * ParticleManager.nbX + (xc + xd));
                if(indx<ParticleManager.grid.size() && indx>=0){
                cell h = ParticleManager.grid.get(indx);
               // if (h != null && h.len > 0) {
                    // ----- foreach neighbors pair -----
                    for (int a = 0, l = h.len; a < l; a++) {
                        Particle pn = h.neighborsParticles.get(a);
                        if (pn != this) {
                            double vx = pn.x - this.x;
                            double vy = pn.y - this.y;
                            double vlen = Math.sqrt(vx * vx + vy * vy);
                            if (vlen < ParticleManager.rad) {
                                // ----- compute density and near-density -----
                                double q = 1 - (vlen / ParticleManager.rad);
                                pressure += q * q; // quadratic spike
                                presnear += q * q * q; // cubic spike
                                pn.q = q;
                                pn.vxl = (vx / vlen) * q;
                                pn.vyl = (vy / vlen) * q;
                                //ParticleManager.neighbors.set(nl++, pn);
                                AddORPush(nl++, pn, ParticleManager.neighbors);
                            }
                        }
                    }
                 }
            }
        }
        // ----- screen limits -----
        if (this.x < ParticleManager.rad) {
            double q = 1 - Math.abs(this.x / ParticleManager.rad);
            this.x += q * q * 0.5;
        } else if (this.x > ParticleManager.nw - ParticleManager.rad) {
            double q = 1 - Math.abs((ParticleManager.nw - this.x) / ParticleManager.rad);
            this.x -= q * q * 0.5;
        }
        if (this.y < ParticleManager.rad) {
            double q = 1 - Math.abs(this.y / ParticleManager.rad);
            this.y += q * q * 0.5;
        } else if (this.y > ParticleManager.nh - ParticleManager.rad) {
            double q = 1 - Math.abs((ParticleManager.nh - this.y) / ParticleManager.rad);
            this.y -= q * q * 0.5;
        }
        if (this.x < ParticleManager.particleWidth) this.x = ParticleManager.particleWidth;
        else if (this.x > ParticleManager.nw - ParticleManager.particleWidth)
            this.x = ParticleManager.nw - ParticleManager.particleWidth;
        if (this.y < ParticleManager.particleHeight) this.y = ParticleManager.particleHeight;
        else if (this.y > ParticleManager.nh - ParticleManager.particleHeight)
            this.y = ParticleManager.nh - ParticleManager.particleHeight;
        // ----- second pass of the relaxation -----
        pressure = (pressure - 3) * 0.5;
        presnear *= 0.5;

        for (int a = 0; a < nl; a++) {
            Particle np = ParticleManager.neighbors.get(a);
            // apply displacements
            double p = pressure + presnear * np.q;
            double dx = (np.vxl * p) * 0.5;
            double dy = (np.vyl * p) * 0.5;
            np.x += dx;
            np.y += dy;
            this.x -= dx;
            this.y -= dy;
        }
    }


    public void pass3(Canvas canvas, Paint p) {
        // use previous position to compute next velocity
        this.velx = this.x - this.pprevx;
        this.vely = this.y - this.pprevy;
        canvas.drawCircle((float) (this.x),(float) (this.y),2, p);

    }
}
