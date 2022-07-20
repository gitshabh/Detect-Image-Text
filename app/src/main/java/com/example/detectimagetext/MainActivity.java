package com.example.detectimagetext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button,clickPhoto;
    TextView textView;
    Bitmap image;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        clickPhoto = findViewById(R.id.clickPhoto);

        imageView.setImageResource(R.drawable.imagetext);
        button.setOnClickListener(view -> detect());
        textView.setMovementMethod(new ScrollingMovementMethod());

        clickPhoto.setOnClickListener(view -> {
            askCameraPermission();
        });

    }

    public void detect() {
        //define TextRecognizer
        TextRecognizer recognizer = new TextRecognizer.Builder(MainActivity.this).build();

        // get bitmap from imageview
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        //get frame from bitmap
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        //get data from frame
        SparseArray<TextBlock> sparseArray = recognizer.detect(frame);

        //set data on textview
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i < sparseArray.size(); i++) {
            TextBlock tx = sparseArray.get(i);
            String str = tx.getValue();

            stringBuilder.append(str);
            stringBuilder.append("\n");
        }
        textView.setText(stringBuilder);
    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},CAMERA_PERM_CODE);
        }else{
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else{
                Toast.makeText(this, "Camera Permission required to use the camera ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_REQUEST_CODE){
            image = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(image);


        }
    }

}