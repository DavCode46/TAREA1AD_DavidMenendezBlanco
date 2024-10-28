package modelo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 
 * Clase que representa a un peregrino, cuando se crea un peregrino
 * también se crea un carnet con los datos de la parada donde se
 * está registrando el peregrino.
 * 
 */
public class Peregrino {
	private Long id;
	private String nombre;
	private String nacionalidad;
	private Carnet carnet;
	
	/* Carga pesada de estancias y paradas
	 * Posiblemente en un futuro se cambie a una carga ligera 
	 * para evitar recursividad
	 */
	private List<Estancia> estancias = new ArrayList<>(); 
	private List<Parada> paradas = new LinkedList<>();
	

	public Peregrino() {
		super();
	}

	public Peregrino(Long id, String nombre, String nacionalidad, Carnet carnet) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.nacionalidad = nacionalidad;
		this.carnet = carnet;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public List<Estancia> getEstancias() {
		return estancias;
	}

	public void setEstancias(List<Estancia> estancias) {
		this.estancias = estancias;
	}

	public List<Parada> getParadas() {
		return paradas;
	}

	public void setParadas(List<Parada> paradas) {
		this.paradas = paradas;
	}

	public Carnet getCarnet() {
		return carnet;
	}

	public void setCarnet(Carnet carnet) {
		this.carnet = carnet;
	}

	@Override
	public int hashCode() {
		return Objects.hash(carnet, estancias, id, nacionalidad, nombre, paradas);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Peregrino other = (Peregrino) obj;
		return Objects.equals(carnet, other.carnet) && Objects.equals(estancias, other.estancias)
				&& Objects.equals(id, other.id) && Objects.equals(nacionalidad, other.nacionalidad)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(paradas, other.paradas);
	}

	@Override
	public String toString() {
	    return "Peregrino [id=" + id + ", nombre=" + nombre + ", nacionalidad=" + nacionalidad +
	           ", carnet=" + carnet + "]";  
	}


}
