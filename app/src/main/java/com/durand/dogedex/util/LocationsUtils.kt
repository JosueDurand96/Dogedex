package com.durand.dogedex.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@SuppressLint("MissingPermission")
class LocationsUtils {

    companion object {
        const val REQUEST_KEY_ADD_COORDINATES = "REQUEST_KEY_ADD_COORDINATES"

        fun isCurrentPositionAllowed(
            context: Context,
            launchPermissions: () -> Unit
        ): Boolean {
            val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasAccessCoarseLocationPermission && hasAccessFineLocationPermission) {
                return true
            } else {
                launchPermissions()
            }
            return false
        }

        fun isGPSActive(activity: Activity): Boolean {
            val manager: LocationManager =
                activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun activeGPS(
            locationRequest: LocationRequest,
            activity: Activity,
            launchIntentSenderRequest: (IntentSenderRequest) -> Unit
        ) {
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val result = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build())

            result.addOnSuccessListener {}
            result.addOnFailureListener {
                if (it is ResolvableApiException) {
                    try {
                        val intentSenderRequest: IntentSenderRequest = IntentSenderRequest.Builder(it.resolution).build()
                        launchIntentSenderRequest(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.printStackTrace()
                    }
                }
            }
        }

        fun FusedLocationProviderClient.locationFlow(
            locationRequest: LocationRequest
        ) = callbackFlow {
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    try {
                        trySend(result.locations.lastOrNull()).isSuccess
                    } catch (_: Exception) {
                    }
                }
            }
            requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
                .addOnFailureListener { e ->
                    close(e)
                }

            awaitClose {
                removeLocationUpdates(callback)
            }
        }

    }

}