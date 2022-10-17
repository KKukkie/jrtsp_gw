package org.kkukie.jrtsp_gw.service.scheduler.schedule.unit;

import org.kkukie.jrtsp_gw.service.scheduler.job.Job;
import org.kkukie.jrtsp_gw.service.scheduler.schedule.handler.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleUnit {

    public static final int DEFAULT_THREAD_COUNT = 5;
    ////////////////////////////////////////////////////////////////////////////////
    private static final Logger logger = LoggerFactory.getLogger(ScheduleUnit.class);
    private final long createdTime = System.currentTimeMillis();

    private final String scheduleUnitKey;

    private final int poolSize; // Thread pool size
    private final JobScheduler jobScheduler;
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    public ScheduleUnit(String key, int poolSize, int queueSize) {
        this.scheduleUnitKey = key;

        if (poolSize > 0) {
            this.poolSize = poolSize;
        } else {
            this.poolSize = DEFAULT_THREAD_COUNT;
        }

        jobScheduler = new JobScheduler(scheduleUnitKey, poolSize, queueSize);
    }
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    public boolean start(Job job) {
        if (job == null) {
            return false;
        }
        job.setScheduleUnitKey(scheduleUnitKey);
        return jobScheduler.schedule(job);
    }

    public void stop(Job job) {
        if (job == null) {
            return;
        }
        job.setScheduleUnitKey(null);
        jobScheduler.cancel(job);
    }

    public void stopAll() {
        jobScheduler.stop();
    }
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    public int getJobListSize() {
        return jobScheduler.getScheduledJobCount();
    }

    public JobScheduler getJobScheduler() {
        return jobScheduler;
    }

    public String getScheduleUnitKey() {
        return scheduleUnitKey;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return "ScheduleUnit{" +
                "key='" + scheduleUnitKey + '\'' +
                ", threadCount=" + poolSize +
                '}';
    }
    ////////////////////////////////////////////////////////////////////////////////

}
