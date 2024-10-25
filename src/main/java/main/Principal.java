package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import controller.Sistema;
import modelo.Carnet;
import modelo.Peregrino;
import modelo.Perfil;

public class Principal {

	static Sesion userActivo = new Sesion("Invitado", Perfil.INVITADO, 1L);

	public static void main(String[] args) {

		mostrarMenu();

	}

	private static void mostrarMenu() {
		String archivoCredenciales = "files/credenciales.txt";
		String archivoParadas = "files/paradas.dat";
		Sistema sistema = new Sistema(archivoCredenciales, archivoParadas);		
		Peregrino p = null;
		Properties prop = new Properties();

		try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
			prop.load(input);

		} catch (IOException e) {
			e.printStackTrace();
		}

		String usuarioAdmin = prop.getProperty("usuarioAdmin");
		String contraseniaAdmin = prop.getProperty("passwordAdmin");

		do {
			String menu = "1. Login\n" + "2. Registrarse como peregrino\n" + "3. Salir\n";
			String opcion = JOptionPane.showInputDialog(null, menu).trim();

			if (opcion == null || opcion.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna opción.");
				continue;
			}

			switch (opcion) {
			case "1": {

				String nombreUsuario = "";
				String contrasenia = "";

				do {
					nombreUsuario = JOptionPane.showInputDialog(null, "Ingrese su nombre de usuario").trim();
					if (nombreUsuario == null || nombreUsuario.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Nombre de usuario no válido.");
						continue;
					}

					contrasenia = JOptionPane.showInputDialog(null, "Ingrese su contraseña").trim();
					if (contrasenia == null || contrasenia.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Contraseña no válida.");
						continue;
					}

					if (nombreUsuario.equals(contraseniaAdmin) && contrasenia.equals(contraseniaAdmin)) {
						userActivo = new Sesion(usuarioAdmin, Perfil.ADMINISTRADOR, 1L);

						JOptionPane.showMessageDialog(null, "Bienvenido " + userActivo.getNombreUsuario()
								+ "!\nPerfil: " + userActivo.getPerfil() + "\nID: " + userActivo.getId());
					} else if (sistema.validarCredenciales(archivoCredenciales, nombreUsuario, contrasenia)) {

						userActivo.setPerfil(sistema.obtenerPerfil(nombreUsuario));
						userActivo.setId(sistema.getId(nombreUsuario));
						userActivo.setNombreUsuario(nombreUsuario);
						

						

						JOptionPane.showMessageDialog(null,
								"Bienvenido " + nombreUsuario + "!\nPerfil: " + userActivo.getPerfil() + "\nID: " + userActivo.getId());
					} else {
						JOptionPane.showMessageDialog(null, "Credenciales incorrectas.");
						continue;
					}
					switch (userActivo.getPerfil()) {
					case PEREGRINO: {
						// Opciones peregrino -- sellar parada, exportar carnet.. etc
						mostrarOpcionesPeregrino();
						break;
					}
					case ADMINISTRADOR: {
						mostrarOpcionesAdmin(sistema, archivoCredenciales, archivoParadas, nombreUsuario);
						break;
					}
					case PARADA: {
						// Opciones responsable de parada...
						break;
					}
					default: {
						JOptionPane.showMessageDialog(null, "Perfil no encontrado.");
					}
					}

				} while (!sistema.validarCredenciales(archivoCredenciales, nombreUsuario, contrasenia));
				break;
			}
			case "2": {
				// Registrar peregrino
				p = Principal.registrarPeregrino(sistema, archivoParadas, archivoCredenciales);
				break;
			}
			case "3": {
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres salir?", "Confirmar",
						JOptionPane.YES_NO_OPTION);
				if (respuesta == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "Hasta la próxima");
					userActivo = null;
					break;
				}
				JOptionPane.showMessageDialog(null, "Volviendo al menú...");

				continue;
			}
			default: {
				JOptionPane.showMessageDialog(null, "Opción no válida.");
			}
			}
		} while (userActivo != null);
	}

	private static void mostrarOpcionesPeregrino() {
		int opcion = -1;
		do {

		} while (opcion != 0);
	}

	private static void mostrarOpcionesAdmin(Sistema sistema, String archivoCredenciales, String archivoParadas,
			String nombreUsuario) {
		String nombreParada = JOptionPane
				.showInputDialog(null, "¿Cual es el nombre de la parada que quieres registrar?").trim();
		char region = JOptionPane.showInputDialog(null, "¿Cual es la region de la parada?").trim().charAt(0);

		if (sistema.paradaExiste(nombreParada)) {
			JOptionPane.showMessageDialog(null, "La parada ya existe.");
			return;
		}

		String responsable = JOptionPane.showInputDialog(null, "¿Quien es el responsable de la parada?").trim();
		String contraseniaResponsable = JOptionPane.showInputDialog(null, "Ingrese la contraseña del responsable")
				.trim();

		if (sistema.validarCredenciales(archivoCredenciales, nombreUsuario, contraseniaResponsable)) {
			JOptionPane.showMessageDialog(null, "El responsable ya existe");
			return;
		}

		sistema.registrarParada(archivoParadas, nombreParada, region, responsable);

		sistema.registrarCredenciales(archivoCredenciales, responsable, contraseniaResponsable, "parada");
	}

	private static Peregrino registrarPeregrino(Sistema sistema, String archivoParadas, String archivoCredenciales) {
		Peregrino nuevoPeregrino = null;
		String nombre = "";
		String contrasenia = "";
		String parada = "";
		String nacionalidad = "";
		JOptionPane.showMessageDialog(null, "Formulario de registro de nuevo peregrino");
		try {
			nombre = JOptionPane.showInputDialog(null, "Ingrese su nombre", "Nombre").trim();
			contrasenia = JOptionPane.showInputDialog(null, "Ingrese su contraseña", "Contraseña").trim();

			// Seleccionar nacionalidad del archivo paises.xml

			nacionalidad = "España";

			// Mostrar paradas desde paradas.dat y usuario debe seleccionar
			sistema.mostrarParadas(archivoParadas, true);

			parada = JOptionPane.showInputDialog(null, "¿En qué parada se encuentra?").trim();
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getLocalizedMessage());
			ex.printStackTrace();
		}

		String mensajeFormateado = String.format("Confirma los datos \n" + "Nombre: %s \n" + "Contraseña: %s\n"
				+ "Nacionalidad: %s\n" + "Parada actual: %s\n", nombre, contrasenia, nacionalidad, parada);

		int confirmacion = JOptionPane.showConfirmDialog(null, mensajeFormateado, "Confirma",
				JOptionPane.YES_NO_OPTION);
		if (confirmacion == JOptionPane.YES_OPTION) {
			// Datos correctos --> Continuar
			if (sistema.validarCredenciales(archivoCredenciales, nombre, contrasenia)) {
				JOptionPane.showMessageDialog(null, "El usuario ya existe");
				return null;
			} else {
				// Recuperar el mayor id de peregrino para poner el siguiente al nuevo peregrino
				// ¿Mismo id al carnet?.
				nuevoPeregrino = new Peregrino(1L, nombre, nacionalidad, new Carnet());
			}

		} else {
			// Datos Erroneos Opción para cambiarlos

		}

		return nuevoPeregrino;
	}
}
