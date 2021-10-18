package com.mousavi.composecurvescrollingsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mousavi.composecurvescrollingsample.ui.theme.ComposeCurveScrollingSampleTheme
import kotlin.math.PI
import kotlin.math.cos

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCurveScrollingSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    CurvedScrollView()
                }
            }
        }
    }
}

@Composable
fun CurvedScrollView() {
    val items = listOf(
        "Kotlin",
        "Java",
        "C#",
        "Python",
        "GoLang",
        "C++",
        "Rust"
    )

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        CurvedItem(items.size) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(if (it == 0) Color.Magenta else Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = items[it],
                    fontSize = 25.sp,
                    color = Color.White,
                    fontWeight = if (it == 0) FontWeight.ExtraBold else FontWeight.Normal
                )
                if (it == 0) {
                    Icon(
                        modifier = Modifier.graphicsLayer {
                            translationY = -30.dp.toPx()
                        },
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CurvedItem(
    count: Int,
    item: @Composable (Int) -> Unit,
) {
    val scrollState = rememberScrollState()
    val size = remember {
        mutableStateOf(IntSize.Zero)
    }

    Box(
        modifier = Modifier
            .onSizeChanged {
                size.value = it
            }
    ) {
        Layout(
            content = {
                repeat(count) {
                    item(it)
                }
            },
            modifier = Modifier
                .verticalScroll(
                    scrollState
                )
        ) { measurables, constraints ->
            val itemSpacing = 16.dp.roundToPx()
            var contentHeight = (count - 1) * itemSpacing
            val placeables = measurables.map { measurable ->
                val placeable = measurable.measure(constraints)
                contentHeight += placeable.height
                placeable
            }

            layout(constraints.maxWidth, size.value.height + contentHeight) {
                var yPosition = size.value.height / 2 - placeables[0].height / 2

                val scrollPercent = scrollState.value.toFloat() / scrollState.maxValue.toFloat()

                placeables.forEachIndexed { index, placeable ->
                    val elementRatio = index.toFloat() / placeables.lastIndex
                    val cosTeta = cos((scrollPercent - elementRatio) * PI)
                    val indent = cosTeta * size.value.width / 2

                    placeable.placeWithLayer(x = indent.toInt(), y = yPosition) {
                        alpha = cosTeta.toFloat()
                    }
                    yPosition += placeable.height + itemSpacing
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeCurveScrollingSampleTheme {

    }
}