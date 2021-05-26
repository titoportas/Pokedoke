package com.manu.pokedoke.view.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.LoadPainter
import com.manu.pokedoke.R
import com.manu.pokedoke.model.Pokemon
import com.manu.pokedoke.view.ui.detail.*
import com.manu.pokedoke.view.ui.theme.PokeDokeTheme
import com.manu.pokedoke.viewmodels.MainActivityViewModel
import com.manu.pokedoke.viewmodels.MainActivityViewModelFactory
import com.manu.pokedoke.viewstate.Error
import com.manu.pokedoke.viewstate.Loading
import com.manu.pokedoke.viewstate.Success

class PokemonMainActivity : ComponentActivity() {

    private val viewmodelFactory by lazy { MainActivityViewModelFactory(this) }
    private val viewModel: MainActivityViewModel by viewModels {
        viewmodelFactory
    }

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PokeDokeTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    topBar = { TopAppBar(title = { Text(stringResource(id = R.string.app_name)) } ) }
                ) {
                    Surface(color = MaterialTheme.colors.background) {
                        PokemonListScreen(viewModel)
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun PokemonListScreen(viewModel: MainActivityViewModel) {
    val viewState by viewModel.pokemonLiveData.observeAsState(Loading)
    when(viewState) {
        is Error -> ErrorView(viewState as Error)
        is Loading -> LoadingView()
        is Success -> PokemonList((viewState as Success<List<Pokemon>>).data)
    }
}

@ExperimentalFoundationApi
@Composable
fun PokemonList(pokemons: List<Pokemon>) {
    LazyVerticalGrid(cells = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
//    LazyColumn {
        items(pokemons) { item: Pokemon ->
            val backgroundColor = rememberSaveable { mutableStateOf(Color.Transparent.toArgb()) }
            val painter = rememberCoilPainter(
                request = item.getImageUrl(),
                requestBuilder = {
                    transformations(object : Transformation {
                        override fun key(): String {
                            return "OK"
                        }

                        override suspend fun transform(
                            pool: BitmapPool,
                            input: Bitmap,
                            size: Size
                        ): Bitmap {
                            Palette.from(input).generate().dominantSwatch?.rgb?.let { it ->
                                backgroundColor.value = it
                            }
                            return input
                        }

                    })
                },
                fadeIn = true
            )

            PokemonCellView(item, backgroundColor, painter)
        }
    }
}

@Composable
fun PokemonCellView(item: Pokemon, backgroundColor: MutableState<Int>, painter: LoadPainter<Any>) {
    val context = LocalContext.current
    Card(elevation = 4.dp, shape = RoundedCornerShape(14.dp),
        backgroundColor = Color(backgroundColor.value),
        modifier = Modifier.clickable {
            Intent(context, PokemonDetailActivity::class.java).apply {
                putExtra(PokemonDetailActivity.ARG_POKEMON_NAME, item.name)
                putExtra(PokemonDetailActivity.ARG_POKEMON_IMAGE_URL, item.getImageUrl())
            }.let {
                context.startActivity(it)
            }
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier
                    .size(155.dp)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
            Text(
                text = item.name.toUpperCase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun PokemonCellPreview() {
//    PokemonCellView(item = Pokemon(1, "Bulbasaur", ""))
}