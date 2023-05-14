package settings


val mongoDBconnectionURL =
        System.getenv("DB_URL") ?: "mongodb://localhost"

@Suppress("SpellCheckingInspection") // because it is just a random value
const val internalHiddenValue =
        "Pq9J7tua26U7cxqrdJHzMC5ZJBoUUovgth65HGMpU0ze8agO2wPyLeUGaCSVnMW1KpqCOgXiE4Q51Y1WO4XYlUNaM7sfw03KGaKT"

