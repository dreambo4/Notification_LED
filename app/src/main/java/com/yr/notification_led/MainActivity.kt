package com.yr.notification_led

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

open class MainActivity : AppCompatActivity() {
    private lateinit var btn1: Button
    private lateinit var btn2: Button

    lateinit var sbr: SeekBar
    lateinit var sbg: SeekBar
    lateinit var sbb: SeekBar

    private lateinit var mBuilder: NotificationCompat.Builder

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)

        sbr = findViewById(R.id.seekBar)
        sbg = findViewById(R.id.seekBar2)
        sbb = findViewById(R.id.seekBar3)

        val r = findViewById<EditText>(R.id.editText)
        val g = findViewById<EditText>(R.id.editText2)
        val b = findViewById<EditText>(R.id.editText3)

        val tvr = findViewById<TextView>(R.id.textView4)
        val tvg = findViewById<TextView>(R.id.textView5)
        val tvb = findViewById<TextView>(R.id.textView6)

        btn1.setOnClickListener {
            val content = (r.getText().toString() + g.getText().toString() + b.getText()
                .toString()).uppercase()
            showNotificationDelayed(content, 1000 * 3)
        }

        btn2.setOnClickListener {
            val content = toHex(sbr.progress) + toHex(sbg.progress) + toHex(sbb.progress)
            showNotificationDelayed(content, 1000 * 3)
        }

        sbr.setOnSeekBarChangeListener(ColorSeekBarListener(sbr, tvr))
        sbg.setOnSeekBarChangeListener(ColorSeekBarListener(sbg, tvg))
        sbb.setOnSeekBarChangeListener(ColorSeekBarListener(sbb, tvb))

        if (checkPermission().not()) {
            requestPermission()
        }
    }

    /** 檢查是否有推播權限 */
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** 請求推播權限 */
    private fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    fun changeBtnColor() {
        val hexStringColor = toHex(sbr.progress) + toHex(sbg.progress) + toHex(sbb.progress)
        val bgColor = Color.parseColor("#$hexStringColor")
        btn2.setBackgroundColor(bgColor)
        btn2.setTextColor(if (isColorDark(bgColor)) Color.WHITE else Color.BLACK)
    }

    /**
     * 檢測顏色亮度的函數
     * <p>
     * 公式由ChatGPT提供，來自於色彩理論中的亮度計算方法，這些權重是基於人眼對不同顏色的敏感度而來的。
     */
    private fun isColorDark(color: Int): Boolean {
        val darkness: Double =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    private fun showNotificationDelayed(colorHexString: String, delayMillis: Long) {
        val notifyIntent = Intent(this, MainActivity::class.java)
        notifyIntent.setAction(Intent.ACTION_MAIN)
        notifyIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        val resultPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent, flags
        )

        mBuilder = buildNotification(colorHexString)
        mBuilder.setContentIntent(resultPendingIntent)

        Handler().postDelayed(runnable, delayMillis)
    }

    fun toHex(num: Int): String {
        var n = num
        val arr = listOf("A", "B", "C", "D", "E", "F")
        val hex = StringBuilder()
        while (n > 0) {
            val a = n % 16
            if (a < 10) {
                hex.insert(0, a)
            } else {
                hex.insert(0, arr[a % 10])
//                Log.v("ccc", arr[a % 10])
            }
            n /= 16
        }

        while (hex.length <= 1) {
            hex.insert(0, "0")
        }

        return if (num == 0) "00" else hex.toString()
    }

    private val runnable = Runnable {
        val mNotificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        // mId allows you to update the notification later on.
        val mId = 0
        mNotificationManager?.notify(mId, mBuilder.build())
    }

    private fun buildNotification(colorHexString: String): NotificationCompat.Builder {
        val color = Integer.decode("0x$colorHexString")
//        Log.v("aaa", toHex(red) + "" + toHex(green) + "" + toHex(blue) + " color:" + color)

        val channelId = "led"
        val mBuilder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("LED Color")
                .setContentText("#$colorHexString")
                .setLights(color, 1000, 300)
                .setColor(color)
        // 準備設定通知效果用的變數
        val defaults = Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND
        //val defaults = Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS
        mBuilder.setDefaults(defaults)
        return mBuilder
    }

    inner class ColorSeekBarListener(private var seekBar: SeekBar, private var textView: TextView) :
        SeekBar.OnSeekBarChangeListener {

        /** SeekBar改變時做的動作 */
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            updateUI(progress)
            changeBtnColor()
        }

        /** 開始拉動 SeekBar 時做的動作 */
        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        /** 拉動 SeekBar 停止時做的動作 */
        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }

        private fun updateUI(progress: Int) {
            val label: String
            val colorCode: String

            when (seekBar) {
                sbr -> {
                    label = "R : "
                    colorCode = "#" + toHex(progress) + "0000"
                }

                sbg -> {
                    label = "G : "
                    colorCode = "#00" + toHex(progress) + "00"
                }

                else -> {
                    label = "B : "
                    colorCode = "#0000" + toHex(progress)
                }
            }

            textView.text = label + progress
            val colorStateList = ColorStateList.valueOf(Color.parseColor(colorCode))
            textView.setTextColor(colorStateList)
            seekBar.setProgressTintList(colorStateList)
        }
    }
}
