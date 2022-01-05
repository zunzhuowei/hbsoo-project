package com.hbsoo.utils.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by zun.wei on 2021/12/31.
 */
public class RMIServer {


    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        IRemoteMath remoteMath = new RemoteMath();
        LocateRegistry.createRegistry(1099);
        final Registry registry = LocateRegistry.getRegistry();
        registry.bind("Compute", remoteMath);

        IDataManager dataManager = new DataManager();
        registry.bind("DataManager", dataManager);

        System.out.println("Math server ready");

        //如果不想再让改对象被继续调用，使用下面一行代码
        //UnicastRemoteObject.unexportObject(remoteMath, false);
    }

}
