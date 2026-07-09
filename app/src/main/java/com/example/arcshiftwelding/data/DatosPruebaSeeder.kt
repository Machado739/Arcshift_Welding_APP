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
                codigo = "MAT-001",
                categoria = "Materiales",
                unidadMedida = "Pieza",
                stockActual = 15,
                stockMinimo = 5,
                ubicacion = "Almacén A",
                proveedor = "Aceros del Norte",
                precioCompra = 420.0,
                precioVenta = 550.0,
                activo = true
            ),
            ProductoEntity(
                id = 2,
                nombre = "Soldadura 6013 1/8",
                codigo = "CON-001",
                categoria = "Consumibles",
                unidadMedida = "Caja",
                stockActual = 25,
                stockMinimo = 8,
                ubicacion = "Almacén B",
                proveedor = "Ferretería Industrial",
                precioCompra = 680.0,
                precioVenta = 820.0,
                activo = true
            ),
            ProductoEntity(
                id = 3,
                nombre = "Disco de corte 4 1/2",
                codigo = "HER-001",
                categoria = "Herramientas",
                unidadMedida = "Pieza",
                stockActual = 40,
                stockMinimo = 10,
                ubicacion = "Almacén C",
                proveedor = "Tool Center",
                precioCompra = 32.0,
                precioVenta = 45.0,
                activo = true
            ),
            ProductoEntity(
                id = 4,
                nombre = "Guantes de carnaza",
                codigo = "SEG-001",
                categoria = "Seguridad",
                unidadMedida = "Par",
                stockActual = 12,
                stockMinimo = 6,
                ubicacion = "Almacén C",
                proveedor = "Seguridad Industrial MX",
                precioCompra = 95.0,
                precioVenta = 130.0,
                activo = true
            )
        )

        val clientes = listOf(
            ClienteEntity(
                id = 1,
                nombre = "Eduardo Barrios",
                telefono = "614 123 4567",
                correo = "eduardo@correo.com",
                direccion = "Col. Centro, Chihuahua",
                ciudad = "Chihuahua",
                notas = "Cliente frecuente para trabajos de herrería.",
                activo = true
            ),
            ClienteEntity(
                id = 2,
                nombre = "José Vera",
                telefono = "614 987 6543",
                correo = "severa@gmail.com",
                direccion = "Av. Tecnológico #1200",
                ciudad = "Cuauhtémoc",
                notas = "Solicita cotizaciones para estructuras metálicas.",
                activo = true
            ),
            ClienteEntity(
                id = 3,
                nombre = "María López",
                telefono = "614 555 1122",
                correo = "mlopez@gmail.com",
                direccion = "Calle Reforma #450",
                ciudad = "Delicias",
                notas = "Cliente registrado para pruebas.",
                activo = true
            )
        )

        val empleados = listOf(
            EmpleadoEntity(
                id = 1,
                nombre = "Jaime Lozano",
                telefono = "614 111 2233",
                puesto = "Soldador",
                direccion = "Col. Obrera, Chihuahua",
                trabajoActual = "Portón residencial",
                notas = "Especialista en soldadura estructural.",
                activo = true
            ),
            EmpleadoEntity(
                id = 2,
                nombre = "Carlos Mendoza",
                telefono = "614 222 3344",
                puesto = "Ayudante general",
                direccion = "Col. Revolución, Chihuahua",
                trabajoActual = "Apoyo en fabricación de rejas",
                notas = "Apoya en corte, limpieza y traslado de material.",
                activo = true
            ),
            EmpleadoEntity(
                id = 3,
                nombre = "Luis Hernández",
                telefono = "614 333 4455",
                puesto = "Instalador",
                direccion = "Col. Panamericana, Chihuahua",
                trabajoActual = "Instalación de estructura metálica",
                notas = "Encargado de instalación en campo.",
                activo = true
            )
        )

        database.productoDao().insertarProductos(productos)
        database.clienteDao().insertarClientes(clientes)
        database.empleadoDao().insertarEmpleados(empleados)
    }
}