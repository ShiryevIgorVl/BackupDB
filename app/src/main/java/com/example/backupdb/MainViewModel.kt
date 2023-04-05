package com.example.backupdb

import android.os.Environment
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainViewModel(dataBase: MainDataBase) : ViewModel() {

    val dao = dataBase.getDao()

    val allPoint: LiveData<List<Point>> = dao.getAllPoint().asLiveData()

    fun insertPoint(point: Point) = viewModelScope.launch { dao.insertPoint(point) }

    fun deleteDB() = viewModelScope.launch { dao.deleteAllTable() }

    fun exportDBxls (pointList: List<Point>, APP_NAME: String) = viewModelScope.launch {
        val wb = HSSFWorkbook()
        val sheet = wb.createSheet(APP_NAME)

        val row = sheet.createRow(0)

        row.createCell(0).setCellValue("#")
        row.createCell(1).setCellValue("nameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
        row.createCell(2).setCellValue("age")



        for (i in 0..pointList.size - 1) {
            val nextRow = sheet.createRow(i + 1)
            nextRow.createCell(0).setCellValue(pointList[i].id.toString())
            nextRow.createCell(1).setCellValue(pointList[i].name)
            nextRow.createCell(2).setCellValue(pointList[i].age.toString())
        }

        val FILE_NAME = "backup.xls"
        val newFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            FILE_NAME
        )
        val outputFile: OutputStream = FileOutputStream(newFile)
        try {
            wb.write(outputFile)

        } catch (e: IOException) {
            // TODO:
        } finally {
            wb.close()
            withContext(Dispatchers.IO) {
                outputFile.close()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    //В соответствии с рекомендациями Google Android
    class MainViewModelFactory(val dataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}