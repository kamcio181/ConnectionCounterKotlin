package com.kamil.connectioncounter

import android.app.Activity
import android.app.Service
import android.content.Context
import android.hardware.display.DisplayManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import java.util.concurrent.TimeUnit

//inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
//    beginTransaction().func().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
//}
//
//fun AppCompatActivity.replaceFragment(fragment : Fragment, containerId : Int = R.id.container){
//    supportFragmentManager.inTransaction { replace(containerId, fragment) }
//}

fun EditText.trimmedText(): String{
    return text.toString().trim()
}

fun EditText.length(): Int{
    return trimmedText().length
}

fun EditText.isEmpty(): Boolean{
    return trimmedText().isEmpty()
}

fun Any.logDebug(message: String){
    Log.d(javaClass.simpleName, message)
}

fun Any.logError(message: String){
    Log.e(javaClass.simpleName, message)
}

fun Activity.toast(message: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, message, duration).show()
}

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT){
    activity.toast(message, duration)
}

fun Service.isScreenOn(): Boolean{
    val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val displays = displayManager.displays
    displays.forEach { if(it.state != Display.STATE_OFF) return true }
    return false
}

fun Activity.formatDuration(duration: Long, addSeconds: Boolean = true): String{
    return formatDuration(duration, addSeconds)
}

fun Service.formatDuration(duration: Long, addSeconds: Boolean = true): String{
    return formatDuration(duration, addSeconds)
}

private fun formatDuration(duration: Long, addSeconds: Boolean = true): String{
    val hours = TimeUnit.SECONDS.toHours(duration)
    val minutes = TimeUnit.SECONDS.toMinutes(duration) - hours * 60
    return if(addSeconds) {
        val seconds = duration - minutes * 60 - hours * 3600
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", hours, minutes)
    }
}

fun Activity.inflate(layoutRes: Int): View {
    return layoutInflater.inflate(layoutRes, null)
}

//fun View.visible(show: Boolean){
//    visibility = if(show) View.VISIBLE else View.GONE
//}
//
//fun ViewGroup.inflate(layoutRes: Int): View {
//    return LayoutInflater.from(context).inflate(layoutRes, this, false)
//}
//
//fun RecyclerView.ViewHolder.setOnClickListener(listener: View.OnClickListener){
//    itemView.setOnClickListener(listener)
//}