package com.example.kartsho.data.repository

import com.example.kartsho.domain.model.*
import com.example.kartsho.data.mapper.*
import com.example.kartsho.`data`.local.AuctionEntity
import com.example.kartsho.`data`.local.ProductEntity
import com.example.kartsho.`data`.local.UserEntity
import com.example.kartsho.`data`.local.KartshoDao
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.Clock
import kotlinx.coroutines.withTimeoutOrNull

// Abstracting Settings for KMP
interface KmpSettings {
    fun getString(key: String, defaultValue: String?): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
}

class KartshoRepositoryImpl(
    private val dao: KartshoDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val settings: KmpSettings
) : com.example.kartsho.domain.repository.IKartshoRepository {
    private val seedTime = Clock.System.now().toEpochMilliseconds()
    private var isFirestoreAvailable = true

    private fun checkFirestoreException(e: Exception) {
        isFirestoreAvailable = false
        println("StockbrokerRepository: Firestore operation failed. Falling back to local Room database. Error: ${e.message}")
    }

    private val seedUsers = listOf(
        User("u-admin", "Aarav Admin", "admin@zeerostock.com", "admin123", UserRole.Admin),
        User("u-buyer", "Maya Buyer", "buyer@zeerostock.com", "buyer123", UserRole.Buyer),
        User("u-supplier", "Rohan Supplier", "supplier@zeerostock.com", "supplier123", UserRole.Supplier)
    )

    private val seedProducts = listOf(
        Product(
            id = "p-1",
            title = "Compact Bluetooth Speaker",
            description = "Portable speaker with rich bass, 10 hour battery, and rugged build.",
            price = 2499.0,
            supplierName = "Rohan Supplier",
            supplierId = "u-supplier",
            approved = true,
            createdAtMillis = seedTime - 86_400_000L,
            colorSeed = 0,
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSfB5HRC3g0rJME8O0IIjqs7agtltUJQkjzIw&s"
        ),
        Product(
            id = "p-2",
            title = "Noise Canceling Headphones",
            description = "Over-ear wireless headphones for travel, study, and long work sessions.",
            price = 7999.0,
            supplierName = "Rohan Supplier",
            supplierId = "u-supplier",
            approved = false,
            createdAtMillis = seedTime - 48_000_000L,
            colorSeed = 1,
            imageUrl = "https://www.analog.com/en/_/media/analog/en/signals/bowers-wilkins-noise-canceling-headphones/bowerswilkins-hero-mobile.jpg?rev=82b8eb63c84d4c98ae66e58825e495ee&sc_lang=en&la=en&h=350&w=640&hash=9AE32307C3B4D70B9ADD0657BCF6114A"
        ),
        Product(
            id = "p-3",
            title = "Stainless Steel Bottle",
            description = "Vacuum insulated bottle with a matte finish and anti-slip grip.",
            price = 899.0,
            supplierName = "Asha Supplies",
            supplierId = "u-supplier",
            approved = true,
            createdAtMillis = seedTime - 12_000_000L,
            colorSeed = 2,
            imageUrl = "https://cdn.shopify.com/s/files/1/0104/9211/7092/files/Steel_Flasks_Mobile_9fc4541e-f969-4b55-a80a-d1e6250fb42e.jpg?v=1709120027"
        ),
        Product(
            id = "p-4",
            title = "Ultra-Slim 4K Monitor",
            description = "27-inch IPS display with USB-C 90W power delivery, 99% sRGB color accuracy, and ultra-thin bezels.",
            price = 28999.0,
            supplierName = "Rohan Supplier",
            supplierId = "u-supplier",
            approved = true,
            createdAtMillis = seedTime - 5_000_000L,
            colorSeed = 3,
            imageUrl = "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=1000&auto=format&fit=crop"
        ),
        Product(
            id = "p-5",
            title = "Ergonomic Mechanical Keyboard",
            description = "Hot-swappable tactile switches, customizable RGB backlighting, PBT keycaps, and a solid aluminum frame.",
            price = 11499.0,
            supplierName = "Asha Supplies",
            supplierId = "u-supplier",
            approved = true,
            createdAtMillis = seedTime - 20_000_000L,
            colorSeed = 0,
            imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?q=80&w=1000&auto=format&fit=crop"
        ),
        Product(
            id = "p-6",
            title = "Professional Mirrorless Camera",
            description = "24.2MP full-frame sensor, 4K60p 10-bit internal video, 5-axis in-body image stabilization, and dual SD slots.",
            price = 145999.0,
            supplierName = "Rohan Supplier",
            supplierId = "u-supplier",
            approved = true,
            createdAtMillis = seedTime - 35_000_000L,
            colorSeed = 1,
            imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?q=80&w=1000&auto=format&fit=crop"
        ),
        Product(
            id = "p-7",
            title = "Smart Fitness Ring",
            description = "Ultra-light titanium smart ring tracking sleep stages, heart rate variability, blood oxygen, and daily readiness score.",
            price = 18999.0,
            supplierName = "Asha Supplies",
            supplierId = "u-supplier",
            approved = false,
            createdAtMillis = seedTime - 2_000_000L,
            colorSeed = 2,
            imageUrl = "https://images.unsplash.com/photo-1605100804763-247f67b3557e?q=80&w=1000&auto=format&fit=crop"
        )
    )

    private val seedAuctions = listOf(
        Auction(
            id = "a-1",
            title = "Limited Edition Smartwatch",
            description = "Auction listing with live updates, countdown timer, and bid history.",
            startingPrice = 4999.0,
            currentBid = 6899.0,
            currentBidderName = "Maya Buyer",
            endAtMillis = seedTime + 10_800_000L, // 3 hours from now
            supplierName = "Rohan Supplier",
            approved = true,
            bidHistory = listOf(
                BidEntry("Maya Buyer", 5600.0, seedTime - 3_600_000L),
                BidEntry("Aman Trader", 6899.0, seedTime - 800_000L)
            ),
            colorSeed = 0,
            imageUrl = "https://i.ytimg.com/vi/DWvYzHsNVjM/maxresdefault.jpg"
        ),
        Auction(
            id = "a-2",
            title = "Collector Camera Lens",
            description = "Premium lens suitable for demos that need a stronger auction showcase.",
            startingPrice = 9999.0,
            currentBid = 9999.0,
            currentBidderName = null,
            endAtMillis = seedTime + 12_600_000L, // 3.5 hours from now
            supplierName = "Asha Supplies",
            approved = true,
            bidHistory = emptyList(),
            colorSeed = 1,
            imageUrl = "https://images.squarespace-cdn.com/content/v1/5fa99add26e5b1322ec84e98/1614817384223-6ZFG8VKRLDRQ6AZ0L13Z/lens+collection+2021-02-26-001.jpg"
        ),
        Auction(
            id = "a-3",
            title = "Vintage Acoustic Guitar",
            description = "Handcrafted 1978 dreadnought acoustic guitar with solid Sitka spruce top and Brazilian rosewood back/sides.",
            startingPrice = 45000.0,
            currentBid = 52500.0,
            currentBidderName = "Aman Trader",
            endAtMillis = seedTime + 14_400_000L, // 4 hours from now
            supplierName = "Asha Supplies",
            approved = true,
            bidHistory = listOf(
                BidEntry("Aman Trader", 52500.0, seedTime - 1_200_000L),
                BidEntry("Maya Buyer", 48000.0, seedTime - 2_500_000L)
            ),
            colorSeed = 2,
            imageUrl = "https://images.unsplash.com/photo-1550291652-6ea9114a47b1?q=80&w=1000&auto=format&fit=crop"
        )
    )

    override suspend fun checkActiveSession(): User? {
        val activeEmail = settings.getString("active_user_email", null) ?: auth.currentUser?.email
        if (activeEmail != null) {
            val localUsers = dao.getAllUsers()
            val matched = localUsers.firstOrNull { it.email.lowercase() == activeEmail.lowercase() }
            if (matched != null) {
                return matched.toDomain()
            }
        }
        return null
    }

    override fun logoutSession() {
        settings.remove("active_user_email")
    }

    override suspend fun seedIfNeeded() {
        try {
            val existingUsers = dao.getAllUsers()
            if (existingUsers.isEmpty()) {
                dao.insertUsers(seedUsers.map { it.toEntity() })
                dao.insertProducts(seedProducts.map { it.toEntity() })
                dao.insertAuctions(seedAuctions.map { it.toEntity() })

                if (isFirestoreAvailable) {
                    try {
                        val result = withTimeoutOrNull(2500L) {
                            seedUsers.forEach { user ->
                                firestore.collection("users").document(user.id).set(user.toEntity())
                            }
                            seedProducts.forEach { prod ->
                                firestore.collection("products").document(prod.id).set(prod.toEntity())
                            }
                            seedAuctions.forEach { auc ->
                                firestore.collection("auctions").document(auc.id).set(auc.toEntity())
                            }
                            true
                        }
                        if (result == null) {
                            isFirestoreAvailable = false
                            println("ZeerostockRepo: Firestore seed timed out. Falling back to local Room database.")
                        }
                    } catch (e: Exception) {
                        checkFirestoreException(e)
                        println("ZeerostockRepo Firestore seed skipped/failed: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("ZeerostockRepo Error during seed: ${e.message}")
        }
    }

    override suspend fun getUsers(): List<User> {
        if (!isFirestoreAvailable) {
            return dao.getAllUsers().map { it.toDomain() }
        }
        return try {
            val snapshot = withTimeoutOrNull(2500L) {
                firestore.collection("users").get()
            }
            if (snapshot != null) {
                val remoteUsers = snapshot.documents.map { doc ->
                    doc.toUserEntity().toDomain()
                }
                if (remoteUsers.isNotEmpty()) {
                    dao.insertUsers(remoteUsers.map { it.toEntity() })
                    remoteUsers
                } else {
                    dao.getAllUsers().map { it.toDomain() }
                }
            } else {
                isFirestoreAvailable = false
                dao.getAllUsers().map { it.toDomain() }
            }
        } catch (e: Exception) {
            checkFirestoreException(e)
            dao.getAllUsers().map { it.toDomain() }
        }
    }

    override suspend fun getProducts(): List<Product> {
        if (!isFirestoreAvailable) {
            return dao.getAllProducts().map { it.toDomain() }
        }
        return try {
            val snapshot = withTimeoutOrNull(2500L) {
                firestore.collection("products").get()
            }
            if (snapshot != null) {
                val remoteProducts = snapshot.documents.map { doc ->
                    doc.toProductEntity().toDomain()
                }
                if (remoteProducts.isNotEmpty()) {
                    dao.insertProducts(remoteProducts.map { it.toEntity() })
                    remoteProducts.sortedByDescending { it.createdAtMillis }
                } else {
                    dao.getAllProducts().map { it.toDomain() }
                }
            } else {
                isFirestoreAvailable = false
                dao.getAllProducts().map { it.toDomain() }
            }
        } catch (e: Exception) {
            checkFirestoreException(e)
            dao.getAllProducts().map { it.toDomain() }
        }
    }

    override suspend fun getAuctions(): List<Auction> {
        if (!isFirestoreAvailable) {
            return dao.getAllAuctions().map { it.toDomain() }
        }
        return try {
            val snapshot = withTimeoutOrNull(2500L) {
                firestore.collection("auctions").get()
            }
            if (snapshot != null) {
                val remoteAuctions = snapshot.documents.map { doc ->
                    doc.toAuctionEntity().toDomain()
                }
                if (remoteAuctions.isNotEmpty()) {
                    dao.insertAuctions(remoteAuctions.map { it.toEntity() })
                    remoteAuctions.sortedBy { it.endAtMillis }
                } else {
                    dao.getAllAuctions().map { it.toDomain() }
                }
            } else {
                isFirestoreAvailable = false
                dao.getAllAuctions().map { it.toDomain() }
            }
        } catch (e: Exception) {
            checkFirestoreException(e)
            dao.getAllAuctions().map { it.toDomain() }
        }
    }

    override suspend fun authenticate(
        mode: AuthMode,
        role: UserRole,
        name: String,
        email: String,
        password: String
    ): Pair<User?, String?> {
        val result = authenticateInternal(mode, role, name, email, password)
        if (result.first != null) {
            settings.putString("active_user_email", result.first!!.email)
        }
        return result
    }

    private suspend fun authenticateInternal(
        mode: AuthMode,
        role: UserRole,
        name: String,
        email: String,
        password: String
    ): Pair<User?, String?> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = name.trim()

        if (trimmedEmail.isBlank() || password.isBlank()) {
            return null to "Email and password are required."
        }

        if (mode == AuthMode.SignUp && trimmedName.isBlank()) {
            return null to "Name is required for sign up."
        }

        if (mode == AuthMode.SignUp && role == UserRole.Admin) {
            return null to "Admin sign up is disabled in this prototype."
        }

        val localUsers = dao.getAllUsers()
        val matchedLocal = localUsers.firstOrNull { it.email.lowercase() == trimmedEmail }

        if (mode == AuthMode.Login) {
            try {
                val authResult = auth.signInWithEmailAndPassword(trimmedEmail, password)
                val firebaseUid = authResult.user?.uid ?: "u-fb"

                var remoteUser: User? = null
                if (isFirestoreAvailable) {
                    try {
                        val docRef = withTimeoutOrNull(2500L) {
                            firestore.collection("users").document(firebaseUid).get()
                        }
                        if (docRef != null) {
                            remoteUser = docRef.toUserEntity().toDomain()
                        } else {
                            isFirestoreAvailable = false
                        }
                    } catch (e: Exception) {
                        checkFirestoreException(e)
                        println("Firestore fetch by ID failed, trying by email: ${e.message}")
                        if (isFirestoreAvailable) {
                            try {
                                val userDoc = withTimeoutOrNull(2500L) {
                                    firestore.collection("users").where { "email" equalTo trimmedEmail }.get()
                                }
                                if (userDoc != null) {
                                    remoteUser = userDoc.documents.firstOrNull()?.toUserEntity()?.toDomain()
                                } else {
                                    isFirestoreAvailable = false
                                }
                            } catch (e2: Exception) {
                                checkFirestoreException(e2)
                                println("Firestore fetch by email failed: ${e2.message}")
                            }
                        }
                    }
                }

                if (remoteUser != null) {
                    dao.insertUser(remoteUser.toEntity())
                    return remoteUser to null
                }

                if (matchedLocal != null) {
                    return matchedLocal.toDomain() to null
                }

                val emailName = trimmedEmail.substringBefore("@")
                    .split(Regex("[._\\-]"))
                    .joinToString(" ") { part -> part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }

                val dynamicUser = User(
                    id = firebaseUid,
                    name = authResult.user?.displayName?.takeIf { it.isNotBlank() } ?: emailName,
                    email = trimmedEmail,
                    password = password,
                    role = role
                )
                if (isFirestoreAvailable) {
                    try {
                        val writeRes = withTimeoutOrNull(2500L) {
                            firestore.collection("users").document(firebaseUid).set(dynamicUser.toEntity())
                            true
                        }
                        if (writeRes == null) {
                            isFirestoreAvailable = false
                        }
                    } catch (e: Exception) {
                        checkFirestoreException(e)
                    }
                }
                dao.insertUser(dynamicUser.toEntity())
                return dynamicUser to null

            } catch (e: Exception) {
                if (matchedLocal == null) {
                    return null to (e.message ?: "Firebase authentication failed.")
                }
            }

            return when {
                matchedLocal == null -> null to "No account found for that email."
                matchedLocal.passwordHash != password -> null to "Password does not match."
                matchedLocal.role != role.label -> null to "That account belongs to a different role."
                else -> matchedLocal.toDomain() to null
            }
        } else {
            if (matchedLocal != null) {
                return null to "An account already exists for that email."
            }

            val fallbackId = "u-${localUsers.size + 1}"
            val baseUser = User(
                id = fallbackId,
                name = trimmedName,
                email = trimmedEmail,
                password = password,
                role = role
            )

            try {
                val authResult = auth.createUserWithEmailAndPassword(trimmedEmail, password)
                val actualUid = authResult.user?.uid ?: fallbackId
                val firebaseNewUser = baseUser.copy(id = actualUid)
                if (isFirestoreAvailable) {
                    try {
                        val writeRes = withTimeoutOrNull(2500L) {
                            firestore.collection("users").document(actualUid).set(firebaseNewUser.toEntity())
                            true
                        }
                        if (writeRes == null) {
                            isFirestoreAvailable = false
                        }
                    } catch (e: Exception) {
                        checkFirestoreException(e)
                    }
                }
                dao.insertUser(firebaseNewUser.toEntity())
                return firebaseNewUser to null
            } catch (e: Exception) {}

            dao.insertUser(baseUser.toEntity())
            return baseUser to null
        }
    }

    override suspend fun addProduct(
        session: User,
        title: String,
        description: String,
        priceText: String,
        colorSeed: Int,
        imageUrl: String
    ): Pair<Product?, String?> {
        if (session.role != UserRole.Supplier && session.role != UserRole.Admin) {
            return null to "Only suppliers can upload products."
        }

        val price = priceText.toDoubleOrNull() ?: return null to "Enter a valid price."
        if (title.isBlank() || description.isBlank()) {
            return null to "Title and description are required."
        }
        if (imageUrl.isBlank()) {
            return null to "Image URL is required."
        }

        val localProducts = dao.getAllProducts()
        val newProd = Product(
            id = "p-${localProducts.size + 1}",
            title = title.trim(),
            description = description.trim(),
            price = price,
            supplierName = session.name,
            supplierId = session.id,
            approved = false,
            createdAtMillis = Clock.System.now().toEpochMilliseconds(),
            colorSeed = colorSeed,
            imageUrl = imageUrl
        )

        if (isFirestoreAvailable) {
            try {
                val writeRes = withTimeoutOrNull(2500L) {
                    firestore.collection("products").document(newProd.id).set(newProd.toEntity())
                    true
                }
                if (writeRes == null) {
                    isFirestoreAvailable = false
                }
            } catch (e: Exception) {
                checkFirestoreException(e)
            }
        }

        dao.insertProduct(newProd.toEntity())
        return newProd to null
    }

    override suspend fun approveProduct(productId: String) {
        if (isFirestoreAvailable) {
            try {
                val writeRes = withTimeoutOrNull(2500L) {
                    firestore.collection("products").document(productId).update("approved" to true)
                    true
                }
                if (writeRes == null) {
                    isFirestoreAvailable = false
                }
            } catch (e: Exception) {
                checkFirestoreException(e)
            }
        }
        dao.updateProductApproval(productId, true)
    }

    override suspend fun rejectProduct(productId: String) {
        if (isFirestoreAvailable) {
            try {
                val writeRes = withTimeoutOrNull(2500L) {
                    firestore.collection("products").document(productId).delete()
                    true
                }
                if (writeRes == null) {
                    isFirestoreAvailable = false
                }
            } catch (e: Exception) {
                checkFirestoreException(e)
            }
        }
        dao.deleteProduct(productId)
    }

    override suspend fun placeBid(
        session: User,
        auctionId: String,
        bidText: String
    ): Pair<Auction?, String?> {
        val localAuctions = dao.getAllAuctions().map { it.toDomain() }
        val auction = localAuctions.firstOrNull { it.id == auctionId }
            ?: return null to "Auction not found."

        if (auction.endAtMillis <= Clock.System.now().toEpochMilliseconds()) {
            return null to "This auction has already ended."
        }

        val amount = bidText.toDoubleOrNull() ?: return null to "Enter a valid bid amount."
        if (amount <= auction.currentBid) {
            return null to "Bid must be higher than the current price."
        }

        val updatedAuction = auction.copy(
            currentBid = amount,
            currentBidderName = session.name,
            bidHistory = listOf(BidEntry(session.name, amount, Clock.System.now().toEpochMilliseconds())) + auction.bidHistory
        )

        if (isFirestoreAvailable) {
            try {
                val writeRes = withTimeoutOrNull(2500L) {
                    firestore.collection("auctions").document(auctionId).set(updatedAuction.toEntity())
                    true
                }
                if (writeRes == null) {
                    isFirestoreAvailable = false
                }
            } catch (e: Exception) {
                checkFirestoreException(e)
            }
        }

        dao.insertAuction(updatedAuction.toEntity())
        return updatedAuction to null
    }
}

fun dev.gitlive.firebase.firestore.DocumentSnapshot.toUserEntity() = UserEntity(
    id = get<String>("id"),
    name = get<String>("name"),
    email = get<String>("email"),
    role = get<String>("role"),
    passwordHash = get<String>("passwordHash")
)

fun dev.gitlive.firebase.firestore.DocumentSnapshot.toProductEntity() = ProductEntity(
    id = get<String>("id"),
    title = get<String>("title"),
    description = get<String>("description"),
    price = get<Double>("price"),
    supplierName = get<String>("supplierName"),
    supplierId = get<String>("supplierId"),
    approved = get<Boolean>("approved"),
    createdAtMillis = get<Long>("createdAtMillis"),
    colorSeed = get<Int>("colorSeed"),
    imageUrl = try { get<String?>("imageUrl") } catch (e: Exception) { null }
)

fun dev.gitlive.firebase.firestore.DocumentSnapshot.toAuctionEntity() = AuctionEntity(
    id = get<String>("id"),
    title = get<String>("title"),
    description = get<String>("description"),
    startingPrice = get<Double>("startingPrice"),
    currentBid = get<Double>("currentBid"),
    currentBidderName = try { get<String?>("currentBidderName") } catch (e: Exception) { null },
    endAtMillis = get<Long>("endAtMillis"),
    supplierName = get<String>("supplierName"),
    approved = get<Boolean>("approved"),
    bidHistory = get<String>("bidHistory"),
    colorSeed = get<Int>("colorSeed"),
    imageUrl = try { get<String?>("imageUrl") } catch (e: Exception) { null }
)
