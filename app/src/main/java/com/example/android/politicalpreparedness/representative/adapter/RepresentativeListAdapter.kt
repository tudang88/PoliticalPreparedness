package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.RepresentativeListItemBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeListAdapter :
    ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

/**
 * ViewHolder class of Representative RecyclerView
 */
class RepresentativeViewHolder(val binding: RepresentativeListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Representative) {
        binding.representative = item

        // binding social channels
        item.official.channels?.let {
            showSocialLinks(it)
        }
        // binding www url
        item.official.urls?.let {
            showWWWLinks(it)
        }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RepresentativeListItemBinding.inflate(layoutInflater, parent, false)
            return RepresentativeViewHolder(binding)
        }
    }

    /**
     * binding social channel with icon
     *
     */
    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) {
            enableLink(binding.fbButton, facebookUrl)
        } else {
            disableLink(binding.fbButton)
        }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) {
            enableLink(binding.twButton, twitterUrl)
        } else {
            disableLink(binding.twButton)
        }
    }

    /**
     * binding the representative
     * website url with www_icon
     */
    private fun showWWWLinks(urls: List<String>) {
        if (urls.first().isNotBlank()) {
            enableLink(binding.ieButton, urls.first())
        } else {
            disableLink(binding.ieButton)
        }

    }

    /**
     * extract facebook link from representative info
     */
    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
            .map { channel -> "https://www.facebook.com/${channel.id}" }
            .firstOrNull()
    }

    /**
     * extract twitter link from representative info
     */
    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
            .map { channel -> "https://www.twitter.com/${channel.id}" }
            .firstOrNull()
    }

    /**
     * enable link and handling click event to open
     * web browser when user clicking on
     */
    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    /**
     * disable link when the link is not available
     */
    private fun disableLink(view: ImageView) {
        view.isEnabled = false
        view.alpha = 0.3f
    }

    /**
     * open web browser by intent
     */
    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }

}

/**
 * Dill util for Representative list adapter
 */
class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
    override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem.official == newItem.official
    }

    override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem == newItem
    }

}
