package vn.truongnnt.atmpro.trafficlight;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Selection;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import vn.truongnnt.atmpro.trafficlight.async.model.FinishResponse;
import vn.truongnnt.atmpro.trafficlight.model.DeviceType;
import vn.truongnnt.atmpro.trafficlight.model.UISavePrint;
import vn.truongnnt.atmpro.trafficlight.model.UISupplyInfo;
import vn.truongnnt.atmpro.trafficlight.model.UserAction;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SavePrintActivity extends BasicActivity {

    private int requestPermissionStorageCode;
    EditText etNote;
    private File saveFile;

    ImageView imBack;
    Button btFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_print);
        setupUI(findViewById(R.id.parent_SavePrint));

        this.etNote = findViewById(R.id.et_note);
        TextView tvLedTitle = findViewById(R.id.tv_col_led);
        if (deviceType == DeviceType.CABINET) {
            tvLedTitle.setVisibility(View.GONE);
        } else if (deviceType == DeviceType.LIGHT) {
            tvLedTitle.setVisibility(View.VISIBLE);
        }

        TextView tvDeviceName = findViewById(R.id.tv_device_name);
        tvDeviceName.setSelected(true);
        if (deviceInfo != null) {
            if (deviceInfo.getName() != null) {
                tvDeviceName.setText(deviceInfo.getName());
            }
            if (deviceInfo.getUiSavePrint() != null) {
                etNote.setText(deviceInfo.getUiSavePrint().getNote());
            }
            if (deviceInfo.getUiSupplyInfoList() != null) {
                TableLayout tblEquipment = findViewById(R.id.tbl_equipment);
                int index = 0;
                for (UISupplyInfo eq : deviceInfo.getUiSupplyInfoList()) {
                    if (eq.getAction() == null || eq.getAction() == UserAction.INSURANCE) {
                        continue;
                    }
                    index++;
                    @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(SavePrintActivity.this)
                            .inflate(R.layout.row_material_summary, null);
                    TextView id = row.findViewById(R.id.row_id);
                    id.setText(String.valueOf(index));
                    TextView led = row.findViewById(R.id.row_led);
                    if (deviceType == DeviceType.CABINET) {
                        led.setVisibility(View.GONE);
                    } else if (deviceType == DeviceType.LIGHT) {
                        led.setVisibility(View.VISIBLE);
                        led.setText(eq.getLed());
                    }

                    TextView name = row.findViewById(R.id.row_name);
                    //name.setSelected(true);
                    name.setText(eq.getName());

                    TextView rowRep = row.findViewById(R.id.row_sum_replace);
                    TextView rowFix = row.findViewById(R.id.row_sum_repair);
                    if (eq.getAction() == UserAction.REPLACE) {
                        rowRep.setText("1");
                        rowFix.setText("0");
                    } else if (eq.getAction() == UserAction.REPAIR) {
                        rowRep.setText("0");
                        rowFix.setText("1");
                    }
                    tblEquipment.addView(row);
                }
            }
        }
        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvTimeAp = findViewById(R.id.tv_date);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ft = new SimpleDateFormat("HH:mm dd/MM/yy");
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

        TextView tvUsername = findViewById(R.id.tv_user_name);
        tvUsername.setText(userInfo.getFullName());

        imBack = findViewById(R.id.img_back);
        imBack.setOnClickListener(v -> onBackPressed());

        btFinish = findViewById(R.id.bt_finish);

