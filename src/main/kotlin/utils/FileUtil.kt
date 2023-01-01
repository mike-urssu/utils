package utils

import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.codehaus.jettison.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object FileUtil {
    private val log = KotlinLogging.logger { }

    fun readJsonFile(jsonFile: File) = JSONObject(readFile(jsonFile))

    private fun readFile(file: File): String {
        BufferedReader(FileReader(file)).use { reader ->
            return reader.readText()
        }
    }

    fun readAllLines(file: File): List<String> {
        BufferedReader(FileReader(file)).use { reader ->
            return reader.readLines()
        }
    }

    fun renameFiles(source: File, destination: File, type: String) {
        val baseFilename = "IMG_OCR_6_T_${type}_"

        log.info { "renaming files" }
        val groups = FileUtils.listFiles(source, TrueFileFilter.TRUE, TrueFileFilter.TRUE)
            .groupBy { it.nameWithoutExtension }

        val shuffledFilenames = groups.keys.shuffled()
        for ((index, filename) in shuffledFilenames.withIndex()) {
            val baseName = String.format("$baseFilename%06d", index)
            log.info { baseName }

            val group = groups[filename]!!
            group.forEach {
                if (it.extension == "json") {
                    val image = File(it.parentFile, "${it.nameWithoutExtension}.png")
                    val ocrObject = JsonUtil.getNewJSONObject(image, readJsonFile(it))
                    ocrObject.getJSONObject("Images").put("identifier", baseName)
                    writeJsonFile(File(destination, "$baseName.json"), ocrObject)
                } else {
                    FileUtils.moveFile(it, File(destination, "$baseName.png"))
//                    it.copyTo(File(destination, "$baseName.png"))
                }
            }
        }
    }

    fun writeJsonFile(jsonFile: File, ocrObject: JSONObject) {
        BufferedWriter(FileWriter(jsonFile)).use { writer ->
            val content = JsonUtil.jsonToFormattedString(ocrObject)
            writer.write(JsonUtil.alignOcrObject(content))
            writer.flush()
        }
    }

    fun writeFilenames(file: File, filenames: Collection<String>) {
        BufferedWriter(FileWriter(file)).use { writer ->
            filenames.forEach { writer.write("${it}\n") }
            writer.flush()
        }
    }
}
