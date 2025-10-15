package com.interview.appscheduler.feature.scheduler.presentation.view.subview

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.utility.ScheduleStatus
import com.interview.appscheduler.library.DateUtils

@Composable
fun AppItemView(
    item: AppEntity,
    showAddButton: Boolean = false,
    showEditButton: Boolean = false,
    showDeleteButton: Boolean = false,
    onClickAdd: () -> Unit = {},
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
    ){
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.icon)
                    .size(50, 50) // Downsample to 100x100 px
                    .scale(Scale.FILL) // or Scale.FIT
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
            )

            Spacer(modifier = Modifier.width(7.5.dp))

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
                    text = "${if(item.scheduledTime != null) "Scheduled:" else "Installed:"} ${DateUtils.getReadableStringFromDate(item.scheduledTime ?: item.installedTime)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                //if the view type is scheduled app, then show the status
                if(item.scheduledTime != null) {
                    Text(
                        text = ScheduleStatus.getStatusFromValue(item.status ?: 0).getStatusText(),
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                ScheduleStatus.getStatusFromValue(item.status ?: 0)
                                    .getStatusColor(),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.5.dp)
                    )
                }
            }

            // Add Button
            if(showAddButton) {
                OutlinedButton(
                    onClick = onClickAdd,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color(0xFF00966E)),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Text(
                        "+",
                        color = Color(0xFF00966E),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))
            }

            // Edit Button
            if(showEditButton) {
                IconButton(onClick = onClickEdit) {
                    Icon(
                        modifier = Modifier
                            .width(28.dp)
                            .height(28.dp)
                            .background(Color.Transparent)
                            .border(
                                width = 0.3.dp,
                                color = Color(0xFF00966E),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Delete",
                        tint = Color(0xFF00966E)
                    )
                }
            }

            // Delete Button
            if(showDeleteButton) {
                IconButton(onClick = onClickDelete) {
                    Icon(
                        modifier = Modifier
                            .width(28.dp)
                            .height(28.dp)
                            .background(Color.Transparent)
                            .border(
                                width = 0.3.dp,
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

        Spacer(modifier = Modifier.height(12.dp))

        Divider(
            modifier = Modifier
                .height(0.6.dp),
            color = Color.Gray.copy(alpha = 0.2f)
        )
    }
}