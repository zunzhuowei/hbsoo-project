package com.hbsoo.utils.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zun.wei on 2022/1/5.
 */
public class DataManager extends UnicastRemoteObject implements IDataManager {

    //protected DataManager() throws RemoteException { }

    private static final Map<String, ConcurrentHashMap<String, Serializable>> cache = new ConcurrentHashMap<>();

    protected DataManager() throws RemoteException {
    }

    /*@Override
    public boolean putData(String dataSpace, String key, Object obj) throws RemoteException {
        return false;
    }

    @Override
    public Object getData(String dataSpace, String key) throws RemoteException {
        return null;
    }*/


    @Override
    public <T extends Serializable> boolean putData(String dataSpace, String key, T obj) {
        ConcurrentHashMap<String, Serializable> map = cache.get(dataSpace);
        if (Objects.isNull(map)) {
            map = new ConcurrentHashMap<>();
            map.put(key, obj);
            cache.put(dataSpace, map);
            return true;
        }
        map.put(key, obj);
        //cache.put(dataSpace, map);
        return true;
    }

    @Override
    public <T extends Serializable> T getData(String dataSpace, String key) {
        ConcurrentHashMap<String, Serializable> map = cache.get(dataSpace);
        if (Objects.isNull(map)) {
            return null;
        }
        final Serializable serializable = map.get(key);
        if (Objects.isNull(serializable)) {
            return null;
        }
        return (T) serializable;
    }


}
