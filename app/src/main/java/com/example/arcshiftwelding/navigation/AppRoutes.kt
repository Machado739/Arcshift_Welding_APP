package com.example.arcshiftwelding.navigation

object AppRoutes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"

///                     INVENTARIO
///                     INVENTARIO
///                     INVENTARIO

    const val INVENTARIO = "inventario"
    const val NUEVO_PRODUCTO = "nuevo_producto"
    const val DETALLE_PRODUCTO = "detalle_producto/{productoId}"
    const val EDITAR_PRODUCTO = "editar_producto/{productoId}"
    const val REPONER_STOCK = "reponer_stock/{productoId}"
    const val REPORTAR_SALIDA = "reportar_salida/{productoId}"
    const val ELIMINAR_PRODUCTO = "eliminar_producto/{productoId}"
    const val SELECCIONAR_PRODUCTO_REPONER = "seleccionar_producto_reponer"

    fun detalleProducto(productoId: Int) = "detalle_producto/$productoId"
    fun editarProducto(productoId: Int) = "editar_producto/$productoId"
    fun reponerStock(productoId: Int) = "reponer_stock/$productoId"
    fun reportarSalida(productoId: Int) = "reportar_salida/$productoId"
    fun eliminarProducto(productoId: Int) = "eliminar_producto/$productoId"

///                     GASTOS
///                     GASTOS
///                     GASTOS
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

///                     INGRESOS
///                     INGRESOS
///                     INGRESOS
    const val INGRESOS = "ingresos"
    const val NUEVO_INGRESO = "nuevo_ingreso"
    const val EDITAR_INGRESO = "editar_ingreso/{ingresoId}"
    const val DETALLE_INGRESO = "detalle_ingreso/{ingresoId}"
    const val ELIMINAR_INGRESO = "eliminar_ingreso/{ingresoId}"

    fun detalleIngreso(ingresoId: Int): String {
        return "detalle_ingreso/$ingresoId"
    }
    fun editarIngreso(ingresoId: Int): String {
        return "editar_ingreso/$ingresoId"
    }
    fun eliminarIngreso(ingresoId: Int): String {
        return "eliminar_ingreso/$ingresoId"
    }




///                 CLIENTES
///                 CLIENTES
///                 CLIENTES
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

///                 COTIZACIONES
///                 COTIZACIONES
///                 COTIZACIONES

    const val COTIZACIONES = "cotizaciones"
    const val NUEVA_COTIZACION = "nueva_cotizacion"
    const val DETALLE_COTIZACION = "detalle_cotizacion/{cotizacionId}"
    const val EDITAR_COTIZACION = "editar_cotizacion/{cotizacionId}"
    const val ELIMINAR_COTIZACION = "eliminar_cotizacion/{cotizacionId}"

    fun detalleCotizacion(cotizacionId: Int): String {
        return "detalle_cotizacion/$cotizacionId"
    }

    fun editarCotizacion(cotizacionId: Int): String {
        return "editar_cotizacion/$cotizacionId"
    }

    fun eliminarCotizacion(cotizacionId: Int): String {
        return "eliminar_cotizacion/$cotizacionId"
    }



///                     EMPLEADOS
///                     EMPLEADOS
///                     EMPLEADOS
    const val EMPLEADOS = "empleados"
    const val NUEVO_EMPLEADO = "nuevo_empleado"
    const val DETALLE_EMPLEADO = "detalle_empleado/{empleadoId}"
    const val EDITAR_EMPLEADO = "editar_empleado/{empleadoId}"

    const val ELIMINAR_EMPLEADO = "eliminar_empleado/{empleadoId}"

    fun detalleEmpleado(id: Int): String {
        return "detalle_empleado/$id"
    }

    fun editarEmpleado(id: Int): String {
        return "editar_empleado/$id"
    }

    fun eliminarEmpleado(id: Int): String {
        return "eliminar_empleado/$id"
    }

///                     REPORTES
///                     REPORTES
///                     REPORTES
    const val REPORTES = "reportes"
    const val DETALLE_REPORTE = "detalle_reporte/{tipoReporte}"

    fun detalleReporte(tipoReporte: String): String {
        return "detalle_reporte/$tipoReporte"
    }

    /// PROYECTOS

    const val PROYECTOS = "proyectos"
    const val NUEVO_PROYECTO = "nuevo_proyecto"
    const val DETALLE_PROYECTO = "detalle_proyecto/{proyectoId}"
    const val EDITAR_PROYECTO = "editar_proyecto/{proyectoId}"

    const val ASIGNAR_EMPLEADO_PROYECTO = "asignar_empleado_proyecto/{proyectoId}"
    const val REGISTRAR_MATERIAL_PROYECTO = "registrar_material_proyecto/{proyectoId}"
    const val AGREGAR_COSTO_PROYECTO = "agregar_costo_proyecto/{proyectoId}"

    fun detalleProyecto(proyectoId: Int): String {
        return "detalle_proyecto/$proyectoId"
    }



    fun asignarEmpleadoProyecto(proyectoId: Int): String {
        return "asignar_empleado_proyecto/$proyectoId"
    }

    fun registrarMaterialProyecto(proyectoId: Int): String {
        return "registrar_material_proyecto/$proyectoId"
    }

    fun agregarCostoProyecto(proyectoId: Int): String {
        return "agregar_costo_proyecto/$proyectoId"
    }

    const val ELIMINAR_PROYECTO = "eliminar_proyecto/{proyectoId}"

    fun editarProyecto(proyectoId: Int): String {
        return "editar_proyecto/$proyectoId"
    }

    fun eliminarProyecto(proyectoId: Int): String {
        return "eliminar_proyecto/$proyectoId"
    }

    const val NUEVO_PROYECTO_DESDE_COTIZACION = "nuevo_proyecto_desde_cotizacion/{cotizacionId}"

    fun nuevoProyectoDesdeCotizacion(cotizacionId: Int): String {
        return "nuevo_proyecto_desde_cotizacion/$cotizacionId"
    }

    const val NUEVO_GASTO_CON_PROYECTO = "nuevo_gasto?proyectoId={proyectoId}"

    fun nuevoGastoProyecto(proyectoId: Int): String {
        return "nuevo_gasto?proyectoId=$proyectoId"
    }

    const val NUEVO_GASTO_PROYECTO = "nuevo_gasto_proyecto/{proyectoId}/{proyectoNombre}"

    fun nuevoGastoProyecto(
        proyectoId: Int,
        proyectoNombre: String
    ): String {
        return "nuevo_gasto_proyecto/$proyectoId/$proyectoNombre"
    }
///                     MAS
///                     MAS
///                     MAS
    const val MAS = "mas"
}