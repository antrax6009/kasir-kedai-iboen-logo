
package com.kedaiiboe.kasir;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.json.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    static final String PREF="kedai_iboe_data";
    LinearLayout root;
    android.content.SharedPreferences sp;
    ArrayList<MenuItemData> menus = new ArrayList<>();
    ArrayList<CartItem> cart = new ArrayList<>();

    int BLACK=Color.rgb(9,11,16), YELLOW=Color.rgb(255,214,0), RED=Color.rgb(229,57,53);
    int WHITE=Color.WHITE, LIGHT=Color.rgb(247,247,247), TEXT=Color.rgb(30,30,30);

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        sp=getSharedPreferences(PREF,MODE_PRIVATE);
        loadMenus();
        showDashboard();
    }

    TextView tv(String text,int size,int color){
        TextView t=new TextView(this); t.setText(text); t.setTextSize(size); t.setTextColor(color);
        t.setPadding(12,10,12,10); return t;
    }
    Button btn(String text,int color){
        Button b=new Button(this); b.setText(text); b.setTextSize(15); b.setTextColor(Color.WHITE);
        b.setAllCaps(false); b.setBackground(round(color,18)); b.setPadding(10,8,10,8);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,58); lp.setMargins(8,8,8,8); b.setLayoutParams(lp);
        return b;
    }
    GradientDrawable round(int color,float r){ GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(r); return g; }
    ImageView banner(){
        ImageView im=new ImageView(this); im.setImageResource(com.kedaiiboe.kasir.R.drawable.kedai_iboe_banner);
        im.setScaleType(ImageView.ScaleType.CENTER_CROP); im.setAdjustViewBounds(true);
        im.setLayoutParams(new LinearLayout.LayoutParams(-1,210)); return im;
    }
    void base(String title){
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(LIGHT);
        ScrollView sv=new ScrollView(this); LinearLayout content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL);
        content.addView(banner()); content.addView(tv(title,26,BLACK));
        sv.addView(content); root.addView(sv,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
    }
    LinearLayout content(){
        ScrollView sv=(ScrollView)root.getChildAt(0); return (LinearLayout)sv.getChildAt(0);
    }
    void addTitle(String s){ content().addView(tv(s,22,TEXT)); }
    void showDashboard(){
        base("Kasir Kedai Iboen");
        addTitle("🔥 Selamat datang, Owner");
        Button trans=btn("🛒  TRANSAKSI",Color.rgb(255,112,30));
        Button menu=btn("🍜  KELOLA MENU",Color.rgb(255,193,7));
        Button report=btn("📊  LAPORAN",Color.rgb(46,125,50));
        Button settings=btn("⚙️  PENGATURAN",Color.rgb(103,58,183));
        Button out=btn("🚪  KELUAR",RED);
        content().addView(trans); content().addView(menu); content().addView(report); content().addView(settings); content().addView(out);
        trans.setOnClickListener(v->showTransactions());
        menu.setOnClickListener(v->showMenuManager());
        report.setOnClickListener(v->showReports());
        settings.setOnClickListener(v->showSettings());
        out.setOnClickListener(v->{ startActivity(new Intent(this,LoginActivity.class)); finish(); });
    }

    void showMenuManager(){
        base("🍜 Kelola Menu");
        Button add=btn("➕ Tambah Menu",RED); content().addView(add); add.setOnClickListener(v->menuDialog(null,-1));
        for(int i=0;i<menus.size();i++){
            final int idx=i; MenuItemData m=menus.get(i);
            LinearLayout row=new LinearLayout(this); row.setPadding(12,4,12,4); row.setGravity(Gravity.CENTER_VERTICAL);
            TextView info=tv(m.emoji+"  "+m.name+"\\nRp"+money(m.price),16,TEXT); row.addView(info,new LinearLayout.LayoutParams(0,80,1));
            Button e=new Button(this); e.setText("✏️"); row.addView(e,new LinearLayout.LayoutParams(64,64));
            Button d=new Button(this); d.setText("🗑️"); row.addView(d,new LinearLayout.LayoutParams(64,64));
            content().addView(row);
            e.setOnClickListener(v->menuDialog(m,idx));
            d.setOnClickListener(v->{menus.remove(idx);saveMenus();showMenuManager();});
        }
        Button back=btn("← Kembali",BLACK); content().addView(back); back.setOnClickListener(v->showDashboard());
    }

    void menuDialog(MenuItemData old,int index){
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(24,8,24,8);
        EditText name=new EditText(this); name.setHint("Nama menu"); if(old!=null)name.setText(old.name); box.addView(name);
        EditText price=new EditText(this); price.setHint("Harga (angka)"); price.setInputType(2); if(old!=null)price.setText(""+old.price); box.addView(price);
        TextView emoji=tv("😀 Pilih emoji: "+(old==null?"🍜":old.emoji),18,TEXT); box.addView(emoji);
        String[] emojis={"🍜","🌶️","🍗","🍚","🧋","🍊","🥤","🍳","🥟","🍟","🍔","🍕","🍨","☕","🥛","🍰","🔥","⭐","❤️","🍴"};
        GridLayout grid=new GridLayout(this); grid.setColumnCount(5);
        for(String em:emojis){Button eb=new Button(this);eb.setText(em);eb.setTextSize(22);grid.addView(eb,new ViewGroup.LayoutParams(110,70));eb.setOnClickListener(v->emoji.setText("😀 Pilih emoji: "+em));}
        box.addView(grid);
        new AlertDialog.Builder(this).setTitle(old==null?"Tambah Menu":"Edit Menu").setView(box)
            .setNegativeButton("Batal",null).setPositiveButton("Simpan",(d,w)->{
                String n=name.getText().toString().trim(); long p=0; try{p=Long.parseLong(price.getText().toString().trim());}catch(Exception ex){}
                String em=emoji.getText().toString().replace("😀 Pilih emoji: ","").trim();
                if(n.length()==0||p<=0){Toast.makeText(this,"Nama dan harga wajib diisi",Toast.LENGTH_SHORT).show();return;}
                MenuItemData m=new MenuItemData(n,p,em);
                if(old==null)menus.add(m);else menus.set(index,m);saveMenus();showMenuManager();
            }).show();
    }

    void showTransactions(){
        base("🛒 Transaksi");
        TextView totalTv=tv("Total: Rp0",24,RED); content().addView(totalTv);
        LinearLayout cartBox=new LinearLayout(this);cartBox.setOrientation(LinearLayout.VERTICAL);content().addView(cartBox);
        for(MenuItemData m:menus){
            Button b=btn(m.emoji+"  "+m.name+"  —  Rp"+money(m.price),BLACK);content().addView(b);
            b.setOnClickListener(v->{addCart(m);refreshCart(cartBox,totalTv);});
        }
        Button pay=btn("💵 BAYAR",RED);content().addView(pay);
        Button back=btn("← Kembali",BLACK);content().addView(back);back.setOnClickListener(v->showDashboard());
        pay.setOnClickListener(v->payDialog(totalTv.getText().toString()));
    }
    void addCart(MenuItemData m){
        for(CartItem c:cart)if(c.menu.name.equals(m.name)){c.qty++;return;}
        cart.add(new CartItem(m,1));
    }
    void refreshCart(LinearLayout box,TextView totalTv){
        box.removeAllViews(); long total=0;
        for(CartItem c:cart){total+=c.menu.price*c.qty;box.addView(tv(c.menu.emoji+" "+c.menu.name+" x"+c.qty+" = Rp"+money(c.menu.price*c.qty),16,TEXT));}
        totalTv.setText("Total: Rp"+money(total));
    }
    long cartTotal(){long t=0;for(CartItem c:cart)t+=c.menu.price*c.qty;return t;}
    void payDialog(String ignored){
        long total=cartTotal(); if(total<=0){Toast.makeText(this,"Belum ada pesanan",Toast.LENGTH_SHORT).show();return;}
        EditText paid=new EditText(this);paid.setHint("Uang dibayar");paid.setInputType(2);
        new AlertDialog.Builder(this).setTitle("Pembayaran Rp"+money(total)).setView(paid).setNegativeButton("Batal",null)
            .setPositiveButton("Bayar",(d,w)->{long p=0;try{p=Long.parseLong(paid.getText().toString());}catch(Exception e){}
                if(p<total){Toast.makeText(this,"Uang kurang",Toast.LENGTH_SHORT).show();return;}
                long change=p-total; recordSale(total); cart.clear(); new AlertDialog.Builder(this).setTitle("✅ Transaksi Berhasil")
                    .setMessage("Total: Rp"+money(total)+"\\nBayar: Rp"+money(p)+"\\nKembalian: Rp"+money(change))
                    .setPositiveButton("OK",(x,y)->showTransactions()).show();
            }).show();
    }
    void recordSale(long total){
        String date=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(new Date());
        String key="sales_"+date; long old=sp.getLong(key,0); sp.edit().putLong(key,old+total).putInt("count_"+date,sp.getInt("count_"+date,0)+1).apply();
    }
    void showReports(){
        base("📊 Laporan Penjualan");
        String date=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(new Date());
        long total=sp.getLong("sales_"+date,0);int count=sp.getInt("count_"+date,0);
        content().addView(tv("Tanggal: "+date,18,TEXT));
        content().addView(tv("💰 Total Penjualan\\nRp"+money(total),25,RED));
        content().addView(tv("🧾 Jumlah Transaksi\\n"+count,22,TEXT));
        Button back=btn("← Kembali",BLACK);content().addView(back);back.setOnClickListener(v->showDashboard());
    }
    void showSettings(){
        base("⚙️ Pengaturan");
        EditText shop=new EditText(this);shop.setHint("Nama kedai");shop.setText(sp.getString("shop","Kedai Iboen"));content().addView(shop);
        EditText addr=new EditText(this);addr.setHint("Alamat");addr.setText(sp.getString("addr",""));content().addView(addr);
        EditText wa=new EditText(this);wa.setHint("Nomor WhatsApp");wa.setInputType(2);wa.setText(sp.getString("wa",""));content().addView(wa);
        Button save=btn("💾 Simpan Pengaturan",RED);content().addView(save);
        Button pass=btn("🔑 Ganti Password Owner",BLACK);content().addView(pass);
        Button back=btn("← Kembali",BLACK);content().addView(back);
        save.setOnClickListener(v->{sp.edit().putString("shop",shop.getText().toString()).putString("addr",addr.getText().toString()).putString("wa",wa.getText().toString()).apply();Toast.makeText(this,"Pengaturan tersimpan",Toast.LENGTH_SHORT).show();});
        pass.setOnClickListener(v->changePassword());
        back.setOnClickListener(v->showDashboard());
    }
    void changePassword(){
        EditText p=new EditText(this);p.setHint("Password baru");p.setInputType(0x81);
        new AlertDialog.Builder(this).setTitle("Ganti Password Owner").setView(p).setNegativeButton("Batal",null)
            .setPositiveButton("Simpan",(d,w)->{String s=p.getText().toString();if(s.length()<4){Toast.makeText(this,"Minimal 4 karakter",Toast.LENGTH_SHORT).show();return;}sp.edit().putString("pass",s).apply();Toast.makeText(this,"Password diubah",Toast.LENGTH_SHORT).show();}).show();
    }
    void loadMenus(){
        String raw=sp.getString("menus","");
        try{JSONArray a=new JSONArray(raw);for(int i=0;i<a.length();i++){JSONObject o=a.getJSONObject(i);menus.add(new MenuItemData(o.getString("name"),o.getLong("price"),o.getString("emoji")));}}
        catch(Exception e){menus.clear();menus.add(new MenuItemData("MIE JEBEWW",12000,"🍜"));menus.add(new MenuItemData("SEBLAK",12000,"🌶️"));menus.add(new MenuItemData("AYAM GEPREK",14000,"🍗"));menus.add(new MenuItemData("NASI PUTIH",5000,"🍚"));menus.add(new MenuItemData("ES TEH",5000,"🧋"));menus.add(new MenuItemData("ES JERUK",6000,"🍊"));saveMenus();}
    }
    void saveMenus(){try{JSONArray a=new JSONArray();for(MenuItemData m:menus){JSONObject o=new JSONObject();o.put("name",m.name);o.put("price",m.price);o.put("emoji",m.emoji);a.put(o);}sp.edit().putString("menus",a.toString()).apply();}catch(Exception ignored){}}
    String money(long n){return NumberFormat.getInstance(new Locale("id","ID")).format(n);}
    static class MenuItemData{String name,emoji;long price;MenuItemData(String n,long p,String e){name=n;price=p;emoji=e;}}
    static class CartItem{MenuItemData menu;int qty;CartItem(MenuItemData m,int q){menu=m;qty=q;}}
}
