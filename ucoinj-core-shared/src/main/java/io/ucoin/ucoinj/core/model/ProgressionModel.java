package io.ucoin.ucoinj.core.model;

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
