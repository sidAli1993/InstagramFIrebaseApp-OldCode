package com.myvuapp.socialapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jackandphantom.circularimageview.CircleImage;
import com.myvuapp.socialapp.Chat;
import com.myvuapp.socialapp.Model.messages;

import java.util.ArrayList;
import java.util.List;

import com.myvuapp.socialapp.R;;

public class RecyclerAdapter extends RecyclerView.Adapter {
    private List<messages> mDataSet = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;


    public RecyclerAdapter(Context context, List<messages> dataSet) {
        mContext = context;
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);

        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_inbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        final ViewHolder holder = (ViewHolder) h;

        if (mDataSet != null && 0 <= position && position < mDataSet.size()) {
            final messages data = mDataSet.get(position);

            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
           FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
           if (firebaseUser.getUid().equals(mDataSet.get(position).getSenderID())){
               holder.textView.setText(mDataSet.get(position).getRecieverName());
               Glide.with(mContext).load(Uri.parse(mDataSet.get(position).getRecieverImg())).into(holder.imv);
               holder.tvdate.setText(data.getTime());
               holder.relav_list_item_data.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent=new Intent(mContext,Chat.class);
                       intent.putExtra("inboxid",mDataSet.get(position).getInboxID());
                       intent.putExtra("name",mDataSet.get(position).getRecieverName());
                       intent.putExtra("img",mDataSet.get(position).getRecieverImg());
                       mContext.startActivity(intent);
                   }
               });
           }else {
               holder.textView.setText(mDataSet.get(position).getSendername());
               if (mDataSet.get(position).getSenderImg()!=null){
                   Glide.with(mContext).load(Uri.parse(mDataSet.get(position).getSenderImg())).into(holder.imv);
               }

               holder.tvdate.setText(data.getTime());
               holder.relav_list_item_data.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent=new Intent(mContext,Chat.class);
                       intent.putExtra("inboxid",mDataSet.get(position).getInboxID());
                       intent.putExtra("name",mDataSet.get(position).getSendername());
                       intent.putExtra("img",mDataSet.get(position).getSenderImg());
                       mContext.startActivity(intent);
                   }
               });
           }
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null)
            return 0;
        return mDataSet.size();
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     */

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     */

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView,tvdate,tvstatus;
        RelativeLayout relav_list_item_data;
        CircleImage imv;
        public ViewHolder(View itemView) {
            super(itemView);
            imv=(CircleImage)itemView.findViewById(R.id.imv_list_item);
            textView = (TextView) itemView.findViewById(R.id.tv_list_item_person_name);
            tvdate = (TextView) itemView.findViewById(R.id.tv_list_item_date);
            tvstatus = (TextView) itemView.findViewById(R.id.tv_list_item_status);
            relav_list_item_data=itemView.findViewById(R.id.relav_list_item_data);
        }


//            textView.setText(data.getName());
//            tvdate.setText(data.getTime());
//            tvstatus.setText(data.getStatus());
//
//            frontLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String displayText = "" + data.getName() + " clicked";
//                    Toast.makeText(mContext, displayText, Toast.LENGTH_SHORT).show();
//                    Log.d("RecyclerAdapter", displayText);
//                    Intent intent=new Intent(mContext, ChatActivity.class);
//                    startactivity(intent);
//                }
//            });


    }
}