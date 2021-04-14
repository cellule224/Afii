package house.thelittlemountaindev.afii;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ActivityPinCreator extends AppCompatActivity {

    private Button btnNext, btnCancel, btnDone;
    private String initialPin;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.fragment_pin_creator);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnCancel = findViewById(R.id.btn_Cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Take data from first ET and hide view
                EditText firstEt = findViewById(R.id.et_pin_first);
                if (firstEt.length() == 4 || firstEt.length() == 6) {
                    initialPin = firstEt.getText().toString();

                    RelativeLayout createPinLayout = findViewById(R.id.rl_create_pin);
                    createPinLayout.setVisibility(View.GONE);


                    RelativeLayout confirmPinLayout = findViewById(R.id.rl_confirm_pin);
                    confirmPinLayout.setVisibility(View.VISIBLE);
                }else {
                    firstEt.setError("Code de 4 ou 6!");
                }

            }
        });


        btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText secondEt = findViewById(R.id.et_pin_second);
                if (secondEt.getText().toString().equals(initialPin) ) {
                    updateUserPin();
                    Toast.makeText(ActivityPinCreator.this, "Pin updated!", Toast.LENGTH_SHORT).show();
                }else{
                    secondEt.setError("Les codes ne correspondent pas");
                }

            }
        });

    }

    private void updateUserPin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int i = Integer.parseInt(initialPin);

        databaseReference.child("users").child(user.getUid()).child("user_data").child("u_pin").setValue(i)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });
    }

    private String securePin(long pin){
        String texOfPin = String.valueOf(pin);
        MessageDigest digest1 = null;

        try {
            digest1 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        digest1.update(texOfPin.getBytes());
        byte[] digest = digest1.digest();

        return Base64.encodeToString(digest, Base64.DEFAULT);
    }


    //Add salt
    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}
