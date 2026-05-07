package com.diogo.imageexplorer.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.diogo.imageexplorer.data.local.AppDatabase
import com.diogo.imageexplorer.data.remote.RetrofitClient
import com.diogo.imageexplorer.data.repository.DogRepository
import com.diogo.imageexplorer.databinding.FragmentFavoritesBinding
import com.diogo.imageexplorer.ui.ViewModelFactory
import com.diogo.imageexplorer.ui.adapter.DogAdapter
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var adapter: DogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = DogRepository(RetrofitClient.instance, AppDatabase.getDatabase(requireContext()).favoriteDogDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FavoritesViewModel::class.java]

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = DogAdapter { url ->
            viewModel.toggleFavorite(url)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteDogs.collect { favorites ->
                    if (favorites.isEmpty()) {
                        binding.textEmpty.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.textEmpty.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        adapter.submitList(favorites.map { it.imageUrl })
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
