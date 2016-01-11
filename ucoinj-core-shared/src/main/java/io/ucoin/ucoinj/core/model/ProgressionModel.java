package io.ucoin.ucoinj.core.model;

/*
 * #%L
 * UCoin Java :: Core Shared
 * %%
 * Copyright (C) 2014 - 2016 EIS
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
import java.io.Serializable;

/**
 * Used to follow a task progression
 *
 * Created by eis on 09/02/15.
 */
public interface ProgressionModel extends Serializable {
    enum Status {
        NOT_STARTED,
        WAITING_EXECUTION,
        RUNNING,
        FAILED,
        SUCCESS,
        STOPPED
    }

    String PROPERTY_CURRENT = "current";

    String PROPERTY_MESSAGE = "message";

    String PROPERTY_TASK = "task";

    String PROPERTY_CANCEL = "cancel";

    String PROPERTY_STATUS = "status";

    String PROPERTY_TOTAL = "total";

    void setTask(String task);
    String getTask();

    String getMessage();
    void setMessage(String message);

    int getCurrent();
    void setCurrent(int progression);

    void setTotal(int total);
    int getTotal();

    void increment();
    void increment(int nb);
    void increment(String message);

    boolean isCancel();
    void cancel();

    Status getStatus();
    void setStatus(Status status);

    // For property change listener
    void addPropertyChangeListener(PropertyChangeListener listener);
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);


}
