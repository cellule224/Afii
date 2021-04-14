package house.thelittlemountaindev.afii;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import house.thelittlemountaindev.afii.adapters.TransactionHistoryAdapter;
import house.thelittlemountaindev.afii.models.Transaction;
import house.thelittlemountaindev.afii.utils.AuthVerification;
import house.thelittlemountaindev.afii.views.SwipeAnimationListener;
import house.thelittlemountaindev.afii.views.SwipeToSendButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private ImageView sendMoneyImg, profileImage;
    private TextView tvReceiveMoney;
    private Button btnSend, btnReceive;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference ;

    private List<Transaction> transactionList;
    private TransactionHistoryAdapter adapter;
    private ListView listView;
    private TextView tvOpenTransacs;

    private ImageView ivNotificationIcon;

    private SwipeToSendButton swipeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x_main_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnSend = findViewById(R.id.btn_main_send);
        btnReceive = findViewById(R.id.btn_main_receive);

        profileImage = (ImageView) findViewById(R.id.iv_profile);
        tvOpenTransacs = findViewById(R.id.tv_open_transactions);

        btnSend.setOnClickListener(this);
        btnReceive.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        tvOpenTransacs.setOnClickListener(this);

        listView = findViewById(R.id.listView_recent);

        transactionList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(this, transactionList);
        listView.setAdapter(adapter);

        fetchRecentTransactions();

        registerDeviceId();

       CardView tempTontine = findViewById(R.id.cv_tontine);
       CardView tempTopup = findViewById(R.id.cv_recharge);
       CardView tempTickets = findViewById(R.id.cv_tickets);
       CardView tempMore = findViewById(R.id.cv_more_services);

       tempTontine.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(MainActivity.this, TontineMainActivity.class));
           }
       });

       tempTopup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(MainActivity.this, TopUpActivity.class));
           }
       });

       tempTickets.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(MainActivity.this, TicketsActivity.class));
           }
       });

       tempMore.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (isOtherAppInstalled("house.thelittlemountaindev.ponwel")){
                   Intent intent = new Intent(Intent.ACTION_MAIN);
                   intent.setComponent(new ComponentName(
                    "house.thelittlemountaindev.ponwel","house.thelittlemountaindev.ponwel.MainActivity"));
                   startActivity(intent);
               }else{
                   Toast.makeText(MainActivity.this, "Affi non-installé", Toast.LENGTH_SHORT).show();
               }
           }
       });
        swipeButton = findViewById(R.id.swipe_to_send_btn);
        swipeButton.setOnSwipeAnimationListener(new SwipeAnimationListener() {
            @Override
            public void onSwiped(boolean isRight) {
                if (isRight) {
                    requestPermission("SEND_TAG");
                }else {
                    requestPermission("RECEIVE_TAG");
                }
            }
        });
    }

    private void requestPermission(String tag){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        }else {
            Intent intent = new Intent(this, EnvoyezActivity.class);
            intent.putExtra("SEND_RECEIVE_TAG", tag);
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(MainActivity.this, EnvoyezActivity.class));
                }
                break;

            default: break;
        }
    }

    @Override
    public void onClick(View view) {
        AuthVerification auth = new AuthVerification();
        switch (view.getId()) {
            case R.id.iv_profile:
                startActivity(new Intent(this, ProfileActivity.class));
/*
                if (!auth.logged()) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                }else{
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                }
*/                 break;

            case R.id.btn_main_send:
                requestPermission("SEND_TAG");

/*
                if (!auth.logged()) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                }else{
                    startActivity(new Intent(MainActivity.this, SendMoneyActivity.class));

                }
*/
                break;

            case R.id.tv_open_transactions:
                startActivity(new Intent(this, FullHistoryActivity.class));
                break;

            case R.id.btn_main_receive:
                requestPermission("RECEIVE_TAG");
                break;

        }
    }

    private void fetchRecentTransactions() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("users").child(uid).child("user_trans_history").orderByChild("tr_time").limitToLast(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    transactionList.clear();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        transactionList.add(snapshot.getValue(Transaction.class));
                        Collections.reverse(transactionList);
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    listView.setVisibility(View.GONE);
                    tvOpenTransacs.setVisibility(View.GONE);
                    //TextView emptyTv = findViewById(R.id.tv_no_transactions_error);
                    //emptyTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void registerDeviceId() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Error", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        String uid = firebaseAuth.getCurrentUser().getUid();


                        databaseReference.child("users").child(uid).child("user_tokens").child(token).setValue(true);

                    }
                });
    }

    private boolean isOtherAppInstalled(String targetPackage){
        PackageManager pm=getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
}
