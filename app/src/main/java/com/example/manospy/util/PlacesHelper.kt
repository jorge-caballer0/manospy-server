package com.example.manospy.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Helper para búsqueda de ubicaciones usando Geocoder de Android
 * Places API requerirá configuración adicional de API Key en AndroidManifest.xml
 */
object PlacesHelper {
    
    data class LocationSuggestion(
        val displayName: String,
        val latitude: Double,
        val longitude: Double,
        val fullAddress: String
    )
    
    // Cache para el país del usuario (evita consultas repetidas)
    private var cachedUserCountry: String? = null

    /**
     * Obtiene la ubicación actual del dispositivo
     * Requiere permisos: android.permission.ACCESS_FINE_LOCATION o ACCESS_COARSE_LOCATION
     */
    suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.IO) {
        return@withContext try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                null
            } else {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        continuation.resume(location)
                    }.addOnFailureListener {
                        continuation.resume(null)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene el país del usuario basado en su ubicación actual
     * Cachea el resultado para evitar consultas repetidas
     */
    suspend fun getUserCountry(context: Context): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            // Si ya tenemos el país cacheado, devolverlo
            if (cachedUserCountry != null) {
                return@withContext cachedUserCountry
            }
            
            val location = getCurrentLocation(context) ?: return@withContext null
            val geocoder = Geocoder(context, Locale.getDefault())
            
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            
            if (!addresses.isNullOrEmpty()) {
                val countryCode = addresses[0].countryCode
                cachedUserCountry = countryCode
                countryCode
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Limpia el caché del país del usuario (llamar cuando se actualice la ubicación)
     */
    fun clearCountryCache() {
        cachedUserCountry = null
    }

    /**
     * Busca ubicaciones basadas en un query de texto
     * @param context Contexto de Android
     * @param query Texto de búsqueda
     * @param maxResults Número máximo de resultados
     * @param filterByUserCountry Si true, solo devuelve resultados del país del usuario
     * @return Lista de sugerencias de ubicación
     */
    suspend fun searchLocations(
        context: Context,
        query: String,
        maxResults: Int = 5,
        filterByUserCountry: Boolean = true
    ): List<LocationSuggestion> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Obtener el país del usuario si se solicita filtrado
            val userCountryCode = if (filterByUserCountry) {
                getUserCountry(context)
            } else null
            
            val geocoder = Geocoder(context, Locale.getDefault())
            // Pedir más resultados para permitir filtrado por subcadenas
            val addresses = geocoder.getFromLocationName(query, maxOf(10, maxResults * 10))

            val all = addresses?.mapNotNull { address ->
                if (address.hasLatitude() && address.hasLongitude()) {
                    // Filtrar por país si es necesario
                    if (userCountryCode != null && address.countryCode != userCountryCode) {
                        return@mapNotNull null
                    }

                    val display = listOfNotNull(
                        address.thoroughfare,
                        address.subLocality,
                        address.locality,
                        address.adminArea
                    ).joinToString(", ")

                    LocationSuggestion(
                        displayName = if (display.isNotBlank()) display else address.featureName ?: getFullAddress(address),
                        latitude = address.latitude,
                        longitude = address.longitude,
                        fullAddress = getFullAddress(address)
                    )
                } else null
            } ?: emptyList()

            // Filtrar por subcadena en displayName o fullAddress para que sugerencias aparezcan con términos parciales
            val q = query.lowercase(Locale.getDefault()).trim()
            val filtered = if (q.isNotEmpty()) {
                all.filter { s ->
                    s.displayName.lowercase(Locale.getDefault()).contains(q) ||
                    s.fullAddress.lowercase(Locale.getDefault()).contains(q)
                }
            } else all

            filtered.take(maxResults)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Obtiene una dirección reversa basada en coordenadas
     * @param context Contexto de Android
     * @param latitude Latitud
     * @param longitude Longitud
     * @return Dirección formateada o null
     */
    suspend fun getReverseGeocode(
        context: Context,
        latitude: Double,
        longitude: Double
    ): LocationSuggestion? = withContext(Dispatchers.IO) {
        return@withContext try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                LocationSuggestion(
                    displayName = listOfNotNull(
                        address.thoroughfare,
                        address.subThoroughfare
                    ).joinToString(" "),
                    latitude = address.latitude,
                    longitude = address.longitude,
                    fullAddress = getFullAddress(address)
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene la dirección completa formateada desde un objeto Address
     */
    private fun getFullAddress(address: Address): String {
        val parts = mutableListOf<String>()
        
        address.thoroughfare?.let { parts.add(it) }
        address.subThoroughfare?.let { parts.add(it) }
        address.locality?.let { parts.add(it) }
        address.adminArea?.let { parts.add(it) }
        address.postalCode?.let { parts.add(it) }
        address.countryName?.let { parts.add(it) }
        
        return parts.joinToString(", ")
    }
}
