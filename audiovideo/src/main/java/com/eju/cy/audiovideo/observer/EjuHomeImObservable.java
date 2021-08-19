package com.eju.cy.audiovideo.observer;


/**
* @ Name: Caochen
* @ Date: 2019-08-23
* @ Time: 11:30
* @ Description： 被观察者
*/
public interface EjuHomeImObservable {


    void addObserver(EjuHomeImObserver observer);


    void removeObserver(EjuHomeImObserver observer);


    void notifyObservers(Object obj);
}
