package model

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonType
import tornadofx.controlsfx.progressDialog
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths

class ShapouetModel {
    var osuDirectory = SimpleStringProperty()
    var outputFile = SimpleStringProperty()

    private lateinit var songDir: File

    private fun invalidForm(): Pair<Boolean, String> {
        if (osuDirectory.value == null || osuDirectory.value.isEmpty())
            return Pair(true, "Osu! directory is missing")

        if (!Files.isDirectory(Paths.get(osuDirectory.value)))
            return Pair(true, "Osu! directory is not a directory")

        for (file in File(osuDirectory.value).listFiles() ?: arrayOf()) {
            if (file.isDirectory && file.name == "Songs") {
                songDir = file
                return if (outputFile.value != null || outputFile.value.isEmpty()) Pair(false, "")
                else Pair(true, "No output file")
            }
        }

        return Pair(true, "Songs directory is missing in Osu! directory")
    }

    fun commit() {
        val (error, msg) = invalidForm()
        if (error)
            tornadofx.error("", msg, ButtonType.CLOSE)
        else
            processing()
    }

    /**
     * Look for the .osu file in a directory
     * @param path Directory
     */
    private fun getOsuFile(path: File): File? {
        for (file in path.listFiles()?: arrayOf()) {
            if (file.absolutePath.endsWith(".osu"))
                return file
        }
        return null
    }

    private fun processing() {
        progressDialog {
            // Create file, overwrite if it already exists: should we ask before ?
            val file = FileWriter(outputFile.value, false)

            val total = songDir.listFiles()?.size?.toLong()
            var progress = 0.toLong()
            updateProgress(progress, total ?: 0)
            for (songDir in songDir.listFiles() ?: arrayOf()) {
                val songFile = getOsuFile(songDir)
                if (songDir == null)
                    continue
                for (line in songFile?.readLines() ?: arrayListOf()) {
                    if (line.startsWith("AudioFilename")) {
                        file.write("${songDir.absolutePath}\\${line.replace("AudioFilename: ", "")}\n")
                        updateMessage(songDir.absolutePath.substringAfterLast("\\"))
                        updateProgress(++progress, total ?: 0)
                        break
                    }
                }
            }

            // Close file
            file.close()
        }
    }
}