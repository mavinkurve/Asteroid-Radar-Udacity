package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import timber.log.Timber

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        //The ViewModelProviders (plural) is deprecated.
        //ViewModelProviders.of(this, DevByteViewModel.Factory(activity.application)).get(DevByteViewModel::class.java)
        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Timber.i("Creating view for main fragment")
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        // set progress spinner to true while results load
        binding.loadingSpinner.visibility = View.VISIBLE

        val adapter = MainAdapter(AsteroidListener {
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })
        binding.asteroidRecycler.adapter = adapter

        // Load asteroid picture of the day
        viewModel.picture.observe(viewLifecycleOwner) {
            it?.let {
                Picasso.with(requireContext())
                    .load(it.url)
                    .fit().centerCrop()
                    .placeholder(R.drawable.placeholder_picture_of_day)
                    .error(R.drawable.ic_broken_image)
                    .into(binding.activityMainImageOfTheDay)
                binding.activityMainImageOfTheDay.contentDescription =
                    getString(R.string.nasa_picture_of_day_content_description_format, it.title)
            }
        }



        // Load list of asteroids
        viewModel.asteroids.observe(viewLifecycleOwner) {
            Timber.d("List has ${it.size} asteroids")
            if (it.isNotEmpty())
                binding.loadingSpinner.visibility = View.GONE
            adapter.submitList(it)
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.setFilter(when (item.itemId) {
            R.id.show_today_menu -> Filter.TODAY
            R.id.show_week_menu -> Filter.WEEK
            else -> Filter.ALL
        })
        return true
    }
}
