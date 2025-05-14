package com.mediaplus.app.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mediaplus.app.R
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.MediaType
import com.mediaplus.app.databinding.ActivitySearchBinding
import com.mediaplus.app.ui.home.MediaAdapter
import com.mediaplus.app.ui.player.AudioPlayerActivity
import com.mediaplus.app.ui.player.VideoPlayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Set up search view
        setupSearchView()
        
        // Observe search results
        observeResults()
    }
    
    private fun setupRecyclerView() {
        adapter = MediaAdapter(
            onItemClick = { mediaItem ->
                // Handle click based on media type
                when (mediaItem.mediaType) {
                    MediaType.AUDIO -> {
                        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                            putExtra("mediaItemId", mediaItem.id)
                        }
                        startActivity(intent)
                    }
                    MediaType.VIDEO -> {
                        val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                            putExtra("mediaItemId", mediaItem.id)
                        }
                        startActivity(intent)
                    }
                    MediaType.IMAGE -> {
                        // TODO: Handle image type if needed
                    }
                }
            },
            onRemoveClick = { mediaItem ->
                viewModel.deleteMediaItem(mediaItem)
            }
        )
        binding.recyclerSearchResults.layoutManager = LinearLayoutManager(this)
        binding.recyclerSearchResults.adapter = adapter
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    performSearch(query)
                }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank() && newText.length >= 3) {
                    performSearch(newText)
                }
                return true
            }
        })
        
        // Set focus and show keyboard
        binding.searchView.requestFocus()
    }
    
    private fun performSearch(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.txtNoResults.visibility = View.GONE
        
        viewModel.searchMedia(query)
    }
    
    private fun observeResults() {
        viewModel.searchResults.observe(this) { results -> 
            binding.progressBar.visibility = View.GONE
            
            if (results.isEmpty()) {
                binding.txtNoResults.visibility = View.VISIBLE
                binding.recyclerSearchResults.visibility = View.GONE
            } else {
                binding.txtNoResults.visibility = View.GONE
                binding.recyclerSearchResults.visibility = View.VISIBLE
                adapter.submitList(results)
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
