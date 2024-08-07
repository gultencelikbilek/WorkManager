package com.example.workmanagerkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.workmanagerkotlin.ui.theme.WorkManagerKotlinTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkManagerKotlinTheme {
                val data = Data.Builder().putInt("intKey", 1)
                    .build() //burdan worker classına veri göndermek istersek

                //Constraints’ nesnesi oluşturarak ağ türünü ve şarj durumu gereksinimlerini belirtiyoruz.
                val constraints = Constraints.Builder()
                    .setRequiresCharging(false) //düşük şarj olmasın
                    //.setRequiredNetworkType(NetworkType.CONNECTED)//internete bağlı olsun
                    .build()
                /*
                val myWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<RefreshDatabase>()
                    .setConstraints(constraints)
                    .setInputData(data)
                   // .setInitialDelay(5,TimeUnit.HOURS)
                   // .addTag("myTag")
                    .build()
                WorkManager.getInstance(this).enqueue(myWorkRequest)
              */

                val myWorkRequest: PeriodicWorkRequest =
                    PeriodicWorkRequestBuilder<RefreshDatabase>(
                        15,
                        TimeUnit.MINUTES
                    ) //parametre olrak bizden zaman istiyor
                        //15 dakikadan az olamaz.
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build()
                WorkManager.getInstance(this).enqueue(myWorkRequest)


                WorkManager.getInstance(this).getWorkInfoByIdLiveData(myWorkRequest.id)
                    .observe(this,
                        Observer {//işimizin durumlarını  kontrol ederiz
                            if (it.state == WorkInfo.State.RUNNING) { //iş çalışıyor
                                println("running")
                            } else if (it.state == WorkInfo.State.FAILED) { //iş bozuldu
                                println("failed")
                            } else if (it.state == WorkInfo.State.SUCCEEDED) { //iş başarılı
                                println("succeded")
                            }
                        })

                // WorkManager.getInstance(this).cancelAllWork() //işleri iptal edebiliriz.Çeşitli iptal seçenekleri de vardır

                //Channing : OneTimeRequestlerde kullanılabiliyor.
                val oneTimeRequest: OneTimeWorkRequest =
                    OneTimeWorkRequestBuilder<RefreshDatabase>()
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build()
                WorkManager.getInstance(this).beginWith(oneTimeRequest)
                    .then(oneTimeRequest)
                    .then(oneTimeRequest)
                    .enqueue()

            }
        }
    }
}

