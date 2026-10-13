package com.example.data.model

data class Wilaya(
    val code: String,
    val name: String,
    val defaultShippingFeeDa: Int
)

object AlgerianWilayas {
    val list: List<Wilaya> = listOf(
        Wilaya("01", "01 - Adrar", 1200),
        Wilaya("02", "02 - Chlef", 700),
        Wilaya("03", "03 - Laghouat", 900),
        Wilaya("04", "04 - Oum El Bouaghi", 700),
        Wilaya("05", "05 - Batna", 700),
        Wilaya("06", "06 - Béjaïa (Atelier DIAWISS)", 400),
        Wilaya("07", "07 - Biskra", 850),
        Wilaya("08", "08 - Béchar", 1100),
        Wilaya("09", "09 - Blida", 600),
        Wilaya("10", "10 - Bouira", 500),
        Wilaya("11", "11 - Tamanrasset", 1500),
        Wilaya("12", "12 - Tébessa", 800),
        Wilaya("13", "13 - Tlemcen", 800),
        Wilaya("14", "14 - Tiaret", 800),
        Wilaya("15", "15 - Tizi Ouzou", 500),
        Wilaya("16", "16 - Alger", 600),
        Wilaya("17", "17 - Djelfa", 850),
        Wilaya("18", "18 - Jijel", 500),
        Wilaya("19", "19 - Sétif", 550),
        Wilaya("20", "20 - Saïda", 800),
        Wilaya("21", "21 - Skikda", 700),
        Wilaya("22", "22 - Sidi Bel Abbès", 800),
        Wilaya("23", "23 - Annaba", 700),
        Wilaya("24", "24 - Guelma", 700),
        Wilaya("25", "25 - Constantine", 650),
        Wilaya("26", "26 - Médéa", 650),
        Wilaya("27", "27 - Mostaganem", 750),
        Wilaya("28", "28 - M'Sila", 700),
        Wilaya("29", "29 - Mascara", 750),
        Wilaya("30", "30 - Ouargla", 1000),
        Wilaya("31", "31 - Oran", 750),
        Wilaya("32", "32 - El Bayadh", 950),
        Wilaya("33", "33 - Illizi", 1500),
        Wilaya("34", "34 - Bordj Bou Arreridj", 550),
        Wilaya("35", "35 - Boumerdès", 550),
        Wilaya("36", "36 - El Tarf", 750),
        Wilaya("37", "37 - Tindouf", 1500),
        Wilaya("38", "38 - Tissemsilt", 800),
        Wilaya("39", "39 - El Oued", 950),
        Wilaya("40", "40 - Khenchela", 750),
        Wilaya("41", "41 - Souk Ahras", 750),
        Wilaya("42", "42 - Tipaza", 600),
        Wilaya("43", "43 - Mila", 650),
        Wilaya("44", "44 - Aïn Defla", 650),
        Wilaya("45", "45 - Naâma", 950),
        Wilaya("46", "46 - Aïn Témouchent", 800),
        Wilaya("47", "47 - Ghardaïa", 950),
        Wilaya("48", "48 - Relizane", 750),
        Wilaya("49", "49 - El M'Ghair", 950),
        Wilaya("50", "50 - El Meniaa", 1100),
        Wilaya("51", "51 - Ouled Djellal", 900),
        Wilaya("52", "52 - Bordj Baji Mokhtar", 1600),
        Wilaya("53", "53 - Béni Abbès", 1300),
        Wilaya("54", "54 - Timimoun", 1300),
        Wilaya("55", "55 - Touggourt", 1000),
        Wilaya("56", "56 - Djanet", 1600),
        Wilaya("57", "57 - In Salah", 1400),
        Wilaya("58", "58 - In Guezzam", 1700)
    )

    fun getByCode(code: String): Wilaya {
        return list.firstOrNull { it.code == code } ?: list[5] // default Béjaïa 06
    }
}
