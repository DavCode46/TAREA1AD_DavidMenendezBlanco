package modelo;

import java.util.Objects;

public class Usuario {

	private String nombreUsuario;
	private String contrasenia;
	private String perfil;
	private String id;
	
	public Usuario() {
		super();
	}
	
	public Usuario(String nombreUsuario, String contrasenia, String perfil, String id) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.contrasenia = contrasenia;
		this.perfil = perfil;
		this.id = id;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contrasenia, id, nombreUsuario, perfil);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(contrasenia, other.contrasenia) && Objects.equals(id, other.id)
				&& Objects.equals(nombreUsuario, other.nombreUsuario) && Objects.equals(perfil, other.perfil);
	}

	@Override
	public String toString() {
		return "Usuario [nombreUsuario=" + nombreUsuario + ", contrasenia=" + contrasenia + ", perfil=" + perfil
				+ ", id=" + id + "]";
	}	
}
