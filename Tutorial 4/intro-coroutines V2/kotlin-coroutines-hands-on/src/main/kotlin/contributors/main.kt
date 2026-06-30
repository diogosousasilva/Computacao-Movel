package contributors

import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        val ui = ContributorsUI()
        ui.isVisible = true
    }
}
