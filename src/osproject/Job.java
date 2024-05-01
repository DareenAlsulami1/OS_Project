/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osproject;

/**
 *
 * @author asil
 */
public class Job {

    int arrivingTime;
    int jobNumber;
    int requestedMemory;
    int requestedDevice;
    int priority;
    int busrtTime;
    int startTime;
    int finishTime;
    int accuredT;
    int turnAT;

    public int getArrivingTime() {
        return arrivingTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public Job(int arrivingTime, int jobNumber) {
        this.arrivingTime = arrivingTime;
        this.jobNumber = jobNumber;
    }

    Job(int arrivingTime, int jobNumber, int requestedMemory, int requestedDevice, int busrtTime, int priority) {
        this.jobNumber = jobNumber;
        this.requestedMemory = requestedMemory;
        this.requestedDevice = requestedDevice;
        this.arrivingTime = arrivingTime;
        this.busrtTime = busrtTime;
        this.priority = priority;

    }

    public int getTurnAT() {
        return turnAT;
    }

    public void setBusrtTime(int busrtTime) {
        this.busrtTime = busrtTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setAccuredT(int accuredT) {
        this.accuredT = accuredT;
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public int getRequestedMemory() {
        return requestedMemory;
    }

    public int getRequestedDevice() {
        return requestedDevice;
    }

    public int getPriority() {
        return priority;
    }

    public int getBusrtTime() {
        return busrtTime;
    }

    public int getAccuredT() {
        return accuredT;
    }

    public void setArrivingTime(int arrivingTime) {
        this.arrivingTime = arrivingTime;
    }

    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    public void setRequestedMemory(int requestedMemory) {
        this.requestedMemory = requestedMemory;
    }

    public void setRequestedDevice(int requestedDevice) {
        this.requestedDevice = requestedDevice;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setTurnAT(int turnAT) {
        this.turnAT = turnAT;
    }

}
