package com.lckiss.photobench.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lckiss.photobench.databinding.PickerBinding
import java.util.*

class TimePickDialog : DialogFragment() {

    lateinit var inflate: PickerBinding

    var onDatePickListener: ((time: Long) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflate = PickerBinding.inflate(LayoutInflater.from(context), container, false)
        return inflate.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance()
        inflate.dataPicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        { _, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            onDatePickListener?.invoke(calendar.time.time)
        }
    }

}