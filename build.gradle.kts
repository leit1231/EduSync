import java.net.Inet4Address
import java.net.NetworkInterface

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.crashlytics) apply false
    alias(libs.plugins.google.services) apply false
}

tasks.register("get_ipv4") {
    doFirst {
        val ipv4 = mutableListOf<String>()
        NetworkInterface.getNetworkInterfaces().toList().forEach { it ->
            if (!it.isLoopback && it.isUp && !it.isVirtual)
                it.inetAddresses.toList().forEach {
                    if (!it.isLoopbackAddress && it is Inet4Address)
                        ipv4 += it.hostAddress
                }
        }
        println("IPV4:")
        ipv4.forEach { println(it) }
    }
}
