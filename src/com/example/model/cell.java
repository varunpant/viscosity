package com.example.model;


import java.util.ArrayList;
import java.util.List;

public class cell {
    public cell() {
        neighborsParticles = new ArrayList<Particle>();
    }

    public int len;
    public List<Particle> neighborsParticles;
}
