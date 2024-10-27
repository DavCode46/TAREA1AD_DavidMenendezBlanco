package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import controller.ExportarCarnetXML;
import controller.Sistema;
import modelo.Carnet;
import modelo.Estancia;
import modelo.Parada;
import modelo.Peregrino;
import modelo.Perfil;

public class Principal {

	static Sesion userActivo = new Sesion("Invitado", Perfil.invitado, 1L);

	public static void main(String[] args) {

//		Peregrino p = new Peregrino(1L, "Pepe", "España", new Carnet(1L, new Parada(1L, "Sevilla", 'A', "Pepe")));
//		Parada parada = new Parada(1L, "Sevilla", 'A', "Jose");
//		Parada parada2 = new Parada(2L, "Asturias", 'B', "Diego");
//		List<Estancia> estancias = new ArrayList<>();
//		List <Parada> paradas = new ArrayList<>();
//		paradas.add(parada);
//		paradas.add(parada2);
//		estancias.add(new Estancia(1L, LocalDate.of(2022, 10, 30), true, p, parada));
//		estancias.add(new Estancia(2L, LocalDate.of(1993, 10, 16), false, p, parada2));
//		p.setEstancias(estancias);
//		p.setParadas(paradas);
//		ExportarCarnetXML exportar = new ExportarCarnetXML();
//		try {
//            exportar.exportarCarnet();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
		String opcion = "";

		do {
			String menu = "1. Login\n" + "2. Registrarse como peregrino\n" + "3. Salir\n";
			
			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true);

			if (opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción.");
				continue;
			}

			switch (opcion) {
			case "1": {

				String nombreUsuario = "";
				String contrasenia = "";

				do {

					nombreUsuario = sistema.obtenerEntrada("Ingrese su nombre de usuario", "Nombre de usuario", false);

					if (nombreUsuario == null) {
						break;
					}

					contrasenia = sistema.obtenerEntrada("Ingrese su contraseña", "Contraseña", false);
					if (contrasenia == null) {
						break;
					}

					if (nombreUsuario.equals(usuarioAdmin) && contrasenia.equals(contraseniaAdmin)) {
						userActivo = new Sesion(usuarioAdmin, Perfil.administrador, 1L);

					} else if (sistema.validarCredenciales(archivoCredenciales, nombreUsuario, contrasenia)) {

						userActivo.setPerfil(sistema.obtenerPerfil(nombreUsuario));
						userActivo.setId(sistema.getId(nombreUsuario));
						userActivo.setNombreUsuario(nombreUsuario);

						JOptionPane.showMessageDialog(null, "Bienvenido " + nombreUsuario + "!\nPerfil: "
								+ userActivo.getPerfil() + "\nID: " + userActivo.getId());
					} else {
						JOptionPane.showMessageDialog(null, "Credenciales incorrectas.");
						continue;
					}
					switch (userActivo.getPerfil()) {
					case peregrino: {
						// Opciones peregrino --> sellar parada, exportar carnet.. etc
						mostrarOpcionesPeregrino(p, sistema);
						break;
					}
					case administrador: {
						mostrarOpcionesAdmin(sistema, archivoCredenciales, archivoParadas, nombreUsuario);
						break;
					}
					case parada: {
						// Implementación futura -->  Opciones responsable de parada...
						// mostrarOpcionesParada();
						JOptionPane.showMessageDialog(null, "Sección en desarrollo...");
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
				p = sistema.registrarPeregrino();
				if (p != null) {
					userActivo = new Sesion(p.getNombre(), Perfil.peregrino, p.getId());
					String mensajeBienvenida = String.format("Sus datos: \n "
							+ "ID: %s\n "
							+ "Nombre: %s\n"
							+ "Nacionalidad: %s\n"
							+ "Fecha de expedición del carnet: %s\n"
							+ "Parada inicial: %s\n"
							+ "Región de la parada: %s\n",p.getId() , p.getNombre(), p.getNacionalidad(), p.getCarnet().getFechaExp(), p.getParadas().get(0).getNombre(), p.getParadas().get(0).getRegion());
					JOptionPane.showMessageDialog(null, mensajeBienvenida);
					mostrarOpcionesPeregrino(p, sistema);
				}
				break;
			}
			case "3": {
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres salir?", "Confirmar",
						JOptionPane.YES_NO_OPTION);
				if (respuesta == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "Has salido del sistema.");
					userActivo = new Sesion("Invitado", Perfil.invitado, 1L);
					break;
				}
				JOptionPane.showMessageDialog(null, "Volviendo al menú...");

				continue;
			}
			default: {
				JOptionPane.showMessageDialog(null, "Opción no válida.");
			}
			}
		} while (!"3".equals(opcion));
	}

