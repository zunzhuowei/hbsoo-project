package com.hbsoo.utils.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by zun.wei on 2021/12/31.
 */
public class RemoteMath extends UnicastRemoteObject implements IRemoteMath {

    private int numberofComputations;

    protected RemoteMath() throws RemoteException {
        numberofComputations = 0;
    }


    @Override
    public double add(double a, double b) throws RemoteException {
        numberofComputations++;
        System.out.println("Number of computati" + numberofComputations);
        return (a + b);
    }

    @Override
    public double subtract(double a, double b) throws RemoteException {
        numberofComputations++;
        System.out.println("Number of co" + numberofComputations);
        return (a - b);
    }


}
