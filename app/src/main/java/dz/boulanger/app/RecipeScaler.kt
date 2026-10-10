package dz.boulanger.app

/** Pure clean-room baker's-math scaler. All units in grams. */
object RecipeScaler {

    data class ScaledRecipe(
        val flourG: Double,
        val waterG: Double,
        val saltG: Double,
        val yeastG: Double,
        val totalDoughG: Double,
    )

    /**
     * Scale from target dough weight.
     * hydrationPct, saltPct, yeastPct are baker's % relative to flour.
     */
    fun fromTargetDough(
        targetDoughG: Double,
        hydrationPct: Double,
        saltPct: Double,
        yeastPct: Double,
    ): ScaledRecipe {
        require(targetDoughG > 0) { "targetDoughG must be > 0" }
        require(hydrationPct in 40.0..100.0) { "hydration out of range" }
        require(saltPct in 0.0..5.0) { "salt out of range" }
        require(yeastPct in 0.0..5.0) { "yeast out of range" }
        val divisor = 1.0 + hydrationPct / 100.0 + saltPct / 100.0 + yeastPct / 100.0
        val flour = targetDoughG / divisor
        val water = flour * hydrationPct / 100.0
        val salt = flour * saltPct / 100.0
        val yeast = flour * yeastPct / 100.0
        return ScaledRecipe(flour, water, salt, yeast, flour + water + salt + yeast)
    }

    /** Scale from flour weight directly. */
    fun fromFlour(
        flourG: Double,
        hydrationPct: Double,
        saltPct: Double,
        yeastPct: Double,
    ): ScaledRecipe {
        require(flourG > 0) { "flourG must be > 0" }
        val water = flourG * hydrationPct / 100.0
        val salt = flourG * saltPct / 100.0
        val yeast = flourG * yeastPct / 100.0
        return ScaledRecipe(flourG, water, salt, yeast, flourG + water + salt + yeast)
    }

    /** Units (loaves) planner: units * unitWeight = target dough. */
    fun fromUnits(units: Int, unitWeightG: Double, hydrationPct: Double, saltPct: Double, yeastPct: Double): ScaledRecipe {
        require(units > 0 && unitWeightG > 0)
        return fromTargetDough(units * unitWeightG, hydrationPct, saltPct, yeastPct)
    }
}
