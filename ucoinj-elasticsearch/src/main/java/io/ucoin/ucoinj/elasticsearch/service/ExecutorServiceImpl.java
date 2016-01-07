package io.ucoin.ucoinj.elasticsearch.service;

/*
 * #%L
 * SIH-Adagio :: Synchro Server WebApp
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2014 Ifremer
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.*;
import io.ucoin.ucoinj.core.beans.InitializingBean;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.model.ProgressionModel;
import io.ucoin.ucoinj.core.model.ProgressionModelImpl;
import io.ucoin.ucoinj.elasticsearch.config.Configuration;
import io.ucoin.ucoinj.elasticsearch.service.task.Job;
import io.ucoin.ucoinj.elasticsearch.service.task.JobFuture;
import io.ucoin.ucoinj.elasticsearch.service.task.JobVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuiton.i18n.I18n;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.nuiton.i18n.I18n.l;

public class ExecutorServiceImpl implements ExecutorService, InitializingBean {

    /** Logger. */
    private static final Log log = LogFactory.getLog(ExecutorServiceImpl.class);


    private Configuration config;
    private ListeningExecutorService delegate;
    private final Map<String, JobFuture> jobsById;
    private final LoadingCache<String, ProgressionModel> progressionByJobIdCache;

    public ExecutorServiceImpl() {
        this.jobsById = Maps.newHashMap();
        this.config = Configuration.instance();
        this.progressionByJobIdCache = initJobByIdCache(
                config.getTaskExecutorQueueCapacity() * 2,
                config.getTaskExecutorTimeToIdle());
        delegate = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(config.getTaskExecutorQueueCapacity()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void close() throws IOException {

        log.debug("Closing executor service");

        synchronized (jobsById) {

            for (ListenableFuture<Job> task : jobsById.values()) {
                task.cancel(true);
            }
            jobsById.clear();

            for (ProgressionModel progressionModel : progressionByJobIdCache.asMap().values()) {
                progressionModel.cancel();
            }
            progressionByJobIdCache.cleanUp();
        }

        if (!delegate.isShutdown()) {
            delegate.shutdownNow();
        }
    }

    @Override
    public ProgressionModel getProgressionByJobId(String jobId) {
        if (StringUtils.isBlank(jobId)) {
            return null;
        }
        synchronized (jobsById) {
            ListenableFuture<Job> job = jobsById.get(jobId);
            if (job != null) {
                try {
                    return job.get().getProgressionModel();
                }
                catch(InterruptedException | ExecutionException e) {
                    // continue
                }
            }
        }

        // Call the cache, but return null if the key is not present
        return progressionByJobIdCache.getIfPresent(jobId);
    }

    @Override
    public void execute(Runnable runnable) {
        execute(runnable, "ucoinj|job|" + System.currentTimeMillis(), null, Locale.getDefault(), new ProgressionModelImpl());
    }

    @Override
    public Job execute(Runnable runnable, String jobId, String issuer, Locale locale, ProgressionModel progression) {

        // Make sure no import already launched
        ProgressionModel existingProgression = getProgressionByJobId(jobId);
        if (existingProgression != null && existingProgression.getStatus() == ProgressionModel.Status.RUNNING) {
            throw new TechnicalException("Could not start a new synchronization: already running.");
        }

        // Run the synchro thread
        final Job job = new Job(
                runnable,
                jobId,
                issuer,
                locale,
                progression);

        // Execute the job
        shedule(jobId,
                job,
                l(locale, "ucoinj.task.starting"),
                locale);

        return job;
    }

    @Override
    public void stop(String jobId) {
        JobFuture job = jobsById.get(jobId);
        if (job != null) {
            job.cancel(false);
        }
    }

    @Override
    public List<JobVO> getAllJobs() {
        synchronized (jobsById) {
            List<JobVO> result = Lists.newArrayListWithExpectedSize(jobsById.size());
            for (Map.Entry<String, JobFuture> entry : jobsById.entrySet()) {
                String jobId = entry.getKey();
                Job job = entry.getValue().getJob();

                // System job
                String issuer = job.getIssuer();
                if (StringUtils.isBlank(issuer)) {
                    issuer = I18n.t("ucoinj.task.issuer.system");
                }

                JobVO jobVO = new JobVO(
                        jobId,
                        issuer
                );

                result.add(jobVO);
            }
            return result;
        }
    }

    public Job getJobById(String jobId) {
        synchronized (jobsById) {
            JobFuture jobFuture = jobsById.get(jobId);
            if (jobFuture != null) {
                return jobFuture.getJob();
            }
            return null;
        }
    }

    /* -- Internal methods -- */
    
    protected final LoadingCache<String, ProgressionModel> initJobByIdCache(
            final int maximumSize, 
            final int expireDurationInSecond) {
        return CacheBuilder.newBuilder()
            .maximumSize(maximumSize)
            .expireAfterWrite(expireDurationInSecond, TimeUnit.SECONDS)
            .build(
                new CacheLoader<String, ProgressionModel>() {
                    @Override
                    public ProgressionModel load(String jobId) throws SQLException {
                        Job job = getJobById(jobId);
                        if (job == null) {
                            throw new TechnicalException("Unknown task id");
                        }
                        return job.getProgressionModel();
                    }
                });
    }

    protected void shedule(final String jobId,
                           final Job job,
                           String taskMessage,
                           final Locale locale) {

        // Set progression as as 'waiting execution'
        final ProgressionModel progressionModel = job.getProgressionModel();
        progressionModel.setTask(taskMessage);
        progressionModel.setMessage(l(locale, "ucoinj.executor.task.waitingExecution"));
        progressionModel.setStatus(ProgressionModel.Status.WAITING_EXECUTION);


        ListenableFuture<Job> future = delegate.submit(new Callable<Job>(){
            @Override
            public Job call() throws Exception {
                job.run();
                return job;
            }
        });

        JobFuture jobFuture = new JobFuture(job, future);
        Futures.addCallback(jobFuture, new FutureCallback<Job>() {
            @Override
            public void onSuccess(Job result) {
                onJobFinish(jobId, progressionModel);
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO EIS : remove this (and log into the progress message instead ?)
                log.error(t);
                onJobFinish(jobId, progressionModel);
            }
        });
        
        // start job
        onJobScheduled(jobId, job, jobFuture);
    }
    
    protected void onJobScheduled(final String jobId, final Job job, final  JobFuture jobFuture) {
        synchronized (jobsById) {
            jobsById.put(jobId, jobFuture);
        }
    }
    
    protected void onJobFinish(final String jobId, final ProgressionModel progressionModel) {
        synchronized (jobsById) {
            // Before to remove from jobsById map, put the progressionModel again into the cache
            // To avoid a getStatus() does not get retrieve it (mantis #23391)
            progressionByJobIdCache.put(jobId, progressionModel);
            
            // ok, progressionModel is in the cache, so remove from map
            jobsById.remove(jobId);
        }
    }
}
