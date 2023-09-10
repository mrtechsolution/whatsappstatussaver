package com.mrtech.whatsappstatussaver

/**
 * Created by umer on 01-May-18.
 */
class InstanceHandler<T>(var value: T) {
    var type: Class<T>? = null
    fun test(type: Class<T>, obj: Any?): String {
        var type = type
        type = type
        return if (type.isInstance(obj)) {
            type.name
        } else type.name
    }
}