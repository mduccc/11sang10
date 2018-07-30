package contacts.indieteam.contacts

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_convert_fragment.*

class ConvertAgainFragment : Fragment() {

    private lateinit var header: TextView
    private lateinit var result: TextView
    private lateinit var button: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_convert_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        event()
    }

    private fun setUI(){
        val sX = (activity as MainActivity).sX
        val sY = (activity as MainActivity).sY

        header = TextView(activity)
        header.let{
            it.text = "Chuyển danh bạ 10 số sang 11 số "
            it.textSize = sX*2f
            it.typeface = Typeface.DEFAULT_BOLD
            it.setTextColor(resources.getColor(R.color.colorDarkBlue))
            it.measure(0,0)
            it.x = sX*50 - it.measuredWidth/2
            it.y = sY*20
            convert_fragment.addView(it)
        }

        result = TextView(activity)
        result.let{
            it.text = "* Tự động chuyển tương thích với các nhà mạng (Viettel, Vinaphone, Mobifone, Vietnamobie, Gmobie)"
            it.textSize = sX*1f
            it.setTextColor(resources.getColor(R.color.colorDarkBlue))
            it.measure(0,0)
            it.y = sY*89
            convert_fragment.addView(it)
            it.gravity = Gravity.CENTER
            it.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        }

        button = Button(activity)
        button.let {
            it.text = "Chuyển"
            it.textSize = sX*1f
            it.background = resources.getDrawable(R.color.colorDarkBlue)
            it.setTextColor(resources.getColor(R.color.colorWhite))
            it.measure(0,0)
            it.x = sX*50 - it.measuredWidth/2
            it.y = sY*53 - it.measuredHeight/2
            convert_fragment.addView(it)
        }
    }

    private fun event(){
        button.setOnClickListener{
            TheardUpdate().theard.start()
        }
    }

    inner class TheardUpdate: Runnable{

        val theard = Thread(this)

        override fun run() {
            ProcessUpdateContactsAgain(activity!!).let {
                it.readContacts()
                val dialog =  DialogProcess(activity!!)
                activity?.runOnUiThread {
                    dialog.showDialog()
                    dialog.dialog.setCanceledOnTouchOutside(false)
                }
                it.updateContacts()
                activity?.runOnUiThread {
                    Toasty.success(activity!!, "Đã chuyển tất cả danh bạ sang 11 số", Toast.LENGTH_SHORT).show()
                    dialog.dialog.cancel()
                }
            }
        }

    }
}
