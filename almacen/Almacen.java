package aed.almacen;

import es.upm.aedlib.indexedlist.IndexedList;
import es.upm.aedlib.indexedlist.ArrayIndexedList;

/**
 * Implementa la logica del almacen.
 */
public class Almacen implements ClienteAPI, AlmacenAPI, ProductorAPI {

	// Compras (sin ningun orden especial)
	private ArrayIndexedList<Compra> compras;
	// Productos ordenados ascendamente usando el productoId de un Product.
	private ArrayIndexedList<Producto> productos;

	// No es necesario cambiar el constructor
	/**
	 * Crea un almacen.
	 */
	public Almacen() {
		this.compras = new ArrayIndexedList<>();
		this.productos = new ArrayIndexedList<>();
	}

	/**
	 * Reabastece un producto en el almacén. Si el producto ya existe, se aumenta su
	 * cantidad disponible. Si no existe, se añade un nuevo producto.
	 *
	 * @param productoId El identificador del producto a reabastecer
	 * @param cantidad   La cantidad del producto que se va a añadir
	 */
	@Override
	public void reabastecerProducto(String productoId, int cantidad) {
		// Se busca el índice donde está o debería estar el producto usando búsqueda
		// binaria
		int indice = busquedaBinariaEnProductos(productoId);

		// Si el producto ya existe, se actualiza su cantidad
		if (indice < productos.size() && productos.get(indice).getProductoId().compareTo(productoId) == 0) {
			Producto producto = productos.get(indice);
			producto.setCantidadDisponible(producto.getCantidadDisponible() + cantidad);
		} else {
			// Crear un nuevo producto y añadirlo en la posición correcta
			Producto nuevoProducto = new Producto(productoId, cantidad);
			productos.add(indice, nuevoProducto);
		}
	}

	/**
	 * Devuelve el producto con el identificador dado si está en el almacén.
	 *
	 * @param productoId El identificador del producto
	 * @return El producto si se encuentra, o null si no existe
	 *
	 * @Override
	 * 
	 */
	public Producto getProducto(String productoId) {
		// Se recorre la lista de productos para encontrar el producto con el ID correspondiente
		for (int i = 0; i < productos.size(); i++) {
			Producto producto = productos.get(i);
			if (producto.getProductoId().compareTo(productoId) == 0) {
				return producto;
			}
		}
		return null; // Producto no encontrado
	}

	/**
	 * Devuelve la compra con el identificador dado si existe.
	 *
	 * @param compraId El identificador de la compra
	 * @return La compra si se encuentra, o null si no existe
	 */
	@Override
	public Compra getCompra(Integer compraId) {
		// Se recorre la lista de compras para encontrar la compra con el ID
		// correspondiente
		for (int i = 0; i < compras.size(); i++) {
			Compra compra = compras.get(i);
			if (compra.getCompraId().compareTo(compraId) == 0) {
				return compra;
			}
		}
		return null; // Compra no encontrada
	}

	 /**
	   * Todos los productos conocidos, ordenados por productoId en orden ascendente.
	   * Notad que la lista devuelta tiene que ser nueva, es decir, no se puede
	   * devolver la lista que contiene el atributo productos dentro la clase Almacen.
	  
	 * @return Una lista nueva de productos ordenada por productoId
	 */
	@Override
	public IndexedList<Producto> getProductos() {
		// Se crea una nueva lista para devolver los productos
		IndexedList<Producto> productosOrdenados = new ArrayIndexedList<>();
		for (int i = 0; i < productos.size(); i++) {
			productosOrdenados.add(i, productos.get(i));
		}
		// Se puede hacer una ordenación si es necesario, en este caso se asume que
		// ya están ordenados
		return productosOrdenados;
	}

	/**
	 * Devuelve una lista de todas las compras realizadas.
	 *
	 * @return Una lista nueva de todas las compras
	 */
	@Override
	public IndexedList<Compra> getCompras() {
		// Se crea una nueva lista para devolver las compras{
		IndexedList<Compra> todasLasCompras = new ArrayIndexedList<>();
		for (int i = 0; i < compras.size(); i++) {
			todasLasCompras.add(i, compras.get(i));
		}
		return todasLasCompras;
	}

	/**
	 * Devuelve una lista de compras realizadas por un cliente específico.
	 *
	 * @param clienteId El identificador del cliente
	 * @return Una lista de compras hechas por el cliente
	 */
	@Override
	public IndexedList<Compra> comprasCliente(String clienteId) {
		// Se filtran las compras para devolver solo las del cliente con el ID dado
		IndexedList<Compra> comprasCliente = new ArrayIndexedList<>();
		for (int i = 0; i < compras.size(); i++) {
			Compra compra = compras.get(i);
			if (compra.getClienteId().compareTo(clienteId) == 0) {
				comprasCliente.add(comprasCliente.size(), compra);
			}
		}
		return comprasCliente;
	}

	/**
	 * Devuelve una lista de compras realizadas para un producto específico.
	 *
	 * @param productoId El identificador del producto
	 * @return Una lista de compras del producto
	 */
	@Override
	public IndexedList<Compra> comprasProducto(String productoId) {
		// Se filtran las compras para devolver solo las relacionadas con el producto
		// dado
		IndexedList<Compra> comprasProducto = new ArrayIndexedList<>();
		for (int i = 0; i < compras.size(); i++) {
			Compra compra = compras.get(i);
			if (compra.getProductoId().compareTo(productoId)==0) {
				comprasProducto.add(comprasProducto.size(), compra);
			}
		}
		return comprasProducto;
	}

	/**
	 * Realiza una compra de un producto si hay suficientes artículos disponibles.
	 * Si la compra es exitosa, se reduce el número de productos disponibles y se
	 * registra la compra.
	 *
	 * @param clienteId  El identificador del cliente
	 * @param productoId El identificador del producto
	 * @param cantidad   La cantidad de productos a comprar
	 * @return El identificador de la compra si es exitosa, o null si no hay
	 *         suficientes productos
	 */
	@Override
	public Integer pedir(String clienteId, String productoId, int cantidad) {
		// Se obtiene el producto solicitado
		Producto producto = getProducto(productoId);
		if (producto != null && producto.getCantidadDisponible() >= cantidad) {
			// Si hay suficiente cantidad, se reduce la cantidad disponible
			producto.setCantidadDisponible(producto.getCantidadDisponible() - cantidad);

			// Se crea una nueva compra y se registra
			Compra nuevaCompra = new Compra(clienteId, productoId, cantidad);
			compras.add(compras.size(), nuevaCompra);

			// Se devuelve el ID de la compra
			return nuevaCompra.getCompraId();
		}
		return null; // No se pudo procesar el pedido
	}

	/**
	 * Realiza una búsqueda binaria en la lista de productos para encontrar el
	 * índice de un producto con el productoId dado, o la posición donde debería
	 * insertarse.
	 *
	 * @param productoId El identificador del producto a buscar
	 * @return El índice del producto o la posición donde debería insertarse
	 */

	private int busquedaBinariaEnProductos(String productoId) {
		int inicio = 0;
		int fin = productos.size() - 1;

		while (inicio <= fin) {
			int medio = (inicio + fin) / 2;
			Producto producto = productos.get(medio);
			int comparacion = producto.getProductoId().compareTo(productoId);

			if (comparacion == 0) {
				return medio; // Producto encontrado
			} else if (comparacion < 0) {
				inicio = medio + 1;
			} else {
				fin = medio - 1;
			}
		}
		return inicio; // Índice donde debería insertarse
	}

}
