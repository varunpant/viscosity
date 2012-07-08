package com.example.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

public class Particle2 {

    int particles = 400;
    double[][] pos = new double[this.particles][2];
    double[][] pprev = new double[this.particles][2];
    double[][] vel = new double[this.particles][2];
    double rad = 10.0D;
    double k = 1.0D;
    double kn = 1.0D;
    double rd = 0.2D;
    double rm = 1.0D;
    double rc = this.rm * this.rad;
    int cs = 10;
    int gw = 60;
    int gh = 45;
    int w = this.gw * this.cs;
    int h = this.gh * this.cs;
    ArrayList<Integer>[][] grid = new ArrayList[this.gw][this.gh];
    ArrayList<Integer>[] neighbors = new ArrayList[this.particles];
    boolean mp;
    boolean rp;

   public void setRp(boolean rp) {
        this.rp = rp;
    }
    public void setMp(boolean mp) {
        this.mp = mp;
    }

    public boolean getMp() {
        return this.mp;
    }

    public void setMx(int mx) {
        this.mx = mx;
    }

    public void setMy(int my) {
        this.my = my;
    }

    int mx = 0;
    int my = 0;

    boolean cd = false;
    ArrayList<Integer>[] s1 = new ArrayList[this.particles];
    ArrayList<Double>[] s2 = new ArrayList[this.particles];
    private Paint mLinePaint;


