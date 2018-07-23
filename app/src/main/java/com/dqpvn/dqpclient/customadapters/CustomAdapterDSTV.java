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
import com.dqpvn.dqpclient.models.DSTV;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import static com.dqpvn.dqpclient.utils.utils.doubleGet;
import static com.dqpvn.dqpclient.utils.utils.longGet;

/**
 * Created by linh3 on 25/12/2017.
 */

public class CustomAdapterDSTV extends BaseAdapter {

    Context context;
    int myLayout;
    ArrayList<DSTV> arrList;
    private crudLocal crudLocaldb;


    public CustomAdapterDSTV(Context context, int myLayout, ArrayList<DSTV> mylist) {
        this.context = context;
        this.myLayout = myLayout;
        this.arrList = mylist;
        crudLocaldb=crudLocal.getInstance(context);
    }


    @Override
    public int getCount() {
        //tra ve so dong tren list, muon bao nhieu thi dua vao, neu khong thi tra ve all
        return arrList.size();
    }

    @Override
    public DSTV getItem(int position) {
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
        //new convertview null (lan dau chay) thi khoi tao va luu vao view holder.

            convertView = inflater.inflate(myLayout, null);

        final TextView tv1 = convertView.findViewById(R.id.customlist_dstv_tv1);
        final TextView tv2 = convertView.findViewById(R.id.customlist_dstv_tv2);
        final TextView tv4 = convertView.findViewById(R.id.customlist_dstv_tv4);
        final TextView tv5 = convertView.findViewById(R.id.customlist_dstv_tv5);
        final TextView tv6 = convertView.findViewById(R.id.customlist_dstv_tv6);
        final LinearLayout ly=convertView.findViewById(R.id.customlist_dstv_ly);

        DSTV dstv=arrList.get(position);
        tv1.setText(String.valueOf(position + 1));
        if (doubleGet(dstv.getDiem())==0.0){
            tv2.setText(dstv.getTen() + " | " + dstv.getNotes());
        }else{
            tv2.setText(dstv.getTen() + " | " + dstv.getNotes()+ " | Điểm: " + dstv.getDiem());
        }

        //viewHolder.tv3.setText(dstv.getNotes());
        tv4.setText(dstv.getTienchia());
        //tv5.setText(dstv.getTienmuon());
        String tenChuyenBien=crudLocaldb.ChuyenBien_getTenChuyenBien(dstv.getRkeychuyenbien());
        tv5.setText(crudLocaldb.DebtBook_SumForThuyenVien(dstv.getRkey(),tenChuyenBien));
        tv6.setText(dstv.getTiencanca());
        formatNumber(tv4);
        formatNumber(tv5);
        formatNumber(tv6);

        //set backgroud color as random
        Random rnd = new Random();
        int color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        tv1.setBackgroundColor(color);

        String colorBackGround="#388E3C";
        String colorAlternate="#43A047";
        if (position % 2 == 1) {
            ly.setBackgroundColor(Color.parseColor(colorBackGround));
        } else {
            ly.setBackgroundColor(Color.parseColor(colorAlternate));
        }

        return convertView;
    }


    private void formatNumber(TextView tv) {
        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            Long lv = longGet(tv.getText() + "");
            String get_value = formatter.format(lv);
            tv.setText(get_value);
        } catch (NumberFormatException e) {
            Log.e("error: ", tv.getText().toString());
        }

    }
}
