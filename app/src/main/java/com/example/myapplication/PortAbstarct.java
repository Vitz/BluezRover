package com.example.myapplication;

public abstract class PortAbstarct {
    abstract void initDevice();
    abstract void sendData(String DataString);
    abstract String receiveData();

}
