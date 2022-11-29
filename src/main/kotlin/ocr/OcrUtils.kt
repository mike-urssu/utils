package ocr

import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import utils.FileUtil
import java.io.File

private val log = KotlinLogging.logger { }

fun main() {
    val types = arrayOf("ET", "NV", "PL")
    types.forEach { type ->
        log.info { "type: $type" }
        val directory = File("E:\\80percent+20\\${type}\\${type}")
        val destination = File("E:\\비식별화\\${type}")
        val files = FileUtils.listFiles(directory, TrueFileFilter.TRUE, TrueFileFilter.TRUE)
        log.info { "read files" }

        val groups = files.groupBy { it.nameWithoutExtension }
        val jsonFiles = files.filter { it.extension == "json" }

        log.info { "separate jsonFiles" }
        jsonFiles.forEach { jsonFile ->
            val images = FileUtil.readJsonFile(jsonFile).getJSONObject("Images")
            if (images.getInt("width") != 1654 && images.getInt("height") != 2340) {
                val group = groups[jsonFile.nameWithoutExtension]!!
                group.forEach {
                    FileUtils.moveFile(it, File(destination, it.name))
                }
            }
        }
    }
}
