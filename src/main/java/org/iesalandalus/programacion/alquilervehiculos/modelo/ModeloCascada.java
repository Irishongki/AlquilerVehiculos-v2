package org.iesalandalus.programacion.alquilervehiculos.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Alquiler;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Cliente;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IFuenteDatos;

public class ModeloCascada extends Modelo {

	public ModeloCascada(IFuenteDatos fuenteDatos) {
		setFuenteDatos(fuenteDatos);
	}

	@Override
	public void insertar(Cliente cliente) throws OperationNotSupportedException {
		getClientes().insertar(new Cliente(cliente));
	}

	@Override
	public void insertar(Vehiculo vehiculo) throws OperationNotSupportedException {
		getVehiculos().insertar(Vehiculo.copiar(vehiculo));
	}

	@Override
	public void insertar(Alquiler alquiler) throws OperationNotSupportedException {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede realizar un alquiler nulo.");
		}
		Cliente clienteBuscado = buscar(alquiler.getCliente());
		Vehiculo vehiculoBuscado = buscar(alquiler.getVehiculo());
		if (clienteBuscado == null) {
			throw new OperationNotSupportedException("ERROR: No existe el cliente del alquiler.");
		}
		if (vehiculoBuscado == null) {
			throw new OperationNotSupportedException("ERROR: No existe el turismo del alquiler.");
		}
		getAlquileres().insertar(new Alquiler(clienteBuscado, vehiculoBuscado, alquiler.getFechaAlquiler()));
	}

	public Cliente buscar(Cliente cliente) {
		return new Cliente(getClientes().buscar(cliente));
	}

	public Vehiculo buscar(Vehiculo vehiculo) {
		return Vehiculo.copiar(getVehiculos().buscar(vehiculo));
	}

	public Alquiler buscar(Alquiler alquiler) {
		return new Alquiler(getAlquileres().buscar(alquiler));
	}

	public void modificar(Cliente cliente, String nombre, String telefono) throws OperationNotSupportedException {
		getClientes().modificar(cliente, nombre, telefono);
	}

	public void devolver(Cliente cliente, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		getAlquileres().devolver(cliente, fechaDevolucion);
	}

	public void devolver(Vehiculo vehiculo, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		getAlquileres().devolver(vehiculo, fechaDevolucion);
	}

	public void borrar(Cliente cliente) throws OperationNotSupportedException {
		for (Alquiler alquiler : getAlquileres().get(cliente)) {
			borrar(alquiler);
		}
		getClientes().borrar(cliente);
	}

	public void borrar(Vehiculo vehiculo) throws OperationNotSupportedException {
		for (Alquiler alquiler : getAlquileres().get(vehiculo)) {
			borrar(alquiler);
		}
		getVehiculos().borrar(vehiculo);
	}

	public void borrar(Alquiler alquiler) throws OperationNotSupportedException {
		getAlquileres().borrar(alquiler);
	}

	@Override
	public List<Cliente> getListaClientes() {
		List<Cliente> listaNueva = new ArrayList<>();
		for (Cliente cliente : getClientes().get()) {
			listaNueva.add(new Cliente(cliente));
		}
		return listaNueva;
	}

	@Override
	public List<Vehiculo> getListaVehiculos() {
		List<Vehiculo> listaNueva = new ArrayList<>();
		for (Vehiculo vehiculo : getVehiculos().get()) {
			listaNueva.add(Vehiculo.copiar(vehiculo));
		}
		return listaNueva;
	}

	@Override
	public List<Alquiler> getListaAlquileres() {
		List<Alquiler> listaNueva = new ArrayList<>();
		for (Alquiler alquiler : getAlquileres().get()) {
			listaNueva.add(new Alquiler(alquiler));
		}
		return listaNueva;
	}

	public List<Alquiler> getListaAlquileres(Cliente cliente) {
		List<Alquiler> listaNueva = new ArrayList<>();
		for (Alquiler alquiler : getAlquileres().get(cliente)) {
			listaNueva.add(new Alquiler(alquiler));
		}
		return listaNueva;
	}

	public List<Alquiler> getListaAlquileres(Vehiculo vehiculo) {
		List<Alquiler> listaNueva = new ArrayList<>();
		for (Alquiler alquiler : getAlquileres().get(vehiculo)) {
			listaNueva.add(new Alquiler(alquiler));
		}
		return listaNueva;
	}

}
