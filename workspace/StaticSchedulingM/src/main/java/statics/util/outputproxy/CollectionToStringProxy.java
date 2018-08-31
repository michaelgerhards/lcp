package statics.util.outputproxy;

import java.util.Collection;

class CollectionToStringProxy {

    Collection<?> col;

    @Override
    public String toString() {
        return String.valueOf(col);
    }

}
