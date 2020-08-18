package com.example.testplayersample.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.testplayersample.Beans.DemoVideoInfoBean.DemoVideoFileBean
import com.example.testplayersample.R
import com.example.testplayersample.setBitmapToView

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

class DemoVideoAdapter(
    fileBeanList: ArrayList<DemoVideoFileBean>?,
    context: Context
) : BaseAdapter() {
    private val TAG = this.javaClass.simpleName
    private val context: Context
    private var cloudBackFileBeanList: ArrayList<DemoVideoFileBean>? = ArrayList()
    private var lastPosition = -1
    override fun getCount(): Int {
        return if (cloudBackFileBeanList != null && cloudBackFileBeanList!!.size > 0) {
            cloudBackFileBeanList!!.size
        } else {
            0
        }
    }

    override fun getItem(position: Int): Any {
        return cloudBackFileBeanList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val gridViewImageHolder: ViewHolder
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.photo_layout, null)
            gridViewImageHolder =
                ViewHolder()
            gridViewImageHolder.photo = view.findViewById<View>(R.id.iv_photo) as ImageView
            gridViewImageHolder.tvTitle = view.findViewById<View>(R.id.tv_title) as TextView
            gridViewImageHolder.llDemoVideoContainer = view.findViewById<View>(R.id.ll_demo_video_container) as LinearLayout
            gridViewImageHolder.flDemoVideo = view.findViewById<View>(R.id.fl_demo_video) as FrameLayout
            view.tag = gridViewImageHolder
        } else {
            gridViewImageHolder =
                view.tag as ViewHolder
        }
//        Log.d(TAG, "AAA_getView position : $position")
        if (cloudBackFileBeanList != null && cloudBackFileBeanList!!.size > 0) {
            val bean = cloudBackFileBeanList!!.get(position)
            if (bean != null) {
                val videoUrl = bean.videoUrl
                val videoName = bean.videoName
//                Log.d(TAG, "AAA_LoadBitmapUtilKt position : $position")
                if (lastPosition != position && !TextUtils.isEmpty(videoUrl)) setBitmapToView(
                    gridViewImageHolder.photo!!,
                    videoUrl
                )
                if (TextUtils.isEmpty(videoName)) gridViewImageHolder.tvTitle!!.text =
                    "" else gridViewImageHolder.tvTitle!!.text = videoName
                gridViewImageHolder.llDemoVideoContainer!!.setOnClickListener {
                    if (!TextUtils.isEmpty(videoUrl)) setOpenFileBehavior(videoUrl)
                }
            }
        }
        lastPosition = position
        return view!!
    }

    internal class ViewHolder {
        var photo: ImageView? = null
        var tvTitle: TextView? = null
        var llDemoVideoContainer: LinearLayout? = null
        var flDemoVideo: FrameLayout? = null
    }

    init {
        cloudBackFileBeanList!!.clear()
        cloudBackFileBeanList = fileBeanList
        this.context = context
        val lastPosition = -1
    }

    fun setOpenFileBehavior(videoUrl: String) = runBlocking<Unit> {
        launch {
            println("setOpenFile")
        }
        if (!TextUtils.isEmpty(videoUrl)) {
            val file = File(videoUrl)
            if (file.exists()) openFile(file)
            else println("file not found error")
        }
    }

    // 打开视频代码。打开图片和音乐代码类似
    private fun openFile(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "video/*")
        context.startActivity(intent)
        /*
        int mode = 0;  // mode 可选为 0 Full，1  Half，2 Default,  3 Mini
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("key_window_size_level", mode);
        intent.putExtra("key_x_position", 1000); // 窗口左上角x位置
        intent.putExtra("key_y_position", 800);   // 窗口左上角yw位置*/
    }


}