package javaiscoffee.polaroad.login.emailAuthentication;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class CustomTransportWithProxy {
    public static HttpTransport createCustomTransportWithProxy() {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
        NetHttpTransport.Builder builder = new NetHttpTransport.Builder().setProxy(proxy);
        return builder.build();
    }
}
