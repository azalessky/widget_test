import android.util.Log

object Logger {
    private const val TAG = "widget_test"

    fun i(source: String, message: String) {
        Log.i(TAG, "[$source] $message")
    }

    fun e(source: String, message: String, throwable: Throwable? = null) {
        Log.e(TAG, "[$source] $message", throwable)
    }
}
