package com.eju.cy.audiovideo.observer;


import java.util.ArrayList;

/**
 * @ Name: Caochen
 * @ Date: 2019-08-23
 * @ Time: 11:32
 * @ Description： 被观察者的实现
 */
public class ConcreteObservableImStatistics implements EjuHomeImObservable {
    private ArrayList<EjuHomeImObserver> observers;

    @Override
    public void addObserver(EjuHomeImObserver observer) {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        observers.add(observer);
    }

    @Override
    public void removeObserver(EjuHomeImObserver observer) {
        if (observers == null || observers.size() <= 0) {
            return;
        }
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object obj) {
        if (observers == null || observers.size() <= 0) {
            return;
        }
        for (EjuHomeImObserver observer : observers) {
            observer.action(obj);
        }
    }
}
