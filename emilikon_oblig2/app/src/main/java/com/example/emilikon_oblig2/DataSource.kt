package com.example.emilikon_oblig2

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DataSource {

    public suspend fun fetchData() : List<AlpacaParty>? {

        val path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v22/obligatoriske-oppgaver/alpacaparties.json"
        val gson = Gson()

            try {
                val response = gson.fromJson(Fuel.get(path).awaitString(), Base :: class.java)
                return response.parties
            }catch (e: Exception){
                println(e.message)
                return null
        }
    }


}