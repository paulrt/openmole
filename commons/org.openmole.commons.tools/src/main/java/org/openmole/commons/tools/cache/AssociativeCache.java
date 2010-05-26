/*
 *  Copyright (C) 2010 reuillon
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.commons.tools.cache;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.collections15.map.ReferenceMap;
import org.openmole.commons.exception.InternalProcessingError;
import org.openmole.commons.tools.service.LockRepository;

public class AssociativeCache<K, T> {

    public static final int WEAK = ReferenceMap.WEAK;
    public static final int SOFT = ReferenceMap.SOFT;
    public static final int HARD = ReferenceMap.HARD;


    final Map<Object, Map<K, T>> hashCache = new WeakHashMap<Object, Map<K, T>>();
    final LockRepository<K> lockRepository = new LockRepository<K>();
    final int keyRefType;
    final int valRefType;

    public AssociativeCache(int keyRefType, int valRefType) {
        this.keyRefType = keyRefType;
        this.valRefType = valRefType;
    }


    public T getCache(final Object cacheAssociation, K key, ICachable<? extends T> cachable) throws InternalProcessingError, InterruptedException {

        final Map<K, T> cache = getHashCache(cacheAssociation);

        T ret = cache.get(key);

        if (ret == null) {
            lockRepository.lock(key);
            try {
                ret = cachable.compute();
            } finally {
                lockRepository.unlock(key);
            }
        }

        synchronized(cache) {
            if(!cache.containsKey(key)) {
                cache.put(key, ret);
            }
        }

        return ret;
    }

    private Map<K, T> getHashCache(Object cacheAssociation) {
        Map<K, T> ret;
        synchronized (hashCache) {
            ret = hashCache.get(cacheAssociation);
            if (ret == null) {
                ret = Collections.synchronizedMap(new ReferenceMap(keyRefType, valRefType));
                hashCache.put(cacheAssociation, ret);
            }
        }

        return ret;
    }
}
