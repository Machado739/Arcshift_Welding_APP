package com.example.arcshiftwelding.navigation

object AppRoutes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val INVENTARIO = "inventario"

    const val NUEVO_PRODUCTO = "nuevo_producto"

    const val DETALLE_PRODUCTO = "detalle_producto/{productoId}"
    const val EDITAR_PRODUCTO = "editar_producto/{productoId}"
    const val REPONER_STOCK = "reponer_stock/{productoId}"
    const val REPORTAR_SALIDA = "reportar_salida/{productoId}"
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

    const val GASTOS = "gastos"
    const val INGRESOS = "ingresos"
    const val COTIZACIONES = "cotizaciones"
    const val CLIENTES = "clientes"
    const val EMPLEADOS = "empleados"
    const val REPORTES = "reportes"

    const val MAS = "mas"
}