import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.AbstractTask
import java.io.Serializable

interface AppleTarget : Serializable {
    val name: String
    val configuration: Configuration
    val sourceSet: AppleSourceSet
    val buildTask: AbstractTask
    var launchStoryboard: String?
    var mainStoryboard: String?
    var bridgingHeader: String?
}