package com.example.messageapp.ui.privateScreen

import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentPrivatyBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivateFragment : BaseFragment<FragmentPrivatyBinding>(FragmentPrivatyBinding::inflate) {

    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun initView() {
        val pages = listOf(
            OnboardingPage(
                imageRes = R.drawable.chat,
                title = getString(R.string.onboarding_title_1),
                description = getString(R.string.onboarding_desc_1)
            ),
            OnboardingPage(
                imageRes = R.drawable.news,
                title = getString(R.string.onboarding_title_2),
                description = getString(R.string.onboarding_desc_2)
            ),
            OnboardingPage(
                imageRes = R.drawable.search,
                title = getString(R.string.onboarding_title_3),
                description = getString(R.string.onboarding_desc_3)
            )
        )

        onboardingAdapter = OnboardingAdapter(pages)
        binding.onboardingPager.adapter = onboardingAdapter

        setupDots(pages.size)
        binding.onboardingPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
        })

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_privateFragment_to_registerFragment)
        }

        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_privateFragment_to_registerFragment)
        }
    }

    private fun setupDots(count: Int) {
        binding.dotsIndicator.removeAllViews()
        for (i in 0 until count) {
            val dot = ImageView(requireContext())
            val size = resources.getDimensionPixelSize(R.dimen.dot_size)
            val params = LinearLayout.LayoutParams(size, size)
            params.marginEnd = resources.getDimensionPixelSize(R.dimen.dot_margin)
            dot.layoutParams = params
            dot.setImageResource(
                if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive
            )
            binding.dotsIndicator.addView(dot)
        }
    }

    private fun updateDots(selectedIndex: Int) {
        for (i in 0 until binding.dotsIndicator.childCount) {
            val dot = binding.dotsIndicator.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == selectedIndex) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }
}
