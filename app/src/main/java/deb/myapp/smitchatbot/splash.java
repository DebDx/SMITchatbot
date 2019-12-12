package deb.myapp.smitchatbot;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(splash.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(5000)
                .withBackgroundColor(Color.parseColor("#ffffff"))
                .withAfterLogoText("CHATBOT")
                .withFooterText("Developed By Deborshi Deb")
                .withLogo(R.mipmap.smit);
                 config.getFooterTextView().setTextColor(Color.BLACK);
                 config.getAfterLogoTextView().setTextColor(Color.BLACK);
        View splash = config.create();
        setContentView(splash);
    }
}

