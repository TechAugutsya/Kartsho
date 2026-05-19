package com.example.kartsho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kartsho.domain.model.*
import com.example.kartsho.domain.repository.IKartshoRepository
import com.example.kartsho.ui.state.AppSection
import com.example.kartsho.ui.state.KartshoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KartshoViewModel(private val repository: IKartshoRepository) : ViewModel() {
    private val _state = MutableStateFlow(KartshoState(loading = true))
    val state: StateFlow<KartshoState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedIfNeeded()
            val activeSession = repository.checkActiveSession()
            if (activeSession != null) {
                val nextSection = when (activeSession.role) {
                    UserRole.Buyer -> AppSection.Shop
                    UserRole.Supplier -> AppSection.Upload
                    UserRole.Admin -> AppSection.Review
                }
                _state.update {
                    it.copy(
                        session = activeSession,
                        section = nextSection,
                        banner = "Welcome back, ${activeSession.name}.",
                        loading = false
                    )
                }
            }
            refreshData()
        }
    }

    private suspend fun refreshData() {
        val users = repository.getUsers()
        val products = repository.getProducts()
        val auctions = repository.getAuctions()
        _state.update {
            it.copy(
                users = users,
                products = products,
                auctions = auctions,
                loading = false
            )
        }
    }

    fun setSection(section: AppSection) {
        _state.update { it.copy(section = section, selectedProductId = null) }
    }

    fun selectProduct(productId: String?) {
        _state.update { it.copy(selectedProductId = productId) }
    }

    fun dismissBanner() {
        _state.update { it.copy(banner = null) }
    }

    fun logout() {
        repository.logoutSession()
        _state.update {
            it.copy(
                session = null,
                section = AppSection.Shop,
                banner = "Logged out successfully."
            )
        }
    }

    fun authenticate(
        mode: AuthMode,
        role: UserRole,
        name: String,
        email: String,
        password: String
    ): String? {
        var errorResult: String? = null
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val (user, error) = repository.authenticate(mode, role, name, email, password)
            if (error != null) {
                errorResult = error
                _state.update { it.copy(loading = false, banner = error) }
            } else if (user != null) {
                val nextSection = when (user.role) {
                    UserRole.Buyer -> AppSection.Shop
                    UserRole.Supplier -> AppSection.Upload
                    UserRole.Admin -> AppSection.Review
                }
                val bannerMsg = if (mode == AuthMode.Login) "Welcome back, ${user.name}." else "Account created for ${user.name}."
                _state.update {
                    it.copy(
                        session = user,
                        section = nextSection,
                        banner = bannerMsg,
                        loading = false
                    )
                }
                refreshData()
            }
        }
        return errorResult
    }

    fun addProduct(
        title: String,
        description: String,
        priceText: String,
        colorSeed: Int,
        imageUrl: String
    ): String? {
        val session = _state.value.session ?: return "Please sign in first."
        var errorResult: String? = null

        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val (_, error) = repository.addProduct(session, title, description, priceText, colorSeed, imageUrl)
            if (error != null) {
                errorResult = error
                _state.update { it.copy(loading = false, banner = error) }
            } else {
                _state.update { it.copy(banner = "Product submitted for admin approval.", loading = false) }
                refreshData()
            }
        }
        return errorResult
    }

    fun approveProduct(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            repository.approveProduct(productId)
            _state.update { it.copy(banner = "Product approved.", loading = false) }
            refreshData()
        }
    }

    fun rejectProduct(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            repository.rejectProduct(productId)
            _state.update { it.copy(banner = "Product removed.", loading = false) }
            refreshData()
        }
    }

    fun placeBid(auctionId: String, bidText: String): String? {
        val session = _state.value.session ?: return "Please sign in first."
        var errorResult: String? = null

        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val (_, error) = repository.placeBid(session, auctionId, bidText)
            if (error != null) {
                errorResult = error
                _state.update { it.copy(loading = false, banner = error) }
            } else {
                _state.update { it.copy(banner = "Bid placed successfully.", loading = false) }
                refreshData()
            }
        }
        return errorResult
    }

    fun addToCart(product: Product) {
        _state.update { current ->
            current.copy(
                cart = current.cart + product,
                banner = "🛒 Added ${product.title} to your cart!"
            )
        }
    }

    fun removeFromCart(productId: String) {
        _state.update { current ->
            current.copy(
                cart = current.cart.filterNot { it.id == productId },
                banner = "Item removed from cart."
            )
        }
    }

    fun toggleCartDialog(show: Boolean) {
        _state.update { it.copy(showCartDialog = show) }
    }

    fun startCheckout(product: Product?, fromCart: Boolean = false) {
        _state.update {
            it.copy(
                checkoutProduct = product,
                checkoutCart = fromCart,
                showCartDialog = false
            )
        }
    }

    fun confirmOrder(paymentMethod: String) {
        _state.update { current ->
            current.copy(
                cart = if (current.checkoutCart) emptyList() else current.cart,
                checkoutProduct = null,
                checkoutCart = false,
                banner = "🎉 Order placed successfully via $paymentMethod! Your package will be delivered by tomorrow."
            )
        }
    }

    fun cancelCheckout() {
        _state.update { it.copy(checkoutProduct = null, checkoutCart = false) }
    }
}
