package io.ucoin.ucoinj.web.components.progressionModel;

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

import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;
import io.ucoin.ucoinj.core.model.ProgressionModel;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;


public class ProgressionPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private final AbstractAjaxTimerBehavior timer;
    private final ProgressBar progressBar;
    private final FeedbackPanel feedback;
    private final Label taskLabel;

    private boolean stopped = false;

    public ProgressionPanel(String id, IModel<ProgressionModel> model) {
        super(id, model);

        // Timer
        timer = new AbstractAjaxTimerBehavior(Duration.ONE_SECOND) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                if (stopped) {
                    ProgressionPanel.this.stop(target);
                    return;
                }
                ProgressionModel progressionModel = getModelObject();
                if (progressionModel != null) {
                    synchronized (progressionModel) {
                        if (progressionModel.getStatus() == ProgressionModel.Status.FAILED ||
                                progressionModel.getStatus() == ProgressionModel.Status.STOPPED) {
                            error(progressionModel.getMessage());
                        } else {
                            info(progressionModel.getMessage());
                        }
                    }
                }
                progressBar.refresh(target);
                target.add(feedback, progressBar, taskLabel);
            }
        };
        add(timer);

        // Job label
        taskLabel = new Label("taskLabel", new PropertyModel<String>(model, "task"));
        taskLabel.setOutputMarkupId(true);
        add(taskLabel);

        // ProgressBar
        this.progressBar = new ProgressBar("progress", new PropertyModel<Integer>(model, "current")) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onComplete(AjaxRequestTarget target)
            {
                timer.stop(target); // wicket6
                getFeedbackMessages().clear();

                ProgressionPanel.this.onComplete(target);

            }
        };
        // progressBar.add(new AjaxSelfUpdatingTimerBehavior(Duration.ONE_SECOND));
        progressBar.setOutputMarkupId(true);
        progressBar.setVisibilityAllowed(true);
        add(progressBar);

        // FeedbackPanel
        feedback = new JQueryFeedbackPanel("feedback", this);
        feedback.setOutputMarkupId(true);
        add(feedback);

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        ProgressionModel progressionModel = getModelObject();
        if (progressionModel != null) {
            synchronized (progressionModel) {
                if (progressionModel.getStatus() == ProgressionModel.Status.FAILED ||
                        progressionModel.getStatus() == ProgressionModel.Status.STOPPED) {
                    error(progressionModel.getMessage());
                }
                else {
                    info(progressionModel.getMessage());
                }
            }
        }
    }

    public void setModelObject(ProgressionModel progressionModel) {
        if (getDefaultModelObject() != progressionModel) {
            setDefaultModelObject(progressionModel);
        }
    }

    public void setModel(IModel<ProgressionModel> model) {
        setDefaultModel(model);
    }

    public void restart(AjaxRequestTarget target) {
        stopped = false;
        if (timer.isStopped()) {
            timer.restart(target);
        }
    }

    public void stop(AjaxRequestTarget target) {
        if (!timer.isStopped()) {
            timer.stop(target);
        }
        stopped = true;
    }

    public void onComplete(AjaxRequestTarget target) {
        // could be override by subclass
    }
    
    public IModel<ProgressionModel> getModel() {
        @SuppressWarnings("unchecked")
        IModel<ProgressionModel> result = (IModel<ProgressionModel>) getDefaultModel();
        return result;
    }
    
    public ProgressionModel getModelObject() {
        return (ProgressionModel)getDefaultModelObject();
    }
    
    /* -- Internal methods -- */


    
}
