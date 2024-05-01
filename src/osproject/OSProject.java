package osproject;

import java.io.*;
import java.util.*;

public class OSProject {

    static PrintWriter write;
    public static int i = 0; // internal: job in CPU
    public static int e = 0; // external: jobs in queue (known by arrival time)
    public static int currentTime;  // system current time
    public static int avbMemory;  // available main memory
    public static int avbDevices;  // available devices
    public static int startingTime;  //system start time
    public static int mainMemorySize;  //system main memory
    public static int devices;  //system total devices
    public static int jobNum;
    public static int Quantum = 0;
    public static int SR = 0;
    public static int AR = 0;

    static Queue<Job> submitQ = new LinkedList<Job>();
    static Queue<Job> readyQ = new LinkedList<Job>();
    static Queue<Job> HoldQueue2 = new LinkedList<Job>();
    public static PriorityQueue<Job> HoldQueue1 = new PriorityQueue<Job>(new Comparator<Job>() {
        @Override
        public int compare(Job o1, Job o2) {
            if (o1.getRequestedMemory() == o2.getRequestedMemory() && o1.getPriority() == o2.getPriority()) {
                if (o1.getArrivingTime() < o2.getArrivingTime()) {
                    return -1;//o1 should come before o2
                } else if (o1.getArrivingTime() > o2.getArrivingTime()) {
                    return 1;//o1 should come after o2
                }
                return 0;//they are equal

            } else if (o1.getRequestedMemory() < o2.getRequestedMemory()) {
                return -1;//o1 should come before o2
            } else {
                return 1;//o1 should come after o2
            }
        }

    });

    static Queue<Job> completeQ = new LinkedList<Job>();

    public static Job JobInCPU = null; // jobs in cpu 

    public static void main(String[] args) throws FileNotFoundException {
        File infile = new File("input.txt");
        Scanner input = new Scanner(infile);
        System.out.println("test1");
        write = new PrintWriter("output.txt");

        String line;
        String[] command;

        //read C 
        line = input.nextLine().replaceAll("[a-zA-Z]=", "");
        // separate the info in an array
        command = line.split(" ");

        // by reading each line and set as variables in an array of commands, start with (C) >>  system config 
        startingTime = Integer.parseInt(command[1]);
        mainMemorySize = Integer.parseInt(command[2]);
        devices = Integer.parseInt(command[3]);

        currentTime = startingTime; // initilaiz current time 
        avbMemory = mainMemorySize;
        avbDevices = devices;
        System.out.println(startingTime);/////////
        while (input.hasNextLine()) {
            // read line by line from the input file
            line = input.nextLine().replaceAll("[a-zA-Z]=", "");
            // separate the info in an array
            command = line.split(" ");
            // by reading each line and set as variables in an array of commands, after (C)

            // read (A) jobs
            if (command[0].equals("A")) {
                int arrivingTime = Integer.parseInt(command[1]);
                int jobNo = Integer.parseInt(command[2]);
                int requestedMM = Integer.parseInt(command[3]);
                int requestedDevices = Integer.parseInt(command[4]);
                int burstTime = Integer.parseInt(command[5]);
                int JobPriority = Integer.parseInt(command[6]);

                // create process for all valid jobs then add them to all_jobs queue
                if (requestedMM <= mainMemorySize && requestedDevices <= devices) {
                    submitQ.add(new Job(arrivingTime, jobNo, requestedMM, requestedDevices, burstTime, JobPriority));
                    // jobArr(submitQ.poll());
                    //  jobNum++; //to count the number of job entered to the queue
                }
                // read (D) job
            } else if (command[0].equals("D")) {
                int time = Integer.parseInt(command[1]);
                submitQ.add(new Job(time, -1));

            }

        }

        // enter the first job to the cpu
        JobInCPU = submitQ.poll();
        currentTime = JobInCPU.arrivingTime;
        avbMemory -= JobInCPU.requestedMemory;
        avbDevices -= JobInCPU.requestedDevice;
        Quantum = JobInCPU.getBusrtTime();

        Get_IN_CPU(JobInCPU, currentTime);

        System.out.println("test2");

        while (!submitQ.isEmpty()) {

            if (submitQ.peek() != null) {
                i = submitQ.peek().getArrivingTime();
            } else {
                i = 999999;
            }
            //-----------------------------------------------------------------
            if (JobInCPU != null) {
                e = JobInCPU.getFinishTime();
            } else {
                e = 999999;
            }
            currentTime = Math.min(i, e);

            if (i == e) {

                InternalEvent(currentTime);
                ExternalEvent(currentTime);

            } else if (e < i) {
                InternalEvent(currentTime);
            } else {
                ExternalEvent(currentTime);
            }

        }
        // call RR 
        // DynamicRR(readyQ.poll());
//        input.close();
//        write.close();
    }

    public static void Get_IN_CPU(Job job, int CurrentTime) {
        job.setStartTime(CurrentTime);

        if (job.getBusrtTime() <= Quantum) {
            job.setFinishTime(CurrentTime + job.getBusrtTime());
        } else {
            job.setFinishTime(CurrentTime + Quantum);
        }

        // update acuuredT
        job.setAccuredT(job.getFinishTime() - job.getStartTime());

    }

