package com.interview.appscheduler.feature.scheduler.presentation.view.subview

import android.R
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppItemView(
    item: AppEntity,
    showAddButton: Boolean = false,
    showDeleteButton: Boolean = false,
    onClickAdd: () -> Unit = {},
    onClickDelete: () -> Unit = {}
) {
    var imageRequest by remember { mutableStateOf<ImageRequest?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        imageRequest = withContext(Dispatchers.Default) {
            ImageRequest.Builder(context)
                .data("https://randomuser.me/api/portraits/men/1.jpg")
                .crossfade(true)
                .build()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
//        AsyncImage(
//            model = imageRequest,
//            contentDescription = "POSM Product Image",
//            modifier = Modifier
//                .size(50.dp)
//                .clip(RoundedCornerShape(8.dp))
//                .background(Color.Gray)
//        )

        DrawableImage(
            drawable = item.icon ?: context.getDrawable(R.drawable.sym_def_app_icon)!!,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = "Scheduled: ${item.scheduledTime}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Add Button
        if(showAddButton) {
            OutlinedButton(
                onClick = onClickAdd,
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.LightGray),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Text(
                    "+",
                    color = Color.Black,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.width(4.dp))
        }

        // Delete Button
        if(showDeleteButton) {
            IconButton(onClick = onClickDelete) {
                Icon(
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .background(Color.Transparent)
                        .border(
                            width = 0.6.dp,
                            color = Color.Red.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(4.dp)
                        ),
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun DrawableImage(drawable: Drawable, modifier: Modifier = Modifier) {
    val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
    val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    Image(
        painter = BitmapPainter(bitmap.asImageBitmap()),
        contentDescription = null,
        modifier = modifier
    )
}