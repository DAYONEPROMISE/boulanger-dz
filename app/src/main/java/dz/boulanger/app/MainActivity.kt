package dz.boulanger.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch

private data class Tab(val titleRes: Int, val icon: ImageVector, val desc: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = Room.databaseBuilder(applicationContext, BakeryDb::class.java, "boulangerdz.db").build()
        setContent {
            BoulangerTheme {
                var tab by remember { mutableIntStateOf(0) }
                val tabs = listOf(
                    Tab(R.string.tab_scale, Icons.Filled.List, "Scale"),
                    Tab(R.string.tab_plan, Icons.Filled.DateRange, "Plan"),
                    Tab(R.string.tab_stock, Icons.Filled.ShoppingCart, "Stock")
                )
                // Adaptive: rail + content on tablets/landscape (>=600dp), bottom bar on phones.
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val wide = maxWidth >= 600.dp
                    if (wide) {
                        Row(Modifier.fillMaxSize()) {
                            NavigationRail {
                                tabs.forEachIndexed { i, t ->
                                    NavigationRailItem(
                                        selected = tab == i,
                                        onClick = { tab = i },
                                        icon = { Icon(t.icon, contentDescription = t.desc) },
                                        label = { Text(getString(t.titleRes)) }
                                    )
                                }
                            }
                            Box(Modifier.weight(1f).fillMaxHeight()) {
                                TabContent(tab, db, wide = true)
                            }
                        }
                    } else {
                        Scaffold(bottomBar = {
                            NavigationBar {
                                tabs.forEachIndexed { i, t ->
                                    NavigationBarItem(
                                        selected = tab == i,
                                        onClick = { tab = i },
                                        icon = { Icon(t.icon, contentDescription = t.desc) },
                                        label = { Text(getString(t.titleRes)) }
                                    )
                                }
                            }
                        }) { pad ->
                            Box(Modifier.padding(pad).fillMaxSize()) {
                                TabContent(tab, db, wide = false)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabContent(tab: Int, db: BakeryDb, wide: Boolean) {
    when (tab) {
        0 -> ScalerScreen(wide)
        1 -> PlanScreen(db)
        else -> StockScreen(db)
    }
}

@Composable
fun ScalerScreen(wide: Boolean = false) {
    var units by remember { mutableStateOf("100") }
    var unitW by remember { mutableStateOf("250") }
    var hyd by remember { mutableStateOf("65") }
    var salt by remember { mutableStateOf("2") }
    var yeast by remember { mutableStateOf("1") }
    var out by remember { mutableStateOf<RecipeScaler.ScaledRecipe?>(null) }
    var err by remember { mutableStateOf("") }

    @Composable
    fun Inputs() {
        OutlinedTextField(units, { units = it }, label = { Text("Units (baguettes/khobz)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(unitW, { unitW = it }, label = { Text("Weight per unit (g)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(hyd, { hyd = it }, label = { Text("Hydration % (flour=100%)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(salt, { salt = it }, label = { Text("Salt %") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(yeast, { yeast = it }, label = { Text("Yeast %") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            try {
                out = RecipeScaler.fromUnits(units.toInt(), unitW.toDouble(), hyd.toDouble(), salt.toDouble(), yeast.toDouble())
                err = ""
            } catch (e: Exception) { err = e.message ?: "Invalid input" }
        }, modifier = Modifier.fillMaxWidth()) { Text("Calculate") }
        if (err.isNotEmpty()) Text(err, color = MaterialTheme.colorScheme.error)
    }

    @Composable
    fun Result() {
        out?.let {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Flour: ${"%.0f".format(it.flourG)} g")
                    Text("Water: ${"%.0f".format(it.waterG)} g")
                    Text("Salt: ${"%.1f".format(it.saltG)} g")
                    Text("Yeast: ${"%.1f".format(it.yeastG)} g")
                    Text("Total dough: ${"%.0f".format(it.totalDoughG)} g", style = MaterialTheme.typography.titleMedium)
                    Text("Presets: Khobz ~60-65% hydro / 1.8-2% salt / 1-1.5% yeast. Baguette ~65-70% / 2% / 0.5-1%.")
                }
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("BoulangerDZ", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Start,
            modifier = Modifier.semantics { heading() })
        if (wide && out != null) {
            // Two-pane on tablets/landscape: inputs left, result right.
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) { Inputs() }
                Box(Modifier.weight(1f)) { Result() }
            }
        } else {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Inputs()
                Result()
            }
        }
    }
}

@Composable
fun PlanScreen(db: BakeryDb) {
    var name by remember { mutableStateOf("Khobz") }
    var units by remember { mutableStateOf("100") }
    var unitW by remember { mutableStateOf("250") }
    val scope = rememberCoroutineScope()
    val day = System.currentTimeMillis() / 86_400_000L
    val plans by db.dao().plansForDay(day).collectAsState(initial = emptyList())
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Today's production", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() })
        OutlinedTextField(name, { name = it }, label = { Text("Product") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(units, { units = it }, label = { Text("Units") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(unitW, { unitW = it }, label = { Text("g / unit") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            val u = units.toIntOrNull() ?: return@Button
            val w = unitW.toDoubleOrNull() ?: return@Button
            val r = RecipeScaler.fromUnits(u, w, 65.0, 2.0, 1.0)
            scope.launch {
                db.dao().insertPlan(ProductionPlan(dateEpochDay = day, productName = name, units = u, unitWeightG = w, flourG = r.flourG, waterG = r.waterG, saltG = r.saltG, yeastG = r.yeastG))
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Save today's plan") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(plans) { p ->
                Card(Modifier.fillMaxWidth()) {
                    Text("${p.productName}: ${p.units} x ${p.unitWeightG}g — flour ${"%.0f".format(p.flourG)}g", modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
fun StockScreen(db: BakeryDb) {
    var item by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val notes by db.dao().allNotes().collectAsState(initial = emptyList())
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Ingredient stock notes", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() })
        OutlinedTextField(item, { item = it }, label = { Text("Ingredient (flour / salt / yeast...)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(qty, { qty = it }, label = { Text("Qty (e.g. 2 x 50kg sacks)") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            if (item.isBlank()) return@Button
            scope.launch { db.dao().insertNote(StockNote(item = item, qty = qty)) }
            item = ""; qty = ""
        }, modifier = Modifier.fillMaxWidth()) { Text("Add note (offline)") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes) { n ->
                Card(Modifier.fillMaxWidth()) { Text("${n.item} — ${n.qty}", modifier = Modifier.padding(12.dp)) }
            }
        }
    }
}
