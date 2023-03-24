package org.iesalandalus.programacion.alquilervehiculos.vista.texto;

import javax.naming.OperationNotSupportedException;

public enum Accion {
	SALIR("Salir") {
		@Override
		public void ejecutar() {
			vista.terminar();
		}
	},
	INSERTAR_CLIENTE("Insertar_Cliente") {
		@Override
		public void ejecutar() {
			vista.insertarCliente();
		}
	},
	INSERTAR_VEHICULO("Insertar_Vehículo") {
		@Override
		public void ejecutar() {
			vista.insertarVehiculo();
		}
	},
	INSERTAR_ALQUILER("Insertar_Alquiler") {
		@Override
		public void ejecutar() {
			vista.insertarAlquiler();
		}
	},
	BUSCAR_CLIENTE("Buscar_Cliente") {
		@Override
		public void ejecutar() {
			vista.buscarCliente();
		}
	},
	BUSCAR_VEHICULO("Buscar_Vehículo") {
		@Override
		public void ejecutar() {
			vista.buscarVehiculo();
		}
	},
	BUSCAR_ALQUILER("Buscar_Alquiler") {
		@Override
		public void ejecutar() {
			vista.buscarAlquiler();
		}
	},
	MODIFICAR_CLIENTE("Modificar_Cliente") {
		@Override
		public void ejecutar() {
			vista.modificarCliente();
		}
	},
	DEVOLVER_ALQUILER_CLIENTE("Devolver_Alquiler_Cliente") {
		@Override
		public void ejecutar() {
			vista.devolverAlquilerCliente();
		}
	},
	DEVOLVER_ALQUILER_VEHICULO("Devolver_Alquiler_Vehículo") {
		@Override
		public void ejecutar() {
			vista.devolverAlquilerVehiculo();
		}
	},
	BORRAR_CLIENTE("Borrar_Cliente") {
		@Override
		public void ejecutar() {
			vista.borrarCliente();

		}
	},
	BORRAR_VEHICULO("Borrar_Vehículo") {
		@Override
		public void ejecutar() {
			vista.borrarVehiculo();
		}
	},
	BORRAR_ALQUILER("Borrar_Alquiler") {
		@Override
		public void ejecutar() {
			vista.borrarAlquiler();
		}
	},
	LISTAR_CLIENTES("Listar_Clientes") {
		@Override
		public void ejecutar() {
			vista.listarClientes();
		}
	},
	LISTAR_VEHICULOS("Listar_Vehículos") {
		@Override
		public void ejecutar() {
			vista.listarVehiculos();
		}
	},
	LISTAR_ALQUILERES("Listar_Alquileres") {
		@Override
		public void ejecutar() {
			vista.listarAlquileres();
		}
	},
	LISTAR_ALQUILERES_CLIENTE("Listar_Alquileres_Cliente") {
		@Override
		public void ejecutar() {
			vista.listarAlquileresCliente();
		}
	},
	LISTAR_ALQUILERES_VEHICULO("Listar_Alquileres_Vehículo") {
		@Override
		public void ejecutar() {
			vista.listarAlquileresVehiculo();

		}
	},
	MOSTRAR_ESTADISTICAS_MENSUALES("Mostrar_Estadísticas_Mensuales") {
		@Override
		public void ejecutar() {
			vista.mostrarEstadisticasMensualesTipoVehiculo();
		}
	};

	private String texto;
	private static VistaTexto vista;

	private Accion(String texto) {
		this.texto = texto;
	}

	public abstract void ejecutar();

	static void setVista(VistaTexto vista) {
		Accion.vista = vista;
	}

	private static boolean esOrdinalValido(int ordinal) {
		boolean ordinalCorrecto = true;
		if (ordinal < 0 || ordinal >= values().length) {
			ordinalCorrecto = false;
		}
		return ordinalCorrecto;
	}

	public static Accion get(int ordinal) throws OperationNotSupportedException {
		if (!esOrdinalValido(ordinal)) {
			throw new OperationNotSupportedException("El ordinal pasado no es correcto");
		}
		return values()[ordinal];
	}

	@Override
	public String toString() {
		return String.format("%d-%s", ordinal(), texto);
	}
}
