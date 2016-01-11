package io.ucoin.ucoinj.web.pages.admin;

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


import io.ucoin.ucoinj.web.components.progressionModel.ProgressionPanel;
import io.ucoin.ucoinj.web.model.WicketProgressionModel;
import io.ucoin.ucoinj.web.pages.BasePage;
import io.ucoin.ucoinj.elasticsearch.service.ServiceLocator;
import io.ucoin.ucoinj.elasticsearch.service.task.JobVO;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

import java.util.List;

public class JobManagerPage extends BasePage {

    private static final long serialVersionUID = 1L;
    
    public JobManagerPage(PageParameters pageParameters) {
        super(pageParameters);
        
        // Create models (list of progressionModel)
        LoadableDetachableModel<List<JobVO>> jobListModel = new LoadableDetachableModel<List<JobVO>>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<JobVO> load() {
                return ServiceLocator.instance().getExecutorService().getAllJobs();
            }
        };

        add(new Label("jobCount", new PropertyModel<Integer>(jobListModel, "size")));

        // List of import jobs
        ListView<JobVO> jobListView = new ListView<JobVO>("jobList", jobListModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<JobVO> item) {
                // User infos
                item.add(new Label("issuer", new PropertyModel<Integer>(item.getModel(), "issuer")));

                // Progress bar
                String jobId = item.getModelObject().getId();
                ProgressionPanel progressionPanel = new ProgressionPanel("progress", new WicketProgressionModel(jobId)) {
                    private static final long serialVersionUID = 1L;
                    
                    @Override
                    public void onComplete(AjaxRequestTarget target) {
                        stop(target);
                    }
                };
                progressionPanel.setOutputMarkupId(true);
                progressionPanel.setOutputMarkupPlaceholderTag(true);
                item.add(progressionPanel);  
            }
        };
        jobListView.setOutputMarkupId(true);
        add(jobListView);

        add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
    }
    
    /* -- internal methods -- */

    protected IModel<String> getPageTitleModel() {
        return new StringResourceModel("jobmanager.title", this, null);
    }

}
