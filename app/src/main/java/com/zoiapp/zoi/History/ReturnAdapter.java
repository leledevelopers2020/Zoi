package com.zoiapp.zoi.History;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.zoiapp.zoi.R;

import java.util.ArrayList;

class ReturnAdapter extends RecyclerView.Adapter<ReturnAdapter.ReturnViewHolder>{

    public static ArrayList<UserProfile> mList;
    public ReturnAdapter(ArrayList<UserProfile> list){
        mList = list;
    }

    @NonNull
    @Override
    public ReturnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history,parent,false);
        return new ReturnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReturnViewHolder holder, final int position) {
        final UserProfile currentItem = mList.get(position);
        holder.itxt.setText(currentItem.getItemNames());
        holder.qtxt.setText(currentItem.getItemQuantity());
        holder.ptxt.setText("₹ "+currentItem.getItemPrice());
        holder.checkBox.setChecked(currentItem.isSelected());


        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) holder.checkBox.getTag();
                if (mList.get(pos).isSelected()) {
                    mList.get(pos).setSelected(false);
                }
                else {
                    mList.get(pos).setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ReturnViewHolder extends RecyclerView.ViewHolder
    {
        public CheckBox checkBox;
        TextView itxt,qtxt,ptxt;
        public ReturnViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            itxt = itemView.findViewById(R.id.txt1);
            qtxt = itemView.findViewById(R.id.txt2);
            ptxt = itemView.findViewById(R.id.txt3);
        }
    }


}
//https://android-pratap.blogspot.com/2015/01/recyclerview-with-checkbox-example.html
//https://codinginfinite.com/recycler-view-scroll-issue/