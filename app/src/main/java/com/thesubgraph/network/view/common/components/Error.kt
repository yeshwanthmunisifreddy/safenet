package com.thesubgraph.network.view.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thesubgraph.network.R
import com.thesubgraph.network.ui.theme.Grey500
import com.thesubgraph.network.ui.theme.PrimarySolidGreen
import com.thesubgraph.network.ui.theme.SecondarySilverGray
import com.thesubgraph.network.ui.theme.TextStyle_Size16_Weight700
import com.thesubgraph.network.ui.theme.TextStyle_Size18_Weight400

@Composable
fun ErrorContent(
    paddingValues: PaddingValues? = null,
    errorMessage: String = "Something Went Wrong.",
    onRetryClick: () -> Unit,

    ) {
    Column(
        modifier = Modifier
            .padding(paddingValues ?: PaddingValues())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ErrorImageView()
        ErrorMessage(errorMessage = errorMessage,onRetryClick = onRetryClick)
        Button(
            onClick = {
                onRetryClick()
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .defaultMinSize(1.dp, 1.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimarySolidGreen,
                disabledContainerColor = SecondarySilverGray
            ),
            enabled = true,
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text = "Retry",
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 68.dp),
                style = TextStyle_Size16_Weight700.copy(lineHeight = 24.sp),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorImageView() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    Image(
        painter = painterResource(id = R.drawable.ic_generic_error),
        contentDescription = "",
        modifier = Modifier
            .width((screenWidth * 0.86).dp)
            .padding(top = 53.dp, bottom = 36.dp),
        alignment = Alignment.Center
    )
}

@Composable
private fun ErrorMessage(onRetryClick: () -> Unit, errorMessage: String) {
    Text(
        text = errorMessage,
        style = TextStyle_Size18_Weight400.copy(lineHeight = 24.sp),
        color = Grey500,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(bottom = 24.dp)
            .fillMaxWidth().clickable {
                onRetryClick()
            }
    )
}

