package com.coco.qrem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.qrem.entidades.Mensjae;
import com.coco.qrem.entidades.Paciente;
import com.coco.qrem.interfaces.QrEMAPI;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.xml.validation.Validator;

public class MainActivity extends AppCompatActivity {
    private EditText cedula, contrasenia;
    private String id = "";
    private TextView estado;
    private Button escanea, inicio, registro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        cedula = (EditText) findViewById(R.id.cedula);
        contrasenia = findViewById(R.id.contrasenia);
        estado = (TextView) findViewById(R.id.estado_escaneo);
        escanea = findViewById(R.id.escanear);
        inicio = findViewById(R.id.iniciar);
        registro = findViewById(R.id.registrar);

        cedula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!id.equals("")) {
                    if (!cedula.getText().toString().trim().equals("")) {
                        cambiarBotonEscanea(false);
                    }
                }
            }
        });
        if(validaPermisos()){
            escanea.setEnabled(true);
        }else{
            escanea.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
            if (result.getContents() != null) {
                id = result.getContents();
                Toast.makeText(this, ("El código escaneado correctamente"), Toast.LENGTH_SHORT).show();
                cambiarBotonEscanea(true);
            } else {
                Toast.makeText(this, "Error al escanear el código", Toast.LENGTH_SHORT).show();
                cambiarBotonEscanea(false);
            }
    }

    public void registro(View vw) {
        Intent i = new Intent(this, Registro.class);

        startActivity(i);
    }

    public void login(View vw) {
        botones(false);
        String contrasenia = this.contrasenia.getText().toString().trim(), cedula = this.cedula.getText().toString().trim();
        if (id.equals("") && cedula.equals("")) {
            Toast.makeText(this, "Debe escribir una cédula o escanear un código Qr", Toast.LENGTH_SHORT).show();
            botones(true);
            return;
        }
        if (contrasenia.equals("")) {
            Toast.makeText(this, "Debe escribir su contraseña", Toast.LENGTH_SHORT).show();
            botones(true);
            return;
        }
        if(!Valida.numeros(cedula)){
            Toast.makeText(this, "Debe escribir una cédula válida", Toast.LENGTH_SHORT).show();
            botones(true);
            return;
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("http://" + this.getResources().getString(R.string.ip) + "/QrEM/servicios/");
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit r = builder.build();

        QrEMAPI qrEMAPI = r.create(QrEMAPI.class);

        if (id.equals("")) {
            Call<Mensjae> call = qrEMAPI.logInParamedico(contrasenia, cedula);
            call.enqueue(new Callback<Mensjae>() {
                @Override
                public void onResponse(Call<Mensjae> call, Response<Mensjae> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                        botones(true);
                        return;
                    }
                    String respuesta = response.body().getMensaje();
                    Toast.makeText(MainActivity.this, respuesta, Toast.LENGTH_SHORT).show();
                    botones(true);
                    if (respuesta.equals("Bienvendio")) {
                        inicioParamedico();
                    }

                }

                @Override
                public void onFailure(Call<Mensjae> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    botones(true);
                }
            });

        } else {
            Call<Paciente> call = qrEMAPI.getPaciente(Integer.parseInt(id), contrasenia);
            call.enqueue(new Callback<Paciente>() {
                @Override
                public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                        botones(true);
                        return;
                    } else {
                        Paciente paciente = response.body();
                        Toast.makeText(MainActivity.this, paciente.getMensaje(), Toast.LENGTH_SHORT).show();
                        if (paciente.getMensaje().equals("Consultado exitosamente")) {
                            inicioPaciente(paciente, contrasenia);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Paciente> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    botones(true);
                }
            });
        }
        botones(true);

    }

    public void escanear(View vw) {
        IntentIntegrator i = new IntentIntegrator(this);
        i.setPrompt("Escanear Código Qr");
        i.setOrientationLocked(false);
        i.setCaptureActivity(CaptureActivtyPortrait.class);
        i.initiateScan();

    }

    private void cambiarBotonEscanea(boolean escaneado) {
        if (escaneado) {
            escanea.setBackgroundColor(this.getResources().getColor(R.color.verde));
            estado.setText("Escaneado");
            estado.setTextColor(this.getResources().getColor(R.color.verde));
        } else {
            id = "";
            escanea.setBackgroundColor(this.getResources().getColor(R.color.rojo));
            estado.setText("No escaneado" + id);
            estado.setTextColor(this.getResources().getColor(R.color.rojo));

        }
    }

    private void inicioParamedico() {
        Intent i = new Intent(this, InicioParamedico.class);
        i.putExtra("cedula", cedula.getText().toString());
        i.putExtra("contrasenia", contrasenia.getText().toString());
        startActivity(i);
        this.finish();
    }

    private void inicioPaciente(Paciente paciente, String contrasenia) {
        Intent i = new Intent(this, ModificaPaciente.class);
        i.putExtra("nombre", paciente.getNombre());
        i.putExtra("apellidoPaterno", paciente.getApellidoPaterno());
        i.putExtra("apellidoMaterno", paciente.getApellidoMaterno());
        i.putExtra("alergias", paciente.getAlergias());
        i.putExtra("contacto", paciente.getContacto());
        i.putExtra("edad", paciente.getEdad() + "");
        i.putExtra("peso", paciente.getPeso() + "");
        i.putExtra("seguro", paciente.getSeguro());
        i.putExtra("tipoSangre", paciente.getTipoSangre());
        i.putExtra("id", id);
        i.putExtra("contrasenia", contrasenia);
        startActivity(i);
        this.finish();
    }

    private boolean validaPermisos() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) ||
                (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);
        }

        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==3 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED&& grantResults[2]==PackageManager.PERMISSION_GRANTED){
                escanea.setEnabled(true);
            }else{
                Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},100);
            }
        });
        dialogo.show();
    }
    public void botones(boolean acyivar){

            escanea.setEnabled(acyivar);
            inicio.setEnabled(acyivar);
            registro.setEnabled(acyivar);
    }
}