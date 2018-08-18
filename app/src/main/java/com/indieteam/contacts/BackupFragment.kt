package com.indieteam.contacts

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import contacts.indieteam.contacts.R
import kotlinx.android.synthetic.main.fragment_backup.*

class BackupFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
    }

    private fun setUI(){
        val sX = (activity as MainActivity).sX
        val sY = (activity as MainActivity).sY
        val textView = TextView(activity)
        textView.let{
            it.text = "Sao lưu và khôi phục danh bạ"
            it.textSize = sX*2f
            it.measure(0,0)
            it.x = sX*50 - it.measuredWidth/2
            it.y = sY*10
            backup_fragment.addView(it)
        }

        val btnbackup = Button(activity)
        btnbackup.let {
            it.text = "Sao lưu"
            it.textSize = sX*1f
            it.measure(0,0)
            it.x = sX*50 - it.measuredWidth/2
            it.y = sY*40 - it.measuredHeight/2
            backup_fragment.addView(it)
        }

        val btnRestore = Button(activity)
        btnRestore.let {
            it.text = "Khôi phục"
            it.textSize = sX*1f
            it.measure(0,0)
            it.x = sX*50 - it.measuredWidth/2
            it.y = sY*50 - it.measuredHeight/2
            backup_fragment.addView(it)
        }
    }
}
