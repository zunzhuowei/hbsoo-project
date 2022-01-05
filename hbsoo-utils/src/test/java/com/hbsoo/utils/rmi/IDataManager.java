package com.hbsoo.utils.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.function.Supplier;

/**
 * Created by zun.wei on 2022/1/5.
 */
public interface IDataManager extends Remote {

    // 所有方法必须抛出RemoteException
    <T extends Serializable> boolean putData(String dataSpace, String key, T obj) throws RemoteException;

    <T extends Serializable> T getData(String dataSpace, String key) throws RemoteException;

    //boolean putData(String dataSpace, String key, Object obj) throws RemoteException;

    //Object getData(String dataSpace, String key) throws RemoteException;

}
