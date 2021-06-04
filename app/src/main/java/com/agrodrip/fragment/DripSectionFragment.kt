package com.agrodrip.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.agrodrip.R
import com.agrodrip.activity.MainActivity
import com.agrodrip.activity.drip.DripCalculateActivity
import com.agrodrip.activity.drip.GovernmentSchemeListActivity
import com.agrodrip.activity.drip.IssueDripListActivity
import com.agrodrip.databinding.FragmentDripSectionBinding
import kotlinx.android.synthetic.main.content_main.*


class DripSectionFragment : Fragment() {

    private lateinit var binding: FragmentDripSectionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_drip_section, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity?)!!.onHidingToolbar(false, resources.getString(R.string.drip_title))
        (requireActivity() as MainActivity?)!!.bottomNavigation.visibility = View.VISIBLE
        initWidget()

    }

    private fun initWidget() {

        binding.cardDripCalculation.setOnClickListener {
            startActivity(Intent(requireActivity(), DripCalculateActivity::class.java))
        }

        binding.cardDripIssues.setOnClickListener {
            startActivity(Intent(requireActivity(), IssueDripListActivity::class.java))
        }

        binding.cardGovernmentScheme.setOnClickListener {
            startActivity(Intent(requireActivity(), GovernmentSchemeListActivity::class.java))
        }
    }


}