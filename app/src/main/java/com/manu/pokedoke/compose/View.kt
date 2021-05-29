package com.manu.pokedoke.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu.pokedoke.R
import com.manu.pokedoke.model.PokemonInfo

@Composable
fun PokemonStatView(name: String,
                    stat: Int,
                    maxStat: Int,
                    modifier: Modifier = Modifier,
                    color: Color = colorResource(id = R.color.colorPrimary)) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.White,
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
            color = Color.White,
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

@Preview
@Composable
fun example() {
    PokemonStatView(
        name = stringResource(id = R.string.atk),
        stat = 201,
        maxStat = PokemonInfo.maxAttack,
        color = colorResource(id = R.color.md_orange_100)
    )
}