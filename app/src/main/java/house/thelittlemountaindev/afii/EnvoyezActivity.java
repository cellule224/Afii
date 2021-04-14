package house.thelittlemountaindev.afii;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import house.thelittlemountaindev.afii.utils.NumberToWord;
import house.thelittlemountaindev.afii.utils.PinCheckActivity;
import house.thelittlemountaindev.afii.views.SendingSheetDialogFragment;

/**
 * Created by Charlie One on 10/6/2017.
 */

public class EnvoyezActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int SELECT_PHONE_NUMBER = 1;
    private static final int PIN_CONFIRMATION_PROMPT = 2;
    private EditText et_entered_amount, et_receiver_phone;
    private BottomSheetBehavior bottomSheetBehavior;
    public ImageButton btnPickContact, btnDoneEnteringNum;
    public String QRCodeFound;
    private RelativeLayout keypadActionBar;

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button switchFlashlightButton;
    private ViewfinderView viewfinderView;
    private TextView tvNameFoundQR;
    private String SEND_RECEIVE_TAG;

    private BeepManager beepManager;

    private DatabaseReference databaseReference;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(QRCodeFound)) {
                // Prevent duplicate scans
                return;
            }

            QRCodeFound = result.getText();
            tvNameFoundQR = findViewById(R.id.tv_name_found_in_qr);
            tvNameFoundQR.setText(QRCodeFound);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sending_platform);

        SEND_RECEIVE_TAG = getIntent().getStringExtra("SEND_RECEIVE_TAG");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        View bottomSheet = findViewById(R.id.keypad_sheet);
        Button btnSendPay = (Button) findViewById(R.id.btn_keypad_send);
        final TextView textualAmount = findViewById(R.id.tv_textual_amount);
        keypadActionBar = findViewById(R.id.rl_action_bar_replacemet);
        btnPickContact = findViewById(R.id.btn_pick_contact);
        btnDoneEnteringNum = findViewById(R.id.btn_done_entering_number);

        et_entered_amount = (EditText) findViewById(R.id.entered_amount);
        et_entered_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    NumberToWord amount = new NumberToWord();
                    String x = amount.convert(Integer.parseInt(s.toString()));
                    textualAmount.setText(x);
                }else if (s.length() == 0){
                    textualAmount.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_receiver_phone = (EditText) findViewById(R.id.et_recvr_phone);
        et_receiver_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    btnDoneEnteringNum.setVisibility(View.VISIBLE);
                    btnPickContact.setVisibility(View.GONE);
                }else{
                    btnDoneEnteringNum.setVisibility(View.GONE);
                    btnPickContact.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        viewfinderView = (ViewfinderView) findViewById(R.id.zxing_viewfinder_view);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();


        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.initializeFromIntent(getIntent());
        barcodeScannerView.decodeSingle(callback);
        beepManager = new BeepManager(this);

        int color = Color.parseColor("#250091EA");
        viewfinderView.setBackgroundColor(color);


        btnPickContact.setOnClickListener(this);
        btnSendPay.setOnClickListener(this);
        btnDoneEnteringNum.setOnClickListener(this);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        keypadActionBar.setVisibility(View.VISIBLE);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        keypadActionBar.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        checkBalance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHONE_NUMBER) {
            if (resultCode == RESULT_OK) {
                // Get the URI and query the content provider for the phone number
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(contactUri, projection,
                        null, null, null);

                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIndex);
                    // Do something with the phone number
                    //et_receiver_phone.setText(number);
                    tvNameFoundQR = findViewById(R.id.tv_name_found_in_qr);
                    tvNameFoundQR.setText(number);
                    QRCodeFound = number;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                cursor.close();
            }
        }else if (requestCode == PIN_CONFIRMATION_PROMPT) {
            if (resultCode == RESULT_OK) {
                String rslt = data.getStringExtra("receiverId");

                logRequestTransaction(rslt);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_show_keypad:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;

            case R.id.btn_keypad_send:
                getReceiverInfos(tvNameFoundQR.getText().toString());
                break;

            case R.id.btn_pick_contact:
                Intent i=new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, SELECT_PHONE_NUMBER);
                break;

            case R.id.btn_done_entering_number:
                tvNameFoundQR = findViewById(R.id.tv_name_found_in_qr);
                tvNameFoundQR.setText(et_receiver_phone.getText().toString());
                QRCodeFound = et_receiver_phone.getText().toString();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            default:
                break;
        }
    }

    private void checkBalance(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            databaseReference.child("users")
                    .child(user.getUid())
                    .child("user_data")
                    .child("u_balance").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String balance = dataSnapshot.getValue().toString();
                    Toast.makeText(EnvoyezActivity.this, "Balance: " + balance , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void getReceiverInfos(String phoneNumber) {
        //Show ModalSheetDialog
        SendingSheetDialogFragment mySheetDialog = new SendingSheetDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        mySheetDialog.show(fm, "sendingModalSheet");

/*

        String spaceLessPhoneNum = phoneNumber.replaceAll("\\s+","");
        final Query resultsQuery = databaseReference.child("users").orderByChild("u_phone_number").equalTo(spaceLessPhoneNum).limitToFirst(1);

        resultsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Check if transaction is a 'send' or 'receive'
                        if (SEND_RECEIVE_TAG.equals("RECEIVE_TAG")){
                            promptPinEntry(snapshot.getKey());
                            resultsQuery.removeEventListener(this);
                            return;
                        }else if (SEND_RECEIVE_TAG.equals("SEND_TAG")) {
                            logTransaction(snapshot.getKey());
                            resultsQuery.removeEventListener(this);
                            return;
                        }
                    }

                }else {
                    Toast.makeText(EnvoyezActivity.this, "Pas de compte", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/

    }

    private void promptPinEntry(String receiverId) {
        Intent intent = new Intent(this, PinCheckActivity.class);
        intent.putExtra("receiverId", receiverId);
        startActivityForResult(intent, PIN_CONFIRMATION_PROMPT);
    }

    public void logTransaction(String receiverId){
        DatabaseReference localDBReference = FirebaseDatabase.getInstance().getReference();

        String transactionId = localDBReference.child("Transactions").push().getKey();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String senderId = user.getUid();

        //The data sent in each transaction
        HashMap<String, Object> map = new HashMap<>();
        map.put("tr_id", transactionId);
        map.put("tr_sender_id", senderId); // Inverse this one
        map.put("tr_receiver_id", receiverId); //with this one, when it's a Request transaction
        map.put("tr_amount", et_entered_amount.getText().toString());
        map.put("tr_note", "Testing");
        map.put("tr_sender_phone_num", user.getPhoneNumber());
        map.put("tr_receiver_phone_num", QRCodeFound);
        map.put("tr_time", ServerValue.TIMESTAMP);
        map.put("tr_delayed", "false");

        //Add transaction to general ledger => Server will take care of the rest
        databaseReference.child("Transactions").child(transactionId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            Intent intent = new Intent(EnvoyezActivity.this, ConfirmPayActivity.class);
            intent.putExtra("amount", et_entered_amount.getText().toString());
            intent.putExtra("receiver", QRCodeFound);
            startActivity(intent);
            finish();
            }
        });
    }

    public void logRequestTransaction(String receiverId){
        DatabaseReference localDBReference = FirebaseDatabase.getInstance().getReference();

        String transactionId = localDBReference.child("Transactions").push().getKey();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String senderId = user.getUid();

        //The data sent in each transaction
        HashMap<String, Object> map = new HashMap<>();
        map.put("tr_id", transactionId);
        map.put("tr_sender_id", receiverId); // Inverse this one
        map.put("tr_receiver_id", senderId); //with this one, when it's a Request transaction
        map.put("tr_amount", et_entered_amount.getText().toString());
        map.put("tr_note", "Testing");
        map.put("tr_sender_phone_num", QRCodeFound); //Inverse this onw too
        map.put("tr_receiver_phone_num", user.getPhoneNumber()); // with this one
        map.put("tr_time", ServerValue.TIMESTAMP);
        map.put("tr_delayed", "false");

        //Add transaction to general ledger => Server will take care of the rest
        databaseReference.child("Transactions").child(transactionId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(EnvoyezActivity.this, ConfirmPayActivity.class);
                intent.putExtra("amount", et_entered_amount.getText().toString());
                intent.putExtra("receiver", QRCodeFound);
                startActivity(intent);
                finish();
            }
        });
    }

}
