package com.yassine.journaltrading.utils

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    @JvmStatic
    fun formatDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("M/d/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

            val parsedDate = inputFormat.parse(date)
            return parsedDate?.let { outputFormat.format(it) } ?: date
        }catch (e:Exception){
            "NaN"
        }
        }
}