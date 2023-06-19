package com.ab.streamverse

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ab.streamverse.databinding.StreamVerseHomeFragmentBinding
import com.ab.streamverse.model.VideoStreamCategory
import com.ab.streamverse.model.StreamDetails
import com.ab.streamverse.recentRelease.RecentReleaseFragment
import com.ab.streamverse.streamVerseHome.StreamDetailsViewHolder
import com.ab.streamverse.streamVerseHome.StreamVerseViewModel
import com.ab.streamverse.util.AdapterUtils
import com.ab.streamverse.util.Constants
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class StreamVerseHomeFragment : Fragment(),StreamDetailsViewHolder.StreamDetailsDelegate {

    private var _binding: StreamVerseHomeFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: StreamVerseViewModel
    private var tabPosition: Int = 0
    private var recentReleaseCount : Int = 0
    // Define a global Handler variable
    private val handler = Handler()

    // Define a Runnable that will be executed periodically
    private val runnable = object : Runnable {
        override fun run() {
            binding.tabLayout.getTabAt((tabPosition+1).rem(recentReleaseCount))?.select()
            handler.postDelayed(this, 5000)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        handleBackPress()
        _binding = StreamVerseHomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitProcess(1)
            }
        }

        // Add the callback to the OnBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity)?.showBottomNavigation(true)
        (requireActivity() as MainActivity)?.binding?.bottomNavigationView?.menu?.findItem(R.id.home)?.isChecked = true
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
        binding.streamCategoryList.isNestedScrollingEnabled = false
    }

    private fun setUpTabLayout(streamList: List<StreamDetails>) {
        binding.tabLayout.removeAllTabs()
        for (i in streamList.indices) {
            binding.tabLayout.addTab(binding.tabLayout.newTab())
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: Tab?) {
            }

            override fun onTabSelected(tab: Tab?) {
                tabPosition = tab!!.position
                binding.recentRecyclerView.layoutManager!!.scrollToPosition(tabPosition)
            }

            override fun onTabUnselected(tab: Tab?) {

            }

        })
    }

    private fun setUpReleaseCarousel(streamList: List<StreamDetails>){
        recentReleaseCount = streamList.size
        handler.postDelayed(runnable, 5000)
        val layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
        binding.recentRecyclerView.layoutManager = layoutManager
        binding.recentRecyclerView.adapter = AdapterUtils.setUpRecentTrendsAdapter(streamList,this)

        // Set up the TabLayout
        setUpTabLayout(streamList)

        binding.recentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        tabPosition = firstVisibleItemPosition
                        binding.tabLayout.getTabAt(firstVisibleItemPosition)?.select()
                    }
                }
            }
        })

    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }

    override fun selectedStreamVideo(data: StreamDetails) {
        val bundle = Bundle()
        bundle.putString(Constants.MOVIE_KEY,data.movieKey)
        findNavController().navigate(R.id.action_HomeFrament_to_PreviewFragment,bundle)
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
