package com.example.kartsho

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kartsho.domain.model.*
import com.example.kartsho.ui.screens.*
import com.example.kartsho.ui.state.*
import com.example.kartsho.ui.viewmodel.KartshoViewModel
import com.example.kartsho.util.LocalImagePicker
import com.example.kartsho.util.KartshoBackHandler
import com.example.kartsho.di.AppModule
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.network.ktor3.KtorNetworkFetcherFactory
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun KartshoApp(viewModel: KartshoViewModel) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .crossfade(true)
            .build()
    }

    CompositionLocalProvider(LocalImagePicker provides AppModule.imagePicker) {
        val state by viewModel.state.collectAsState()
        val session = state.session
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(state.banner) {
            val message = state.banner ?: return@LaunchedEffect
            snackbarHostState.showSnackbar(message)
            viewModel.dismissBanner()
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (session != null) {
                    DemoTopBar(
                        session = session,
                        section = state.section,
                        cartCount = state.cart.size,
                        showBackButton = state.selectedProductId != null,
                        onBack = { viewModel.selectProduct(null) },
                        onOpenCart = { viewModel.toggleCartDialog(true) },
                        onLogout = viewModel::logout
                    )
                }
            },
            bottomBar = {
                if (session != null && state.selectedProductId == null) {
                    DemoBottomBar(
                        session = session,
                        section = state.section,
                        onSectionChange = viewModel::setSection
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
            ) {
                if (state.session == null) {
                    AuthScreen(onSubmit = viewModel::authenticate)
                } else {
                    HomeScreen(
                        state = state,
                        viewModel = viewModel
                    )

                    if (state.showCartDialog) {
                        CartDialog(
                            cart = state.cart,
                            onDismiss = { viewModel.toggleCartDialog(false) },
                            onRemoveItem = viewModel::removeFromCart,
                            onCheckout = { viewModel.startCheckout(null, fromCart = true) }
                        )
                    }

                    if (state.checkoutProduct != null || state.checkoutCart) {
                        val total = if (state.checkoutCart) state.cart.sumOf { it.price } else null
                        CheckoutDialog(
                            product = state.checkoutProduct,
                            cartTotal = total,
                            onDismiss = viewModel::cancelCheckout,
                            onConfirmOrder = viewModel::confirmOrder
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DemoTopBar(
    session: User,
    section: AppSection,
    cartCount: Int,
    showBackButton: Boolean,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            if (showBackButton) {
                androidx.compose.material3.IconButton(onClick = onBack) {
                    Text("⬅️")
                }
            }
        },
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Kartsho", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("by Zeerostock", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = "${section.label} dashboard",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        actions = {
            if (cartCount > 0) {
                androidx.compose.material3.BadgedBox(
                    badge = {
                        androidx.compose.material3.Badge {
                            Text(cartCount.toString())
                        }
                    }
                ) {
                    androidx.compose.material3.IconButton(onClick = onOpenCart) {
                        Text("🛒")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            AssistChip(
                onClick = {},
                label = { Text(session.role.label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun DemoBottomBar(
    session: User,
    section: AppSection,
    onSectionChange: (AppSection) -> Unit
) {
    val options = when (session.role) {
        UserRole.Buyer -> listOf(AppSection.Shop, AppSection.Auctions)
        UserRole.Supplier -> listOf(AppSection.Upload, AppSection.Shop, AppSection.Auctions)
        UserRole.Admin -> listOf(AppSection.Review, AppSection.Shop, AppSection.Auctions)
    }

    NavigationBar {
        options.forEach { item ->
            NavigationBarItem(
                selected = section == item,
                onClick = { onSectionChange(item) },
                icon = { /* Icon can be added here */ },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    state: KartshoState,
    viewModel: KartshoViewModel
) {
    val isDialogVisible = state.showCartDialog || state.checkoutProduct != null || state.checkoutCart
    val isNotAtRoot = isDialogVisible || state.selectedProductId != null || state.section != AppSection.Shop
    
    KartshoBackHandler(enabled = isNotAtRoot) {
        when {
            state.checkoutProduct != null || state.checkoutCart -> viewModel.cancelCheckout()
            state.showCartDialog -> viewModel.toggleCartDialog(false)
            state.selectedProductId != null -> viewModel.selectProduct(null)
            state.section != AppSection.Shop -> viewModel.setSection(AppSection.Shop)
        }
    }

    if (state.selectedProductId != null) {
        val selectedProduct = state.products.firstOrNull { it.id == state.selectedProductId }
        if (selectedProduct != null) {
            ProductDetailScreen(
                product = selectedProduct,
                onBack = { viewModel.selectProduct(null) },
                onAddToCart = viewModel::addToCart,
                onBuyNow = { viewModel.startCheckout(it, fromCart = false) }
            )
            return
        }
    }

    when (state.section) {
        AppSection.Shop -> MarketplaceScreen(
            state = state,
            onSelectProduct = viewModel::selectProduct,
            onAddToCart = viewModel::addToCart,
            onBuyNow = { viewModel.startCheckout(it, fromCart = false) }
        )
        AppSection.Auctions -> AuctionScreen(state = state, onPlaceBid = viewModel::placeBid)
        AppSection.Upload -> SupplierScreen(state = state, onAddProduct = viewModel::addProduct)
        AppSection.Review -> AdminScreen(
            state = state,
            onApproveProduct = viewModel::approveProduct,
            onRejectProduct = viewModel::rejectProduct
        )
    }
}

@Composable
fun CartDialog(
    cart: List<Product>,
    onDismiss: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🛒 Your Shopping Cart", fontWeight = FontWeight.Bold) },
        text = {
            if (cart.isEmpty()) {
                Text("Your cart is currently empty.", modifier = Modifier.padding(16.dp))
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    cart.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                                Text("₹${item.price.toInt()}", color = MaterialTheme.colorScheme.primary)
                            }
                            TextButton(onClick = { onRemoveItem(item.id) }) {
                                Text("Remove", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    val total = cart.sumOf { it.price }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("₹${total.toInt()}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
        confirmButton = {
            if (cart.isNotEmpty()) {
                androidx.compose.material3.Button(onClick = onCheckout) {
                    Text("Proceed to Checkout ⚡")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continue Shopping")
            }
        }
    )
}

@Composable
fun CheckoutDialog(
    product: Product?,
    cartTotal: Double?,
    onDismiss: () -> Unit,
    onConfirmOrder: (String) -> Unit
) {
    var selectedPayment by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("💳 Cash on Delivery (COD)") }
    val amount = product?.price ?: cartTotal ?: 0.0

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📦 Order Confirmation", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Review your order summary and select a payment method below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (product != null) {
                            Text("Item: ${product.title}", fontWeight = FontWeight.SemiBold)
                        } else {
                            Text("Item: Cart Total (Multiple Items)", fontWeight = FontWeight.SemiBold)
                        }
                        Text("Delivery Fee: FREE 🚚", color = MaterialTheme.colorScheme.primary)
                        androidx.compose.material3.HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Payable:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("₹${amount.toInt()}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Text("Select Payment Method", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                listOf("💳 Cash on Delivery (COD)", "⚡ UPI / Google Pay", "🏦 Net Banking / Cards").forEach { method ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { selectedPayment = method }.padding(vertical = 4.dp)
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedPayment == method,
                            onClick = { selectedPayment = method }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(method, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Text(
                    "🛡️ 100% Safe & Secure Payments powered by Zeerostock Gateway.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(onClick = { onConfirmOrder(selectedPayment) }) {
                Text("Confirm Order 🎉")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
