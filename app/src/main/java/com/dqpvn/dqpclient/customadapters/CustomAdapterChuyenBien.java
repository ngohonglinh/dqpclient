package com.dqpvn.dqpclient.customadapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dqpvn.dqpclient.R;
import com.dqpvn.dqpclient.models.ChuyenBien;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by linh3 on 22/11/2017.
 */

public class CustomAdapterChuyenBien extends BaseAdapter {

    Context context;
    int myLayout;
    ArrayList<ChuyenBien> arrayList;

    public CustomAdapterChuyenBien(Context context, int myLayout, ArrayList<ChuyenBien> arrayList) {
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
            //TextView tvOrdered= (TextView)convertView.findViewById(R.id.customlist_chuyenbien_tv1);
            TextView tvCol1Content=(TextView) convertView.findViewById(R.id.customlist_chuyenbien_tv2);
        ChuyenBien chuyenbien =arrayList.get(position);
        //tvOrdered.setText(String.valueOf(position+1));

        if (chuyenbien.getChuyenbien().length()>15){
            String haiKyTuNam= StringUtils.right(chuyenbien.getChuyenbien(),2);
            String bonKyTuNam= StringUtils.right(chuyenbien.getChuyenbien(),4);
            String ChuyenBienSauKhiCheBien="";
           if (chuyenbien.getChuyenbien().length()>18){
               ChuyenBienSauKhiCheBien=StringUtils.replace(chuyenbien.getChuyenbien(),bonKyTuNam,"");
           }else{
               ChuyenBienSauKhiCheBien=StringUtils.replace(chuyenbien.getChuyenbien(),bonKyTuNam,haiKyTuNam);
           }
           tvCol1Content.setText("Dự án | " +ChuyenBienSauKhiCheBien);
        }else{
            tvCol1Content.setText("Dự án | " +chuyenbien.getChuyenbien());
        }
        if (chuyenbien.getDachia()==0){
            tvCol1Content.setBackgroundColor(Color.parseColor("#5E35B1"));
            //tvCol1Content.setTextColor(Color.parseColor("#ffffff"));
        }else{
            tvCol1Content.setBackgroundColor(Color.parseColor("#4527A0"));
            //tvCol1Content.setTextColor(Color.parseColor("#000000"));
        }
        //set backgroud color as random
        Random rnd =new Random();
        int color = Color.rgb(rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256));
        //tvOrdered.setBackgroundColor(color);
        return convertView;
    }

}
