package com.example.fpwn


import android.content.DialogInterface
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), InventoryAdapter.OnItemDeleteListener {

    private lateinit var btnAddInventory: Button
    private lateinit var btnDeleteAll: Button
    private lateinit var btnSms: Button
    private lateinit var rvInventory: RecyclerView
    private lateinit var llInventoryInfo: LinearLayout
    private lateinit var tvTotalItems: TextView
    private lateinit var inventoryAdapter: InventoryAdapter

    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.inventory_home)

        btnAddInventory = findViewById(R.id.btnAddInventory)
        btnDeleteAll = findViewById(R.id.btnDeleteAll)
        btnSms = findViewById(R.id.btnSms)
        rvInventory = findViewById(R.id.rvInventory)
        llInventoryInfo = findViewById(R.id.llInventoryInfo)
        tvTotalItems = findViewById(R.id.tvTotalItems)

        val userId = intent.getLongExtra("userId", -1L)


        inventoryAdapter = InventoryAdapter(emptyList(), this)
        rvInventory.layoutManager = LinearLayoutManager(this)
        rvInventory.adapter = inventoryAdapter


        lifecycleScope.launch(Dispatchers.IO) {
            val items = AppDatabase.getDatabase(applicationContext).InventoryDao().getAllItems(userId)
            launch(Dispatchers.Main) {
                if (items.isEmpty()) {

                    Toast.makeText(this@HomeActivity, "No items in inventory", Toast.LENGTH_SHORT).show()
                    llInventoryInfo.visibility = View.GONE
                } else {

                    inventoryAdapter.setItems(items)
                    tvTotalItems.text = "Total Items: ${items.size}"

                }
            }
        }

        btnAddInventory.setOnClickListener {
            val addItemDialog = AddItemDialogFragment()


            val bundle = Bundle()
            bundle.putLong("userId", userId)
            addItemDialog.arguments = bundle


            addItemDialog.setOnItemAddedListener {
                refreshItemList(userId)
            }


            addItemDialog.show(supportFragmentManager, "AddItemDialog$userId")
        }

        btnDeleteAll.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getDatabase(applicationContext).InventoryDao().deleteAllItems(userId)
                launch(Dispatchers.Main) {

                    inventoryAdapter.setItems(emptyList())
                    tvTotalItems.text = "Total Items: 0"
                    Toast.makeText(this@HomeActivity, "All items deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnSms.setOnClickListener {
            showPhoneNumberDialog()
        }
    }

    private fun showPhoneNumberDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Phone Number")

        val input = androidx.appcompat.widget.AppCompatEditText(this)
        input.hint = "Phone Number"
        input.inputType = android.text.InputType.TYPE_CLASS_PHONE
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            phoneNumber = input.text.toString()
            sendSms()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }

    private fun sendSms() {
        val userId = intent.getLongExtra("userId", -1L)

        lifecycleScope.launch(Dispatchers.IO) {
            val items = AppDatabase.getDatabase(applicationContext).InventoryDao().getAllItems(userId)

            launch(Dispatchers.Main) {
                if (items.isNotEmpty() && phoneNumber != null) {
                    val message = generateMessage(items)

                    try {
                        val smsManager = SmsManager.getDefault()
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null)

                        Toast.makeText(
                            this@HomeActivity,
                            "SMS Sent to $phoneNumber:\n$message",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@HomeActivity,
                            "Failed to send SMS. Check permissions and try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "No items to send or invalid phone number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun generateMessage(items: List<InventoryItem>): String {
        val stringBuilder = StringBuilder()
        for (item in items) {
            stringBuilder.append("${item.description}: ${item.quantity} ${item.unit}\n")
        }
        return stringBuilder.toString()
    }

    private fun refreshItemList(userId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val updatedItems = AppDatabase.getDatabase(applicationContext).InventoryDao().getAllItems(userId)
            launch(Dispatchers.Main) {
                if (updatedItems.isEmpty()) {

                    Toast.makeText(this@HomeActivity, "No items in inventory", Toast.LENGTH_SHORT).show()
                    llInventoryInfo.visibility = View.GONE
                } else {

                    inventoryAdapter.setItems(updatedItems)
                    tvTotalItems.text = "Total Items: ${updatedItems.size}"

                }
            }
        }
    }


    override fun onItemDelete(item: InventoryItem) {

        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(applicationContext).InventoryDao().deleteItem(item)
            launch(Dispatchers.Main) {

                val updatedList = inventoryAdapter.getItems().toMutableList()
                updatedList.remove(item)
                inventoryAdapter.setItems(updatedList)
                tvTotalItems.text = "Total Items: ${updatedList.size}"
                Toast.makeText(this@HomeActivity, "Item deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}