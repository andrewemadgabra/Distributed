package com.example.andrew.newsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;


public class AddNewsActivity extends AppCompatActivity {

    FloatingActionButton fab;
    ImageView imageView;
    EditText editText;
    Spinner spinner;
    Button button;
    Bitmap bitmap;
    private Uri fileUri, cameraImageUri;
    String image;

    String newsData[] = new String[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        fab = findViewById(R.id.fab_add_image);
        imageView = findViewById(R.id.news_image);
        editText = findViewById(R.id.et_news_bod);
        spinner = findViewById(R.id.news_category_spinner);
        button = findViewById(R.id.btn_add_news);

        final String[] arraySpinner = new String[]{
                "Sports", "Politics", "Mass Media", "Arts", "General"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);

        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Toast.makeText(AddNewsActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                newsData[0] = arraySpinner[position];
                newsData[1] = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newsBody = editText.getText().toString().trim();

                newsData[3] = newsBody;

                Date currentTime = Calendar.getInstance().getTime();

                newsData[2] = currentTime.toString();
                newsData[4] = image;

                if (newsData[0] != null && newsData[1] != null && newsData[2] != null && newsData[4] != null) {

                    PostNewsTask postNewsTask = new PostNewsTask();
                    postNewsTask.execute();
                } else {
                    Toast.makeText(AddNewsActivity.this, "Required fields empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {

            fileUri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            image = imageToString(bitmap);
        }

        if (requestCode == 200 && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");

            imageView.setImageBitmap(bitmap);
            image = imageToString(bitmap);
        }
    }


    private String imageToString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] image = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(image, Base64.DEFAULT);
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    private void showPictureDialog() {

        final android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this);

        pictureDialog.setTitle("Update Your Photo");

        String[] pictureDialogItems = {
                "Select photo from Gallery",
                "Capture photo from Camera",
                "Cancel"};
        pictureDialog.setIcon(R.drawable.news_icon);

        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;

                            case 1:
                                takePhotoFromCamera();
                                break;

                            case 2:
                                dialog.dismiss();
                        }
                    }
                });

        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/jpeg");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);


        startActivityForResult(galleryIntent, 100);
    }

    private void takePhotoFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        cameraImageUri = cameraIntent.getData();

        startActivityForResult(cameraIntent, 200);
    }

    public class PostNewsTask extends AsyncTask<Void, Void, Void> {

        private String message;
//        private boolean postedSuccess;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Socket socket = new Socket("10.0.2.2", 300);

                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                os.writeObject(newsData);
                os.flush();

                message = (String) ois.readObject();
//                os.close();
//                ois.close();
//
//                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}