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
import com.dqpvn.dqpclient.models.TicketDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by linh3 on 18/03/2018.
 */

public class CustomAdapterTicketDetail extends BaseAdapter {

    Context context;
    int myLayout;
    ArrayList<TicketDetail> arrList;


    public CustomAdapterTicketDetail(Context context, int myLayout, ArrayList<TicketDetail> mylist) {
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
    public TicketDetail getItem(int position) {
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

        final TextView tv1 = convertView.findViewById(R.id.customlist_ticketdetail_tv1);
        final TextView tv2 = convertView.findViewById(R.id.customlist_ticketdetail_tv2);
        final TextView tv3 = convertView.findViewById(R.id.customlist_ticketdetail_tv3);
        final TextView tv4 = convertView.findViewById(R.id.customlist_ticketdetail_tv4);
        final TextView tv5 = convertView.findViewById(R.id.customlist_ticketdetail_tv5);
        final LinearLayout ly=convertView.findViewById(R.id.customlist_ticketdetail_ly);

        TicketDetail ticket=arrList.get(position);
        tv1.setText(String.valueOf(position + 1));
        tv2.setText(ticket.getNgayps());
        tv3.setText(ticket.getForuser());
        tv4.setText(ticket.getAmount());
        tv5.setText(ticket.getNotes());
        formatNumber(tv4);

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
