package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import main.Sesion;
import modelo.Parada;
import modelo.Perfil;

public class Sistema {

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

				Sesion nuevoUsuario = new Sesion(usuario, perfil, id);

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

	public boolean registrarCredenciales(String archivoCredenciales, String nombre, String contrasenia,
			String perfilString) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCredenciales, true))) {
			if (credenciales.containsKey(nombre)) {
				JOptionPane.showMessageDialog(null, "El usuario ya existe");
				return false;
			}

			Perfil perfil = Perfil.valueOf(perfilString.toUpperCase());
			Long id = (long) credenciales.size() + 1;
			String usuarioFormateado = String.format("%s %s %s %,d", nombre, contrasenia, perfil, id);
			bw.write(usuarioFormateado);
			bw.newLine();
			JOptionPane.showMessageDialog(null, "Usuario registrado con éxito\n" + "Nombre: " + nombre
					+ "\nContraseña: " + contrasenia + "\nPerfil: " + perfil + "\nID: " + id);
			return true;
		} catch (FileNotFoundException ex) {
			System.out.println("Fichero no encontrado");
			return false;
		} catch (IOException ex) {
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
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoParadas, true))) {
			oos.writeObject(paradas);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void mostrarParadas(String archivoParadas, boolean isPeregrino) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoParadas))) {
			Map<String, Parada> paradasLeidas = (Map<String, Parada>) ois.readObject();

			StringBuilder sb = new StringBuilder("Paradas registradas: \n");

			for (Parada parada : paradasLeidas.values()) {
				if (isPeregrino) {
					sb.append("\nNombre: ").append(parada.getNombre());
					sb.append("\nRegión: ").append(parada.getRegion());
				} else {

					sb.append("\nID: ").append(parada.getId());
					sb.append("\nNombre: ").append(parada.getNombre());
					sb.append("\nRegión: ").append(parada.getRegion());
					sb.append("\nResponsable: ").append(parada.getResponsable());
				}
			}

			JOptionPane.showMessageDialog(null, sb);
		} catch (FileNotFoundException ex) {
			System.out.println("No se ha encontrado el archivo de paradas.");
		} catch (EOFException ex) {
			System.out.println("El archivo de paradas está vacío.");
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public boolean registrarParada(String archivoParadas, String nombre, char region, String responsable) {
		if (paradas.containsKey(nombre)) {
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
	
	public String mostrarPaises() {
	    try {
	        // Crear un parser
	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document documento = builder.parse(new File("src/main/resources/paises.xml"));
	        documento.getDocumentElement().normalize();
	        System.out.println("Elemento raíz: " + documento.getDocumentElement().getNodeName());

	        // Crear una lista con todos los nodos 'pais'
	        NodeList paises = documento.getElementsByTagName("pais");

	        StringBuilder sb = new StringBuilder("Países disponibles: \n");
	       
	        for (int i = 0; i < paises.getLength(); i++) {
	            Node pais = paises.item(i);
	            if (pais.getNodeType() == Node.ELEMENT_NODE) {
	                Element elemento = (Element) pais;
	                String id = getNodo("id", elemento);
	                String nombrePais = getNodo("nombre", elemento);
	                // Agregar cada país a una fila de 4 columnas
	                sb.append(String.format("ID: %-5s País: %-20s", id, nombrePais));

	                // Añadir un salto de línea después de cada 4 países
	                if ((i + 1) % 4 == 0) {
	                    sb.append("\n");
	                } else {
	                    sb.append("\t"); // Separar columnas con un tabulador
	                }
	            }
	        }
	      
	        return sb.toString();
	    } catch (ParserConfigurationException | SAXException | IOException ex) {
	        System.err.println("Error: " + ex.getMessage());
	    } 
	    return "No se han encontrado países";
	}

	private static String getNodo(String etiqueta, Element elem) {
	    NodeList nodo = elem.getElementsByTagName(etiqueta).item(0).getChildNodes();
	    Node valorNodo = nodo.item(0);
	    return valorNodo.getNodeValue();
	}
}

