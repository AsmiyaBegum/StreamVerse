package com.ab.streamverse.othermenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ab.streamverse.MainActivity
import com.ab.streamverse.R
import com.ab.streamverse.databinding.OtherMenuLayoutBinding
import com.ab.streamverse.util.Constants

class OtherMenuFragment : Fragment(){

    lateinit var binding : OtherMenuLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        handleBackPress()
        binding = OtherMenuLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleMenusText()
    }

    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_OtherMenuFragment_to_StreamVerseHomeFragment)
            }
        }

        // Add the callback to the OnBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun handleMenusText(){
        val menu = requireArguments().getString(Constants.MENU_NAME)?:""
        val menuName = when(menu.toInt()) {
            R.id.search -> "Search"
            R.id.comingSoon -> "Coming Soon"
            R.id.downloads -> "Downloads"
            R.id.more -> "More"
            else -> ""
        }
        binding.menuName.text = getString(R.string.implement_screen_functionality,menuName)
    }
}