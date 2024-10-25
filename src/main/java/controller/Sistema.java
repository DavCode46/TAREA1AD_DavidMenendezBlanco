package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import modelo.Parada;
import modelo.Perfil;
import main.Sesion;

public class Sistema {
	
	// Tareas a hacer
	//Variable estática sesión con el perfil del usuario

	private Map<String, Sesion> credenciales = new HashMap<>();
	
	
	private Map<String, Parada> paradas = new HashMap<>();
	
	

	public Sistema(String archivoCredenciales, String archivoParadas) {
		cargarCredenciales(archivoCredenciales);
		cargarParadas(archivoParadas);
	}
	
	public Perfil obtenerPerfil(String nombreUsuario) {
		Sesion s = credenciales.get(nombreUsuario);

		System.out.print(s.getPerfil());
		return (s != null) ? s.getPerfil() : null;
	}

	public Long getId(String nombreUsuario) {
		Sesion u = credenciales.get(nombreUsuario);

		return (u != null) ? u.getId() : null;
	}

	private void cargarCredenciales(String archivoCredenciales) {
		try (BufferedReader br = new BufferedReader(new FileReader(archivoCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] credencial = linea.split(" ");
				String usuario = credencial[0];
				String perfilString = credencial[2];
				Perfil perfil = Perfil.valueOf(perfilString.toUpperCase());
				Long id = Long.parseLong(credencial[3]);

				Sesion nuevoUsuario = new Sesion(usuario,  perfil, id);

				credenciales.put(usuario, nuevoUsuario);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public boolean validarCredenciales(String archivoCredenciales, String nombreUsuario, String contrasenia) {
	    try (BufferedReader br = new BufferedReader(new FileReader(archivoCredenciales))) {
	        String linea;
	        while ((linea = br.readLine()) != null) {
	            String[] credencial = linea.split(" ");
	            String usuario = credencial[0];
	            String contraseniaGuardada = credencial[1];
	            
	            if (usuario.equals(nombreUsuario)) {
	               
	                return contraseniaGuardada.equals(contrasenia);
	            }
	        }
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	    return false;
	}

	
	public boolean registrarCredenciales(String archivoCredenciales, String nombre, String contrasenia, String perfilString) {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCredenciales, true))) {
			if(credenciales.containsKey(nombre)) {
				JOptionPane.showMessageDialog(null, "El usuario ya existe");
				return false;
			}
			
			 Perfil perfil = Perfil.valueOf(perfilString.toUpperCase());
			Long id = (long) credenciales.size() + 1;
			String usuarioFormateado = String.format("%s %s %s %,d", nombre, contrasenia, perfil, id);
			bw.write(usuarioFormateado);
			bw.newLine();
			JOptionPane.showMessageDialog(null, "Usuario registrado con éxito\n"
					+ "Nombre: " + nombre
					+ "\nContraseña: " + contrasenia
					+ "\nPerfil: " + perfil
					+ "\nID: " + id);
			return true;
		} catch(FileNotFoundException ex) {
			System.out.println("Fichero no encontrado");
			return false;
		}catch(IOException ex) {
			ex.printStackTrace();
			return false;
		}
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
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoParadas,true))) {
			oos.writeObject(paradas);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
	
	@SuppressWarnings("unchecked")
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
	
	public boolean paradaExiste(String nombre) {
		return paradas.containsKey(nombre);
	}
	
}
