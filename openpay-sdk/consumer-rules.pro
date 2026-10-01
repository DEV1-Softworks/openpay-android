# Rules applied automatically to consumers of the openpay-sdk AAR.

# Keep the serializable API models so kotlinx.serialization keeps working after R8.
-keepclassmembers @kotlinx.serialization.Serializable class mx.dev1.openpay.sdk.** {
    *** Companion;
}
-keepclasseswithmembers class mx.dev1.openpay.sdk.** {
    kotlinx.serialization.KSerializer serializer(...);
}
