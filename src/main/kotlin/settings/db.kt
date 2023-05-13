package settings


val jdbcURL = run {
    val isContainer = System.getenv().getOrDefault("HOSTNAME", "") != ""
    if (isContainer)
        "db/db"
    else
        System.getenv("DB_PATH") ?: "db/db"
}

@Suppress("SpellCheckingInspection") // because it is just a random value
const val internalHiddenValue =
    "Pq9J7tua26U7cxqrdJHzMC5ZJBoUUovgth65HGMpU0ze8agO2wPyLeUGaCSVnMW1KpqCOgXiE4Q51Y1WO4XYlUNaM7sfw03KGaKT"

