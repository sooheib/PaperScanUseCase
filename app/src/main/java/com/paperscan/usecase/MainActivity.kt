package com.paperscan.usecase

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.paperscan.usecase.modelViews.PaperScanViewModel
import com.paperscan.usecase.ui.theme.MergeDisabledTextColor
import com.paperscan.usecase.ui.theme.MergeEnabledTextColor
import com.paperscan.usecase.ui.theme.PaperScanUseCaseTheme
import com.paperscan.usecase.ui.theme.buttonPadding
import com.paperscan.usecase.ui.theme.buttonTextSize
import com.paperscan.usecase.ui.theme.topBartextPadding
import com.paperscan.usecase.views.tileGridLayout

/***
 * On Create MainActivity
 */
class MainActivity : ComponentActivity() {
    private val viewModel: PaperScanViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaperScanUseCaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PaperScanScreen(viewModel)
                }
            }
        }
    }
}

/***
 * Create TopBar & Grid layout
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PaperScanScreen(viewModel: PaperScanViewModel) {
    Scaffold(
        topBar = { topPaperScanAppBar(viewModel) },
    ) {
        tileGridLayout(viewModel = viewModel)
    }
}

/***
 * TopBar view Implementation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topPaperScanAppBar(viewModel: PaperScanViewModel) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.main_page_title),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = topBartextPadding
                    ),
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            /** 3. merge button top right, enabled only when more than one tile is selected. Disabled
             * otherwise.
             */
            mergeButton(
                buttonTitle = stringResource(R.string.merge_button),
                isButtonEnabled = viewModel.selectedtilesList.size > 1,
                onClickButton = { viewModel.mergeTiles() },
            )
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}

/***
 * Button view Implementation
 * ***/
@Composable
fun mergeButton(buttonTitle: String, isButtonEnabled: Boolean, onClickButton: () -> Unit) {
    val textColor = if (isButtonEnabled) MergeEnabledTextColor else MergeDisabledTextColor
    Text(
        text = buttonTitle,
        modifier = Modifier
            .clickable(
                enabled = isButtonEnabled,
                onClick = onClickButton
            )
            .padding(all = buttonPadding),
        color = textColor,
        fontSize = buttonTextSize
    )
}

/***
 * Preview MainActivity
 */
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    PaperScanUseCaseTheme {
        PaperScanScreen(viewModel = PaperScanViewModel())
    }
}