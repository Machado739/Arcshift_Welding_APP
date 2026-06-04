package com.example.arcshiftwelding.ui.Screen.gastos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EliminarGastoScreen(
    navController: NavController,
    gastoId: Int
) {
    /*
        Datos de prueba.
        Después estos datos vendrán desde Room usando gastoId.
    */
    val conceptoGasto = "Compra de material"
    val categoriaGasto = "Materiales"
    val proveedorGasto = "Aceros del Norte"
    val montoGasto = 3200.00
    val fechaGasto = "10/05/2026"
    val metodoPago = "Efectivo"
    val observaciones = "Se realizó compra de acero estructural para proyecto en construcción"

    var confirmarEliminacion by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Eliminar Gasto")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F6FA))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            CardAdvertenciaEliminarGasto()

            CardGastoEliminar(
                conceptoGasto = conceptoGasto,
                categoriaGasto = categoriaGasto,
                proveedorGasto = proveedorGasto,
                montoGasto = montoGasto,
                fechaGasto = fechaGasto,
                metodoPago = metodoPago
            )

            CardImpactoEliminacionGasto(
                montoGasto = montoGasto
            )

            CardConfirmacionEliminarGasto(
                confirmado = confirmarEliminacion,
                onConfirmadoChange = {
                    confirmarEliminacion = it
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        /*
                            Aquí después harás:
                            viewModel.eliminarGasto(gastoId)

                            Luego regresas a gastos:
                        */
                        navController.navigate(AppRoutes.GASTOS) {
                            popUpTo(AppRoutes.GASTOS) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    enabled = confirmarEliminacion,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB42318),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE5E7EB),
                        disabledContentColor = Color.Gray
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Eliminar")
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun CardAdvertenciaEliminarGasto() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFE8E6)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFB42318),
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Advertencia",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB42318)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Esta acción eliminará el gasto del registro. Revisa la información antes de continuar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A271A)
                )
            }
        }
    }
}

@Composable
fun CardGastoEliminar(
    conceptoGasto: String,
    categoriaGasto: String,
    proveedorGasto: String,
    montoGasto: Double,
    fechaGasto: String,
    metodoPago: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Gasto seleccionado",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = Color(0xFFEDEDED),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        modifier = Modifier.size(38.dp),
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = conceptoGasto,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Categoría: $categoriaGasto",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Text(
                        text = "Proveedor: $proveedorGasto",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Text(
                        text = "Fecha: $fechaGasto",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CajaInfoEliminarGasto(
                    titulo = "Monto",
                    valor = "$ ${"%.2f".format(montoGasto)}",
                    subtitulo = "Total",
                    modifier = Modifier.weight(1f)
                )

                CajaInfoEliminarGasto(
                    titulo = "Método Pago",
                    valor = metodoPago,
                    subtitulo = "Registrado",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CajaInfoEliminarGasto(
    titulo: String,
    valor: String,
    subtitulo: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F7F7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CardImpactoEliminacionGasto(
    montoGasto: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Impacto de eliminación",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            ItemImpactoEliminarGasto(
                texto = "El gasto dejará de aparecer en el registro de gastos.",
                advertencia = false
            )

            ItemImpactoEliminarGasto(
                texto = "El monto de ${"%.2f".format(montoGasto)} será removido de los reportes.",
                advertencia = true
            )

            ItemImpactoEliminarGasto(
                texto = "Esta acción no se puede deshacer. Se recomienda hacer copia de seguridad.",
                advertencia = true
            )
        }
    }
}

@Composable
fun ItemImpactoEliminarGasto(
    texto: String,
    advertencia: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (advertencia) Color(0xFFB42318) else Color(0xFF6B7280),
                    shape = RoundedCornerShape(50)
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = if (advertencia) Color(0xFFB42318) else Color(0xFF374151)
        )
    }
}

@Composable
fun CardConfirmacionEliminarGasto(
    confirmado: Boolean,
    onConfirmadoChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = confirmado,
                onCheckedChange = onConfirmadoChange
            )

            Column {
                Text(
                    text = "Confirmar eliminación",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Entiendo que este gasto será eliminado del registro.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}