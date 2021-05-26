package com.manu.pokedoke.view.ui.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.coil.rememberCoilPainter
import com.manu.pokedoke.R
import com.manu.pokedoke.extensions.getTypeColor
import com.manu.pokedoke.model.PokemonInfo
import com.manu.pokedoke.view.ui.theme.PokeDokeTheme
import com.manu.pokedoke.viewmodels.DetailActivityViewModel
import com.manu.pokedoke.viewmodels.DetailActivityViewModelFactory
import com.manu.pokedoke.viewstate.Error
import com.manu.pokedoke.viewstate.Loading
import com.manu.pokedoke.viewstate.Success

class PokemonDetailActivity : ComponentActivity() {

    private val viewmodelFactory by lazy { DetailActivityViewModelFactory(this) }
    private val viewModel: DetailActivityViewModel by viewModels {
        viewmodelFactory
    }
    companion object {
        const val ARG_POKEMON_NAME = "pokemon_name"
        const val ARG_POKEMON_IMAGE_URL = "pokemon_image_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nameFromMainActivity = intent.getStringExtra(ARG_POKEMON_NAME) ?: "pikachu"
        val imageUrl = intent.getStringExtra(ARG_POKEMON_IMAGE_URL).orEmpty()

        setContent {
            PokeDokeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    PokemonDetailScreen(viewModel, imageUrl)
                }
            }
        }

        viewModel.fetchPokemonDetails(nameFromMainActivity)
    }
}

@Composable
fun PokemonDetailScreen(viewModel: DetailActivityViewModel, imageUrl: String) {
    val viewState by viewModel.pokemonInfoData.observeAsState(Loading)
    when(viewState) {
        is Error -> ErrorView(viewState as Error)
        is Loading -> LoadingView()
        is Success -> PokemonDetailView((viewState as Success<PokemonInfo>).data, imageUrl)
    }
}

@Composable
fun PokemonDetailView(pokemonInfo: PokemonInfo, imageUrl: String) {
    val painter = rememberCoilPainter(
        request = imageUrl,
        fadeIn = true
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .background(
                    getBackground(pokemonInfo.types),
                    RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp)
                )
                .padding(vertical = 20.dp),
            contentScale = ContentScale.Fit,
            painter = painter,
            contentDescription = null
        )
        Text(
            text = pokemonInfo.name.toUpperCase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
        )
        PokemonTypesView(pokemonInfo.types)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SizeView(stringResource(id = R.string.height), pokemonInfo.getHeightString())
            SizeView(stringResource(id = R.string.weight), pokemonInfo.getWeightString())
        }
        Text(
            text = stringResource(id = R.string.base_stats).toUpperCase(),
            fontSize = 21.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(16.dp)
        )
        PokemonStatView(
            stringResource(id = R.string.hp),
            pokemonInfo.getHp(),
            PokemonInfo.maxHp,
            colorResource(id = R.color.colorPrimary)
        )
        PokemonStatView(
            stringResource(id = R.string.atk),
            pokemonInfo.getAttack(),
            PokemonInfo.maxAttack,
            colorResource(id = R.color.md_orange_100)
        )
        PokemonStatView(
            stringResource(id = R.string.def),
            pokemonInfo.getDefense(),
            PokemonInfo.maxDefense,
            colorResource(id = R.color.md_green_100)
        )
        PokemonStatView(
            stringResource(id = R.string.spd),
            pokemonInfo.getSpeed(),
            PokemonInfo.maxSpeed,
            colorResource(id = R.color.md_blue_100)
        )
        PokemonStatView(
            stringResource(id = R.string.exp),
            pokemonInfo.getExp(),
            PokemonInfo.maxExp,
            colorResource(id = R.color.md_blue_200)
        )
    }
}

@Composable
fun getBackground(types: List<PokemonInfo.TypeResponse>): Brush {
    val colors = mutableListOf<Color>()
    when(types.size) {
        1 -> {
            colors.add(
                colorResource(types[0].type.name.getTypeColor())
            )
            colors.add(
                colorResource(types[0].type.name.getTypeColor())
            )
        }
        2 -> {
            colors.add(
                colorResource(types[0].type.name.getTypeColor())
            )
            colors.add(
                colorResource(types[1].type.name.getTypeColor())
            )
        }
        else -> {
            colors.add(Color.Black)
            colors.add(Color.Black)
        }
    }
    return Brush.verticalGradient(colors = colors)
}

@Composable
fun PokemonStatView(name: String,
                    stat: Int,
                    maxStat: Int,
                    color: Color = colorResource(id = R.color.colorPrimary)) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(20f)
                .padding(end = 16.dp)
        )
        Box(
            Modifier
                .height(18.dp)
                .align(CenterVertically)
                .weight(60f)
                .background(Color.White, CircleShape)
        ) {
            PokemonStatProgressView(stat, maxStat, color)
        }
        Text(
            text = stat.toString(),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(20f)
                .padding(start = 16.dp)
        )
    }
}

@Composable
fun PokemonStatProgressView(stat: Int, maxStat: Int, color: Color) {
    val size = remember { Animatable(initialValue = 0f) }
    LaunchedEffect(stat) {
        size.animateTo(
            stat.div(maxStat.toFloat()),
            tween(1000, 500)
        )
    }
    Box(
        Modifier
            .fillMaxWidth(size.value)
            .fillMaxHeight()
            .background(color, CircleShape)
            .animateContentSize()
    )
}


@Composable
fun SizeView(name: String, measurement: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = measurement,
            fontSize = 21.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            text = name.toUpperCase(),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun PokemonTypesView(types: List<PokemonInfo.TypeResponse>) {
    Row(horizontalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.size(8.dp))
        types.forEach { typeResponse ->
            PokemonTypeView(typeResponse.type)
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@Composable
fun PokemonTypeView(type: PokemonInfo.Type) {
    Text(
        text = type.name.toUpperCase(),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .background(
                colorResource(
                    id = type.name.getTypeColor()
                ), shape = CircleShape
            )
            .padding(horizontal = 16.dp, vertical = 2.dp)

    )
}

@Composable
fun ErrorView(viewState: Error) {
    Snackbar {
        Text(text = viewState.errMsg)
    }
}

@Composable
fun LoadingView() {
    CircularProgressIndicator(Modifier.size(48.dp))
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LoadingView()
        ErrorView(viewState = Error("An error occurred!"))
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonDetailPreview() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PokemonDetailView(
            PokemonInfo(
            1,
            "Bulbasaur",
            100,
            90,
            200,
            listOf(
                PokemonInfo.TypeResponse(1, PokemonInfo.Type(name = "grass")),
                PokemonInfo.TypeResponse(1, PokemonInfo.Type(name = "normal"))
            )),
            ""
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonStatPreview() {
    PokemonStatView(name = "HP", stat = 200, maxStat = 1000)
}