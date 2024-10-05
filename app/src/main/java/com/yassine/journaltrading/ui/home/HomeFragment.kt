package com.yassine.journaltrading.ui.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.yassine.journaltrading.R
import com.yassine.journaltrading.data.TradeHistory
import com.yassine.journaltrading.databinding.DialogTradeHistoryBinding
import com.yassine.journaltrading.databinding.FragmentHomeBinding

import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var tradeViewModel: TradeViewModel
    private  val tradeAdapter = TradeAdapter()

    private val binding get() = _binding!!

    private lateinit  var dateTextView: EditText

    private lateinit var selectedStatusOption:String

    private fun deleteTrade(tradeHistory: TradeHistory) {
        tradeViewModel.deleteTrade(tradeHistory)
        // tradeAdapter.notifyItemRemoved(tradeHistory)
    }
    // Implement edit function
    private fun editTrade(tradeHistory: TradeHistory) {
        // Use the same dialog for adding data
         showAddOrEditDialog(tradeHistory)
    }





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        tradeViewModel = ViewModelProvider(this)[TradeViewModel::class.java]

        binding.viewModel = tradeViewModel

        binding.lifecycleOwner = this
        binding.recyclerViewTrades.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tradeAdapter
        }

        val swipeHandler = SwipeToDeleteCallback(requireContext(), tradeAdapter, ::editTrade, ::deleteTrade)
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTrades)

        // Find TextView and set up an observer for totalProfitOrLoss
        tradeViewModel.totalProfitOrLoss.observe(viewLifecycleOwner, Observer { total ->
            binding.tvTotalProfitOrLoss.text = total
        })

        // Setup FAB to add a new trade
        binding.fabAddTrade.setOnClickListener {
            showAddOrEditDialog(null)
        }

        return binding.root
    }

    private fun showAddOrEditDialog(tradeHistory: TradeHistory?) {
        // Inflate the dialog layout
        val dialogBinding = DialogTradeHistoryBinding.inflate(LayoutInflater.from(requireContext()))

        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)

        val dateButton = dialogBinding.dateImageButton
        dateTextView = dialogBinding.dateTextView

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Load the string array from resources
        val options = resources.getStringArray(R.array.status_options)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = dialogBinding.spinnerStatus
        spinner.adapter = adapter

        // Set an item selected listener to handle selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedStatusOption = options[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        val buttonSave = dialogBinding.btnSave
        val buttonCancel = dialogBinding.btnCancel
        val etMargin = dialogBinding.etMargin
        val etPL = dialogBinding.etProfitOrLoss
        val etTradeNumber = dialogBinding.etTradeNumber

        // If tradeHistory is not null, it means we are editing an existing trade
        if (tradeHistory != null) {
            // Populate the dialog with existing trade data
            dateTextView.setText(tradeHistory.date)
            etTradeNumber.setText(tradeHistory.tradeNumber.toString())
            etMargin.setText(tradeHistory.margin.toString())
            etPL.setText(tradeHistory.profitOrLoss.toString())
            spinner.setSelection(options.indexOf(tradeHistory.status))
            selectedStatusOption = tradeHistory.status
        }

        val alertDialog = dialogBuilder.create()

        buttonSave.setOnClickListener {
            val database = FirebaseDatabase.getInstance().getReference("TradeHistory")
            val historyId = tradeHistory?.id ?: database.push().key ?: "33"

            val history = TradeHistory(
                id = historyId,
                status = selectedStatusOption,
                win = tradeHistory?.win ?: 1,
                loss = tradeHistory?.loss ?: 1,
                date = dateTextView.text.toString(),
                tradeNumber = etTradeNumber.text.toString().toIntOrNull() ?: 1,
                margin = etMargin.text.toString().toDoubleOrNull() ?: 0.0,
                profitOrLoss = etPL.text.toString().toDoubleOrNull() ?: 0.0
            )

            database.child(historyId).setValue(history).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showMessage("Data saved successfully!")
                    alertDialog.dismiss()
                } else {
                    task.exception?.let { showMessage("Failed to save data: ${it.message}") }
                }
            }
        }
        buttonCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showMessage(trades: List<TradeHistory>) {
        // Format the trades into a string for display
        val tradeMessages = trades.joinToString(separator = "\n") { trade ->
            "ID: ${trade.id}, Status: ${trade.status}, Win: ${trade.win}, Loss: ${trade.loss}, Date: ${trade.date}, TradeNumber: ${trade.tradeNumber}, Margin: ${trade.margin}, Profit/Loss: ${trade.profitOrLoss}"
        }

//        binding.textData.text = tradeMessages
    }

    private fun showAddDialog() {
        // Inflate the dialog layout
        val dialogBinding = DialogTradeHistoryBinding.inflate(LayoutInflater.from(requireContext()))

        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)

        val dateButton = dialogBinding.dateImageButton
        dateTextView = dialogBinding.dateTextView

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Load the string array from resources
        val options = resources.getStringArray(R.array.status_options)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = dialogBinding.spinnerStatus
        // Apply the adapter to the spinner
        spinner.adapter = adapter

        // Set an item selected listener to handle selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                selectedStatusOption = options[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        val buttonSave = dialogBinding.btnSave
        val buttonCancel = dialogBinding.btnCancel

        val etMargin = dialogBinding.etMargin
        val etPL = dialogBinding.etProfitOrLoss
        val etTradeNumber = dialogBinding.etTradeNumber




        val alertDialog = dialogBuilder.create()


        buttonSave.setOnClickListener {

            val database = FirebaseDatabase.getInstance().getReference("TradeHistory")
            val historyId = database.push().key ?: "33"

            //  alertDialog.dismiss()
            val history = TradeHistory(
                id = historyId ,
                status = selectedStatusOption,
                win = 1,
                loss = 1,
                date = dateTextView.text.toString(),
                tradeNumber = etTradeNumber.text.toString().toIntOrNull() ?: 1,
                margin = etMargin.text.toString().toDoubleOrNull() ?: 0.0,
                profitOrLoss = etPL.text.toString().toDoubleOrNull() ?: 0.0
            )

//            showMessage(history.toString())


            database.child(historyId).setValue(history).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    showMessage("data saved successfully!")
                    alertDialog.dismiss()
                }else{
                    task.exception?.let{ showMessage("failed to save data: ${it.message}") }
                }
            }

        }
        buttonCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showDatePickerDialog() {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create the DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Handle the date selection
                val selectedDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                dateTextView.setText(selectedDate) // Assuming you have a TextView to display the date
            },
            year, month, day
        )

        // Show the dialog
        datePickerDialog.show()
    }
    private fun addHistory(history: TradeHistory) {
        val database = FirebaseDatabase.getInstance().getReference("TradeHistory")
        val historyId = database.push().key ?: return
        history.id = historyId
        database.child(historyId).setValue(history)
    }

    private fun showMessage(message:String){
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                // Handle the undo action here
            }
            .show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}