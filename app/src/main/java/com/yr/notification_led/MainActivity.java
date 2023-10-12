package com.yr.notification_led;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btn1,btn2;
    Handler aHandler;
    EditText r,g,b;
    SeekBar sbr,sbg,sbb;
    TextView tvr,tvg,tvb;
    int count=0;
    int color=0;
    int red=0,green=0,blue=0;
    String content="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);

        r=(EditText)findViewById(R.id.editText);
        g=(EditText)findViewById(R.id.editText2);
        b=(EditText)findViewById(R.id.editText3);

        sbr=(SeekBar)findViewById(R.id.seekBar);
        sbg=(SeekBar)findViewById(R.id.seekBar2);
        sbb=(SeekBar)findViewById(R.id.seekBar3);

        tvr=(TextView)findViewById(R.id.textView4);
        tvg=(TextView)findViewById(R.id.textView5);
        tvb=(TextView)findViewById(R.id.textView6);

        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                content=(r.getText().toString()+g.getText().toString()+b.getText().toString()).toUpperCase();
                color=Integer.decode("0x"+content);
                //Log.v("bbb",r.getText().toString()+g.getText().toString()+b.getText().toString()+" color:"+color);
                count=3;
                aHandler = new Handler();
                aHandler.post(runnable);
            }

        });

        sbr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //SeekBar改變時做的動作
                tvr.setText("R : "+progress);
                red=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //開始拉動SeekBar時做的動作
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //拉動SeekBar停止時做的動作
            }
        });

        sbg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //SeekBar改變時做的動作
                tvg.setText("G : "+progress);
                green=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //開始拉動SeekBar時做的動作
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //拉動SeekBar停止時做的動作
            }
        });

        sbb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //SeekBar改變時做的動作
                tvb.setText("B : "+progress);
                blue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //開始拉動SeekBar時做的動作
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //拉動SeekBar停止時做的動作
            }
        });

        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                content=toHex(red)+toHex(green)+toHex(blue);
                color=Integer.decode("0x"+content);
                //Log.v("aaa",toHex(red)+""+toHex(green)+""+toHex(blue)+" color:"+color);

                count=3;
                aHandler = new Handler();
                aHandler.post(runnable);


            }

        });

    }

    static String toHex(int num){
        int n=num;
        String[] arr={"A","B","C","D","E","F"};
        String hex="";
        while(n>0){
            int a=n%16;
            if(a<10){
                hex=a+hex;
            }else{
                hex=arr[a%10]+hex;
                //Log.v("ccc",arr[a%10]);
            }
            n/=16;
        }

        while(hex.length()<=1){
            hex="0"+hex;
        }

        return num==0 ? "00" : hex ;
    }


    final Runnable runnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub

            if (count > 0) {
                count--;
                aHandler.postDelayed(runnable, 1000);
            }else{
                // Creates an explicit intent for an Activity in your app
                final Intent resultIntent = new Intent(MainActivity.this, Main2Activity.class);
                final TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                stackBuilder.addParentStack(Main2Activity.class);
                stackBuilder.addNextIntent(resultIntent);

                NotificationCompat.Builder mBuilder = excution();
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                int mId = 0;
                mNotificationManager.notify(mId, mBuilder.build());

                //seekbar歸零
                /*sbr.setProgress(0);
                sbg.setProgress(0);
                sbb.setProgress(0);*/
            }
        }
    };
    protected NotificationCompat.Builder excution(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("LED Color")
                        .setContentText("#"+content)
                        .setLights(color,1000,300)
                        .setColor(color);
                        //.setVibrate(Notification.DEFAULT_VIBRATE)
                        //.setSound(Notification.DEFAULT_SOUND);
        // 準備設定通知效果用的變數
        int defaults = 0;
        defaults |= Notification.DEFAULT_VIBRATE;
        defaults |= Notification.DEFAULT_SOUND;
        //defaults |= Notification.DEFAULT_LIGHTS;
        mBuilder.setDefaults(defaults);
        return  mBuilder;
    }
}
