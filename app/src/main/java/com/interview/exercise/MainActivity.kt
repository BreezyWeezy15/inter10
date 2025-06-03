package com.interview.exercise

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.interview.exercise.databinding.ActivityMainBinding

/**
 * MainActivity displays a list of items using RecyclerView and observes LiveData from MainViewModel.
 *
 * Responsibilities:
 * - Set up the layout using View Binding.
 * - Initialize and observe ViewModel LiveData for item list and selected item.
 * - Display the list using RecyclerView.
 * - Respond to item selection events and update the ViewModel accordingly.
 * - Observe system window insets for edge-to-edge display support.
 */

class MainActivity : AppCompatActivity() {

    // View Binding for accessing views in activity_main.xml
    private lateinit var binding: ActivityMainBinding

    // Adapter for RecyclerView
    private lateinit var adapter: ItemAdapter

    // ViewModel containing business logic and persistent state
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables full edge-to-edge layout (status/navigation bar support)
        enableEdgeToEdge()

        // Inflate layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle system window insets to avoid overlapping UI with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the ViewModel using ViewModelProvider
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Observe LiveData for the list of items
        viewModel.items.observe(this) { itemList ->
            // Build adapter with currently selected ID (if available)
            viewModel.selectedId.value?.let { selectedId ->
                adapter = ItemAdapter(itemList, selectedId) { viewModel.selectItem(it) }
            } ?: run {
                adapter = ItemAdapter(itemList, null) { viewModel.selectItem(it) }
            }

            // Configure RecyclerView layout and adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }

        // Observe LiveData for selected item ID and update the adapter's UI
        viewModel.selectedId.observe(this, Observer { selectedId ->
            adapter.updateSelection(selectedId)
        })
    }
}
