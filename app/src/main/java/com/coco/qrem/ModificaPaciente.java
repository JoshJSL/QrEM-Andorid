package com.coco.qrem;

import androidx.appcompat.app.AppCompatActivity;
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
import com.coco.qrem.entidades.Paciente;
import com.coco.qrem.interfaces.QrEMAPI;

public class ModificaPaciente extends AppCompatActivity {

    private EditText nombre, apellidoPaterno, apellidoMaterno, edad, peso, alergias,seguro,contacto;
    private RadioButton noAlergias, noSeguro;
    private Spinner tipoSangre;
    private String id, contrasenia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_paciente);
        nombre = (EditText)findViewById(R.id.nombre);
        apellidoPaterno = (EditText)findViewById(R.id.apellido_paterno);
        apellidoMaterno = (EditText)findViewById(R.id.apellido_materno);
        edad = (EditText)findViewById(R.id.edad);
        peso = (EditText)findViewById(R.id.peso);
        alergias = (EditText)findViewById(R.id.alergias);
        seguro = (EditText)findViewById(R.id.seguro);
        contacto = (EditText)findViewById(R.id.contacto);
        noAlergias = (RadioButton)findViewById(R.id.no_alergias);
        noSeguro = (RadioButton)findViewById(R.id.no_seguro);

        tipoSangre = (Spinner)findViewById(R.id.tipo_sangre);
        String[]tiposDeSangre={"0+","A+","B+","AB+","0-","A-","B-","AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tiposDeSangre);
        tipoSangre.setAdapter(adapter);


        Bundle extras=this.getIntent().getExtras();
        id=extras.getString("id");
        contrasenia=extras.getString("contrasenia");
        nombre.setText(extras.getString("nombre"));
        apellidoPaterno.setText(extras.getString("apellidoPaterno"));
        apellidoMaterno.setText(extras.getString("apellidoMaterno"));
        edad.setText(extras.getString("edad"));
        peso.setText(extras.getString("peso"));
        if(extras.getString("alergias").equals("##")){
            alergias.setText("");
            noAlergias.setChecked(true);
        }else{
            alergias.setText(extras.getString("alergias"));
            noAlergias.setChecked(false);
        }
        alergias(noAlergias);
        if(extras.getString("seguro").equals("##")){
            seguro.setText("");
            noSeguro.setChecked(true);
        }else{
            seguro.setText(extras.getString("seguro"));
            noSeguro.setChecked(false);
        }
        seguro(noSeguro);
        contacto.setText(extras.getString("contacto"));

        tipoSangre.setSelection(adapter.getPosition(extras.getString("tipoSangre")));
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

    public void modifica(View vw){
        String nombre=this.nombre.getText().toString().trim(), apellidoPaterno=this.apellidoPaterno.getText().toString().trim(),apellidoMaterno=this.apellidoMaterno.getText().toString().trim(),
            edad=this.edad.getText().toString().trim(),peso=this.peso.getText().toString().trim(), alergias=noAlergias.isChecked()?"##":this.alergias.getText().toString().trim(),
                seguro=noSeguro.isChecked()?"##":this.seguro.getText().toString().trim(),contacto=this.contacto.getText().toString().trim(),tipoSangre=this.tipoSangre.getSelectedItem().toString();

        if(nombre.equals("")|| apellidoMaterno.equals("") || apellidoPaterno.equals("") || edad.equals("") || peso.equals("") || alergias.equals("") || seguro.equals("") || contacto.equals("")    ){
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

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("http://"+this.getResources().getString(R.string.ip)+"/QrEM/servicios/");
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit r= builder.build();

        QrEMAPI qrEMAPI = r.create(QrEMAPI.class);

        Call<Mensjae> call=qrEMAPI.modificaPaciente(Integer.parseInt(id),contrasenia,nombre,apellidoPaterno,apellidoMaterno,Integer.parseInt(edad),Integer.parseInt(peso),tipoSangre,alergias,contacto,seguro);
        call.enqueue(new Callback<Mensjae>() {
            @Override
            public void onResponse(Call<Mensjae> call, Response<Mensjae> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(ModificaPaciente.this, "Error "+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                String respuesta=response.body().getMensaje();
                Toast.makeText(ModificaPaciente.this, respuesta, Toast.LENGTH_SHORT).show();
                if(respuesta.equals("Modificado exitosamente")){
                    Intent i = new Intent(ModificaPaciente.this, MainActivity.class);
                    startActivity(i);
                    ModificaPaciente.this.finish();
                }
            }

            @Override
            public void onFailure(Call<Mensjae> call, Throwable t) {
                Toast.makeText(ModificaPaciente.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
}
