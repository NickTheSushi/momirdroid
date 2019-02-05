package com.projects.nickpeace.momirbasic

import android.content.Context
import android.support.v4.content.AsyncTaskLoader

class CardLoader(context: Context,cmcToGet:String):AsyncTaskLoader<String>(context){

    private val cmcToGet = cmcToGet

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }

    override fun loadInBackground(): String? {
        return NetworkUtils.fetchCard(cmcToGet)
    }

}