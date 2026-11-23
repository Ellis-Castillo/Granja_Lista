package com.example.castilloschmuck_ellis_granja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PantallaPrincipal()
        }
    }
}

@Composable
fun PantallaPrincipal(){
    val animalesEnGranja = rememberSaveable(saver = AnimalesSaver) { mutableStateListOf<Animal>() }
    val listaIds = rememberSaveable { mutableStateListOf<Int>() }
    var nombreInput by rememberSaveable { mutableStateOf("") }
    var especieInput by rememberSaveable { mutableStateOf("") }
    var edadInput by rememberSaveable { mutableStateOf("0") }
    var mostrarMensajeError by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        Titulo("Mi Granja")
        Spacer(modifier = Modifier.height(8.dp))
        AnimalesRegistrados(
            animalesEnGranja.size,
            modifier = Modifier.align(Alignment.End)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Formulario(
            nombreInput,
            cambioNombre = { nombreInput = it },
            especieInput,
            cambioEspecie = { especieInput = it },
            edadInput,
            cambioMeses = { edadInput = it },
        )

        if (animalesEnGranja.size < 8){
            Button(
                onClick = {
                    val edadNumerica = validarEdad(edadInput)

                    if (nombreInput.isNotBlank() && especieInput.isNotBlank() && edadInput.isNotBlank()
                        && edadNumerica in 0..360){
                        val animalPorAgregar = Animal(
                            conseguirIdAleatorio(listaIds),
                            nombreInput.trim(),
                            especieInput.trim(),
                            edadNumerica
                        )
                        animalesEnGranja.add(animalPorAgregar)
                        nombreInput = ""
                        especieInput = ""
                        edadInput = ""
                    } else{
                        mostrarMensajeError = true
                    }
                }
            ) {
                Text("Registrar Animal")
            }
        } else{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Recinto completo")
            }
        }

        if (animalesEnGranja.isEmpty()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("No hay animales registrados")
            }
        } else{
            LazyColumn() {
                items(
                    items = animalesEnGranja,
                    key = {animal -> animal.identificador}
                ){
                        animal ->
                    FichaAnimal(animal = animal)
                    Row {
                        /*BotonModificarJugador(
                            modificarJugador = {
                                nombreInput = jugador.nombre
                                nicknameInput = jugador.nickname
                                rolInput = jugador.rol
                                idEnEdicion = jugador.id
                                modificandoJugadorExistente = true
                            }
                        )*/
                        BotonEliminarAnimal(
                            alEliminar = {
                                val idTransitivo = animal.identificador
                                listaIds.remove(idTransitivo)
                                animalesEnGranja.remove(animal)
                            }
                        )
                    }
                }
            }
        }
        if (mostrarMensajeError){
            MensajeError(
                onDismissRequest = {
                    mostrarMensajeError = false
                },
                mensaje = "Error: Los campos no pueden estar vacíos"
            )
        }
    }
}

@Composable
fun Titulo(titulo: String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .weight(1f)
        )
    }
}

@Composable
fun Formulario(
    nombre: String,
    cambioNombre: (String) -> Unit,
    especie: String,
    cambioEspecie: (String) -> Unit,
    edad: String,
    cambioMeses: (String) -> Unit
){
    TextoEditable(
        "Nombre",
        nombre,
        cambioNombre,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth()
    )
    TextoEditable(
        "Especie",
        especie,
        cambioEspecie,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth()
    )
    TextoEditable(
        "Edad (en meses)",
        edad,
        cambioMeses,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

fun validarEdad(edad: String): Int{
    var edadNumerica: Int
    if (edad.toIntOrNull() == null){
        edadNumerica = -1
    } else{
        edadNumerica = edad.toInt()
    }
    return edadNumerica
}

@Composable
fun TextoEditable(
    etiqueta: String,
    valor: String,
    cambioTexto: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier
){
    TextField(
        value = valor,
        onValueChange = cambioTexto,
        modifier = modifier
            .padding(bottom = 12.dp),
        label = {
            Text(etiqueta)
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
    )
}

@Composable
fun AnimalesRegistrados(
    cantidadAnimales: Int,
    modifier: Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text("Animales registrados: $cantidadAnimales / 8")
    }
}

@Composable
fun FichaAnimal(
    animal: Animal
){
    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier
        .statusBarsPadding()
        .fillMaxWidth()
        .background(
            MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(50)
        )
        .padding(horizontal = 6.dp)
        .height(108.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 12.dp)
                .weight(5f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = animal.nombre.uppercase(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 22.sp,
                lineHeight = 22.sp,
                maxLines = 1,
            )
            Text(
                text = animal.edadMeses.toString() + "meses",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                maxLines = 1,
            )
        }

        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 8.dp,
                    end = 24.dp
                )
                .weight(5f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = animal.especie,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 22.sp,
                maxLines = 1,
            )
        }
    }
}

 @Composable
 fun BotonEliminarAnimal(
     alEliminar: () -> Unit
 ){
     Row(
         modifier = Modifier
             .fillMaxWidth()
     ) {
         Button(
             onClick = alEliminar
         ) {
             Text("Eliminar animal del Registro")
         }
     }
 }

@Composable
fun MensajeError(onDismissRequest: () -> Unit, mensaje: String){
    Dialog(onDismissRequest = {onDismissRequest()}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = mensaje,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun conseguirIdAleatorio(
    listaIds: SnapshotStateList<Int>
): Int{
    var idObjetivo = 0
    var buscar = true

    while (buscar){
        idObjetivo = (500000..799999).random()
        buscar = false

        for (id in listaIds){
            if (id == idObjetivo){
                buscar = true
            }
        }
    }

    listaIds.add(idObjetivo)
    return idObjetivo
}

data class Animal constructor(
    val identificador: Int,
    val nombre: String,
    val especie: String,
    val edadMeses: Int
){}

val AnimalesSaver = listSaver<MutableList<Animal>, Any>(
    save = { animales ->
        animales.flatMap { animal ->
            listOf(
                animal.identificador,
                animal.nombre,
                animal.especie,
                animal.edadMeses
            )
        }
    },
    restore = { datos ->
        mutableStateListOf<Animal>().apply {
            datos.chunked(4).forEach { animal ->
                add(
                    Animal(
                        identificador = animal[0] as Int,
                        nombre = animal[1] as String,
                        especie = animal[2] as String,
                        edadMeses = animal[3] as Int
                    )
                )
            }
        }
    }
)