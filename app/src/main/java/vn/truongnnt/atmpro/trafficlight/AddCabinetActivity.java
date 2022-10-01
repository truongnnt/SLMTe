package vn.truongnnt.atmpro.trafficlight;

import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.truongnnt.atmpro.trafficlight.async.model.AddressLocation;

public class AddCabinetActivity extends BasicActivity {

    //private TextView tvLocation;
    //private LocationManager locationManager;
    //private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cabinet);

        this.tvLocation = findViewById(R.id.tv_device_location);
        this.tvLocation.setSelected(true);

        TextView tvDeviceName = findViewById(R.id.tv_device_name);
        tvDeviceName.setSelected(true);

        String qrCode = (String) getIntent().getSerializableExtra(Utils.PARAM_QR_CODE);
        String id = qrCode.split(Utils.QR_SEPARATOR)[0];

        tvDeviceName.setText(id);

        ImageView imBack = findViewById(R.id.img_back);
        imBack.setOnClickListener(v -> onBackPressed());

        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvTimeAp = findViewById(R.id.tv_time_ap);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ft = new SimpleDateFormat("HH:mm a");
        Timer lock = new Timer(false);
        lock.schedule(new TimerTask() {
            @Override
            public void run() {
                String[] sDate = ft.format(new Date()).split(" ");
                handler.post(() -> {
                    tvTime.setText(sDate[0]);
                    tvTimeAp.setText(sDate[1]);
                });
            }
        }, 0, 60000);

        Spinner spinCity = findViewById(R.id.spin_city);
        final List<AddressLocation> listCity = new ArrayList<>();
        ArrayAdapter<AddressLocation> adpCity = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCity);
        adpCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCity.setAdapter(adpCity);
        adpCity.notifyDataSetChanged();

        Spinner spinDist = findViewById(R.id.spin_district);
        final List<AddressLocation> listDist = new ArrayList<>();
        ArrayAdapter<AddressLocation> adpDist = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listDist);
        adpDist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDist.setAdapter(adpDist);

        Spinner spinStreet = findViewById(R.id.spin_street);
        final List<AddressLocation> listStreet = new ArrayList<>();
        ArrayAdapter<AddressLocation> adpStreet = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listStreet);
        adpStreet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinStreet.setAdapter(adpStreet);

        Button bt = findViewById(R.id.bt_accept);
        bt.setOnClickListener(v -> {
            String gps = "";
            if (location != null) {
                //gps = String.format("%f, %f", location.getLatitude(), location.getLongitude());
                gps = String.format(location.getLatitude() + ", " + location.getLongitude());
            }

            AddressLocation street = listStreet.get(spinStreet.getSelectedItemPosition());
            AddressLocation district = listDist.get(spinDist.getSelectedItemPosition());
            AddressLocation city = listCity.get(spinCity.getSelectedItemPosition());

            String idStreet = street.getId();
            String idDistrict = district.getId();
            String idCity = city.getId();
            Log.e(AddCabinetActivity.class.getCanonicalName(), "Street [" + street.getName() + "], District ["
                    + district.getName() + "], City [" + city.getName() + "]");

            connectService.addCabinet(deviceType, id, qrCode, gps, idStreet, idDistrict, idCity);
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLocation();

        spinCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    return;
                exe.submit(() -> {
                    AddressLocation select = listCity.get(position);
                    Log.e(AddressLocation.class.getCanonicalName(), "Select City [" + select.getName() + "]");
                    List<AddressLocation> district = connectService.getDistrict(select.getId());
                    handler.post(() -> {
                        adpDist.clear();
                        adpDist.add(AddressLocation.builder().name("------------------").build());
                        adpStreet.clear();
                        adpStreet.add(AddressLocation.builder().name("------------------").build());

                        district.forEach(addressLocation -> {
                            adpDist.add(addressLocation);
                        });
                        spinDist.setSelection(0);
                    });
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinDist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    return;
                exe.submit(() -> {
                    AddressLocation select = listDist.get(position);
                    Log.e(AddressLocation.class.getCanonicalName(), "Select District [" + select.getName() + "]");
                    List<AddressLocation> street = connectService.getStreet(select.getId());
                    handler.post(() -> {
                        adpStreet.clear();
                        adpStreet.add(AddressLocation.builder().name("------------------").build());
                        street.forEach(addressLocation -> {
                            adpStreet.add(addressLocation);
                        });
                        spinStreet.setSelection(0);
                    });
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adpCity.add(AddressLocation.builder().name("------------------").build());
        adpDist.add(AddressLocation.builder().name("------------------").build());
        adpStreet.add(AddressLocation.builder().name("------------------").build());
        exe.submit(() -> {
            List<AddressLocation> cities = connectService.getCityProvince();
            cities.forEach(addressLocation -> {
                adpCity.add(addressLocation);
            });
            spinCity.setSelection(0);
        });
    }

//    private void getLocation() {
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            //requestPermissionLocationCode = (int) (Math.random() * 100);
//            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//            //        Manifest.permission.ACCESS_COARSE_LOCATION}, requestPermissionLocationCode);
//        } else {
//            Executors.newSingleThreadExecutor().submit(() -> {
//                displayLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
//            });
//            //your code here
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000, 1000, this::displayLocation);
//        }
//    }
//
//    private void displayLocation(Location location) {
//        if (location != null) {
//            this.location = location;
//            String lct = Utils.getCompleteAddressString(AddCabinetActivity.this, location.getLatitude(), location.getLongitude());
//            handler.post(() -> tvLocation.setText(lct));
//        } else {
//            Log.w(AddCabinetActivity.this.getLocalClassName(), "Location is null");
//        }
//    }
}