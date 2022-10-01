package vn.truongnnt.atmpro.trafficlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import java.util.List;

import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;

public class Splash extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.isResourcePage = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(() -> {
            startActivity(new Intent(Splash.this, LoginActivity.class));
            finish();
        }, 1000);

        Button btStart = findViewById(R.id.bt_start);
        btStart.setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null);
            handler.post(() -> {
                Intent i = new Intent(Splash.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            });
        });
    }
}