//        File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        //File imagesDir = getExternalFilesDir(Environment.DIRECTORY_SCREENSHOTS);
//        try {
//            this.saveFile = File.createTempFile(
//                    "SLMte_SCREEN",  /* prefix */
//                    ".png",         /* suffix */
//                    imagesDir      /* directory */
//            );
//        } catch (Exception ex) {
//            Log.w(SavePrintActivity.class.getCanonicalName(), "", ex);
//        }
//        this.saveFile = new File(imagesDir, "SLMte_SCREEN_" + System.currentTimeMillis() + ".png");
        this.saveFile = new File("SLMte_SCREEN_" + System.currentTimeMillis() + ".png");
        btFinish.setOnClickListener(v -> {
            //takeScreenshot();
            deviceInfo.setUiSavePrint(UISavePrint.builder()
                    .note(etNote.getText().toString())
                    .build());
            Log.w(SavePrintActivity.class.getCanonicalName(), "DeviceInfo: [" + new Gson().toJson(deviceInfo) + "]");
            connectService.sendFinish(deviceType, deviceInfo);
        });
    }

    @Override
    void onSendFinishSuccess(FinishResponse resp) {
        Log.w(SavePrintActivity.class.getCanonicalName(), "Start CaptureScreen");
        captureScreenShot();
    }

    @Override
    void preNextActivity(Intent i) {
        super.preNextActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        deviceInfo.setUiSupplyInfoList(new ArrayList<>());
        deviceInfo = null;
    }

    private void captureScreenShot() {
        if (!checkPermission()) {
            return;
        }
        showProgress();
        btFinish.setClickable(false);
        imBack.setClickable(false);
        btFinish.setVisibility(View.GONE);
        boolean stillNot;
        do {
            stillNot = Selection.moveUp(etNote.getText(), etNote.getLayout());
        } while (stillNot);

        showMessage(String.format("%s %s", getString(R.string.msg_storage_screenshot), saveFile.getPath()));
        handler.postDelayed(() -> {
            captureScreen(); // -> for full
            //takeScreenshot();
            //Log.w(SavePrintActivity.class.getCanonicalName(), "DeviceInfo: [" + new Gson().toJson(deviceInfo) + "]");
            doNextIntent(HomeActivity.class);
            finish();
            dismissProgress();
        }, 1);
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissionStorageCode = (int) (Math.random() * 100);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestPermissionStorageCode);
            return false;
        }
        return true;
    }

    private void takeScreenshot() {
        Bitmap bmp = loadBitmapFromView(this, findViewById(R.id.parent_SavePrint));
        Utils.saveBitmapToFile(this, bmp, saveFile);
    }

    private void captureScreen() {
        ViewGroup v = findViewById(R.id.parent_SavePrint);
        RelativeLayout layoutTop = v.findViewById(R.id.layout_top);
        RelativeLayout layoutUserInfo = v.findViewById(R.id.layout_user_info);
        LinearLayout layoutUserNote = v.findViewById(R.id.linear_note);
        EditText etNote = layoutUserNote.findViewById(R.id.et_note);
        ScrollView eqScoll = layoutUserNote.findViewById(R.id.scroll_tbl);
//        TableLayout eqTbl = eqScoll.findViewById(R.id.tbl_equipment);

        //layoutUserNote.getLayoutParams().height = WRAP_CONTENT; //must have
//        etNote.getLayoutParams().height = WRAP_CONTENT;
//        etNote.invalidate();
//        etNote.requestLayout();
//
//        eqScoll.getLayoutParams().height = WRAP_CONTENT;
//        eqTbl.getLayoutParams().height = WRAP_CONTENT;
//        eqTbl.invalidate();
//        eqTbl.requestLayout();
//        eqScoll.invalidate();
//        eqScoll.requestLayout();

////        //etNote.setOverScrollMode(OVER_SCROLL_ALWAYS);
////        etNote.setGravity(Gravity.TOP | Gravity.LEFT);
////        //etNote.setSelection(0);
////        //etNote.setVerticalScrollBarEnabled(true);
////        //etNote.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
////        //String etString = etNote.getText().toString();
////        //etNote.setText("truonnnt");
////        etNote.clearFocus();
////        Selection.removeSelection(etNote.getText());
////        Selection.moveToLeftEdge(etNote.getText(), etNote.getLayout());
////        etNote.setSelection(0);
////        //etNote.setText(etString);
////        moveToLogicalLineCenter(etNote);
//        boolean stillNot = true;
//        do {
//            stillNot = Selection.moveUp(etNote.getText(), etNote.getLayout());
//        }while (stillNot);

        //layoutUserNote.invalidate();
        //layoutUserNote.requestLayout();
        layoutUserNote.measure(MATCH_PARENT, WRAP_CONTENT);
        int heightContent =
                3 * getResources().getDimensionPixelSize(R.dimen.space_tiny_10)
                        + layoutTop.getHeight()
                        + layoutUserInfo.getHeight()
                        //+ eqScoll.getChildAt(0).getHeight()
                        //+ etNote.getHeight()
                        + layoutUserNote.getMeasuredHeight();
        Log.w(SavePrintActivity.class.getCanonicalName(), "Height Content [" + heightContent + "]");
        Log.w(SavePrintActivity.class.getCanonicalName(), "RootView size [" + v.getWidth() + " x " + v.getHeight() + "]");
        Bitmap bmp;
        if (!Utils.TRIAL) {
            etNote.getLayoutParams().height = WRAP_CONTENT;
            eqScoll.getLayoutParams().height = WRAP_CONTENT;
////            eqScoll.measure(MATCH_PARENT, WRAP_CONTENT);
//            if(eqTbl.getHeight() > eqScoll.getHeight()){
//                eqScoll.getLayoutParams().height = WRAP_CONTENT;
//            }else{
//                //eqScoll.getLayoutParams().height = eqScoll.getHeight();
//            }

            //done
            if (heightContent > v.getHeight()) {
                layoutUserNote.getLayoutParams().height = WRAP_CONTENT;
            } else {
                layoutUserNote.getLayoutParams().height = layoutUserNote.getHeight();
            }

            int sp = getResources().getDimensionPixelSize(R.dimen.space_tiny_10);
            LinearLayout home_scroll = new LinearLayout(this);
            home_scroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT));
            home_scroll.setPadding(sp, sp, sp, sp);
            home_scroll.setOrientation(LinearLayout.VERTICAL);
            home_scroll.setGravity(Gravity.CENTER);

            ScrollView scrollViewContainer = new ScrollView(this);
            scrollViewContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT));
            scrollViewContainer.addView(home_scroll);

            ((ViewGroup) layoutTop.getParent()).removeView(layoutTop);
            home_scroll.addView(layoutTop);
            ((ViewGroup) layoutUserInfo.getParent()).removeView(layoutUserInfo);
            home_scroll.addView(layoutUserInfo);
            ((ViewGroup) layoutUserNote.getParent()).removeView(layoutUserNote);
            home_scroll.addView(layoutUserNote);

            //home_scroll.invalidate();
            //home_scroll.requestLayout();
            //scrollViewContainer.invalidate();
            //scrollViewContainer.requestLayout();

            scrollViewContainer.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            scrollViewContainer.layout(0, 0, scrollViewContainer.getMeasuredWidth(),
                    scrollViewContainer.getMeasuredHeight()
//                layoutTop.getHeight()
//                        + layoutUserInfo.getHeight()
//                        + eqScoll.getChildAt(0).getHeight()
//                        + etNote.getHeight()
            );

            bmp = Utils.getBitmapFromView(scrollViewContainer,
                    scrollViewContainer.getChildAt(0).getWidth(),
                    scrollViewContainer.getChildAt(0).getHeight());
        } else {
            bmp = Utils.getBitmapFromView(v, v.getMeasuredWidth(), v.getMeasuredHeight());
        }
        Utils.saveBitmapToFile(this, bmp, saveFile);

