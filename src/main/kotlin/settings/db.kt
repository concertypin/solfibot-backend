package settings


val jdbcURL = run {
    val isContainer = System.getenv().getOrDefault("HOSTNAME", "") != ""
    if (isContainer)
        "db/db"
    else
        System.getenv("DB_PATH") ?: "db/db"
}
