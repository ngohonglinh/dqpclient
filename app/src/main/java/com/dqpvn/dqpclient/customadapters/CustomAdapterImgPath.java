package com.dqpvn.dqpclient.customadapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dqpvn.dqpclient.R;
import com.dqpvn.dqpclient.models.ImgStore;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by linh3 on 05/01/2018.
 */

public class CustomAdapterImgPath extends BaseAdapter {

    Context context;
    int myLayout;
    ArrayList<ImgStore> arrayList;

    public CustomAdapterImgPath(Context context, int myLayout, ArrayList<ImgStore> arrayList) {
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
        ViewHolder viewHolder;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //new convertview null (lan dau chay) thi khoi tao va luu vao view holder.
        if (convertView==null){
            convertView=inflater.inflate(myLayout,null);
            viewHolder=new ViewHolder();
            viewHolder.tvOrdered= (TextView)convertView.findViewById(R.id.customlist_imgpath_tv1);
            convertView.setTag(viewHolder);
        }else{
            //Nue view holder da co du lieu thi lay dung tranh tao lai phong bi lag
            viewHolder= (ViewHolder) convertView.getTag();
        }
        ImgStore imgstore =arrayList.get(position);
        viewHolder.tvOrdered.setText(String.valueOf(position+1));
        //set backgroud color as random
        Random rnd =new Random();
        int color = Color.rgb(rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256));
        viewHolder.tvOrdered.setBackgroundColor(color);

        return convertView;
    }

    public class ViewHolder{
        TextView tvOrdered;
    }
}
