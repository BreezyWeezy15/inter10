package com.interview.exercise

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.interview.exercise.databinding.ActivityMainBinding

/**
 * MainActivity displays a list of items using RecyclerView and observes LiveData from MainViewModel.
 *
 * Responsibilities:
 * - Set up the layout using View Binding.
 * - Initialize and observe ViewModel LiveData for the item list and selected item IDs.
 * - Display the list using RecyclerView.
 * - Respond to item selection events and update the ViewModel accordingly.
 * - Observe system window insets for edge-to-edge display support.
 */

class MainActivity : AppCompatActivity() {

    // View Binding for accessing views defined in activity_main.xml
    private lateinit var binding: ActivityMainBinding

    // RecyclerView Adapter for displaying the list of items
    private lateinit var adapter: ItemAdapter

    // ViewModel responsible for managing item list and selected items
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout rendering
        enableEdgeToEdge()

        // Inflate layout with View Binding and set it as the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for proper padding (e.g., status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewModel (shared across configuration changes)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Observe item list from ViewModel and set up RecyclerView
        viewModel.items.observe(this) { itemList ->
            val selectedIds = viewModel.selectedIds.value ?: emptySet()

            // Initialize adapter with items and selection handler
            adapter = ItemAdapter(itemList, selectedIds) { selectedItem ->
                viewModel.toggleItemSelection(selectedItem)
            }

            // Set up RecyclerView with layout manager and adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }

        // Observe selection changes and update adapter
        viewModel.selectedIds.observe(this) { selectedIds ->
            adapter.updateSelection(selectedIds)
        }
    }
}
