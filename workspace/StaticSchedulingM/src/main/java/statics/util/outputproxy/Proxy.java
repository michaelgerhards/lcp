package statics.util.outputproxy;

import java.util.Collection;

public class Proxy {

    private static CollectionToStringProxy proxy = new CollectionToStringProxy();

    public static Object collectionToString(Collection<?> col) {
        proxy.col = col;
        return proxy;
    }
    
//    public static Object proxy(Object o) {
//        return new Object() {
//            @Override
//            public String toString() {
//                return super.toString(); //To change body of generated methods, choose Tools | Templates.
//            }
//            
//          
//        };
//    }

}
