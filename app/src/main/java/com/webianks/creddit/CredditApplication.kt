package com.webianks.creddit

import android.app.Application
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient

/**
 * Created by R Ankit on 23-09-2017.
 */

class CredditApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        //Initializing main BMS SDK
        BMSClient.getInstance().initialize(this,BMSClient.REGION_UK)
    }
}