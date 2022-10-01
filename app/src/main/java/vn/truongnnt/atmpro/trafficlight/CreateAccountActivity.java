package vn.truongnnt.atmpro.trafficlight;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateAccountActivity extends BasicActivity {


    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.isResourcePage = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setupUI(findViewById(R.id.parent_create_account));

        EditText etFullName = findViewById(R.id.et_new_full_name);
        EditText etEmail = findViewById(R.id.et_new_email);
        EditText etPhone = findViewById(R.id.et_new_phone);
        EditText etPassword = findViewById(R.id.et_new_password);
        EditText etConfirmPassword = findViewById(R.id.et_confirm_password);

        TextView tvCreateAccount = findViewById(R.id.tv_sign_in);
        tvCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        LinearLayout btCreateNew = findViewById(R.id.bt_create_account);
        btCreateNew.setOnClickListener((view) -> {
            String fullName = etFullName.getText().toString();
            email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (fullName.isEmpty()) {
                showMessage(getString(R.string.msg_invalid_full_name));
                return;
            }
            if (Utils.isValidEmail(email)) {
                showMessage(getString(R.string.msg_invalid_email));
                return;
            }
            if (password.length() < Utils.MIN_LEN_PASSWORD) {
                showMessage(getString(R.string.msg_invalid_password));
                return;
            }
            if (!password.equals(confirmPassword)) {
                showMessage(getString(R.string.msg_not_match_password));
                return;
            }
            if(!phone.isEmpty() && (phone.length() < 8 || !phone.matches("[0-9]+"))){
                showMessage(getString(R.string.msg_invalid_phone));
                return;
            }
            exe.submit(() -> {
                boolean ret = connectService.newAccount(fullName, email, password, email, phone);
                if (ret) {
                    handler.post(() -> {
                        doCreateAccountSuccess();
                    });
                }
            });
        });
    }

    @Override
    void doCreateAccountSuccess() {
        ImageView imageView = findViewById(R.id.img_new_success);
        imageView.setVisibility(View.VISIBLE);
        RelativeLayout linearBottom = findViewById(R.id.layout_bottom);
        linearBottom.setVisibility(View.GONE);

        AtomicInteger countNo = new AtomicInteger(Utils.COUNT_SUCCESS);
        TextView count = findViewById(R.id.tv_new_decrease);
        count.setText(String.valueOf(countNo.get()));
        Timer lock = new Timer(false);
        lock.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (countNo.decrementAndGet() == 0) {
                        Intent login = new Intent(CreateAccountActivity.this, LoginActivity.class);
                        login.putExtra(Utils.PARAM_EMAIL, email);
                        startActivity(login);
                        lock.cancel();
                    } else {
                        count.setText(String.valueOf(countNo.get()));
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}