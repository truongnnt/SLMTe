package vn.truongnnt.atmpro.trafficlight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.truongnnt.atmpro.trafficlight.async.model.DeviceInfoResponse;
import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;
import vn.truongnnt.atmpro.trafficlight.model.DeviceType;
import vn.truongnnt.atmpro.trafficlight.model.UISupplyInfo;

public class DeviceActivity extends BasicActivity {

    private TableLayout tblEquipment;
    //private int requestPermissionCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device);

        this.tblEquipment = findViewById(R.id.tbl_equipment);
        //this.deviceSelected = new LinkedList<>();
        //this.rowEquipments = new HashMap<>();

        TextView tvEqTitle = findViewById(R.id.tv_device_title);
        if (deviceType == DeviceType.CABINET) {
            tvEqTitle.setText(getString(R.string.cabinet_devices));
        } else if (deviceType == DeviceType.LIGHT) {
            tvEqTitle.setText(getString(R.string.light_devices));
        }

        loadDeviceInfo();

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

        Button accept = findViewById(R.id.bt_accept);
        accept.setOnClickListener(v -> {
            if (deviceType == DeviceType.CABINET) {
                doNextIntentForResult(ParameterActivity.class);
            } else if (deviceType == DeviceType.LIGHT) {
                doNextIntentForResult(SupplyActivity.class);
            }
        });

        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //getLocation();
        ImageView qrScan = findViewById(R.id.img_qr_scan);
        qrScan.setOnClickListener(v -> {


            requestCamera();

//            IntentIntegrator scanIntegrator = new IntentIntegrator(DeviceActivity.this);
//            scanIntegrator.setPrompt("Scan");
//            scanIntegrator.setBeepEnabled(true);
//            //The following line if you want QR code
//            scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
//            scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
//            scanIntegrator.setOrientationLocked(true);
//            scanIntegrator.setBarcodeImageEnabled(true);
//            scanIntegrator.initiateScan();


//            try {
//                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//                intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
//                startActivityForResult(intent, RESULT_CODE_QR_SCAN);
//            } catch (Exception e) {
//                Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
//                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
//                startActivity(marketIntent);
//            }
        });
    }

    private void loadDeviceInfo() {
        TextView tvDeviceLocation = findViewById(R.id.tv_device_location);
        TextView tvDeviceName = findViewById(R.id.tv_device_name);
        tvDeviceName.setSelected(true);
        if (this.deviceInfo != null) {
            //showEquipment(deviceInfo.getUiSupplyInfoList());
            if (this.deviceInfo.getName() != null) {
                tvDeviceName.setText(deviceInfo.getName());
            }
            if (this.deviceInfo.getLocation() != null) {
                tvDeviceLocation.setText(deviceInfo.getLocation());
            }
            //tvDeviceLocation.setText(Utils.getAddress(this, deviceInfo.getGps()));
        }
    }

    void loadListEquipment() {
        if (deviceInfo != null && deviceInfo.getUiSupplyInfoList() != null) {
            List<UISupplyInfo> allEquipments = deviceInfo.getUiSupplyInfoList();
            showEquipment(allEquipments);
        }
    }

