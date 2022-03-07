package com.example.emilikon_oblig2

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.InputStream


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

    public suspend fun fetchVotes(token: String) : List<AlpacaParty> {

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


        return tellStemmer(alpacaVotes, fetchData())
    }

    fun tellStemmer(listeStemmer : ArrayList<AlpacaVote> , listePartier : List<AlpacaParty>?): List<AlpacaParty> {
        val stemmerTotalt = listeStemmer.size
        Log.i("DS: LISTE PARTIER FØR:", listePartier.toString())

        if (listePartier != null) {
            for (party in listePartier) {

                var totalVotes = 0
                for (vote in listeStemmer) {
                    if (vote.id == party.id) {
                        totalVotes++
                    }
                }
                party.votes = totalVotes
                party.oppslutning = (totalVotes/stemmerTotalt*100).toString()
            }
        }
        Log.i("DS: LISTE PARTIER", listePartier.toString())

        return listePartier!!

    }



    //tar inn hvilken liste? Hvor skal denne ligge?
    public suspend fun fetchVotesXML(tokenIn: String): List<AlpacaParty>? {
        val baseUrl =
            "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v22/obligatoriske-oppgaver/district3.xml"
        val token = tokenIn

        fun getData(): String {
            val requestUrl = "$baseUrl"
        //    val requestUrl = "$baseUrl+$token"
            return khttp.get(requestUrl).text
        }

        val response = getData()
        Log.i("DS RESPONSE XML", response.toString())

        val listePartier : List<AlpacaParty>? = fetchData()
        val parties = CoroutineScope(Dispatchers.Default).async {
            val inputStream: InputStream = response.byteInputStream()
            val list = AlpacaParser().parse(inputStream)
            Log.i("DS: INPUTSTREAM", list[1].toString())
            if (listePartier != null) {
                for (party in listePartier) {
                    for (vote in list) {
                        if (party.id == vote.id) {
                            party.votes = vote.votes?.toInt()
                        }
                    }
                }
            }
        }


        return listePartier
    }


}


