package com.ideensoftware.area52;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * Created by hpandelo on 09/11/15.
 */
public class GPS extends Service implements LocationListener {
    public static boolean _DISABLED = true;
    public static Context mAppContext;

    public static Location location;
    public static LocationManager locationManager;
    static double latitude, longitude, precisao;
    static long time;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    static boolean canGetLocation = false;

    public static int _LOCATION_SOURCE = -1; // 1 = GPS | 2 = Network | 3 = Cell

    // Declaring a Location Manager


    public static List<HashMap<String, String>> checkpoints;

    public GPS(Context contexto) {
        try {
            MainActivity.statusGPS.setImageResource(R.drawable.ic_gps_off_white_48dp);
            MainActivity.txtGPS.setText("Inicializando");
            MainActivity.txtGPS.setTextColor(Color.parseColor("#332222"));
            MainActivity.colorBarStatus("GPS", "#332222");

            Log.d("GPS", "Inicializando GPS...");

            mAppContext = contexto;

            /* Inicializa o GPS */
            locationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);

            location = getLocation();

            verificaGPS();

            atualizaStatusBar(location);

            Log.d("GPS", "GPS Inicializado...");

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DEBUG/GPS", "Erro ao inicializar GPS: " + e.toString());
        }
    }


    /*----Method to Check GPS is enable or disable ----- */
    public static void verificaGPS() {
        do {
            // Get Location Manager and check for GPS & Network location services
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                _DISABLED = true;

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Activity) mAppContext).startActivityForResult(intent, 1);

            } else {
                _DISABLED = false;
            }
        } while (_DISABLED);
    }

    @Override
    public void onLocationChanged(Location loc) {
        location = loc;
        atualizaStatusBar(location);


//        Toast.makeText(mAppContext, "Changed "+ loc.getAccuracy()+" | Getted"+ getLocation().getAccuracy(), Toast.LENGTH_SHORT).show();
    }

    /**
     *  Atualiza a barra de Status com a Precisão Atual e sua devida cor de status
     *  @author Helcio Macedo
     */

    private void atualizaStatusBar(final Location actualLocation) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {

                NumberFormat integer = new DecimalFormat("##");

                if(actualLocation != null) {
                    if (actualLocation.getProvider().equalsIgnoreCase("gps"))
                        MainActivity.statusGPS.setImageResource(R.drawable.ic_gps_fixed_white_48dp);
                    else if (actualLocation.getProvider().equalsIgnoreCase("network"))
                        MainActivity.statusGPS.setImageResource(R.drawable.ic_gps_not_fixed_white_48dp);
                    else
                        MainActivity.statusGPS.setImageResource(R.drawable.ic_gps_off_white_48dp);

                    if (actualLocation.getAccuracy() < 10) {
                        latitude = actualLocation.getLatitude();
                        longitude = actualLocation.getLongitude();
                        precisao = actualLocation.getAccuracy();

                        String texto = "CHANGED: LATITUDE: " + latitude + " LONGITUDE: " + longitude + " PRECISAO: " + precisao;
                        MainActivity.txtGPS.setText(String.valueOf(integer.format(precisao)) + "m");
                        MainActivity.txtGPS.setTextColor(Color.parseColor("#008800"));
                        MainActivity.colorBarStatus("GPS", "#008800");
//                        Log.i("GPS",texto);
                    } else {
                        MainActivity.txtGPS.setText(String.valueOf(integer.format(actualLocation.getAccuracy())) + "m");
                        MainActivity.txtGPS.setTextColor(Color.parseColor("#880000"));
                        MainActivity.colorBarStatus("GPS", "#880000");
                        //            Log.d("GPS", "Precisão insuficiente.. Aguardando melhor precisão... (" + String.valueOf(loc.getAccuracy()) + ")");
                    }
                }
            }
        });
    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider,
                                int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    /**
     * Function to get Accuracy
     */
    public static double getAccuracy() {
        if (location != null) {
            precisao = location.getAccuracy();
        }

        // return latitude
        return precisao;
    }

    /**
     * Function to get latitude
     */
    public static double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public static double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to get time
     */
    public static long getTime() {
        if (location != null) {
            time = location.getTime();
        }

        // return longitude
        return time;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public static boolean canGetLocation() {
        return canGetLocation;
    }

    public Location getLocation() {
        if(location == null)
            location = getLastLocation();

        return location;
    }

    public Location getLastLocation() {
        try {

                   /* Verifica se tem permissões adequadas */
            if (mAppContext.checkCallingPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    verificaGPS();

                } else {
                    canGetLocation = true;


//                    // Pega Localização do GPS
//                    if (isGPSEnabled) {
//                        if(_LOCATION_SOURCE != 1) {
//                            FullscreenActivity.statusGPS.setImageResource(R.drawable.ic_gps_fixed_white_48dp);
//                            _LOCATION_SOURCE = 1;
//                        }
//
//                        if (location == null) {
//                            locationManager.requestLocationUpdates(
//                                    LocationManager.GPS_PROVIDER, 0, 0, this);
//                            Log.d("GPS", "Using GPS");
//                            if (locationManager != null) {
//                                location = locationManager
//                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                                if (location != null) {
//                                    latitude = location.getLatitude();
//                                    longitude = location.getLongitude();
//                                    precisao = location.getAccuracy();
//                                    time = location.getTime();
//                                }
//                            }
//                        }
//                    }
//
//                    // Senão pega da Rede
//                    if (isNetworkEnabled) {
//                        if(_LOCATION_SOURCE != 2) {
//                            FullscreenActivity.statusGPS.setImageResource(R.drawable.ic_gps_not_fixed_white_48dp);
//                            _LOCATION_SOURCE = 2;
//                        }
//
//                        locationManager.requestLocationUpdates(
//                                LocationManager.NETWORK_PROVIDER, 0, 0, this);
//                        Log.d("GPS", "Using Network");
//                        if (locationManager != null) {
//                            location = locationManager
//                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                            if (location != null) {
//                                latitude = location.getLatitude();
//                                longitude = location.getLongitude();
//                                precisao = location.getAccuracy();
//                                time = location.getTime();
//                            }
//                        }
//                    }
//
//                    // Senão pega da triangulação de antenas de difusão
//                    else {
//                        if(_LOCATION_SOURCE != 3) {
//                            FullscreenActivity.statusGPS.setImageResource(R.drawable.ic_gps_off_white_48dp);
//                            _LOCATION_SOURCE = 3;
//                        }
//                    }

                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    String bestProvider = locationManager.getBestProvider(criteria, true);

                    /*
                    // Muda Icone de Acordo com Melhor Provedor
                    if(bestProvider.equalsIgnoreCase("gps"))
                        FullscreenActivity.statusGPS.setImageResource(R.drawable.ic_gps_fixed_white_48dp);
                    else if(bestProvider.equalsIgnoreCase("network"))
                        FullscreenActivity.statusGPS.setImageResource(R.drawable.ic_gps_not_fixed_white_48dp);
                    else
                        FullscreenActivity.statusGPS.setImageResource(R.drawable.ic_gps_off_white_48dp);
                    */

                    locationManager.requestLocationUpdates(bestProvider, 0, 0, this);
                    Log.d("GPS", "Best: "+bestProvider);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            precisao = location.getAccuracy();
                            time = location.getTime();
                        }
                    }

//                    atualizaStatusBar();

                }

            } else {
                Log.d("DEBUG/GPS", "Sem permissões adequadas ao GPS!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DEBUG/GPS/GETLOCATION", "Erro: " + e.toString());
        }

        return location;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }



    private static final int ONE_MINUTE = 1000 * 60 * 1;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     * http://developer.android.com/intl/pt-br/guide/topics/location/strategies.html#BestEstimate
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    public Address getLocationAddress() {
        try {

            Geocoder geoCoder = new Geocoder(mAppContext, Locale.getDefault());
            StringBuilder builder = new StringBuilder();
            List<Address> address = null;

            try {
                address = geoCoder.getFromLocation(latitude, longitude, 1);
//                Log.d("DEBUG/ADDRESS","País: " + address.get(0).getCountryName());
//                Log.d("DEBUG/ADDRESS","Estado: " + address.get(0).getAdminArea());
//                Log.d("DEBUG/ADDRESS","Cidade: " + address.get(0).getLocality());
//                Log.d("DEBUG/ADDRESS","Endereço: " + address.get(0).getThoroughfare() + "," +  address.get(0).getSubThoroughfare());
//
//                Log.d("DEBUG/ADDRESS","");
//                Log.d("DEBUG/ADDRESS",address.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i = 0; i < maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            builder.toString(); //This is the complete address.
            return address.get(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }






}
