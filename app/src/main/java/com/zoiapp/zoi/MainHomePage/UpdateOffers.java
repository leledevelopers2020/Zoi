package com.zoiapp.zoi.MainHomePage;

import androidx.recyclerview.widget.DiffUtil;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;

import java.util.ArrayList;

class UpdateOffers extends DiffUtil.Callback {

    private  ArrayList<ItemModelClass> oldList;
    private  ArrayList<ItemModelClass> newList;

    public UpdateOffers(ArrayList<ItemModelClass> oldList, ArrayList<ItemModelClass> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        System.out.println("Comparision : "+oldList.get(oldItemPosition).getProductName() +newList.get(newItemPosition).getProductName());
        return oldList.get(oldItemPosition).getProductName() == newList.get(newItemPosition).getProductName();
    }
}
