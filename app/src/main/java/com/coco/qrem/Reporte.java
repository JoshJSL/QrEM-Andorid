package com.coco.qrem;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.qrem.entidades.Mensjae;
import com.coco.qrem.entidades.Paciente;
import com.coco.qrem.interfaces.QrEMAPI;

import org.w3c.dom.Text;

import java.time.LocalDate;


public class Reporte extends AppCompatActivity {
    String paciente,id;
    TextView descripcion;
    Button boton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        boton=findViewById(R.id.aceptar);
        descripcion=findViewById(R.id.descripcion);
        paciente=getIntent().getExtras().getString("paciente");
        id=getIntent().getExtras().getString("id");

        ((TextView)findViewById(R.id.paciente)).setText(paciente);
    }

    public void hacerReporte(View vw){
        boton.setEnabled(false);
        String descripcion=this.descripcion.getText().toString().trim();
        if(!Valida.textoLargo(descripcion)){
            Toast.makeText(this, "Debe escribir una descripción válida", Toast.LENGTH_SHORT).show();
            boton.setEnabled(true);
            return;
        }
        if(!descripcion.equals("")){

            SharedPreferences shared=getSharedPreferences("paramedicoSP",this.MODE_PRIVATE);
            String cedula=shared.getString("cedula"," "),contrassenia=shared.getString("contrasenia"," ");
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl("http://" + this.getResources().getString(R.string.ip) + "/QrEM/servicios/");
            builder.addConverterFactory(GsonConverterFactory.create());
            Retrofit r = builder.build();
            QrEMAPI qrEMAPI = r.create(QrEMAPI.class);
            Call<Mensjae> call = qrEMAPI.hacerReporte(id,cedula,descripcion,contrassenia);

            call.enqueue(new Callback<Mensjae>() {
                @Override
                public void onResponse(Call<Mensjae> call, Response<Mensjae> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(Reporte.this, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                        boton.setEnabled(true);
                        return;
                    }
                    String mensaje=response.body().getMensaje();
                    Toast.makeText(Reporte.this, mensaje, Toast.LENGTH_SHORT).show();
                    if(mensaje.equals("Guardado correctamente")){
                        Reporte.this.finish();
                    }

                    boton.setEnabled(true);
                }

                @Override
                public void onFailure(Call<Mensjae> call, Throwable t) {
                    Toast.makeText(Reporte.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    boton.setEnabled(true);
                }
            });



        }else{
            Toast.makeText(this, "Debe escribir una descripción del incidente", Toast.LENGTH_SHORT).show();
            boton.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "No puede ir atrás, debe hacer el reporte del incidente, presione Aceptar", Toast.LENGTH_SHORT).show();
    }
}
