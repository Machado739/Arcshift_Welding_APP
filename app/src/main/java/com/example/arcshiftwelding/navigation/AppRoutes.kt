package com.example.arcshiftwelding.navigation

object AppRoutes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"

    const val INVENTARIO = "inventario"

    const val NUEVO_PRODUCTO = "nuevo_producto"

    const val DETALLE_PRODUCTO =
        "detalle_producto/{productoId}"

    const val EDITAR_PRODUCTO = "editar_producto"

    const val AGREGAR_STOCK = "agregar_stock"

    const val REPORTAR_SALIDA = "reportar_salida"

    fun detalleProducto(
        productoId: Int
    ) = "detalle_producto/$productoId"

    const val GASTOS = "gastos"
    const val INGRESOS = "ingresos"
    const val COTIZACIONES = "cotizaciones"
    const val CLIENTES = "clientes"
    const val EMPLEADOS = "empleados"
    const val REPORTES = "reportes"

    const val MAS = "mas"
}