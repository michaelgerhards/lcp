package statics.util;

import java.util.HashMap;
import java.util.Map;


/**
 * Two partners of K having one result of V.
 *
 * @author M.Gerhards
 *
 * @param <K>
 * @param <V>
 */
public class CacheTwoToOne<K, V> {

    private final Map<K, Map<K, V>> cache = new HashMap<>(10000);

    /**
     * both directions must exist.
     *
     * @param l1
     * @param l2
     * @return
     */
    public V get(K l1, K l2) {
//        if (containsKey(l1, l2)) {
        return cache.get(l1).get(l2); // equals to cache.get(l2).get(l1);
//        } else {
//            throw new RuntimeException("Cache: key do not exist");
//        }
    }

    public boolean containsKey(K l1, K l2) {
        return cache.containsKey(l1) && cache.containsKey(l2)
                && cache.get(l1).containsKey(l2)
                && cache.get(l2).containsKey(l1);
    }

    /**
     * establish both directions
     *
     * @param l1
     * @param l2
     * @param res
     */
    public void put(K l1, K l2, V res) {
        putP(l1, l2, res);
        putP(l2, l1, res);
    }

    private void putP(K l1, K l2, V res) {
        Map<K, V> map;
        if (cache.containsKey(l1)) {
            map = cache.get(l1);
        } else {
            map = new HashMap<>();
            cache.put(l1, map);
        }
        map.put(l2, res);
    }

    /**
     * delete only one direction -> performance. no loop required to search
     * inner keyspace as value of outer keyspace.
     *
     * @param l1
     */
    public void delete(K l1) {
        cache.remove(l1);
    }

}
