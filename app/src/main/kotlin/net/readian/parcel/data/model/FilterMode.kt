package net.readian.parcel.data.model

import kotlinx.serialization.Serializable

/**
 * Filter modes for delivery queries as defined by the Parcel API
 */
@Serializable
enum class FilterModeDataModel(val value: String) {
    /**
     * Shows only active/ongoing deliveries
     */
    ACTIVE("active"),
    
    /**
     * Shows recent deliveries (default)
     */
    RECENT("recent");
    
    override fun toString(): String = value
}