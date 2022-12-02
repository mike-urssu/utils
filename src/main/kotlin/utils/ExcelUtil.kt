package utils

import mu.KotlinLogging
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

//Requires
//implementation("org.apache.commons:commons-collections4:4.4")
//implementation("org.apache.poi:poi:5.2.3")
//implementation("org.apache.poi:poi-ooxml:5.2.3")
//implementation("org.apache.poi:poi-ooxml-lite:5.2.3")
//implementation("org.apache.xmlbeans:xmlbeans:5.1.1")
//implementation("org.apache.logging.log4j:log4j-api:2.19.0")
//implementation("org.apache.logging.log4j:log4j-core:2.19.0")

object ExcelUtil {
    private val log = KotlinLogging.logger { }

    fun readExcel(excel: File) {
        XSSFWorkbook(FileInputStream(excel)).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            val rowIndices = sheet.physicalNumberOfRows
            log.info { "rowIndices: $rowIndices" }

            for (rowIndex in 0 until rowIndices) {
                val row = sheet.getRow(rowIndex)
                if (row != null) {
                    val columnIndices = row.physicalNumberOfCells
                    log.info { "columnIndices: $columnIndices" }

                    for (columnIndex in 0 until columnIndices) {
                        val cell = row.getCell(columnIndex)
                        if (cell != null) {
                            val value = when (cell.cellType) {
                                CellType.FORMULA -> cell.cellFormula.toString()
                                CellType.NUMERIC -> cell.numericCellValue.toString()
                                CellType.STRING -> cell.stringCellValue.toString()
                                CellType.BLANK -> cell.booleanCellValue.toString()
                                CellType.ERROR -> cell.errorCellValue.toString()
                                else -> {
                                    cell.errorCellValue.toString()
                                }
                            }
                            log.info { "($rowIndex, $columnIndex): $value" }
                        }
                    }
                }
            }
        }
    }
}
