package algorithm.comparator.pbws.reference;

import java.util.Comparator;

public interface ReferenceComparator<T,V> extends Comparator<V> {

	
	void setReference(T t);
	T getReference();
	
	
}
