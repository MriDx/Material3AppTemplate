package com.mridx.androidtemplate.utils

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DatePicker {

    private val sdf by lazy {
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

    private fun formatTimeStampToDate(s: Long): String {
        return sdf.format(s)
    }

    private fun formatTimeStampToDate(s: Long, format: SimpleDateFormat): String {
        return format.format(s)
    }

    private fun formatDateToShow(s: Long): String {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(s))
    }


    fun showPicker(
        fragmentManager: FragmentManager,
        title: String? = null,
        listener: (date: String, toShow: String, cancelled: Boolean) -> Unit
    ) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(title ?: "Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.addOnPositiveButtonClickListener {
            it?.let { s ->
                val date = formatTimeStampToDate(s)
                val date2 = formatTimeStampToDate(s, SimpleDateFormat("dd MMM yyyy", Locale.US))
                val toShow = formatDateToShow(s)
                listener.invoke(date, date2, false)
                datePicker.dismiss() //dismiss
            }
        }
        datePicker.addOnNegativeButtonClickListener {
            //
            listener.invoke("", "", true)
            datePicker.dismiss() //dismiss
        }
        datePicker.show(fragmentManager, "MATERIAL_DATE_PICKER")
    }

}