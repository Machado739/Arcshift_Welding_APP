package com.example.arcshiftwelding.data

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteStatement
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.security.PasswordSecurity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.random.Random

enum class PerfilDatosPrueba(
    val titulo: String,
    val descripcion: String,
    val clientes: Int,
    val empleados: Int,
    val productos: Int,
    val cotizaciones: Int,
    val proyectos: Int,
    val ingresos: Int,
    val gastos: Int,
    val pagosProgramados: Int,
    val movimientosInventario: Int,
    val empleadosProyecto: Int,
    val materialesProyecto: Int,
    val costosProyecto: Int,
    val avancesProyecto: Int
) {
    RAPIDO(
        titulo = "Prueba rápida",
        descripcion = "Datos completos para revisar pantallas y relaciones.",
        clientes = 40,
        empleados = 20,
        productos = 100,
        cotizaciones = 120,
        proyectos = 50,
        ingresos = 180,
        gastos = 160,
        pagosProgramados = 180,
        movimientosInventario = 500,
        empleadosProyecto = 100,
        materialesProyecto = 250,
        costosProyecto = 100,
        avancesProyecto = 200
    ),
    REPORTES(
        titulo = "Prueba de reportes",
        descripcion = "Histórico de dos años con miles de registros relacionados.",
        clientes = 180,
        empleados = 60,
        productos = 400,
        cotizaciones = 900,
        proyectos = 350,
        ingresos = 1_300,
        gastos = 1_100,
        pagosProgramados = 1_500,
        movimientosInventario = 4_000,
        empleadosProyecto = 900,
        materialesProyecto = 2_500,
        costosProyecto = 800,
        avancesProyecto = 1_200
    ),
    ESTRES(
        titulo = "Prueba de estrés",
        descripcion = "Carga intensiva para medir listas, filtros, Room y reportes.",
        clientes = 500,
        empleados = 150,
        productos = 1_000,
        cotizaciones = 2_500,
        proyectos = 1_000,
        ingresos = 4_500,
        gastos = 4_000,
        pagosProgramados = 5_000,
        movimientosInventario = 15_000,
        empleadosProyecto = 3_000,
        materialesProyecto = 8_000,
        costosProyecto = 2_500,
        avancesProyecto = 4_000
    );

    val detallesCotizacion: Int
        get() = cotizaciones * 3

    val totalEstimado: Int
        get() = clientes + empleados + productos + cotizaciones + detallesCotizacion +
            proyectos + ingresos + gastos + pagosProgramados + movimientosInventario +
            empleadosProyecto + materialesProyecto + costosProyecto + avancesProyecto + 9
}

data class ResumenCargaDatosPrueba(
    val perfil: PerfilDatosPrueba,
    val totalRegistros: Int,
    val duracionMs: Long,
    val avancesInsertados: Int
)

object DatosPruebaSeeder {

    private const val SEMILLA = 739_2026
    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val hoy: LocalDate
        get() = LocalDate.now()

    private val nombres = listOf(
        "Alejandro", "Andrea", "Arturo", "Beatriz", "Brenda", "Carlos", "Carolina",
        "Daniel", "Diana", "Eduardo", "Elena", "Ernesto", "Fernanda", "Francisco",
        "Gabriela", "Gerardo", "Héctor", "Isabel", "Javier", "Jessica", "Jorge",
        "José", "Laura", "Leticia", "Luis", "Manuel", "María", "Miguel", "Mónica",
        "Natalia", "Óscar", "Patricia", "Raúl", "Ricardo", "Roberto", "Rosa",
        "Sergio", "Silvia", "Sofía", "Teresa", "Valeria", "Víctor"
    )

    private val apellidos = listOf(
        "Aguilar", "Barrios", "Cabrera", "Campos", "Cárdenas", "Castillo", "Chávez",
        "Contreras", "Cruz", "Delgado", "Díaz", "Domínguez", "Escobar", "Flores",
        "García", "Gómez", "González", "Gutiérrez", "Hernández", "Herrera", "Jiménez",
        "Lara", "López", "Martínez", "Medina", "Mendoza", "Morales", "Navarro",
        "Ortega", "Pacheco", "Pérez", "Ramírez", "Reyes", "Ríos", "Rodríguez",
        "Romero", "Ruiz", "Salazar", "Sánchez", "Silva", "Torres", "Vargas", "Vega"
    )

    private val ciudades = listOf(
        "Chihuahua", "Cuauhtémoc", "Delicias", "Camargo", "Parral", "Aldama",
        "Meoqui", "Saucillo", "Casas Grandes", "Ciudad Juárez"
    )

    private val colonias = listOf(
        "Centro", "Panamericana", "San Felipe", "Revolución", "Obrera", "Las Granjas",
        "Nombre de Dios", "Quintas Carolinas", "Industrial", "Los Pinos", "Campesina",
        "Dale", "Santa Rosa", "Ávalos", "Universidad"
    )

    private val girosEmpresa = listOf(
        "Construcciones", "Servicios Industriales", "Agropecuaria", "Transportes",
        "Metalmecánica", "Desarrollos", "Mantenimiento", "Ingeniería", "Manufactura",
        "Comercializadora", "Invernaderos", "Empaques", "Alimentos", "Minería"
    )

    private val proyectosBase = listOf(
        "Portón residencial", "Nave industrial", "Estructura para tejabán",
        "Escalera metálica", "Barandal de seguridad", "Reja perimetral",
        "Mezanine de almacén", "Base para maquinaria", "Cerco de malla",
        "Puerta de acceso", "Rack industrial", "Protección para ventanas",
        "Remolque utilitario", "Techumbre metálica", "Plataforma de trabajo",
        "Mesa de acero inoxidable", "Mantenimiento de estructura", "Ductería metálica",
        "Soporte para paneles", "Caseta de vigilancia"
    )

    private val proveedores = listOf(
        "Aceros del Norte", "Perfiles Chihuahua", "Ferretería Industrial del Centro",
        "Soldaduras y Gases del Norte", "Herramientas del Desierto", "Metales del Estado",
        "Seguridad Industrial MX", "Distribuidora Técnica del Norte", "Abrasivos de Chihuahua",
        "Tornillos y Fijaciones del Centro", "Pinturas Industriales del Norte",
        "Suministros Metalmecánicos"
    )

    private data class ProductoPlantilla(
        val nombre: String,
        val categoria: String,
        val unidad: String,
        val precioBase: Double
    )

