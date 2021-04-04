package com.zoiapp.zoi.Drawer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.zoiapp.zoi.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NavigationDrawer extends AppCompatActivity {
    TextView selecteditem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigationdrawer);
        selecteditem=(TextView)findViewById(R.id.text);
        String selectedItem=getIntent().getExtras().getString("SelectedItem");
        switch (selectedItem)
        {
            case "AboutUs" :
            {
                InputStream inputStreamAboutUs = getResources().openRawResource(R.raw.aboutus);

                ByteArrayOutputStream byteArrayOutputStreamAboutUs = new ByteArrayOutputStream();

                int abo;
                try {
                    abo = inputStreamAboutUs.read();
                    while (abo != -1)
                    {
                        byteArrayOutputStreamAboutUs.write(abo);
                        abo = inputStreamAboutUs.read();
                    }
                    inputStreamAboutUs.close();
                } catch ( IOException e) {
                    e.printStackTrace();
                }
                selecteditem.setTextSize(30);
                selecteditem.setText(byteArrayOutputStreamAboutUs.toString());
            }
            break;
            case "PrivacyPolicy" :
            {
                InputStream inputStreamPrivacyPolicy = getResources().openRawResource(R.raw.privacypolicies);

                ByteArrayOutputStream byteArrayOutputStreamPrivacyPolicy = new ByteArrayOutputStream();

                int pri;
                try {
                    pri = inputStreamPrivacyPolicy.read();
                    while (pri != -1) {
                        byteArrayOutputStreamPrivacyPolicy.write(pri);
                        pri = inputStreamPrivacyPolicy.read();
                    }
                    inputStreamPrivacyPolicy.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selecteditem.setTextSize(20);
                selecteditem.setText(byteArrayOutputStreamPrivacyPolicy.toString());
            }
            break;
            case "TermsAndConditions" :
            {InputStream inputStreamPrivacyPolicy = getResources().openRawResource(R.raw.termsandconditions);

                ByteArrayOutputStream byteArrayOutputStreamtermsandconditions = new ByteArrayOutputStream();

                int pri;
                try {
                    pri = inputStreamPrivacyPolicy.read();
                    while (pri != -1) {
                        byteArrayOutputStreamtermsandconditions.write(pri);
                        pri = inputStreamPrivacyPolicy.read();
                    }
                    inputStreamPrivacyPolicy.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selecteditem.setTextSize(20);
                selecteditem.setText(byteArrayOutputStreamtermsandconditions.toString());
            }
            break;
            case "FeedBack" :
            {
                InputStream inputStreamPrivacyPolicy = getResources().openRawResource(R.raw.feedback);

                ByteArrayOutputStream byteArrayOutputStreamfeedback = new ByteArrayOutputStream();

                int pri;
                try {
                    pri = inputStreamPrivacyPolicy.read();
                    while (pri != -1) {
                        byteArrayOutputStreamfeedback.write(pri);
                        pri = inputStreamPrivacyPolicy.read();
                    }
                    inputStreamPrivacyPolicy.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selecteditem.setTextSize(20);
                selecteditem.setText(byteArrayOutputStreamfeedback.toString());
            }
            break;
        }

    }
}
