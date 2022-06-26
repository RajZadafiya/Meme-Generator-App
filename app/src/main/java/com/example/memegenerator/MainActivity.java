package com.example.memegenerator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int  MY_PERMISSION_REQUEST = 1;
    public static final int REQUEST_LOAD_IMAGE = 2;
    Button load,save,share,go;
    TextView textView1,textView2;
    EditText editText1,editText2;
    ImageView imageView;
    String currentImage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
            ,MY_PERMISSION_REQUEST);
        }else
        {
            Toast.makeText(MainActivity.this, "Problems!!!", Toast.LENGTH_SHORT).show();
        }

        imageView = findViewById(R.id.imageview);

        textView1 = findViewById(R.id.textview1);
        textView2 = findViewById(R.id.textview2);

        editText1 = findViewById(R.id.edittext1);
        editText2 = findViewById(R.id.edittext2);

        go = findViewById(R.id.go);

        load = findViewById(R.id.load);
        save = findViewById(R.id.save);
        share = findViewById(R.id.share);

        save.setEnabled(false);
        share.setEnabled(false);

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,REQUEST_LOAD_IMAGE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View content = findViewById(R.id.lay);
                Bitmap bitmap = getscreenshot(content);
                currentImage = "rzmeme" + System.currentTimeMillis() + ".png";
                store(bitmap,currentImage);
                share.setEnabled(true);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareimage(currentImage);
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView1.setText(editText1.getText().toString());
                textView2.setText(editText2.getText().toString());

                editText1.setText("");
                editText2.setText("");
            }
        });
    }

    public static Bitmap getscreenshot(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bm,String filename){
        String dirpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MEMEGENERATOR";
        File dir = new File(dirpath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dirpath,filename);
        try {
            FileOutputStream fo = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG,100,fo);
            fo.flush();
            fo.close();
            Toast.makeText(MainActivity.this, "Saved!!!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Not Saved!!!", Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }
    }

    private void shareimage(String filename){
        String dirpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MEMEGENERATOR";
        Uri uri = Uri.fromFile(new File(dirpath,filename));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT,"");
        intent.putExtra(Intent.EXTRA_TEXT,"");
        intent.putExtra(Intent.EXTRA_STREAM,uri);

        try{
            startActivity(Intent.createChooser(intent,"Share Via"));
        }catch (ActivityNotFoundException e){
            Toast.makeText(MainActivity.this, "No Sharing Application Found!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOAD_IMAGE && requestCode == RESULT_OK && null != data) {
            Uri selectedimage = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
           /* String[] filepathcolumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedimage, filepathcolumn, null, null, null);
            cursor.moveToFirst();
            int columnindex = cursor.getColumnIndex(filepathcolumn[0]);
            String picturepath = cursor.getString(columnindex);
            cursor.close();
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturepath));*/
            save.setEnabled(true);
            share.setEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                        // DO NOTHING
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Permission Not Granted!!!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}