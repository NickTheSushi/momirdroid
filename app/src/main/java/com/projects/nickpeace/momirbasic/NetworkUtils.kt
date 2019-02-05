package com.projects.nickpeace.momirbasic

import android.net.Uri
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class NetworkUtils{

    companion object {

        private val LOG_TAG = "NetworkUtils"
        private val RANDOM_CARD_BASE_URL = "https://api.scryfall.com/cards/random?"
        private val PARAM_CARD_TYPE = "type"
        private val PARAM_CMC = "cmc"

        fun fetchCard(cmcToGet: String): String{

            var urlConnection:HttpURLConnection? = null
            var reader:BufferedReader? = null
            var cardJSONString:String? = null

            try{
                // Here we build our URI dynamically. In the future we will conditionally append other
                // parameters, such as 'exculde Un-Cards' or 'don't allow Phage/lose game cards'
//                val builtURI = Uri.parse(RANDOM_CARD_BASE_URL).buildUpon()
//                    .appendQueryParameter(PARAM_CMC,cmcToGet)
//                    .appendQueryParameter(PARAM_CARD_TYPE,"creature")
//                    .build()

                // Temporary fix -- I need to research how to dynamically percentage-encode the
                // uri. For right now, we're only searching for creatures based on CMC
                val builtURI = Uri.parse("https://api.scryfall.com/cards/random?q=type%3Acreature+cmc%3D$cmcToGet")

                // Create the request
                val request = URL(builtURI.toString())
//                Log.i("OUR URI: ",builtURI.toString())

                urlConnection = request.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                val inputStream = urlConnection.inputStream
                reader = BufferedReader(InputStreamReader(inputStream))
                val builder = StringBuilder()

                // Build our CardJSONString
                var line: String?
                var hasNextLine = true
                while (hasNextLine) {

                    line = reader.readLine()

                    if (line == null) {
                        hasNextLine = false
                        break
                    }

                    builder.append(line).append("\n")

                    if (builder.isEmpty())
                        return "null"

                    cardJSONString = builder.toString()

                }
            } catch (e:IOException){
                e.printStackTrace()
            } finally {

                if (urlConnection != null){
                    urlConnection.disconnect()
                }

                if(reader != null){
                    try {
                        reader.close()
                    } catch (e:IOException){
                        e.printStackTrace()
                    }
                }
            }

            return if(cardJSONString != null) cardJSONString else "null";
        }
    }
}