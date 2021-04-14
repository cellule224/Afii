package house.thelittlemountaindev.afii;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.EXTRA_OUTPUT;

/**
 * Created by Charlie One on 2/8/2018.
 */

public class FragmentProfileSetupTwo extends Fragment {

    private static final int SELECT_FILE = 3;
    private static final int REQUEST_CAMERA = 4;
    private SurfaceView preview=null;
    private SurfaceHolder previewHolder=null;
    private Camera camera=null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;

    private Button btnShutter;
    private ImageView ivTakenImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_setup_two, container, false);

        preview = (SurfaceView)rootView.findViewById(R.id.surfaceViewCamera);

        previewHolder=preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        btnShutter = rootView.findViewById(R.id.btn_shutter);
        ivTakenImg = rootView.findViewById(R.id.iv_taken_picture);


        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnShutter.setEnabled(false);
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        btnShutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPicture();
              /*  if (inPreview) {
                    camera.takePicture(null, null, new PhotoHandler(getActivity()) {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            super.onPictureTaken(data, camera);

                            if (data != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, 0);

                                new ImageSaver(getContext()).setFileName("national_id").setDirectoryName("images").save(bitmap);
                                ivTakenImg.setVisibility(View.VISIBLE);
                                camera.stopPreview();
                                preview.setVisibility(View.GONE);
                                ivTakenImg.setImageBitmap(bitmap);
                            }
                        }
                    });
                }*/
            }
        });
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();

        camera=Camera.open();
        startPreview();
    }

    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera=null;
        inPreview=false;

        super.onPause();
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                }
                else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private void initPreview(int width, int height) {
        if (camera!=null && previewHolder.getSurface()!=null) {
            try {
                camera.setPreviewDisplay(previewHolder);
            }
            catch (Throwable t) {
                Log.e("PreviewDemo-surCallback",
                        "Exception in setPreviewDisplay()", t);
                Toast
                        .makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters=camera.getParameters();
                Camera.Size size=getBestPreviewSize(width, height,
                        parameters);

                if (size!=null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured=true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera!=null) {
            camera.startPreview();
            inPreview=true;
        }
    }

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder,
                                   int format, int width,
                                   int height) {
            //camera.setDisplayOrientation(Surface.ROTATION_270); // preventcamera from going sideways
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };


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
        String imageFileName = "TAXIMO_NID" + timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String mCurrentPhotoPath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(mCurrentPhotoPath);
        Uri outputFileUri = Uri.fromFile(file);
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
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ivTakenImg.setImageBitmap(bitmap);
                    btnShutter.setText("Suivant");
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
                        ivTakenImg.setImageBitmap(bitmap);
                        btnShutter.setText("Suivant");
                        btnShutter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                    }
                }
                break;

        }
    }
}
