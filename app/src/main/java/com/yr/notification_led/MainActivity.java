package com.yr.notification_led;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    Button btn1, btn2;
    SeekBar sbr, sbg, sbb;
    NotificationCompat.Builder mBuilder;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        sbr = findViewById(R.id.seekBar);
        sbg = findViewById(R.id.seekBar2);
        sbb = findViewById(R.id.seekBar3);

        EditText r = findViewById(R.id.editText);
        EditText g = findViewById(R.id.editText2);
        EditText b = findViewById(R.id.editText3);

        TextView tvr = findViewById(R.id.textView4);
        TextView tvg = findViewById(R.id.textView5);
        TextView tvb = findViewById(R.id.textView6);

        btn1.setOnClickListener(v -> {
            String content = (r.getText().toString() + g.getText().toString() + b.getText().toString()).toUpperCase();
            showNotificationDelayed(content, 1000 * 3);
        });

        btn2.setOnClickListener(v -> {
            String content = toHex(sbr.getProgress()) + toHex(sbg.getProgress()) + toHex(sbb.getProgress());
            showNotificationDelayed(content, 1000 * 3);
        });

        sbr.setOnSeekBarChangeListener(new ColorSeekBarListener(sbr, tvr));
        sbg.setOnSeekBarChangeListener(new ColorSeekBarListener(sbg, tvg));
        sbb.setOnSeekBarChangeListener(new ColorSeekBarListener(sbb, tvb));

        if (!checkPermission()) {
            requestPermission();
        }
    }

    /** 檢查是否有推播權限 */
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    /** 請求推播權限 */
    private void requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void changeBtnColor() {
        String hexStringColor = toHex(sbr.getProgress()) + toHex(sbg.getProgress()) + toHex(sbb.getProgress());
        btn2.setBackgroundColor(Color.parseColor("#" + hexStringColor));
    }

    private void showNotificationDelayed(String colorHexString, long delayMillis) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setAction(Intent.ACTION_MAIN);
        notifyIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, flags);

        mBuilder = buildNotification(colorHexString);
        mBuilder.setContentIntent(resultPendingIntent);

        Handler aHandler = new Handler();
        aHandler.postDelayed(runnable, delayMillis);
    }

    static String toHex(int num) {
        int n = num;
        String[] arr = {"A", "B", "C", "D", "E", "F"};
        StringBuilder hex = new StringBuilder();
        while (n > 0) {
            int a = n % 16;
            if (a < 10) {
                hex.insert(0, a);
            } else {
                hex.insert(0, arr[a % 10]);
//                Log.v("ccc", arr[a % 10]);
            }
            n /= 16;
        }

        while (hex.length() <= 1) {
            hex.insert(0, "0");
        }

        return num == 0 ? "00" : hex.toString();
    }

    final Runnable runnable = () -> {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 0;
        mNotificationManager.notify(mId, mBuilder.build());
    };

    protected NotificationCompat.Builder buildNotification(String colorHexString) {
        int color = Integer.decode("0x" + colorHexString);
//        Log.v("aaa", toHex(red) + "" + toHex(green) + "" + toHex(blue) + " color:" + color);

        String channelId = "led";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivity.this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("LED Color")
                        .setContentText("#" + colorHexString)
                        .setLights(color, 1000, 300)
                        .setColor(color);
        // 準備設定通知效果用的變數
        int defaults = 0;
        defaults |= Notification.DEFAULT_VIBRATE;
        defaults |= Notification.DEFAULT_SOUND;
        //defaults |= Notification.DEFAULT_LIGHTS;
        mBuilder.setDefaults(defaults);
        return mBuilder;
    }

    class ColorSeekBarListener implements SeekBar.OnSeekBarChangeListener {
        private final SeekBar seekBar;
        private final TextView textView;

        public ColorSeekBarListener(SeekBar seekBar, TextView textView) {
            this.seekBar = seekBar;
            this.textView = textView;
        }

        /** SeekBar改變時做的動作 */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateUI(progress);
            changeBtnColor();
        }

        /** 開始拉動 SeekBar 時做的動作 */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        /** 拉動 SeekBar 停止時做的動作 */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        private void updateUI(int progress) {
            String label;
            String colorCode;

            if (seekBar == sbr) {
                label = "R : ";
                colorCode = "#" + toHex(progress) + "0000";
            } else if (seekBar == sbg) {
                label = "G : ";
                colorCode = "#00" + toHex(progress) + "00";
            } else {
                label = "B : ";
                colorCode = "#0000" + toHex(progress);
            }

            textView.setText(label + progress);
            ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor(colorCode));
            textView.setTextColor(colorStateList);
            seekBar.setProgressTintList(colorStateList);
        }
    }
}
