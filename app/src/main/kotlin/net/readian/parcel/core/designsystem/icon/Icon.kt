package net.readian.parcel.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon as MaterialIcon

sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}

@Composable
fun Icon.asComposable(contentDescription: String? = null) {
    when (this) {
        is Icon.ImageVectorIcon -> MaterialIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
        )
        is Icon.DrawableResourceIcon -> MaterialIcon(
            painter = painterResource(id = id),
            contentDescription = contentDescription,
        )
    }
}
