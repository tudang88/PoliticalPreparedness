@file:Suppress("UNCHECKED_CAST")

package com.example.android.politicalpreparedness.representative.adapter

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.representative.model.Representative

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val uri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(uri)
            .apply(
                RequestOptions().placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
            ).into(view)
    }
    // show default icon in case imgUrl empty or null
    if (imgUrl == "" || imgUrl == null) {
        view.setImageResource(R.drawable.ic_profile)
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

@BindingAdapter("stateList")
fun setAdapterToSpinner(spinner: Spinner, stateList: List<String>) {
    val adapter = ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, stateList)
    spinner.adapter = adapter
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}

@BindingAdapter("representativesList")
fun setRepresentativeList(recyclerView: RecyclerView, representatives: List<Representative>?) {
    representatives?.let {
        (recyclerView.adapter as RepresentativeListAdapter).submitList(
            representatives
        )
    }
}
