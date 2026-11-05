package com.jegly.offlineLLM.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

object OrganicMapsHelper {
    
    private const val ORGANIC_MAPS_PACKAGE = "app.organicmaps"
    
    fun isInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(ORGANIC_MAPS_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    fun openSearch(context: Context, query: String): Boolean {
        val encodedQuery = Uri.encode(query)
        val uri = Uri.parse("geo:0,0?q=$encodedQuery")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(ORGANIC_MAPS_PACKAGE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            true
        } else {
            false
        }
    }

    fun openCurrentLocation(context: Context): Boolean {
        // geo:0,0?q=0,0 triggers the map app to center on current location
        val uri = Uri.parse("geo:0,0?q=0,0")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(ORGANIC_MAPS_PACKAGE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        return if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            true
        } else {
            false
        }
    }
}
