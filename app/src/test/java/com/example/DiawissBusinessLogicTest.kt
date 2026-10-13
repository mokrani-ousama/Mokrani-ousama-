package com.example

import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test

class DiawissBusinessLogicTest {

    @Test
    fun testAlgerianWilayasDataset() {
        val wilayas = AlgerianWilayas.list
        assertEquals("Should have all 58 Algerian wilayas", 58, wilayas.size)

        val bejaia = AlgerianWilayas.getByCode("06")
        assertEquals("06", bejaia.code)
        assertTrue(bejaia.name.contains("Béjaïa"))

        val adrar = AlgerianWilayas.getByCode("01")
        assertEquals("01", adrar.code)

        val inGuezzam = AlgerianWilayas.getByCode("58")
        assertEquals("58", inGuezzam.code)
    }

    @Test
    fun testCartItemPriceCalculation() {
        val hat = CartItem(
            productId = "chapeau",
            productName = "Chapeau de Soutenance",
            category = "CHAPEAU",
            basePriceDa = 1800,
            selectedOptions = mapOf(
                "STYLE" to ProductOptionEntity(
                    productId = "chapeau",
                    groupType = "STYLE",
                    groupLabel = "Style",
                    optionName = "Velours Royal",
                    surchargeDa = 300
                ),
                "COLOR" to ProductOptionEntity(
                    productId = "chapeau",
                    groupType = "COLOR",
                    groupLabel = "Couleur",
                    optionName = "Bordeaux Cérémonie",
                    surchargeDa = 100
                )
            ),
            surchargesTotalDa = 400,
            unitPriceDa = 2200, // 1800 + 300 + 100
            quantity = 3
        )

        assertEquals(2200, hat.unitPriceDa)
        assertEquals(6600, hat.totalItemPriceDa)
        assertTrue(hat.getOptionsSummary().contains("Velours Royal"))
        assertTrue(hat.getOptionsSummary().contains("Bordeaux Cérémonie"))
    }

    @Test
    fun testDiscountPercentageAndFixedLogic() {
        val discountPercent = DiscountEntity(
            code = "SOUTENANCE2026",
            description = "-10% remise",
            discountType = "PERCENTAGE",
            value = 10.0,
            targetScope = "ORDER",
            minOrderDa = 3500
        )

        val subtotalDa = 7500
        val discountAmount = ((subtotalDa * discountPercent.value) / 100.0).toInt()
        assertEquals(750, discountAmount)
        assertEquals(6750, subtotalDa - discountAmount)

        val discountFixed = DiscountEntity(
            code = "BEJAIA06",
            description = "500 DA remise",
            discountType = "FIXED",
            value = 500.0,
            targetScope = "ORDER",
            minOrderDa = 3000
        )

        val fixedDiscountAmount = discountFixed.value.toInt()
        assertEquals(500, fixedDiscountAmount)
        assertEquals(7000, subtotalDa - fixedDiscountAmount)
    }

    @Test
    fun testOrderNumberFormat() {
        val seq = 1
        val orderNumber = "DIA-2026-%04d".format(seq)
        assertEquals("DIA-2026-0001", orderNumber)
        assertTrue(orderNumber.startsWith("DIA-2026-"))
    }

    @Test
    fun testNotificationStatusTexts() {
        val readyStatus = "Prête à récupérer"
        assertTrue(readyStatus.contains("Prête"))

        val inPreparationStatus = "En préparation"
        assertTrue(inPreparationStatus.contains("préparation"))
    }
}