    private static void InternalEvent(int currentTime) {

        Get_OUT_CPU();//release
        Get_OUT_Hold();

        if (!readyQ.isEmpty()) {
            Get_IN_CPU(readyQ.poll(), currentTime);

            // updating SA and AR, Quantum
            for (Job job : readyQ) {
                SR += job.getBusrtTime();

            }
            if (readyQ.size() > 0) {
                AR = SR / readyQ.size();
                Quantum = AR;
            } else {
                Quantum = AR;
            }
        }
    }

    private static void ExternalEvent(int currentTime) {

        if (!submitQ.isEmpty()) {

            if (submitQ.peek().getJobNumber() == -1) {
                Display(currentTime, write);

            } else {
                if (submitQ.peek().requestedMemory <= avbMemory && submitQ.peek().requestedDevice <= avbDevices) {
                    readyQ.add(submitQ.poll());
                } else if (submitQ.peek().priority == 1) {
                    HoldQueue1.add(submitQ.poll());
                } else {
                    HoldQueue2.add(submitQ.poll());

                }
            }
        }
    }

    private static void Get_OUT_CPU() {

        if (JobInCPU != null) {

            // update B.T
            JobInCPU.setBusrtTime(JobInCPU.getBusrtTime() - JobInCPU.getAccuredT());
            JobInCPU.setTurnAT(JobInCPU.getFinishTime() - JobInCPU.getArrivingTime());

            // if Job is complete , decrese requested MM size 
            // Then add it into complete queue
            if (JobInCPU.getBusrtTime() == 0) {
                avbMemory += JobInCPU.getRequestedMemory(); //*
                avbDevices += JobInCPU.getRequestedDevice(); //*               
                completeQ.add(JobInCPU);

                // JobInCPU = null; 
            } // the process not finish it yet , move it into ready queue
            else {
                readyQ.add(JobInCPU);
            }

        }
    }

    private static void Get_OUT_Hold() {

        // if the Hold queue 1 not empty 
        while (!HoldQueue1.isEmpty()) {

            if (HoldQueue1.peek().getRequestedMemory() <= avbMemory && HoldQueue1.peek().getRequestedDevice() <= avbDevices) {

                avbMemory -= HoldQueue1.peek().getRequestedMemory();
                avbDevices -= HoldQueue1.peek().getRequestedDevice();
                readyQ.add(HoldQueue1.poll());

            } // end while 

            // check if HoldQueue2 queue is not empty and the requested MM size less than or equal unused memory 
            while (!HoldQueue2.isEmpty()) {

                if (HoldQueue2.peek().getRequestedMemory() <= avbMemory && HoldQueue2.peek().getRequestedDevice() <= avbDevices) {

                    avbMemory -= HoldQueue2.peek().getRequestedMemory();
                    avbDevices -= HoldQueue2.peek().getRequestedDevice();
                    readyQ.add(HoldQueue2.poll());

                } // end while 

            }
        }
    }

    private static void Display(int currentTime, PrintWriter write) {
        System.out.println("System Configuration:\n----------------------------------");
        System.out.println("Memory Size: " + avbMemory + "  No of Devices: " + avbDevices);
        System.out.println("------------------------------------------------------------\n");
        System.out.println("|Process   |Status           |Burst Time     |Arrival Time         |Completion Time |Turnaround Time |");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        for (Job job : completeQ) {

            System.out.println(job.getJobNumber() + " completed at " + currentTime + "   " + job.getArrivingTime() + "  " + job.getFinishTime() + "  " + job.getTurnAT());
        }

        for (Job job : readyQ) {
            System.out.println(job.getJobNumber() + " Ready Queue " + job.getArrivingTime() + "  " + job.getFinishTime() + "  " + job.getTurnAT());
        }

        for (Job job : HoldQueue1) {
            System.out.println(job.getJobNumber() + " Hold Queue 1 " + job.getArrivingTime() + "  " + job.getFinishTime() + "  " + job.getTurnAT());
        }

        for (Job job : HoldQueue2) {
            System.out.println(job.getJobNumber() + " Hold Queue 2 " + job.getArrivingTime() + "  " + job.getFinishTime() + "  " + job.getTurnAT());
        }

        /////////////////////////////
        System.out.print("Content of Submit Queue: ");
        for (Job job : submitQ) {
            System.out.print(job.getJobNumber() + ", ");
        }
        System.out.println("");

        System.out.print("Content of Submit Ready Queue: ");
        for (Job job : readyQ) {
            System.out.print(job.getJobNumber() + ", ");
        }
        System.out.println("");

        System.out.print("Content of Submit Hold Queue1: ");
        for (Job job : HoldQueue1) {
            System.out.print(job.getJobNumber() + ", ");
        }
        System.out.println("");

        System.out.print("Content of Submit Hold Queue2: ");
        for (Job job : HoldQueue2) {
            System.out.print(job.getJobNumber() + ", ");
        }
        System.out.println("");
        ////////////////////////////////////////////////
        if (submitQ.poll().getArrivingTime() == 999999) {
            //print the TAT for the system>>
            int system_TAT = 0;

            for (Job job : readyQ) {
                system_TAT += job.getTurnAT();
            }

            System.out.println("System Turnaround Time: " + system_TAT);
        }

    }
}
