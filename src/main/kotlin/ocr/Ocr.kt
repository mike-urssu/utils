package ocr

import mu.KotlinLogging

private val log = KotlinLogging.logger { }

fun main() {
//    val destPath = "C:\\Users\\Administrator\\Desktop\\caffe-label\\ocr\\작업\\1128\\비식별화"
//    val file = File("C:\\Users\\Administrator\\Desktop\\caffe-label\\ocr\\작업\\1128\\비식별화 데이터 폴더 목록.csv")
//    FileUtil.readAllLines(file).forEach {
//        val source = File("Z:${it.substringAfter("/uploads")}")
//        val images = FileUtils.listFiles(source, FileFilterUtils.suffixFileFilter("png"), TrueFileFilter.TRUE)
//        log.info { "${source.absolutePath}: ${images.size}" }
//        val destination = File(destPath, source.name.substring(0, 2))
//        images.forEach { image ->
//            image.copyTo(File(destination, image.name))
////            FileUtils.copyFile(image, File(destination, image.name))
//        }
//    }

    /**
     * DB에서 비식별화 데이터를 json 파일로 저장 후
     * 새로 json 파일로 생성
     */
//    val destPath = "C:\\Users\\Administrator\\Desktop\\caffe-label\\ocr\\작업\\1128\\비식별화"
//
//    val input = File("C:\\Users\\Administrator\\Desktop\\caffe-label\\ocr\\작업\\1128\\비식별화 데이터.json")
//    val rows = FileUtil.readJsonFile(input).getJSONArray("rows")
//
//    for (i in 0 until rows.length()) {
//        val jsonObject = rows.getJSONObject(i)
//
//        val path = "Z:${jsonObject.getString("file_path").substringAfter("/uploads")}"
//        val filename = jsonObject.getString("file_name")
//        val labelData = JSONArray(jsonObject.getString("label_data"))
//        val image = File(path, filename)
//        log.info { image.absolutePath }
//
//        val directory = File(destPath, image.name.substring(12, 14))
//        val jsonFile = File(directory, "${image.nameWithoutExtension}.json")
//
//        FileUtils.copyFile(image, File(directory, filename))
//
//        val ocrObject = JsonUtil.parseLabelData(image, labelData)
//        val formattedOcrObject = JsonUtil.jsonToFormattedString(ocrObject)
//        FileUtil.writeJsonFile(jsonFile, formattedOcrObject)
//    }


    /**
     * 파일 셔플하기
     */
//    val startedAt = System.currentTimeMillis()
//    val type = "NV"
//    val source = File("E:\\10만장\\old\\${type}")
//    val destination = File("E:\\10만장\\labeling data\\${type}")
//    FileUtil.renameFiles(source, destination, type)
//    val endedAt = System.currentTimeMillis()
//    log.info { "spent: ${TimeUnit.MILLISECONDS.toSeconds(endedAt - startedAt)}" }


    /**
     * bbox 추가하기
     */
//    val destination = File("F:\\new")
//
//    val appendDirectory = File("F:\\correct_result")
//    val appendFiles = FileUtils.listFiles(appendDirectory, TrueFileFilter.TRUE, TrueFileFilter.TRUE).toSortedSet()
//    log.info { "appendFiles: ${appendFiles.size}" }
//
//    val sourceDirectory = File("F:\\BL-F008")
//    val jsonFiles = FileUtils.listFiles(sourceDirectory, FileFilterUtils.suffixFileFilter("json"), TrueFileFilter.TRUE)
//    log.info { "jsonFiles: ${jsonFiles.size}" }
//
//    for (jsonFile in jsonFiles) {
//        val source = FileUtil.readJsonFile(jsonFile)
//        if (appendFiles.contains(jsonFile)) {
//            val append = FileUtil.readJsonFile(File(appendDirectory, jsonFile.name))
//            var bboxes = BBoxUtil.appendBBoxes(source.getJSONArray("bbox"), append.getJSONArray("bbox"))
//            bboxes = BBoxUtil.removeOverlappedBBoxes(jsonFile, bboxes)
//            source.put("bbox", bboxes)
//        }
//        FileUtil.writeJsonFile(File(destination, jsonFile.name), source)
//    }


    // rename: json file, image , jsonObject -> identifier

//    val filenames =
//        FileUtil.readAllLines(File("C:\\Users\\Administrator\\Desktop\\caffe-label\\ocr\\작업\\K_T 포함된 json 파일명들.txt"))
//
//    val groups = FileUtils.listFiles(File("F:\\new"), TrueFileFilter.TRUE, TrueFileFilter.TRUE)
//        .groupBy { it.nameWithoutExtension }
//
//    val destination = File("F:\\ww")
//
//    for ((index, key) in groups.keys.withIndex()) {
//        val group = groups[key]!!
//        val filename = filenames[index]
//        group.forEach {
//            if (it.extension == "json") {
//                var ocrObject = FileUtil.readJsonFile(it)
//                ocrObject = JsonUtil.getNewJSONObject(File(it.parent, "${it.nameWithoutExtension}.png"), ocrObject)
//                ocrObject.getJSONObject("Images").put("identifier", File(filename).nameWithoutExtension)
//                FileUtil.writeJsonFile(File(destination, "${File(filename).nameWithoutExtension}.json"), ocrObject)
//            } else {
//                FileUtils.moveFile(it, File(destination, "${File(filename).nameWithoutExtension}.png"))
//            }
//        }
//    }
}
