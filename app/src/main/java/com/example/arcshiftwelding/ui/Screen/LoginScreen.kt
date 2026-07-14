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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arcshiftwelding.data.PerfilDatosPrueba
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember {
        ArcshiftWeldingDatabase.getDatabase(context)
    }
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(database)
    )
    val estadoCarga by loginViewModel.estadoCargaDatosPrueba.collectAsState()

    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarSelectorDatos by remember { mutableStateOf(false) }
    var perfilSeleccionado by remember { mutableStateOf(PerfilDatosPrueba.REPORTES) }

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
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0ECFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Logo",
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

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

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    label = { Text("Usuario") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D4ED8)
                    ),
                    enabled = !estadoCarga.cargando
                ) {
                    Text("Iniciar Sesión", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        loginViewModel.limpiarMensajeDatosPrueba()
                        mostrarSelectorDatos = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !estadoCarga.cargando
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
