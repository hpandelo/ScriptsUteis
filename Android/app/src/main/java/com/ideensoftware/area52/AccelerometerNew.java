package com.ideensoftware.area52;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class AccelerometerNew extends AppCompatActivity implements SensorEventListener {


    private FloatingActionButton fall_float_ico;
    private SensorManager sensorManager;
    private Sensor accelerometer,
                   gyroscope,
                   linearAceleration;

    public TextView accel_x,
                    accel_y,
                    accel_z,
                    accel_spd,
                    accel_fall_speed,
                    gyro_x,
                    gyro_y,
                    gyro_z,
                    linear_accel_x,
                    linear_accel_y,
                    linear_accel_z,
                    precisao;

    int count = 0;

    private NotificationCompat.Builder notificationBuilder;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        accel_x = (TextView)findViewById(R.id.accel_x);
        accel_y = (TextView)findViewById(R.id.accel_y);
        accel_z = (TextView)findViewById(R.id.accel_z);
        accel_spd = (TextView)findViewById(R.id.accel_speed);
        accel_fall_speed = (TextView)findViewById(R.id.accel_fall_speed);
        gyro_x = (TextView)findViewById(R.id.gyro_x);
        gyro_y = (TextView)findViewById(R.id.gyro_y);
        gyro_z = (TextView)findViewById(R.id.gyro_z);
        linear_accel_x = (TextView)findViewById(R.id.linear_accel_x);
        linear_accel_y = (TextView)findViewById(R.id.linear_accel_y);
        linear_accel_z = (TextView)findViewById(R.id.linear_accel_z);
        precisao = (TextView)findViewById(R.id.precisao);

        fall_float_ico = (FloatingActionButton) findViewById(R.id.fall_float_ico);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.d("SENSORS", " ");
        Log.d("SENSORS", " ***** LISTA DE SENSORES DISPONIVEIS ***** ");
        for(Sensor s : deviceSensors)
            Log.d("SENSORS","[" + s.getVendor() + "] " + s.getName() + " | " + s.getStringType());

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ALL);
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        linearAceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
//        sensorManager.registerListener(this, linearAceleration, SensorManager.SENSOR_DELAY_FASTEST);

    }

    // Movement
    float x = 0, last_x = 0;
    float y = 0, last_y = 0;
    float z = 0, last_z = 0;
    long actualTime = 0, lastUpdate = 0;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onSensorChanged(SensorEvent event) {
        DecimalFormat df = new DecimalFormat("#");
        actualTime = System.currentTimeMillis();

        // Movement
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        if (event.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
            accel_x.setText(getString(R.string.eixo_x) + " " + String.valueOf(df.format(x)) + "m/s²");
            accel_y.setText(getString(R.string.eixo_y) + " " + String.valueOf(df.format(y)) + "m/s²");
            accel_z.setText(getString(R.string.eixo_z) + " " + String.valueOf(df.format(z)) + "m/s²");


            // Movement
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            if ((actualTime - lastUpdate) > 100) {
                long diffTime = (actualTime - lastUpdate);
                lastUpdate = actualTime;

                float flagPointPositive = new Float(5.0),
                      flagPointNegative = new Float(-5.0),
                      speedNoiseFilter = new Float(560);

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
//                if ((x > flagPointPositive || x < flagPointNegative) || (z > flagPointNegative && z < flagPointPositive) || (y > flagPointPositive || y < flagPointNegative)) {
//                    accel_spd.setText(getString(R.string.speed) + String.valueOf(speed));
//                }

                accel_spd.setText(getString(R.string.speed) + " " + String.valueOf(speed));
                if(speed >= speedNoiseFilter) {
                    Log.d("SENSOR", getString(R.string.speed) + String.valueOf(speed));
//                        accel_spd.setText(getString(R.string.speed) + String.valueOf(speed));
                    fall_float_ico.setVisibility(View.VISIBLE);
                    accel_fall_speed.setText(getString(R.string.accel_fall_speed) + " " + String.valueOf(speed));



                    notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_menu_send)
//                            .setLargeIcon()
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                            .setColor(getColor(R.color.colorAccent))
                            .setContentTitle("Area 52: Notificação")
                            .setContentText("Movimento Brusco Detectado!");

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(12378, notificationBuilder.build());

                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();
                    String[] events = new String[6];
                    // Sets a title for the Inbox in expanded layout
                    inboxStyle.setBigContentTitle("Event tracker details:");
                    // Moves events into the expanded layout
                    for (int i=0; i < events.length; i++) {

                        inboxStyle.addLine(events[i]);
                    }
                    // Moves the expanded layout object into the notification object.
                    notificationBuilder.setStyle(inboxStyle);



                }
                else {
                    fall_float_ico.setVisibility(View.GONE);
                }


                last_x = event.values[0];
                last_y = event.values[1];
                last_z = event.values[2];
            }

        }
        if (event.sensor.getType()== Sensor.TYPE_GYROSCOPE){
            gyro_x.setText(getString(R.string.eixo_x) + String.valueOf(df.format(event.values[0])) + "rad/s");
            gyro_y.setText(getString(R.string.eixo_y) + String.valueOf(df.format(event.values[1])) + "rad/s");
            gyro_z.setText(getString(R.string.eixo_z) + String.valueOf(df.format(event.values[2])) + "rad/s");

        }
        if (event.sensor.getType()== Sensor.TYPE_LINEAR_ACCELERATION){
            linear_accel_x.setText(getString(R.string.eixo_x) + String.valueOf(df.format(event.values[0])) + "m/s²");
            linear_accel_y.setText(getString(R.string.eixo_y) + String.valueOf(df.format(event.values[1])) + "m/s²");
            linear_accel_z.setText(getString(R.string.eixo_z) + String.valueOf(df.format(event.values[2])) + "m/s²");
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        precisao.setText(getString(R.string.accuracy) + String.valueOf(accuracy));
    }

}
