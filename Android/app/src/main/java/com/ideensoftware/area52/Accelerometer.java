package com.ideensoftware.area52;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


/**
 * Created by hpandelo on 07/03/16.
 */
public class Accelerometer extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    double ax,ay,az;   // these are the acceleration in x,y and z axis
    public TextView y_up,
                    y_down,
                    x_left,
                    x_right,
                    z_top,
                    z_bottom,
                    txt_accel_accuracy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        y_up = (TextView)findViewById(R.id.y_up);
        y_down = (TextView)findViewById(R.id.y_down);
        x_left = (TextView)findViewById(R.id.x_left);
        x_right = (TextView)findViewById(R.id.x_right);
        z_top = (TextView)findViewById(R.id.z_top);
        z_bottom = (TextView)findViewById(R.id.z_bottom);
        txt_accel_accuracy = (TextView)findViewById(R.id.txt_accel_accuracy);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            Log.d("DEBUG/ACCEL",event.toString());


            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        txt_accel_accuracy.setText(accuracy);
    }
}