package cabuser.com.rydz.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import cabuser.com.rydz.R
import cabuser.com.rydz.util.base.BaseActivity
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.edit_header.*
import java.util.*

//this class's main functionality to display list of user's previous trips
class YourTripsActivity : BaseActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        inits()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
        }
    }

    private fun inits() {
        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.yourtrips)
        setupViewPager(viewpager)
        tabs.setupWithViewPager(viewpager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HistoryFragment(), getString(R.string.past))
        adapter.addFragment(UpcomingFragment(), getString(R.string.upcoming))
        viewPager.adapter = adapter
    }

    class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private var mFragmentTitleList = ArrayList<String>() as MutableList<String>
        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }
}

