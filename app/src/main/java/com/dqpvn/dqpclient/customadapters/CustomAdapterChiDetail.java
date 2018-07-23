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
import com.dqpvn.dqpclient.models.ChiDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by linh3 on 13/12/2017.
 */

public class CustomAdapterChiDetail extends BaseAdapter {

    Context context;
    int myLayout;
    ArrayList<ChiDetail> arrList;


    public CustomAdapterChiDetail(Context context, int myLayout, ArrayList<ChiDetail> mylist) {
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
    public ChiDetail getItem(int position) {
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

        final TextView tv1 = convertView.findViewById(R.id.customlist_chidetail_tv1);
        final TextView tv2 = convertView.findViewById(R.id.customlist_chidetail_tv2);
        final TextView tv3 = convertView.findViewById(R.id.customlist_chidetail_tv3);
        final TextView tv4 = convertView.findViewById(R.id.customlist_chidetail_tv4);
        final TextView tv5 = convertView.findViewById(R.id.customlist_chidetail_tv5);
        final TextView tv6 = convertView.findViewById(R.id.customlist_chidetail_tv6);
        final LinearLayout ly=convertView.findViewById(R.id.customlist_chidetail_ly);

        ChiDetail chiDetail=arrList.get(position);
        tv1.setText(String.valueOf(position + 1));
        tv2.setText(chiDetail.getTenchuyenbien());
        tv3.setText(chiDetail.getSanpham());
        tv4.setText(chiDetail.getSoluong());
        tv5.setText(chiDetail.getDongia());
        tv6.setText(chiDetail.getThanhtien());
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
            Long lv = Long.valueOf(tv.getText() + "");
            String get_value = formatter.format(lv);
            tv.setText(get_value);
        } catch (NumberFormatException e) {
            Log.e("error: ", tv.getText().toString());
        }

    }
}