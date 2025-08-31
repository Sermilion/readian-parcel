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

public val net.readian.parcel.core.designsystem.icon.ReadianIcons.UserPlaceholder: ImageVector
  get() {
    if (_UserPlaceholder != null) {
      return _UserPlaceholder!!
    }
    _UserPlaceholder = Builder(
      name = "User-female-svgrepo-com",
      defaultWidth =
      800.0.dp,
      defaultHeight = 800.0.dp, viewportWidth = 512.0f,
      viewportHeight =
      512.0f,
    ).apply {
      path(
        fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
        pathFillType = NonZero,
      ) {
        moveTo(256.0f, 0.0f)
        curveTo(114.837f, 0.0f, 0.0f, 114.837f, 0.0f, 256.0f)
        reflectiveCurveToRelative(114.837f, 256.0f, 256.0f, 256.0f)
        reflectiveCurveToRelative(256.0f, -114.837f, 256.0f, -256.0f)
        reflectiveCurveTo(397.163f, 0.0f, 256.0f, 0.0f)
        close()
        moveTo(256.0f, 490.667f)
        curveToRelative(-45.376f, 0.0f, -87.659f, -13.163f, -123.605f, -35.563f)
        lineToRelative(52.843f, -26.453f)
        curveToRelative(17.003f, -9.856f, 29.141f, -54.592f, 33.579f, -73.365f)
        curveToRelative(1.216f, -5.141f, -1.515f, -10.389f, -6.421f, -12.352f)
        curveToRelative(-0.32f, -0.128f, -29.269f, -11.733f, -47.915f, -22.848f)
        curveToRelative(5.717f, -16.085f, 16.555f, -53.867f, 16.555f, -109.931f)
        curveToRelative(0.0f, -64.576f, 28.992f, -93.995f, 55.936f, -93.995f)
        curveToRelative(2.837f, 0.0f, 5.547f, -1.109f, 7.552f, -3.115f)
        lineToRelative(6.336f, -6.315f)
        curveToRelative(54.613f, 1.643f, 80.107f, 34.624f, 80.107f, 103.445f)
        curveToRelative(0.0f, 56.064f, 10.816f, 93.845f, 16.555f, 109.931f)
        curveToRelative(-18.624f, 11.093f, -47.595f, 22.72f, -47.915f, 22.848f)
        curveToRelative(-4.907f, 1.963f, -7.637f, 7.211f, -6.421f, 12.352f)
        curveToRelative(4.437f, 18.773f, 16.576f, 63.509f, 34.155f, 73.685f)
        lineToRelative(52.245f, 26.112f)
        curveTo(343.637f, 477.504f, 301.376f, 490.667f, 256.0f, 490.667f)
        close()
        moveTo(398.037f, 442.368f)
        curveToRelative(-1.003f, -1.579f, -2.261f, -3.008f, -4.075f, -3.904f)
        lineToRelative(-56.491f, -28.224f)
        curveToRelative(-5.248f, -3.179f, -14.613f, -25.984f, -21.312f, -51.072f)
        curveToRelative(12.8f, -5.461f, 36.757f, -16.277f, 50.667f, -26.155f)
        curveToRelative(4.181f, -2.965f, 5.653f, -8.491f, 3.52f, -13.163f)
        curveToRelative(-0.192f, -0.384f, -18.048f, -40.192f, -18.048f, -109.696f)
        curveToRelative(0.0f, -82.837f, -35.584f, -124.843f, -105.792f, -124.843f)
        curveToRelative(-2.837f, 0.0f, -5.547f, 1.109f, -7.552f, 3.115f)
        lineToRelative(-6.571f, 6.571f)
        curveToRelative(-35.84f, 2.859f, -72.661f, 39.147f, -72.661f, 115.136f)
        curveToRelative(0.0f, 69.504f, -17.877f, 109.312f, -18.048f, 109.696f)
        curveToRelative(-2.133f, 4.672f, -0.661f, 10.197f, 3.52f, 13.163f)
        curveToRelative(13.909f, 9.877f, 37.845f, 20.693f, 50.667f, 26.155f)
        curveToRelative(-6.699f, 25.067f, -16.021f, 47.829f, -20.736f, 50.731f)
        lineToRelative(-57.088f, 28.544f)
        curveToRelative(-1.813f, 0.896f, -3.072f, 2.347f, -4.096f, 3.925f)
        curveTo(57.792f, 399.424f, 21.333f, 331.968f, 21.333f, 256.0f)
        curveToRelative(0.0f, -129.387f, 105.28f, -234.667f, 234.667f, -234.667f)
        reflectiveCurveTo(490.667f, 126.613f, 490.667f, 256.0f)
        curveTo(490.667f, 331.989f, 454.208f, 399.445f, 398.037f, 442.368f)
        close()
      }
    }
      .build()
    return _UserPlaceholder!!
  }

private var _UserPlaceholder: ImageVector? = null
