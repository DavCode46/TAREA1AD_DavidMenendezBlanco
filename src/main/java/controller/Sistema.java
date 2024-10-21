package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Sistema {

	private Map<String, String> credenciales = new HashMap<>();
	private String perfil;
	private String id; 
	
	public Sistema() {
		cargarCredenciales();
	}
	
	private void cargarCredenciales() {
		try(BufferedReader br = new BufferedReader(new FileReader("credenciales.txt"))) {
			String linea;
			while((linea = br.readLine()) != null) {
				String [] credencial = linea.split(" ");
				credenciales.put(credencial[0], credencial[1]);
				perfil = credencial[2];
				id = credencial[3]; // Preguntar Luis --> String o Long??
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean validarCredenciales(String usuario, String contrasenia) {
		return credenciales.containsKey(usuario) && credenciales.get(usuario).equals(contrasenia);
	}
	
	public String getPerfil() {
		return perfil;
	}
	
	public String getId() {
		return id;
	}
}
