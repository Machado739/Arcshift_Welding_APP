package com.example.arcshiftwelding.ui.Screen.configuracion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.arcshiftwelding.ui.Screen.notificaciones.CampanaNotificacionesPrincipal
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.ui.theme.arcshiftColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(navController: NavController,
                        onBack: () -> Unit = {},
) {
    val context = LocalContext.current

    val database = remember {
        ArcshiftWeldingDatabase.getDatabase(context.applicationContext)
    }
    val viewModel: ConfiguracionViewModel = viewModel(
        factory = ConfiguracionViewModelFactory(
            database = database,
            context = context.applicationContext
        )
    )
    val estado by viewModel.uiState.collectAsState()

    var passwordActual by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var passwordParaCodigos by remember { mutableStateOf("") }
    var mostrarPasswords by remember { mutableStateOf(false) }

    if (estado.codigosGenerados.isNotEmpty()) {
        DialogoCodigosRespaldo(
            codigos = estado.codigosGenerados,
            onCerrar = viewModel::ocultarCodigosGenerados
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(
                        start = 17.dp,
                        top = 8.dp,
                        end = 14.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onBack()
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
                Text(
                    text = "Configuración",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                    CampanaNotificacionesPrincipal(navController)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (estado.cargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            estado.mensaje?.let { mensaje ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (estado.esError) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.arcshiftColors.successContainer
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mensaje,
                            color = if (estado.esError) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.arcshiftColors.success
                            },
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = viewModel::limpiarMensaje) {
                            Text("Cerrar")
                        }
                    }
                }
            }

            CardSeccionConfiguracion(
                titulo = "Cuenta",
                icono = { Icon(Icons.Default.Person, contentDescription = null) }
            ) {
                DatoCuenta("Nombre", estado.usuario?.nombre ?: "No disponible")
                DatoCuenta("Usuario", estado.usuario?.usuario ?: "No disponible")
                DatoCuenta("Rol", estado.usuario?.rol ?: "No disponible")
            }

            CardSeccionConfiguracion(
                titulo = "Cambiar contraseña",
                icono = { Icon(Icons.Default.Lock, contentDescription = null) }
            ) {
                Text(
                    text = "Usa al menos ocho caracteres, una letra y un número.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                CampoPasswordConfiguracion(
                    valor = passwordActual,
                    onValorChange = { passwordActual = it },
                    etiqueta = "Contraseña actual",
                    mostrar = mostrarPasswords,
                    onCambiarVisibilidad = { mostrarPasswords = !mostrarPasswords }
                )
                CampoPasswordConfiguracion(
                    valor = nuevaPassword,
                    onValorChange = { nuevaPassword = it },
                    etiqueta = "Nueva contraseña",
                    mostrar = mostrarPasswords,
                    onCambiarVisibilidad = { mostrarPasswords = !mostrarPasswords }
                )
                CampoPasswordConfiguracion(
                    valor = confirmacion,
                    onValorChange = { confirmacion = it },
                    etiqueta = "Confirmar nueva contraseña",
                    mostrar = mostrarPasswords,
                    onCambiarVisibilidad = { mostrarPasswords = !mostrarPasswords }
                )

                Button(
                    onClick = {
                        viewModel.cambiarPassword(
                            passwordActual = passwordActual,
                            nuevaPassword = nuevaPassword,
                            confirmacion = confirmacion,
                            onExito = {
                                passwordActual = ""
                                nuevaPassword = ""
                                confirmacion = ""
                            }
                        )
                    },
                    enabled = !estado.cargando && estado.usuario != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (estado.cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Actualizar contraseña")
                    }
                }
            }

            CardSeccionConfiguracion(
                titulo = "Códigos de respaldo",
                icono = { Icon(Icons.Default.Security, contentDescription = null) }
            ) {
                Text(
                    text = "Disponibles: ${estado.codigosDisponibles}",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Permiten recuperar el acceso sin conocer la contraseña. Cada código funciona una sola vez. Al generar nuevos, los anteriores quedan invalidados.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                CampoPasswordConfiguracion(
                    valor = passwordParaCodigos,
                    onValorChange = { passwordParaCodigos = it },
                    etiqueta = "Confirma tu contraseña",
                    mostrar = mostrarPasswords,
                    onCambiarVisibilidad = { mostrarPasswords = !mostrarPasswords }
                )

                OutlinedButton(
                    onClick = {
                        viewModel.generarCodigosRespaldo(passwordParaCodigos)
                        passwordParaCodigos = ""
                    },
                    enabled = !estado.cargando && estado.usuario != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Key, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (estado.codigosDisponibles > 0) {
                            "Regenerar códigos"
                        } else {
                            "Generar códigos"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CardSeccionConfiguracion(
    titulo: String,
    icono: @Composable () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                icono()
                Text(titulo, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            contenido()
        }
    }
}

@Composable
private fun DatoCuenta(titulo: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(text = valor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CampoPasswordConfiguracion(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    mostrar: Boolean,
    onCambiarVisibilidad: () -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (mostrar) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = onCambiarVisibilidad) {
                Icon(
                    imageVector = if (mostrar) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun DialogoCodigosRespaldo(
    codigos: List<String>,
    onCerrar: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val textoCompleto = codigos.joinToString("\n")

    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(Icons.Default.Security, contentDescription = null)
        },
        title = {
            Text("Guarda tus códigos", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Esta es la única ocasión en que se mostrarán completos. Guárdalos fuera del teléfono.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.arcshiftColors.onWarningContainer
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        codigos.forEachIndexed { index, codigo ->
                            Text(
                                text = "${index + 1}.  $codigo",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onCerrar) {
                Text("Ya los guardé")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    clipboard.setText(AnnotatedString(textoCompleto))
                }
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copiar todos")
            }
        }
    )
}
