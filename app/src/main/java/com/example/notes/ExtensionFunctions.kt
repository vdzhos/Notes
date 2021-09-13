package com.example.notes

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.setFocusAndKeyBoardOnView(view: View){
    if(view.requestFocus()){
        val imm: InputMethodManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED)
    }
}

fun Activity.removeFocusAndKeyBoard(){
    val view = currentFocus
    if(view != null){
        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}

//fun <T, K : Comparable<K>> List<T>.binarySearchDA(key: K?, fromIndex: Int = 0, toIndex: Int = size, comparator: Boolean = true, selector: (T) -> K?): Int {
//
//    var low = fromIndex
//    var high = toIndex - 1
//
//    val cmpT: (Int, Int) -> Boolean = { i: Int, i1: Int ->
//        if(comparator) i<=i1
//        else i1<=i
//    }
//
//    while (cmpT(low,high)) {
//        val mid = (low + high).ushr(1) // safe from overflows
//        val midVal = get(mid)
//        val cmp = compareValues(selector(midVal), key)
//
//        if (cmp < 0)
//            low = mid + 1
//        else if (cmp > 0)
//            high = mid - 1
//        else
//            return mid // key found
//    }
//    return -(low + 1)  // key not found
//}