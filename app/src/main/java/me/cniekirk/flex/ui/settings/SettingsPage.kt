package me.cniekirk.flex.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.*

@Composable
fun SettingsPage() {
    SettingsPageContent()
}

@Composable
fun SettingsPageContent() {
    var onDraw: DrawScope.() -> Unit by remember { mutableStateOf({}) }

    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            modifier = Modifier.drawBehind { onDraw() },
//            text = testString,
//            onTextLayout = { layoutResult ->
//                val annotation = testString.getStringAnnotations("squiggle", 0, testString.length).first()
//                val textBounds = layoutResult.getBoundingBoxes(annotation.start, annotation.end)
//                onDraw = {
//                    for (bound in textBounds) {
//                        val underline = bound.copy(top = bound.bottom - 4.sp.toPx())
//                        drawPath(
//                            color = Color.Green,
//                            path = buildSquigglesFor(bound)
//                        )
//                    }
//                }
//            }
//        )
    }
}

@OptIn(ExperimentalTextApi::class)
private val testString = buildAnnotatedString {
    append("This is a test of the ")
    withAnnotation("squiggle", "ignored") {
        withStyle(SpanStyle(color = Color.Green)) {
            append("squiggle ")
        }
    }
    append("line feature!")
}