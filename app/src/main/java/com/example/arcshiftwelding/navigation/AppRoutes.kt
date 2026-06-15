package com.example.arcshiftwelding.navigation

object AppRoutes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val INVENTARIO = "inventario"

    const val NUEVO_PRODUCTO = "nuevo_producto"

    const val DETALLE_PRODUCTO = "detalle_producto/{productoId}"

    // Rutas para acciones específicas en el detalle del producto
    const val EDITAR_PRODUCTO = "editar_producto/{productoId}"
    const val REPONER_STOCK = "reponer_stock/{productoId}"
    const val REPORTAR_SALIDA = "reportar_salida/{productoId}"
    const val ELIMINAR_PRODUCTO = "eliminar_producto/{productoId}"

    fun detalleProducto(productoId: Int): String {
        return "detalle_producto/$productoId"
    }
    fun editarProducto(productoId: Int): String {
        return "editar_producto/$productoId"
    }
    fun reponerStock(productoId: Int): String {
        return "reponer_stock/$productoId"
    }
    fun reportarSalida(productoId: Int): String {
        return "reportar_salida/$productoId"
    }
    fun eliminarProducto(productoId: Int): String {
        return "eliminar_producto/$productoId"
    }
    const val SELECCIONAR_PRODUCTO_REPONER = "seleccionar_producto_reponer"

    const val GASTOS = "gastos"
    const val NUEVO_GASTO = "nuevo_gasto"

    const val DETALLE_GASTO = "detalle_gasto/{gastoId}"
    const val EDITAR_GASTO = "editar_gasto/{gastoId}"
    const val ELIMINAR_GASTO = "eliminar_gasto/{gastoId}"

    fun detalleGasto(gastoId: Int): String {
        return "detalle_gasto/$gastoId"
    }

    fun editarGasto(gastoId: Int): String {
        return "editar_gasto/$gastoId"
    }

    fun eliminarGasto(gastoId: Int): String {
        return "eliminar_gasto/$gastoId"
    }


    const val INGRESOS = "ingresos"

    const val NUEVO_INGRESO = "nuevo_ingreso"

    fun detalleIngreso(ingresoId: Int): String {
        return "detalle_ingreso/$ingresoId"
    }


    const val COTIZACIONES = "cotizaciones"
    const val CLIENTES = "clientes"

    const val NUEVO_CLIENTE = "nuevo_cliente"
    const val DETALLE_CLIENTE = "detalle_cliente/{clienteId}"
    const val EDITAR_CLIENTE = "editar_cliente/{clienteId}"
    const val ELIMINAR_CLIENTE = "eliminar_cliente/{clienteId}"

    fun detalleCliente(clienteId: Int): String {
        return "detalle_cliente/$clienteId"
    }

    fun editarCliente(clienteId: Int): String {
        return "editar_cliente/$clienteId"
    }

    fun eliminarCliente(clienteId: Int): String {
        return "eliminar_cliente/$clienteId"
    }

    const val EMPLEADOS = "empleados"
    const val REPORTES = "reportes"

    const val MAS = "mas"
}