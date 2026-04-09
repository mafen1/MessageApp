package com.example.messageapp.ui.privateScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messageapp.R

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

class OnboardingAdapter(
    private val pages: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingAdapter.PageViewHolder>() {

    inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.onboardingImage)
        private val title: TextView = view.findViewById(R.id.onboardingTitle)
        private val desc: TextView = view.findViewById(R.id.onboardingDesc)

        fun bind(page: OnboardingPage) {
            image.setImageResource(page.imageRes)
            title.text = page.title
            desc.text = page.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.onboarding_page, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size
}