	private static void mostrarOpcionesPeregrino(Peregrino p, Sistema sistema) {
		ExportarCarnetXML exportar = new ExportarCarnetXML();
		String opcion = "";
		do {
			String menu = "1. Exportar carnet\n" + "2. Sellar carnet (Implementación futura)\n" + "0. Cerrar sesión\n";
			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true);
			if(opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción.");
				continue;
			}
			switch (opcion) {
			case "1": {
				// Exportar carnet
				try {
					exportar.exportarCarnet(p);
				} catch (Exception e) {
					System.out.println("Error: " + e.getLocalizedMessage());
					e.printStackTrace();
				}
				break;
			}
			case "2": {
				// Sellar carnet
				JOptionPane.showMessageDialog(null, "Sección en desarrollo...");
				break;
			}
			case "0": {
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres cerrar sesión?",
						"Confirmar", JOptionPane.YES_NO_OPTION);
				if (respuesta == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "Has cerrado sesión.");
					userActivo = new Sesion("Invitado", Perfil.invitado, 1L);
					return;
				}
				JOptionPane.showMessageDialog(null, "Volviendo al menú...");

				continue;
			}

			default: {
				JOptionPane.showMessageDialog(null, "Opción no válida.");
			}
			}
		} while (!opcion.equals("0"));
	}

	private static void mostrarOpcionesAdmin(Sistema sistema, String archivoCredenciales, String archivoParadas,
			String nombreUsuario) {
		String opcion = "";
		JOptionPane.showMessageDialog(null,
				"Bienvenido " + nombreUsuario + "!\nPerfil: " + userActivo.getPerfil() + "\nID: " + userActivo.getId());
		do {
			String menu = "1. Registrar responsable de parada\n" + "0. Cerrar sesión\n";
			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true);
			if(opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción.");
				continue;
			}
			switch (opcion) {
			case "1": {
				String nombreParada = sistema.obtenerEntrada("Ingrese el nombre de la parada", "Nombre de la parada", false);
				if (nombreParada == null) {
					return;
				}
				char region = sistema.obtenerEntrada("¿Cual es la region de la parada?", "Region de la parada", false)
						.charAt(0);
				if (region == '0') {
					return;
				}

				if (sistema.paradaExiste(nombreParada)) {
					JOptionPane.showMessageDialog(null, "La parada ya existe.");
					return;
				}

				String responsable = sistema.obtenerEntrada("¿Quien es el responsable de la parada?", "Responsable", false);

				if (responsable == null) {
					return;
				}
				String contraseniaResponsable = sistema.obtenerEntrada("Ingrese la contraseña del responsable",
						"Contraseña del responsable", false);
				if (contraseniaResponsable == null) {
					return;
				}

				if (sistema.validarCredenciales(archivoCredenciales, nombreUsuario, contraseniaResponsable)) {
					JOptionPane.showMessageDialog(null, "El responsable ya existe");
					return;
				}

				sistema.registrarParada(archivoParadas, nombreParada, region, responsable);
				sistema.registrarCredenciales(archivoCredenciales, responsable, contraseniaResponsable, Perfil.parada.toString().toLowerCase(), false);
				break;
			}
			case "0": {
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres cerrar sesión?",
						"Confirmar", JOptionPane.YES_NO_OPTION);
				if (respuesta == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "Has cerrado sesión.");
					userActivo = new Sesion("Invitado", Perfil.invitado, 1L);
					return;
				} else {
					JOptionPane.showMessageDialog(null, "Volviendo al menú...");

				}
				break;
			}
			default:
				JOptionPane.showMessageDialog(null, "Opción no válida.");
			}
		} while (true);
	}

}
