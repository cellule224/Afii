package house.thelittlemountaindev.afii;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import house.thelittlemountaindev.afii.adapters.TransactionHistoryAdapter;
import house.thelittlemountaindev.afii.models.Transaction;

public class FullHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private List<Transaction> transactionList;
    private TransactionHistoryAdapter adapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_history);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        listView = findViewById(R.id.history_listview);
        transactionList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(this, transactionList);
        listView.setAdapter(adapter);

        fetchRecentTransactions();
    }

    private void fetchRecentTransactions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("users").child(user.getUid()).child("user_trans_history").orderByChild("tr_time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        transactionList.add(snapshot.getValue(Transaction.class));
                        Collections.reverse(transactionList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
