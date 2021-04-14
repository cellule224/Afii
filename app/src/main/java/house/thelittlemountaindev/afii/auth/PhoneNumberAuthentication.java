package house.thelittlemountaindev.afii.auth;

/**
 * Created by Charlie One on 10/1/2017.
 */
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Arrays;
import java.util.HashMap;

import house.thelittlemountaindev.afii.MainActivity;
import house.thelittlemountaindev.afii.R;

public class PhoneNumberAuthentication extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            startActivity(new Intent(PhoneNumberAuthentication.this, MainActivity.class));
            finish();
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                                    ))
                            .setTheme(R.style.LoginTheme)
                            .build(),
                    RC_SIGN_IN);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                if (auth.getCurrentUser() != null){
                    String id = auth.getCurrentUser().getUid();
                    String phone = auth.getCurrentUser().getPhoneNumber();
                    addUserToDb(id, phone, "Anonyme");
                    return;
                }else{
                    startActivity(new Intent(PhoneNumberAuthentication.this, ProfileSetupActivity.class));
                    finish();
                    return;
                }
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login","Login canceled by User");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e("Login","No Internet Connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login","Unknown Error");
                    return;
                }
            }
            Log.e("Login","Unknown sign in response");
        }
    }

    private void addUserToDb(String uid, String phone, String name) {
        HashMap<String, Object> userValues = new HashMap<>();
        userValues.put("u_id", uid);
        userValues.put("u_phone_number", phone);
        userValues.put("u_first_last_name", name);
        userValues.put("u_pic_url", "");
        userValues.put("u_verified", "false");
        userValues.put("u_pin", 1958);
        userValues.put("u_balance", 0.00);
        userValues.put("u_date_created", ServerValue.TIMESTAMP);
        userValues.put("u_updated", ServerValue.TIMESTAMP);

        databaseReference.child("users").child(uid).child("user_data").setValue(userValues).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(PhoneNumberAuthentication.this, ProfileSetupActivity.class));
                finish();
            }
        });

    }
}