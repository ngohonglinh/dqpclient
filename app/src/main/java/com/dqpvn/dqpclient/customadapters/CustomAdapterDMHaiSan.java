package com.dqpvn.dqpclient.customadapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dqpvn.dqpclient.R;
import com.dqpvn.dqpclient.models.DMHaiSan;
import com.dqpvn.dqpclient.utils.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import static com.dqpvn.dqpclient.utils.utils.longGet;

/**
 * Created by linh3 on 31/03/2018.
 */

public class CustomAdapterDMHaiSan extends BaseAdapter {

    Context context;
    int myLayout;
    ArrayList<DMHaiSan> arrayList;

    public CustomAdapterDMHaiSan(Context context, int myLayout, ArrayList<DMHaiSan> arrayList) {
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

        final TextView tv1=(TextView)convertView.findViewById(R.id.customlist_dmhaisan_tv1);
        final TextView tv2=(TextView) convertView.findViewById(R.id.customlist_dmhaisan_tv2);
        final TextView tv3=(TextView) convertView.findViewById(R.id.customlist_dmhaisan_tv3);
        final TextView tv4=(TextView) convertView.findViewById(R.id.customlist_dmhaisan_tv4);
        final TextView tv5=(TextView) convertView.findViewById(R.id.customlist_dmhaisan_tv5);
        final LinearLayout ly=convertView.findViewById(R.id.customlist_dmhaisan_ly);

        tv1.setText(String.valueOf(position+1));
        tv2.setText(arrayList.get(position).getTenhs());
        tv3.setText(arrayList.get(position).getPhanloai());
        tv4.setText(utils.getDateFromMiliSecond(longGet(arrayList.get(position).getUpdatetime())));
        tv5.setText(formatNumber(arrayList.get(position).getDongia()));
        //set backgroud color as random
        Random rnd =new Random();
        int color = Color.rgb(rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256));
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

    private String formatNumber(String tv){
        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            Long lv=longGet(tv);
            String get_value = formatter.format(lv);
            return get_value;
        }catch (NumberFormatException e){
            Log.e("error: ", e.getMessage().toString());
            return tv;
        }

    }
}
