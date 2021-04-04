package com.zoiapp.zoi.Notes;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zoiapp.zoi.R;
import com.zoiapp.zoi.ModalClasses.UserProfile;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    public static ArrayList<UserProfile> mList;


    public Adapter(ArrayList<UserProfile> list)
    {
        mList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_note,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        UserProfile currentItem = mList.get(position);
        holder.noteContent.setText(currentItem.getItemNames());
        holder.noteQuantity.setText(currentItem.getItemQuantity());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText noteQuantity,noteContent;
        View view;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteContent = itemView.findViewById(R.id.itemNameEdit);
            noteQuantity = itemView.findViewById(R.id.itemQuantityEdit);

            noteContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mList.get(getAdapterPosition()).setItemNames(noteContent.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            noteQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mList.get(getAdapterPosition()).setItemQuantity(noteQuantity.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }
}
