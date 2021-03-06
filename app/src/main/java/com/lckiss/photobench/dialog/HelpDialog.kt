package com.lckiss.photobench.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lckiss.photobench.databinding.HelpBinding

class HelpDialog : DialogFragment() {

    lateinit var inflate: HelpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflate = HelpBinding.inflate(LayoutInflater.from(context), container, false)
        return inflate.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflate.content.text="""
            使用说明：
                本人最近有点闲，搞丢了一些数据，恢复回来照片时间全乱了。于是写了这么个破玩意儿。
            支持功能：
                1、对于格式为小米拍照时间的照片，即格式为：IMG_20210117_230159.jpg 的文件，在 exif 信息时间为空的情况下，可将时间修改为文件名中的时间，同时将文件属性 lastModifyTime 改为文件名中的时间。
                2、对于照片 exif 信息中的时间不为空时，文件属性 lastModifyTime 将被修改为 exif 信息中的时间。
            怎么用:
                1、找到你需要纠正的可爱的相册目录，点进去，右下角的批量处理点一下完事儿。
                2、对于单个文件，可直接单击修改 lastModifyTime 的年份，想问为什么没有时间设置？我觉得没什么必要。
                3、可通过更改文件名为 IMG_20210117_230159.jpg 格式，来修改 lastModifyTime 与 exif 时间信息（如果为空的情况下）。
            其他说明：
                1、exif 信息的修改只支持 jpg 文件，不要犯傻把 png 改后缀为 jpg，那将毫无用处。
                2、我只有小米手机，不保证支持其他机型，目前安卓版本 Android 11，MIUI 12.5。
                3、有空再开源，不要骂我代码写得烂。。。
                4、什么？手机相册不支持排序？砸了吧。
                
            求人不如求己。
        """.trimIndent()
    }

}