package com.perseverance.phando.retrofit


import okhttp3.OkHttpClient
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

 fun setSslClent(okHttpClient: OkHttpClient.Builder) {
    val x509TrustManager = object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }
    val trustAllCerts = arrayOf<TrustManager>(x509TrustManager)

    try {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)

        val sslContext = SSLContext.getInstance("TLS")

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, "keystore_pass".toCharArray())
        sslContext.init(null, trustAllCerts, SecureRandom())

        okHttpClient.sslSocketFactory(sslContext.socketFactory, x509TrustManager)
                .hostnameVerifier(HostnameVerifier { hostname, session -> true })

    } catch (e: KeyStoreException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: CertificateException) {
        e.printStackTrace()
    } catch (e: KeyManagementException) {
        e.printStackTrace()
    } catch (e: UnrecoverableKeyException) {
        e.printStackTrace()
    }

}