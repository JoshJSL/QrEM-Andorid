package com.coco.qrem;

public class Valida {
    public static boolean numeros(String cedula){
        for(int i=0; i<cedula.length();i++){
            if(!Character.isDigit(cedula.charAt(i))){
                return false;
            }
        }
        return true;
    }
    public static boolean nombre(String nombre){
        for(int i=0; i<nombre.length();i++){
            if(!Character.isLetter(nombre.charAt(i))){
                return false;
            }
        }
        return true;
    }
    public static boolean textoLargo(String cadena){
        for(int i=0; i<cadena.length();i++){
            if(!(Character.isLetter(cadena.charAt(i)) ||Character.isDigit(cadena.charAt(i)) || cadena.charAt(i)==' '|| cadena.charAt(i)==','|| cadena.charAt(i)=='-'|| cadena.charAt(i)=='.'|| cadena.charAt(i)==' '||
                    cadena.charAt(i)=='\n'|| cadena.charAt(i)==' ')){
                return false;
            }
        }
        return true;
    }
}
