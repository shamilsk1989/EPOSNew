package com.alhikmahpro.www.e_pos.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alhikmahpro.www.e_pos.R;
import com.alhikmahpro.www.e_pos.data.SessionHandler;
import com.alhikmahpro.www.e_pos.data.dbHelper;
import com.goodiebag.pinview.Pinview;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.security.AccessController.getContext;

public class PasscodeActivity extends AppCompatActivity {

    @BindView(R.id.pinview)
    Pinview pinview;
    String TAG="PasscodeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        ButterKnife.bind(this);
        final dbHelper helper=new dbHelper(this);

        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean b) {
               // pinview.getChildAt(0).requestFocus();
                String pin=pinview.getValue();
                Log.d(TAG,"PIN :"+pin);
                String res=helper.getLogin(pin);
                Log.d(TAG,"result:"+res);
                if(res!=null){

                    SessionHandler.getInstance(getApplicationContext()).setUserType(res);
                    Intent myIntent = new Intent(PasscodeActivity.this, HomeActivity.class);
                    PasscodeActivity.this.startActivity(myIntent);
                    closeKey();
                    //startActivity(new Intent(this, PasscodeActivity.class));
                    finish();

                }
                else{
                    Toast.makeText(getApplicationContext(), "Wrong Pin", Toast.LENGTH_LONG).show();
//                    for (int i = 0;i < pinview.getPinLength();i++) {
//                        pinview.onKey(pinview.getFocusedChild(), KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_DEL));
//                    }
                }




            }


        });



        pinview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode==KeyEvent.KEYCODE_DEL){
                    for(int i=0;i<pinview.getPinLength();i++){
                        pinview.onKey(pinview.getFocusedChild(),KeyEvent.KEYCODE_DEL,new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_DEL));
                    }
                }
                return false;
            }
        });
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.d("delete","delete press :");
//        if(keyCode==KeyEvent.KEYCODE_DEL){
//            for (int i = 0;i < pinview.getPinLength();i++) {
//                pinview.onKey(pinview.getFocusedChild(), KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_DEL));
//            }
//            return true;
//        }
//        return false;
//    }

    private void closeKey(){
        View view=this.getCurrentFocus();
        if(view!=null){
            InputMethodManager methodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }

    }

}
