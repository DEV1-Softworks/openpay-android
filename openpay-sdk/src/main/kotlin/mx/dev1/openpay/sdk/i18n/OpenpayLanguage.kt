package mx.dev1.openpay.sdk.i18n

/**
 * Languages bundled with the SDK.
 */
enum class OpenpayLanguage(val languageTag: String) {
    ENGLISH("en"),
    SPANISH("es"),
    PORTUGUESE("pt"),
    FRENCH("fr");

    companion object {

        /** Resolves a language from a BCP-47 tag like "es" or "es-MX". */
        fun fromLanguageTag(languageTag: String): OpenpayLanguage? {
            val primarySubtag = languageTag.substringBefore('-').lowercase()
            return entries.firstOrNull { language -> language.languageTag == primarySubtag }
        }
    }
}
