package contacts.indieteam.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CONTACTS = 1
    var sX = 0f
    var sY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){
        permission()
        getScreen()
    }

    private fun setUI(){
        setSupportActionBar(toolbar_)
        title = "Chuyển đổi danh bạ"
        val layout = arrayListOf(ConvertFragment(), ConvertAgainFragment())
        view_pager.adapter = ViewpagerAdapter(supportFragmentManager, layout)
        view_pager.currentItem = 0

        tab_layout.setupWithViewPager(view_pager)
        for (i in 0 until tab_layout.tabCount){
            when(i){
                0 ->{
                    tab_layout.getTabAt(i)?.text = "11 sang 10"
                }
                1 ->{
                    tab_layout.getTabAt(i)?.text = "10 sang 11"
                }
                else ->{

                }
            }
        }
    }

    private fun getScreen(){
        val manager = windowManager.defaultDisplay
        val point  = Point()
        manager.getSize(point)
        sX = point.x/100f
        sY = point.y/100f
    }

    private fun permission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.WRITE_CONTACTS), PERMISSIONS_REQUEST_CONTACTS)
            }else{
                setUI()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSIONS_REQUEST_CONTACTS){
            if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUI()
            }
            else
                Toasty.error(this, "Permission is not granded", Toast.LENGTH_SHORT).show()
        }
    }

}