package house.thelittlemountaindev.afii.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import house.thelittlemountaindev.afii.R;
import house.thelittlemountaindev.afii.models.Transaction;
import house.thelittlemountaindev.afii.utils.LetterTileProvider;

public class TransactionHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<Transaction> transactionList;
    private ArrayList<Uri> picsUrls = new ArrayList<>();

    public TransactionHistoryAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @Override
    public int getCount() {
        return transactionList.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_transation_list, parent, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Transaction transaction = transactionList.get(position);

        ImageView pic = row.findViewById(R.id.iv_item_pic);
        TextView tvNameNum = row.findViewById(R.id.tv_item_sender);
        TextView tvDate = row.findViewById(R.id.tv_item_date);
        TextView tvAmount = row.findViewById(R.id.tv_item_amount);
        TextView tvNote = row.findViewById(R.id.tv_item_note);

        String senderPhone = transaction.getTr_sender_phone_num();
        String receiverPhone = transaction.getTr_receiver_phone_num();

    /*
    TEST CharCircles
    */
        final LetterTileProvider tileProvider = new LetterTileProvider(context);
        final Bitmap letterTile = tileProvider.getLetterTile(senderPhone.substring(5), senderPhone.substring(5), 78, 78, true);

        pic.setImageDrawable(new BitmapDrawable(context.getResources(), letterTile));

        //Check to see if the transaction is sent or receive and show apropriate UI
        if (senderPhone.equals(user.getPhoneNumber())){
            tvNameNum.setText(receiverPhone);
            tvAmount.setTextColor(Color.RED);
            tvAmount.setText("-"+ transaction.getTr_amount()+" USD");
        }else {
            tvNameNum.setText(senderPhone);
            tvAmount.setText(transaction.getTr_amount()+" USD");
        }
        tvDate.setText(getRelativeTime(transaction.getTr_time()));
        //TODO: tvNote.setText(transaction.getTr_note());
        //TODO: Glide for picture

        return row;
    }

    private String epochToDate(Long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy, HH:mm", Locale.FRANCE);
        return sdf.format(new Date(timestamp));
    }


    /*
     * Copyright 2012 Google Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    private static String getRelativeTime(long time) {

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;

        if (diff < MINUTE_MILLIS) {
            return "maintenant";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "depuis 1 min";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "depuis " + diff / MINUTE_MILLIS + " min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 heure";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " heures";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "hier ";
        } else if (diff < 6 * DAY_MILLIS) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", Locale.FRANCE);
            return sdf.format(new Date(time));
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YY", Locale.FRANCE);
            return sdf.format(new Date(time));
        }

    }
}

