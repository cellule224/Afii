package house.thelittlemountaindev.afii;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
//import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Created by Charlie One on 9/24/2017.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ImageView ivQRCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton ibBackBtn = findViewById(R.id.btn_back_press_from_profile);
        ivQRCode = findViewById(R.id.iv_profile_qr);
        TextView tvPersonal = findViewById(R.id.tv_profile_personal);
        TextView tvInvite = findViewById(R.id.tv_profile_invite);
        TextView tvNotifications = findViewById(R.id.tv_profile_notifications);
        TextView tvHelp = findViewById(R.id.tv_profile_help);


        ibBackBtn.setOnClickListener(this);
        tvPersonal.setOnClickListener(this);
        tvInvite.setOnClickListener(this);
        tvNotifications.setOnClickListener(this);
        tvHelp.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            genrateQR(user.getPhoneNumber());
        }else{
            genrateQR("Anonymous user");
        }
    }

    private void genrateQR(String phoneNum) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(phoneNum, BarcodeFormat.QR_CODE,360,360);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQRCode.setImageBitmap(bitmap);
        }
        catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_press_from_profile:
                onBackPressed();
                break;

            case R.id.tv_profile_personal:
                startActivity(new Intent(ProfileActivity.this, PersonalInfoActivity.class));
                break;
            case R.id.tv_profile_invite:

                break;
            case R.id.tv_profile_notifications:

                break;
            case R.id.tv_profile_help:

                break;

            default:
                break;
        }
    }
}
