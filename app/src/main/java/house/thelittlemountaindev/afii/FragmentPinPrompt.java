package house.thelittlemountaindev.afii;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.nightonke.blurlockview.BlurLockView;
import com.nightonke.blurlockview.Password;

public class FragmentPinPrompt extends AppCompatActivity {

    private BlurLockView blurLockView;
    private String receiverId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiverId = getIntent().getStringExtra("receiverId");
        setContentView(R.layout.fragment_pin_prompt);

        ImageView imageView1 = (ImageView)findViewById(R.id.image_1);

        blurLockView = (BlurLockView)findViewById(R.id.blurlockview);

        // Set the view that need to be blurred
        blurLockView.setBlurredView(imageView1);

        // Set the password
        blurLockView.setCorrectPassword("1235");

        // blurLockView.setDownsampleFactor(0);
        blurLockView.setBlurRadius(2);
        blurLockView.setOverlayColor(R.color.colorPrimary);
        blurLockView.setLeftButton("Annuler");
        blurLockView.setRightButton("Effacer");
        blurLockView.setType(Password.NUMBER, true);

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
            }

            @Override
            public void incorrect(String inputPassword) {

            }

            @Override
            public void input(String inputPassword) {

            }


        });

    }

    private void returnResults() {
        Intent data = new Intent();
        data.putExtra("receiverId", receiverId);
        setResult(RESULT_OK, data);
        finish();
    }
}
