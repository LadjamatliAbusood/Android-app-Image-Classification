package com.example.lanchanika;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lanchanika.ml.Model;
import com.example.lanchanika.ml.Model2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {
    private Bitmap bitmap;
    private String uri;
    ImageView imageView;
    TextView txtresult, txtresult2, txtconfindence, txtpercent, txtmild,txtrotten,btnquestion;
    LinearLayout line1;
    FloatingActionButton btncapture;
    ProgressBar progressBar;
    int imageSize = 224;
    Button btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideNavigationBar();

        imageView = findViewById(R.id.imageView);
        txtresult = findViewById(R.id.result);
        txtresult2 = findViewById(R.id.result2);
        txtconfindence = findViewById(R.id.txtconfidence);
        txtpercent = findViewById(R.id.txtpercent);
        txtmild = findViewById(R.id.txtmild);
        txtrotten = findViewById(R.id.txtrotten);
        btncapture = findViewById(R.id.btncapture);
        btnquestion = findViewById(R.id.btnquestion);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        btnback = findViewById(R.id.btnback);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Bitmap image = bundle.getParcelable("BitmapImage");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

            if (image != null) {

                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }

        }

        btnquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });



        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,HomeMenuActivity.class);
                startActivity(i);
                finish();
            }
        });

        btncapture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //launch
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,1);
                }else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA},100);
                }
            }
        });


    }

    private void openDialog() {
        DialogBox dialogBox = new DialogBox();
        dialogBox.show(getSupportFragmentManager(),"dialog box");

    }

    public void classifyImage(Bitmap image){
        try {


            Model model = Model.newInstance(getApplicationContext());
            Model2 model2 = Model2.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4  * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0 ,image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++){
                for (int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++];//RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Runs model inference and gets result.
            Model2.Outputs outputs2 = model2.process(inputFeature0);
            TensorBuffer outputFeature02 = outputs2.getOutputFeature0AsTensorBuffer();

            float[] confidences2 = outputFeature02.getFloatArray();
            int maxPos2 = 0;
            float maxConfidence2 = 0;

            for(int i = 0; i < confidences2.length; i++){
                if (confidences2[i] > maxConfidence2){
                    maxConfidence2 = confidences2[i];
                    maxPos2 = i;
                    // System.out.println(maxConfidence2);


                }

            }

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;


            for(int i = 0; i < confidences.length; i++){
                if (confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;

                    //System.out.println(maxConfidence);

                }

            }


            String[] classes = {"Cucumber", "Garlic", "Ginger", "Leek", "Lettuce", "Moringa", "Onion",  "Squash", "Sweet Potato", "Water Spinach"};
            String[] classes2 = {"Cucumber Fresh", "Garlic Fresh", "Ginger Fresh", "Leek Fresh", "Lettuce Fresh", "Moringa Fresh", "Onion Fresh",
                    "Squash Fresh","Sweet Potato Fresh","Water Spinach Fresh","Cucumber Mild", "Garlic Mild", "Ginger Mild", "Leek Mild", "Lettuce Mild","Moringa Mild",
                    "Onion Mild","Squash Mild","Water Spinach Mild","Sweet Potato Mild","Cucumber Rotten","Garlic Rotten","Ginger Rotten","Leek Rotten",
                    "Lettuce Rotten","Moringa Rotten","Onion Rotten","Squash Rotten","Sweet Potato Rotten","Water Spinach Rotten"};



            if (maxConfidence < 0.8000000){
                txtresult.setText("Unidentified Vegetable");
                txtconfindence.setText("");
                txtmild.setText("");
                txtrotten.setText("");
                progressBar.setVisibility(View.INVISIBLE);

            }else{
                txtresult.setText(classes[maxPos]);
                txtresult.setPaintFlags(txtresult.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

            }
            txtresult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ladjamatliabusood.github.io/Lanchanika/"+txtresult.getText()+".html")));

                }
            });
           if (maxConfidence2 < 0.8000000){
               txtresult2.setText("Unidentified Vegetable");
               txtmild.setText("");
               txtpercent.setText("");
                txtrotten.setText("");
               progressBar.setVisibility(View.INVISIBLE);
            }else{
                txtresult2.setText(classes2[maxPos2]);
           }


            String s = "";

            for (int i = 0; i < classes.length; i++){


                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);

            }
            String vegeresult = txtresult.getText().toString().trim();
            if (vegeresult.equals("Unidentified Vegetable")){
                txtconfindence.setText("");
            }else{
                txtconfindence.setText(String.format("%s: %.1f%%\n","", maxConfidence * 100));
            }



            String vegeresult2 = txtresult2.getText().toString().trim();
            if (vegeresult.equals("Unidentified Vegetable")){
                txtresult2.setText("Unidentified Vegetable");
                txtpercent.setText("");
                txtmild.setText("");
                txtrotten.setText("");
                progressBar.setVisibility(View.INVISIBLE);

            }else{
                if (vegeresult2.equals("Cucumber Fresh")||vegeresult2.equals("Garlic Fresh")||vegeresult2.equals("Ginger Fresh")
                        ||vegeresult2.equals("Leek Fresh")||vegeresult2.equals("Lettuce Fresh")||vegeresult2.equals("Moringa Fresh")||vegeresult2.equals("Onion Fresh")
                        ||vegeresult2.equals("Squash Fresh")||vegeresult2.equals("Sweet Potato Fresh")||vegeresult2.equals("Water Spinach Fresh")){
                    txtresult2.setText(classes2[maxPos2]);
                    txtpercent.setText(String.format("%s %.1f%%\n","", maxConfidence2 * 100));


                    if (classes2[maxPos2].equals("Cucumber Fresh")){
                            txtmild.setText("5 - 7 days mild");
                            txtrotten.setText("After 8 days Rotten");
                    }
                    if (classes2[maxPos2].equals("Garlic Fresh")){
                            txtmild.setText("1 - 3 weeks mild");
                            txtrotten.setText("After 4 weeks Rotten");
                    }
                    if (classes2[maxPos2].equals("Ginger Fresh")){
                        txtmild.setText("5 days - 2 weeks mild");
                        txtrotten.setText("After 3 weeks Rotten");
                    }
                    if (classes2[maxPos2].equals("Leek Fresh")){
                        txtmild.setText("6 days - 2 weeks mild");
                        txtrotten.setText("After 3 weeks Rotten");
                    }
                    if (classes2[maxPos2].equals("Lettuce Fresh")){
                        txtmild.setText("7 - 10 days mild");
                        txtrotten.setText("After 11 days Rotten");
                    }
                    if (classes2[maxPos2].equals("Moringa Fresh")){
                        txtmild.setText("1 - 3 days mild");
                        txtrotten.setText("After 4 days Rotten");
                    }
                    if (classes2[maxPos2].equals("Onion Fresh")){
                        txtmild.setText("5 - 9 days mild");
                        txtrotten.setText("After 10 days Rotten");
                    }
                    if (classes2[maxPos2].equals("Squash Fresh")){
                        txtmild.setText("2 - 5 days mild");
                        txtrotten.setText("After 6 days Rotten");
                    }
                    if (classes2[maxPos2].equals("Sweet Potato Fresh")){
                        txtmild.setText("3 - 5 weeks mild");
                        txtrotten.setText("After 6 weeks Rotten");
                    }
                    if (classes2[maxPos2].equals("Water Spinach Fresh")){
                        txtmild.setText("2 - 3 days mild");
                        txtrotten.setText("After 4 days Rotten");
                    }


                    float cnvrttoint =  maxConfidence2 * 100;
                    progressBar.setProgress((int) cnvrttoint);
                    progressBar.setVisibility(View.VISIBLE);


                }else if(vegeresult2.equals("Cucumber Mild")||vegeresult2.equals("Garlic Mild")||vegeresult2.equals("Ginger Mild")
                        ||vegeresult2.equals("Leek Mild")||vegeresult2.equals("Lettuce Mild")||vegeresult2.equals("Moringa Mild")||vegeresult2.equals("Onion Mild")
                        ||vegeresult2.equals("Squash Mild")||vegeresult2.equals("Sweet Potato Mild")||vegeresult2.equals("Water Spinach Mild")){
                    txtresult2.setText(classes2[maxPos2]);
                    txtpercent.setText(String.format("%s %.1f%%\n","", maxConfidence2 * 100));


                    if (classes2[maxPos2].equals("Cucumber Mild")){
                        txtmild.setText("After 2 weeks Rotten");
                        txtrotten.setText("");
                    }
                    if (classes2[maxPos2].equals("Garlic Mild")){
                        txtmild.setText("After 2 weeks Rotten");
                        txtrotten.setText("");
                    }
                    if (classes2[maxPos2].equals("Ginger Mild")){
                        txtmild.setText("After 2 weeks Rotten");
                        txtrotten.setText("");
                    }
                    if (classes2[maxPos2].equals("Leek Mild")){
                        txtmild.setText("After 6 days Rotten");
                        txtrotten.setText("");
                    }
                    if (classes2[maxPos2].equals("Lettuce Mild")){
                        txtmild.setText("After 7 days Rotten");
                        txtrotten.setText("");
                    }
                    if (classes2[maxPos2].equals("Moringa Mild")){
                        txtmild.setText("After 1 day Rotten");
                        txtrotten.setText("");
                    }
                    if (classes2[maxPos2].equals("Onion Mild")){
                        txtmild.setText("After 8 days Rotten");
                        txtrotten.setText("");
                    }
                    if (classes2[maxPos2].equals("Squash Mild")){
                        txtmild.setText("After 5 Weeks Rotten");
                        txtrotten.setText("");
                    }
                    if (classes2[maxPos2].equals("Sweet Potato Mild")){
                        txtmild.setText("After 4 weeks Rotten");
                        txtrotten.setText("");
                    }
                    if (classes2[maxPos2].equals("Water Spinach Mild")){
                        txtmild.setText("After 2 days Rotten");
                        txtrotten.setText("");
                    }


                    float cnvrttoint =  maxConfidence2 * 100;
                    progressBar.setProgress((int) cnvrttoint);
                    progressBar.setVisibility(View.VISIBLE);


                }

                else if(vegeresult2.equals("Cucumber Rotten")||vegeresult2.equals("Garlic Rotten")||vegeresult2.equals("Ginger Rotten")||
                        vegeresult2.equals("Leek Rotten")||vegeresult2.equals("Lettuce Rotten")||vegeresult2.equals("Moringa Rotten")||
                        vegeresult2.equals("Onion Rotten")||vegeresult2.equals("Squash Rotten")||vegeresult2.equals("Sweet Potato Rotten")||
                        vegeresult2.equals("Water Spinach Rotten")){
                    txtresult2.setText(classes2[maxPos2]);
                    txtpercent.setText(String.format("%s %.1f%%\n","", maxConfidence2 * 100));
                    float cnvrttoint =  maxConfidence2 * 100;
                    progressBar.setProgress((int) cnvrttoint);
                    progressBar.setVisibility(View.VISIBLE);
                    txtmild.setText("Rotten");
                    txtrotten.setText("");


                }

            }







            // Releases model resources if no longer used.
            model.close();
            model2.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {



        if (requestCode == 1 && resultCode == RESULT_OK){

            Bitmap image = (Bitmap) data.getExtras().get("data");
            int  dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image,dimension,dimension);

            imageView.setImageBitmap(image);


            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);




        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void hideNavigationBar() {
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}