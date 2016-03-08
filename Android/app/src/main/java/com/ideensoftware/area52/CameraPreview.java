package com.ideensoftware.area52;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * Created by hpandelo on 22/02/16.
 * Exemplo. http://blog.rhesoft.com/2015/04/02/tutorial-how-to-use-camera-with-android-and-android-studio/
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
        private SurfaceHolder mSurfaceHolder;
        private Camera mCamera;

        // Constructor that obtains context and camera
        @SuppressWarnings("deprecation")
        public CameraPreview(Context context, Camera camera) {
            super(context);
            this.mCamera = camera;
            this.mSurfaceHolder = this.getHolder();
            this.mSurfaceHolder.addCallback(this);
            this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();

                this.mCamera.setDisplayOrientation(90);

                Camera.Parameters parameters = mCamera.getParameters();

                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    mCamera.setParameters(parameters);
                }

            } catch (IOException e) {
                // left blank for now
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mCamera.stopPreview();
            mCamera.release();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
        int width, int height) {
            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}