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

package org.openmole.commons.tools.pattern;

import java.util.NoSuchElementException;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.SoftReferenceObjectPool;

public class BufferFactory implements ObjectPool {

    public final static int MAX_BUFF_SIZE = 10 * 1024;
    
    private ObjectPool bufferPool = new SoftReferenceObjectPool(new BasePoolableObjectFactory() {

        @Override
        public Object makeObject() throws Exception {
            return new byte[MAX_BUFF_SIZE];
        }
        
    });

    private static BufferFactory instance = new BufferFactory();

    private BufferFactory() {
    }

    public static BufferFactory GetInstance() {
        return instance;
    }

    @Override
    public void addObject() throws Exception, IllegalStateException,
            UnsupportedOperationException {
        bufferPool.addObject();
    }

    @Override
    public byte[] borrowObject() throws Exception, NoSuchElementException,
            IllegalStateException {
        return (byte[]) bufferPool.borrowObject();
    }

    @Override
    public void clear() throws Exception, UnsupportedOperationException {
        bufferPool.clear();
    }

    @Override
    public void close() throws Exception {
        bufferPool.close();
    }

    @Override
    public int getNumActive() throws UnsupportedOperationException {
        return bufferPool.getNumActive();
    }

    @Override
    public int getNumIdle() throws UnsupportedOperationException {
        return bufferPool.getNumIdle();
    }

    @Override
    public void invalidateObject(Object arg0) throws Exception {
        bufferPool.invalidateObject(arg0);
    }

    @Override
    public void returnObject(Object arg0) throws Exception {
        bufferPool.returnObject(arg0);
    }

    @Override
    public void setFactory(PoolableObjectFactory arg0)
            throws IllegalStateException, UnsupportedOperationException {
        bufferPool.setFactory(arg0);
    }
}
