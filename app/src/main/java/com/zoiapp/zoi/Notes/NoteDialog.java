package com.zoiapp.zoi.Notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.zoiapp.zoi.Cam_Gal_Phn.Camera;
import com.zoiapp.zoi.R;

public class NoteDialog extends AppCompatDialogFragment {
    private EditText itemName,itemQuantity;
    private Button save,addMore;
    private NoteDialogListener listener;

    public static boolean isNextValue() {
        return nextValue;
    }

    public static void setNextValue(boolean nextValue) {
        NoteDialog.nextValue = nextValue;
    }

    public static boolean nextValue=true;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        itemName = view.findViewById(R.id.item_name);
        itemQuantity = view.findViewById(R.id.item_Quantity);
        save = view.findViewById(R.id.saveDialog);
        addMore = view.findViewById(R.id.addMoreDialog);
        builder.setView(view)
                .setTitle("Add Items")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setNextValue(true);
                        dismiss();
                    }
                });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnectivity()) {
                    String itemname = itemName.getText().toString();
                    String itemquantity = itemQuantity.getText().toString();
                    Boolean productName = validate(itemname, 1);
                    Boolean productQuantity = validate(itemquantity, 2);
                    if (productName && productQuantity) {
                        listener.applyText(itemname, itemquantity, false);
                        setNextValue(true);
                        dismiss();
                    }
                }
            }
        });
        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnectivity()) {
                    setNextValue(true);
                    String itemname = itemName.getText().toString();
                    String itemquantity = itemQuantity.getText().toString();
                    Boolean productName = validate(itemname, 1);
                    Boolean productQuantity = validate(itemquantity, 2);
                    if (productName && productQuantity) {
                        listener.applyText(itemname, itemquantity, true);
                        setNextValue(true);
                        dismiss();
                    }
                }
            }
        });
        return builder.create();
    }
    private Boolean internetConnectivity()
    {
        boolean connectivityInfo=false;
        ConnectivityManager connection=(ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connection.getActiveNetworkInfo();
        if(activeNetwork!=null) {
            if (activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE||activeNetwork.getType()==ConnectivityManager.TYPE_WIFI) {
                connectivityInfo = true;
            }
        }
        return connectivityInfo;
    }
    private Boolean validate(String itemDetails, int type) {
        Boolean returnValue = true;

        if (itemDetails.isEmpty() || itemDetails.length() >= 20) {
            if (type == 1) {
                itemName.setError("Enter the Proper Product Name");
                itemName.requestFocus();
                returnValue = false;
            } else if (type == 2) {
                itemQuantity.setError("Enter the valid quantity");
                itemQuantity.requestFocus();
                returnValue = false;
            }

        }
        return returnValue;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NoteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implement NoteDialogListener");
        }
    }

    public interface NoteDialogListener{
        void applyText(String itemname,String itemquantity,Boolean state);

    }

}