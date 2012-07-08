package com.example;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


public class MainThread extends Thread {

    private static final String TAG = MainThread.class.getSimpleName();

    // Surface holder that can access the physical surface
    private SurfaceHolder surfaceHolder;
    // The actual view that handles inputs
    // and draws to the surface
    private MainPanel mainPanel;

    // flag to hold app state
    private boolean running;
    public void setRunning(boolean running) {
        this.running = running;
    }

    public MainThread(SurfaceHolder surfaceHolder, MainPanel mainPanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.mainPanel = mainPanel;
    }@Override
    public void run() {
        Canvas canvas;
        Log.d(TAG, "Starting render loop");
        while (running) {
            canvas = null;
            // try locking the canvas for exclusive pixel editing
            // in the surface
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized(surfaceHolder) {
                    // update render state
                    // render state to the screen
                    // draws the canvas on the panel
                    this.mainPanel.onDraw(canvas);
                }
            } finally {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            } // end finally
        }
    }


}