package com.example.emilikon_oblig2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emilikon_oblig2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        startViewModel()


}

    fun startViewModel() {
        viewModel.getParties().observe(this ){ parties ->
            val konvertertParties : MutableList<AlpacaParty> = parties.toMutableList()

            binding.recyclerViewMain.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = PartyAdapter(konvertertParties)
            }
        }
    }

}