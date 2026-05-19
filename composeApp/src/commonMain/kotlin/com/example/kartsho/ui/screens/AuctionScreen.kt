package com.example.kartsho.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kartsho.domain.model.Auction
import com.example.kartsho.ui.state.KartshoState
import kotlinx.datetime.Clock
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun AuctionScreen(
    state: KartshoState,
    onPlaceBid: (String, String) -> String?
) {
    var tick by remember { mutableStateOf(Clock.System.now().toEpochMilliseconds()) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(1000)
            tick = Clock.System.now().toEpochMilliseconds()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val approvedAuctions = remember(state.auctions) { state.auctions.filter { it.approved } }
        OverviewStrip(
            title = "Auctions",
            subtitle = "Track live bids and place an updated offer before the timer ends.",
            stats = listOf(
                "Live" to approvedAuctions.count { it.endAtMillis > tick }.toString(),
                "Highest" to (approvedAuctions.maxOfOrNull { it.currentBid }?.toInt() ?: 0).toString()
            )
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(approvedAuctions) { auction ->
                AuctionCard(auction = auction, onPlaceBid = onPlaceBid, currentTime = tick)
            }
        }
    }
}

@Composable
fun AuctionCard(auction: Auction, onPlaceBid: (String, String) -> String?, currentTime: Long) {
    var bidText by remember(auction.id) { mutableStateOf("") }
    var error by remember(auction.id) { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DemoIllustrationBanner(
                seed = auction.colorSeed,
                height = 240.dp,
                badgeText = "⚡ Live Auction",
                title = auction.title,
                imageUrl = auction.imageUrl
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(auction.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = auction.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ApprovalChip(approved = auction.approved)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatPill(label = "Current bid", value = "₹${auction.currentBid.toInt()}")
                StatPill(label = "Ends in", value = remainingText(auction.endAtMillis, currentTime))
            }

            Text(
                text = "Highest bidder: ${auction.currentBidderName ?: "No bids yet"}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("⚡ Minimum increment: ₹100", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = bidText,
                onValueChange = { bidText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your bid") }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(100, 500, 1000).forEach { inc ->
                    androidx.compose.material3.AssistChip(
                        onClick = {
                            val nextVal = auction.currentBid + inc
                            bidText = nextVal.toInt().toString()
                        },
                        label = { Text("+₹$inc") }
                    )
                }
            }

            Button(
                onClick = {
                    error = onPlaceBid(auction.id, bidText)
                    if (error == null) bidText = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Place bid")
            }
            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            if (auction.bidHistory.isNotEmpty()) {
                HorizontalDivider()
                Text("Recent bids", fontWeight = FontWeight.SemiBold)
                auction.bidHistory.take(3).forEach { bid ->
                    Text("${bid.bidderName}  •  ₹${bid.amount.toInt()}")
                }
            }
        }
    }
}
