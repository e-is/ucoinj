package io.ucoin.ucoinj.core.model;

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


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class ProgressionModelImpl implements ProgressionModel, Serializable {

    private static final long serialVersionUID = 1L;

    protected String task;
    protected String message;
    protected int current;
    protected boolean cancel;
    protected int total;
    protected Status status;

    /* set as transient to avoid serialization of all listener */
    protected transient final PropertyChangeSupport propertyChangeSupport;

    public ProgressionModelImpl() {
        super();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.task = "";
        this.message = "";
        this.current = 0;
        this.cancel = false;
        this.total = 100;
        this.status = Status.NOT_STARTED;
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public synchronized String getMessage() {
        return message;
    }

    @Override
    public synchronized void setTask(String task) {
        String oldValue = this.task;
        this.task = task;
        propertyChangeSupport.firePropertyChange(ProgressionModel.PROPERTY_TASK, oldValue, this.task);
        setMessage(task);
    }

    @Override
    public synchronized String getTask() {
        return this.task;
    }

    @Override
    public synchronized void setMessage(String progressionMessage) {
        String oldValue = this.message;
        this.message = progressionMessage;
        propertyChangeSupport.firePropertyChange(ProgressionModel.PROPERTY_MESSAGE, oldValue, this.message);
    }

    @Override
    public synchronized void setTotal(int total) {
        int oldValue = this.total;
        this.total = total;
        propertyChangeSupport.firePropertyChange(ProgressionModel.PROPERTY_TOTAL, oldValue, this.total);
    }

    @Override
    public int getTotal() {
        return this.total;
    }

    @Override
    public synchronized void setCurrent(int current) {
        int oldValue = this.current;
        this.current = current;
        propertyChangeSupport.firePropertyChange(ProgressionModel.PROPERTY_CURRENT, oldValue, this.current);
    }

    @Override
    public synchronized int getCurrent() {
        return current;
    }

    @Override
    public synchronized void increment() {
        setCurrent(current + 1);
    }

    @Override
    public synchronized void increment(int increment) {
        setCurrent(current + increment);
    }

    @Override
    public synchronized void increment(String message) {
        increment();
        setMessage(message);
    }

    public boolean isCancel() {
        return cancel;
    }

    @Override
    public void cancel() {
        if (!cancel) {
            boolean oldValue = this.cancel;
            this.cancel = true;
            propertyChangeSupport.firePropertyChange(ProgressionModel.PROPERTY_CANCEL, oldValue, this.cancel);
        }
    }

    @Override
    public synchronized ProgressionModel.Status getStatus() {
        if (status == null) {
            status = Status.NOT_STARTED;
        }
        return status;
    }

    @Override
    public synchronized void setStatus(Status progressionStatus) {
        Status oldValue = this.status;
        this.status = progressionStatus;
        propertyChangeSupport.firePropertyChange(ProgressionModel.PROPERTY_STATUS, oldValue, this.status);
    }


}
