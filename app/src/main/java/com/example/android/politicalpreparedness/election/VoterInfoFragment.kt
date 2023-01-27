package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private val _viewModel: VoterInfoViewModel by viewModel()
    private val args: VoterInfoFragmentArgs by navArgs()
    private var snackBar: Snackbar? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
        _viewModel.start(args.argElectionId, args.argDivision)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        /**
         * show error toast when input argument not qualify
         */
        _viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (error) {
                errorResponse(getString(R.string.not_found_voter))
                _viewModel.errorHandlingFinished()
            }
        })
        /**
         * open link when user click
         */
        _viewModel.openLink.observe(viewLifecycleOwner, Observer {
            it?.let {
                openUrl(it)
                _viewModel.openLinkDone()
            }
        })

        return binding.root
    }

    /**
     * open web view when user click on election url
     */
    private fun openUrl(url: String) {
        if (url != "") {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    /**
     * dismiss Snackbar fragment destroy
     */
    override fun onDestroy() {
        super.onDestroy()
        snackBar?.dismiss()
    }

    /**
     * Show snackBar Message to
     * inform error
     */
    private fun errorResponse(message: String) {
        snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
        snackBar?.show()
        binding.followUnfollowButton.isEnabled = false
        binding.followUnfollowButton.alpha = 0.3f
    }
}