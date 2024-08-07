package com.example.workmanagerkotlin

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class RefreshDatabase(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result { // Result ile bize bir sonuç döndürmemizi istiyor.
        // burda work managerın ne yapmasını istiyorsak o işlemleri buraya yazarız.
        val getData  =inputData //aktivityden bu yolla veri alırız
        val myNumber = getData.getInt("intKey",0)
        refreshDatabase(myNumber)
        return Result.success()
    }

    private fun refreshDatabase(myNumber : Int){
        val sharedPreferences = context.getSharedPreferences("com.example.kotlinworkmanager",
            Context.MODE_PRIVATE) //shared preferencesi açıyrouz
        var mySaveNumber = sharedPreferences.getInt("myNumber",0)
        mySaveNumber = mySaveNumber + myNumber
        println(mySaveNumber)
        sharedPreferences.edit().putInt("myNumber",mySaveNumber).apply()
    }
}