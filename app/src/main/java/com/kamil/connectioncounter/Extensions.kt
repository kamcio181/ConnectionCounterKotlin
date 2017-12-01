package com.samsung.android.sdk.b2b.samples.wificlientserverdemo

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
}

fun AppCompatActivity.replaceFragment(fragment : Fragment, containerId : Int = R.id.container){
    supportFragmentManager.inTransaction { replace(containerId, fragment) }
}

fun EditText.trimmedText(): String{
    return text.toString().trim()
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

fun Int.convertIpAddressToString(): String?{
    var temp = this

    if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
        temp = Integer.reverseBytes(this)
    }

    val ipByteArray = BigInteger.valueOf(temp.toLong()).toByteArray()


    return try {
        InetAddress.getByAddress(ipByteArray).hostAddress
    } catch (ex: UnknownHostException) {
        logError("convertIpAddressFromIntToString: Unable to get host address.")
        null
    }
}

fun View.visible(show: Boolean){
    visibility = if(show) View.VISIBLE else View.GONE
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun RecyclerView.ViewHolder.setOnClickListener(listener: View.OnClickListener){
    itemView.setOnClickListener(listener)
}