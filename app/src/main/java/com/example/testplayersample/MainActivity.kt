package com.example.testplayersample

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.widget.GridView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.testplayersample.Beans.DemoVideoInfoBean
import com.example.testplayersample.adapters.DemoVideoAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var gridView: GridView? = null
    private var rootView: RelativeLayout? = null
    private var ivQrCode: ImageView? = null
    private var gridViewAdapter: DemoVideoAdapter? = null
    /**
     * @param SDCARD_DIR_APP_ROOT
     * Put Mp4 in [system/media/tutorial]
     * */
    private val SDCARD_DIR_APP_ROOT = "/media/tutorial"
    //Debug
    private val DB_FOLDER_NAME = "DBsaves/mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gridView = findViewById<GridView>(R.id.gridviews)
        ivQrCode = findViewById<ImageView>(R.id.iv_qrcode)
        rootView = findViewById<RelativeLayout>(R.id.root_view)
        disableDeathOnFileUriExposure()
        setUpDemoVideoGridView()
    }


    fun getDemoVideoListFlow(): Flow<ArrayList<DemoVideoInfoBean.DemoVideoFileBean>> = flow {
        emit(getDemoVideoList())
    }

    fun setUpDemoVideoGridView() = runBlocking<Unit> {
        launch {
            println("Set DemoVideo")
        }
        getDemoVideoListFlow().take(1).collect { value -> setUpDemoVideoGridView(value) }
    }

    private fun setUpDemoVideoGridView(resultList: ArrayList<DemoVideoInfoBean.DemoVideoFileBean>?) {
        if (resultList != null) {
            Log.d(TAG, "DemoVideo File size: " + resultList.size)
            if (gridViewAdapter == null) {
                gridViewAdapter = DemoVideoAdapter(resultList, this)
                gridView!!.adapter = gridViewAdapter
                gridViewAdapter!!.notifyDataSetChanged()
            }
        } else {
            Log.e(TAG, "resultList is null")
        }
    }


    private fun getDemoVideoList(): ArrayList<DemoVideoInfoBean.DemoVideoFileBean> {
        val originalDownloadFileBeanLists: ArrayList<DemoVideoInfoBean.DemoVideoFileBean> =
            ArrayList<DemoVideoInfoBean.DemoVideoFileBean>()
        val fileEvents = File(Environment.getRootDirectory().absolutePath + SDCARD_DIR_APP_ROOT)

        ///Debug
//        val fileEvents = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), DB_FOLDER_NAME)

        val files = fileEvents.listFiles()
        if (files.size != 0) {
            for (file in files) {
                if (file.isDirectory) {
                    Log.d(TAG, "file is directory(Warring!!!)")
                } else {
                    val fileAllName = file.name
                    /* Parser 副檔名 */
                    val fileName = fileAllName.split(".").toTypedArray()
                    val fileNameFirst = fileName[0]
                    val fileNameSecond = fileName[1]

                    /* Parser thumb */
                    var eventName = ""
                    if (fileNameSecond == "jpg") eventName = fileNameFirst.replace("_thumb", "")
                    var findIndexInLists = -1
                    var eventDataBean: DemoVideoInfoBean.DemoVideoFileBean =
                        DemoVideoInfoBean.DemoVideoFileBean()
                    for (index in originalDownloadFileBeanLists.indices) {
                        val saveBeanName: String =
                            originalDownloadFileBeanLists[index].eventName
                        Log.d(TAG, """$saveBeanName""".trimIndent())
                        if (fileName[0].contains(saveBeanName)) {
                            eventDataBean = originalDownloadFileBeanLists[index]
                            findIndexInLists = index
                            break
                        }
                    }
                    if (fileNameSecond == "jpg") {
                        if (fileNameFirst.contains("thumb")) {
                            eventDataBean.snapshotUrl = file.absolutePath
                            eventDataBean.snapshotName = file.name
                            eventDataBean.eventName = eventName
                            Log.d(TAG, """$eventName""".trimIndent())
                        } else {
                            eventDataBean.imageUrl = file.absolutePath
                            eventDataBean.imageName = file.name
                            eventDataBean.eventName = fileNameFirst
                        }
                    } else if (fileNameSecond == "mp4") {
                        eventDataBean.videoUrl = file.absolutePath
                        eventDataBean.videoName = file.name
                        eventDataBean.eventName = fileNameFirst
                    } else if (fileNameSecond == "txt") {
                        eventDataBean.gpsUrl = file.absolutePath
                        eventDataBean.gpsName = file.name
                        eventDataBean.eventName = fileNameFirst
                    }
                    if (findIndexInLists < 0) originalDownloadFileBeanLists.add(eventDataBean)
                    else originalDownloadFileBeanLists[findIndexInLists] = eventDataBean
                    Log.d(TAG, "file name: " + file.name + " file path: " + file.absolutePath)
                }
            }
        }
        return originalDownloadFileBeanLists
    }


    /**
     * Android 7.0以上使用file://格式的URI给其他APP时，会抛出FileUriExposedException异常。
     * 但我们仍然希望通过使用file://格式的URI传递给其他App，使用以下方法时使Android不检测file://格式的URI
     */
    private fun disableDeathOnFileUriExposure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val cls = StrictMode::class.java
                val m = cls.getMethod("disableDeathOnFileUriExposure", String::class.java)
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
