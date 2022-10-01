package vn.truongnnt.atmpro.trafficlight;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import vn.truongnnt.atmpro.trafficlight.async.model.SysInfoResponse;

public class LoginActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.isResourcePage = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUI(findViewById(R.id.parent_login));

        EditText etEmail = findViewById(R.id.et_login_email);
        String em = getIntent().getStringExtra(Utils.PARAM_EMAIL);
        if (em != null) {
            etEmail.setText(em);
        }
        EditText etPassword = findViewById(R.id.et_login_password);

        LinearLayout btLogin = findViewById(R.id.bt_sign_in);
        btLogin.setOnClickListener(v -> {
            try {
                if (!Utils.checkTrial()) {
                    //trial has expired
                    showMessage(getString(R.string.trial_expired));
                    return;
                }
            } catch (Exception ex) {

            }

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (Utils.isValidEmail(email)) {
                showMessage(getString(R.string.msg_invalid_email));
                return;
            }
            if (password.length() < Utils.MIN_LEN_PASSWORD) {
                showMessage(getString(R.string.msg_invalid_password));
                return;
            }
            connectService.login(email, password);
        });

        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setOnClickListener(v -> {
            String username = etEmail.getText().toString();
            if (username == null || username.isEmpty()) {
                showMessage(getString(R.string.msg_invalid_email));
                return;
            }
            exe.submit(() -> {
                connectService.resetPassword(username);
            });
        });

        TextView tvCreateAccount = findViewById(R.id.tv_create_account);
        tvCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            finish();
        });

//        try {
//            int pid = Binder.getCallingUid();
//            String facet = Utils.getFacetID(this, pid);
//            Log.w(LoginActivity.class.getCanonicalName(), "Facet [" + facet + "]");
//            //facet = Utils.getFacetID(this.getPackageManager().getPackageInfo(this.getPackageName(), this.getPackageManager().GET_SIGNATURES));
//            //facet = Utils.getFacetID(this);
//
//            facet = Utils.getFacetID(this.getPackageManager().getPackageInfo(getCallingPackage(), this.getPackageManager().GET_SIGNATURES));
//            Log.w(LoginActivity.class.getCanonicalName(), "Facet [" + facet + "]");
//        } catch (Exception ex) {
//            Log.w(LoginActivity.class.getCanonicalName(), "Fail to get FacetID", ex);
//        }
//        // Get the intent that started this activity
//        ComponentName componentName = getCallingActivity();
//        if (componentName != null) {
//            String callingActivity = getCallingActivity().getPackageName();
//            Log.w(LoginActivity.class.getCanonicalName(), "CallingActivity [" + callingActivity + "]");
//            Intent intent = getIntent();
//            //String[] emails = (String[]) intent.getExtras().get(Intent.EXTRA_EMAIL);
//            //String subject = (String) intent.getExtras().getString(Intent.EXTRA_SUBJECT);
//            //Log.w(LoginActivity.class.getCanonicalName(), "Email [" + emails[0] + "], Subject [" + subject + "]");
//
//            // Figure out what to do based on the intent type
//            if (intent.getType().indexOf("image/") != -1) {
//                // Handle intents with image data ...
//                //Log.w(LoginActivity.class.getCanonicalName(),"Handle image Uri [" + data.toString() +"]");
//            } else if (intent.getType().equals("text/plain")) {
//                // Handle intents with text ...
//                Log.w(LoginActivity.class.getCanonicalName(),"Handle text Intent");
//            }
//        } else {
//            Log.w(LoginActivity.class.getCanonicalName(), "Not found calling Activity");
//        }
    }

    @Override
    public void onEndSession() {

    }

    boolean checkFIDOClientPermission(String packageName)
            throws PackageManager.NameNotFoundException {
        for (String requestedPermission :
                getPackageManager().getPackageInfo(packageName,
                        PackageManager.GET_PERMISSIONS).requestedPermissions) {
            if (requestedPermission.matches(
                    "org.fidoalliance.uaf.permissions.FIDO_CLIENT"))
                return true;
        }
        return false;
    }

    @Override
    void doLoginSuccess() {
        //Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        Intent i = getNextIntent(HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    @Override
    void onGetSysInfo(SysInfoResponse resp) {
        //Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        Intent i = getNextIntent(HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}