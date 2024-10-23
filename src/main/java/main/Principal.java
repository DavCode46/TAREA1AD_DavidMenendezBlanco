package main;

import javax.swing.JOptionPane;
import controller.Sistema;

public class Principal {

	public static void main(String[] args) {

		String archivoCredenciales = "credenciales.txt";
		String archivoParadas = "paradas.dat";
		Sistema sistema = new Sistema(archivoCredenciales, archivoParadas);
		
	
		boolean flag = false;

		do {
			String menu = "1. Login\n" + "2. Continuar como invitado\n" + "3. Salir\n";
			String opcion = JOptionPane.showInputDialog(null, menu);

			if (opcion == null || opcion.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna opción.");
				continue;
			}

			switch (opcion) {
			case "1": {
				flag = true;
				String nombreUsuario = "";
				String contrasenia = "";
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

					if (sistema.validarCredenciales(nombreUsuario, contrasenia)) {
						String perfil = sistema.obtenerPerfil(nombreUsuario);
						String id = sistema.getId(nombreUsuario);

						JOptionPane.showMessageDialog(null,
								"Bienvenido " + nombreUsuario + "!\nPerfil: " + perfil + "\nID: " + id);
						switch(perfil.toLowerCase()) {
						case "peregrino": {
							
						}
						case "administrador": {
							String nombreParada = JOptionPane.showInputDialog(null, "¿Cual es el nombre de la parada que quieres registrar?");
							char region = JOptionPane.showInputDialog(null, "¿Cual es la region de la parada?").charAt(0);
							
							if(sistema.paradaExiste(nombreParada)) {
								JOptionPane.showMessageDialog(null, "La parada ya existe.");
								break;
							}
							
							String responsable = JOptionPane.showInputDialog(null, "¿Quien es el responsable de la parada?");
							String contraseniaResponsable = JOptionPane.showInputDialog(null, "Ingrese la contraseña del responsable");
							
							if(sistema.validarCredenciales(nombreUsuario, contraseniaResponsable)) {
								JOptionPane.showMessageDialog(null, "El responsable ya existe");
								break;
							}
							
							sistema.registrarParada(archivoParadas, nombreParada, region, responsable);
							
							sistema.registrarCredenciales(archivoCredenciales, responsable, contraseniaResponsable, "parada");				
						}
						case "responsable": {
							
						}
						default: {
							JOptionPane.showMessageDialog(null, "Perfil no encontrado.");
						}
						}
					} else {
						JOptionPane.showMessageDialog(null, "Credenciales incorrectas.");
					}
				} while (!sistema.validarCredenciales(nombreUsuario, contrasenia));
				break;
			}
			case "2": {
				flag = true;
				JOptionPane.showMessageDialog(null, "Bienvenido invitado!");
				break;
			}
			case "3": {
				flag = true;
				break;
			}
			default: {
				JOptionPane.showMessageDialog(null, "Opción no válida.");
			}
			}
		} while (!flag);

		JOptionPane.showMessageDialog(null, "Programa finalizado.");
	}
}
