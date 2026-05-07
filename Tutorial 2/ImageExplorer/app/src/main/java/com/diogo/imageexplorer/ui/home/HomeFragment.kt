package com.diogo.imageexplorer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.diogo.imageexplorer.data.local.AppDatabase
import com.diogo.imageexplorer.data.remote.RetrofitClient
import com.diogo.imageexplorer.data.repository.DogRepository
import com.diogo.imageexplorer.databinding.FragmentHomeBinding
import com.diogo.imageexplorer.ui.ViewModelFactory
import com.diogo.imageexplorer.ui.adapter.DogAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit viewModel: HomeViewModel
    private lateinit adapter: DogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = DogRepository(RetrofitClient.instance, AppDatabase.getDatabase(requireContext()).favoriteDogDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        setupRecyclerView()
        setupObservers()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchRandomDogs()
        }
    }

    private fun setupRecyclerView() {
        adapter = DogAdapter { url ->
            viewModel.toggleFavorite(url)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.textError.visibility = View.GONE
                }
                is HomeUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textError.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    adapter.submitList(state.images)
                }
                is HomeUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textError.visibility = View.VISIBLE
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
