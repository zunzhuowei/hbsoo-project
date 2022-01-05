package com.hbsoo.utils.rmi;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by zun.wei on 2021/12/31.
 */
public class MathClient {


    public static void main(String[] args) throws RemoteException, NotBoundException {
        final Registry registry = LocateRegistry.getRegistry("localhost");
        final IRemoteMath remoteMath = (IRemoteMath)registry.lookup("Compute");
        final double add = remoteMath.add(5.0, 3.0);
        System.out.println("add = " + add);
        final double subtract = remoteMath.subtract(5.0, 3.0);
        System.out.println("subtract = " + subtract);

        IDataManager dataManager = (IDataManager) registry.lookup("DataManager");
        //dataManager.putData("aa", "bb", "11");
        final String data = dataManager.getData("aa", "bb");
        System.out.println("data = " + data);
    }

}
