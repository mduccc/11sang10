package contacts.indieteam.contacts

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog

class DialogProcess(private val context: Activity){

    lateinit var dialog: MaterialDialog

    fun showDialog(){
        dialog = MaterialDialog.Builder(context)
                .content("Đang chuyển... (có thể mất tới vài phút)\n")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show()
    }
}