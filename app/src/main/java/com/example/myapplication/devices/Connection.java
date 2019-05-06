package com.example.myapplication.devices;

public interface Connection {
     void initDevice();
     void initConnection(Object device);
     void sendData(byte[] DataBytes);
     String receiveData();
     void cancel();
}
