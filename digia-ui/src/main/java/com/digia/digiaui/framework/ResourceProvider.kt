import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import com.digia.digiaui.framework.DUIFontFactory
import com.digia.digiaui.framework.UIResources
import com.digia.digiaui.network.APIModel

/* ---------------------------------------------------------
 * CompositionLocal (replacement for InheritedWidget)
 * --------------------------------------------------------- */

val LocalUIResources =
        compositionLocalOf<UIResources> {
            error("UIResources not provided. Wrap your UI with ResourceProvider.")
        }

val LocalApiModels = compositionLocalOf<Map<String, APIModel>> { emptyMap() }

/* ---------------------------------------------------------
 * ResourceProvider (top-level provider)
 * --------------------------------------------------------- */

@Composable
fun ResourceProvider(
        resources: UIResources,
        apiModels: Map<String, APIModel>? = null,
        content: @Composable () -> Unit
) {
    CompositionLocalProvider(
            LocalUIResources provides resources,
            LocalApiModels provides (apiModels ?: emptyMap())
    ) { content() }
}

/* ---------------------------------------------------------
 * Resource access helpers (NO Context)
 * --------------------------------------------------------- */

@Composable
fun resourceColor(key: String): Color? {
    val resources = LocalUIResources.current
    val isDark = isSystemInDarkTheme()

    return (if (isDark) resources.darkColors else resources.colors)?.get(key)
            ?: ColorUtil.fromString(key)
}

@Composable
fun resourceTextStyle(token: String): TextStyle? {
    return LocalUIResources.current.textStyles?.get(token)
}

@Composable
fun resourceImage(key: String): Painter? {
    return LocalUIResources.current.images?.get(key) as Painter?
}

@Composable
fun resourceIcon(key: String): ImageVector? {
    return LocalUIResources.current.icons?.get(key)
}

@Composable
fun resourceApiModel(key: String): APIModel? {
    return LocalApiModels.current[key]
}

@Composable
fun resourceFontFactory(): DUIFontFactory? {
    return LocalUIResources.current.fontFactory
}
