package com.interview.appscheduler.feature.scheduler.presentation.view.subview

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddToScheduleButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 15.dp), // height matches the design
        shape = RoundedCornerShape(50), // pill shape
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00966E), // green background
            contentColor = Color.White          // text color
        )
    ) {
        Text(
            text = "Create Schedule",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
