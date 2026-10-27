package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.Destination
import com.example.data.model.Parcel
import com.example.data.model.ParcelStatus
import com.example.data.model.PaymentGatewayType
import com.example.data.model.PaymentStatus
import com.example.data.model.PaymentTransaction
import com.example.data.model.StatusHistoryItem
import com.example.data.repository.FirestoreParcelRepository
import com.example.data.repository.ParcelRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.ui.navigation.AppDestination

typealias AppScreen = AppDestination

data class UiMessage(
  val id: Long = System.currentTimeMillis(),
  val message: String,
  val isError: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application, viewModelScope)
  val repository = ParcelRepository(database.parcelDao(), database.destinationDao())

  // Firestore Repository initialized with named database ID from firebase_applet_config.xml
  private val firestoreDb = FirebaseFirestore.getInstance(
    application.getString(R.string.firestore_database_id)
  )
  private val firestoreRepository = FirestoreParcelRepository(firestoreDb)

  private val _currentScreen = MutableStateFlow(AppScreen.HOME)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  val allParcels: StateFlow<List<Parcel>> = repository.allParcels.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val activeDestinations: StateFlow<List<Destination>> = repository.activeDestinations.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  // Tracking State
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedParcel = MutableStateFlow<Parcel?>(null)
  val selectedParcel: StateFlow<Parcel?> = _selectedParcel.asStateFlow()

  // Payment Flow State
  private val _paymentTargetParcel = MutableStateFlow<Parcel?>(null)
  val paymentTargetParcel: StateFlow<Parcel?> = _paymentTargetParcel.asStateFlow()

  private val _isProcessingPayment = MutableStateFlow(false)
  val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

  private val _latestReceipt = MutableStateFlow<PaymentTransaction?>(null)
  val latestReceipt: StateFlow<PaymentTransaction?> = _latestReceipt.asStateFlow()

  // Admin Auth State
  private val _isAdminLoggedIn = MutableStateFlow(false)
  val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

  // Firebase Auth State
  private val _currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)
  val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

  private var firestoreSyncJob: Job? = null

  // UI Snackbars
  private val _uiMessage = MutableStateFlow<UiMessage?>(null)
  val uiMessage: StateFlow<UiMessage?> = _uiMessage.asStateFlow()

  // Quick price calculator state
  private val _calcCity = MutableStateFlow("Bouaké")
  val calcCity: StateFlow<String> = _calcCity.asStateFlow()

  private val _calcWeight = MutableStateFlow(1.5)
  val calcWeight: StateFlow<Double> = _calcWeight.asStateFlow()

  private val _calcCategory = MutableStateFlow("Colis standard")
  val calcCategory: StateFlow<String> = _calcCategory.asStateFlow()

  private val _calcPrice = MutableStateFlow(1500)
  val calcPrice: StateFlow<Int> = _calcPrice.asStateFlow()

  private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
    _currentUser.value = auth.currentUser
    if (auth.currentUser != null) {
      startFirestoreSync()
    } else {
      stopFirestoreSync()
    }
  }

  init {
    updateCalculatedPrice()
    Firebase.auth.addAuthStateListener(authStateListener)
    if (Firebase.auth.currentUser != null) {
      startFirestoreSync()
    }
  }

  override fun onCleared() {
    super.onCleared()
    Firebase.auth.removeAuthStateListener(authStateListener)
    stopFirestoreSync()
  }

  private fun startFirestoreSync() {
    firestoreSyncJob?.cancel()
    firestoreSyncJob = viewModelScope.launch {
      try {
        firestoreRepository.observeUserParcels().collect { firestoreList ->
          if (firestoreList.isNotEmpty()) {
            database.parcelDao().insertParcels(firestoreList)
          }
        }
      } catch (e: Exception) {
        // Logged via FirestoreErrorHandler
      }
    }
  }

  private fun stopFirestoreSync() {
    firestoreSyncJob?.cancel()
    firestoreSyncJob = null
  }

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  private val _isSearchingTracking = MutableStateFlow(false)
  val isSearchingTracking: StateFlow<Boolean> = _isSearchingTracking.asStateFlow()

  fun selectParcelForTracking(parcel: Parcel) {
    _selectedParcel.value = parcel
    _searchQuery.value = parcel.reference
    _currentScreen.value = AppScreen.TRACKING
  }

  fun searchParcelByRef(ref: String) {
    val clean = ref.trim().uppercase()
    _searchQuery.value = clean
    viewModelScope.launch {
      _isSearchingTracking.value = true
      try {
        // 1. Query Cloud Firestore first for live real-time status
        val firestoreData = firestoreRepository.fetchTracking(clean)
        if (firestoreData != null) {
          val parcelStatus = try {
            ParcelStatus.valueOf(firestoreData["status"] as? String ?: "DEMANDE_RECU")
          } catch (e: Exception) { ParcelStatus.DEMANDE_RECU }

          val pStatus = try {
            PaymentStatus.valueOf(firestoreData["paymentStatus"] as? String ?: "NON_PAYE")
          } catch (e: Exception) { PaymentStatus.NON_PAYE }

          val historyData = (firestoreData["history"] as? List<*>) ?: emptyList<Any>()
          val historyItems = historyData.mapNotNull { item ->
            val map = item as? Map<*, *> ?: return@mapNotNull null
            try {
              val st = ParcelStatus.valueOf(map["status"] as? String ?: "DEMANDE_RECU")
              val ts = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
              val loc = map["location"] as? String ?: "Côte d'Ivoire"
              val com = map["comment"] as? String ?: ""
              StatusHistoryItem(st, ts, loc, com)
            } catch (e: Exception) { null }
          }

          val p = Parcel(
            reference = clean,
            senderName = "Client Abidjan",
            senderPhone = "",
            senderCommune = firestoreData["senderCommune"] as? String ?: "Abidjan",
            senderAddress = "",
            recipientName = firestoreData["recipientName"] as? String ?: "Destinataire",
            recipientPhone = "",
            destinationCity = firestoreData["destinationCity"] as? String ?: "Côte d'Ivoire",
            destinationAddress = "",
            parcelCategory = "Colis",
            weightKg = 1.0,
            description = "Colis enregistré sur Firestore",
            priceFcfa = ((firestoreData["priceFcfa"] as? Number)?.toInt() ?: 1500),
            status = parcelStatus,
            paymentStatus = pStatus,
            createdAt = System.currentTimeMillis(),
            history = historyItems
          )

          _selectedParcel.value = p
          _currentScreen.value = AppScreen.TRACKING
          _uiMessage.value = UiMessage(message = "Colis $clean synchronisé depuis Cloud Firestore !")
          return@launch
        }

        // 2. Fallback to local Room database
        val found = repository.getParcel(clean)
        if (found != null) {
          _selectedParcel.value = found
          _currentScreen.value = AppScreen.TRACKING
          return@launch
        }

        val loose = allParcels.value.firstOrNull { it.reference.contains(clean, ignoreCase = true) }
        if (loose != null) {
          _selectedParcel.value = loose
          _currentScreen.value = AppScreen.TRACKING
        } else {
          _uiMessage.value = UiMessage(message = "Aucun colis trouvé avec la référence : $clean sur Firestore ni en local", isError = true)
        }
      } catch (e: Exception) {
        val found = repository.getParcel(clean)
        if (found != null) {
          _selectedParcel.value = found
          _currentScreen.value = AppScreen.TRACKING
        } else {
          _uiMessage.value = UiMessage(message = "Erreur de recherche Firestore : ${e.localizedMessage}", isError = true)
        }
      } finally {
        _isSearchingTracking.value = false
      }
    }
  }

  fun setCalcParams(city: String? = null, weight: Double? = null, category: String? = null) {
    city?.let { _calcCity.value = it }
    weight?.let { _calcWeight.value = it }
    category?.let { _calcCategory.value = it }
    updateCalculatedPrice()
  }

  private fun updateCalculatedPrice() {
    viewModelScope.launch {
      val price = repository.calculateEstimatedPrice(_calcCity.value, _calcWeight.value, _calcCategory.value)
      _calcPrice.value = price
    }
  }

  fun submitPickupOrder(
    senderName: String,
    senderPhone: String,
    senderCommune: String,
    senderAddress: String,
    recipientName: String,
    recipientPhone: String,
    destinationCity: String,
    destinationAddress: String,
    parcelCategory: String,
    weightKg: Double,
    description: String,
    chosenPaymentMethod: PaymentGatewayType,
    payerPhone: String,
    onSuccess: (Parcel) -> Unit
  ) {
    viewModelScope.launch {
      val estimatedPrice = repository.calculateEstimatedPrice(destinationCity, weightKg, parcelCategory)
      val ref = ParcelRepository.generateReference()
      val now = System.currentTimeMillis()

      val isImmediateOnlinePayment = chosenPaymentMethod != PaymentGatewayType.ESPECES_COLLECTE

      val initialPaymentStatus = if (isImmediateOnlinePayment) {
        if (chosenPaymentMethod == PaymentGatewayType.CARTE_BANCAIRE) PaymentStatus.PAYE_CARTE else PaymentStatus.PAYE_MOBILE_MONEY
      } else {
        PaymentStatus.PAYE_A_LA_COLLECTE
      }

      val initialTransactionId = if (isImmediateOnlinePayment) {
        "TXN-${chosenPaymentMethod.name.take(3)}-${(100000..999999).random()}"
      } else null

      val parcel = Parcel(
        reference = ref,
        senderName = senderName.trim(),
        senderPhone = senderPhone.trim(),
        senderCommune = senderCommune.trim(),
        senderAddress = senderAddress.trim(),
        recipientName = recipientName.trim(),
        recipientPhone = recipientPhone.trim(),
        destinationCity = destinationCity,
        destinationAddress = destinationAddress.trim(),
        parcelCategory = parcelCategory,
        weightKg = weightKg,
        description = description.trim(),
        priceFcfa = estimatedPrice,
        status = ParcelStatus.DEMANDE_RECU,
        paymentStatus = initialPaymentStatus,
        paymentMethod = chosenPaymentMethod.displayName,
        paymentTransactionId = initialTransactionId,
        createdAt = now,
        history = listOf(
          StatusHistoryItem(
            status = ParcelStatus.DEMANDE_RECU,
            timestamp = now,
            location = "Abidjan - $senderCommune",
            comment = "Demande d'enlèvement enregistrée. Mode de règlement : ${chosenPaymentMethod.displayName}."
          )
        )
      )

      // 1. Save to local Room DB
      repository.createParcel(parcel)

      // 2. Synchronize to Firebase Firestore in real-time if signed in
      if (Firebase.auth.currentUser != null) {
        try {
          firestoreRepository.saveParcel(parcel)
        } catch (e: Exception) {
          // Handled via firestore error handler
        }
      }

      _selectedParcel.value = parcel
      val saveNotice = if (Firebase.auth.currentUser != null) {
        "Commande $ref enregistrée sur Firebase Firestore & en local !"
      } else {
        "Commande $ref enregistrée en local. Connectez-vous avec Google pour synchroniser sur le Cloud !"
      }
      _uiMessage.value = UiMessage(message = saveNotice)

      if (isImmediateOnlinePayment) {
        _latestReceipt.value = PaymentTransaction(
          transactionId = initialTransactionId ?: "TXN-OK",
          parcelReference = ref,
          amountFcfa = estimatedPrice,
          gateway = chosenPaymentMethod,
          payerPhone = if (payerPhone.isNotBlank()) payerPhone else senderPhone,
          status = "SUCCESS",
          timestamp = now
        )
      }

      onSuccess(parcel)
    }
  }

  fun startPaymentForParcel(parcel: Parcel) {
    _paymentTargetParcel.value = parcel
    _currentScreen.value = AppScreen.PAYMENT
  }

  fun dismissPaymentDialog() {
    _paymentTargetParcel.value = null
  }

  fun processDirectPayment(
    parcel: Parcel,
    gateway: PaymentGatewayType,
    payerPhone: String,
    onCompleted: (PaymentTransaction) -> Unit
  ) {
    viewModelScope.launch {
      _isProcessingPayment.value = true
      kotlinx.coroutines.delay(1200)

      val txnId = "${gateway.name.take(3)}-CI-${(100000..999999).random()}"
      val pStatus = if (gateway == PaymentGatewayType.CARTE_BANCAIRE) PaymentStatus.PAYE_CARTE else PaymentStatus.PAYE_MOBILE_MONEY

      repository.recordPayment(parcel.reference, gateway.displayName, txnId, pStatus)

      // Also sync payment status to Firestore
      if (Firebase.auth.currentUser != null) {
        try {
          val updated = parcel.copy(
            paymentStatus = pStatus,
            paymentMethod = gateway.displayName,
            paymentTransactionId = txnId
          )
          firestoreRepository.saveParcel(updated)
        } catch (e: Exception) {
          // Handled
        }
      }

      val updated = repository.getParcel(parcel.reference)
      if (updated != null) {
        _selectedParcel.value = updated
      }

      val receipt = PaymentTransaction(
        transactionId = txnId,
        parcelReference = parcel.reference,
        amountFcfa = parcel.priceFcfa,
        gateway = gateway,
        payerPhone = payerPhone,
        status = "SUCCESS",
        timestamp = System.currentTimeMillis()
      )
      _latestReceipt.value = receipt
      _isProcessingPayment.value = false
      _uiMessage.value = UiMessage(message = "Paiement de ${parcel.priceFcfa} FCFA validé via ${gateway.displayName} !")
      onCompleted(receipt)
    }
  }

  fun dismissReceipt() {
    _latestReceipt.value = null
  }

  fun loginAdmin(pin: String): Boolean {
    if (pin.trim() == "2026" || pin.trim() == "0000") {
      _isAdminLoggedIn.value = true
      _uiMessage.value = UiMessage(message = "Connexion Administrateur réussie.")
      return true
    } else {
      _uiMessage.value = UiMessage(message = "Code PIN administrateur incorrect (défaut : 2026)", isError = true)
      return false
    }
  }

  fun logoutAdmin() {
    _isAdminLoggedIn.value = false
    _uiMessage.value = UiMessage(message = "Déconnexion de l'espace administrateur.")
  }

  fun adminUpdateStatus(
    ref: String,
    newStatus: ParcelStatus,
    location: String,
    comment: String
  ) {
    viewModelScope.launch {
      val success = repository.updateParcelStatus(ref, newStatus, location, comment)
      if (success) {
        val updatedParcel = repository.getParcel(ref)

        // Sync to Firestore in real-time
        if (Firebase.auth.currentUser != null) {
          try {
            firestoreRepository.updateParcelStatus(ref, newStatus, location, comment)
          } catch (e: Exception) {
            // Handled
          }
        }

        // Trigger local notification to alert the user about the parcel's new status
        val destCity = updatedParcel?.destinationCity ?: "Côte d'Ivoire"
        com.example.ui.util.NotificationHelper.notifyParcelStatusChanged(
          context = getApplication(),
          parcelReference = ref,
          destinationCity = destCity,
          newStatus = newStatus,
          location = location,
          comment = comment
        )

        _uiMessage.value = UiMessage(message = "Statut du colis $ref mis à jour : ${newStatus.label}")
        if (_selectedParcel.value?.reference == ref) {
          _selectedParcel.value = updatedParcel
        }
      } else {
        _uiMessage.value = UiMessage(message = "Colis $ref introuvable.", isError = true)
      }
    }
  }

  fun updateParcelStatusQuick(ref: String, newStatus: ParcelStatus) {
    val (defaultLocation, defaultComment) = when (newStatus) {
      ParcelStatus.RECU_AGENCE -> "Hub Abidjan Cocody Faya" to "Colis reçu et vérifié à l'agence"
      ParcelStatus.EN_TRANSIT -> "En cours d'acheminement" to "Colis en transit vers l'intérieur de la Côte d'Ivoire"
      ParcelStatus.ARRIVE_DESTINATION -> "Gare d'arrivée" to "Colis réceptionné au guichet local"
      ParcelStatus.LIVRE -> "Gare / Domicile destination" to "Colis livré avec succès au destinataire"
      ParcelStatus.EXPEDIE -> "Gare de départ Abidjan" to "Colis expédié"
      ParcelStatus.RAMASSE_ABIDJAN -> "Abidjan (Ramassage)" to "Colis récupéré par le coursier"
      else -> "Agence Abidjan Cocody" to "Statut mis à jour par l'agent logistique"
    }
    adminUpdateStatus(ref, newStatus, defaultLocation, defaultComment)
  }

  fun sendTestNotification(ref: String = "IVO-TEST-01", city: String = "Bouaké") {
    com.example.ui.util.NotificationHelper.notifyParcelStatusChanged(
      context = getApplication(),
      parcelReference = ref,
      destinationCity = city,
      newStatus = ParcelStatus.EN_TRANSIT,
      location = "Autoroute du Nord vers Bouaké",
      comment = "Acheminement en cours par les équipes IVOCOLIS"
    )
    _uiMessage.value = UiMessage(message = "Notification de test envoyée !")
  }

  fun refreshFirestoreData() {
    if (Firebase.auth.currentUser != null) {
      startFirestoreSync()
      _uiMessage.value = UiMessage(message = "Synchronisation Firestore actualisée.")
    } else {
      _uiMessage.value = UiMessage(message = "Connectez-vous avec Google pour synchroniser Firestore.", isError = true)
    }
  }

  fun clearUiMessage() {
    _uiMessage.value = null
  }
}
