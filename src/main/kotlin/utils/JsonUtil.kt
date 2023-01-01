package utils

import com.fasterxml.uuid.Generators
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import mu.KotlinLogging
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object JsonUtil {
    private val log = KotlinLogging.logger { }

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    private val dataSet = JSONObject().run {
        this.put("category", 0)
        this.put("identifier", "IMG_OCR_6_T")
        this.put("label_path", "OCR/물류/02.라벨링데이터/")
        this.put("name", "대규모 OCR 데이터셋 (물류)")
        this.put("src_path", "OCR/물류/01.원천데이터/")
        this.put("type", 1)
    }

//    fun parseLabelData(image: File, labelData: JSONArray): JSONObject {
//        // here
//        val bboxes = getBBoxes(image, labelData)
//
//        val ocrObject = JSONObject()
//        ocrObject.put("Annotation", getAnnotation(bboxes))
//        ocrObject.put("DataSet", dataSet)
//        ocrObject.put("Images", getImages(image))
//        ocrObject.put("bbox", bboxes)
//        return ocrObject
//    }

    fun getNewJSONObject(image: File, previousPreLabelData: JSONObject): JSONObject {
        val newJSONObject = JSONObject()
        newJSONObject.put("Annotation", getAnnotation(previousPreLabelData.getJSONArray("bbox")))
        newJSONObject.put("DataSet", dataSet)
        newJSONObject.put("Images", getImages(image, previousPreLabelData.getJSONObject("Images")))
        newJSONObject.put("bbox", getBBoxes(previousPreLabelData.getJSONArray("bbox")))
        return newJSONObject
    }

    private fun getAnnotation(bboxes: JSONArray) =
        JSONObject().run {
            this.put("object_recognition", 1)
            if (bboxes.toString().matches(Regex(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*"))) {
                this.put("text_language", 0)
            } else {
                this.put("text_language", 2)
            }
        }

    private fun getImages(image: File, images: JSONObject) =
        JSONObject().run {
            if (images.has("data_capture")) {
                this.put("data_captured", images["data_capture"])
            } else if (images.has("data_captured")) {
                this.put("data_captured", images["data_captured"])
            } else {
                this.put("data_captured", LocalDate.now().format(dateFormat))
            }
            if (image.name.contains("BL")) {
                this.put("form_type", "선하증권")
            } else if (image.name.contains("NV")) {
                this.put("form_type", "상업송장")
            } else if (image.name.contains("PL")) {
                this.put("form_type", "포장명세서")
            } else if (image.name.contains("CO")) {
                this.put("form_type", "원산지증명서")
            } else {
                this.put("form_type", "기타")
            }

            val imageSize = ImageUtil.getImageSize(image)
            this.put("height", imageSize[1])
            this.put("identifier", image.nameWithoutExtension)
            this.put("type", "png")
            this.put("width", imageSize[0])
        }

    private fun getBBoxes(bboxes: JSONArray) =
        JSONArray().run {
            for (i in 0 until bboxes.length()) {
                val bbox = bboxes.getJSONObject(i)
                val newBBox = JSONObject()
                newBBox.put("data", bbox.getString("data"))
                newBBox.put("id", Generators.timeBasedGenerator().generate().toString())
                newBBox.put("x", bbox.getJSONArray("x"))
                newBBox.put("y", bbox.getJSONArray("y"))
                this.put(newBBox)
            }
            this
        }

    private fun getBBoxes(image: File, bboxes: JSONArray) =
        JSONArray().run {
            val imageSize = ImageUtil.getImageSize(image)
            val width = imageSize[0]
            val height = imageSize[1]

            for (i in 0 until bboxes.length()) {
                val bbox = bboxes.getJSONObject(i)
                val newBBox = JSONObject()
                newBBox.put("data", bbox.getString("cls"))
                newBBox.put("id", Generators.timeBasedGenerator().generate().toString())

                val x1 = (bbox.getDouble("x") * width).toInt()
                val x2 = ((bbox.getDouble("x") + bbox.getDouble("w")) * width).toInt()
                val y1 = (bbox.getDouble("y") * height).toInt()
                val y2 = ((bbox.getDouble("y") + bbox.getDouble("h")) * height).toInt()
                newBBox.put("x", JSONArray(listOf(x1, x1, x2, x2)))
                newBBox.put("y", JSONArray(listOf(y1, y2, y1, y2)))
                this.put(newBBox)
            }
            this
        }

    fun jsonToFormattedString(preLabelData: JSONObject): String =
        gson.toJson(JsonParser.parseString(preLabelData.toString()))

    fun alignOcrObject(ocrObject: String): String {
        val lines = ocrObject.split("\n") // FileUtil.readAllLines(jsonFile)
        val content = StringBuilder()
        var index = -1
        for (line in lines) {
            if (line.contains("\"x\":") || line.contains("\"y\":")) {
                index++
                content.append("$line ")
            } else {
                when (index) {
                    -1 -> {
                        content.append("$line \n")
                    }
                    4 -> {
                        index = -1
                        content.append("${line.trim()} \n")
                    }
                    else -> {
                        index++
                        content.append("${line.trim()} ")
                    }
                }
            }
        }
        return content.toString()
    }
}
