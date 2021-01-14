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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.qrem.entidades.Reporte;
import com.coco.qrem.interfaces.QrEMAPI;

import java.util.List;

public class Bitacora extends AppCompatActivity {
    ListView lista;
    TextView nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitacora);
        lista=(ListView)findViewById(R.id.reportes);
        nombre=(TextView)findViewById(R.id.paramedico);

        SharedPreferences shared=getSharedPreferences("paramedicoSP",this.MODE_PRIVATE);
        String cedula=shared.getString("cedula"," "),contrasenia=shared.getString("contrasenia"," ");
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("http://" + this.getResources().getString(R.string.ip) + "/QrEM/servicios/");
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit r = builder.build();
        QrEMAPI qrEMAPI = r.create(QrEMAPI.class);
        Call<List<com.coco.qrem.entidades.Reporte>> call= qrEMAPI.tareBitacora(cedula, contrasenia);
        call.enqueue(new Callback<List<com.coco.qrem.entidades.Reporte>>() {
            @Override
            public void onResponse(Call<List<com.coco.qrem.entidades.Reporte>> call, Response<List<com.coco.qrem.entidades.Reporte>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Bitacora.this, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<com.coco.qrem.entidades.Reporte> reportes=response.body();
                Toast.makeText(Bitacora.this, reportes.get(reportes.size()-1).getPaciente(), Toast.LENGTH_SHORT).show();
                if(reportes.get(reportes.size()-1).getPaciente().equals("Consultado exitosamente")){
                    nombre.setText(reportes.get(reportes.size()-1).getFecha());
                    reportes.remove(reportes.get(reportes.size()-1));
                    Adaptador adapter=new Adaptador(Bitacora.this,R.layout.item_bitacora,reportes);
                    lista.setAdapter(adapter);

                }

            }

            @Override
            public void onFailure(Call<List<Reporte>> call, Throwable t) {
                Toast.makeText(Bitacora.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void regresar(View vw){
        this.finish();
    }

}
