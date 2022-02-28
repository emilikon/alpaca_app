package com.example.emilikon_oblig2

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DataSource {

    public suspend fun fetchData() : List<AlpacaParty>? {

        val path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v22/obligatoriske-oppgaver/alpacaparties.json"
        val gson = Gson()

            try {
                val response = gson.fromJson(Fuel.get(path).awaitString(), Base :: class.java)
                Log.i("DS: RESPONSE: ", response.parties[0].toString())
                return response.parties

            }catch (e: Exception){
                println(e.message)
                return null
        }
    }

    public suspend fun fetchVotes(token: String) : ArrayList<AlpacaVote>? {
        val basePath = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v22/obligatoriske-oppgaver/"
        val gson = Gson()
        var response = ""
        val voteType = object: TypeToken<ArrayList<AlpacaVote>>() {}.type

        try {
            response = (Fuel.get(basePath+token).awaitString())
            Log.i("DS: RESPONSE VOTES (EXAMPLE 0):", response[0].toString())

        }catch (e: Exception){
            println(e.message)
        }

        val alpacaVotes: ArrayList<AlpacaVote> = gson.fromJson(response, voteType)
        Log.i("DS: ALPACA VOTE EXAMPLE: ", alpacaVotes.get(1).toString())

        return alpacaVotes
    }
}