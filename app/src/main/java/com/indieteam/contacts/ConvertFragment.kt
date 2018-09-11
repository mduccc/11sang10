package com.indieteam.contacts

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import contacts.indieteam.contacts.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_convert.*

class ConvertFragment : Fragment() {

    private lateinit var header: TextView
    private lateinit var result: TextView
    private lateinit var button: Button
    private var sX = 0f
    private var sY = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_convert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        ads()
        event()
    }

    private fun ads(){
        val adRequest = AdRequest.Builder().build()
        bottom_ads_1.loadAd(adRequest)
        bottom_ads_1.layoutParams.height = (sY*7).toInt()
    }

    private fun setUI(){
        sX = (activity as MainActivity).sX
        sY = (activity as MainActivity).sY

        header = TextView(activity)
        header.let{
            it.text = "Chuyển danh bạ 11 số sang 10 số"
            it.textSize = 20f
            it.typeface = Typeface.DEFAULT_BOLD
            it.setTextColor(resources.getColor(R.color.colorDarkBlue))
            it.measure(0,0)
            it.x = sX*50 - it.measuredWidth/2
            it.y = sY*22
            convert_fragment.addView(it)
        }

        result = TextView(activity)
        result.let{
            it.text = "* Tự động chuyển tương thích với các nhà mạng (Viettel, Vinaphone, Mobifone, Vietnamobie, Gmobie)"
            it.textSize = 10f
            it.setTextColor(resources.getColor(R.color.colorDarkBlue))
            it.measure(0,0)
            it.y = sY*88
            convert_fragment.addView(it)
            it.gravity = Gravity.CENTER
            it.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        }

        button = Button(activity)
        button.let {
            it.text = "Chuyển"
            if(Build.VERSION.SDK_INT < 16){
                it.setBackgroundDrawable(resources.getDrawable(R.color.colorDarkBlue))
            }else {
                it.background = resources.getDrawable(R.color.colorDarkBlue)
            }
            it.setTextColor(resources.getColor(R.color.colorWhite))
            it.measure(0,0)
            it.textSize = 10f
            it.x = sX*50 - it.measuredWidth/2
            it.y = sY*54 - it.measuredHeight/2
            convert_fragment.addView(it)
        }
    }

    private fun event(){
        button.setOnClickListener{
            ThreadUpdate().thread.start()
        }
    }

    inner class ThreadUpdate: Runnable{

        val thread = Thread(this)

        override fun run() {
            ProcessUpdateContacts(activity as MainActivity).let {
                it.readContacts()
                val dialog =  DialogProcess(activity!!)
                activity?.runOnUiThread {
                    dialog.showDialog()
                    dialog.dialog.setCanceledOnTouchOutside(false)
                }
                it.updateContacts()
                activity?.runOnUiThread {
                    Toasty.success(activity!!, "Đã chuyển tất cả danh bạ sang 10 số", Toast.LENGTH_SHORT).show()
                    dialog.dialog.cancel()
                    Handler().postDelayed({
                        (activity as MainActivity).adsVideo()
                    },500)
                }
            }
        }

    }

}
