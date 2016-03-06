package com.example.loafsmac.rubyred;

import android.widget.BaseAdapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by bertolopez-cruz on 2/10/16.
 */
public class SaveContactAdapter extends BaseAdapter {

    public List<SaveContact> mContactData;
    private ArrayList<SaveContact> arraylist;
    Context mContext;
    ViewHolder tempHoler;

    public SaveContactAdapter(List<SaveContact> selectUsers, Context context) {
        mContactData = selectUsers;
        mContext = context;
        this.arraylist = new ArrayList<SaveContact>();
        this.arraylist.addAll(mContactData);
    }

    @Override
    public int getCount() {
        return mContactData.size();
    }

    @Override
    public Object getItem(int i) {
        return mContactData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        tempHoler = new ViewHolder();

        if (view == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.view_contact_format, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        tempHoler.title = (TextView) view.findViewById(R.id.contactname);
        tempHoler.phone = (TextView) view.findViewById(R.id.contactnumber);
        tempHoler.check = (CheckBox) view.findViewById(R.id.boxcheck);


        final SaveContact data = (SaveContact) mContactData.get(i);
        tempHoler.title.setText(data.getName());
        tempHoler.check.setChecked(data.getCheckedBox());
        tempHoler.phone.setText(data.getPhone());




        /*// Set check box listener android
        tempHoler.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    data.setCheckedBox(true);
                  } else {
                    data.setCheckedBox(false);
                }
            }
        });*/

        view.setTag(data);
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mContactData.clear();
        if (charText.length() == 0) {
            mContactData.addAll(arraylist);
        } else {
            for (SaveContact cc : arraylist) {
                if (cc.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mContactData.add(cc);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView title, phone;
        CheckBox check;
    }
}


