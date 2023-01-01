package ocr

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import utils.FileUtil
import utils.JsonUtil
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

private val parents = arrayOf("BL", "CO", "ET", "NV", "PL")
private val totalSizes = intArrayOf(25000, 10000, 15000, 25000, 25000)

private const val source = "D:\\ocr 작업\\test\\ocr 납품용 데이터"
private const val destination = "D:\\ocr 작업\\test\\납품용"

fun main() {
    repeat(1) { i ->
        val parent = parents[i]
        val groups = getGroups(parent, totalSizes[i])
        val logs = logFilenames(parent, groups.keys.shuffled())
        renameFiles(parent, groups, logs)
        reformatJsonFiles(parent)
    }
}

/**
 * parent 타입에 대해서 totalSize 만큼의 이미지를 반환한다.
 * ex) parent: BL, totalSize: 25000장인 경우
 * 비식별화/BL에서 N장, 일반/BL에서 (25000 - N)장을 모아서 반환한다.
 */
private fun getGroups(parent: String, totalSize: Int): Map<String, List<File>> {
    val src1 = File("$source\\비식별화\\${parent}")
    val groups1 = FileUtils.listFiles(src1, TrueFileFilter.TRUE, TrueFileFilter.TRUE)
        .groupBy { it.nameWithoutExtension }

    val src2 = File("$source\\일반\\${parent}")
    var groups2 = FileUtils.listFiles(src2, TrueFileFilter.TRUE, TrueFileFilter.TRUE)
        .groupBy { it.nameWithoutExtension }
    val selectedFiles = groups2
        .keys.toList()
        .subList(0, totalSize - groups1.size)
    groups2 = groups2.filter { selectedFiles.contains(it.key) }

    val groups = sortedMapOf<String, List<File>>()
    groups.putAll(groups1)
    groups.putAll(groups2)
    return groups
}

/**
 * 현재 파일명과 바꿀 파일명을 txt 파일에 기록한다.
 */
private fun logFilenames(parent: String, filenames: List<String>): Set<Pair<String, String>> {
    val logs = sortedSetOf<Pair<String, String>>(Comparator { it, other -> it.first.compareTo(other.first) })
    BufferedWriter(FileWriter(File(destination, "${parent}.txt"))).use { writer ->
        filenames.forEachIndexed { index, oldName ->
            val newName = "IMG_OCR_6_T_${parent}_${String.format("%06d", index)}"
            writer.write("$oldName,$newName\n")
            logs.add(Pair(oldName, newName))
        }
        writer.flush()
    }
    return logs
}

/**
 * 현재 파일명을 새로운 파일명으로 변경한다.
 */
private fun renameFiles(parent: String, groups: Map<String, List<File>>, logs: Set<Pair<String, String>>) {
    val dest = File(destination, parent)
    logs.forEach { log ->
        val oldName = log.first
        val newName = log.second
        groups[oldName]!!.forEach { file ->
//            FileUtils.moveFile(file, File(dest, "$newName.${file.extension}"))    // 빠른 처리, 원본 유지 X
            file.copyTo(File(dest, "$newName.${file.extension}"))   // 속도 신경 안쓰면 원본 유지
        }
    }
}

/**
 * json 내용을 수정한다.
 */
private fun reformatJsonFiles(parent: String) {
    FileUtils.listFiles(File(destination, parent), TrueFileFilter.TRUE, TrueFileFilter.TRUE)
        .groupBy { it.nameWithoutExtension }
        .forEach { group ->
            val jsonFile = group.value.find { it.extension == "json" }!!
            val image = group.value.find { it.extension != "json" }!!
            val content = FileUtil.readJsonFile(jsonFile)
            val newContent = JsonUtil.getNewJSONObject(image, content)  // bbox의 크기가 하드코딩됐는지 확인
            FileUtil.writeJsonFile(jsonFile, newContent)
        }
}
