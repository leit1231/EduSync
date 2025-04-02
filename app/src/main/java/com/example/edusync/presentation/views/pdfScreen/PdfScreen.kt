package com.example.edusync.presentation.views.pdfScreen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import ru.marat.pdf_reader.layout.ReaderLayout
import ru.marat.pdf_reader.layout.state.rememberReaderLayoutState

@Composable
fun PdfScreen(
    uri: Uri,
    onBack: () -> Unit
) {
    val readerState = rememberReaderLayoutState(
        maxZoom = 10f,
        uri = uri
    )
    val windowInsets = WindowInsets.statusBars
    val density = LocalDensity.current

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.height(density.run { windowInsets.getTop(this).toDp() } + 60.dp).background(
                Color.White),
        ) {
            Button(onClick = {
                onBack()
            }) {
                Text(text = "Назад")
            }
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = "КАКОЙ НИБУДЬ ТУЛБАР"
            )
        }

        ReaderLayout(
            modifier = Modifier.weight(1f),
            layoutState = readerState,
        ) {}
        Box(
            modifier = Modifier.height(density.run { windowInsets.getBottom(this).toDp() } + 60.dp).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("КАКАЯ НИБУДЬ НАВИГАЦИЯ")
        }
    }
}

//val pdfPickerLauncher = rememberLauncherForActivityResult(
//    contract = ActivityResultContracts.OpenDocument()
//) { uri ->
//    if (uri != null) {
//        navController.navigate(NavRoutes.PdfScreen(uri.toString()))
//    } else Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
//}

//pdfPickerLauncher.launch(arrayOf("application/pdf"))
