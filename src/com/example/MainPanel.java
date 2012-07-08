package com.example;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.model.Particle2;
import com.example.model.ParticleManager;

public class MainPanel extends SurfaceView implements
        SurfaceHolder.Callback {

    private static final String TAG = MainPanel.class.getSimpleName();
    ParticleManager pm;
    private MainThread thread;

    public MainPanel(Context context) {
        super(context);
        // adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        pm = new ParticleManager();

        // create the animation loop thread
        thread = new MainThread(getHolder(), this);

        // make the main panel focusable so it can handle events
        setFocusable(true);
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        pm.init(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // at this point the surface is created and
        // we can safely start the animation loop
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface is being destroyed");
        // tell the thread to shut down and wait for it to finish
        // this is a clean shutdown
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
        Log.d(TAG, "Thread was shut down cleanly");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                ParticleManager.mousePressed = true;
                ParticleManager.mouseX = event.getX();
                ParticleManager.mouseY = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                ParticleManager.mousePressed = false;
                ParticleManager.mouseX = event.getX();
                ParticleManager.mouseY = event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (ParticleManager.mousePressed) {
                    ParticleManager.mouseX = event.getX();
                    ParticleManager.mouseY = event.getY();
                }
                break;
            }
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        pm.Run(canvas);
    }

}