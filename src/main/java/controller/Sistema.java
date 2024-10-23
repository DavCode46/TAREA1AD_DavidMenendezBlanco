package controller;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import modelo.Parada;
import modelo.Usuario;

public class Sistema {

	private Map<String, Usuario> credenciales = new HashMap<>();
	private Map<String, Parada> paradas = new HashMap<>();

	public Sistema(String archivoCredenciales, String archivoParadas) {
		cargarCredenciales(archivoCredenciales);
		cargarParadas(archivoParadas);
	}

	private void cargarCredenciales(String archivoCredenciales) {
		try (BufferedReader br = new BufferedReader(new FileReader(archivoCredenciales))) {
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

	@SuppressWarnings("unchecked")
	private void cargarParadas(String archivoParadas) {
	    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoParadas))) {
	        paradas = (Map<String, Parada>) ois.readObject();  
	    } catch (FileNotFoundException ex) {
	        System.out.println("No se ha encontrado el archivo de paradas. Creando uno nuevo...");
	    
	        paradas = new HashMap<>();
	    } catch (EOFException ex) {
	        System.out.println("El archivo de paradas está vacío. Iniciando con un mapa vacío.");
	       
	        paradas = new HashMap<>();
	    } catch (IOException | ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
	}


	private void guardarParadas(String archivoParadas) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoParadas))) {
			oos.writeObject(paradas);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
	
	public void mostrarParadas(String archivoParadas) {
	    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoParadas))) {
	        Map<String, Parada> paradasLeidas = (Map<String, Parada>) ois.readObject();
	        
	        
	        for (Parada parada : paradasLeidas.values()) {
	            JOptionPane.showMessageDialog(null, "Paradas registradas: \n"
	            					+"ID: " + parada.getId() +
	                               "\nNombre: " + parada.getNombre() +
	                               "\nRegión: " + parada.getRegion() +
	                               "\nResponsable: " + parada.getResponsable());
	        }
	    } catch (FileNotFoundException ex) {
	        System.out.println("No se ha encontrado el archivo de paradas.");
	    } catch (EOFException ex) {
	        System.out.println("El archivo de paradas está vacío.");
	    } catch (IOException | ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
	}


	public boolean registrarParada(String archivoParadas, String nombre, char region, String responsable) {
		if(paradas.containsKey(nombre)) {
			JOptionPane.showMessageDialog(null, "La parada ya existe");
			return false;
		}
		Long id = (long) paradas.size() + 1;
		Parada nuevaParada = new Parada(id, nombre, region, responsable);
		paradas.put(nombre, nuevaParada);
		guardarParadas(archivoParadas);
		
		JOptionPane.showMessageDialog(null, "Parada registrada con éxito");
		return true;
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
