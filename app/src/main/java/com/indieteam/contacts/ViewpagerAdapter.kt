package contacts.indieteam.contacts

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class ViewpagerAdapter(fragmentManager: FragmentManager, val layout: ArrayList<Fragment>): FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return layout[position]
    }

    override fun getCount(): Int {
        return layout.size
    }


}