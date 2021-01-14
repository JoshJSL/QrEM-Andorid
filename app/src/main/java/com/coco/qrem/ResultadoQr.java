package com.coco.qrem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;

public class ResultadoQr extends AppCompatActivity {
    ImageView codigo;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_qr);
        codigo=(ImageView)findViewById(R.id.codigo);
        String id=this.getIntent().getExtras().getString("id");
        bitmap = QRCode.from(id).withSize(5000,5000).bitmap();
        codigo.setImageBitmap(bitmap);
    }

    public void hecho(View vw){
        this.finish();
    }

    public void guardar(View vw){
        Toast.makeText(this, "Guardando por favor espere", Toast.LENGTH_SHORT).show();
        vw.setEnabled(false);

        String nombrePaciente =this.getIntent().getExtras().getString("nombre");

        try {
            String ExternalStorageDirectory = Environment.getExternalStorageDirectory() + File.separator;
            String rutacarpeta = "CodigosQr/";
            String nombre = "Qr_"+ nombrePaciente +".png";
            File directorioImagenes = new File(ExternalStorageDirectory + rutacarpeta);
            if (!directorioImagenes.exists())
                directorioImagenes.mkdirs();
            Bitmap bitmapout = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
            bitmapout.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(ExternalStorageDirectory + rutacarpeta + nombre));

            File filefinal = new File(ExternalStorageDirectory + rutacarpeta + nombre);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Qr_"+nombrePaciente);
            values.put(MediaStore.Images.Media.DESCRIPTION, "Código Qr para app QrEM");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, filefinal.toString().toLowerCase(Locale .getDefault()).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, filefinal.getName().toLowerCase(Locale.getDefault()));
            values.put("_data", filefinal.getAbsolutePath());
            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Toast.makeText(this, "Se guardó correctamente ahora debe poder verlo desde su galeria", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            Toast.makeText(this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        vw.setEnabled(true);


    }
}
