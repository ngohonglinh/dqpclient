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
import com.dqpvn.dqpclient.TicketActivity;
import com.dqpvn.dqpclient.models.Ticket;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by linh3 on 19/12/2017.
 */

public class CustomAdapterTicket extends BaseAdapter {

    Context context;
    int myLayout;
    ArrayList<Ticket> arrList;


    public CustomAdapterTicket(Context context, int myLayout, ArrayList<Ticket> mylist) {
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
    public Ticket getItem(int position) {
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

        final TextView tv1 = convertView.findViewById(R.id.customlist_ticket_tv1);
        final TextView tv2 = convertView.findViewById(R.id.customlist_ticket_tv2);
        final TextView tv3 = convertView.findViewById(R.id.customlist_ticket_tv3);
        final TextView tv4 = convertView.findViewById(R.id.customlist_ticket_tv4);
        final TextView tv5 = convertView.findViewById(R.id.customlist_ticket_tv5);
        final TextView tv6 = convertView.findViewById(R.id.customlist_ticket_tv6);
        final LinearLayout ly=convertView.findViewById(R.id.customlist_ticket_ly);

        Ticket ticket=arrList.get(position);
        tv2.setText(ticket.getUsername());
        tv3.setText(ticket.getOpendate());
        tv4.setText(ticket.getAmount());
        tv5.setText(ticket.getUsed());
        tv6.setText(ticket.getComeback());
        formatNumber(tv4);
        formatNumber(tv5);
        formatNumber(tv6);

        //set backgroud color as random
        Random rnd = new Random();
        int color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        String colorBackGround="#43A047";
        String colorAlternate="#2E7D32";
        if (ticket.getFinished()==0){
            ly.setBackgroundColor(Color.parseColor(colorBackGround));
            tv1.setBackgroundColor(color);
            tv1.setText(String.valueOf(position + 1));
        }else{
            ly.setBackgroundColor(Color.parseColor(colorAlternate));
            tv1.setBackgroundColor(Color.parseColor("#424242"));
            tv1.setText("F");
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
