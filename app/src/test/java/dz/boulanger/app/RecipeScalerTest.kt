package dz.boulanger.app

import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeScalerTest {

    @Test fun `flour=1000 at 65-2-1 gives expected weights`() {
        val r = RecipeScaler.fromFlour(1000.0, 65.0, 2.0, 1.0)
        assertEquals(650.0, r.waterG, 0.001)
        assertEquals(20.0, r.saltG, 0.001)
        assertEquals(10.0, r.yeastG, 0.001)
        assertEquals(1680.0, r.totalDoughG, 0.001)
    }

    @Test fun `target dough round-trips`() {
        val r = RecipeScaler.fromTargetDough(1680.0, 65.0, 2.0, 1.0)
        assertEquals(1000.0, r.flourG, 0.5)
        assertEquals(1680.0, r.totalDoughG, 0.5)
    }

    @Test fun `100 units of 250g khobz`() {
        val r = RecipeScaler.fromUnits(100, 250.0, 60.0, 2.0, 1.2)
        assertEquals(25000.0, r.totalDoughG, 1.0)
        // flour = 25000 / 1.632 = ~15319
        assertEquals(15319.0, r.flourG, 5.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero target rejected`() {
        RecipeScaler.fromTargetDough(0.0, 65.0, 2.0, 1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `hydration out of range rejected`() {
        RecipeScaler.fromTargetDough(1000.0, 150.0, 2.0, 1.0)
    }

    @Test fun `baguette preset sanity`() {
        val r = RecipeScaler.fromFlour(1000.0, 68.0, 2.0, 0.7)
        assertEquals(680.0, r.waterG, 0.001)
        assertEquals(7.0, r.yeastG, 0.001)
    }
}
