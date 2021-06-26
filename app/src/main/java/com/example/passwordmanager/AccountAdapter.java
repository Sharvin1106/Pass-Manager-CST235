package com.example.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder>
implements View.OnClickListener
{

    View.OnClickListener ocl;
    List<account> acclist;
    Context context;
    public AccountAdapter(Context context, List<account> acclist) {
        this.acclist = acclist;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.acc_list,parent,false);
        view.setOnClickListener(this);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        holder.apptype.setText(acclist.get(position).getApp_name());
        holder.email.setText(acclist.get(position).getE_mail());
        holder.last_edited.setText(acclist.get(position).getLast_edited());
        holder.groImage.setImageResource(Integer.parseInt(acclist.get(position).getLogo()));
        //Picasso.get().load(acclist.get(position).getImageUri()).into(holder.groImage);

    }

    @Override
    public int getItemCount() {
        return acclist.size();
    }

    public boolean ocl(View.OnClickListener ocl)
    {
        this.ocl = ocl;
        return true;
    }

    @Override
    public void onClick(View v) {
        ocl.onClick(v);
    }


    public static final class AccountViewHolder extends RecyclerView.ViewHolder{

        ImageView groImage;
        TextView apptype,email,last_edited;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);

            groImage = itemView.findViewById(R.id.imageView2);
            apptype = itemView.findViewById(R.id.textaccount);
            email = itemView.findViewById(R.id.textsecondtext);
            last_edited=itemView.findViewById(R.id.dateedited);
        }
    }

}
