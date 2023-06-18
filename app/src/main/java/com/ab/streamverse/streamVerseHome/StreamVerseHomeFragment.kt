package com.ab.streamverse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ab.streamverse.databinding.FragmentFirstBinding
import com.ab.streamverse.model.VideoStreamCategory
import com.ab.streamverse.model.StreamDetails
import com.ab.streamverse.recentRelease.RecentReleaseFragment
import com.ab.streamverse.streamVerseHome.StreamDetailsViewHolder
import com.ab.streamverse.streamVerseHome.StreamVerseViewModel
import com.ab.streamverse.util.AdapterUtils
import com.ab.streamverse.util.CircularPagerSnapHelper
import com.ab.streamverse.util.Constants
import com.google.android.material.tabs.TabLayout

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class StreamVerseHomeFragment : Fragment(),StreamDetailsViewHolder.StreamDetailsDelegate {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: StreamVerseViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StreamVerseViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStreamCategoryList()
        viewModel.getRecentTrendVideoList()
        viewModel.getVideoStreamCategory()

        viewModel.recentTrendVideoList.observe(viewLifecycleOwner) { recentTrendList ->
            setUpReleaseCarousel(recentTrendList)
        }
        viewModel.videoStreamCategoryList.observe(viewLifecycleOwner){ videoStreamCategories ->
            setUpStreamCategoryList(videoStreamCategories)
        }
    }

    private fun setUpStreamCategoryList(streamCategoryList : List<VideoStreamCategory>){
        binding.streamCategoryList.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        binding.streamCategoryList.adapter = AdapterUtils.setUpVideoStreamCategoryListAdapter(streamCategoryList,this)
    }

    private fun setUpReleaseCarousel(streamList: List<StreamDetails>){

//        binding.viewpagerTop.adapter = RecentReleaseAdapter(requireFragmentManager(), streamList,false)
//        binding.viewPagerBackground.adapter = RecentReleaseAdapter(requireFragmentManager(), streamList,true)
//
//        binding.viewpagerTop.addOnPageChangeListener(object  : OnPageChangeListener{
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                val width: Int = binding.viewPagerBackground.getWidth()
//                binding.viewPagerBackground.scrollTo((width * position + width * positionOffset).toInt(), 0)
//            }
//
//            override fun onPageSelected(position: Int) {
//                if (position == binding.viewpagerTop.adapter?.count?.minus(1)) {
//                    // Reached the last page, set to the first page
//                    binding.viewpagerTop.currentItem = 0
//                }
//                binding.tabLayout.setScrollPosition(position, 0f, true)
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//                // implement
//            }
//        })


        val layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
        binding.viewPager.layoutManager = layoutManager
//        binding.viewPager.adapter = RecentReleaseAdapter(requireFragmentManager(),streamList)
        binding.viewPager.adapter = AdapterUtils.setUpRecentTrendsAdapter(streamList)

        // Set up the TabLayout
        for (i in 0 until streamList.size) {
            binding.tabLayout.addTab(binding.tabLayout.newTab())
        }

        // Create an instance of CircularPagerSnapHelper
        val circularPagerSnapHelper = CircularPagerSnapHelper()
         // Attach it to the RecyclerView
        circularPagerSnapHelper.attachToRecyclerView(binding.streamCategoryList)

        binding.viewPager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // Update the selected tab based on the first visible item position
                if (firstVisibleItemPosition >= 0 && lastVisibleItemPosition >= 0) {
                    val currentSelectedPosition = binding.tabLayout.selectedTabPosition

                    // Check if the first visible item position is different from the current selected position
                    if (firstVisibleItemPosition != currentSelectedPosition) {
                        binding.tabLayout.getTabAt(firstVisibleItemPosition)?.select()
                    }
                }
            }
        })





//        binding.tabLayout.setupWithViewPager(binding.viewPager, true)
//        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                val position = tab.position
//
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//                //override fun not implemented
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//                //override fun not implemented
//            }
//
//
//        })

//        binding.viewPager.addOnPageChangeListener(object  : OnPageChangeListener{
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//              //override fun not implemented
//            }
//
//            override fun onPageSelected(position: Int) {
//                if (position == binding.viewPager.adapter?.count?.minus(1)) {
//                    // Reached the last page, set to the first page
//                    binding.viewPager.currentItem = 0
//                }
//                binding.tabLayout.setScrollPosition(position, 0f, true)
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//                // implement
//            }
//        })

    }


    override fun selectedStreamVideo(data: StreamDetails) {
        val bundle = Bundle()
        bundle.putString(Constants.MOVIE_KEY,data.movieKey)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class RecentReleaseAdapter(
    fragmentManager: FragmentManager,
    private val strealmList: List<StreamDetails>
) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int = strealmList.size

    override fun getItem(position: Int): Fragment {
        return RecentReleaseFragment.newInstance(strealmList[position])
    }

}

class TabLayoutOnScrollListener(
    private val tabLayout: TabLayout,
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // Calculate the first visible item position in the RecyclerView
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        // Update the selected tab based on the first visible item position
        tabLayout.setScrollPosition(firstVisibleItemPosition, 0f, true)
    }
}
