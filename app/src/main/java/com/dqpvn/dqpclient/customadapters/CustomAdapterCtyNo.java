package com.dqpvn.dqpclient.customadapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dqpvn.dqpclient.R;

import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.models.Chi;



import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by linh3 on 29/11/2017.
 */

public class CustomAdapterCtyNo extends BaseAdapter {
    final private String TAG = getClass().getSimpleName();

    Context context;
    int myLayout;
    ArrayList<Chi> arrayList;


    public CustomAdapterCtyNo(Context context, int myLayout, ArrayList<Chi> arrayList) {
        this.context = context;
        this.myLayout = myLayout;
        this.arrayList = arrayList;
    }


    @Override
    public int getCount() {
        //tra ve so dong tren list, muon bao nhieu thi dua vao, neu khong thi tra ve all
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //new convertview null (lan dau chay) thi khoi tao va luu vao view holder.

            convertView=inflater.inflate(myLayout,null);

        final TextView tv1=(TextView) convertView.findViewById(R.id.customlist_ctyno_tv1);
        final TextView tv2=(TextView) convertView.findViewById(R.id.customlist_ctyno_tv2);
        final TextView tv3=(TextView) convertView.findViewById(R.id.customlist_ctyno_tv3);
        final TextView tv4=(TextView) convertView.findViewById(R.id.customlist_ctyno_tv4);
        final TextView tv5=(TextView) convertView.findViewById(R.id.customlist_ctyno_tv5);
        final TextView tv6=(TextView) convertView.findViewById(R.id.customlist_ctyno_tv6);
        final TextView tv7=(TextView) convertView.findViewById(R.id.customlist_ctyno_tv7);
        LinearLayout ly=convertView.findViewById(R.id.customlist_ctyno_ly);

        String dt=arrayList.get(position).getDatra()+"";
        String gt=arrayList.get(position).getGiatri()+"";
        long conlai=0;
        long giatri=0;
        long datra=0;
        try{
            giatri=Long.parseLong(gt);
        }catch(NumberFormatException e){
            giatri=0;
        }
        try{
            datra=Long.parseLong(dt);
        }catch(NumberFormatException e){
            datra=0;
        }
        conlai=giatri-datra;

        tv1.setText(String.valueOf(position+1));
        tv2.setText(arrayList.get(position).getmTenChuyenBien());
        tv3.setText(arrayList.get(position).getNgayps());
        tv4.setText(arrayList.get(position).getLydo());
        tv5.setText(String.valueOf(giatri));
        tv6.setText(String.valueOf(datra));
        tv7.setText(String.valueOf(conlai));
        formatNumber(tv5);
        formatNumber(tv6);
        formatNumber(tv7);

        //set backgroud color as random
        Random rnd =new Random();
        int color = Color.rgb(rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256));
        tv1.setBackgroundColor(color);

        String colorAlternate="#EEEEEE";
        String colorBackGround="#E0E0E0";
        if (position % 2 == 1) {
            ly.setBackgroundColor(Color.parseColor(colorBackGround));
        } else {
            ly.setBackgroundColor(Color.parseColor(colorAlternate));
        }

        return convertView;
    }


    private void formatNumber(TextView tv){
        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            Long lv=Long.valueOf(tv.getText()+"");
            String get_value = formatter.format(lv);
            tv.setText(get_value);
        }catch (NumberFormatException e){
            Log.e("error: ", tv.getText().toString());
        }

    }
}

