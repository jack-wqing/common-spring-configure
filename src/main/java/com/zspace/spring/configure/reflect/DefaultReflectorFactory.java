
package com.zspace.spring.configure.reflect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultReflectorFactory implements ReflectorFactory {
  
  private boolean classCacheEnabled = true;
  
  private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<Class<?>, Reflector>();
  
  private ReentrantLock lock = new ReentrantLock();
  
  public DefaultReflectorFactory() {
  }

  @Override
  public boolean isClassCacheEnabled() {
    return classCacheEnabled;
  }

  @Override
  public void setClassCacheEnabled(boolean classCacheEnabled) {
    this.classCacheEnabled = classCacheEnabled;
  }

  @Override
  public Reflector findForClass(Class<?> type) {
    if (classCacheEnabled) {
      Reflector cached = reflectorMap.get(type);
      if (cached == null) {
        try {
            lock.lock();
            cached = reflectorMap.get(type);
            if(cached == null) {
                cached = new Reflector(type);
                reflectorMap.put(type, cached);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
      }
      return cached;
    } else {
      return new Reflector(type);
    }
  }

}
