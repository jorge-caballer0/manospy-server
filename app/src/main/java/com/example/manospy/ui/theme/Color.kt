package com.example.manospy.ui.theme

import androidx.compose.ui.graphics.Color

// Corporate Colors
val CorporateBlue = Color(0xFF0D47A1)       // Primary Blue #0D47A1
val CorporateLightBlue = Color(0xFF1976D2) // Secondary Blue #1976D2
val DarkGray = Color(0xFF212121)           // Dark Gray for text

// Material 3 Colors
val Primary = CorporateBlue
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFD1E7FF)
val OnPrimaryContainer = CorporateBlue

val Secondary = CorporateLightBlue
val OnSecondary = Color(0xFFFFFFFF)

val Background = Color(0xFFFFFFFF)
val Surface = Color(0xFFFFFFFF)
val Outline = Color(0xFFE2E8F0)

val Success = Color(0xFF22C55E)
val Error = Color(0xFFEF4444)

val TextPrimary = DarkGray
val TextSecondary = Color(0xFF1E293B)
val Disabled = Color(0xFF94A3B8)

// Lowercase aliases for backward compatibility
@get:JvmName("textPrimaryLegacy")
val textPrimary = TextPrimary
@get:JvmName("textSecondaryLegacy")
val textSecondary = TextSecondary
@get:JvmName("disabledLegacy")
val disabled = Disabled

val primaryBlue = Primary
val cardBg = Surface
val borderGray = Outline
val brandBlue = Primary
val accentCyan = Color(0xFF06B6D4)  // Cyan
val successGreen = Success
val warningOrange = Color(0xFFF97316)  // Orange
val amberAlert = Color(0xFFEAB308)  // Amber
