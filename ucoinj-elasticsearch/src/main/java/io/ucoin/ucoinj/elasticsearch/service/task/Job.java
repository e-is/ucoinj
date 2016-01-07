package io.ucoin.ucoinj.elasticsearch.service.task;

/*
 * #%L
 * SIH-Adagio :: UI for Core Allegro
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

import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.core.model.ProgressionModel;
import io.ucoin.ucoinj.core.util.ObjectUtils;
import io.ucoin.ucoinj.elasticsearch.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import static org.nuiton.i18n.I18n.t;

/**
 * Encapsulate a RunnableWithProgress into a Job, with meta data
 *
 *
 */
public class Job implements java.lang.Runnable {

    private static final Logger log = LoggerFactory.getLogger(Job.class);


    protected final ProgressionModel progressionModel;
    protected final Configuration config;
    protected final String jobId;
    protected final String issuer;
    protected final Locale locale;
    protected boolean interrupted;
    protected final Runnable delegate;

    public Job(Runnable delegate, String jobId, String issuer, Locale locale, ProgressionModel progressionModel) {
        ObjectUtils.checkNotNull(delegate);
        ObjectUtils.checkNotNull(jobId);
        ObjectUtils.checkNotNull(locale);
        ObjectUtils.checkNotNull(progressionModel);

        this.delegate = delegate;
        this.jobId = jobId;
        this.issuer = issuer;
        this.locale = locale;
        this.progressionModel = progressionModel;
        this.config = Configuration.instance();
    }

    public ProgressionModel getProgressionModel() {
        return this.progressionModel;
    }

    public String getIssuer() {
        return issuer;
    }

    public Locale getLocale() {
        return locale;
    }

    public void stop() {
        this.interrupted = true;
    }

    @Override
    public void run() {
        delegate.run();
    }

    /**
     * Remap progression model to a new progression model, to be able to encapsulate many tasks
     * 
     * @param progressionModel
     * @param progressionModelOffset
     * @param progressionCount
     */
    protected void addProgressionListeners(final ProgressionModel progressionModel,
            final int progressionModelOffset,
            final int progressionCount) {
        // Listen 'current' attribute changes
        progressionModel.addPropertyChangeListener(ProgressionModel.PROPERTY_CURRENT,
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        Integer current = (Integer) evt.getNewValue();

                        onProgressionCurrentChanged(current, progressionModel.getTotal(), progressionModelOffset, progressionCount);
                    }
                });

        // Listen message changes
        progressionModel.addPropertyChangeListener(ProgressionModel.PROPERTY_MESSAGE,
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        String message = (String) evt.getNewValue();
                        onProgressionMessageChanged(message);
                    }
                });
    }

    protected void onProgressionCurrentChanged(Integer current, Integer total, int progressionOffset, int progressionCount) {
        if (current == null || total == null) {
            progressionModel.setCurrent(progressionOffset);
            return;
        }
        int progression = progressionOffset
                + Math.round(progressionCount * current / total);
        if (progression >= progressionOffset + progressionCount) {
            progression = progressionOffset + progressionCount - 2; // max - 2, to avoid ProgressionPanel to run
            // onComplet()
        }
        progressionModel.setCurrent(progression);

        checkJobInterruption();
    }

    protected void onProgressionMessageChanged(String message) {
        progressionModel.setMessage(message);
    }

    protected void onError(String errorMessage) {
        progressionModel.setCurrent(0);
        progressionModel.setTask("");
        progressionModel.setMessage(errorMessage);
        progressionModel.setStatus(ProgressionModel.Status.FAILED);
    }

    protected void onSuccess() {
        progressionModel.setCurrent(100);
        progressionModel.setTask("");
        progressionModel.setMessage(t("ucoinj.job.success"));
        progressionModel.setStatus(ProgressionModel.Status.SUCCESS);
    }

    /**
     * Will stop the synchronization if job cancellation has been asked
     */
    protected void checkJobInterruption() {
        if (this.interrupted) {
            if (log.isInfoEnabled()) {
                log.info(t("ucoinj.job.stopping"));
            }
            throw new TechnicalException(t("ucoinj.job.stopped"), new InterruptedException());
        }
    }

}
