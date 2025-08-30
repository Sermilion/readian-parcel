package net.readian.parcel.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val ReadianLetter: ImageVector
    get() {
        if (_icReadianLetter != null) {
            return _icReadianLetter!!
        }
        _icReadianLetter = Builder(
            name = "IcReadianLetter", defaultWidth = 42.0.dp,
            defaultHeight =
            42.0.dp,
            viewportWidth = 42.0f, viewportHeight = 42.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF2C73F3)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(18.4802f, 38.64f)
                lineTo(10.9202f, 36.12f)
                lineTo(4.2002f, 38.64f)
                verticalLineTo(9.66f)
                lineTo(18.4802f, 3.36f)
                verticalLineTo(38.64f)
                close()
                moveTo(24.3602f, 10.08f)
                curveTo(24.3602f, 13.86f, 27.3002f, 16.8f, 31.0802f, 16.8f)
                curveTo(34.8602f, 16.8f, 37.8002f, 13.86f, 37.8002f, 10.08f)
                curveTo(37.8002f, 6.3f, 34.8602f, 3.36f, 31.0802f, 3.36f)
                curveTo(24.3602f, 3.36f, 20.1602f, 11.76f, 20.1602f, 11.76f)
                lineTo(24.3602f, 10.08f)
                close()
            }
        }
            .build()
        return _icReadianLetter!!
    }

private var _icReadianLetter: ImageVector? = null
