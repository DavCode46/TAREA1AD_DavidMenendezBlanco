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
import modelo.Carnet;
import modelo.Parada;
import modelo.Peregrino;
import modelo.Perfil;

public class Sistema {

	private String archivoCredenciales;
	private String archivoParadas;

	private Map<String, Sesion> credenciales = new HashMap<>();

	private Map<String, Parada> paradas = new HashMap<>();

	public Sistema(String archivoCredenciales, String archivoParadas) {
		this.archivoCredenciales = archivoCredenciales;
		this.archivoParadas = archivoParadas;
		cargarCredenciales(archivoCredenciales);
		cargarParadas(archivoParadas);
	}

	public Perfil obtenerPerfil(String nombreUsuario) {
		Sesion s = credenciales.get(nombreUsuario);

		System.out.print(s.getPerfil());
		return (s != null) ? s.getPerfil() : null;
	}

	public Parada obtenerParada(String nombreParada) {
		return paradas.get(nombreParada);
	}

	public Long getId(String nombreUsuario) {
		Sesion u = credenciales.get(nombreUsuario);

		return (u != null) ? u.getId() : null;
	}

	public Long obtenerSiguienteId(String archivoCredenciales, boolean esPeregrino) {
		long maxId = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(archivoCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] credencial = linea.split(" ");

				if (esPeregrino && credencial[2].startsWith("peregrino")) {
					Long id = Long.parseLong(credencial[3]);
					if (id > maxId) {
						maxId = id;
					}
				} else if (!esPeregrino && credencial[2].startsWith("parada")) {
					Long id = Long.parseLong(credencial[3]);
					if (id > maxId) {
						maxId = id;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return maxId + 1;
	}

	private void cargarCredenciales(String archivoCredenciales) {
		try (BufferedReader br = new BufferedReader(new FileReader(archivoCredenciales))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] credencial = linea.split(" ");
				String usuario = credencial[0];
				String perfilString = credencial[2];
				Perfil perfil = Perfil.valueOf(perfilString.toLowerCase());
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
			String perfilString, boolean esPeregrino) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCredenciales, true))) {
			if (credenciales.containsKey(nombre)) {
				JOptionPane.showMessageDialog(null, "El usuario ya existe");
				return false;
			}

			Perfil perfil = Perfil.valueOf(perfilString.toLowerCase());
			Long id;
			if (esPeregrino) {
				id = obtenerSiguienteId(archivoCredenciales, true);
				Sesion nuevoUsuario = new Sesion(nombre, perfil, id);
				credenciales.put(nombre, nuevoUsuario);
			} else {
				id = obtenerSiguienteId(archivoCredenciales, false);
				Sesion nuevoUsuario = new Sesion(nombre, perfil, id);
				credenciales.put(nombre, nuevoUsuario);
			}
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

	public Peregrino registrarPeregrino() {

		return registrarPeregrino(archivoParadas, archivoCredenciales);
	}

	private Peregrino registrarPeregrino(String archivoParadas, String archivoCredenciales) {
		Peregrino nuevoPeregrino = null;
		JOptionPane.showMessageDialog(null, "Formulario de registro de nuevo peregrino");

		// Obtener datos del usuario
		String nombre = obtenerEntrada("Ingrese su nombre", "Nombre", false);
		if (nombre == null)
			return null;

		String contrasenia = obtenerEntrada("Ingrese su contraseña", "Contraseña", false);
		if (contrasenia == null)
			return null;

		String nacionalidad = "";
		do {
			nacionalidad = obtenerEntrada(mostrarPaises(), "Nacionalidad", false);
			if(!validarPais(nacionalidad)){
				JOptionPane.showMessageDialog(null, "El país ingresado no es válido.");
			}
		} while(!validarPais(nacionalidad));
		
		String parada = "";
		do {
			parada = obtenerEntrada(mostrarParadas(archivoParadas, true), "Parada", false);
			if(!paradaExiste(parada)){
				JOptionPane.showMessageDialog(null, "La parada seleccionada no es válida.");
			}
		} while(!paradaExiste(parada));

		// Confirmar los datos
		nuevoPeregrino = confirmarDatos(nombre, contrasenia, nacionalidad, parada, archivoCredenciales);

		if (nuevoPeregrino == null) {
			// Si la confirmación falla, pedir nuevos datos

			nuevoPeregrino = obtenerDatosModificados(archivoCredenciales, nombre, contrasenia, nacionalidad, parada);

		}

		return nuevoPeregrino;
	}

	private Peregrino confirmarDatos(String nombre, String contrasenia, String nacionalidad, String parada,
			String archivoCredenciales) {
		String mensajeFormateado = String.format("Confirma los datos \n" + "Nombre: %s \n" + "Contraseña: %s\n"
				+ "Nacionalidad: %s\n" + "Parada actual: %s\n", nombre, contrasenia, nacionalidad, parada);

		int confirmacion = JOptionPane.showConfirmDialog(null, mensajeFormateado, "Confirma",
				JOptionPane.YES_NO_OPTION);
		if (confirmacion == JOptionPane.YES_OPTION) {
			// Datos correctos --> Continuar
			if (validarCredenciales(archivoCredenciales, nombre, contrasenia)) {
				JOptionPane.showMessageDialog(null, "El usuario ya existe");
				return null;
			} else {
				Long id = obtenerSiguienteId(archivoCredenciales, true);
				Parada paradaObj = obtenerParada(parada);
				Peregrino nuevoPeregrino = new Peregrino(id, nombre, nacionalidad, new Carnet(id, paradaObj));
				nuevoPeregrino.getParadas().add(paradaObj);
				registrarCredenciales(archivoCredenciales, nombre, contrasenia,
						Perfil.peregrino.toString().toLowerCase(), true);
				return nuevoPeregrino;
			}
		}
		return null; // Si el usuario cancela
	}

	private Peregrino obtenerDatosModificados(String archivoCredenciales, String nombre, String contrasenia,
			String nacionalidad, String parada) {
		// Pide los nuevos datos al usuario
		String nuevoNombre = obtenerEntrada("Ingrese su nuevo nombre", nombre, false);
		if (nuevoNombre == null)
			return null;

		String nuevaContrasenia = obtenerEntrada("Ingrese su nueva contraseña", contrasenia, false);
		if (nuevaContrasenia == null)
			return null;

		String nuevaNacionalidad = obtenerEntrada(mostrarPaises(), nacionalidad, false);
		if (nuevaNacionalidad == null)
			return null;

		String nuevaParada = obtenerEntrada(mostrarParadas(archivoParadas, true), parada, false);
		if (nuevaParada == null)
			return null;

		// Crear el nuevo peregrino
		Long id = obtenerSiguienteId(archivoCredenciales, true);
		Parada paradaObj = obtenerParada(nuevaParada);
		return new Peregrino(id, nuevoNombre, nuevaNacionalidad, new Carnet(id, paradaObj));
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
	public String mostrarParadas(String archivoParadas, boolean isPeregrino) {
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

			return sb.toString();
		} catch (FileNotFoundException ex) {
			System.out.println("No se ha encontrado el archivo de paradas.");
		} catch (EOFException ex) {
			System.out.println("El archivo de paradas está vacío.");
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return "No se han encontrado paradas";
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
	
	private boolean validarPais(String nacionalidad) {
	    try {
	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document documento = builder.parse(new File("src/main/resources/paises.xml"));
	        documento.getDocumentElement().normalize();
	        NodeList paises = documento.getElementsByTagName("pais");

	        for (int i = 0; i < paises.getLength(); i++) {
	            Element elemento = (Element) paises.item(i);
	            String nombrePais = getNodo("nombre", elemento);
	            if (nombrePais.equalsIgnoreCase(nacionalidad)) {
	                return true; 
	            }
	        }
	    } catch (ParserConfigurationException | SAXException | IOException ex) {
	        System.err.println("Error: " + ex.getMessage());
	    }
	    return false; 
	}

	public boolean validarStr(String str, boolean esMenu) {

		if (str.isEmpty()) {
			return false;
		}

		if (str.trim().isEmpty()) {
			return false;
		}

		if(!esMenu) {
			if (Character.isDigit(str.charAt(0))) {
				return false;
			}
		}

		if (str.contains(" ")) {
			return false;
		}

		return true;
	}

	public String obtenerEntrada(String mensaje, String titulo, boolean esMenu) {
		String entrada;
		do {
			entrada = JOptionPane.showInputDialog(null, mensaje, titulo);
			if (entrada == null) {
				JOptionPane.showMessageDialog(null, "Operación cancelada.");
				return null; // Salir si se cancela
			}
			if (!validarStr(entrada, esMenu)) {
				if(!esMenu) {
					JOptionPane.showMessageDialog(null,
							" El campo " + titulo + " no puede contener espacios ni empezar por números.");
				} else {
					JOptionPane.showMessageDialog(null, "Selecciona una opción entre las disponibles.");
				}
				
			}
			
		} while (!validarStr(entrada, esMenu));
		return entrada.trim();
	}

	private static String getNodo(String etiqueta, Element elem) {
		NodeList nodo = elem.getElementsByTagName(etiqueta).item(0).getChildNodes();
		Node valorNodo = nodo.item(0);
		return valorNodo.getNodeValue();
	}
}
