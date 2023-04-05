package com.example.backupdb

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import com.example.backupdb.databinding.ActivityMainBinding
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import java.net.URI


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val point = Point(null, "Чертик", 12.0)

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.MainViewModelFactory((applicationContext as App).pointDataBase)
        )
            .get(MainViewModel::class.java)
    }

    val TAG = "MyTag"
    lateinit var decimalFormat: DecimalFormat

    val pointList: ArrayList<Point> = arrayListOf()
    val cellStringList: ArrayList<String> = arrayListOf()
    val pointListBackup: ArrayList<Point> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bSaveDB.setOnClickListener {
            onActionImport()
        }
        binding.bReadDB.setOnClickListener {
            insertPointMain()
        }

        mainViewModel.allPoint.observe(this) {
            pointList.clear()
            for (i in 0..(it.size - 1)) {
                // Log.d(TAG, "viewItemDB List<Point> ${it.size}")
                pointList.add(it[i])
                //  Log.d(TAG, "viewItemDB PointList ${pointList.size}")
            }
        }

        binding.btDeleteDB.setOnClickListener {
            deleteDB()
            pointList.clear()
        }

        binding.btInsertDB.setOnClickListener {
            exportDBxls()
        }

        binding.openFile.setOnClickListener {

        }

    }

    private fun exportDBxls() {
        mainViewModel.exportDBxls(pointList, applicationContext.getString(R.string.app_name))
    }

    private fun createListPoint(): List<Point> {
        val cellList = cellStringList.drop(3)
        val idList = cellList.slice(0..cellList.size - 1 step 3)
        val nameList = cellList.slice(1..cellList.size - 1 step 3)
        val ageList = cellList.slice(2..cellList.size - 1 step 3)
//        Log.d(TAG, "createListPoint idList: ${idList}")
//        Log.d(TAG, "createListPoint nameList: ${nameList}")
//        Log.d(TAG, "createListPoint ageList: ${ageList}")
//        Log.d(TAG, "createListPoint размер: ${cellList.size}")

        for (i in 0..idList.size - 1) {

            //      Log.d(TAG, "idListToInt: ${idListToInt}")

            pointListBackup.add(
                Point(
                    id = idList[i].toDouble().toInt(),
                    name = nameList[i],
                    age = ageList[i].toDouble()
                )
            )

//            Log.d(TAG, "createListPoint Point for: ${pointListBackup}")
        }
//        Log.d(TAG, "createListPoint Point после for: ${pointListBackup}")
        return pointListBackup
    }


    private fun importDataBase(uri: Uri, context: Context) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val wbImport = HSSFWorkbook(inputStream)
        val sheet = wbImport.getSheetAt(0)
        cellStringList.clear()

        for (row: Row in sheet) {
            for (cell: Cell in row) {
                when (cell.cellType) {
                    CellType.NUMERIC -> cellStringList.add(cell.numericCellValue.toString())
                    CellType.STRING -> cellStringList.add(cell.stringCellValue)
                    else -> cellStringList.add("")
                }
            }
        }

//        for (i in 0..cellStringList.size-1){
//           // Log.d(TAG, "importDataBase: ${cellStringList[i]}")
//
//        }
        // Log.d(TAG, "importDataBase размер: ${cellStringList.size}")
        wbImport.close()

        val importList = createListPoint()
        //  Log.d(TAG, "importDataBase importList: ${importList}")
        for (i in 0..importList.size - 1) {
            mainViewModel.insertPoint(importList[i])
        }
    }


    private fun deleteDB() {
        mainViewModel.deleteDB()
    }

    private fun insertPointMain() {
        mainViewModel.insertPoint(point)
    }
// Регистрируем контракт
    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // получаем  Uri из intent
                val intent = result.data
                val uri = intent?.data!!
                // запускаем чтение файла .xls по полученному Uri
                importDataBase(uri, this)
            }
        }

    fun onActionImport() {
        // настраиваем фильтры intent
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        // запускаем контракт
        startForResult.launch(intent)
    }


}