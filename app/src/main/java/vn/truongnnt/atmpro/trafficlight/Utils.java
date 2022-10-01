package vn.truongnnt.atmpro.trafficlight;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Utils {

    //public static final Date TRIAL_DATE = new Date(2022, 10,14);
    public static final String TRIAL_DATE = "2024-03-30";
    public static final boolean CHECK_TRIAL_DATE = true;

    public static final boolean TRIAL = true;

    public static final String PARAM_EMAIL = "EMAIL";
    public static final String QR_SEPARATOR = ";";

    public static final String PARAM_DEVICE_TYPE = "DEVICE_TYPE";
    public static final String PARAM_SYS_INFO = "SYS_INFO";
    public static final String PARAM_USER_INFO = "USER_INFO";
    public static final String PARAM_DEVICE_INFO = "DEVICE_INFO";

    public static final String PARAM_LOCATION = "LOCATION";


    public static final String PARAM_QR_CODE = "QR_CODE";

    public static final String PARAM_EQUIPMENT_ACTION = "EQUIPMENT_ACTION";
    public static final String PARAM_UI_CACHE = "UI_CACHE";

    public static final int MIN_LEN_PASSWORD = 4;
    public static final int COUNT_SUCCESS = 5;

    public static boolean isValidEmail(CharSequence target) {
        return (TextUtils.isEmpty(target));// || !Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String getCompleteAddressString(Context ctx, double lat, double log) {
        StringBuilder strAdd = new StringBuilder();
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());

        Log.w("Address", "Latitude [" + lat + "], longitude [" + log + "]");
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, log, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }


                String[] add = strReturnedAddress.toString().split(",");
                if (add.length >= 3) {
                    strAdd.append(add[add.length - 3].trim())
                            .append(", ")
                            .append(add[add.length - 2].trim())
                    ;
                } else {
                    strAdd.append(strReturnedAddress.toString());
                }

                Log.w("Address", strReturnedAddress.toString());
            } else {
                Log.w("Address", "No Address returned!");
            }
        } catch (Throwable e) {
            //e.printStackTrace();
            Log.w("Address", "Cannot get Address!", e);
        }
        return strAdd.toString();
    }

    public static String getAddress(Context ctx, String gps) {
        String address;
        try {
            String[] latlon = gps.split(",");
            address = Utils.getCompleteAddressString(ctx, Double.valueOf(latlon[0].trim()),
                    Double.valueOf(latlon[1].trim()));
        } catch (Exception ex) {
            Log.w(Utils.class.getCanonicalName(), ex);
            address = "";
        }
        return address;
    }

    //create bitmap from the ScrollView
    public static Bitmap getBitmapFromView(View view, int width, int height) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static String saveBitmapToFile(Context ctx, Bitmap bmp, File saveFile) {
        String filePath = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), bmp, "SCREEN"
                + System.currentTimeMillis() + ".png", "Screenshot");
        return filePath;
//        try {
////            File f = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(), "SCREEN"
////                    + System.currentTimeMillis() + ".png");
//            Log.w(Utils.class.getCanonicalName(), "Save screenshot at [" + saveFile.getAbsolutePath() + "]");
//            FileOutputStream fos = new FileOutputStream(saveFile);
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//            fos.close();
//            return true;
//        } catch (IOException e) {
//            Log.w(Utils.class.getCanonicalName(), "Error when save screenshot at [" + saveFile.getAbsolutePath() + "]", e);
//            return false;
//        }
    }

    public static void moveToLogicalLineCenter(EditText edt) {
        Spannable text = edt.getText();
        Layout layout = edt.getLayout();

        // Find the index of the line that cursor is currently on.
        int pos = Selection.getSelectionStart(text);
        int line = layout.getLineForOffset(pos);

        // Find the start pos and end pos of that line.
        int lineStart = layout.getLineStart(line);
        int lineEnd = layout.getLineEnd(line);

        // Calculate center pos and set selection.
        int lineCenter = Math.round((lineEnd + lineStart) / 2f);
        Selection.setSelection(text, lineCenter);
    }

    public static String getFacetID(Context aContext, int callingUid) {

        String packageNames[] = aContext.getPackageManager().getPackagesForUid(callingUid);

        if (packageNames == null) {
            return null;
        }

        try {
            PackageInfo info = aContext.getPackageManager().getPackageInfo(packageNames[0], PackageManager.GET_SIGNATURES);

            byte[] cert = info.signatures[0].toByteArray();
            InputStream input = new ByteArrayInputStream(cert);

            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate c = (X509Certificate) cf.generateCertificate(input);

            MessageDigest md = MessageDigest.getInstance("SHA1");

            return "android:apk-key-hash:" +
                    Base64.encodeToString(md.digest(c.getEncoded()), Base64.DEFAULT | Base64.NO_WRAP | Base64.NO_PADDING);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getFacetID(PackageInfo paramPackageInfo) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramPackageInfo.signatures[0].toByteArray());
            Certificate certificate = CertificateFactory.getInstance("X509").generateCertificate(byteArrayInputStream);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            String facetID = "android:apk-key-hash:" + Base64.encodeToString(((MessageDigest) messageDigest).digest(certificate.getEncoded()), 3);
            return facetID;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getFacetID(Activity aContext) {
        String str = null;
        try {
            int callingUid = aContext.getPackageManager().getApplicationInfo(aContext.getCallingPackage(), PackageManager.GET_META_DATA).uid;
            String[] packageNames = aContext.getPackageManager().getPackagesForUid(callingUid);
            Log.w(Utils.class.getCanonicalName(), "DATA PACKAGE::::" + Arrays.toString(packageNames));
            if (packageNames == null) {
                Log.w(Utils.class.getCanonicalName(), "packageNames is null");
            } else {
                PackageInfo info = aContext.getPackageManager().getPackageInfo(packageNames[0], PackageManager.GET_SIGNATURES);
                byte[] cert = info.signatures[0].toByteArray();
                InputStream input = new ByteArrayInputStream(cert);
                CertificateFactory cf = CertificateFactory.getInstance("X509");
                X509Certificate c = (X509Certificate) cf.generateCertificate(input);
                //X509Certificate c = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(aContext.getPackageManager().getPackageInfo(packageNames[0], PackageManager.GET_SIGNATURES).signatures[0].toByteArray()));
                MessageDigest md = MessageDigest.getInstance("SHA1");
                str = "android:apk-key-hash:" + Base64.encodeToString(md.digest(c.getEncoded()), Base64.DEFAULT | Base64.NO_WRAP | Base64.NO_PADDING);
                Log.w(Utils.class.getCanonicalName(), "Facet of caller android app: " + str);

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e2) {
            e2.printStackTrace();
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
        }
        return str;
    }

    static public float floatFromString(String str) {
        try {
            return Float.valueOf(str);
        } catch (Exception ex) {
            //Log.w(Utils.class.getCanonicalName(), ex);
            return 0;
        }
    }

    public static boolean checkTrial() {
        try {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
            Date trial = ft.parse(Utils.TRIAL_DATE);
            if (Utils.CHECK_TRIAL_DATE && new Date().after(trial)) {
                //trial has expired
                //showMessage(getString(R.string.trial_expired));
                return false;
            }
        } catch (Exception ex) {

        }
        return true;
    }
}
