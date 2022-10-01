package vn.truongnnt.atmpro.trafficlight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.truongnnt.atmpro.trafficlight.async.model.SysInfo;
import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;
import vn.truongnnt.atmpro.trafficlight.model.DeviceType;
import vn.truongnnt.atmpro.trafficlight.ui.component.DeviceComponent;
import vn.truongnnt.atmpro.trafficlight.ui.component.SystemInfoComponent;

public class HomeActivity extends BasicActivity {

    //private TextView tvLocation;
    //private int requestPermissionLocationCode;
    //private LocationManager locationManager;

    private TableLayout tblDevice;
    private TextView tvColDeviceId;

    private DeviceComponent dvCabinet;
    private DeviceComponent dvLight;
    private DeviceComponent dvPole;

    private TableRow rowSelected = null;

    private DeviceInfo deviceSelected = null;
    private List<DeviceInfo> deviceInfos;

    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");

    private static void loadSysInfo(SystemInfoComponent sysCom, SysInfo sysInfo) {
        if (sysInfo != null) {
            sysCom.setNumber(sysInfo.getCount());
            if (sysInfo.getTime() != null)
                sysCom.setTime(ft.format(sysInfo.getTime()));
        }
    }

    private static void loadDeviceInfo(DeviceComponent sysCom, int count) {
        sysCom.setNumber(count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SystemInfoComponent tvNormal = findViewById(R.id.sys_info_normal);
        SystemInfoComponent sysComTrouble = findViewById(R.id.sys_info_trouble);
        SystemInfoComponent sysComMaintain = findViewById(R.id.sys_info_maintenance);

        TextView tvUserName = findViewById(R.id.tv_user_name);
        tvUserName.setText(userInfo.getFullName());

        TextView tvTime = findViewById(R.id.time);
        TextView tvTimeAp = findViewById(R.id.time_ap);

        this.tvLocation = findViewById(R.id.location);
        this.tvLocation.setSelected(true);

        this.tvColDeviceId = findViewById(R.id.tbl_device_id);

        this.dvCabinet = findViewById(R.id.dv_cabinet);
        this.dvCabinet.setOnClickListener(v -> {
            Log.w(HomeActivity.class.getName(), "Click on Cabinet");
            selectDevice(DeviceType.CABINET);
        });

        this.dvLight = findViewById(R.id.dv_light);
        this.dvLight.setOnClickListener(v -> {
            Log.w(HomeActivity.class.getName(), "Click on Light");
            selectDevice(DeviceType.LIGHT);
        });

        this.dvPole = findViewById(R.id.dv_pole);
        this.dvPole.setOnClickListener(v -> {
            Log.w(HomeActivity.class.getName(), "Click on Pole");
            selectDevice(DeviceType.POLE);
        });

        if (sysInfoResponse != null) {
            loadSysInfo(tvNormal, sysInfoResponse.getNormal());
            loadSysInfo(sysComTrouble, sysInfoResponse.getTrouble());
            loadSysInfo(sysComMaintain, sysInfoResponse.getMaintain());

            loadDeviceInfo(dvCabinet, sysInfoResponse.getCabinetCount());
            loadDeviceInfo(dvLight, sysInfoResponse.getLightCount());
            loadDeviceInfo(dvPole, sysInfoResponse.getPoleCount());
        }

        this.tblDevice = findViewById(R.id.tbl_device);
        selectDevice(DeviceType.CABINET);

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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLocation();
        Button btOk = findViewById(R.id.bt_maintain);
        btOk.setOnClickListener(v -> {
            if (rowSelected == null) {
                showMessage(getString(R.string.please_select_device));
                return;
            }
            TextView deviceName = rowSelected.findViewById(R.id.row_name);
            if (this.deviceInfo != null
                    //&& this.deviceInfo.getName().equals(deviceName.getText().toString())
                    && deviceName.getText().toString().equals(this.deviceInfo.getName())
                //&& !this.deviceInfo.getUiSupplyInfoList().isEmpty()
            ) {
                doDeviceInfoSuccess(this.deviceInfo);
            } else {
                Log.e(HomeActivity.class.getCanonicalName(), "Select [" + deviceType +"], with ID [" + deviceSelected.getId() +"]");
                connectService.infoDevice(deviceType, deviceSelected);
            }
        });

        TextView tvScan = findViewById(R.id.tv_all);
        tvScan.setOnClickListener(v -> {
            requestCamera();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(deviceInfos != null){
            showDevice(deviceInfos);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    void preNextActivity(Intent i) {
        super.preNextActivity(i);
        i.putExtra(Utils.PARAM_QR_CODE, this.qrCode);
    }

    private void selectDevice(DeviceType dv) {
        Log.w("DeviceSelected", "Select device [" + dv + "]");
        this.deviceType = dv;
        switch (dv) {
            case POLE:
                this.dvPole.selected();
                this.dvLight.unSelect();
                this.dvCabinet.unSelect();
                break;
            case LIGHT:
                this.dvPole.unSelect();
                this.dvLight.selected();
                this.dvCabinet.unSelect();
                this.tvColDeviceId.setText(getResources().getString(R.string.tbl_col_light_id));
                break;
            case CABINET:
                this.dvPole.unSelect();
                this.dvLight.unSelect();
                this.dvCabinet.selected();
                this.tvColDeviceId.setText(getResources().getString(R.string.tbl_col_cabinet_id));
                break;
        }
        if (dv == DeviceType.POLE) {
            return;
        }
        connectService.listDevice(dv);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    void showDevice(List<DeviceInfo> deviceInfos) {
        tblDevice.removeAllViews();
        rowSelected = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
        int index = 0;
        for (DeviceInfo dv : deviceInfos) {
            index++;
            @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(HomeActivity.this).inflate(R.layout.attrib_row, null);

            TextView id = row.findViewById(R.id.row_id);
            id.setText(String.valueOf(index));
            TextView name = row.findViewById(R.id.row_name);
            //name.setSelected(true);
            name.setText(dv.getName());
            TextView tm = row.findViewById(R.id.row_time);
            tm.setText(ft.format(dv.getDate()));

            row.setTransitionName(String.valueOf(index));
            row.setOnClickListener(v -> {
                if (rowSelected != null) {
                    ImageView imgSelected = rowSelected.findViewById(R.id.row_select);
                    imgSelected.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));
                }
                ImageView newSelect = v.findViewById(R.id.row_select);
                newSelect.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_selected, null));
                rowSelected = (TableRow) v;
                deviceSelected = dv;
            });
            tblDevice.addView(row);
        }
    }

    @Override
    void doDeviceInfoSuccess(DeviceInfo dvi) {
        super.doDeviceInfoSuccess(dvi);
        //doNextIntent(DeviceActivity.class);
//        Intent i = getNextIntent(DeviceActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(i);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        deviceInfo.setTest(new ArrayList<>());
        doNextIntentForResult(DeviceActivity.class);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

//        Intent i = getNextIntent(DeviceActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivityIfNeeded(i, 1);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.w(HomeActivity.class.getCanonicalName(), "onActivityResult, requestCode [" + requestCode + "]");
//    }
}