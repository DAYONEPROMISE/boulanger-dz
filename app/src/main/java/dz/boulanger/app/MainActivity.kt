package dz.boulanger.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, BakeryDb::class.java, "boulangerdz.db").build()
        setContent {
            MaterialTheme {
                var tab by remember { mutableIntStateOf(0) }
                Scaffold(bottomBar = {
                    NavigationBar {
                        NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, label = { Text(getString(R.string.tab_scale)) }, icon = {})
                        NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, label = { Text(getString(R.string.tab_plan)) }, icon = {})
                        NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, label = { Text(getString(R.string.tab_stock)) }, icon = {})
                    }
                }) { pad ->
                    Box(Modifier.padding(pad).fillMaxSize()) {
                        when (tab) {
                            0 -> ScalerScreen()
                            1 -> PlanScreen(db)
                            else -> StockScreen(db)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScalerScreen() {
    var units by remember { mutableStateOf("100") }
    var unitW by remember { mutableStateOf("250") }
    var hyd by remember { mutableStateOf("65") }
    var salt by remember { mutableStateOf("2") }
    var yeast by remember { mutableStateOf("1") }
    var out by remember { mutableStateOf<RecipeScaler.ScaledRecipe?>(null) }
    var err by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("BoulangerDZ", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Start)
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
}

@Composable
fun PlanScreen(db: BakeryDb) {
    var name by remember { mutableStateOf("Khobz") }
    var units by remember { mutableStateOf("100") }
    var unitW by remember { mutableStateOf("250") }
    val day = java.time.LocalDate.now().toEpochDay()
    val plans by db.dao().plansForDay(day).collectAsState(initial = emptyList())
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(name, { name = it }, label = { Text("Product") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(units, { units = it }, label = { Text("Units") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(unitW, { unitW = it }, label = { Text("g / unit") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            val u = units.toIntOrNull() ?: return@Button
            val w = unitW.toDoubleOrNull() ?: return@Button
            val r = RecipeScaler.fromUnits(u, w, 65.0, 2.0, 1.0)
            kotlinx.coroutines.MainScope().launch {
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
    val notes by db.dao().allNotes().collectAsState(initial = emptyList())
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(item, { item = it }, label = { Text("Ingredient (flour / salt / yeast...)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(qty, { qty = it }, label = { Text("Qty (e.g. 2 x 50kg sacks)") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            if (item.isBlank()) return@Button
            kotlinx.coroutines.MainScope().launch { db.dao().insertNote(StockNote(item = item, qty = qty)) }
            item = ""; qty = ""
        }, modifier = Modifier.fillMaxWidth()) { Text("Add note (offline)") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes) { n ->
                Card(Modifier.fillMaxWidth()) { Text("${n.item} — ${n.qty}", modifier = Modifier.padding(12.dp)) }
            }
        }
    }
}
