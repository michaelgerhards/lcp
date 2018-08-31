package statics.util;

/**
 * Proxy no Cache. Required for performance measure
 * 
 * @author M.Gerhards
 *
 * @param <K>
 * @param <V>
 */
public class CacheTwoToOneProxy<K, V> extends CacheTwoToOne<K, V> {

	@Override
	public V get(K l1, K l2) {
		return null;
	}

	@Override
	public void put(K l1, K l2, V res) {
		// nothing
	}

	@Override
	public void delete(K l1) {
		// nothing
	}

	@Override
	public boolean containsKey(K l1, K l2) {
		return false;
	}
	
	
	
	
}
