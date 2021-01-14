package com.coco.qrem.entidades;

public class Paciente {

    private int edad;
    private int peso;
    private String tipoSangre;
    private String alergias;
    private String seguro;
    private String contacto;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String mensaje;


    public String getNombre() {
        return nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public int getEdad() {
        return edad;
    }

    public int getPeso() {
        return peso;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public String getAlergias() {
        return alergias;
    }

    public String getSeguro() {
        return seguro;
    }

    public String getContacto() {
        return contacto;
    }
}
