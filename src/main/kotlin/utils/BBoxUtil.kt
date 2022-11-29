package utils

import mu.KotlinLogging
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject
import java.io.File
import java.util.TreeMap
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sqrt

object BBoxUtil {
    private val log = KotlinLogging.logger { }

    /**
     * 기존 bboxes 새로운 bboxes를 추가한다.
     *
     * @param bboxes1 기존 bboxes
     * @param bboxes2 새로운 bboxes
     *
     * @return bboxes1과 bboxes2가 합쳐진 새로운 bboxes (겹쳐진 bbox가 있을 수 있다)
     * @see removeOverlappedBBoxes(bboxes: JSONArray)
     */
    fun appendBBoxes(bboxes1: JSONArray, bboxes2: JSONArray): JSONArray {
        for (i in 0 until bboxes2.length()) {
            bboxes1.put(bboxes2.getJSONObject(i))
        }
        return bboxes1
    }

    /**
     * 기존 bboxes에서 지울 bboxes 영역과 겹치는 모든 bboxes를 제거한다.
     *
     * @param bboxes1 기존 bboxes
     * @param boundaries 지울 bboxes 영역
     */
    fun eraseBBoxes(bboxes1: JSONArray, boundaries: JSONArray): JSONArray {
        val bboxesToErase = JSONArray()
        for (i in 0 until bboxes1.length()) {
            val bbox = bboxes1.getJSONObject(i)
            if (isInBoundary(bbox, boundaries)) {
                bboxesToErase.put(bbox)
            }
        }

        for (i in 0 until bboxesToErase.length()) {
            bboxes1.remove(bboxesToErase.getJSONObject(i))
        }

        return bboxes1
    }

    private fun isInBoundary(bbox: JSONObject, boundaries: JSONArray): Boolean {
        val xs = bbox.getJSONArray("x")
        val ys = bbox.getJSONArray("y")

        for (i in 0 until boundaries.length()) {
            val boundary = boundaries.getJSONObject(i)
            val point1 = Pair(getMin(boundary.getJSONArray("x")), getMin(boundary.getJSONArray("y")))
            val point2 = Pair(getMin(boundary.getJSONArray("x")), getMax(boundary.getJSONArray("y")))
            for (j in 0 until 4) {
                if (xs.getInt(j) in point1.first..point2.first && ys.getInt(j) in point2.second..point2.second) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 겹쳐있는 bbox들을 제거한다.
     *
     * @return 겹쳐있지 않은 bboxes
     */
    fun removeOverlappedBBoxes(jsonFile: File, bboxes: JSONArray): JSONArray {
        val newBBoxes = JSONArray()
        val texts = TreeMap<String, MutableList<JSONObject>>()

        for (i in 0 until bboxes.length()) {
            val bbox = bboxes.getJSONObject(i)
            val data = bbox.getString("data")
            val boundaries = texts.putIfAbsent(data, mutableListOf())!!

            if (!isOverlapped(boundaries, bbox)) {
                newBBoxes.put(bbox)
                boundaries.add(bbox)
            } else {
                log.info { "overlapped: ${jsonFile.name}" }
            }
        }

        return newBBoxes
    }

    private fun isOverlapped(boundaries: List<JSONObject>, bbox: JSONObject): Boolean {
        val limitedDistance = 15
        val center1 = getCenter(bbox.getJSONArray("x"), bbox.getJSONArray("y"))
        for (boundary in boundaries) {
            val center2 = getCenter(boundary.getJSONArray("x"), boundary.getJSONArray("y"))
            if (getDistance(center1, center2) <= limitedDistance) {
                return true
            }
        }
        return false
    }

    private fun getCenter(xs: JSONArray, ys: JSONArray) =
        Pair((getMax(xs) + getMin(xs)) / 2, (getMax(ys) + getMin(ys)) / 2)

    private fun getMin(intArray: JSONArray): Int {
        var min = Int.MAX_VALUE
        for (i in 0 until intArray.length()) {
            min = min.coerceAtMost(intArray.getInt(i))
        }
        return min
    }

    private fun getMax(intArray: JSONArray): Int {
        var max = Int.MIN_VALUE
        for (i in 0 until intArray.length()) {
            max = max.coerceAtLeast(intArray.getInt(i))
        }
        return max
    }

    private fun getDistance(center1: Pair<Int, Int>, center2: Pair<Int, Int>) =
        sqrt(((center2.first - center1.first).toDouble().pow(2) + (center2.second - center1.second).toDouble().pow(2)))
}
