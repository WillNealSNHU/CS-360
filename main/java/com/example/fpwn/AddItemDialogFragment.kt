package com.example.fpwn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddItemDialogFragment : DialogFragment() {

    private lateinit var editTextDescription: EditText
    private lateinit var editTextQuantity: EditText
    private lateinit var editTextUnit: EditText
    private lateinit var btnDecrement: Button
    private lateinit var btnIncrement: Button
    private lateinit var btnAddItem: Button

    private var userId: Long = -1L


    private var onItemAddedListener: (() -> Unit)? = null


    fun setOnItemAddedListener(listener: () -> Unit) {
        onItemAddedListener = listener
    }
// I used a lot of information from
// https://stackoverflow.com/questions/53815268/kotlin-create-content-in-oncreateview
//
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_add_item, container, false)


        userId = arguments?.getLong("userId", -1L) ?: -1L


        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextQuantity = view.findViewById(R.id.editTextQuantity)
        editTextUnit = view.findViewById(R.id.editTextUnit)
        btnDecrement = view.findViewById(R.id.btnDecrement)
        btnIncrement = view.findViewById(R.id.btnIncrement)
        btnAddItem = view.findViewById(R.id.btnAddItem)


        btnDecrement.setOnClickListener {
            decrementQuantity()
        }

        btnIncrement.setOnClickListener {
            incrementQuantity()
        }

        btnAddItem.setOnClickListener {
            if (validateFields()) {
                addItemToDatabase()
                dismiss()
            }
        }

        return view
    }
// https://stackoverflow.com/questions/60778558/set-limits-for-increments-and-decrements-in-android-studio-kotlin

    private fun decrementQuantity() {
        val currentQuantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
        if (currentQuantity > 0) {
            editTextQuantity.setText((currentQuantity - 1).toString())
        }
    }

    private fun incrementQuantity() {
        val currentQuantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
        editTextQuantity.setText((currentQuantity + 1).toString())
    }

    private fun validateFields(): Boolean {
        val description = editTextDescription.text.toString()
        val quantity = editTextQuantity.text.toString().toIntOrNull()

        if (description.isEmpty()) {

            editTextDescription.error = "Description cannot be empty"
            return false
        }

        if (quantity == null || quantity <= 0) {

            editTextQuantity.error = "Quantity must be greater than zero"
            return false
        }


        return true
    }

    private fun addItemToDatabase() {
        val description = editTextDescription.text.toString()
        val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
        val unit = editTextUnit.text.toString()


        GlobalScope.launch(Dispatchers.IO) {
            val newItem = InventoryItem(userId = userId, description = description, unit = unit, quantity = quantity)
            AppDatabase.getDatabase(requireContext()).InventoryDao().addItem(newItem)


            onItemAddedListener?.invoke()
        }
    }
}