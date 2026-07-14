package com.example.arcshiftwelding.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arcshiftwelding.data.PerfilDatosPrueba
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit
) {
    val context = LocalContext.current
    val database = remember {
        ArcshiftWeldingDatabase.getDatabase(context.applicationContext)
    }
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            database = database,
            context = context.applicationContext
        )
    )

    val estadoCarga by loginViewModel.estadoCargaDatosPrueba.collectAsState()
    val estadoLogin by loginViewModel.estadoAutenticacion.collectAsState()

    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarSelectorDatos by remember { mutableStateOf(false) }
    var mostrarRecuperacion by remember { mutableStateOf(false) }
    var perfilSeleccionado by remember { mutableStateOf(PerfilDatosPrueba.REPORTES) }

    fun intentarLogin() {
        loginViewModel.iniciarSesion(
            usuario = usuario,
            password = password,
            onLoginExitoso = onLoginExitoso
        )
    }

    if (mostrarSelectorDatos) {
        SelectorPerfilDatosPrueba(
            perfilSeleccionado = perfilSeleccionado,
            onPerfilSeleccionado = { perfilSeleccionado = it },
            onCancelar = { mostrarSelectorDatos = false },
            onConfirmar = {
                mostrarSelectorDatos = false
                loginViewModel.cargarDatosPrueba(
                    perfil = perfilSeleccionado,
                    reemplazarDatosExistentes = true
                )
            }
        )
    }

    if (mostrarRecuperacion) {
        DialogoRecuperarAcceso(
            usuarioInicial = usuario,
            viewModel = loginViewModel,
            onCerrar = {
                mostrarRecuperacion = false
                loginViewModel.limpiarEstadoRecuperacion()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0ECFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Logo",
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(39.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Arcshift Welding",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "Control administrativo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    label = { Text("Usuario") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                            Icon(
                                imageVector = if (mostrarPassword) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (mostrarPassword) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                }
                            )
                        }
                    },
                    visualTransformation = if (mostrarPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { intentarLogin() })
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            loginViewModel.limpiarEstadoRecuperacion()
                            mostrarRecuperacion = true
                        }
                    ) {
                        Text("Usar código de respaldo")
                    }
                }

                estadoLogin.mensaje?.let { mensaje ->
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (estadoLogin.esError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            Color(0xFF15803D)
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                if (estadoLogin.usuarioInicialCreado) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF7ED)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Primer acceso: admin / admin123. Cámbiala desde Configuración.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9A3412),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Button(
                    onClick = { intentarLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D4ED8)
                    ),
                    enabled = !estadoCarga.cargando && !estadoLogin.cargando
                ) {
                    if (estadoLogin.cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(21.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Iniciar sesión", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        loginViewModel.limpiarMensajeDatosPrueba()
                        mostrarSelectorDatos = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !estadoCarga.cargando && !estadoLogin.cargando
                ) {
                    Text(
                        if (estadoCarga.cargando) {
                            "Cargando datos de prueba..."
                        } else {
                            "Cargar datos masivos de prueba"
                        }
                    )
                }

                if (estadoCarga.cargando) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = estadoCarga.progreso / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${estadoCarga.progreso}% · ${estadoCarga.etapa}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                estadoCarga.mensaje?.let { mensaje ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (estadoCarga.esError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            Color(0xFF15803D)
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Versión 1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DialogoRecuperarAcceso(
    usuarioInicial: String,
    viewModel: LoginViewModel,
    onCerrar: () -> Unit
) {
    val estado by viewModel.estadoRecuperacion.collectAsState()
    var usuario by remember(usuarioInicial) { mutableStateOf(usuarioInicial) }
    var codigo by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            if (!estado.cargando) onCerrar()
        },
        icon = {
            Icon(Icons.Default.Key, contentDescription = null)
        },
        title = {
            Text("Recuperar acceso", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Utiliza uno de los códigos generados previamente en Configuración. El código quedará inutilizado después del cambio.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )

                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    label = { Text("Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !estado.exitosa
                )

                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it.uppercase() },
                    label = { Text("Código de respaldo") },
                    placeholder = { Text("XXXX-XXXX-XXXX") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !estado.exitosa
                )

                OutlinedTextField(
                    value = nuevaPassword,
                    onValueChange = { nuevaPassword = it },
                    label = { Text("Nueva contraseña") },
                    visualTransformation = if (mostrarPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                            Icon(
                                if (mostrarPassword) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !estado.exitosa
                )

                OutlinedTextField(
                    value = confirmacion,
                    onValueChange = { confirmacion = it },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = if (mostrarPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !estado.exitosa
                )

                estado.mensaje?.let { mensaje ->
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (estado.exitosa) {
                            Color(0xFF15803D)
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        },
        confirmButton = {
            if (estado.exitosa) {
                Button(onClick = onCerrar) {
                    Text("Cerrar")
                }
            } else {
                Button(
                    onClick = {
                        viewModel.recuperarAcceso(
                            usuario = usuario,
                            codigoRespaldo = codigo,
                            nuevaPassword = nuevaPassword,
                            confirmacion = confirmacion,
                            onRecuperacionExitosa = {}
                        )
                    },
                    enabled = !estado.cargando
                ) {
                    if (estado.cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Cambiar contraseña")
                    }
                }
            }
        },
        dismissButton = {
            if (!estado.exitosa) {
                TextButton(
                    onClick = onCerrar,
                    enabled = !estado.cargando
                ) {
                    Text("Cancelar")
                }
            }
        }
    )
}

@Composable
private fun SelectorPerfilDatosPrueba(
    perfilSeleccionado: PerfilDatosPrueba,
    onPerfilSeleccionado: (PerfilDatosPrueba) -> Unit,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text(
                text = "Cargar datos de prueba",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Selecciona el volumen. La carga reemplazará todos los datos actuales de la aplicación.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                PerfilDatosPrueba.entries.forEach { perfil ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPerfilSeleccionado(perfil) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (perfil == perfilSeleccionado) {
                                Color(0xFFEAF2FF)
                            } else {
                                Color(0xFFF8FAFC)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = perfil == perfilSeleccionado,
                                onClick = { onPerfilSeleccionado(perfil) }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = perfil.titulo,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = perfil.descripcion,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Aproximadamente ${perfil.totalEstimado} registros",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF2563EB)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirmar) {
                Text("Reemplazar y cargar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}
