package com.coco.qrem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.coco.qrem.entidades.Mensjae;
import com.coco.qrem.entidades.Paciente;
import com.coco.qrem.entidades.Reporte;
import com.coco.qrem.interfaces.QrEMAPI;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class InicioParamedico extends AppCompatActivity {
    String id,cedula, contrasenia;
    Button escanea, bitacora, cerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_paramedico);
        cedula=getIntent().getExtras().getString("cedula");
        contrasenia=getIntent().getExtras().getString("contrasenia");
        SharedPreferences shared = getSharedPreferences("paramedicoSP", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("cedula", cedula);
        editor.putString("contrasenia", contrasenia);
        editor.commit();

        escanea = findViewById(R.id.escanear);
        bitacora = findViewById(R.id.bitacora);
        cerrar = findViewById(R.id.cerrar);
    }

    public void consultaQr(View vw) {
        botones(false);
        IntentIntegrator i = new IntentIntegrator(this);
        i.setPrompt("Escanear Código Qr");
        i.setOrientationLocked(false);
        i.setCaptureActivity(CaptureActivtyPortrait.class);
        i.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
            if (result.getContents() != null) {
                id = result.getContents();
                Toast.makeText(this, ("El código escaneado correctamente"), Toast.LENGTH_SHORT).show();
                consultarPaciente();
            } else {
                Toast.makeText(this, "Error al escanear el código", Toast.LENGTH_SHORT).show();
            }
    }

    public void consultarPaciente() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("http://" + this.getResources().getString(R.string.ip) + "/QrEM/servicios/");
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit r = builder.build();
        QrEMAPI qrEMAPI = r.create(QrEMAPI.class);
        Call<Paciente> call = qrEMAPI.consultaPaciente(Integer.parseInt(id));
        call.enqueue(new Callback<Paciente>() {
            @Override
            public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(InicioParamedico.this, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                    botones(true);
                    return;
                }
                Paciente paciente = response.body();
                botones(true);
                Toast.makeText(InicioParamedico.this, paciente.getMensaje(), Toast.LENGTH_SHORT).show();
                if (paciente.getMensaje().equals("Consultado exitosamente")) {
                    Intent i = new Intent(InicioParamedico.this, InforamcionPaciente.class);
                    i.putExtra("nombre", paciente.getNombre());
                    i.putExtra("apellidoPaterno", paciente.getApellidoPaterno());
                    i.putExtra("apellidoMaterno", paciente.getApellidoMaterno());
                    i.putExtra("alergias", paciente.getAlergias());
                    i.putExtra("contacto", paciente.getContacto());
                    i.putExtra("edad", paciente.getEdad() + "");
                    i.putExtra("peso", paciente.getPeso() + "");
                    i.putExtra("seguro", paciente.getSeguro());
                    i.putExtra("tipoSangre", paciente.getTipoSangre());
                    startActivity(i);
                }

            }

            @Override
            public void onFailure(Call<Paciente> call, Throwable t) {
                Toast.makeText(InicioParamedico.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                botones(true);
            }
        });

    }

    public void botones(boolean activar) {
        cerrar.setEnabled(activar);
        bitacora.setEnabled(activar);
        escanea.setEnabled(activar);
    }

    public void bitacora(View vw) {
            Intent i = new Intent(this, Bitacora.class);
            startActivity(i);
    }

    public void cerrar(View vw){
        Intent i =  new Intent(this, MainActivity.class);
        deleteSharedPreferences("paramedicoSP");
        startActivity(i);
        this.finish();
    }
}