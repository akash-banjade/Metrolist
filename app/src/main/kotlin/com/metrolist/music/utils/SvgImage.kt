package com.metrolist.music.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.caverock.androidsvg.SVG

/**
 * Load an SVG from a raw resource and render it to a [Painter] rasterized at the
 * given [renderSize]. The painter can be used directly with [Image] or [Icon]
 * composables, where [Icon] will apply its default content-color tint.
 */
@Composable
fun rememberSvgPainter(
    @RawRes rawRes: Int,
    renderSize: IntSize = IntSize(200, 200),
): Painter {
    val context = LocalContext.current
    val bitmap = remember(rawRes, renderSize) {
        val svg = SVG.getFromResource(context, rawRes)
        val picture: Picture = svg.renderToPicture()

        val scaleX = renderSize.width.toFloat() / picture.width.coerceAtLeast(1)
        val scaleY = renderSize.height.toFloat() / picture.height.coerceAtLeast(1)
        val scale = minOf(scaleX, scaleY)

        val bitmap =
            Bitmap.createBitmap(renderSize.width, renderSize.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.save()
        canvas.translate(
            (renderSize.width - picture.width * scale) / 2f,
            (renderSize.height - picture.height * scale) / 2f,
        )
        canvas.scale(scale, scale)
        canvas.drawPicture(picture)
        canvas.restore()
        bitmap.asImageBitmap()
    }
    return remember(bitmap) { BitmapPainter(bitmap) }
}

/**
 * Load an SVG from a raw resource and render it as an [Image] composable,
 * rasterized at the given [renderSize].
 */
@Composable
fun SvgImage(
    @RawRes rawRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    renderSize: IntSize = IntSize(200, 200),
    contentScale: ContentScale = ContentScale.Fit,
) {
    val painter = rememberSvgPainter(rawRes = rawRes, renderSize = renderSize)
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = if (tint != Color.Unspecified) ColorFilter.tint(tint) else null,
        contentScale = contentScale,
    )
}
