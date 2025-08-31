package net.readian.parcel.feature.packages.model

/**
 * Represents one-time UI feedback events that should be shown to the user
 */
sealed interface UiFeedbackEvent {
  sealed interface Error : UiFeedbackEvent {
    data class RateLimit(val remainingMinutes: Int) : Error
    object RateLimitGeneral : Error
  }
  object NavigateToLogin : UiFeedbackEvent
}
