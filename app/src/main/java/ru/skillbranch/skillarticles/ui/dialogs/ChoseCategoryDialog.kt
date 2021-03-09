package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel


class ChoseCategoryDialog : DialogFragment() {

    private val viewModel : ArticlesViewModel by activityViewModels()
    private val selectedCategories = mutableListOf<String>()
    private val args: ChoseCategoryDialogArgs by navArgs()

    private val categoryAdapter = CategoryAdapter { categoryId: String, isChecked: Boolean ->
        if(isChecked) selectedCategories.add(categoryId)
        else selectedCategories.remove(categoryId)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        selectedCategories.clear()
        selectedCategories.addAll(
            savedInstanceState?.getStringArray("checked") ?: args.selectedCategories
        )

        val categoryItems = args.categories.map { it.toItem(selectedCategories.contains(it.categoryId)) }

        categoryAdapter.submitList(categoryItems)

        val listView = layoutInflater.inflate(R.layout.fragment_chose_category_dialog, null) as RecyclerView

        with(listView){
            layoutManager  = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Choose category")
            .setPositiveButton("Apply"){ _,_ ->
                viewModel.applyCategories(selectedCategories.toList())
            }
            .setNegativeButton("Reset"){_,_ ->
                viewModel.applyCategories(emptyList())
            }.setView(listView)
            .create()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray("checked", selectedCategories.toTypedArray())
        super.onSaveInstanceState(outState)
    }
}