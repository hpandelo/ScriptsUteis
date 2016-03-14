package com.ideensoftware.area52;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SurfaceHolder.Callback,
        Camera.ShutterCallback,
        Camera.PictureCallback {

    private Camera mCamera = null;
    private CameraPreview mCameraPreview = null;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_ID = 0;
    SurfaceView mPreview;
    public GPS gps;

    public static TextView txtGPS, txt_device_id;
    public static ImageView statusGPS;

    public static MenuView.ItemView menu4;
    public static String deviceId = new String("Área de Testes [Nº 52]\nDevice ID: ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        statusGPS = (ImageView) findViewById(R.id.statusGPS);
        txtGPS = (TextView) findViewById(R.id.txtGPS);
        txt_device_id = (TextView) findViewById(R.id.txt_device_id);

        final TelephonyManager mTelephony = (TelephonyManager) getSystemService(
                Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = deviceId + mTelephony.getDeviceId(); //*** use for mobiles
        } else {
            deviceId = deviceId + Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID); //*** use for tablets
        }
//        txt_device_id.setText(deviceId);
        Log.d("DEVICE ID",deviceId);


        /**
         * Solicitar permissão em RunTime caso seja Android M
         */
        try {
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA )
                    != PackageManager.PERMISSION_GRANTED
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.CAMERA}, 127);
                Log.d("DEBUG","Sem permissão de Câmera");
            }
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 128);
                Log.d("DEBUG","Sem permissão de External Storage");
            }
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )
                    != PackageManager.PERMISSION_GRANTED
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 129);
                Log.d("DEBUG","Sem permissão de GPS");
            }
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.READ_PHONE_STATE )
                    != PackageManager.PERMISSION_GRANTED
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_PHONE_STATE}, 130);
                Log.d("DEBUG","Sem permissão de Ler Estado do Telefone");
            }

        } catch (Exception e){
            e.printStackTrace();
            Log.d("ERROR", "Failed to get permissions: " + e.getMessage());
        }


        /**
         * Inicializa GPS
         */
        gps = new GPS(getApplicationContext());

        /**
         * Inicializa a Câmera no Frame central
         */
         startCamera();

/*        if(mCamera != null) {

            //set camera to continually auto-focus
            Camera.Parameters params = mCamera.getParameters();
            if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            mCamera.setParameters(params);

            mCameraPreview = new CameraPreview(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout preview = (FrameLayout)findViewById(R.id.camera_frame_layout);
            preview.addView(mCameraPreview);//add the SurfaceView to the layout
        }*/




//        Button captureDengue = (Button) findViewById(R.id.bt_foco_dengue);
//        captureDengue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                mCamera.takePicture(null, null, mPicture);
//                Snackbar.make(v, "Foco de Dengue enviado!", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
//            }
//        });


