package view

import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos
import javafx.stage.FileChooser
import model.ShapouetModel
import tornadofx.*
import java.io.File

class MyView: View("Shapouet") {
    val model = ShapouetModel()
    override val root = form {
        alignment = Pos.CENTER

        fieldset(title, labelPosition = VERTICAL) {
            field("Osu! directory") {
                textfield().textProperty().bindBidirectional(model.osuDirectory)
                button("...").action {
                    model.osuDirectory.set(chooseDirectory(
                        initialDirectory = if (model.osuDirectory.value != null) File(model.osuDirectory.value)
                        else null)?.absolutePath ?: model.osuDirectory.value)
                }
            }

            field("Output file") {
                textfield().textProperty().bindBidirectional(model.outputFile)
                button("...").action {
                    val file = chooseFile(
                        mode = FileChooserMode.Save, filters = arrayOf(FileChooser.ExtensionFilter(
                            "Playlists (*.m3u)", "*.m3u"
                        )))
                    model.outputFile.set(if (file.isEmpty()) model.outputFile.value else file[0].absolutePath)
                }
            }

            field {
                hbox {
                    alignment = Pos.BASELINE_CENTER
                    button("Bite")
                        .enableWhen(model.osuDirectory.isNotEmpty.and(model.outputFile.isNotEmpty))
                        .action { model.commit() }
                }
            }
        }
    }
}