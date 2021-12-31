package com.hbsoo.utils.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by zun.wei on 2021/12/31.
 */
public interface IRemoteMath extends Remote {

    // 所有方法必须抛出RemoteException
    public double add(double a, double b) throws RemoteException;
    public double subtract(double a, double b) throws RemoteException;

}
