package house.thelittlemountaindev.afii;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.EXTRA_OUTPUT;

/**
 * Created by Charlie One on 2/8/2018.
 */

public class FragmentProfileSetupOne extends Fragment {

    private static final int SELECT_FILE = 1;
    private static final int REQUEST_CAMERA = 2;
    private ImageView ivPicture;
    private Uri outputFileUri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_setup_one, container, false);

        ivPicture = rootView.findViewById(R.id.iv_add_profile_pic);
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPicture();
            }
        });

        return rootView;

    }


    private void addPicture() {
        final CharSequence[] items = {"Prennant une photo", "Selectionnant du phone", "Annuler"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Ajouter en:");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Prennant une photo")) {
                    sayCheese(); //Open Camera and return image path

                } else if (items[i].equals("Selectionnant du phone")) {
                    Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPictureIntent, SELECT_FILE);
                } else if (items[i].equals("Annulez")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void sayCheese() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TAXIMO_PROFILE_PIC" + timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String mCurrentPhotoPath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(mCurrentPhotoPath);
        outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null){
            cameraIntent.putExtra(EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                            ivPicture.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                break;

            case SELECT_FILE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ivPicture.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }
}