    private val productosPlantilla = listOf(
        ProductoPlantilla("PTR 1 x 1 cal. 14", "Materiales", "Pieza", 245.0),
        ProductoPlantilla("PTR 2 x 2 cal. 14", "Materiales", "Pieza", 425.0),
        ProductoPlantilla("PTR 3 x 3 cal. 11", "Materiales", "Pieza", 890.0),
        ProductoPlantilla("Ángulo 1 1/2 x 1/8", "Materiales", "Pieza", 280.0),
        ProductoPlantilla("Canal monten 4 pulgadas", "Materiales", "Pieza", 540.0),
        ProductoPlantilla("Solera 1 x 1/8", "Materiales", "Pieza", 165.0),
        ProductoPlantilla("Lámina negra cal. 14", "Materiales", "Hoja", 1_180.0),
        ProductoPlantilla("Lámina antiderrapante 1/8", "Materiales", "Hoja", 2_350.0),
        ProductoPlantilla("Tubo cédula 40 de 2 pulgadas", "Materiales", "Pieza", 1_120.0),
        ProductoPlantilla("Malla ciclónica", "Materiales", "Rollo", 2_100.0),
        ProductoPlantilla("Electrodo 6013 1/8", "Consumibles", "Caja", 690.0),
        ProductoPlantilla("Electrodo 7018 1/8", "Consumibles", "Caja", 980.0),
        ProductoPlantilla("Microalambre ER70S-6", "Consumibles", "Rollo", 1_250.0),
        ProductoPlantilla("Gas mezcla argón CO2", "Consumibles", "Cilindro", 1_650.0),
        ProductoPlantilla("Disco de corte 4 1/2", "Consumibles", "Pieza", 34.0),
        ProductoPlantilla("Disco de desbaste 4 1/2", "Consumibles", "Pieza", 48.0),
        ProductoPlantilla("Disco flap grano 80", "Consumibles", "Pieza", 72.0),
        ProductoPlantilla("Punta de contacto MIG", "Consumibles", "Pieza", 28.0),
        ProductoPlantilla("Pintura anticorrosiva", "Consumibles", "Litro", 165.0),
        ProductoPlantilla("Thinner estándar", "Consumibles", "Litro", 88.0),
        ProductoPlantilla("Esmeril angular 4 1/2", "Herramientas", "Pieza", 1_850.0),
        ProductoPlantilla("Taladro industrial 1/2", "Herramientas", "Pieza", 2_400.0),
        ProductoPlantilla("Prensa tipo C", "Herramientas", "Pieza", 390.0),
        ProductoPlantilla("Escuadra magnética", "Herramientas", "Pieza", 185.0),
        ProductoPlantilla("Pinza de tierra", "Herramientas", "Pieza", 320.0),
        ProductoPlantilla("Careta electrónica", "Seguridad", "Pieza", 1_450.0),
        ProductoPlantilla("Guantes de carnaza", "Seguridad", "Par", 110.0),
        ProductoPlantilla("Mandil de carnaza", "Seguridad", "Pieza", 460.0),
        ProductoPlantilla("Lentes de seguridad", "Seguridad", "Pieza", 75.0),
        ProductoPlantilla("Respirador para humos", "Seguridad", "Pieza", 390.0)
    )

