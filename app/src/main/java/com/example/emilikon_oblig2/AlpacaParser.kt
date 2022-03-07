package com.example.emilikon_oblig2

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class AlpacaParser {

    //party: votes and id
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<AlpacaVoteXML> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<AlpacaVoteXML> {
        val partyList = mutableListOf<AlpacaVoteXML>()

        parser.require(XmlPullParser.START_TAG, ns, "districtThree")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "party") {
                partyList.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return partyList
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): AlpacaVoteXML {
        parser.require(XmlPullParser.START_TAG, ns, "party")
        var id: String? = null
        var votes: String? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "id" -> id = readId(parser)
                "votes" -> votes = countVotes(parser)
                //readAttribute(parser, parser.name).toIntIrNull()

                else -> skip(parser)
            }
        }
        return AlpacaVoteXML(id, votes)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun countVotes(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "votes")
        val votes = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "votes")
        return votes
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readId(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "id")
        val id = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "id")
        return id
    }

    //readAttribute: (tag: String)

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }


    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

}

data class AlpacaVoteXML(val id: String?, val votes : String?)

