package vn.truongnnt.atmpro.trafficlight;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import vn.truongnnt.atmpro.trafficlight.async.ConnectService;
import vn.truongnnt.atmpro.trafficlight.async.DaggerConnectServiceFactory;
import vn.truongnnt.atmpro.trafficlight.async.model.Action;
import vn.truongnnt.atmpro.trafficlight.async.model.CheckExistResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.DeviceInfoResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.DeviceListResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.FinishResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.ResponseCode;
import vn.truongnnt.atmpro.trafficlight.async.model.ServiceResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.SysInfoResponse;
import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;
import vn.truongnnt.atmpro.trafficlight.model.DeviceType;
import vn.truongnnt.atmpro.trafficlight.model.UISupplyInfo;
import vn.truongnnt.atmpro.trafficlight.model.UserInfo;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

@Module
public class BasicActivity extends AppCompatActivity {

    protected static final int RESULT_CODE_QR_SCAN = 10;
    protected static final int RESULT_CODE_NEXT_ACTIVITY = 9;

    //protected View layoutToast;
    protected TextView textToast;
    protected ImageView imgToast;
    protected Toast toast;

    protected ProgressDialog progressDialog;
    protected TextView textProgress;
    protected View layoutProgress;

    protected Exception exception;

    protected boolean isResourcePage = true;
    protected DeviceType deviceType;
    protected UserInfo userInfo;
    protected DeviceInfo deviceInfo;
    protected SysInfoResponse sysInfoResponse;

    protected Location location;
    //protected UIAllCache uiAllCache;
    protected TextView tvLocation;
    protected LocationManager locationManager;
    protected int requestPermissionLocationCode;
    protected static boolean isUpdateLocation;

    protected int requestPermissionCamera;
    protected String qrCode;

    protected final ExecutorService exe = Executors.newCachedThreadPool();
    protected final Handler handler = new Handler(Looper.getMainLooper());

    @Inject
    ConnectService connectService;

    @Inject
    public BasicActivity() {
    }

    @Provides
    BasicActivity context() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(getClass().getCanonicalName(), "=======>>> onCreate <<<=======");

        this.userInfo = (UserInfo) getIntent().getSerializableExtra(Utils.PARAM_USER_INFO);
        this.deviceType = (DeviceType) getIntent().getSerializableExtra(Utils.PARAM_DEVICE_TYPE);
        this.sysInfoResponse = (SysInfoResponse) getIntent().getSerializableExtra(Utils.PARAM_SYS_INFO);
        this.deviceInfo = (DeviceInfo) getIntent().getSerializableExtra(Utils.PARAM_DEVICE_INFO);

        if (isResourcePage && userInfo == null) {
            Intent i = new Intent(this, LoginActivity.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View layoutToast = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        textToast = (TextView) layoutToast.findViewById(R.id.text);
        imgToast = (ImageView) layoutToast.findViewById(R.id.image);

        toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layoutToast);


        layoutProgress = inflater.inflate(R.layout.progress,
                (ViewGroup) findViewById(R.id.progress_layout_root));
        textProgress = (TextView) layoutProgress.findViewById(R.id.progress_text);

        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        this.connectService = DaggerConnectServiceFactory.builder().basicActivity(this).build().getLiveInstance();
        this.connectService.setUserInfo(this.userInfo);

        dismissProgress();
    }

    public void onEndSession() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(getClass().getCanonicalName(), "******* onResume *******");
    }

    public void showMessage(String mess) {
        textToast.setText(mess);
        toast.show();
    }

    protected void showProgress(String mess) {
        textProgress.setText(mess);
        progressDialog.show();
        progressDialog.setContentView(layoutProgress);
    }

    public void showProgress() {
        textProgress.setText(getResources().getString(R.string.loading));
        progressDialog.show();
        progressDialog.setContentView(layoutProgress);
    }

    public void dismissProgress() {
        progressDialog.dismiss();
    }

