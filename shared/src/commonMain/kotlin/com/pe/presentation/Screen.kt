package com.pe.presentation

sealed class Screen {

    object Splash : Screen()

    object Welcome : Screen()

    object Home : Screen()

    object EquipmentDetail : Screen()

    object LoanRequest : Screen()

    object LoanConfirmation : Screen()

    object LoanDetail : Screen()

    object IncidentReport : Screen()

    object IncidentDetail : Screen()

    object ProfileUpdate : Screen()
}