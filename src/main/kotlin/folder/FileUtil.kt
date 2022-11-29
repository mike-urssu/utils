package folder

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.codehaus.jettison.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object FileUtil {
    fun getBaseFolders(folder: File) {
        FileUtils.listFiles(folder, TrueFileFilter.TRUE, TrueFileFilter.TRUE).map { it.parentFile }.distinct()
            .filter { it.isDirectory }.forEach { println(it.absolutePath) }
    }

    fun splitFiles(folder: File) {
        val files = ArrayList<File>(FileUtils.listFiles(folder, TrueFileFilter.TRUE, TrueFileFilter.TRUE))
        val size = files.size

        for (i in 0..size / 100) {
            val newFolder = File(folder.absolutePath, String.format("%02d", i))
            FileUtils.forceMkdir(newFolder)

            if (i == size / 100) {
                val sub = files.subList(i * 100, size)
                for (file in sub) {
                    FileUtils.moveFile(file, File(newFolder.absolutePath, file.name))
                }
            } else {
                val sub = files.subList(i * 100, (i + 1) * 100)
                for (file in sub) {
                    FileUtils.moveFile(file, File(newFolder.absolutePath, file.name))
                }
            }
        }

    }

    fun separateFiles(folder: File) {
        val pathForAppend = "C:\\Users\\Administrator\\Desktop\\caffe-label\\ocr\\작업\\labeling\\append"
        val pathForErase = "C:\\Users\\Administrator\\Desktop\\caffe-label\\ocr\\작업\\labeling\\erase"
        val files = FileUtils.listFiles(folder, TrueFileFilter.TRUE, TrueFileFilter.TRUE)
        for (file in files) {
            if (file.parentFile.name.endsWith("_Append")) {
                FileUtils.moveFile(file, File(pathForAppend, file.name))
            } else if (file.parentFile.name.endsWith("_Erase")) {
                FileUtils.moveFile(file, File(pathForErase, file.name))
            }
        }
    }

    fun readFile(file: File): String {
        BufferedReader(FileReader(file)).use { reader ->
            return reader.readText()
        }
    }

    fun readJsonFile(jsonFile: File) = JSONObject(readFile(jsonFile))
}

private val log = KotlinLogging.logger { }

private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

private val dataSet = JSONObject()

private val annotation = JSONObject()

private fun setDataSet() {
    dataSet.put("category", 0)
    dataSet.put("identifier", "IMG_OCR_6_T")
    dataSet.put("label_path", "OCR/물류/02.라벨링데이터/")
    dataSet.put("name", "대규모 OCR 데이터셋 (물류)")
    dataSet.put("src_path", "OCR/물류/01.원천데이터/")
    dataSet.put("type", 1)
}

private fun setAnnotation() {
    annotation.put("object_recognition", 1)
    annotation.put("text_language", 2)  // here
}

fun main() {
    val types = arrayOf("BL", "PL", "NV")
    val previousIndices = arrayOf(18329, 18589, 22793)

    val current = 2
    val type = types[current]
    val previousIndex = previousIndices[current]

    val baseFilename = "IMG_OCR_6_T_${type}_"
    val srcPath = "C:\\Users\\Administrator\\Desktop\\caffe-label\\ocr\\작업\\1117\\cut_NV-F104"
//    val srcPath = "E:\\$type"
//    val destPath = "E:\\라벨데이터\\$type"

    val files = FileUtils.listFiles(File(srcPath), TrueFileFilter.TRUE, TrueFileFilter.TRUE)

    val groups = files.groupBy { it.nameWithoutExtension }.toSortedMap()
    val filenamesWithoutExtension = groups.keys.shuffled()

    for ((index, filename) in filenamesWithoutExtension.withIndex()) {
        val group = groups[filename]!!
        group.forEach {
            val newBaseName = String.format("$baseFilename%06d", previousIndex + index)
            it.renameTo(File(srcPath, "$newBaseName.${it.extension}"))

//            val newFile = File(destPath, "$newBaseName.${it.extension}")
////            log.info { newBaseName }
//            if (it.extension == "json") {
//                createJsonFile(it, newFile)
//            } else {
//                FileUtils.copyFile(it, newFile)
//            }
        }
    }
}

/**
 * OCR json 파일을 읽고 수정하고 새로운 파일로 덮어쓰기
 */
fun main2() {
    setAnnotation()
    setDataSet()

    val source = File("")
    val fileFilter = FileFilterUtils.suffixFileFilter("json")
    val jsonFiles = FileUtils.listFiles(source, fileFilter, TrueFileFilter.TRUE)

    jsonFiles.forEach {
        val previousJsonObject = FileUtil.readJsonFile(it)
        val newJsonObject = reformatDataSet(it, previousJsonObject)
        overwriteJsonFile(it, newJsonObject)
    }
}

private fun reformatDataSet(jsonFile: File, previousDataSet: JSONObject): JSONObject {
    val newJSONObject = JSONObject()

    newJSONObject.put("DataSet", dataSet)
    newJSONObject.put("Annotation", annotation)

    val images = JSONObject()
    images.put("identifier", jsonFile.nameWithoutExtension)
    images.put("type", "png")
    images.put("width", 1654)
    images.put("height", 2340)

    if (jsonFile.name.contains("BL")) {
        images.put("form_type", "선하증권")
    } else if (jsonFile.name.contains("NV")) {
        images.put("form_type", "상업송장")
    } else {
        images.put("form_type", "포장명세서")
    }

    images.put("data_capture", "2022.11.17")

    newJSONObject.put("Images", images)
    newJSONObject.put("bbox", previousDataSet.getJSONArray("bbox"))
    return newJSONObject
}

private fun overwriteJsonFile(jsonFile: File, dataSet: JSONObject) {
    BufferedWriter(FileWriter(jsonFile)).use { writer ->
        val element = JsonParser.parseString(dataSet.toString())
        writer.write(gson.toJson(element))
    }
}
