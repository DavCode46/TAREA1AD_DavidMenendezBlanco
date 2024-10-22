package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import modelo.Usuario;

public class Sistema {

    private Map<String, Usuario> credenciales = new HashMap<>();
    
    public Sistema(String archivo) {
        cargarCredenciales(archivo);
    }

    private void cargarCredenciales(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] credencial = linea.split(" ");
                String usuario = credencial[0];
                String contrasenia = credencial[1];
                String perfil = credencial[2];
                String id = credencial[3];
                
                Usuario nuevoUsuario = new Usuario(usuario, contrasenia, perfil, id);

                credenciales.put(usuario, nuevoUsuario);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean validarCredenciales(String nombreUsuario, String contrasenia) {
        Usuario u = credenciales.get(nombreUsuario);
        
        return (u != null) && u.getContrasenia().equals(contrasenia);
    }

    public String obtenerPerfil(String nombreUsuario) {
        Usuario u = credenciales.get(nombreUsuario);
        
        return (u != null) ? u.getPerfil() : null;
    }

    public String getId(String nombreUsuario) {
        Usuario u = credenciales.get(nombreUsuario);
        
        return (u != null) ? u.getId() : null;
    }
}