//        Button captureLixo = (Button) findViewById(R.id.bt_foco_lixo);
//        captureLixo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    mCamera.startPreview();
////                    mCamera.takePicture(null, null, mPicture);
////                    mCamera.takePicture(this, null, null, this);
//                } catch (Exception e){
//                    e.printStackTrace();
//                    Log.d("ERROR", "O trem da camera falhou! " + e.getMessage());
//                }
//                Snackbar.make(v, "Lixo denunciado!", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
//            }
//        });



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.menu4) {
            Intent intent = new Intent(this, AccelerometerNew.class);
            startActivity(intent);
        }

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






    @Override
    public void onPause() {
        super.onPause();
        if(mCamera != null) mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCamera != null) mCamera.release();
        Log.d("CAMERA","Destroy");
    }





    public void fecharApp(MenuItem item){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ea_title);
        builder.setMessage(R.string.ea_message);

        builder.setPositiveButton(R.string.ea_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                System.exit(0);
                finish();
            }
        });

        builder.setNegativeButton(R.string.ea_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void onSnapClick(View v) {
        mCamera.takePicture(this, null, null, this);
        Snackbar.make(v, R.string.txt_capture_camera, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }


    public void onGpsClick(View v) {
        String fullLocation =
            "GPS Position" +
            "\nLat: " + String.valueOf(gps.getLatitude()) + " Long: " + String.valueOf(gps.getLongitude()) +
            "\nPrecisao: " + String.valueOf(gps.getAccuracy()) +
            "\n\nCidade: " + gps.getLocationAddress().getLocality() +
                " / " + gps.getLocationAddress().getAdminArea() +
                " - " + gps.getLocationAddress().getCountryName() +
            "\nEndereço: " + gps.getLocationAddress().getThoroughfare() +
                ", " +  gps.getLocationAddress().getSubThoroughfare()
        ;

        TextView txt_location = (TextView)findViewById(R.id.txt_location);
        txt_location.setText(fullLocation);

//        Snackbar.make(v,fullLocation, Snackbar.LENGTH_LONG).setAction("Action", null).show();
//        Toast.makeText(MainActivity.this,fullLocation, Toast.LENGTH_LONG).show();
    }



    public void startCamera(){
        try{
            mPreview = (SurfaceView)findViewById(R.id.surfaceView);
            mPreview.getHolder().addCallback(this);
            mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

//            releaseCameraAndPreview();
            mCamera = mCamera.open(CAMERA_ID);

            this.mCamera.setDisplayOrientation(90);

            Camera.Parameters parameters = mCamera.getParameters();

            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
            {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(parameters);
            }

        } catch (Exception e){
            e.printStackTrace();
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }
    }


    /**
     * Auxiliar de Cores da StatusBar
     *
     * @param item
     * @param color
     */
    public static void colorBarStatus(String item, String color) {
        switch (item) {
            case "GPS":
                statusGPS.getDrawable().setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
                txtGPS.setTextColor(Color.parseColor(color));
                break;

            case "CNN":
//                statusCnn.getDrawable().setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
//                txtCnn.setTextColor(Color.parseColor(color));
                break;

            case "SYNC":
//                statusSync.getDrawable().setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
//                txtSync.setTextColor(Color.parseColor(color));
                break;

            case "ALL":
//                statusSync.setAnimation(null);
                statusGPS.getDrawable().setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
//                statusCnn.getDrawable().setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
//                statusSync.getDrawable().setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
                txtGPS.setTextColor(Color.parseColor(color));
//                txtCnn.setTextColor(Color.parseColor(color));
//                txtSync.setTextColor(Color.parseColor(color));
                break;

        }
    }





    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {

            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Colabore");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Colabore/DEBUG", "failed to create directory");
                }
            }
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File pictureFile;
            pictureFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");


            if (pictureFile == null){
                Toast.makeText(getApplicationContext(),"Failed to get picture!",Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                ExifInterface exif = new ExifInterface(pictureFile.getCanonicalPath());
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, String.valueOf(gps.getLatitude()));
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, String.valueOf(gps.getLongitude()));
                exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, String.valueOf(gps.getTime()));
                exif.saveAttributes();

                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
                MediaScannerConnection.scanFile(this,
                        new String[] { pictureFile.toString() }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });

            } catch (FileNotFoundException e) {
                Log.d("DEBUG/TakePic","File not Found! " + e.getMessage());
                Toast.makeText(getApplicationContext(),"File not Found!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            } catch (IOException e) {
                Log.d("DEBUG/TakePic","Failed to write picture! " + e.getMessage());
                Toast.makeText(getApplicationContext(),"Failed to write picture!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.d("DEBUG/TakePic","Failed to take picture! " + e.getMessage());
            Toast.makeText(getApplicationContext(),"Failed to take picture!",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        mCamera.startPreview();

    }



    @Override
    public void onShutter() {
        Toast.makeText(this, "Click!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mCamera == null) startCamera();
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width,selected.height);
        mCamera.setParameters(params);

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("PREVIEW","surfaceDestroyed");
    }

}
