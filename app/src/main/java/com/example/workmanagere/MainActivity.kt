package com.example.workmanagere

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.workmanagere.ui.theme.WorkManagereTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkManagereTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val permState = rememberPermissionState(
                        permission = android.Manifest.permission.POST_NOTIFICATIONS,
                        )
                    if(!permState.status.isGranted){
                        LaunchedEffect(key1 = true){
                            permState.launchPermissionRequest()
                        }
                    }
                    else{
                        val coroutineScope = rememberCoroutineScope()
                        MyScreenContent{
                            coroutineScope.launch {
                                val workRequest = OneTimeWorkRequest.Builder(
                                    MyCustomWorker1::class.java
                                ).build()
                                WorkManager
                                    .getInstance(applicationContext)
                                    .enqueue(workRequest)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyScreenContent(
    performTask: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Button(onClick = performTask) {
            Text(text = "Start Task")
        }
    }
}

class MyCustomWorker1(
   private val context: Context,

   private val params: WorkerParameters
): CoroutineWorker(context, params) {
    override suspend fun doWork() : Result {
        return try {
            delay(1000)
            Log.d("TAG", "My Custom Worker 1: onWork()")
            MyNotification(context,
                "My Awesome Task Completed",
                "My Custom Worker 1: doWork()")
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

fun MyNotification(context: Context, title:String, content:String){
    val notificationMgr = context.getSystemService(NotificationManager::class.java)
    val notification = Notification.Builder(context, "work_manager_001")
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .build()
    notificationMgr.notify(1, notification)
}
