package utils

import java.io.File
import javax.imageio.ImageIO

object ImageUtil {
    fun getImageSize(image: File): IntArray {
        ImageIO.createImageInputStream(image).use {
            val reader = ImageIO.getImageReaders(it).next()
            reader.setInput(it, true)
            return intArrayOf(reader.getWidth(0), reader.getHeight(0))
        }
    }
}
