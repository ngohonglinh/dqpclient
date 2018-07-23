package com.dqpvn.dqpclient.customadapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dqpvn.dqpclient.R;
import com.dqpvn.dqpclient.crudmanager.crudLocal;
import com.dqpvn.dqpclient.models.BanHSDetail;
import com.dqpvn.dqpclient.utils.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import static com.dqpvn.dqpclient.utils.utils.doubleGet;
import static com.dqpvn.dqpclient.utils.utils.getStringLeft;
import static com.dqpvn.dqpclient.utils.utils.isBad;
import static com.dqpvn.dqpclient.utils.utils.longGet;
import static com.dqpvn.dqpclient.utils.utils.round;

/**
 * Created by linh3 on 31/03/2018.
 */

public class CustomAdapterBanHSDetail extends BaseAdapter {

    Context context;
    int myLayout;
    ArrayList<BanHSDetail> arrList;

    public CustomAdapterBanHSDetail(Context context, int myLayout, ArrayList<BanHSDetail> mylist) {
        this.context = context;
        this.myLayout = myLayout;
        this.arrList = mylist;
    }


    @Override
    public int getCount() {
        //tra ve so dong tren list, muon bao nhieu thi dua vao, neu khong thi tra ve all
        return arrList.size();
    }

    @Override
    public BanHSDetail getItem(int position) {
        return arrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(myLayout, null);

        final TextView tv1 = convertView.findViewById(R.id.customlist_banhsdetail_tv1);
        final TextView tv2 = convertView.findViewById(R.id.customlist_banhsdetail_tv2);
        final TextView tv3 = convertView.findViewById(R.id.customlist_banhsdetail_tv3);
        final TextView tv4 = convertView.findViewById(R.id.customlist_banhsdetail_tv4);

        int p=position+1;
        BanHSDetail banhsdetail=arrList.get(position);
        tv1.setText(String.valueOf(p));
        tv4.setText(String.valueOf("*"));
        tv2.setText(strL(banhsdetail.getSoluong()));
        tv3.setText(strR(banhsdetail.getSoluong()));
        int color = Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.colorPrimary)));
        if (p % 5 ==0) {
            double z=0;
            for (int i=p-1;i>p-6;i--){
                BanHSDetail bhddt=arrList.get(i);
                z+=doubleGet(bhddt.getSoluong());
            }
            tv4.setText(formatNumber(String.valueOf(z)));
            tv1.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorAccent))));
            tv4.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorAccent))));
            tv1.setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorWhite))));
            tv4.setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorWhite))));
            tv1.setTypeface(Typeface.DEFAULT_BOLD);
            tv4.setTypeface(Typeface.DEFAULT_BOLD);
            tv2.setBackgroundColor(color);
            tv3.setBackgroundColor(color);
            //tv2.setTypeface(Typeface.DEFAULT_BOLD);
            //tv3.setTypeface(Typeface.DEFAULT_BOLD);

        }

        if (p % 5 ==1 && p>1) {
            double z=0;
            for (int i=0;i<p-1;i++){
                BanHSDetail bhddt=arrList.get(i);
                z+=doubleGet(bhddt.getSoluong());
            }
            tv4.setText(formatNumber(String.valueOf(z)));
            tv4.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorPrimary))));
            tv4.setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorWhite))));
            tv4.setTypeface(Typeface.DEFAULT_BOLD);

        }

        if (p==arrList.size()){
            crudLocal crudLocaldb=crudLocal.getInstance(context);
            String s=crudLocaldb.BanHSDetail_SumSLHaiSanByRkeyThuDetail(arrList.get(0).getRkeythudetail());
            double d=doubleGet(s);
            String d2=formatNumber(d+"");
            tv4.setText(d2);
            tv1.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorPurple))));
            tv1.setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorWhite))));
            tv1.setTypeface(Typeface.DEFAULT_BOLD);
            tv4.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorPurple))));
            tv4.setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.colorWhite))));
            tv4.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return convertView;
    }

    private String strL(String tv) {
        if (isBad(tv)){
            return "";
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        if (tv.contains("\\.")){
            String [] arr=tv.split(".");
            arr =tv.split(".");
            return formatter.format(arr[0]);
        }else{
            try{
                return formatter.format(longGet(tv));
            }catch (NumberFormatException e){
                double s=Double.valueOf(tv).longValue();
                return longGet(String.valueOf(s))+"";
            }

        }
    }
    private String strR(String tv) {
        if (isBad(tv)){
            return "";
        }
        if (tv.contains(".")){
            String [] arr=tv.split(".");
            arr =tv.split("\\.");
            return "."+arr[1];
        }else{
            return "";
        }
    }
    private String formatNumber(String tv) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        double lv = utils.doubleGet(tv);
        String get_value = formatter.format(lv);
        return get_value;
    }
}
