package com.example.emilikon_oblig2

data class AlpacaParty (val id: String?, val name: String?, val  leader: String?, val img: String?, val color: String?, var votes: Int?, var oppslutning : String?) {}

data class Base (val parties : List<AlpacaParty>) {}

data class AlpacaVote(val id: String?)