    public void init() {
        this.cd = false;
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLUE);
        mLinePaint.setStrokeWidth(5);
        mLinePaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < this.particles; i++) {
            this.pos[i][0] = (Math.random() * this.w);
            this.pos[i][1] = (Math.random() * this.h);
            this.pprev[i][0] = this.pos[i][0];
            this.pprev[i][1] = this.pos[i][1];
            this.vel[i][0] = 0.0D;
            this.vel[i][1] = 0.0D;
        }
        for (int i = 0; i < this.particles; i++) {
            this.neighbors[i] = new ArrayList();
            this.s1[i] = new ArrayList();
            this.s2[i] = new ArrayList();
        }
        for (int a = 0; a < this.gw; a++) {
            for (int b = 0; b < this.gh; b++) {
                this.grid[a][b] = new ArrayList();
            }
        }
    }

    public void paint(Canvas canvas) {

        canvas.drawColor(Color.WHITE);
        for (int a = 0; a < this.particles; a++) {
        //    canvas.drawLine((float) this.pos[a][0], (float) this.pos[a][1], (float) this.pprev[a][0], (float) this.pprev[a][1], mLinePaint);
            canvas.drawCircle((float) this.pos[a][0], (float) this.pos[a][1],5,mLinePaint);
        }
    }

    public void simulate() {
        for (int i = 0; i < this.particles; i++) {
            this.neighbors[i].clear();
            this.vel[i][1] += 0.02D;
            if (this.mp) {
                double vx = this.pos[i][0] - this.mx;
                double vy = this.pos[i][1] - this.my;
                float vlen = (float) Math.sqrt(vx * vx + vy * vy);
                if (vlen >= 1.0F) {
                    double dx = vx / vlen;
                    double dy = vy / vlen;
                    if (this.cd) {
                        this.vel[i][0] -= 0.03D * dx;
                        this.vel[i][1] -= 0.03D * dy;
                    } else {
                        this.vel[i][0] += 0.3D * this.rad * dx / vlen;
                        this.vel[i][1] += 0.3D * this.rad * dy / vlen;
                    }
                }
            }
        }
        for (int a = 0; a < this.gw; a++) {
            for (int b = 0; b < this.gh; b++) {
                this.grid[a][b].clear();
            }

        }

        for (int i = 0; i < this.particles; i++) {
            this.pprev[i][0] = this.pos[i][0];
            this.pprev[i][1] = this.pos[i][1];
            this.pos[i][0] += this.vel[i][0];
            this.pos[i][1] += this.vel[i][1];
            if (this.pos[i][0] < 1.0D) this.pos[i][0] = 1.0D;
            else if (this.pos[i][0] > this.w - 1) {
                this.pos[i][0] = (this.w - 1);
            }
            if (this.pos[i][1] < 1.0D) this.pos[i][1] = 1.0D;
            else if (this.pos[i][1] > this.h - 1) {
                this.pos[i][1] = (this.h - 1);
            }
        }

        for (int i = 0; i < this.particles; i++) {
            int hcell = (int) (this.pos[i][0] / this.cs);
            int vcell = (int) (this.pos[i][1] / this.cs);
            for (int nx = -1; nx < 2; nx++) {
                for (int ny = -1; ny < 2; ny++) {
                    int xc = hcell + nx;
                    int yc = vcell + ny;
                    if ((xc > -1) && (xc < this.gw) && (yc > -1) && (yc < this.gh)) {
                        for (int a = 0; a < this.grid[xc][yc].size(); a++) {
                            int j = ((Integer) this.grid[xc][yc].get(a)).intValue();
                            double vx = this.pos[j][0] - this.pos[i][0];
                            double vy = this.pos[j][1] - this.pos[i][1];
                            if ((vx > -this.rc) && (vx < this.rc) && (vy > -this.rc) && (vy < this.rc)) {
                                double vlen = Math.sqrt(vx * vx + vy * vy);
                                if (vlen < this.rc) this.neighbors[j].add(Integer.valueOf(i));
                            }
                        }
                    }
                }
            }
            if ((hcell > -1) && (hcell < this.gw) && (vcell > -1) && (vcell < this.gh)) {
                this.grid[hcell][vcell].add(Integer.valueOf(i));
            }
        }
        if (this.rp) {
            for (int i = 0; i < this.particles; i++) {
                for (int n = 0; n < this.neighbors[i].size(); n++) {
                    int j = ((Integer) this.neighbors[i].get(n)).intValue();
                    if (i < j) {
                        double vx = this.pos[j][0] - this.pos[i][0];
                        double vy = this.pos[j][1] - this.pos[i][1];
                        if ((vx > -this.rad) && (vx < this.rad) && (vy > -this.rad) && (vy < this.rad)) {
                            double vlen = Math.sqrt(vx * vx + vy * vy);
                            if (vlen < this.rad) {
                                int l = this.s1[i].size();
                                for (int a = 0; a < l; a++) {
                                    if (((Integer) this.s1[i].get(a)).intValue() == j) {
                                        l = a;
                                        a = this.s1[i].size();
                                    }
                                }
                                if (l == this.s1[i].size()) {
                                    this.s1[i].add(Integer.valueOf(j));
                                    this.s2[i].add(Double.valueOf(vlen));
                                }
                            }
                        }
                    }

                }

            }

            for (int i = 0; i < this.particles; i++) {
                if (this.s2[i].size() > 0) {
                    for (int a = 0; a < this.s2[i].size(); a++) {
                        int j = ((Integer) this.s1[i].get(a)).intValue();
                        double vx = this.pos[j][0] - this.pos[i][0];
                        double vy = this.pos[j][1] - this.pos[i][1];
                        double vlen = Math.sqrt(vx * vx + vy * vy);
                        double rl = ((Double) this.s2[i].get(a)).doubleValue();
                        double td = 1.0D * rl;
                        if (vlen > rl + td) this.s2[i].set(a, Double.valueOf(rl + (vlen - rl - td)));
                        else if (vlen < rl - td) {
                            this.s2[i].set(a, Double.valueOf(rl - (rl - td - vlen)));
                        }
                        rl = ((Double) this.s2[i].get(a)).doubleValue();
                        if (rl > this.rad) {
                            this.s1[i].remove(a);
                            this.s2[i].remove(a);
                            a--;
                        }
                    }
                }
            }

            for (int i = 0; i < this.particles; i++) {
                if (this.s1[i].size() > 0) {
                    for (int a = 0; a < this.s1[i].size(); a++) {
                        int j = ((Integer) this.s1[i].get(a)).intValue();
                        double rl = ((Double) this.s2[i].get(a)).doubleValue();
                        double vx = this.pos[j][0] - this.pos[i][0];
                        double vy = this.pos[j][1] - this.pos[i][1];
                        double vlen = Math.sqrt(vx * vx + vy * vy);
                        double dx = vx / vlen;
                        double dy = vy / vlen;
                        double cx = (1.0D - rl / this.rad) * (rl - vlen) * dx;
                        double cy = (1.0D - rl / this.rad) * (rl - vlen) * dy;

                        this.pos[i][0] -= cx / 2.0D;
                        this.pos[i][1] -= cy / 2.0D;
                        this.pos[j][0] += cx / 2.0D;
                        this.pos[j][1] += cy / 2.0D;
                    }
                }
            }

        }

        for (int i = 0; i < this.particles; i++) {
            double d = 0.0D;
            double dn = 0.0D;

            for (int n = 0; n < this.neighbors[i].size(); n++) {
                int j = ((Integer) this.neighbors[i].get(n)).intValue();
                if (i != j) {
                    double vx = this.pos[j][0] - this.pos[i][0];
                    double vy = this.pos[j][1] - this.pos[i][1];
                    if ((vx > -this.rad) && (vx < this.rad) && (vy > -this.rad) && (vy < this.rad)) {
                        double vlen = Math.sqrt(vx * vx + vy * vy);
                        if (vlen < this.rad) {
                            double q = vlen / this.rad;
                            d += (1.0D - q) * (1.0D - q);
                            dn += (1.0D - q) * (1.0D - q) * (1.0D - q);
                        }
                    }
                }
            }
            if (this.pos[i][0] < this.rad) {
                double q = Math.abs(this.pos[i][0] / this.rad);
                d += Math.pow(1.0D - q, 2.0D);
                dn += Math.pow(1.0D - q, 3.0D);
            } else if (this.pos[i][0] > this.w - this.rad) {
                double q = Math.abs((this.w - this.pos[i][0]) / this.rad);
                d += Math.pow(1.0D - q, 2.0D);
                dn += Math.pow(1.0D - q, 3.0D);
            }
            if (this.pos[i][1] < this.rad) {
                double q = Math.abs(this.pos[i][1] / this.rad);
                d += Math.pow(1.0D - q, 2.0D);
                dn += Math.pow(1.0D - q, 3.0D);
            } else if (this.pos[i][1] > this.h - this.rad) {
                double q = Math.abs((this.h - this.pos[i][1]) / this.rad);
                d += Math.pow(1.0D - q, 2.0D);
                dn += Math.pow(1.0D - q, 3.0D);
            }

            double cx = 0.0D;
            double cy = 0.0D;
            for (int n = 0; n < this.neighbors[i].size(); n++) {
                int j = ((Integer) this.neighbors[i].get(n)).intValue();
                if (i != j) {
                    double vx = this.pos[j][0] - this.pos[i][0];
                    double vy = this.pos[j][1] - this.pos[i][1];
                    if ((vx > -this.rad) && (vx < this.rad) && (vy > -this.rad) && (vy < this.rad)) {
                        double vlen = Math.sqrt(vx * vx + vy * vy);
                        if ((vlen < this.rad) && (vlen > 0.0D)) {
                            double ux = vx / vlen;
                            double uy = vy / vlen;
                            double q = vlen / this.rad;
                            double t = (1.0D - q) * (d + dn * (1.0D - q));
                            double dx = t * ux;
                            double dy = t * uy;
                            this.pos[j][0] += dx / 2.0D;
                            this.pos[j][1] += dy / 2.0D;
                            cx -= dx / 2.0D;
                            cy -= dy / 2.0D;
                        }
                    }
                }
            }
            if (this.pos[i][0] < this.rad) {
                double q = Math.abs(this.pos[i][0] / this.rad);
                double x = (1.0D - q) * (d + dn * (1.0D - q));
                cx += x;
            } else if (this.pos[i][0] > this.w - this.rad) {
                double q = Math.abs((this.w - this.pos[i][0]) / this.rad);
                double x = (1.0D - q) * (d + dn * (1.0D - q));
                cx -= x;
            }
            if (this.pos[i][1] < this.rad) {
                double q = Math.abs(this.pos[i][1] / this.rad);
                double y = (1.0D - q) * (d + dn * (1.0D - q));
                cy += y;
            } else if (this.pos[i][1] > this.h - this.rad) {
                double q = Math.abs((this.h - this.pos[i][1]) / this.rad);
                double y = (1.0D - q) * (d + dn * (1.0D - q));
                cy -= y;
            }
            this.pos[i][0] += cx;
            this.pos[i][1] += cy;
        }

        for (int i = 0; i < this.particles; i++) {
            this.vel[i][0] = (this.pos[i][0] - this.pprev[i][0]);
            this.vel[i][1] = (this.pos[i][1] - this.pprev[i][1]);
        }
    }


}
