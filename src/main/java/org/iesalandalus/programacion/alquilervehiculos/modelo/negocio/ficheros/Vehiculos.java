package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.DocumentBuilder;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Autobus;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Furgoneta;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Turismo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IVehiculos;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Vehiculos implements IVehiculos {

	private static final File FICHERO_VEHICULOS = new File(
			String.format("%s%s%s", "datos", File.separator, "vehiculos.xml"));
	private static final String RAIZ = "vehiculos";
	private static final String VEHICULO = "vehiculo";
	private static final String MARCA = "marca";
	private static final String MODELO = "modelo";
	private static final String MATRICULA = "matricula";
	private static final String CILINDRADA = "cilindrada";
	private static final String PLAZAS = "plazas";
	private static final String PMA = "pma";
	private static final String TIPO = "tipo";
	private static final String TURISMO = "turismo";
	private static final String AUTOBUS = "autobus";
	private static final String FURGONETA = "furgoneta";
	private static Vehiculos instancia;

	private List<Vehiculo> coleccionVehiculos;

	private Vehiculos() {
		coleccionVehiculos = new ArrayList<>();
	}

	static Vehiculos getInstancia() {
		if (instancia == null) {
			instancia = new Vehiculos();
		}
		return instancia;
	}

	public void comenzar() {
		leerDom(UtilidadesXML.leerXmlDeFichero(FICHERO_VEHICULOS));
	}

	private void leerDom(Document documentoXml) {
		NodeList vehiculos = documentoXml.getElementsByTagName(VEHICULO);
		for (int i = 0; i < vehiculos.getLength(); i++) {
			Node vehiculo = vehiculos.item(i);
			if (vehiculo.getNodeType() == Node.ELEMENT_NODE) {
				try {
					insertar(getVehiculo((Element) vehiculo));
				} catch (OperationNotSupportedException e) {
					System.out.print(e.getMessage());
				}
			}
		}
	}

	private Vehiculo getVehiculo(Element elemento) {
		Vehiculo vehiculo = null;
		String marca = elemento.getAttribute(MARCA);
		String modelo = elemento.getAttribute(MODELO);
		String matricula = elemento.getAttribute(MATRICULA);
		String tipo = elemento.getAttribute(TIPO);
		if (tipo.equals(TURISMO)) {
			String cilindrada = elemento.getAttribute(CILINDRADA);
			vehiculo = new Turismo(marca, modelo, Integer.parseInt(cilindrada), matricula);
		} else if (tipo.equals(AUTOBUS)) {
			String plazas = elemento.getAttribute(PLAZAS);
			vehiculo = new Autobus(marca, modelo, Integer.parseInt(plazas), matricula);
		} else if (tipo.equals(FURGONETA)) {
			String pma = elemento.getAttribute(PMA);
			String plazas = elemento.getAttribute(PLAZAS);
			vehiculo = new Furgoneta(marca, modelo, Integer.parseInt(pma), Integer.parseInt(plazas), matricula);
		}
		return vehiculo;
	}

	public void terminar() {
		UtilidadesXML.escribirXmlAFichero(crearDom(), FICHERO_VEHICULOS);
	}

	private Document crearDom() {
		DocumentBuilder constructor = UtilidadesXML.crearConstructorDocumentoXml();
		Document documentoXml = null;
		if (constructor != null) {
			documentoXml = constructor.newDocument();
			documentoXml.appendChild(documentoXml.createElement(RAIZ));
			for (Vehiculo vehiculo : coleccionVehiculos) {
				Element elementoVehiculo = getElemento(documentoXml, vehiculo);
				documentoXml.getDocumentElement().appendChild(elementoVehiculo);
			}
		}
		return documentoXml;
	}

	private Element getElemento(Document documentoXml, Vehiculo vehiculo) {
		Element elementoVehiculo = documentoXml.createElement(VEHICULO);
		elementoVehiculo.setAttribute(MARCA, vehiculo.getMarca());
		elementoVehiculo.setAttribute(MODELO, vehiculo.getModelo());
		elementoVehiculo.setAttribute(MATRICULA, vehiculo.getMatricula());
		if (vehiculo instanceof Turismo turismo) {
			elementoVehiculo.setAttribute(CILINDRADA, String.format("%d", turismo.getCilindrada()));
			elementoVehiculo.setAttribute(TIPO, TURISMO);
		} else if (vehiculo instanceof Autobus autobus) {
			elementoVehiculo.setAttribute(PLAZAS, String.format("%d", autobus.getPlazas()));
			elementoVehiculo.setAttribute(TIPO, AUTOBUS);
		} else if (vehiculo instanceof Furgoneta furgoneta) {
			elementoVehiculo.setAttribute(PMA, String.format("%d", furgoneta.getPma()));
			elementoVehiculo.setAttribute(PLAZAS, String.format("%d", furgoneta.getPlazas()));
			elementoVehiculo.setAttribute(TIPO, FURGONETA);
		}
		return elementoVehiculo;
	}

	@Override
	public List<Vehiculo> get() {
		return new ArrayList<>(coleccionVehiculos);
	}

	@Override
	public void insertar(Vehiculo vehiculo) throws OperationNotSupportedException {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede insertar un vehículo nulo.");
		}
		if (coleccionVehiculos.contains(vehiculo)) {
			throw new OperationNotSupportedException("ERROR: Ya existe un vehículo con esa matrícula.");
		}
		coleccionVehiculos.add(vehiculo);
	}

	@Override
	public Vehiculo buscar(Vehiculo vehiculo) {
		Vehiculo vehiculoEncontrado;
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede buscar un vehículo nulo.");
		}
		int indice = coleccionVehiculos.indexOf(vehiculo);
		if (coleccionVehiculos.contains(vehiculo)) {
			vehiculoEncontrado = coleccionVehiculos.get(indice);
		} else {
			vehiculoEncontrado = null;
		}
		return vehiculoEncontrado;
	}

	@Override
	public void borrar(Vehiculo vehiculo) throws OperationNotSupportedException {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede borrar un vehículo nulo.");
		}
		if (!coleccionVehiculos.contains(vehiculo)) {
			throw new OperationNotSupportedException("ERROR: No existe ningún vehículo con esa matrícula.");
		}
		coleccionVehiculos.remove(vehiculo);
	}
}
