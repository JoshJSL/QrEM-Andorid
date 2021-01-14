package com.coco.qrem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.coco.qrem.entidades.Mensjae;
import com.coco.qrem.interfaces.QrEMAPI;

public class Registro extends AppCompatActivity {
    private EditText nombre, apellidoPaterno, apellidoMaterno, edad, peso, alergias,seguro,contacto,cedula,contrasenia;
    private RadioButton noAlergias, noSeguro, paramedico;
    private Spinner tipoSangre;
    private ConstraintLayout layPaceinte,layParamedico;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        nombre = (EditText)findViewById(R.id.nombre);
        apellidoPaterno = (EditText)findViewById(R.id.apellido_paterno);
        apellidoMaterno = (EditText)findViewById(R.id.apellido_materno);
        edad = (EditText)findViewById(R.id.edad);
        peso = (EditText)findViewById(R.id.peso);
        alergias = (EditText)findViewById(R.id.alergias);
        seguro = (EditText)findViewById(R.id.seguro);
        contacto = (EditText)findViewById(R.id.contacto);
        cedula = (EditText)findViewById(R.id.cedula);
        contrasenia = (EditText)findViewById(R.id.contrasenia);
        noAlergias = (RadioButton)findViewById(R.id.no_alergias);
        noSeguro = (RadioButton)findViewById(R.id.no_seguro);
        paramedico = (RadioButton)findViewById(R.id.paramedico);
        layPaceinte = (ConstraintLayout)findViewById(R.id.paciente_layout);
        layParamedico = (ConstraintLayout)findViewById(R.id.paramedico_layout);

        tipoSangre = (Spinner)findViewById(R.id.tipo_sangre);
        String[]tiposDeSangre={"0+","A+","B+","AB+","0-","A-","B-","AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tiposDeSangre);
        tipoSangre.setAdapter(adapter);
    }

    public void registro(View vw){

        String nombre=this.nombre.getText().toString().trim(), apellidoPaterno=this.apellidoPaterno.getText().toString().trim(),apellidoMaterno=this.apellidoMaterno.getText().toString().trim(),
                edad=this.edad.getText().toString().trim(),peso=this.peso.getText().toString().trim(), alergias=noAlergias.isChecked()?"##":this.alergias.getText().toString().trim(),
                seguro=noSeguro.isChecked()?"##":this.seguro.getText().toString().trim(),contacto=this.contacto.getText().toString().trim(),tipoSangre=this.tipoSangre.getSelectedItem().toString(),
                cedula=this.cedula.getText().toString().trim(), contrasenia=this.contrasenia.getText().toString();
        if((!this.paramedico.isChecked()&&(nombre.equals("")|| apellidoMaterno.equals("") || apellidoPaterno.equals("") || edad.equals("") || peso.equals("") || alergias.equals("") || seguro.equals("") || contacto.equals("")||contrasenia.equals("")))
                ||(this.paramedico.isChecked()&&(nombre.equals("")|| apellidoMaterno.equals("") || apellidoPaterno.equals("") ||cedula.equals("")||contrasenia.equals("")))){
            Toast.makeText(this, "No puede haber campos vacios", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Valida.nombre(nombre)) {
            Toast.makeText(this, "Debe escribir un nombre válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Valida.nombre(apellidoPaterno)){
            Toast.makeText(this, "Debe escribir un apellido paterno válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Valida.nombre(apellidoMaterno)){
            Toast.makeText(this, "Debe escribir un apellido materno válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!this.paramedico.isChecked()){
            if(!Valida.numeros(edad)){
                Toast.makeText(this, "Debe escribir una edad válida", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Valida.numeros(peso)){
                Toast.makeText(this, "Debe escribir un peso válido", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!(Valida.numeros(contacto)&&contacto.length()==10)){
                Toast.makeText(this, "Debe escribir un número de contacto válido", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!noAlergias.isChecked()){
                if(!Valida.textoLargo(alergias)){
                    Toast.makeText(this, "Debe escribir texto válido en alergias", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if(!noSeguro.isChecked()){
                if(!Valida.nombre(seguro)){
                    Toast.makeText(this, "Debe escribir un nombre de seguro médico válido", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }else{
            if(!Valida.numeros(cedula)){
                Toast.makeText(this, "Debe escribir una cédula válida", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("http://"+this.getResources().getString(R.string.ip)+"/QrEM/servicios/");
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit r= builder.build();
        QrEMAPI qrEMAPI = r.create(QrEMAPI.class);
        vw.setEnabled(false);
        if(this.paramedico.isChecked()){
            Call<Mensjae> call = qrEMAPI.registroParamedico(nombre,apellidoPaterno,apellidoMaterno,contrasenia,cedula);
            call.enqueue(new Callback<Mensjae>() {
                @Override
                public void onResponse(Call<Mensjae> call, Response<Mensjae> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(Registro.this, "Error "+response.code(), Toast.LENGTH_SHORT).show();
                        vw.setEnabled(true);
                        return;
                    }
                    String respuesta=response.body().getMensaje();
                    Toast.makeText(Registro.this, respuesta, Toast.LENGTH_SHORT).show();
                    if(respuesta.equals("Registrado exitosamente")){
                        Registro.this.finish();
                    }

                }

                @Override
                public void onFailure(Call<Mensjae> call, Throwable t) {
                    Toast.makeText(Registro.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    vw.setEnabled(true);
                }
            });
        }else{
            Call<Mensjae> call= qrEMAPI.registroPaciente(nombre,apellidoPaterno,apellidoMaterno,contrasenia,Integer.parseInt(edad),Integer.parseInt(peso),tipoSangre,alergias,contacto,seguro);
            call.enqueue(new Callback<Mensjae>() {
                @Override
                public void onResponse(Call<Mensjae> call, Response<Mensjae> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(Registro.this, "Error "+response.code(), Toast.LENGTH_SHORT).show();
                        vw.setEnabled(true);
                        return;
                    }
                    String respuesta=response.body().getMensaje();
                    Toast.makeText(Registro.this, "Registrado exitosamene", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Registro.this, ResultadoQr.class);
                    i.putExtra("id",respuesta);
                    i.putExtra("nombre",nombre+"_"+apellidoPaterno+"_"+apellidoMaterno);
                    startActivity(i);
                    Registro.this.finish();

                }

                @Override
                public void onFailure(Call<Mensjae> call, Throwable t) {
                    vw.setEnabled(true);
                    Toast.makeText(Registro.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

    public void tipo(View vw){
        if(paramedico.isChecked()){
            layParamedico.setVisibility(View.VISIBLE);
            layPaceinte.setVisibility(View.GONE);
        }else{
            layParamedico.setVisibility(View.GONE);
            layPaceinte.setVisibility(View.VISIBLE);
        }
    }
    public void alergias(View vw){
        if(noAlergias.isChecked()){
            alergias.setVisibility(View.GONE);
        }else{
            alergias.setVisibility(View.VISIBLE);
        }
    }
    public void seguro(View vw){
        if(noSeguro.isChecked()){
            seguro.setVisibility(View.GONE);
        }else{
            seguro.setVisibility(View.VISIBLE);
        }
    }
}
