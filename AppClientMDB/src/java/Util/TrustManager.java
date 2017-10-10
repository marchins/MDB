package Util;

import java.security.GeneralSecurityException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class TrustManager {

    public static void initTrustManager() {

        javax.net.ssl.TrustManager[] tm;
        tm = new javax.net.ssl.TrustManager[]{
            new X509TrustManager() {

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, tm, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
        }
    }

}
