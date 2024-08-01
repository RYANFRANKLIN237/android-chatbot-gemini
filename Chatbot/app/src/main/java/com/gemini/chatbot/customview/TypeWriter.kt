package com.gemini.chatbot.customview

import android.content.Context
import android.os.Handler
import android.util.AttributeSet


class TypeWriter : androidx.appcompat.widget.AppCompatTextView {
    private var mText: CharSequence? = null
    private var mIndex = 0
    private var mDelay: Long = 500 //default 500ms delay
    private val mHandler = Handler()
    private val characterAdder: Runnable = object : Runnable {
        override fun run() {
            text = mText!!.subSequence(0, mIndex++)
            if(mIndex <= mText!!.length){
                mHandler.postDelayed(this, mDelay)
            }
        }
    }

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    fun animateText(text: CharSequence?){
        mText = text
        mIndex = 0
        setText("")
        mHandler.removeCallbacks(characterAdder)
        mHandler.postDelayed(characterAdder, mDelay)
    }

    fun setCharacterDelay(millis: Long){
        mDelay = millis
    }
}