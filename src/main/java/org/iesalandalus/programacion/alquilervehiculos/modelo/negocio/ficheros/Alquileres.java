package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.DocumentBuilder;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Alquiler;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Cliente;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IAlquileres;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Alquileres implements IAlquileres {

	private List<Alquiler> coleccionAlquileres;
	private static final File FICHERO_ALQUILERES = new File(
			String.format("%s%s%s", "datos", File.separator, "alquileres.xml"));
	private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final String RAIZ = "alquileres";
	private static final String ALQUILER = "alquiler";
	private static final String CLIENTE = "cliente";
	private static final String VEHICULO = "vehiculo";
	private static final String FECHA_ALQUILER = "fechaAlquiler";
	private static final String FECHA_DEVOLUCION = "fechaDevolucion";
	private static Alquileres instancia;

	private Alquileres() {
		coleccionAlquileres = new ArrayList<>();
	}

	static Alquileres getInstancia() {
		if (instancia == null) {
			instancia = new Alquileres();
		}
		return instancia;
	}

	public void comenzar() {
		Document documento = UtilidadesXml.leerXmlDeFichero(FICHERO_ALQUILERES);
		if (documento != null) {
			leerDom(documento);
			System.out.print("El documento se ha leido correctamente");
		} else {
			System.out.print("ERROR: El documento no se ha leido correctamente");
		}
	}

	private void leerDom(Document documentoXml) {
		NodeList alquileres = documentoXml.getElementsByTagName(ALQUILER);
		for (int i = 0; i < alquileres.getLength(); i++) {
			Node alquiler = alquileres.item(i);
			if (alquiler.getNodeType() == Node.ELEMENT_NODE) {
				try {
					insertar(getAlquiler((Element) alquiler));
				} catch (NullPointerException | IllegalArgumentException | OperationNotSupportedException e) {
					System.out.printf("Error al procesar el alquiler : %d --> %s%n", i, e.getMessage());
				}
			}
		}
	}

	private Alquiler getAlquiler(Element elemento) throws OperationNotSupportedException {
		Alquiler alquiler = null;
		Cliente cliente = null;
		Vehiculo vehiculo = null;
		String dni = elemento.getAttribute(CLIENTE);
		cliente = Cliente.getClienteConDni(dni);
		try {
			cliente = Clientes.getInstancia().buscar(cliente);
		} catch (NullPointerException e) {
			System.out.print(e.getMessage());
		}
		String fechaAlquiler = elemento.getAttribute(FECHA_ALQUILER);
		String matricula = elemento.getAttribute(VEHICULO);
		vehiculo = Vehiculo.getVehiculoConMatricula(matricula);
		try {
			vehiculo = Vehiculos.getInstancia().buscar(vehiculo);
		} catch (NullPointerException e) {
			System.out.print(e.getMessage());
		}
		alquiler = new Alquiler(cliente, vehiculo, LocalDate.parse(fechaAlquiler, FORMATO_FECHA));

		if (elemento.hasAttribute(FECHA_DEVOLUCION)) {
			String fechaDevolucion = elemento.getAttribute(FECHA_DEVOLUCION);
			alquiler.devolver(LocalDate.parse(fechaDevolucion, FORMATO_FECHA));
		}
		return alquiler;
	}

	public void terminar() {
		UtilidadesXml.escribirXmlAFichero(crearDom(), FICHERO_ALQUILERES);
	}

	private Document crearDom() {
		DocumentBuilder constructor = UtilidadesXml.crearConstructorDocumentoXml();
		Document documentoXml = null;
		if (constructor != null) {
			documentoXml = constructor.newDocument();
			documentoXml.appendChild(documentoXml.createElement(RAIZ));
			for (Alquiler alquiler : coleccionAlquileres) {
				Element elementoAlquiler = getElemento(documentoXml, alquiler);
				documentoXml.getDocumentElement().appendChild(elementoAlquiler);
			}
		}
		return documentoXml;
	}

	private Element getElemento(Document documentoXml, Alquiler alquiler) {
		Element elementoAlquiler = documentoXml.createElement(ALQUILER);
		elementoAlquiler.setAttribute(CLIENTE, alquiler.getCliente().getDni());
		elementoAlquiler.setAttribute(FECHA_ALQUILER, alquiler.getFechaAlquiler().format(FORMATO_FECHA));
		elementoAlquiler.setAttribute(VEHICULO, alquiler.getVehiculo().getMatricula());

		if (alquiler.getFechaDevolucion() != null) {
			elementoAlquiler.setAttribute(FECHA_DEVOLUCION, alquiler.getFechaDevolucion().format(FORMATO_FECHA));
		}
		return elementoAlquiler;
	}

	@Override
	public List<Alquiler> get() {
		return new ArrayList<>(coleccionAlquileres);
	}

	// Devolvemos una lista de los Alquileres para el cliente indicado
	@Override
	public List<Alquiler> get(Cliente cliente) {
		List<Alquiler> listaAlquileres = new ArrayList<>();
		for (Alquiler elemento : coleccionAlquileres) {
			if (elemento.getCliente().equals(cliente)) {
				listaAlquileres.add(elemento);
			}
		}
		return listaAlquileres;
	}

	// Devolvemos una lista de los Alquileres para el turismo indicado
	@Override
	public List<Alquiler> get(Vehiculo turismo) {
		List<Alquiler> listaAlquileres = new ArrayList<>();
		for (Alquiler elemento : coleccionAlquileres) {
			if (elemento.getVehiculo().equals(turismo)) {
				listaAlquileres.add(elemento);
			}
		}
		return listaAlquileres;
	}

	private void comprobarAlquiler(Cliente cliente, Vehiculo turismo, LocalDate fechaAlquiler)
			throws OperationNotSupportedException {
		for (Alquiler alquiler : coleccionAlquileres) {
			if (alquiler.getFechaDevolucion() == null) {
				if (alquiler.getCliente().equals(cliente)) {
					throw new OperationNotSupportedException("ERROR: El cliente tiene otro alquiler sin devolver.");
				} else if (alquiler.getVehiculo().equals(turismo)) {
					throw new OperationNotSupportedException("ERROR: El vehículo está actualmente alquilado.");
				}
			} else if (alquiler.getCliente().equals(cliente) && (alquiler.getFechaDevolucion().isAfter(fechaAlquiler)
					|| alquiler.getFechaDevolucion().isEqual(fechaAlquiler))) {
				throw new OperationNotSupportedException("ERROR: El cliente tiene un alquiler posterior.");
			} else if (alquiler.getVehiculo().equals(turismo) && (alquiler.getFechaDevolucion().isAfter(fechaAlquiler)
					|| alquiler.getFechaDevolucion().isEqual(fechaAlquiler))) {
				throw new OperationNotSupportedException("ERROR: El vehículo tiene un alquiler posterior.");
			}
		}
	}

	@Override
	public void insertar(Alquiler alquiler) throws OperationNotSupportedException {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede insertar un alquiler nulo.");
		}
		comprobarAlquiler(alquiler.getCliente(), alquiler.getVehiculo(), alquiler.getFechaAlquiler());
		coleccionAlquileres.add(alquiler);
	}

	@Override
	public void devolver(Cliente cliente, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		Alquiler alquilerEncontrado = getAlquilerAbierto(cliente);
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede devolver un alquiler de un cliente nulo.");
		}
		if (alquilerEncontrado == null) {
			throw new OperationNotSupportedException("ERROR: No existe ningún alquiler abierto para ese cliente.");
		}
		alquilerEncontrado.devolver(fechaDevolucion);
	}

	private Alquiler getAlquilerAbierto(Cliente cliente) {
		Alquiler alquilerAbierto = null;
		for (Iterator<Alquiler> iterator = coleccionAlquileres.iterator(); alquilerAbierto == null
				&& iterator.hasNext();) {
			Alquiler alquiler = iterator.next();
			if (alquiler.getCliente().equals(cliente) && alquiler.getFechaDevolucion() == null) {
				alquilerAbierto = alquiler;
			}
		}
		return alquilerAbierto;
	}

	@Override
	public void devolver(Vehiculo vehiculo, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		Alquiler alquilerEncontrado = getAlquilerAbierto(vehiculo);
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede devolver un alquiler de un vehículo nulo.");
		}
		if (alquilerEncontrado == null) {
			throw new OperationNotSupportedException("ERROR: No existe ningún alquiler abierto para ese vehículo.");
		}
		alquilerEncontrado.devolver(fechaDevolucion);
	}

	private Alquiler getAlquilerAbierto(Vehiculo vehiculo) {
		Alquiler alquilerAbierto = null;
		for (Iterator<Alquiler> iterator = coleccionAlquileres.iterator(); alquilerAbierto == null
				&& iterator.hasNext();) {
			Alquiler alquiler = iterator.next();
			if (alquiler.getVehiculo().equals(vehiculo) && alquiler.getFechaDevolucion() == null) {
				alquilerAbierto = alquiler;
			}
		}
		return alquilerAbierto;
	}

	@Override
	public Alquiler buscar(Alquiler alquiler) {
		Alquiler alquilerEncontrado;
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede buscar un alquiler nulo.");
		}
		int indice = coleccionAlquileres.indexOf(alquiler);
		if (coleccionAlquileres.contains(alquiler)) {
			alquilerEncontrado = coleccionAlquileres.get(indice);
		} else {
			alquilerEncontrado = null;
		}
		return alquilerEncontrado;
	}

	@Override
	public void borrar(Alquiler alquiler) throws OperationNotSupportedException {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede borrar un alquiler nulo.");
		}
		if (!coleccionAlquileres.contains(alquiler)) {
			throw new OperationNotSupportedException("ERROR: No existe ningún alquiler igual.");
		}
		coleccionAlquileres.remove(alquiler);
	}

}