    suspend fun cargarDatosPrueba(
        database: ArcshiftWeldingDatabase,
        perfil: PerfilDatosPrueba = PerfilDatosPrueba.REPORTES,
        reemplazarDatosExistentes: Boolean = true,
        onProgreso: (porcentaje: Int, etapa: String) -> Unit = { _, _ -> }
    ): ResumenCargaDatosPrueba = withContext(Dispatchers.IO) {
        val inicio = System.currentTimeMillis()
        val random = Random(SEMILLA + perfil.ordinal)

        onProgreso(1, "Preparando base de datos")
        if (reemplazarDatosExistentes) {
            database.clearAllTables()
        }

        val db = database.openHelper.writableDatabase
        var avancesInsertados = 0

        db.beginTransaction()
        try {
            insertarConfiguracionBase(db)
            onProgreso(4, "Configuración y usuarios")

            insertarClientes(db, perfil.clientes, random)
            onProgreso(10, "${perfil.clientes} clientes")

            insertarEmpleados(db, perfil.empleados, random)
            onProgreso(15, "${perfil.empleados} empleados")

            insertarProductos(db, perfil.productos, random)
            onProgreso(23, "${perfil.productos} productos")

            insertarCotizaciones(db, perfil.cotizaciones, perfil.clientes)
            insertarDetallesCotizacion(db, perfil.cotizaciones)
            onProgreso(34, "Cotizaciones y conceptos")

            insertarProyectos(db, perfil.proyectos, perfil.cotizaciones, perfil.clientes)
            onProgreso(42, "${perfil.proyectos} proyectos")

            insertarIngresos(db, perfil.ingresos, perfil.proyectos, perfil.cotizaciones, perfil.clientes)
            onProgreso(51, "${perfil.ingresos} ingresos")

            insertarPagosProgramados(
                db = db,
                cantidad = perfil.pagosProgramados,
                proyectos = perfil.proyectos,
                ingresos = perfil.ingresos,
                cotizaciones = perfil.cotizaciones,
                clientes = perfil.clientes
            )
            onProgreso(59, "Pagos programados")

            insertarGastos(
                db = db,
                cantidad = perfil.gastos,
                proyectos = perfil.proyectos,
                cotizaciones = perfil.cotizaciones,
                clientes = perfil.clientes,
                random = random
            )
            onProgreso(67, "${perfil.gastos} gastos")

            insertarEmpleadosProyecto(
                db,
                perfil.empleadosProyecto,
                perfil.proyectos,
                perfil.empleados,
                perfil.cotizaciones
            )
            actualizarTrabajoActualEmpleados(db)
            onProgreso(73, "Asignaciones de empleados")

            insertarMaterialesProyecto(
                db,
                perfil.materialesProyecto,
                perfil.proyectos,
                perfil.productos
            )
            onProgreso(79, "Materiales usados")

            insertarCostosProyecto(db, perfil.costosProyecto, perfil.proyectos, random)
            onProgreso(84, "Costos adicionales")

            insertarMovimientosInventario(
                db,
                perfil.movimientosInventario,
                perfil.productos,
                perfil.cotizaciones,
                perfil.clientes
            )
            onProgreso(93, "Movimientos de inventario")

            if (db.existeTabla("proyecto_avances")) {
                insertarAvancesProyecto(db, perfil.avancesProyecto, perfil.proyectos)
                avancesInsertados = perfil.avancesProyecto
            }
            onProgreso(98, "Historiales de avance")

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        onProgreso(100, "Carga terminada")

        ResumenCargaDatosPrueba(
            perfil = perfil,
            totalRegistros = perfil.totalEstimado - perfil.avancesProyecto + avancesInsertados,
            duracionMs = System.currentTimeMillis() - inicio,
            avancesInsertados = avancesInsertados
        )
    }

    private fun insertarConfiguracionBase(db: SupportSQLiteDatabase) {
        db.ejecutarPreparado(
            """
            INSERT OR REPLACE INTO empresa
            (id, nombre, telefono, correo, direccion, logo)
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
        ) { statement ->
            statement.bindLong(1, 1)
            statement.bindString(2, "ARCSHIFT WELDING")
            statement.bindString(3, "614 555 0100")
            statement.bindString(4, "contacto@arcshiftwelding.test")
            statement.bindString(5, "Chihuahua, Chihuahua")
            statement.bindString(6, "")
            statement.executeInsert()
        }

        db.ejecutarPreparado(
            """
            INSERT OR REPLACE INTO usuarios
            (id, nombre, usuario, password, rol)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
        ) { statement ->
            val usuarios = listOf(
                arrayOf("Administrador de pruebas", "admin", "admin123", "Administrador"),
                arrayOf("Supervisor de taller", "supervisor", "super123", "Supervisor"),
                arrayOf("Capturista de pruebas", "captura", "captura123", "Capturista")
            )
            usuarios.forEachIndexed { index, datos ->
                statement.clearBindings()
                statement.bindLong(1, (index + 1).toLong())
                statement.bindString(2, datos[0])
                statement.bindString(3, datos[1])
                statement.bindString(4, PasswordSecurity.hashPassword(datos[2]))
                statement.bindString(5, datos[3])
                statement.executeInsert()
            }
        }

        db.ejecutarPreparado(
            "INSERT OR REPLACE INTO categorias_producto (id, nombre) VALUES (?, ?)"
        ) { statement ->
            listOf("Materiales", "Consumibles", "Herramientas", "Seguridad", "Otros")
                .forEachIndexed { index, categoria ->
                    statement.clearBindings()
                    statement.bindLong(1, (index + 1).toLong())
                    statement.bindString(2, categoria)
                    statement.executeInsert()
                }
        }
    }

    private fun insertarClientes(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        random: Random
    ) {
        val sql = """
            INSERT OR REPLACE INTO clientes
            (id, nombre, empresa, tipoCliente, estatus, telefono, correo, direccion, rfc,
             personaContacto, cargo, notas, fotoUri, clienteActivo, recibeCotizaciones,
             contactoWhatsapp, contactoLlamadas, contactoCorreo, fechaRegistro,
             ultimaActualizacion, eliminado)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val nombre = nombrePersona(id)
                val esEmpresa = id % 4 != 0
                val empresa = if (esEmpresa) {
                    "${apellidos[(id * 3) % apellidos.size]} ${girosEmpresa[id % girosEmpresa.size]}"
                } else {
                    "Particular"
                }
                val estatus = when {
                    id % 29 == 0 -> "Pendiente"
                    id % 17 == 0 -> "Inactivo"
                    else -> "Activo"
                }
                val activo = estatus == "Activo"
                val fecha = fechaHistorica(id, 720)
                val fechaMillis = fecha.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindString(2, nombre)
                statement.bindString(3, empresa)
                statement.bindString(4, if (esEmpresa) "Empresa" else "Cliente general")
                statement.bindString(5, estatus)
                statement.bindString(6, telefono(id))
                statement.bindString(7, correo(nombre, id))
                statement.bindString(8, direccion(id))
                statement.bindString(9, rfcSintetico(id))
                statement.bindString(10, nombrePersona(id + 7))
                statement.bindString(11, if (esEmpresa) cargos[id % cargos.size] else "Cliente")
                statement.bindString(
                    12,
                    "Registro sintético para pruebas. Preferencia de contacto: " +
                        listOf("llamada", "correo", "WhatsApp")[id % 3] + "."
                )
                statement.bindString(13, "")
                statement.bindLong(14, activo.toSqlLong())
                statement.bindLong(15, (id % 11 != 0).toSqlLong())
                statement.bindLong(16, (id % 5 != 0).toSqlLong())
                statement.bindLong(17, (id % 7 != 0).toSqlLong())
                statement.bindLong(18, (id % 3 != 0).toSqlLong())
                statement.bindLong(19, fechaMillis)
                statement.bindLong(20, fechaMillis + random.nextLong(0, 30L * 86_400_000L))
                statement.bindLong(21, false.toSqlLong())
                statement.executeInsert()
            }
        }
    }

    private val cargos = listOf(
        "Propietario", "Gerente de mantenimiento", "Compras", "Supervisor de obra",
        "Administrador", "Jefe de planta", "Encargado de proyecto"
    )

    private fun insertarEmpleados(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        random: Random
    ) {
        val sql = """
            INSERT OR REPLACE INTO empleados
            (id, nombre, telefono, correo, puesto, salario, fechaIngreso, activo, tipoPago,
             direccion, porcentajeContrato, trabajoActual, notas, fotoUri)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val tipoPago = when (id % 5) {
                    0 -> "Porcentaje"
                    1, 2, 3 -> "Día"
                    else -> "Semana"
                }
                val salario = when (tipoPago) {
                    "Porcentaje" -> (5 + id % 16).toDouble()
                    "Semana" -> 2_600.0 + (id % 18) * 180.0
                    else -> 380.0 + (id % 16) * 35.0
                }
                val activo = id % 23 != 0
                val nombre = nombrePersona(id + 1000)

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindString(2, nombre)
                statement.bindString(3, telefono(id + 1000))
                statement.bindString(4, correo(nombre, id + 1000))
                statement.bindString(5, puestos[id % puestos.size])
                statement.bindDouble(6, salario)
                statement.bindString(7, fechaHistorica(id + 17, 1_200).formatear())
                statement.bindLong(8, activo.toSqlLong())
                statement.bindString(9, tipoPago)
                statement.bindString(10, direccion(id + 1000))
                statement.bindString(11, if (tipoPago == "Porcentaje") "${salario.toInt()}%" else "")
                statement.bindString(
                    12,
                    if (id % 4 == 0) proyectosBase[id % proyectosBase.size] else "Disponible"
                )
                statement.bindString(
                    13,
                    "Empleado sintético. Experiencia estimada: ${1 + id % 18} años. " +
                        "Evaluación de prueba ${random.nextInt(70, 101)}/100."
                )
                statement.bindString(14, "")
                statement.executeInsert()
            }
        }
    }

    private val puestos = listOf(
        "Soldador estructural", "Soldador MIG", "Soldador TIG", "Ayudante general",
        "Instalador", "Cortador", "Pintor industrial", "Supervisor de taller",
        "Diseñador", "Operador de plasma"
    )

    private fun insertarProductos(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        random: Random
    ) {
        val sql = """
            INSERT OR REPLACE INTO productos
            (id, nombre, categoria, codigo, ubicacion, stock, unidad, stockMinimo,
             stockMaximo, estado, precioCompra, precioVenta, descripcion, proveedor,
             notas, imagenUri, permitirStockNegativo, activo, fechaRegistro)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val consecutivos = mutableMapOf<String, Int>()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val plantilla = productosPlantilla[(id - 1) % productosPlantilla.size]
                val prefijo = when (plantilla.categoria) {
                    "Materiales" -> "MAT"
                    "Consumibles" -> "CON"
                    "Herramientas" -> "HER"
                    "Seguridad" -> "SEG"
                    else -> "OTR"
                }
                val consecutivo = (consecutivos[prefijo] ?: 0) + 1
                consecutivos[prefijo] = consecutivo
                val stockMinimo = 4 + id % 17
                val stockMaximo = stockMinimo + 40 + id % 160
                val stock = when {
                    id % 20 == 0 -> 0
                    id % 7 == 0 -> max(1, stockMinimo / 2)
                    else -> random.nextInt(stockMinimo + 1, stockMaximo + 1)
                }
                val estado = when {
                    stock == 0 -> "Agotado"
                    stock <= stockMinimo -> "Bajo Stock"
                    else -> "En Stock"
                }
                val factor = 0.82 + ((id * 13) % 35) / 100.0
                val compra = dinero(plantilla.precioBase * factor)
                val venta = dinero(compra * (1.18 + (id % 12) / 100.0))
                val lote = 1 + (id - 1) / productosPlantilla.size

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindString(2, "${plantilla.nombre} · variante $lote")
                statement.bindString(3, plantilla.categoria)
                statement.bindString(4, "$prefijo-${consecutivo.toString().padStart(4, '0')}")
                statement.bindString(5, "Almacén ${('A'.code + id % 5).toChar()} / Estante ${1 + id % 12}")
                statement.bindLong(6, stock.toLong())
                statement.bindString(7, plantilla.unidad)
                statement.bindLong(8, stockMinimo.toLong())
                statement.bindLong(9, stockMaximo.toLong())
                statement.bindString(10, estado)
                statement.bindDouble(11, compra)
                statement.bindDouble(12, venta)
                statement.bindString(13, "Producto sintético de ${plantilla.categoria.lowercase()} para pruebas de inventario.")
                statement.bindString(14, proveedores[id % proveedores.size])
                statement.bindString(15, "Lote de prueba $lote. Rotación ${listOf("alta", "media", "baja")[id % 3]}.")
                statement.bindString(16, "")
                statement.bindLong(17, false.toSqlLong())
                statement.bindLong(18, (id % 37 != 0).toSqlLong())
                statement.bindString(19, fechaHistorica(id + 41, 800).formatear())
                statement.executeInsert()
            }
        }
    }

    private fun insertarCotizaciones(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        clientes: Int
    ) {
        val sql = """
            INSERT OR REPLACE INTO cotizaciones
            (id, folio, clienteId, descripcionTrabajo, proyecto, subtotal,
             descuentoPorcentaje, descuento, ivaPorcentaje, iva, total,
             anticipoPorcentaje, anticipo, saldo, fecha, vigencia, observaciones,
             estado, fechaAprobacion, fechaActualizacion, archivosAdjuntosJson)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val clienteId = clienteIdDeCotizacion(id, clientes)
                val estado = estadoCotizacion(id)
                val fecha = fechaCotizacion(id, estado)
                val vigencia = vigenciaCotizacion(id, estado, fecha)
                val montos = montosCotizacion(id)
                val proyecto = nombreProyecto(id)

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindString(2, "COT-${id.toString().padStart(5, '0')}")
                statement.bindLong(3, clienteId.toLong())
                statement.bindString(4, descripcionProyecto(id))
                statement.bindString(5, proyecto)
                val fechaActualizacion = fecha.plusDays((id % 9).toLong()).formatear()
                val fechaAprobacion = if (estado == "Aprobada") {
                    fecha.plusDays((1 + id % 8).toLong()).formatear()
                } else {
                    ""
                }

                statement.bindDouble(6, montos.subtotal)
                statement.bindDouble(7, montos.descuentoPorcentaje)
                statement.bindDouble(8, montos.descuento)
                statement.bindDouble(9, 16.0)
                statement.bindDouble(10, montos.iva)
                statement.bindDouble(11, montos.total)
                statement.bindDouble(12, montos.anticipoPorcentaje)
                statement.bindDouble(13, montos.anticipo)
                statement.bindDouble(14, montos.saldo)
                statement.bindString(15, fecha.formatear())
                statement.bindString(16, vigencia.formatear())
                statement.bindString(
                    17,
                    if (id % 6 == 0) {
                        "Incluye fabricación, acabado e instalación. No incluye obra civil."
                    } else {
                        "Precios sujetos a disponibilidad de materiales."
                    }
                )
                statement.bindString(18, estado)
                statement.bindString(19, fechaAprobacion)
                statement.bindString(20, fechaActualizacion)
                statement.bindString(21, "[]")
                statement.executeInsert()
            }
        }
    }

    private data class Montos(
        val subtotal: Double,
        val descuentoPorcentaje: Double,
        val descuento: Double,
        val iva: Double,
        val total: Double,
        val anticipoPorcentaje: Double,
        val anticipo: Double,
        val saldo: Double
    )

    private fun montosCotizacion(id: Int): Montos {
        val subtotal = dinero(3_500.0 + ((id * 791L) % 74_500L))
        val descuentoPorcentaje = if (id % 9 == 0) 5.0 else 0.0
        val descuento = dinero(subtotal * descuentoPorcentaje / 100.0)
        val baseIva = dinero(subtotal - descuento)
        val iva = dinero(baseIva * 0.16)
        val total = dinero(baseIva + iva)
        val anticipoPorcentaje = listOf(30.0, 40.0, 50.0)[id % 3]
        val anticipo = dinero(total * anticipoPorcentaje / 100.0)
        val saldo = dinero(total - anticipo)

        return Montos(
            subtotal = subtotal,
            descuentoPorcentaje = descuentoPorcentaje,
            descuento = descuento,
            iva = iva,
            total = total,
            anticipoPorcentaje = anticipoPorcentaje,
            anticipo = anticipo,
            saldo = saldo
        )
    }

    private fun insertarDetallesCotizacion(db: SupportSQLiteDatabase, cotizaciones: Int) {
        val sql = """
            INSERT OR REPLACE INTO detalle_cotizacion
            (id, cotizacionId, tipo, descripcion, cantidad, unidad, precioUnitario, total)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            var detalleId = 1
            for (cotizacionId in 1..cotizaciones) {
                val subtotal = montosCotizacion(cotizacionId).subtotal
                val partes = listOf(0.46, 0.34, 0.20)
                val tipos = listOf("Materiales", "Mano de obra", "Gastos adicionales")
                val descripciones = listOf(
                    "Materiales para ${nombreProyecto(cotizacionId).lowercase()}",
                    "Fabricación, soldadura e instalación",
                    "Transporte, consumibles y acabado"
                )
                partes.forEachIndexed { index, proporcion ->
                    val totalDetalle = if (index == 2) {
                        dinero(subtotal - dinero(subtotal * partes[0]) - dinero(subtotal * partes[1]))
                    } else {
                        dinero(subtotal * proporcion)
                    }
                    val cantidad = when (index) {
                        0 -> (2 + cotizacionId % 12).toDouble()
                        1 -> (8 + cotizacionId % 45).toDouble()
                        else -> 1.0
                    }
                    val unidad = when (index) {
                        0 -> "Pza"
                        1 -> "Hora"
                        else -> "Servicio"
                    }

                    statement.clearBindings()
                    statement.bindLong(1, detalleId++.toLong())
                    statement.bindLong(2, cotizacionId.toLong())
                    statement.bindString(3, tipos[index])
                    statement.bindString(4, descripciones[index])
                    statement.bindDouble(5, cantidad)
                    statement.bindString(6, unidad)
                    statement.bindDouble(7, dinero(totalDetalle / cantidad))
                    statement.bindDouble(8, totalDetalle)
                    statement.executeInsert()
                }
            }
        }
    }

    private fun insertarProyectos(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        cotizaciones: Int,
        clientes: Int
    ) {
        val sql = """
            INSERT OR REPLACE INTO proyectos
            (id, nombre, clienteId, cotizacionId, descripcion, estado, fechaInicio,
             fechaEstimadaFin, fechaFinReal, avance, presupuestoEstimado, costoMaterial,
             costoManoObra, costoTotal, observaciones, imagenesJson)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val cotizacionId = obtenerCotizacionAprobada(id, cotizaciones)
                val clienteId = clienteIdDeCotizacion(cotizacionId, clientes)
                val estado = estadoProyecto(id)
                val avance = avanceProyecto(id, estado)
                val inicio = fechaHistorica(id + 91, 650)
                val duracion = 12L + (id % 80)
                val finEstimada = inicio.plusDays(duracion)
                val finReal = if (estado == "Terminado") {
                    finEstimada.plusDays(((id % 11) - 5).toLong())
                } else {
                    null
                }
                val presupuesto = montosCotizacion(cotizacionId).total
                val costoMaterial = dinero(presupuesto * (0.28 + (id % 17) / 100.0))
                val costoManoObra = dinero(presupuesto * (0.18 + (id % 13) / 100.0))
                val costoTotal = dinero(costoMaterial + costoManoObra + presupuesto * 0.06)

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindString(2, "${nombreProyecto(id)} #${id.toString().padStart(4, '0')}")
                statement.bindLong(3, clienteId.toLong())
                statement.bindLong(4, cotizacionId.toLong())
                statement.bindString(5, descripcionProyecto(id))
                statement.bindString(6, estado)
                statement.bindString(7, inicio.formatear())
                statement.bindString(8, finEstimada.formatear())
                statement.bindString(9, finReal?.formatear().orEmpty())
                statement.bindLong(10, avance.toLong())
                statement.bindDouble(11, presupuesto)
                statement.bindDouble(12, costoMaterial)
                statement.bindDouble(13, costoManoObra)
                statement.bindDouble(14, costoTotal)
                statement.bindString(
                    15,
                    "Proyecto sintético para pruebas. Prioridad ${listOf("alta", "media", "normal")[id % 3]}."
                )
                statement.bindString(16, "[]")
                statement.executeInsert()
            }
        }
    }

    private fun insertarIngresos(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        proyectos: Int,
        cotizaciones: Int,
        clientes: Int
    ) {
        val sql = """
            INSERT OR REPLACE INTO ingresos
            (id, concepto, clienteId, cotizacionId, proyectoId, trabajo, folio,
             comprobanteUri, tipoComprobante, fecha, subtotal, ivaPorcentaje, iva,
             total, montoTotalProyecto, anticipo, pendiente, metodoPago, formaPago,
             observaciones, ordenTrabajo, proyecto, activo, comprobantesJson)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val proyectoId = 1 + (id - 1) % proyectos
                val cotizacionId = obtenerCotizacionAprobada(proyectoId, cotizaciones)
                val clienteId = clienteIdDeCotizacion(cotizacionId, clientes)
                val presupuesto = montosCotizacion(cotizacionId).total
                val porcentajePago = 0.08 + (id % 18) / 100.0
                val total = dinero(min(presupuesto, presupuesto * porcentajePago))
                val subtotal = dinero(total / 1.16)
                val iva = dinero(total - subtotal)
                val esAnticipo = id <= proyectos || id % 9 == 0
                val pendiente = dinero(max(0.0, presupuesto - total))

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindString(2, if (esAnticipo) "Anticipo de proyecto" else "Abono de proyecto")
                statement.bindLong(3, clienteId.toLong())
                statement.bindLong(4, cotizacionId.toLong())
                statement.bindLong(5, proyectoId.toLong())
                statement.bindString(6, nombreProyecto(proyectoId))
                statement.bindString(7, "ING-${id.toString().padStart(6, '0')}")
                statement.bindString(8, "")
                statement.bindString(9, "")
                statement.bindString(10, fechaHistorica(id + 121, 730).formatear())
                statement.bindDouble(11, subtotal)
                statement.bindDouble(12, 16.0)
                statement.bindDouble(13, iva)
                statement.bindDouble(14, total)
                statement.bindDouble(15, presupuesto)
                statement.bindDouble(16, if (esAnticipo) total else 0.0)
                statement.bindDouble(17, pendiente)
                statement.bindString(18, metodosPago[id % metodosPago.size])
                statement.bindString(19, if (id % 4 == 0) "Parcialidad" else "Una sola exhibición")
                statement.bindString(20, "Ingreso sintético relacionado con proyecto $proyectoId.")
                statement.bindString(21, "OT-${proyectoId.toString().padStart(5, '0')}")
                statement.bindString(22, "${nombreProyecto(proyectoId)} #${proyectoId.toString().padStart(4, '0')}")
                statement.bindLong(23, (id % 71 != 0).toSqlLong())
                statement.bindString(24, "[]")
                statement.executeInsert()
            }
        }
    }

    private val metodosPago = listOf(
        "Transferencia", "Efectivo", "Tarjeta", "Cheque", "Depósito"
    )

    private fun insertarPagosProgramados(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        proyectos: Int,
        ingresos: Int,
        cotizaciones: Int,
        clientes: Int
    ) {
        val sql = """
            INSERT OR REPLACE INTO pagos_programados
            (id, proyectoId, clienteId, ingresoAnticipoId, ingresoPagadoId,
             fechaProgramada, montoProgramado, estado, observaciones, fechaRegistro,
             fechaPago, montoPagado, metodoPago, comprobanteUri, tipoComprobante, activo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val proyectoId = 1 + (id - 1) % proyectos
                val cotizacionId = obtenerCotizacionAprobada(proyectoId, cotizaciones)
                val clienteId = clienteIdDeCotizacion(cotizacionId, clientes)
                val ingresoId = 1 + (id - 1) % ingresos
                val estado = if (id % 10 < 6) "Pendiente" else "Pagado"
                val fechaProgramada = when {
                    estado == "Pendiente" && id % 8 == 0 -> hoy.plusDays((id % 7).toLong())
                    estado == "Pendiente" && id % 11 == 0 -> hoy.minusDays((1 + id % 20).toLong())
                    else -> fechaHistorica(id + 201, 500).plusDays((id % 180).toLong())
                }
                val presupuesto = montosCotizacion(cotizacionId).total
                val monto = dinero(presupuesto * (0.08 + (id % 20) / 100.0))
                val fechaPago = if (estado == "Pagado") {
                    fechaProgramada.plusDays(((id % 7) - 3).toLong()).formatear()
                } else {
                    ""
                }

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindLong(2, proyectoId.toLong())
                statement.bindLong(3, clienteId.toLong())
                statement.bindLong(4, ingresoId.toLong())
                if (estado == "Pagado") statement.bindLong(5, ingresoId.toLong()) else statement.bindNull(5)
                statement.bindString(6, fechaProgramada.formatear())
                statement.bindDouble(7, monto)
                statement.bindString(8, estado)
                statement.bindString(9, "Pago sintético ${1 + id % 6} del proyecto $proyectoId.")
                statement.bindString(10, fechaProgramada.minusDays((7 + id % 40).toLong()).formatear())
                statement.bindString(11, fechaPago)
                statement.bindDouble(12, if (estado == "Pagado") monto else 0.0)
                statement.bindString(13, if (estado == "Pagado") metodosPago[id % metodosPago.size] else "")
                statement.bindString(14, "")
                statement.bindString(15, "")
                statement.bindLong(16, (id % 97 != 0).toSqlLong())
                statement.executeInsert()
            }
        }
    }

    private fun insertarGastos(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        proyectos: Int,
        cotizaciones: Int,
        clientes: Int,
        random: Random
    ) {
        val sql = """
            INSERT OR REPLACE INTO gastos
            (id, proyectoId, proyectoNombre, concepto, categoria, fecha, proveedor,
             subtotal, ivaPorcentaje, iva, total, metodoPago, formaPago,
             telefonoProveedor, correoProveedor, rfcProveedor, observaciones,
             proyecto, clienteId, cotizacionId, comprobanteUri, tipoComprobante,
             nombreComprobante, comprobantesJson)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val proyectoId = 1 + (id - 1) % proyectos
                val cotizacionId = obtenerCotizacionAprobada(proyectoId, cotizaciones)
                val clienteId = clienteIdDeCotizacion(cotizacionId, clientes)
                val categoria = categoriasGasto[id % categoriasGasto.size]
                val concepto = conceptosGasto[id % conceptosGasto.size]
                val subtotal = dinero(90.0 + random.nextDouble() * 18_000.0)
                val ivaPorcentaje = if (id % 5 == 0) 0.0 else 16.0
                val iva = dinero(subtotal * ivaPorcentaje / 100.0)
                val total = dinero(subtotal + iva)
                val proveedor = proveedores[id % proveedores.size]
                val nombreProyecto = "${nombreProyecto(proyectoId)} #${proyectoId.toString().padStart(4, '0')}"

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindLong(2, proyectoId.toLong())
                statement.bindString(3, nombreProyecto)
                statement.bindString(4, concepto)
                statement.bindString(5, categoria)
                statement.bindString(6, fechaHistorica(id + 271, 730).formatear())
                statement.bindString(7, proveedor)
                statement.bindDouble(8, subtotal)
                statement.bindDouble(9, ivaPorcentaje)
                statement.bindDouble(10, iva)
                statement.bindDouble(11, total)
                statement.bindString(12, metodosPago[id % metodosPago.size])
                statement.bindString(13, if (id % 4 == 0) "Crédito" else "Contado")
                statement.bindString(14, telefono(id + 5000))
                statement.bindString(15, "ventas${id % 50}@proveedor.test")
                statement.bindString(16, "PRV${id.toString().padStart(7, '0')}T1")
                statement.bindString(17, "Gasto sintético para validar filtros, totales y reportes.")
                statement.bindString(18, nombreProyecto)
                statement.bindLong(19, clienteId.toLong())
                statement.bindLong(20, cotizacionId.toLong())
                statement.bindString(21, "")
                statement.bindString(22, "")
                statement.bindString(23, "")
                statement.bindString(24, "[]")
                statement.executeInsert()
            }
        }
    }

    private val categoriasGasto = listOf(
        "Materiales", "Consumibles", "Herramientas", "Transporte", "Servicios",
        "Mantenimiento", "Viáticos", "Renta", "Otros"
    )

    private val conceptosGasto = listOf(
        "Compra de perfil y lámina", "Compra de electrodos", "Recarga de cilindro",
        "Discos de corte y desbaste", "Pintura y solventes", "Flete de materiales",
        "Renta de plataforma", "Mantenimiento de soldadora", "Combustible",
        "Equipo de protección", "Tornillería y fijaciones", "Servicio de galvanizado"
    )

    private fun insertarEmpleadosProyecto(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        proyectos: Int,
        empleados: Int,
        cotizaciones: Int
    ) {
        val sql = """
            INSERT OR REPLACE INTO proyecto_empleados
            (id, proyectoId, empleadoId, nombreEmpleado, puesto, tipoPago, pagoAcordado,
             diasTrabajados, horasTrabajadas, porcentaje, costoCalculado,
             fechaAsignacion, estado, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val proyectoId = 1 + (id - 1) % proyectos
                val empleadoId = 1 + ((proyectoId * 11 + id * 3) % empleados)
                val tipoPago = when (empleadoId % 5) {
                    0 -> "Porcentaje"
                    4 -> "Semana"
                    else -> "Día"
                }
                val dias = 1.0 + id % 25
                val horas = dias * (7.0 + id % 3)
                val porcentaje = if (tipoPago == "Porcentaje") (5 + empleadoId % 16).toDouble() else 0.0
                val pagoAcordado = when (tipoPago) {
                    "Porcentaje" -> porcentaje
                    "Semana" -> 2_600.0 + (empleadoId % 18) * 180.0
                    else -> 380.0 + (empleadoId % 16) * 35.0
                }
                val costo = when (tipoPago) {
                    "Porcentaje" -> dinero(montosCotizacion(obtenerCotizacionAprobada(proyectoId, cotizaciones)).total * porcentaje / 100.0)
                    "Semana" -> dinero((dias / 7.0) * pagoAcordado)
                    else -> dinero(dias * pagoAcordado)
                }

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindLong(2, proyectoId.toLong())
                statement.bindLong(3, empleadoId.toLong())
                statement.bindString(4, nombrePersona(empleadoId + 1000))
                statement.bindString(5, puestos[empleadoId % puestos.size])
                statement.bindString(6, tipoPago)
                statement.bindDouble(7, pagoAcordado)
                statement.bindDouble(8, dias)
                statement.bindDouble(9, horas)
                statement.bindDouble(10, porcentaje)
                statement.bindDouble(11, costo)
                statement.bindString(12, fechaHistorica(id + 331, 500).formatear())
                statement.bindString(13, if (id % 13 == 0) "Finalizado" else "Asignado")
                statement.bindString(14, "Asignación sintética para validación de costos de mano de obra.")
                statement.executeInsert()
            }
        }
    }

    private fun actualizarTrabajoActualEmpleados(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            UPDATE empleados
            SET trabajoActual = COALESCE(
                (
                    SELECT p.nombre
                    FROM proyecto_empleados pe
                    INNER JOIN proyectos p ON p.id = pe.proyectoId
                    WHERE pe.empleadoId = empleados.id
                      AND pe.estado = 'Asignado'
                      AND p.estado IN ('Pendiente', 'En trabajo')
                    ORDER BY p.id DESC
                    LIMIT 1
                ),
                'Disponible'
            )
            """.trimIndent()
        )
    }

    private fun insertarMaterialesProyecto(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        proyectos: Int,
        productos: Int
    ) {
        val sql = """
            INSERT OR REPLACE INTO proyecto_materiales
            (id, proyectoId, productoId, nombreProducto, codigoProducto, categoria,
             cantidadUsada, unidad, costoUnitario, subtotal, fechaUso, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val proyectoId = 1 + (id - 1) % proyectos
                val productoId = 1 + ((id * 17 + proyectoId * 5) % productos)
                val plantilla = productosPlantilla[(productoId - 1) % productosPlantilla.size]
                val codigo = codigoProducto(productoId)
                val cantidadUsada = when (plantilla.unidad) {
                    "Litro" -> 1.0 + id % 12
                    "Caja", "Rollo", "Cilindro", "Hoja" -> 1.0 + id % 4
                    else -> 1.0 + id % 18
                }
                val costoUnitario = dinero(plantilla.precioBase * (0.82 + (productoId % 35) / 100.0))
                val subtotal = dinero(cantidadUsada * costoUnitario)

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindLong(2, proyectoId.toLong())
                statement.bindLong(3, productoId.toLong())
                statement.bindString(4, "${plantilla.nombre} · variante ${1 + (productoId - 1) / productosPlantilla.size}")
                statement.bindString(5, codigo)
                statement.bindString(6, plantilla.categoria)
                statement.bindDouble(7, cantidadUsada)
                statement.bindString(8, plantilla.unidad)
                statement.bindDouble(9, costoUnitario)
                statement.bindDouble(10, subtotal)
                statement.bindString(11, fechaHistorica(id + 381, 500).formatear())
                statement.bindString(12, "Salida sintética asociada al proyecto $proyectoId.")
                statement.executeInsert()
            }
        }
    }

    private fun insertarCostosProyecto(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        proyectos: Int,
        random: Random
    ) {
        val sql = """
            INSERT OR REPLACE INTO proyecto_costos
            (id, proyectoId, tipo, descripcion, monto, fecha, comprobanteUri, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val tipos = listOf("Transporte", "Servicio", "Herramienta", "Viáticos", "Otro")

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val proyectoId = 1 + (id - 1) % proyectos
                val tipo = tipos[id % tipos.size]
                val monto = dinero(120.0 + random.nextDouble() * 8_500.0)

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindLong(2, proyectoId.toLong())
                statement.bindString(3, tipo)
                statement.bindString(4, "${tipo} adicional del proyecto")
                statement.bindDouble(5, monto)
                statement.bindString(6, fechaHistorica(id + 431, 500).formatear())
                statement.bindNull(7)
                statement.bindString(8, "Costo sintético para análisis de rentabilidad.")
                statement.executeInsert()
            }
        }
    }

    private fun insertarMovimientosInventario(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        productos: Int,
        cotizaciones: Int,
        clientes: Int
    ) {
        val sql = """
            INSERT OR REPLACE INTO movimientos_inventario
            (id, productoId, cotizacionId, clienteId, tipo, cantidad, stockAnterior,
             stockNuevo, unidad, fecha, hora, usuario, referencia, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val productoId = 1 + (id - 1) % productos
                val cotizacionId = 1 + (id - 1) % cotizaciones
                val clienteId = clienteIdDeCotizacion(cotizacionId, clientes)
                val tipo = when (id % 10) {
                    0 -> "Ajuste"
                    1, 2, 3, 4 -> "Entrada"
                    else -> "Salida"
                }
                val cantidadMovimiento = 1 + id % 22
                val stockAnterior = 15 + (id * 7) % 180
                val stockNuevo = when (tipo) {
                    "Entrada" -> stockAnterior + cantidadMovimiento
                    "Salida" -> max(0, stockAnterior - cantidadMovimiento)
                    else -> max(0, stockAnterior + (id % 9) - 4)
                }
                val plantilla = productosPlantilla[(productoId - 1) % productosPlantilla.size]

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindLong(2, productoId.toLong())
                if (tipo == "Salida") statement.bindLong(3, cotizacionId.toLong()) else statement.bindNull(3)
                if (tipo == "Salida") statement.bindLong(4, clienteId.toLong()) else statement.bindNull(4)
                statement.bindString(5, tipo)
                statement.bindLong(6, cantidadMovimiento.toLong())
                statement.bindLong(7, stockAnterior.toLong())
                statement.bindLong(8, stockNuevo.toLong())
                statement.bindString(9, plantilla.unidad)
                statement.bindString(10, fechaHistorica(id + 491, 730).formatear())
                statement.bindString(11, "%02d:%02d".format(7 + id % 12, id % 60))
                statement.bindString(12, "Sistema de pruebas")
                statement.bindString(
                    13,
                    if (tipo == "Salida") "COT-${cotizacionId.toString().padStart(5, '0')}" else "Inventario"
                )
                statement.bindString(14, "Movimiento sintético para historial y reportes.")
                statement.executeInsert()
            }
        }
    }

    private fun insertarAvancesProyecto(
        db: SupportSQLiteDatabase,
        cantidad: Int,
        proyectos: Int
    ) {
        val sql = """
            INSERT OR REPLACE INTO proyecto_avances
            (id, proyectoId, porcentaje, fecha, comentario, fotosJson)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        db.ejecutarPreparado(sql) { statement ->
            for (id in 1..cantidad) {
                val proyectoId = 1 + (id - 1) % proyectos
                val indiceAvance = (id - 1) / proyectos
                val avancesBase = cantidad / proyectos
                val proyectosConAvanceExtra = cantidad % proyectos
                val totalAvancesProyecto = avancesBase +
                    if (proyectoId <= proyectosConAvanceExtra) 1 else 0
                val objetivo = avanceProyecto(proyectoId, estadoProyecto(proyectoId))
                val proporcion = (indiceAvance + 1).toDouble() /
                    totalAvancesProyecto.coerceAtLeast(1).toDouble()
                val porcentaje = min(objetivo, round(objetivo * proporcion).toInt())
                val fechaInicio = fechaHistorica(proyectoId + 91, 650)
                val fecha = fechaInicio.plusDays((indiceAvance * 8L))

                statement.clearBindings()
                statement.bindLong(1, id.toLong())
                statement.bindLong(2, proyectoId.toLong())
                statement.bindLong(3, porcentaje.toLong())
                statement.bindString(4, fecha.formatear())
                statement.bindString(5, comentarioAvance(porcentaje))
                statement.bindString(6, "[]")
                statement.executeInsert()
            }
        }
    }

    private fun comentarioAvance(porcentaje: Int): String = when {
        porcentaje < 15 -> "Levantamiento de medidas y preparación de material."
        porcentaje < 40 -> "Corte, habilitado y armado inicial."
        porcentaje < 70 -> "Soldadura principal y revisión de dimensiones."
        porcentaje < 95 -> "Acabados, pintura y preparación para instalación."
        else -> "Proyecto concluido y revisado."
    }

    private fun nombrePersona(id: Int): String {
        val nombre = nombres[(id * 7) % nombres.size]
        val apellido1 = apellidos[(id * 11) % apellidos.size]
        val apellido2 = apellidos[(id * 17 + 3) % apellidos.size]
        return "$nombre $apellido1 $apellido2"
    }

    private fun telefono(id: Int): String {
        val numero = 100_0000 + (id * 7_919) % 8_900_000
        return "614 ${numero.toString().take(3)} ${numero.toString().drop(3)}"
    }

    private fun correo(nombre: String, id: Int): String {
        val normalizado = nombre
            .lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
            .replace(" ", ".")
        return "$normalizado.$id@datos-prueba.test"
    }

    private fun direccion(id: Int): String {
        val calle = listOf(
            "Tecnológico", "Independencia", "Universidad", "De las Industrias",
            "H. Colegio Militar", "20 de Noviembre", "Ocampo", "Juárez", "Pacheco",
            "Vallarta"
        )[id % 10]
        return "Av. $calle #${100 + id % 9_800}, Col. ${colonias[id % colonias.size]}, ${ciudades[id % ciudades.size]}"
    }

    private fun rfcSintetico(id: Int): String = "TST${id.toString().padStart(6, '0')}A${id % 10}"

    private fun clienteIdDeCotizacion(cotizacionId: Int, totalClientes: Int): Int {
        require(totalClientes > 0) { "Debe existir al menos un cliente" }
        return 1 + ((cotizacionId * 13 - 1) % totalClientes)
    }

    private fun codigoProducto(productoId: Int): String {
        val plantilla = productosPlantilla[(productoId - 1) % productosPlantilla.size]
        val prefijo = prefijoCategoria(plantilla.categoria)
        var consecutivo = 0

        for (id in 1..productoId) {
            val categoria = productosPlantilla[(id - 1) % productosPlantilla.size].categoria
            if (prefijoCategoria(categoria) == prefijo) {
                consecutivo++
            }
        }

        return "$prefijo-${consecutivo.toString().padStart(4, '0')}"
    }

    private fun estadoCotizacion(id: Int): String = when (id % 10) {
        0, 1, 2, 3 -> "Aprobada"
        4, 5, 6, 7 -> "Pendiente"
        else -> "Rechazada"
    }

    private fun fechaCotizacion(id: Int, estado: String): LocalDate {
        return when {
            estado == "Pendiente" && id % 8 == 4 -> hoy.minusDays((id % 4).toLong())
            estado == "Pendiente" && id % 11 == 5 -> hoy.minusDays((20 + id % 20).toLong())
            else -> fechaHistorica(id + 601, 730)
        }
    }

    private fun vigenciaCotizacion(
        id: Int,
        estado: String,
        fecha: LocalDate
    ): LocalDate {
        return when {
            estado == "Pendiente" && id % 8 == 4 -> hoy.plusDays((id % 7).toLong())
            estado == "Pendiente" && id % 11 == 5 -> hoy.minusDays((1 + id % 12).toLong())
            else -> fecha.plusDays((15 + id % 31).toLong())
        }
    }

    private fun obtenerCotizacionAprobada(indice: Int, totalCotizaciones: Int): Int {
        var id = 1 + (indice * 7 - 1).mod(totalCotizaciones)
        repeat(12) {
            if (estadoCotizacion(id) == "Aprobada") return id
            id = 1 + id.mod(totalCotizaciones)
        }
        return 1
    }

    private fun nombreProyecto(id: Int): String = proyectosBase[(id - 1) % proyectosBase.size]

    private fun descripcionProyecto(id: Int): String {
        return "Fabricación e instalación de ${nombreProyecto(id).lowercase()} con materiales y acabado especificados por el cliente."
    }

    private fun estadoProyecto(id: Int): String = when (id % 20) {
        0 -> "Cancelado"
        in 1..4 -> "Pendiente"
        in 5..13 -> "En trabajo"
        else -> "Terminado"
    }

    private fun avanceProyecto(id: Int, estado: String): Int = when (estado) {
        "Pendiente" -> id % 15
        "En trabajo" -> 20 + (id * 7) % 76
        "Terminado" -> 100
        else -> id % 35
    }

    private fun fechaHistorica(id: Int, maxDias: Int): LocalDate {
        return when {
            id % 25 == 0 -> hoy
            id % 10 == 0 -> hoy.minusDays((id % 7).toLong())
            id % 6 == 0 -> hoy.minusMonths((id % 18).toLong()).minusDays((id % 24).toLong())
            else -> hoy.minusDays(((id * 37L) % maxDias.coerceAtLeast(1)).toLong())
        }
    }

    private fun prefijoCategoria(categoria: String): String = when (categoria) {
        "Materiales" -> "MAT"
        "Consumibles" -> "CON"
        "Herramientas" -> "HER"
        "Seguridad" -> "SEG"
        else -> "OTR"
    }

    private fun dinero(valor: Double): Double = round(valor * 100.0) / 100.0

    private fun LocalDate.formatear(): String = format(formatoFecha)

    private fun Boolean.toSqlLong(): Long = if (this) 1L else 0L

    private inline fun SupportSQLiteDatabase.ejecutarPreparado(
        sql: String,
        bloque: (SupportSQLiteStatement) -> Unit
    ) {
        val statement = compileStatement(sql)
        try {
            bloque(statement)
        } finally {
            statement.close()
        }
    }

    private fun SupportSQLiteDatabase.existeTabla(nombre: String): Boolean {
        query(
            "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ? LIMIT 1",
            arrayOf(nombre)
        ).use { cursor ->
            return cursor.moveToFirst()
        }
    }
}
