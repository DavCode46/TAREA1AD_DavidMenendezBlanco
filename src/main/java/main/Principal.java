package main;

import java.util.Properties;

import javax.swing.JOptionPane;

import controller.Sistema;
import modelo.Perfil;


public class Principal {

	static Sesion userActivo = new Sesion("Invitado", Perfil.INVITADO, 1L);
	
	public static void main(String[] args) {

		String archivoCredenciales = "files/credenciales.txt";
		String archivoParadas = "files/paradas.dat";
		Sistema sistema = new Sistema(archivoCredenciales, archivoParadas);
		Properties prop = new Properties();
		String usuarioAdmin = prop.getProperty("usuarioAdmin");
		String contraseniaAdmin = prop.getProperty("passwordAdmin");
		
	
		

		do {
			String menu = "1. Login\n" + "2. Registrarse como peregrino\n" + "3. Salir\n";
			String opcion = JOptionPane.showInputDialog(null, menu);

			if (opcion == null || opcion.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna opción.");
				continue;
			}

			switch (opcion) {
			case "1": {
				
				String nombreUsuario = "";
				String contrasenia = "";
				Perfil perfil = Perfil.INVITADO;
				do {
					nombreUsuario = JOptionPane.showInputDialog(null, "Ingrese su nombre de usuario");
					if (nombreUsuario == null || nombreUsuario.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Nombre de usuario no válido.");
						continue;
					}

					contrasenia = JOptionPane.showInputDialog(null, "Ingrese su contraseña");
					if (contrasenia == null || contrasenia.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Contraseña no válida.");
						continue;
					}

					if (sistema.validarCredenciales(archivoCredenciales, nombreUsuario, contrasenia)) {
						
						perfil = sistema.obtenerPerfil(nombreUsuario);
						
						Long id = sistema.getId(nombreUsuario);

						JOptionPane.showMessageDialog(null,
								"Bienvenido " + nombreUsuario + "!\nPerfil: " + perfil + "\nID: " + id);
						switch(perfil) {
						case PEREGRINO: {
							// Opciones peregrino -- sellar parada, exportar carnet.. etc
							mostrarOpcionesPeregrino();
						}
						case ADMINISTRADOR: {
							mostrarOpcionesAdmin(sistema, archivoCredenciales, archivoParadas, nombreUsuario);
							break;
						}
						case PARADA: {
							// Opciones responsable de parada...
						}
						default: {
							JOptionPane.showMessageDialog(null, "Perfil no encontrado.");
						}
						}
					} else {
						JOptionPane.showMessageDialog(null, "Credenciales incorrectas.");
					}
				} while (!sistema.validarCredenciales(archivoCredenciales, nombreUsuario, contrasenia));
				break;
			}
			case "2": {
				registrarPeregrino();
				break;
			}
			case "3": {
				 int respuesta = JOptionPane.showConfirmDialog(null, 
				            "¿Estás seguro que quieres salir?", 
				            "Confirmar", 
				            JOptionPane.YES_NO_OPTION);
				 if(respuesta == JOptionPane.YES_OPTION) {
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
	
	private void mostrarMenu() {
		
	}
	
	private static void mostrarOpcionesPeregrino() {
		
	}
	
	private static void mostrarOpcionesAdmin(Sistema sistema, String archivoCredenciales, String archivoParadas, String nombreUsuario) {
		String nombreParada = JOptionPane.showInputDialog(null, "¿Cual es el nombre de la parada que quieres registrar?");
		char region = JOptionPane.showInputDialog(null, "¿Cual es la region de la parada?").charAt(0);
		
		if(sistema.paradaExiste(nombreParada)) {
			JOptionPane.showMessageDialog(null, "La parada ya existe.");
			return;
		}
		
		String responsable = JOptionPane.showInputDialog(null, "¿Quien es el responsable de la parada?");
		String contraseniaResponsable = JOptionPane.showInputDialog(null, "Ingrese la contraseña del responsable");
		
		if(sistema.validarCredenciales(archivoCredenciales, nombreUsuario, contraseniaResponsable)) {
			JOptionPane.showMessageDialog(null, "El responsable ya existe");
			return;
		}
		
		sistema.registrarParada(archivoParadas, nombreParada, region, responsable);
		
		sistema.registrarCredenciales(archivoCredenciales, responsable, contraseniaResponsable, "parada");	
	}
	
	private static void registrarPeregrino() {
		
	}
}
