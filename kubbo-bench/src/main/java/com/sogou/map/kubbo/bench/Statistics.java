package com.sogou.map.kubbo.bench;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Heavily inspired by apache ab
 * @author liufuliang
 */
public class Statistics {
    public static final int VFS = 25;
    private int concurrency;
    private long timeTaken; //ms

    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger badCount = new AtomicInteger(0);
    private AtomicInteger index = new AtomicInteger(0);

    private ExecuteState[] executeStates;
    
    public Statistics(Options options){
        this.concurrency = options.getConcurrency();
        this.executeStates = new ExecuteState[options.getTotal()];
    }


    public int getConcurrency() {
        return concurrency;
    }


    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }


    public long getTimeTaken() {
        return timeTaken;
    }
    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public ExecuteState[] getExecuteStates(){
        return this.executeStates;
    }

    public void complete(long timeTaken) {
        executeStates[index.getAndIncrement()] = new ExecuteState(true, timeTaken);
        successCount.incrementAndGet();
    }
    public void uncomplete(long timeTaken) {
        executeStates[index.getAndIncrement()] = new ExecuteState(false, timeTaken);
        badCount.incrementAndGet();
    }



    /**
     * the total tests
     * 
     */
    public int getRequestedNum() {
        return successCount.intValue() + badCount.intValue();
    }

    public float getTimePerRequest() {
        if (executeStates.length == 0) {
            return 0;
        }
        long sum = 0;
        for (ExecuteState single : executeStates) {
            sum += single.getTimeTaken();
        }
        return (((float) sum) / executeStates.length)/1000000;
    }

    /**
     * something like: 90% of tests done in 3 ms, 95% done in 4 ms
     * 
     */
    public TreeMap<Double, Long> getPercentInCertainTime() {
        TreeMap<Double, Long> map = new TreeMap<Double, Long>();
        if (executeStates.length == 0) {
            return map;
        }
        List<ExecuteState> sortedResults = new ArrayList<ExecuteState>(Arrays.asList(executeStates));
        Collections.sort(sortedResults);
        fillPercentN(map, sortedResults, 0.5);
        fillPercentN(map, sortedResults, 0.6);
        fillPercentN(map, sortedResults, 0.7);
        fillPercentN(map, sortedResults, 0.8);
        fillPercentN(map, sortedResults, 0.9);
        fillPercentN(map, sortedResults, 0.95);
        fillPercentN(map, sortedResults, 0.98);
        fillPercentN(map, sortedResults, 0.99);
        fillPercentN(map, sortedResults, 1);
        return map;
    }

    private void fillPercentN(TreeMap<Double, Long> map, List<ExecuteState> sortedResults, double percent) {
        map.put(percent, getTimeTakenOfNthSingleResult(sortedResults, percent));
    }

    private long getTimeTakenOfNthSingleResult(
            List<ExecuteState> sortedResults, double percent) {
        int length = sortedResults.size();
        int indexOneBased = (int) Math.round(length * percent);
        long timeTaken = sortedResults.get(indexOneBased - 1).getTimeTaken()/1000000;
        return timeTaken;
    }



    /**
     * the throughput. If you are doing http test, this is the QPS
     */
    public float getRequestsPerSecond() {
        if (this.getTimeTaken() == 0) {
            return 0;
        }
        return (float) getRequestedNum() * 1000 / this.getTimeTaken();
    }

    public String report() {
        StringBuffer report = new StringBuffer();
        report.append(
                reportLine("Concurrency Level", this.getConcurrency()))
                .append("\n");
        report.append(
                reportLine("Time taken for tests", this.getTimeTaken()/1000 + " seconds"))
                .append("\n");
        report.append(
                reportLine("Successful requests", this.successCount.get()))
                .append("\n");
        report.append(reportLine("Failed requests", this.badCount.get()))
                .append("\n");
        report.append(
                reportLine("Requests per second", this.getRequestsPerSecond()))
                .append("\n");
        report.append(
                reportLine("Time per request", this.getTimePerRequest() + "ms"))
                .append("\n");

        TreeMap<Double, Long> percentInTime = this.getPercentInCertainTime();
        if (percentInTime.size() > 0){
            report.append("Percentage of the requests served within a certain time (ms)\n");
            for (Map.Entry<Double, Long> entry : percentInTime.entrySet()) {
                Double key = entry.getKey();
                Long value = entry.getValue();
                report.append(reportLine(toPercentage(key), value));
                if(key == 1){
                    report.append(" (longest request)");
                }
                report.append("\n");
            }
        }
        return report.toString();
    }

    private static String toPercentage(double n) {
        return String.format("%.0f", n * 100) + "%";
    }

    private String reportLine(String key, Object value) {
        StringBuffer sb = new StringBuffer();
        String leftColumn = key + ":";
        sb.append(leftColumn);
        for (int i = 1; i <= VFS - leftColumn.length(); i++) {
            sb.append(" ");
        }
        sb.append(value);
        return sb.toString();
    }

}
