package house.thelittlemountaindev.afii;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import house.thelittlemountaindev.afii.utils.ImageSaver;

/**
 * Created by Charlie One on 2/8/2018.
 */

public class FragmentProfileSetupThree extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_setup_three, container, false);

        ImageView iv = rootView.findViewById(R.id.iv_id);
        Bitmap bitmap = new ImageSaver(getContext()).setFileName("national_id").setDirectoryName("images").load();
        iv.setImageBitmap(bitmap);

        Button btnStart = rootView.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
        return rootView;

    }

}
