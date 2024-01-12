package it.unipi.m598992;

import it.unipi.m598992.jobscheduler.JobScheduler;
import it.unipi.m598992.jobscheduler.instance.CiaoWordCsvOutput;
import it.unipi.m598992.jobscheduler.instance.CiaoWordDirectoryEmitter;

public class Main {

    public static void main(String[] args) {
        // Creating an instance of the job scheduler by injecting specific strategies
        // to meet the requirements
        JobScheduler<String, String> jobScheduler =
                new JobScheduler<>(new CiaoWordDirectoryEmitter(), new CiaoWordCsvOutput());
        // Running the framework
        jobScheduler.runSteps();
    }
}