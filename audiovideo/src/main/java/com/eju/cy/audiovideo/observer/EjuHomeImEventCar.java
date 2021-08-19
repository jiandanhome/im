package com.eju.cy.audiovideo.observer;

/**
* @ Name: Caochen
* @ Date: 2019-08-23
* @ Time: 13:45
* @ Description： 单例工具类
*/
public class EjuHomeImEventCar {

    private static EjuHomeImEventCar instance;
    private ConcreteObservableImStatistics observableA;

    public static EjuHomeImEventCar getDefault() {
        if (instance == null) {
            synchronized (EjuHomeImEventCar.class) {
                if (instance == null) {
                    instance = new EjuHomeImEventCar();
                }
            }
        }
        return instance;
    }

    private EjuHomeImEventCar() {
        observableA = new ConcreteObservableImStatistics();
    }


    public void register(EjuHomeImObserver observer) {
        observableA.addObserver(observer);
    }


    public void unregister(EjuHomeImObserver observer) {
        observableA.removeObserver(observer);
    }


    public void post(Object obj) {
        observableA.notifyObservers(obj);
    }
}
