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
import com.dqpvn.dqpclient.models.DSTV;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.utils.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import static com.dqpvn.dqpclient.utils.utils.getStringLeft;

/**
 * Created by linh3 on 25/12/2017.
 */

public class CustomAdapterDebtBook extends BaseAdapter {

    Context context;
    int myLayout;
    ArrayList<DebtBook> arrList;


    public CustomAdapterDebtBook(Context context, int myLayout, ArrayList<DebtBook> mylist) {
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
    public DebtBook getItem(int position) {
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
            final TextView tv1 = convertView.findViewById(R.id.customlist_debtbook_tv1);
            final TextView tv2 = convertView.findViewById(R.id.customlist_debtbook_tv2);
            final TextView tv3 = convertView.findViewById(R.id.customlist_debtbook_tv3);
            final TextView tv4 = convertView.findViewById(R.id.customlist_debtbook_tv4);
            final LinearLayout ly=convertView.findViewById(R.id.customlist_debtbook_ly);
        DebtBook debtBook=arrList.get(position);
        String s=formatNumber(debtBook.getSotien());
        tv1.setText(String.valueOf(position + 1));
        tv2.setText(debtBook.getTen());
        tv3.setText(s+ " | " + debtBook.getNgayps());
        tv4.setText(getStringLeft(debtBook.getChuyenbien(),"@") + " | " + debtBook.getLydo());


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



    private String formatNumber(String tv) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            Long lv = utils.longGet(tv);
            String get_value = formatter.format(lv);
            return get_value;
    }
}
