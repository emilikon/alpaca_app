package com.example.emilikon_oblig2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emilikon_oblig2.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private var stemmerTotalt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        startRecyclerView()


        val spinner: Spinner = binding.spinner

        ArrayAdapter.createFromResource(
            this,
            R.array.valgdistrikt,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // layout
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // setter adapter
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //nothing
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val fraBruker = resources.getStringArray(R.array.valgdistrikt)[position]
                Log.i("VALGDISTRIKT: ", fraBruker)
                when (fraBruker.toString()) {
                    "Valgdistrikt 1" -> viewModel.loadVotes("district1.json")
                    "Valgdistrikt 2" -> viewModel.loadVotes("district2.json")
                    "Valgdistrikt 3" -> viewModel.loadVotesXML("district3.xml")
                    else -> {
                        Log.i("ELSE", fraBruker)
                    }
                }
            }
        }
    }


    private fun startRecyclerView() {
        viewModel.getParties().observe(this) { parties ->
            val partiesListFromJson = parties.toMutableList()


            binding.recyclerViewMain.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = PartyAdapter(partiesListFromJson)
            }
        }
    }




    }





