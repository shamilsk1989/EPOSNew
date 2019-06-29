package com.alhikmahpro.www.e_pos.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;

import java.util.Timer;
import java.util.TimerTask;

public class FlashActivity extends AppCompatActivity {
    boolean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        dbHelper helper=new dbHelper(this);
        user=helper.getAllLogin("User");


        moveToNextScreen();
    }

    public void moveToNextScreen() {
        final Timer timer = new Timer();
        TimerTask splashTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (SessionHandler.getInstance(FlashActivity.this).isUserloggedIn()) {

                            if(user){
                                SessionHandler.getInstance(FlashActivity.this).setSessionStart(true);
                                gotoPasscodeScreen();
                            }
                            else{
                                gotoHomeScreen();
                            }
                        }
                        else {
                            gotoLoginScreen();
                        }
                        finish();
                    }
                });
            }
        };

        timer.schedule(splashTask, 2600);
    }

    private void gotoPasscodeScreen() {
        startActivity(new Intent(this,PasscodeActivity.class));
    }


    private void gotoHomeScreen() {
        startActivity(new Intent(this,HomeActivity.class));
        }

    private void gotoLoginScreen() {
        startActivity(new Intent(this,LoginActivity.class));
        }
}
