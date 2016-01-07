package io.ucoin.ucoinj.elasticsearch.service.task;

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


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Ludovic Pecquot <ludovic.pecquot@e-is.pro>
 */
public class JobFuture implements ListenableFuture<Job> {

    private final Job job;
    private final ListenableFuture<Job> delegate;

    public JobFuture(Job job, ListenableFuture<Job> delegate) {
        this.job = job;
        this.delegate = delegate;
    }

    public Job getJob() {
        return job;
    }

    public void addCallback(FutureCallback<? super Job> callback) {
        Futures.addCallback(delegate,callback);
    }

    @Override
    public void addListener(Runnable runnable, Executor executor) {
        delegate.addListener(runnable, executor);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.job.stop();
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public Job get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public Job get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }



}
