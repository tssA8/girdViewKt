package com.example.testplayersample.Beans
import java.io.Serializable

object DemoVideoInfoBean {
    private const val TAG = "DemoVideoInfoBean"
    private val photoList: List<Int>? = null
    private const val IMAGE_JPEG = "image/jpeg"
    private const val VIDEO_MP4 = "video/mp4"
    private const val TEXT_PLAIN = "text/plain"
    private const val THUMB_JPG = "thumb.jpg"

    class DemoVideoFileBean : Serializable {
        var imageName = ""
        var imageUrl = ""
        var snapshotName = ""
        var snapshotUrl = ""
        var videoName = ""
        var videoUrl = ""
        var videoLength = ""
        var gpsName = ""
        var gpsUrl = ""
        var eventName = ""
        companion object {
            private const val serialVersionUID = -1276958093766354586L
        }
    }
}