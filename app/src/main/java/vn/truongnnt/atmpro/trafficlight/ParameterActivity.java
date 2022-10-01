package vn.truongnnt.atmpro.trafficlight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import vn.truongnnt.atmpro.trafficlight.model.UIParameter;

public class ParameterActivity extends BasicActivity {

    EditText tvU1;
    EditText tvU2;
    EditText tvU3;

    EditText tvI1;
    EditText tvI2;
    EditText tvI3;

    EditText tvBrI1;
    EditText tvBrI2;
    EditText tvBrI3;

    EditText tvLeakMainI;
    EditText tvLeakI1;
    EditText tvLeakI2;
    EditText tvLeakI3;

    EditText tvPower;

    RelativeLayout rlU1;
    RelativeLayout rlU2;
    RelativeLayout rlU3;
    RelativeLayout rlI1;
    RelativeLayout rlI2;
    RelativeLayout rlI3;
    RelativeLayout rlBc1;
    RelativeLayout rlBc2;
    RelativeLayout rlBc3;
    RelativeLayout rlLc1;
    RelativeLayout rlLc2;
    RelativeLayout rlLc3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter);
        setupUI(findViewById(R.id.parent_param));

        tvU1 = findViewById(R.id.et_vol_1);
        tvU2 = findViewById(R.id.et_vol_2);
        tvU3 = findViewById(R.id.et_vol_3);

        tvI1 = findViewById(R.id.et_current_1);
        tvI2 = findViewById(R.id.et_current_2);
        tvI3 = findViewById(R.id.et_current_3);

        tvBrI1 = findViewById(R.id.et_branch_current_1);
        tvBrI2 = findViewById(R.id.et_branch_current_2);
        tvBrI3 = findViewById(R.id.et_branch_current_3);

        tvLeakMainI = findViewById(R.id.et_leakage_current_total);
        tvLeakI1 = findViewById(R.id.et_leakage_current_1);
        tvLeakI2 = findViewById(R.id.et_leakage_current_2);
        tvLeakI3 = findViewById(R.id.et_leakage_current_3);

        tvPower = findViewById(R.id.et_power_consumption);

        rlU1 = findViewById(R.id.rlU1);
        rlU2 = findViewById(R.id.rlU2);
        rlU3 = findViewById(R.id.rlU3);

        rlI1 = findViewById(R.id.rlI1);
        rlI2 = findViewById(R.id.rlI2);
        rlI3 = findViewById(R.id.rlI3);

        rlBc1 = findViewById(R.id.rlBc1);
        rlBc2 = findViewById(R.id.rlBc2);
        rlBc3 = findViewById(R.id.rlBc3);

        rlLc1 = findViewById(R.id.rlLc1);
        rlLc2 = findViewById(R.id.rlLc2);
        rlLc3 = findViewById(R.id.rlLc3);

        TextView tvDeviceLocation = findViewById(R.id.tv_device_location);
        TextView tvDeviceName = findViewById(R.id.tv_device_name);
        tvDeviceName.setSelected(true);
        if (this.deviceInfo != null) {
            if (this.deviceInfo.getName() != null) {
                tvDeviceName.setText(deviceInfo.getName());
            }
            if (this.deviceInfo.getLocation() != null) {
                tvDeviceLocation.setText(deviceInfo.getLocation());
            }
            UIParameter uiParameter = deviceInfo.getUiParameter();
            if (uiParameter != null) {
                tvU1.setText(uiParameter.getU1());
                tvU2.setText(uiParameter.getU2());
                tvU3.setText(uiParameter.getU3());

                tvI1.setText(uiParameter.getI1());
                tvI2.setText(uiParameter.getI2());
                tvI3.setText(uiParameter.getI3());

                tvBrI1.setText(uiParameter.getBrI1());
                tvBrI2.setText(uiParameter.getBrI2());
                tvBrI3.setText(uiParameter.getBrI3());

                tvLeakMainI.setText(uiParameter.getLeakMainI());
                tvLeakI1.setText(uiParameter.getLeakI1());
                tvLeakI2.setText(uiParameter.getLeakI2());
                tvLeakI3.setText(uiParameter.getLeakI3());

                tvPower.setText(uiParameter.getPower());
            }
            if (deviceInfo.getPhase().equals("1")) {
                rlU2.setVisibility(View.GONE);
                rlU3.setVisibility(View.GONE);
                rlI2.setVisibility(View.GONE);
                rlI3.setVisibility(View.GONE);
                rlBc2.setVisibility(View.GONE);
                rlBc3.setVisibility(View.GONE);
                rlLc2.setVisibility(View.GONE);
                rlLc3.setVisibility(View.GONE);
            }
        }

        ImageView imbBack = findViewById(R.id.img_back);
        imbBack.setOnClickListener(v -> onBackPressed());

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

        Button accept = findViewById(R.id.bt_accept);
        accept.setOnClickListener(v -> {
            saveParam();
            doNextIntentForResult(SupplyActivity.class);
        });
    }

    @Override
    public void onBackPressed() {
        doBackIntent(DeviceActivity.class);
    }

    void saveParam() {
        deviceInfo.setUiParameter(UIParameter.builder()
                .u1(tvU1.getText().toString())
                .u2(tvU2.getText().toString())
                .u3(tvU3.getText().toString())
                .i1(tvI1.getText().toString())
                .i2(tvI2.getText().toString())
                .i3(tvI3.getText().toString())
                .brI1(tvBrI1.getText().toString())
                .brI2(tvBrI2.getText().toString())
                .brI3(tvBrI3.getText().toString())
                .leakMainI(tvLeakMainI.getText().toString())
                .leakI1(tvLeakI1.getText().toString())
                .leakI2(tvLeakI2.getText().toString())
                .leakI3(tvLeakI3.getText().toString())
                .power(tvPower.getText().toString())
                .build()
        );
    }

    @Override
    void preBackActivity(Intent i) {
        super.preBackActivity(i);
        saveParam();
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        Log.w(ParameterActivity.class.getCanonicalName(), "onSaveInstanceState");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.w(ParameterActivity.class.getCanonicalName(), "onPause");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.w(ParameterActivity.class.getCanonicalName(), "onStop");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.w(ParameterActivity.class.getCanonicalName(), "onDestroy");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.w(ParameterActivity.class.getCanonicalName(), "onResume");
//    }
}