package utils

import java.io.File
import javax.imageio.ImageIO

object ImageUtil {
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
}
