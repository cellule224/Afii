package house.thelittlemountaindev.afii;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Charlie One on 9/27/2017.
 */

public class ConfirmPayActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    private TextView tv_amount_to_confirm, tv_receiver, tvCancelCounter;
    private Button btn_confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pay);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        String amount = getIntent().getStringExtra("amount");
        String receiver = getIntent().getStringExtra("receiver");


        tv_amount_to_confirm = (TextView) findViewById(R.id.tv_final_amount);
        tv_receiver = findViewById(R.id.tv_confirm_receiver);
        btn_confirm = (Button) findViewById(R.id.btn_confirm_exit);

        tv_amount_to_confirm.setText(amount + " USD");
        tv_receiver.setText("A: " + receiver);


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean isAmountAvailable() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child("users").child(user.getUid()).child("user_data").child("u_balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return true;
    }

    private void makeTransaction(){

    }


}
