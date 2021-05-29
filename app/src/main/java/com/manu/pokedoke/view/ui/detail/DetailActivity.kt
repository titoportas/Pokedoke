package com.manu.pokedoke.view.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.manu.pokedoke.R
import com.manu.pokedoke.compose.PokemonStatView
import com.manu.pokedoke.extensions.changeColor
import com.manu.pokedoke.extensions.getTypeColor
import com.manu.pokedoke.extensions.hide
import com.manu.pokedoke.extensions.show
import com.manu.pokedoke.model.PokemonInfo
import com.manu.pokedoke.viewmodels.DetailActivityViewModel
import com.manu.pokedoke.viewmodels.DetailActivityViewModelFactory
import com.manu.pokedoke.viewstate.Error
import com.manu.pokedoke.viewstate.Loading
import com.manu.pokedoke.viewstate.Success
import com.manu.pokedoke.viewstate.ViewState
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_detail)

        supportActionBar?.elevation = 0f

        val intent: Intent = intent
        val nameFromMainActivity = intent.getStringExtra(ARG_POKEMON_NAME) ?: "pikachu"
        val imageUrl = intent.getStringExtra(ARG_POKEMON_IMAGE_URL)

        Glide.with(this)
            .load(imageUrl)
            .listener(
                GlidePalette.with(imageUrl)
                    .use(BitmapPalette.Profile.MUTED_LIGHT)
                    .intoCallBack { palette ->
                        val rgb = palette?.dominantSwatch?.rgb
                        if (rgb != null) {
                            if(!resources.getBoolean(R.bool.is_tablet)){
                                pokemon_image_layout.setBackgroundColor(rgb)
                            }
                            changeColor(rgb)
                        }
                    }
                    .crossfade(true))
            .into(pokemon_image)
        pokemon_name.text = nameFromMainActivity

        viewModel.pokemonInfoData.observe(this, Observer<ViewState<PokemonInfo>> { viewState ->
            when(viewState){
                is Success -> {
                    detail_progress_bar.hide()
                    val pokemonInfo = viewState.data
                    if(pokemonInfo.types.size == 1) {
                        type_name_one.show()
                        type_name_one.text = pokemonInfo.types[0].type.name
                        type_name_one.setBackgroundColor(ContextCompat.getColor(this, pokemonInfo.types[0].type.name.getTypeColor()))

                        type_name_two.hide()
                    }else {
                        type_name_one.show()
                        type_name_one.text = pokemonInfo.types[0].type.name
                        type_name_one.setBackgroundColor(ContextCompat.getColor(this, pokemonInfo.types[0].type.name.getTypeColor()))

                        type_name_two.show()
                        type_name_two.text = pokemonInfo.types[1].type.name
                        type_name_two.setBackgroundColor(ContextCompat.getColor(this, pokemonInfo.types[1].type.name.getTypeColor()))
                    }
                    height.text = pokemonInfo.getHeightString()
                    weight.text = pokemonInfo.getWeightString()

                    progress_hp.labelText = pokemonInfo.getHpString()
                    progress_hp.max = PokemonInfo.maxHp.toFloat()
                    progress_hp.progress = pokemonInfo.getHp().toFloat()

                    progress_attack.labelText = pokemonInfo.getAttackString()
                    progress_attack.max = PokemonInfo.maxAttack.toFloat()
                    progress_attack.progress = pokemonInfo.getAttack().toFloat()

                    progress_defense.labelText = pokemonInfo.getDefenseString()
                    progress_defense.max = PokemonInfo.maxDefense.toFloat()
                    progress_defense.progress = pokemonInfo.getDefense().toFloat()

                    progress_speed.labelText = pokemonInfo.getSpeedString()
                    progress_speed.max = PokemonInfo.maxSpeed.toFloat()
                    progress_speed.progress = pokemonInfo.getSpeed().toFloat()

                    progress_exp.labelText = pokemonInfo.getExpString()
                    progress_exp.max = PokemonInfo.maxExp.toFloat()
                    progress_exp.progress = pokemonInfo.getExp().toFloat()


                    progress_hp_2?.setContent {
                        PokemonStatView(
                            name = stringResource(id = R.string.hp),
                            stat = pokemonInfo.getHp(),
                            maxStat = PokemonInfo.maxHp,
                            color = colorResource(id = R.color.colorPrimary)
                        )
                    }
                    progress_attack_2?.setContent {
                        PokemonStatView(
                            name = stringResource(id = R.string.atk),
                            stat = pokemonInfo.getAttack(),
                            maxStat = PokemonInfo.maxAttack,
                            color = colorResource(id = R.color.md_orange_100)
                        )
                    }
                    progress_defense_2?.setContent {
                        PokemonStatView(
                            name = stringResource(id = R.string.def),
                            stat = pokemonInfo.getDefense(),
                            maxStat = PokemonInfo.maxDefense,
                            color = colorResource(id = R.color.md_green_200)
                        )
                    }
                    progress_speed_2?.setContent {
                        PokemonStatView(
                            name = stringResource(id = R.string.spd),
                            stat = pokemonInfo.getSpeed(),
                            maxStat = PokemonInfo.maxSpeed,
                            color = colorResource(id = R.color.md_blue_200)
                        )
                    }
                    progress_exp_2?.setContent {
                        PokemonStatView(
                            name = stringResource(id = R.string.exp),
                            stat = pokemonInfo.getExp(),
                            maxStat = PokemonInfo.maxExp,
                            color = colorResource(id = R.color.flying)
                        )
                    }
                }
                is Error -> {
                    detail_progress_bar.hide()
                    Toast.makeText(this, viewState.errMsg, Toast.LENGTH_SHORT).show()
                }
                is Loading -> {
                    detail_progress_bar.show()
                }
            }



        })

        viewModel.fetchPokemonDetails(nameFromMainActivity)
    }
}