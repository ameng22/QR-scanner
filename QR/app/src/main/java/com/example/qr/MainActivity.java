package com.example.qr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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

    Button scan_btn;
    TextView txt1,txt2;
    Boolean isEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan_btn = (Button) findViewById(R.id.scan_btn);
        txt1 = (TextView) findViewById(R.id.text_format);
        txt2 = (TextView) findViewById(R.id.text_content);

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
                    Intent emailIntent = new Intent(MainActivity.this,EmailActivity.class);
                    emailIntent.putExtra("email",uri);
                    startActivity(emailIntent);
                }
                else{
                    Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(uri));
//                    startActivity(intent);
                }
                txt2.setText(intentResult.getContents());
                txt1.setText(intentResult.getFormatName());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}