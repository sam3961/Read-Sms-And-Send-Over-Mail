package com.app.readsms


import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.AsyncTask
import android.util.Log

const val SAVED_INT_KEY = "int_key";

// Small play code for JobSchedulers
class DeepJobService : JobService() {

    lateinit var params: JobParameters
    lateinit var task: CounterTask
    var TAG = DeepJobService::class.java.simpleName

    // Whenever the contraints are satisfied this will get fired.
    override fun onStartJob(params: JobParameters?): Boolean {
        // We land here when system calls our job.
        this.params = params!!
        val start = getValue()

        Log.d(TAG, "onStartJob: "+params.extras.getString("data"))

        task = CounterTask(this,start)          // Not the best way in prod.
        task.execute(Unit)

        return true     // Our task will run in background, we will take care of notifying the finish.
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        task.cancel(true)       // Cancel the counter task.
        Log.d(DeepJobService::class.java.simpleName, "Job paused.")
        return true
        // I want it to reschedule so returned true, if we would have returned false, then job would have ended here.
        // It would not fire onStartJob() when constraints are re satisfied.
    }

    private fun getValue(): Int {
        val prefs = getSharedPreferences("deep_service", Context.MODE_PRIVATE)
        // Try to fetch a preference.
        val start = prefs.getInt(SAVED_INT_KEY, 0)
        return start
    }

    fun notifyJobFinished() {
        Log.d(DeepJobService::class.java.simpleName,"Job finished. Calling jobFinished()")
        val prefs = getSharedPreferences("deep_service", Context.MODE_PRIVATE)
        // Try to fetch a preference.
        prefs.edit().putInt(SAVED_INT_KEY,0).apply()
        // Job has finished now, calling jobFinishedI(false) will release all resources and
        // false as we do not want it to reschedule as the job is done now.
        jobFinished(params,false)
    }


    /**
     * Task which performs the counting with added delay. Before it starts, it picks up the value
     * which has been already printed from previous onStartJob() calls.
     */
    class CounterTask(private val params: DeepJobService, var startInt: Int) : AsyncTask<Unit,Int,Unit>() {
        private val LIMIT = 100
        private var start = 0

        override fun onPreExecute() {
            super.onPreExecute()
            // Pick the last value which was saved in the last execution and continue from there.
            start = params.getValue()
        }
        override fun doInBackground(vararg params: Unit?) {
            for(i in start .. LIMIT) {
                if (!isCancelled) {         // Execute only if job is not cancelled. on every
                    // stopJob() we will cancel this CounterTask
                    Thread.sleep(400)
                    if (startInt < LIMIT) {
                        publishProgress(startInt++)
                    }
                }
            }
        }

        // Write the completed status after each work is finished.
        override fun onProgressUpdate(vararg values: Int?) {
            Log.d(DeepJobService::class.java.simpleName, "Counter value: ${values[0]}")
            val prefs = params.getSharedPreferences("deep_service", Context.MODE_PRIVATE)
            // Try to fetch a preference and add current progress.
            values[0]?.let { prefs.edit().putInt(SAVED_INT_KEY, it).commit() }
        }

        // Once job is finished, reset the preferences.
        override fun onPostExecute(result: Unit?) {
            params.notifyJobFinished()
        }
    }
}