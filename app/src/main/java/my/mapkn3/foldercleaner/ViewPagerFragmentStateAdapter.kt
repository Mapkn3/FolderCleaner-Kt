package my.mapkn3.foldercleaner

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentStateAdapter(private val items: List<Fragment>,
                                    fragmentManager: FragmentManager,
                                    lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position]
}