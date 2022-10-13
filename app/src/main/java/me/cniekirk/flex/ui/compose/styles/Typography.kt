package me.cniekirk.flex.ui.compose.styles

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
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

val rubikTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = rubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = rubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = rubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    titleSmall = TextStyle(
        fontFamily = rubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
)