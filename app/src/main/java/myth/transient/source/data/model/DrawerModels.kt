package myth.transient.source.data.model

enum class ScreenKind { HOME, DRAWER }
enum class DrawerStyle { GRID, UNSUPPORTED }
enum class MatchState { MATCHED, NEEDS_CONFIRMATION, UNMATCHED }

data class NormalizedBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

data class DetectedItem(
    val id: String,
    val label: String,
    val bounds: NormalizedBounds,
    val confidence: Float
)

data class LauncherItem(
    val id: String,
    val label: String,
    val packageName: String? = null,
    val activityName: String? = null,
    val row: Int = 0,
    val column: Int = 0,
    val isFolder: Boolean = false,
    val matchState: MatchState = MatchState.UNMATCHED
)

data class ScreenLayout(
    val kind: ScreenKind,
    val columns: Int = 5,
    val items: List<LauncherItem> = emptyList()
)

data class MigrationDraft(
    val drawer: ScreenLayout? = null,
    val home: ScreenLayout? = null
)