//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//        //progressDialog.dismiss();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            hideSoftKeyboard();
//        }
//    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard();
                return false;
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public void onReceiveResponse(Action action, ServiceResponse resp) {
        //progressDialog.dismiss();
        switch (action) {
            case LOGIN:
                //startActivity(new Intent(this, HomeActivity.class));
                //finish();
                doLoginSuccess();
                break;
            case LIST_DEVICE:
                showDevice(((DeviceListResponse) resp).getDeviceInfos());
                break;
            case NEW_ACCOUNT:
                if (resp.getCode() == ResponseCode.SUCCESS) {
                    showMessage(getResources().getString(R.string.msg_new_account_success));
                    doCreateAccountSuccess();
                }
                break;
            case INFO_DEVICE:
                //showEquipment(((DeviceInfoResponse) resp).getEquipmentInfos());
                this.deviceInfo = ((DeviceInfoResponse) resp).getDeviceInfo();
                doDeviceInfoSuccess(((DeviceInfoResponse) resp).getDeviceInfo());
                break;
            case GET_SYS_INFO:
                this.sysInfoResponse = (SysInfoResponse) resp;
                this.userInfo = connectService.getUserInfo();
                onGetSysInfo((SysInfoResponse) resp);
                break;
            case SEND_FINISH:
                onSendFinishSuccess((FinishResponse) resp);
                break;
            case CHECK_EXIST:
                CheckExistResponse checkExistResponse = (CheckExistResponse) resp;
                onCheckExist(checkExistResponse);
                break;
            case GET_INFO:
                onQrCodeGetInfo((DeviceInfoResponse) resp);
                break;
            case ADD_CABINET:
                finish();
                showMessage(getString(R.string.add_new_device_success));
                break;
        }

    }

    protected Intent getNextIntent(Class<? extends Activity> next) {
        Intent i = new Intent(this, next);
        i.putExtra(Utils.PARAM_DEVICE_TYPE, deviceType);
        i.putExtra(Utils.PARAM_SYS_INFO, sysInfoResponse);
        i.putExtra(Utils.PARAM_USER_INFO, userInfo);
        i.putExtra(Utils.PARAM_DEVICE_INFO, deviceInfo);
        i.putExtra(Utils.PARAM_LOCATION, location);
        return i;
    }

    protected Intent doNextIntentForResult(Class<? extends Activity> next, int requestCode) {
        Intent i = getNextIntent(next);
        preNextActivity(i);
        //i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(i, requestCode);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return i;
    }

    protected Intent doNextIntentForResult(Class<? extends Activity> next) {
        Intent i = getNextIntent(next);
        preNextActivity(i);
        //i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(i, RESULT_CODE_NEXT_ACTIVITY);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return i;
    }

    protected Intent doNextIntent(Class<? extends Activity> next) {
        Intent i = getNextIntent(next);
        preNextActivity(i);
        //i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return i;
    }

    protected Intent doBackIntent(Class<? extends Activity> back) {
        //Intent i = getNextIntent(back);
        Intent i = new Intent();
        preBackActivity(i);
        i.putExtra(Utils.PARAM_DEVICE_INFO, this.deviceInfo);
        //i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        //startActivity(i);
        setResult(Activity.RESULT_OK, i);
        finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return i;
    }

    protected Intent doCancelIntent() {
        //Intent i = getNextIntent(back);
        Intent i = new Intent();
        preBackActivity(i);
        i.putExtra(Utils.PARAM_DEVICE_INFO, this.deviceInfo);
        //i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        //startActivity(i);
        setResult(Activity.RESULT_CANCELED, i);
        finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return i;
    }

    void preNextActivity(Intent i) {

    }

    void preBackActivity(Intent i) {

    }

    void onGetSysInfo(SysInfoResponse resp) {
        throw new UnsupportedOperationException("BasicActivity not support this funtion");
    }

    void showEquipment(List<UISupplyInfo> equipmentInfos) {
        throw new UnsupportedOperationException("BasicActivity not support this funtion");
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    void showDevice(List<DeviceInfo> deviceInfos) {
        throw new UnsupportedOperationException("Basic can't show device");
    }


    void doDeviceInfoSuccess(DeviceInfo deviceInfo) {
        if (this.deviceType == null || this.deviceType == DeviceType.POLE) {
            return;
        }
        Executors.newSingleThreadExecutor().submit(() -> {
            this.deviceInfo.setLocation(Utils.getAddress(this, this.deviceInfo.getGps()));
            deviceInfo.setLocation(this.deviceInfo.getLocation());
            deviceInfo.setDate(this.deviceInfo.getDate());
        });
    }

    void doCreateAccountSuccess() {
        throw new UnsupportedOperationException("Basic can't do create account success");
    }

    void doLoginSuccess() {
        throw new UnsupportedOperationException("Basic can't do login success");
    }


    void onSendFinishSuccess(FinishResponse resp) {
        throw new UnsupportedOperationException("Basic can't do FinishResponse success");
    }

    void onCheckExist(CheckExistResponse checkExistResponse) {
        if (checkExistResponse.isExist()) {
            Log.e(HomeActivity.class.getCanonicalName(), "Exist [" + deviceType + "], with ID [" + checkExistResponse.getId() + "]");
            connectService.getInfo(deviceType, checkExistResponse.getId());
        } else {
            doNextIntentForResult(AddCabinetActivity.class);
        }
    }

    void onQrCodeGetInfo(DeviceInfoResponse checkExistResponse) {
        Log.e(DeviceActivity.class.getCanonicalName(), "QrCodeGetInfo [" + new Gson().toJson(checkExistResponse) + "]");
    }

    void onQrCodeScan(int requestCode, int resultCode, Intent data) {
        //showMessage("QR-Code: [" + qrCode + "]");
        //String scanContent = "";
        String scanFormat = "";
        this.qrCode = "";
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                this.qrCode = scanningResult.getContents();
                scanFormat = scanningResult.getFormatName();
            }
            String[] qrs = this.qrCode.split(Utils.QR_SEPARATOR);
            if (qrs == null || qrs.length < 1 || qrs[0] == null || qrs[0].isEmpty()) {
                Log.e(DeviceActivity.class.getCanonicalName(), "QR-Code [" + qrCode + "]");
                showMessage(getResources().getString(R.string.invalid_qr));
                return;
            }
            //Toast.makeText(this, scanContent + "   type:" + scanFormat, Toast.LENGTH_LONG).show();
            Log.e(this.getClass().getCanonicalName(), "Check-Exist [" + deviceType + "], with ID [" + qrs[0] + "]");
            connectService.checkExist(deviceType, qrs[0]);
        } else {
            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(getClass().getCanonicalName(), "onActivityResult");
        if (requestCode == RESULT_CODE_NEXT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                this.deviceInfo = (DeviceInfo) data.getSerializableExtra(Utils.PARAM_DEVICE_INFO);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        } else if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //String qrCode = data.getStringExtra(Utils.PARAM_QR_CODE);
                onQrCodeScan(requestCode, resultCode, data);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull @org.jetbrains.annotations.NotNull String[] permissions,
                                           @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestPermissionLocationCode) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Log.w(this.getLocalClassName(), "Not allow permission ACCESS_FINE_LOCATION | ACCESS_COARSE_LOCATION");
            }
        } else if (requestCode == requestPermissionCamera) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Log.w(this.getLocalClassName(), "Not allow permission CAMERA");
            }
        } else {
            Log.w(this.getLocalClassName(), "Result permission is not ACCESS_FINE_LOCATION | ACCESS_COARSE_LOCATION");
        }
    }

    protected void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissionLocationCode = (int) (Math.random() * 100);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, requestPermissionLocationCode);
        } else {
//            Executors.newSingleThreadExecutor().submit(() -> {
//                displayLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
//            });
            displayLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            //your code here
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !isUpdateLocation) {
                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,   locationListener);
                showMessage("Bật chế độ cập nhật vị trí");
                isUpdateLocation = true;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1 * 60 * 1000, 400, this::displayLocation);
            }
        }
    }

    private void displayLocation(Location location) {
        Executors.newSingleThreadExecutor().submit(() -> {
            if (location != null) {
                String lct = Utils.getCompleteAddressString(this, location.getLatitude(), location.getLongitude());
                //if(tvLocation != null)
                handler.post(() -> tvLocation.setText(lct));
            } else {
                Log.w(this.getLocalClassName(), "Location is null");
            }
        });
    }

    protected void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionCamera = (int) (Math.random() * 100);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, requestPermissionCamera);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, requestPermissionCamera);
            }
        }
    }

    private void startCamera() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setPrompt("Scan a barcode");
        scanIntegrator.setBeepEnabled(true);
        //The following line if you want QR code
        scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.initiateScan();
    }
}