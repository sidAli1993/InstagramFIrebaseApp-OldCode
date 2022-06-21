package com.myvuapp.socialapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myvuapp.socialapp.Model.checkout;

import java.util.List;

import com.myvuapp.socialapp.R;;


public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.ViewHolder>{

    private Context mContext;
    private List<checkout> mComment;
    private FirebaseUser firebaseUser;

    // connect comment with post
    public SaleAdapter(Context mContext, List<checkout> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_sales, parent, false);
        return new SaleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final checkout comment = mComment.get(position);

        holder.tvbookname.setText(comment.getBookName());
        holder.tvdate.setText(comment.getDate());
        holder.tvaddress.setText(comment.getAddress());
        holder.tvphone.setText(comment.getPhone());
        holder.tvtype.setText(comment.getSaleType());
        holder.tvauthorname.setText("Author: "+comment.getAuthorName());
        holder.tvcity.setText("City: "+comment.getCity());
        holder.tvprice.setText("Price: "+comment.getPrice());
        if (comment.getBuyername()!=null){
            holder.tvbuyername.setText("Buyer: "+comment.getBuyername());
        }




    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvbookname,tvdate,tvaddress,tvphone,tvtype,tvauthorname,tvcity,tvprice,tvbuyername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvbookname = itemView.findViewById(R.id.tvbookname1);
            tvaddress = itemView.findViewById(R.id.tvaddress);
            tvphone = itemView.findViewById(R.id.tvphone);
            tvtype = itemView.findViewById(R.id.tvtype);
            tvauthorname = itemView.findViewById(R.id.tvauthername);
            tvdate = itemView.findViewById(R.id.tvdate);
            tvprice = itemView.findViewById(R.id.tvprice1);
            tvcity = itemView.findViewById(R.id.tvcity);
            tvbuyername=itemView.findViewById(R.id.tvbuyername);

        }
    }

}
