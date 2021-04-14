package house.thelittlemountaindev.afii;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Charlie One on 9/22/2017.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText phoneField, codeField;
    private Button btnSendCode, btnVerifyCode;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth firebaseAuth;
    private String verificationID;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        phoneField = (EditText) findViewById(R.id.editText_UserPhone);
        codeField = (EditText) findViewById(R.id.editTextCode);

        btnVerifyCode = (Button) findViewById(R.id.btnVerifyCode);
        btnSendCode = (Button) findViewById(R.id.btnSendCode);

        btnSendCode.setOnClickListener(this);
        btnVerifyCode.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                Log.d("tag", "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationID = verificationId;
                mResendToken = forceResendingToken;

                //hide textView and Button
                phoneField.setVisibility(View.GONE);
                btnSendCode.setVisibility(View.GONE);

                Toast.makeText(SignInActivity.this, "Code envoyé. Verifiez vos messages", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void sendVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void verifyCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();
                            addUserToDb(user.getUid(), user.getPhoneNumber(), "Nom vide");

                        }else{
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                codeField.setError("Code invalide");
                            }
                        }
                    }
                });
    }

    private void addUserToDb(String uid, String phone, String name) {
        HashMap<String, Object> userValues = new HashMap<>();
        userValues.put("u_id", uid);
        userValues.put("u_phone", phone);
        userValues.put("u_name", name);
        userValues.put("u_pic", " ");

        databaseReference.child("users").child(uid).setValue(userValues).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    private boolean validatePhoneNumber() {
        String phone = phoneField.getText().toString();
        if (TextUtils.isEmpty(phone)){
            phoneField.setError("Numéro invalide");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSendCode:
                if (!validatePhoneNumber()){
                    return;
                }

                sendVerification(phoneField.getText().toString());
                break;

            case R.id.btnVerifyCode:
                String code = codeField.getText().toString();
                if (TextUtils.isEmpty(code)){
                    codeField.setError("Le code ici!");
                }

                verifyCode(verificationID, code);
                break;

            default:
                break;
        }
    }


}
