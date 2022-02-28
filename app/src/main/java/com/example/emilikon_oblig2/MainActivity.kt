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

        var listeParties: MutableList<AlpacaParty> = mutableListOf<AlpacaParty>()
        var listeVotes: MutableList<AlpacaVote> = mutableListOf<AlpacaVote>()

        startRecyclerView(listeParties)


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
                    "Valgdistrikt 1" -> getVotesFromJson(listeParties, "district1.json")
                    "Valgdistrikt 2" -> getVotesFromJson(listeParties, "district2.json")
                    "Valgdistrikt 3" -> parseXML(listeParties)
                    else -> {
                        Log.i("ELSE", fraBruker)
                    }
                }
            }
        }
    }


    private fun startRecyclerView(alpacaParties: MutableList<AlpacaParty>) {
        viewModel.getParties().observe(this) { parties ->
            val partiesListFromJson = parties.toMutableList()
            for (party in partiesListFromJson) {
                alpacaParties.add(party)
            }

            binding.recyclerViewMain.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = PartyAdapter(partiesListFromJson)
            }
        }
    }

    private fun getVotesFromJson(alpacaParties: MutableList<AlpacaParty>, token: String) {
        viewModel.getVotes(token).observe(this) { votes ->

            val listOfVotes: MutableList<AlpacaVote> = votes.toMutableList()

            Log.i("MA: EXAMPLE ELEMENT IN LISTOFVOTES", listOfVotes[0].toString())

            for (p in alpacaParties) p.votes = 0
            stemmerTotalt = listOfVotes.size
            Log.i("MA: STEMMER TOTALT ", stemmerTotalt.toString())


            //vil helst gjøre dette i DataSource, men slet sånn med kallene dit så måtte det legge det her for å kunne teste
            for (vote in listOfVotes) {
                for (party in alpacaParties) {
                    if (vote.id.equals(party.id)) {
                        party.votes = party.votes?.plus(1)
                        val oppslutning = (party.votes?.div(stemmerTotalt))?.times(100)
                        party.oppslutning = "Votes: " + party.votes + " - $oppslutning%"
                    }

                    }
                }

            binding.recyclerViewMain.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = PartyAdapter(alpacaParties)
            }
            }
        }



    fun parseXML(listeParties : MutableList<AlpacaParty>) {
        val baseUrl =
            "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v22/obligatoriske-oppgaver/district3.xml"

        fun getData(): String {
            val requestUrl = "$baseUrl"
            return khttp.get(requestUrl).text
        }

        CoroutineScope(Dispatchers.IO).launch {
            val response = getData()
            Log.d(response, response)

            val inputStream: InputStream = response.byteInputStream()
            val listOfVotes = AlpacaParser().parse(inputStream)

            for (vote in listOfVotes) {
                for (party in listeParties)
                    if (party.id.equals(vote.id)) vote.votes?.let { party.votes?.plus(it.toInt()) }
            }
        }
    }
    }


