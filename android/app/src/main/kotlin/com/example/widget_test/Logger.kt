import android.os.SystemClock
import android.util.Log

object Logger {
    private const val TAG = "widget_test"

    fun i(source: String, message: String) {
        val time = SystemClock.elapsedRealtime()
        Log.i(TAG, "$time $source: $message")
    }

    fun e(source: String, message: String, throwable: Throwable? = null) {
        Log.e(TAG, "$source: $message", throwable)
    }
}
