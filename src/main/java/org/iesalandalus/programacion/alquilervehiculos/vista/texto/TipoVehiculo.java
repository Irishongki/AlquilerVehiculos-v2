package org.iesalandalus.programacion.alquilervehiculos.vista.texto;

import javax.naming.OperationNotSupportedException;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Autobus;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Furgoneta;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Turismo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;

public enum TipoVehiculo {
	TURISMO("Turismo"), AUTOBUS("Autobus"), FURGONETA("Furgoneta");

	private String nombre;

	private TipoVehiculo(String nombre) {
		this.nombre = nombre;
	}

	private static boolean esOrdinalValido(int ordinal) {
		boolean ordinalCorrecto = true;
		if (ordinal < 0 || ordinal >= values().length) {
			ordinalCorrecto = false;
		}
		return ordinalCorrecto;
	}

	public static TipoVehiculo get(int ordinal) throws OperationNotSupportedException {
		if (!esOrdinalValido(ordinal)) {
			throw new OperationNotSupportedException("El ordinal pasado no es correcto");
		}
		return values()[ordinal];
	}

	public static TipoVehiculo get(Vehiculo vehiculo) {
		int indice = -1;
		if (vehiculo instanceof Turismo) {
			indice = TipoVehiculo.TURISMO.ordinal();
		} else if (vehiculo instanceof Autobus) {
			indice = TipoVehiculo.AUTOBUS.ordinal();
		} else if (vehiculo instanceof Furgoneta) {
			indice = TipoVehiculo.FURGONETA.ordinal();
		}
		return values()[indice];
	}

	@Override
	public String toString() {
		return String.format("%s", nombre);
	}

}
