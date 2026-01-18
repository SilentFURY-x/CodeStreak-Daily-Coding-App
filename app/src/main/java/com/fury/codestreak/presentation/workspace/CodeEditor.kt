package com.fury.codestreak.presentation.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fury.codestreak.presentation.theme.PrimaryBlue
import com.fury.codestreak.presentation.theme.SurfaceHighlight

// 1. The Logic: Colors keywords and numbers
class CodeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val rawText = text.text
        val annotatedString = buildAnnotatedString {
            var lastIndex = 0
            // Keywords to highlight (Kotlin/Java style)
            val keywords = listOf("fun", "val", "var", "return", "if", "else", "true", "false", "class", "for", "while", "int", "boolean", "void", "def", "import", "package", "null", "try", "catch")
            val keywordPattern = Regex("\\b(${keywords.joinToString("|")})\\b")
            val numberPattern = Regex("\\b\\d+\\b")

            // Find all matches
            val matches = (keywordPattern.findAll(rawText) + numberPattern.findAll(rawText))
                .sortedBy { it.range.first }

            for (match in matches) {
                if (match.range.first < lastIndex) continue // Skip overlaps

                // Append text before match
                append(rawText.substring(lastIndex, match.range.first))

                // Color the match
                val color = if (match.value.matches(Regex("\\d+"))) Color(0xFFD19A66) // Orange (Numbers)
                else Color(0xFFCC7832) // Gold (Keywords)

                withStyle(SpanStyle(color = color)) {
                    append(match.value)
                }
                lastIndex = match.range.last + 1
            }
            // Append remaining text
            if (lastIndex < rawText.length) {
                append(rawText.substring(lastIndex))
            }
        }
        return TransformedText(annotatedString, OffsetMapping.Identity)
    }
}

// 2. The UI: The Editor Component
@Composable
fun CodeEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E)) // Dark VS Code background
            .border(1.dp, SurfaceHighlight, RoundedCornerShape(12.dp))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // A. Line Numbers Column
            Column(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF252526)) // Slightly lighter gutter
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val lineCount = value.lines().size
                // Ensure at least 15 lines of numbers are shown
                repeat(lineCount.coerceAtLeast(15)) { index ->
                    Text(
                        text = "${index + 1}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = Color(0xFF606366) // Dim grey for numbers
                        )
                    )
                }
            }

            // B. The Code Input Area
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = Color(0xFFA9B7C6), // Light grey code text
                    lineHeight = 20.sp
                ),
                visualTransformation = CodeVisualTransformation(), // Apply the colors
                cursorBrush = SolidColor(PrimaryBlue),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Allow scrolling
            )
        }
    }
}