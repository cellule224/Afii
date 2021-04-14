package house.thelittlemountaindev.afii.utils;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.blurlockview.BlurLockView;
import com.nightonke.blurlockview.Password;

import house.thelittlemountaindev.afii.R;

public class PinCheckActivity extends AppCompatActivity {


    private BlurLockView blurLockView;
    private String receiverId;
    private String temp_data_to_remove = "+224";
    private ImageView imageView1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiverId = getIntent().getStringExtra("receiverId");
        setContentView(R.layout.fragment_pin_prompt);

       imageView1 = (ImageView)findViewById(R.id.image_1);
       verifyCode();
    }

    private void returnResults() {
            Intent data = new Intent();
            data.putExtra("receiverId", receiverId);
            setResult(RESULT_OK, data);
            finish();
    }

    private void verifyCode() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(receiverId).child("user_data").child("u_pin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    IDK(dataSnapshot.getValue().toString());
                    databaseReference.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PinCheckActivity.this, ""+databaseError, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void IDK(String p) {
        blurLockView = (BlurLockView)findViewById(R.id.blurlockview);

        // Set the view that need to be blurred
        blurLockView.setBlurredView(imageView1);

        blurLockView.setOverlayColor(R.color.colorPrimary);
        blurLockView.setLeftButton("Annuler");
        blurLockView.setRightButton("Effacer");
        blurLockView.setType(Password.NUMBER, true);
        blurLockView.setCorrectPassword(p);

        blurLockView.setOnLeftButtonClickListener(new BlurLockView.OnLeftButtonClickListener() {
            @Override
            public void onClick() {
                onBackPressed();
            }
        });

        blurLockView.setOnPasswordInputListener(new BlurLockView.OnPasswordInputListener() {
            @Override
            public void correct(String inputPassword) {
                returnResults();
                Toast.makeText(PinCheckActivity.this, "Accepté", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void incorrect(String inputPassword) {

            }

            @Override
            public void input(String inputPassword) {

            }


        });

    }
}
