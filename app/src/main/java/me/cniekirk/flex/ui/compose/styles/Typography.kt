package me.cniekirk.flex.ui.compose.styles

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.cniekirk.flex.R

private val light = Font(R.font.rubik, FontWeight.W300)
private val regular = Font(R.font.rubik_regular, FontWeight.W400)
private val medium = Font(R.font.rubik_medium, FontWeight.W500)
private val bold = Font(R.font.rubik_bold, FontWeight.W600)

private val rubikFontFamily = FontFamily(light, regular, medium, bold)

val rubikTypography = androidx.compose.material.Typography(
    defaultFontFamily = rubikFontFamily,
    h1 = TextStyle(
        color = Color.Black,
        fontSize = 48.sp,

    )
)

@Composable
fun FlexTheme(content: @Composable () -> Unit) {
    MaterialTheme(typography = rubikTypography) {
        content()
    }
}