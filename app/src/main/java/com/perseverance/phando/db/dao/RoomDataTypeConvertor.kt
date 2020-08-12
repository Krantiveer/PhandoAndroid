package com.perseverance.phando.db.dao

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomDataTypeConvertor {

    @TypeConverter
    fun convertStringToBehaviorList(value: String?): ArrayList<String> {
        value ?: return arrayListOf()
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun convertBehaviorListToString(valueList: ArrayList<String>?): String {
        valueList ?: return ""
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().toJson(valueList, type)
    }


}