//    private void requestCamera() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            startCamera();
//        } else {
//            requestPermissionCamera = (int) (Math.random() * 100);
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                ActivityCompat.requestPermissions(DeviceActivity.this, new String[]{Manifest.permission.CAMERA}, requestPermissionCamera);
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, requestPermissionCamera);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == requestPermissionCamera) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startCamera();
//            } else {
//                Log.w(DeviceActivity.this.getLocalClassName(), "Not allow permission CAMERA");
//            }
//        } else {
//            Log.w(DeviceActivity.this.getLocalClassName(), "Result permission is not CAMERA");
//        }
//    }
//
//    private void startCamera() {
//        //doNextIntentForResult(CameraPreviewActivity.class, RESULT_CODE_QR_SCAN);
//
//        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
//        scanIntegrator.setPrompt("Scan a barcode");
//        scanIntegrator.setBeepEnabled(true);
//        //The following line if you want QR code
//        scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
//        scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
//        scanIntegrator.setOrientationLocked(true);
//        scanIntegrator.setBarcodeImageEnabled(true);
////        scanIntegrator.setRequestCode(RESULT_CODE_QR_SCAN);
//        scanIntegrator.initiateScan();
//
////        IntentIntegrator integrator = new IntentIntegrator(this);
////        integrator.setPrompt("Scan a barcode");
////        integrator.setBeepEnabled(false);
////        //The following line if you want QR code
////        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
////        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
////        integrator.setOrientationLocked(true);
////        integrator.setBarcodeImageEnabled(true);
////        //integrator.setCameraId(0);  // Use a specific camera of the device
////        //integrator.setRequestCode(RESULT_CODE_QR_SCAN);
////        integrator.initiateScan();
//
//    }
//
//    @Override
//    void onQrCodeScan(int requestCode, int resultCode, Intent data) {
//        super.onQrCodeScan(requestCode, resultCode, data);
//        //showMessage("QR-Code: [" + qrCode + "]");
//
//        //String scanContent = "";
//        String scanFormat = "";
//        this.qrCode = "";
//        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (scanningResult != null) {
//            if (scanningResult.getContents() != null) {
//                this.qrCode = scanningResult.getContents();
//                scanFormat = scanningResult.getFormatName();
//            }
//            String[] qrs = this.qrCode.split(Utils.QR_SEPARATOR);
//            if (qrs == null || qrs.length < 1 || qrs[0] == null || qrs[0].isEmpty()) {
//                Log.e(DeviceActivity.class.getCanonicalName(), "QR-Code [" + qrCode + "]");
//                showMessage(getResources().getString(R.string.invalid_qr));
//                return;
//            }
//            //Toast.makeText(this, scanContent + "   type:" + scanFormat, Toast.LENGTH_LONG).show();
//            Log.e(DeviceActivity.class.getCanonicalName(), "Check-Exist [" + deviceType + "], with ID [" + qrs[0] + "]");
//            connectService.checkExist(deviceType, qrs[0]);
//        } else {
//            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListEquipment();
    }

    @Override
    public void onBackPressed() {
        doBackIntent(HomeActivity.class);
//        Intent i = getNextIntent(HomeActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//        setResult(1,i);
//        startActivity(i);
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    void preNextActivity(Intent i) {
        super.preNextActivity(i);
        i.putExtra(Utils.PARAM_QR_CODE, this.qrCode);
    }

//    @Override
//    void onCheckExist(CheckExistResponse checkExistResponse) {
//        if (checkExistResponse.isExist()) {
//            Log.e(HomeActivity.class.getCanonicalName(), "Exist [" + deviceType + "], with ID [" + checkExistResponse.getId() + "]");
//            connectService.getInfo(deviceType, checkExistResponse.getId());
//        } else {
//            doNextIntentForResult(AddCabinetActivity.class);
//        }
//    }
//
//    @Override
//    void onQrCodeGetInfo(DeviceInfoResponse checkExistResponse) {
//        Log.e(DeviceActivity.class.getCanonicalName(), "QrCodeGetInfo [" + new Gson().toJson(checkExistResponse) + "]");
//    }

    @Override
    void doDeviceInfoSuccess(DeviceInfo dvi) {
        super.doDeviceInfoSuccess(dvi);
        loadDeviceInfo();
        loadListEquipment();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    void showEquipment(List<UISupplyInfo> equipmentInfos) {
        tblEquipment.removeAllViews();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yy");
        int index = 0;
        for (UISupplyInfo eq : equipmentInfos) {
            index++;
            @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(DeviceActivity.this).inflate(R.layout.attrib_row, null);

            TextView id = row.findViewById(R.id.row_id);
            id.setText(String.valueOf(index));
            TextView name = row.findViewById(R.id.row_name);
            name.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_14));
            //name.setSelected(true);
            name.setText(eq.getName());
            TextView tm = row.findViewById(R.id.row_time);
            tm.setText(ft.format(eq.getWarrantyDate()));

            ImageView imgSel = row.findViewById(R.id.row_select);
            if (!eq.isSelected()) {
                imgSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));
            } else {
                imgSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_error, null));
            }

            row.setOnClickListener(v -> {
                ImageView img = v.findViewById(R.id.row_select);
                if (eq.isSelected()) {
                    img.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));
                    eq.setSelected(false);
                } else {
                    img.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_error, null));
                    eq.setSelected(true);
                }

//                //TextView lblName = v.findViewById(R.id.row_name);
//                if (v.getTransitionName() != null && v.getTransitionName().equals("true")) {
//                    img.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected));
//                    v.setTransitionName("false");
////                    for (UISupplyInfo ui : deviceInfo.getUiSupplyInfoList()) {
////                        if (ui.getName().equals(lblName.getText())) {
////                            ui.setSelected(false);
////                            break;
////                        }
////                    }
//                } else {
//                    img.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_error));
//                    v.setTransitionName("true");
////                    for (UISupplyInfo ui : deviceInfo.getUiSupplyInfoList()) {
////                        if (ui.getName().equals(lblName.getText())) {
////                            ui.setSelected(true);
////                            break;
////                        }
////                    }
//                }
            });
            tblEquipment.addView(row);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.w(DeviceActivity.class.getCanonicalName(), "onPause");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.w(DeviceActivity.class.getCanonicalName(), "onStop");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.w(DeviceActivity.class.getCanonicalName(), "onDestroy");
//    }

}