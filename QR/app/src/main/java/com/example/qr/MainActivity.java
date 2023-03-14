package com.example.qr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button scan_btn,notify_btn;
    TextView txt1,txt2;
    Boolean isEmail = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan_btn = (Button) findViewById(R.id.scan_btn);
        notify_btn = (Button) findViewById(R.id.notification_btn);
        txt1 = (TextView) findViewById(R.id.text_format);
        txt2 = (TextView) findViewById(R.id.text_content);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("My Notification","My Notification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

        notify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, " Notify Button Clicked", Toast.LENGTH_SHORT).show();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"My Notification");
                builder.setContentTitle("Scanner Notification");
                builder.setContentText("QR Code is being scanned right now");
                builder.setSmallIcon(R.drawable.notification_icon);
                builder.setAutoCancel(true);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                notificationManagerCompat.notify(1,builder.build());
            }
        });

        scan_btn.setOnClickListener(this);
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onClick(View view) {

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a Barcode or QR code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult!=null){
            if (intentResult.getContents()==null){
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }else{

                String uri= intentResult.getContents();

                if (isEmailValid(uri)){
                    Intent emailIntent = new Intent(getApplicationContext(),EmailActivity.class);
                    emailIntent.putExtra("email",uri);
                    startActivity(emailIntent);
                }
                else{
                    Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                }
                txt2.setText(intentResult.getContents());
                txt1.setText(intentResult.getFormatName());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}