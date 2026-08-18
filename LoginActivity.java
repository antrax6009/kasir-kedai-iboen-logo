
package com.kedaiiboe.kasir;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

public class LoginActivity extends Activity {
    int BLACK=Color.rgb(9,11,16), YELLOW=Color.rgb(255,214,0), RED=Color.rgb(229,57,53);
    android.content.SharedPreferences sp;
    @Override public void onCreate(Bundle b){
        super.onCreate(b); sp=getSharedPreferences("kedai_iboe_data",MODE_PRIVATE); show();
    }
    GradientDrawable round(int c,float r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(r);return g;}
    TextView tv(String s,int z,int c){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);t.setGravity(Gravity.CENTER);t.setPadding(10,12,10,12);return t;}
    void show(){
        LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(28,40,28,28);l.setGravity(Gravity.CENTER_HORIZONTAL);l.setBackgroundColor(BLACK);
        ImageView im=new ImageView(this);im.setImageResource(R.drawable.kedai_iboe_banner);im.setScaleType(ImageView.ScaleType.CENTER_CROP);im.setAdjustViewBounds(true);l.addView(im,new LinearLayout.LayoutParams(-1,230));
        l.addView(tv("KASIR KEDAI IBOEN",28,Color.WHITE));
        l.addView(tv("🔐 Login Owner",18,YELLOW));
        EditText user=new EditText(this);user.setHint("Username");user.setTextColor(Color.WHITE);user.setHintTextColor(Color.LTGRAY);l.addView(user,new LinearLayout.LayoutParams(-1,60));
        EditText pass=new EditText(this);pass.setHint("Password");pass.setInputType(0x81);pass.setTextColor(Color.WHITE);pass.setHintTextColor(Color.LTGRAY);l.addView(pass,new LinearLayout.LayoutParams(-1,60));
        Button masuk=new Button(this);masuk.setText("MASUK");masuk.setTextColor(Color.BLACK);masuk.setTextSize(17);masuk.setAllCaps(false);masuk.setBackground(round(YELLOW,18));l.addView(masuk,new LinearLayout.LayoutParams(-1,62));
        l.addView(tv("Default: owner / 1234",14,Color.LTGRAY));
        setContentView(l);
        masuk.setOnClickListener(v->{String u=user.getText().toString().trim();String p=pass.getText().toString();String saved=sp.getString("pass","1234");
            if(u.equalsIgnoreCase("owner")&&p.equals(saved)){startActivity(new Intent(this,MainActivity.class));finish();}
            else Toast.makeText(this,"Username/password salah",Toast.LENGTH_SHORT).show();
        });
    }
}
