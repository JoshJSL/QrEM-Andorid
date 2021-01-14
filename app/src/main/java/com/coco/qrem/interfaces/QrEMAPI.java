package com.coco.qrem.interfaces;

import com.coco.qrem.entidades.Reporte;
import com.coco.qrem.entidades.Mensjae;
import com.coco.qrem.entidades.Paciente;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface QrEMAPI {
    @GET("paramedico/login")
    Call<Mensjae> logInParamedico(@Query("contrasenia") String contrasenia, @Query("cedula") String cedula);

    @GET("paramedico/registro")
    Call<Mensjae> registroParamedico(@Query("nombre") String nombre, @Query("apellidoPaterno") String apellidoPaterno, @Query("apellidoMaterno") String apellidoMaterno,
                                     @Query("contrasenia") String contrasenia, @Query("cedula") String cedula);

    @GET("paramedico/consultaPaciente")
    Call<Paciente> consultaPaciente(@Query("id")int id);

    @GET("paramedico/reporte")
    Call<Mensjae> hacerReporte(@Query("paciente") String paciente, @Query("cedula") String cedula,
                               @Query("descripcion") String descripcion, @Query("contrasenia") String contrasenia);

    @GET("paramedico/bitacora")
    Call<List<Reporte>> tareBitacora(@Query("cedula") String cedula, @Query("contrasenia") String contrasenia);

    @GET("paciente/consulta")
    Call<Paciente> getPaciente(@Query("id") int id,@Query("contrasenia") String contrasenia);

    @GET("paciente/modifica")
    Call<Mensjae> modificaPaciente(@Query("id") int id,@Query("contrasenia") String contrasenia,@Query("nombre") String nombre,@Query("apellidoPaterno") String apellidoPaterno,@Query("apellidoMaterno") String apellidoMaterno
            ,@Query("edad") int edad,@Query("peso") int peso,@Query("tipoSangre") String tipoSangre, @Query("alergias") String alergias,@Query("contacto") String contacto,@Query("seguro") String seguro);

    @GET("paciente/registro")
    Call<Mensjae> registroPaciente(@Query("nombre") String nombre,@Query("apellidoPaterno") String apellidoPaterno,@Query("apellidoMaterno") String apellidoMaterno,
                                   @Query("contrasenia") String contrasenia,@Query("edad") int edad,@Query("peso") int peso,@Query("tipoSangre") String tipoSangre,
                                   @Query("alergias") String alergias,@Query("contacto") String contacto,@Query("seguro") String seguro);

}
