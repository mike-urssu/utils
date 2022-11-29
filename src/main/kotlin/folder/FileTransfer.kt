package folder

import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File

private val log = KotlinLogging.logger { }

fun main() {
    val line =
        "151.831192    89.615639    2.728137    1.188190    -1.171126    92.272568    -1.151415    6.270603    0.284070    7.562393    -6.867399    -2.911945    -3.908318    11.614763    -1.573458    -2.755852    0.967461    -5.076863    -68.335167    5.286192    -15.444199    -10.007301    -1.676274    0.777933    74.086365    6.177076    -35.230522    -2.272284    2.849212    10.819938    77.804306    -4.386143    11.498352    13.895043    -3.114357    9.010531    -76.046036    12.256090    58.476738    4.684415    1.021154    3.849106    -7.015901    11.005527    3.980674    -0.107271    -11.525742    -1.239129    0.000000    0.000000    0.000000    0.053133    2.199352    -1.703449    1.103091    8.234710    -2.644592    0.141626    -9.289190    2.844473    0.000000    0.000000    0.000000    "
    println(line.count { it == '.' })
}

fun main01() {
    val type = "BL"
    val srcPath = "E:\\Trade (80%)\\labeling data\\$type"

    val groups = FileUtils.listFiles(File(srcPath), TrueFileFilter.TRUE, TrueFileFilter.TRUE)
        .groupBy { it.nameWithoutExtension }
        .toSortedMap()

    groups.onEachIndexed { index, entry ->
        val dest = File("E:\\Trade\\labeling data\\$type\\${String.format("%d", index / 5000 + 1)}")
        FileUtils.forceMkdir(dest)
        entry.value.forEach { FileUtils.moveFile(it, dest) }
    }
}

fun main02() {
    val types = arrayOf("BL", "CO", "ET", "NV", "PL")

    for (type in types) {
        log.info { "type: $type" }
        val source = File("F:\\2022_물류_80percent+20percent복사본\\labeling data\\${type}")
        val destination = File("E:\\labeling data\\${type}")

        val fileFilter = FileFilterUtils.suffixFileFilter("json")
        val jsonFiles = FileUtils.listFiles(source, fileFilter, TrueFileFilter.TRUE)
        log.info { "jsonFiles: ${jsonFiles.size}" }

        jsonFiles.forEach {
            log.info { it.name }
            FileUtils.copyFile(it, File(destination, it.name))
        }
    }
}
