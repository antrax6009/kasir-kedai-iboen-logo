
package com.kedaiiboe.kasir;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.widget.*;

public class SplashActivity extends Activity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        ImageView im=new ImageView(this);im.setImageResource(R.drawable.kedai_iboe_banner);im.setScaleType(ImageView.ScaleType.CENTER_CROP);setContentView(im);
        new Handler().postDelayed(()->{startActivity(new Intent(this,LoginActivity.class));finish();},1800);
    }
}
