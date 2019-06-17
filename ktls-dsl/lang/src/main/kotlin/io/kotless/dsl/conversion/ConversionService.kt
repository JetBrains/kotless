package io.kotless.dsl.conversion

import java.lang.reflect.Type
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

//Taken from KTor

/**
 * Data conversion service that does serialization and deserialization to/from request parameters
 */
abstract class ConversionService {
    /**
     * Convert [value] to an instance of [type]
     *
     * @throws ConversionException in case service does not support conversion of this type
     */
    protected abstract fun convertFrom(value: String, type: Type): Any


    /**
     * Convert [value] to a String representation
     *
     * @throws ConversionException in case service does not support conversion of this type
     */
    protected abstract fun convertTo(value: Any): String

    companion object {
        private val registered: HashSet<ConversionService> = hashSetOf(DefaultConversionService)

        fun register(service: ConversionService) {
            registered.add(service)
        }

        fun <T : Any> convertTo(value: T): String {
            val result: String
            for (conversion in registered) {
                result = try {
                    conversion.convertTo(value)
                } catch (e: Exception) {
                    continue
                }
                return result
            }
            throw ConversionException("No viable conversion to string found for $value and type ${value.javaClass}")
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> convertFrom(value: String, type: KType): T? {
            val result: T?
            for (conversion in registered) {
                result = try {
                    conversion.convertFrom(value, type.javaType) as T?
                } catch (e: Exception) {
                    continue
                }
                return result
            }
            throw ConversionException("No viable conversion from string found for $value and type $type")
        }
    }
}

