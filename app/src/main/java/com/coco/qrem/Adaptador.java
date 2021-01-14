package com.coco.qrem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coco.qrem.R;
import com.coco.qrem.entidades.Reporte;

import java.util.ArrayList;
import java.util.List;

public class Adaptador extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Reporte> reportes;

    public Adaptador(Context context, int layout, List<Reporte> reportes) {
        this.context = context;
        this.layout = layout;
        this.reportes = reportes;
    }

    @Override
    public int getCount() {
        return this.reportes.size();
    }

    @Override
    public Object getItem(int position) {
        return this.reportes.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // Copiamos la vista
        View v = convertView;

        //Inflamos la vista con nuestro propio layout
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);

        v = layoutInflater.inflate(R.layout.item_bitacora, null);
        // Valor actual según la posición

        Reporte reporte = reportes.get(position);

        // Referenciamos el elemento a modificar y lo rellenamos
        TextView textView = (TextView) v.findViewById(R.id.paciente);
        textView.setText(reporte.getPaciente());
        textView = (TextView)v.findViewById(R.id.fecha);
        textView.setText(reporte.getFecha());
        textView = (TextView)v.findViewById(R.id.descripcion);
        textView.setText(reporte.getDescripcion());
        //Devolvemos la vista inflada
        return v;
    }
}
