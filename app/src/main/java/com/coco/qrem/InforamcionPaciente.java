package com.coco.qrem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class InforamcionPaciente extends AppCompatActivity {
    String paciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inforamcion_paciente);
        String aux;
        paciente=this.getIntent().getExtras().getString("nombre")+" "+this.getIntent().getExtras().getString("apellidoPaterno")+" "+this.getIntent().getExtras().getString("apellidoMaterno");

        ((TextView)findViewById(R.id.paciente)).setText(paciente);
        ((TextView)findViewById(R.id.edad)).setText(this.getIntent().getExtras().getString("edad"));
        ((TextView)findViewById(R.id.peso)).setText(this.getIntent().getExtras().getString("peso"));
        ((TextView)findViewById(R.id.tipo_sangre)).setText(this.getIntent().getExtras().getString("tipoSangre"));
        aux=this.getIntent().getExtras().getString("alergias");
        if(aux.equals("##")){
            (findViewById(R.id.alergias)).setVisibility(View.GONE);
            (findViewById(R.id.alergias_letrero)).setVisibility(View.GONE);
        }else{
            ((TextView)findViewById(R.id.alergias)).setText(aux);
        }
        aux=this.getIntent().getExtras().getString("seguro");
        if(aux.equals("##")){
            (findViewById(R.id.seguro)).setVisibility(View.GONE);
            (findViewById(R.id.seguro_letrero)).setVisibility(View.GONE);
        }else{
            ((TextView)findViewById(R.id.seguro)).setText(aux);
        }
        ((TextView)findViewById(R.id.contacto)).setText(this.getIntent().getExtras().getString("contacto"));
    }

    public void continuar(View vw){
        Intent i= new Intent(this, Reporte.class);
        i.putExtra("paciente", paciente);
        startActivity(i);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "No puede ir atrás, debe hacer el reporte del incidente, presione Continuar", Toast.LENGTH_SHORT).show();
    }
}