//        String filePath = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "SCREEN"
//                + System.currentTimeMillis() + ".png", "Screenshoot");
//        showMessage(String.format("%s %s", getString(R.string.msg_storage_screenshot), filePath));
////        try {
//////            File f = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), "SCREEN"
//////                    + System.currentTimeMillis() + ".png");
////            Log.w(SavePrintActivity.class.getCanonicalName(), "Save screenshot at [" + saveFile.getAbsolutePath() + "]");
////            FileOutputStream fos = new FileOutputStream(saveFile);
////            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
////            fos.flush();
////            fos.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
    }

    public Bitmap loadBitmapFromView(Context context, View v) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        return Utils.getBitmapFromView(v, v.getWidth(), v.getHeight());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestPermissionStorageCode) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureScreenShot();
            } else {
                Log.w(SavePrintActivity.this.getLocalClassName(), "Not allow permission WRITE_EXTERNAL_STORAGE");
            }
        } else {
            Log.w(SavePrintActivity.this.getLocalClassName(), "Result permission is not WRITE_EXTERNAL_STORAGE");
        }
    }

    //    private void takeScreenshot() {
//        Date now = new Date();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
//        try {
//            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
//
//            // create bitmap screen capture
//            View v1 = getWindow().getDecorView().getRootView();
//            v1.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//            v1.setDrawingCacheEnabled(false);
//
//            File imageFile = new File(mPath);
//
//            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            int quality = 100;
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//            outputStream.flush();
//            outputStream.close();
//
//            openScreenshot(imageFile);
//        } catch (Throwable e) {
//            // Several error may come out with file handling or DOM
//            e.printStackTrace();
//        }
//    }
//
//    private void openScreenshot(File imageFile) {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        Uri uri = Uri.fromFile(imageFile);
//        intent.setDataAndType(uri, "image/*");
//        startActivity(intent);
//    }

    @Override
    void preBackActivity(Intent i) {
        super.preBackActivity(i);
        deviceInfo.setUiSavePrint(UISavePrint.builder()
                .note(etNote.getText().toString())
                .build()
        );
    }

    @Override
    public void onBackPressed() {
        doBackIntent(SupplyActivity.class);
    }
}