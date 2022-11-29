package folder

import com.fasterxml.uuid.Generators
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

private val dataSet = JSONObject()
private fun setDataSet() {
    dataSet.put("category", 0)
    dataSet.put("identifier", "IMG_OCR_6_T")
    dataSet.put("label_path", "OCR/물류/02.라벨링데이터/")
    dataSet.put("name", "대규모 OCR 데이터셋 (물류)")
    dataSet.put("src_path", "OCR/물류/01.원천데이터/")
    dataSet.put("type", 1)
}

private const val type = "CO"

private val jsonSource1 = File("E:\\2022_물류_80percent\\labeling data\\${type}")
private val jsonSource2 = File("F:\\${type}")

private val jsonSource = File("E:\\2022_물류_80percent+20percent복사본\\labeling data\\${type}")
private val imageSource = File("E:\\2022_물류_80percent+20percent복사본\\labeling data\\${type}")

private const val width = 1654
private const val height = 2340

private val log = KotlinLogging.logger { }

private val koreans = mutableListOf<String>()

fun main9() {
//    overwriteJsonFiles()

    var startedAt = System.currentTimeMillis()
    setDataSet()

    log.info { "getting jsonFiles from jsonSource" }
    val fileFilter = FileFilterUtils.suffixFileFilter("json")
    val jsonFiles =
        FileUtils.listFiles(File("E:\\2022_물류_80percent\\labeling data\\CO"), fileFilter, TrueFileFilter.TRUE)
    var endedAt = System.currentTimeMillis()
    log.info { "reading ${jsonFiles.size} files took ${TimeUnit.MILLISECONDS.toSeconds(endedAt - startedAt)} (s)" }

    startedAt = System.currentTimeMillis()
    jsonFiles.forEach {
        log.info { it.name }
        val previousJSONObject = FileUtil.readJsonFile(it)
        val newJsonObject = reformatJSONObject(it, previousJSONObject)
        overwriteJsonFile(File("E:\\2022_물류_80percent+20percent복사본\\labeling data\\CO", it.name), newJsonObject)
    }
    endedAt = System.currentTimeMillis()
    log.info { "${TimeUnit.MILLISECONDS.toSeconds(endedAt - startedAt)}" }

    BufferedWriter(
        FileWriter(
            File("C:\\Users\\Administrator\\Desktop\\caffe-label\\ocr\\작업\\1123\\korean.txt"),
            true
        )
    ).use { writer ->
        koreans.forEach { writer.write("$it\n") }
    }
}

private fun overwriteJsonFiles() {
    log.info { "overwriting jsonFiles" }

    val fileFilter = FileFilterUtils.suffixFileFilter("json")

    val jsonFiles1 = FileUtils.listFiles(jsonSource1, fileFilter, TrueFileFilter.TRUE)
    log.info { "jsonFiles1: ${jsonFiles1.size}" }
    jsonFiles1.forEach { FileUtils.copyFile(it, File(jsonSource, it.name)) }

    val jsonFiles2 = FileUtils.listFiles(jsonSource2, fileFilter, TrueFileFilter.TRUE)
    log.info { "jsonFiles2: ${jsonFiles2.size}" }
    jsonFiles2.forEach { FileUtils.copyFile(it, File(jsonSource, it.name)) }
}

private fun reformatJSONObject(jsonFile: File, previousJSONObject: JSONObject): JSONObject {
    val size = getSize(File(imageSource, jsonFile.nameWithoutExtension + ".png"))

    val newJSONObject = JSONObject()
    newJSONObject.put("Annotation", getAnnotation(jsonFile, previousJSONObject))
    newJSONObject.put("DataSet", dataSet)
    newJSONObject.put("Images", getImages(jsonFile, size))
    newJSONObject.put("bbox", getBBox(previousJSONObject, size))
    return newJSONObject
}

private fun getAnnotation(jsonFile: File, previousJSONObject: JSONObject): JSONObject {
    val annotation = JSONObject()
    annotation.put("object_recognition", 1)
    val containsKorean = previousJSONObject.getJSONArray("bbox").toString()
    if (containsKorean.matches(Regex(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*"))) {
        koreans.add(jsonFile.name)
        annotation.put("text_language", 0)
    } else {
        annotation.put("text_language", 2)
    }
    return annotation
}

private fun getImages(jsonFile: File, size: IntArray): JSONObject {
    val images = JSONObject()
    images.put("data_captured", "2022.11.22")
    if (jsonFile.name.contains("BL")) {
        images.put("form_type", "선하증권")
    } else if (jsonFile.name.contains("NV")) {
        images.put("form_type", "상업송장")
    } else if (jsonFile.name.contains("PL")) {
        images.put("form_type", "포장명세서")
    } else if (jsonFile.name.contains("CO")) {
        images.put("form_type", "원산지증명서")
    } else {
        images.put("form_type", "기타")
    }
    images.put("height", size[1])
    images.put("identifier", jsonFile.nameWithoutExtension)
    images.put("type", "png")
    images.put("width", size[0])
    return images
}

private fun getSize(image: File): IntArray {
    val imageInputStream = ImageIO.createImageInputStream(image)
    val reader = ImageIO.getImageReaders(imageInputStream).next()
    reader.setInput(imageInputStream, true)
    return intArrayOf(reader.getWidth(0), reader.getHeight(0))
}

private fun getBBox(previousJSONObject: JSONObject, size: IntArray): JSONArray {
    val bbox = JSONArray()

    val previousBBox = previousJSONObject.getJSONArray("bbox")
    for (i in 0 until previousBBox.length()) {
        val previousBox = previousBBox.getJSONObject(i)
        val box = JSONObject()
        box.put("data", previousBox.getString("data"))
        box.put("id", Generators.timeBasedGenerator().generate().toString())

        val x = JSONArray()
        val previousXs = previousBox.getJSONArray("x")
        for (j in 0 until 4) {
            x.put(previousXs.getInt(j) * size[0] / width)
        }
        box.put("x", x)

        val y = JSONArray()
        val previousYs = previousBox.getJSONArray("y")
        for (j in 0 until 4) {
            y.put(previousYs.getInt(j) * size[1] / height)
        }
        box.put("y", y)

        bbox.put(box)
    }

    return bbox
}

private fun overwriteJsonFile(jsonFile: File, dataSet: JSONObject) {
    BufferedWriter(FileWriter(jsonFile)).use {
        val element = JsonParser.parseString(dataSet.toString())
        it.write(gson.toJson(element))
    }
}
