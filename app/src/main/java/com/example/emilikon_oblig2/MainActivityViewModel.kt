package com.example.emilikon_oblig2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    private val dataSource = DataSource()

    private val parties: MutableLiveData<List<AlpacaParty>> by lazy {
        MutableLiveData<List<AlpacaParty>>().also {
            loadParties()
        }
    }

    fun getParties(): LiveData<List<AlpacaParty>> {
        return parties
    }

    fun loadParties() {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchData().also {
                //returnerer verdi
                parties.postValue(it)
            }
        }
    }

    private var token: String = ""
    private val votes: MutableLiveData<ArrayList<AlpacaVote>> by lazy {
        MutableLiveData<ArrayList<AlpacaVote>>().also {
            loadVotes(token)
        }
    }

    fun getVotes(tokenIn: String): LiveData<ArrayList<AlpacaVote>> {
        token = tokenIn
        Log.i("VM: TOKEN", token)
        return votes
    }

    fun loadVotes(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchVotes(token).also {
                votes.postValue(it)
            }
        }

    }

}
