package myth.transient.source.migration

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import myth.transient.source.data.model.DetectedItem
import myth.transient.source.data.model.NormalizedBounds
import java.util.UUID

data class OcrResult(
    val detectedItems: List<DetectedItem>,
    val allRecognizedText: String
)

/**
 * Local OCR only. This extracts text and bounding boxes, not app identities.
 * App matching and icon/grid inference are separate stages.
 */
class LocalScreenshotOcr {
    suspend fun analyze(context: Context, uri: Uri): OcrResult {
        val image = InputImage.fromFilePath(context, uri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        return try {
            val result = recognizer.process(image).await()
            val width = image.width.coerceAtLeast(1)
            val height = image.height.coerceAtLeast(1)

            val items = result.textBlocks.mapNotNull { block ->
                val box: Rect = block.boundingBox ?: return@mapNotNull null
                val label = block.lines.joinToString(" ") { it.text }.trim()
                if (label.isBlank()) return@mapNotNull null

                DetectedItem(
                    id = UUID.randomUUID().toString(),
                    label = label,
                    bounds = NormalizedBounds(
                        left = (box.left.toFloat() / width).coerceIn(0f, 1f),
                        top = (box.top.toFloat() / height).coerceIn(0f, 1f),
                        right = (box.right.toFloat() / width).coerceIn(0f, 1f),
                        bottom = (box.bottom.toFloat() / height).coerceIn(0f, 1f)
                    ),
                    confidence = 0.5f // OCR confidence is not app-match confidence.
                )
            }

            OcrResult(items, result.text)
        } finally {
            recognizer.close()
        }
    }
}
