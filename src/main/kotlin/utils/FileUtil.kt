package utils

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File
import javax.imageio.ImageIO

object FileUtil {
    /**
     * @return IntArray { it[0]: width, it[1]: height }
     *
     * @sample
     * val imageSize = ImageUtil.getImageSize(image)
     * val width = imageSize[0]
     * val height = imageSize[1]
     */
    fun getImageSize(image: File): IntArray {
        ImageIO.createImageInputStream(image).use {
            val reader = ImageIO.getImageReaders(it).next()
            reader.setInput(it, true)
            return intArrayOf(reader.getWidth(0), reader.getHeight(0))
        }
    }

    private const val dpi = 72F

    /**
     * @return List<File> { images from pdf }
     */
    fun convertPdfToImage(pdf: File): List<File> {
        val images = mutableListOf<File>()

        val extension = "png"
        PDDocument.load(pdf).use {
            val renderer = PDFRenderer(it)
            for (page in 0 until it.numberOfPages) {
                val renderedImage = renderer.renderImageWithDPI(page, dpi, ImageType.RGB)
                val image = File(pdf.parentFile, String.format("${pdf.nameWithoutExtension}_%02d.$extension", page))
                ImageIO.write(renderedImage, extension, image)

                images.add(image)
            }
        }

        return images
    }
}
