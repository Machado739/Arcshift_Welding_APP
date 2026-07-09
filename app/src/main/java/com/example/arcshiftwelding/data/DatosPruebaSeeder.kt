package com.example.arcshiftwelding.data

import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.EmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.ProductoEntity

object DatosPruebaSeeder {

    suspend fun cargarDatosPrueba(database: ArcshiftWeldingDatabase) {

        val productos = listOf(
            ProductoEntity(
                id = 1,
                nombre = "PTR 2x2 Cal. 14",
                categoria = "Materiales",
                codigo = "MAT-001",
                ubicacion = "Almacén A",
                stock = 15,
                unidad = "Pieza",
                stockMinimo = 5,
                stockMaximo = 50,
                estado = "En Stock",
                precioCompra = 420.0,
                precioVenta = 550.0,
                descripcion = "Perfil tubular rectangular para estructuras metálicas.",
                proveedor = "Aceros del Norte",
                notas = "Material de prueba",
                activo = true,
                fechaRegistro = "09/07/2026"
            ),
            ProductoEntity(
                id = 2,
                nombre = "Soldadura 6013 1/8",
                categoria = "Consumibles",
                codigo = "CON-001",
                ubicacion = "Almacén B",
                stock = 25,
                unidad = "Caja",
                stockMinimo = 8,
                stockMaximo = 60,
                estado = "En Stock",
                precioCompra = 680.0,
                precioVenta = 820.0,
                descripcion = "Electrodo para trabajos generales de soldadura.",
                proveedor = "Ferretería Industrial",
                notas = "Consumible de prueba",
                activo = true,
                fechaRegistro = "09/07/2026"
            ),
            ProductoEntity(
                id = 3,
                nombre = "Disco de corte 4 1/2",
                categoria = "Herramientas",
                codigo = "HER-001",
                ubicacion = "Almacén C",
                stock = 40,
                unidad = "Pieza",
                stockMinimo = 10,
                stockMaximo = 100,
                estado = "En Stock",
                precioCompra = 32.0,
                precioVenta = 45.0,
                descripcion = "Disco de corte para metal.",
                proveedor = "Tool Center",
                notas = "Herramienta de prueba",
                activo = true,
                fechaRegistro = "09/07/2026"
            ),
            ProductoEntity(
                id = 4,
                nombre = "Guantes de carnaza",
                categoria = "Seguridad",
                codigo = "SEG-001",
                ubicacion = "Almacén C",
                stock = 12,
                unidad = "Par",
                stockMinimo = 6,
                stockMaximo = 40,
                estado = "En Stock",
                precioCompra = 95.0,
                precioVenta = 130.0,
                descripcion = "Guantes de protección para soldadura.",
                proveedor = "Seguridad Industrial MX",
                notas = "Equipo de seguridad de prueba",
                activo = true,
                fechaRegistro = "09/07/2026"
            )
        )

        val clientes = listOf(
            ClienteEntity(
                id = 1,
                nombre = "Eduardo Barrios",
                empresa = "Barrios Construcciones",
                tipoCliente = "Cliente frecuente",
                estatus = "Activo",
                telefono = "614 123 4567",
                correo = "eduardo@correo.com",
                direccion = "Col. Centro, Chihuahua",
                rfc = "BACE850101AB1",
                personaContacto = "Eduardo Barrios",
                cargo = "Propietario",
                notas = "Cliente frecuente para trabajos de herrería.",
                clienteActivo = true,
                recibeCotizaciones = true,
                contactoWhatsapp = true,
                contactoLlamadas = true,
                contactoCorreo = true,
                eliminado = false
            ),
            ClienteEntity(
                id = 2,
                nombre = "José Vera",
                empresa = "Vera Industrial",
                tipoCliente = "Empresa",
                estatus = "Activo",
                telefono = "614 987 6543",
                correo = "severa@gmail.com",
                direccion = "Av. Tecnológico #1200, Cuauhtémoc",
                rfc = "VEJO900202CD2",
                personaContacto = "José Vera",
                cargo = "Encargado",
                notas = "Solicita cotizaciones para estructuras metálicas.",
                clienteActivo = true,
                recibeCotizaciones = true,
                contactoWhatsapp = true,
                contactoLlamadas = true,
                contactoCorreo = true,
                eliminado = false
            ),
            ClienteEntity(
                id = 3,
                nombre = "María López",
                empresa = "Particular",
                tipoCliente = "Cliente general",
                estatus = "Activo",
                telefono = "614 555 1122",
                correo = "mlopez@gmail.com",
                direccion = "Calle Reforma #450, Delicias",
                rfc = "LOMA920303EF3",
                personaContacto = "María López",
                cargo = "Cliente",
                notas = "Cliente registrado para pruebas.",
                clienteActivo = true,
                recibeCotizaciones = true,
                contactoWhatsapp = true,
                contactoLlamadas = true,
                contactoCorreo = false,
                eliminado = false
            )
        )

        val empleados = listOf(
            EmpleadoEntity(
                id = 1,
                nombre = "Jaime Lozano",
                telefono = "614 111 2233",
                correo = "jaime.lozano@arcshift.com",
                puesto = "Soldador",
                salario = 550.0,
                fechaIngreso = "01/06/2026",
                activo = true,
                direccion = "Col. Obrera, Chihuahua",
                porcentajeContrato = "Pago por día",
                trabajoActual = "Portón residencial",
                notas = "Especialista en soldadura estructural."
            ),
            EmpleadoEntity(
                id = 2,
                nombre = "Carlos Mendoza",
                telefono = "614 222 3344",
                correo = "carlos.mendoza@arcshift.com",
                puesto = "Ayudante general",
                salario = 400.0,
                fechaIngreso = "05/06/2026",
                activo = true,
                direccion = "Col. Revolución, Chihuahua",
                porcentajeContrato = "Pago por día",
                trabajoActual = "Apoyo en fabricación de rejas",
                notas = "Apoya en corte, limpieza y traslado de material."
            ),
            EmpleadoEntity(
                id = 3,
                nombre = "Luis Hernández",
                telefono = "614 333 4455",
                correo = "luis.hernandez@arcshift.com",
                puesto = "Instalador",
                salario = 500.0,
                fechaIngreso = "10/06/2026",
                activo = true,
                direccion = "Col. Panamericana, Chihuahua",
                porcentajeContrato = "Pago por trabajo",
                trabajoActual = "Instalación de estructura metálica",
                notas = "Encargado de instalación en campo."
            )
        )

        database.productoDao().insertarProductos(productos)
        database.clienteDao().insertarClientes(clientes)
        database.empleadoDao().insertarEmpleados(empleados)
    }
}