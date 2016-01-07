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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import io.ucoin.ucoinj.core.model.ProgressionModelImpl;
import io.ucoin.ucoinj.web.components.progressionModel.ProgressionPanel;
import io.ucoin.ucoinj.web.model.WicketProgressionModel;
import io.ucoin.ucoinj.web.pages.BasePage;
import io.ucoin.ucoinj.core.client.config.Configuration;
import io.ucoin.ucoinj.core.client.model.local.Peer;
import io.ucoin.ucoinj.core.model.ProgressionModel;
import io.ucoin.ucoinj.elasticsearch.service.ServiceLocator;
import io.ucoin.ucoinj.elasticsearch.service.task.Job;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

public class ToolsPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private final ProgressionPanel progressionPanel;

    private boolean isIndexingLastBlocksRunning;

    private AbstractAjaxTimerBehavior selfUpdatingTimer;

    private WicketProgressionModel progressionModel;

    public ToolsPage(final PageParameters parameters) {
        super(parameters);
        
        IModel<ToolsPage> model = new CompoundPropertyModel<ToolsPage>(this);

        isIndexingLastBlocksRunning = false;

        // Progression panel
        progressionModel = new WicketProgressionModel(getSession().getId());
        progressionPanel = new ProgressionPanel("progress", progressionModel) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isIndexingLastBlocksRunning);
            }

            @Override
            public void onComplete(AjaxRequestTarget target) {
                ToolsPage.this.onIndexLastBlocksComplete(target);
            }
        };
        progressionPanel.setOutputMarkupId(true);
        progressionPanel.setOutputMarkupPlaceholderTag(true);
        add(progressionPanel);

        Form<ToolsPage> form = new Form<ToolsPage>("form", model);
        form.setOutputMarkupId(true);
        add(form);

        AjaxButton startIndexLastBlocksButton = new AjaxButton("startIndexLastBlocksButton", form) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onAfterSubmit(target, form);
                startIndexLastBlocks(target);
            }
        };
        form.add(startIndexLastBlocksButton);

        // auto refresh
        selfUpdatingTimer = new AbstractAjaxTimerBehavior(Duration.seconds(5)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                //String message = new StringResourceModel("tools.refreshLastUpdateDate.done", ToolsPage.this, new Model<ToolsPage>(ToolsPage.this)).getString();
                stop(target);

                //info(message);
                target.add(ToolsPage.this);
            }
        };
        selfUpdatingTimer.stop(null);
        add(selfUpdatingTimer);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        ProgressionModel progression = progressionModel.getObject();

        if (progression != null) {
            // Refresh fields
            isIndexingLastBlocksRunning = progression != null && (progression.getStatus() == ProgressionModel.Status.WAITING_EXECUTION
                    || progression.getStatus() == ProgressionModel.Status.RUNNING);
            if (isIndexingLastBlocksRunning) {
                getFeedbackPanel().setVisibilityAllowed(false);
            }
        }
        else {
            isIndexingLastBlocksRunning = false;
        }
    }

    /* -- internal methods -- */

    protected IModel<String> getPageTitleModel() {
        return new StringResourceModel("tools.title", this, null);
    }

    protected void startIndexLastBlocks(AjaxRequestTarget target) {
        isIndexingLastBlocksRunning = true;
        final ProgressionModel progressionModel = new ProgressionModelImpl();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Peer peer = checkConfigAndGetPeer(Configuration.instance());
                if (peer != null) {
                    try {
                        ServiceLocator.instance().getBlockIndexerService().indexLastBlocks(peer, progressionModel);
                    }
                    catch(Exception e) {
                    }
                    finally {
                        isIndexingLastBlocksRunning = false;
                    }
                }

            }
        };

        ServiceLocator.instance().getExecutorService().execute(runnable,
                getWebSession().getId(),
                "admin",
                getWebSession().getLocale(),
                progressionModel);

        //progressionPanel.setModel(new Model<>(progressionModel));
        progressionPanel.setDefaultModelObject(progressionModel);
        progressionPanel.restart(target);

        // Mask feedback panel, to avoid multiple message)
        getFeedbackPanel().setVisibilityAllowed(false);

        target.add(ToolsPage.this);
    }

    protected Peer checkConfigAndGetPeer(Configuration config) {
        if (StringUtils.isBlank(config.getNodeHost())) {
            return null;
        }
        if (config.getNodePort() <= 0) {
            return null;
        }

        Peer peer = new Peer(config.getNodeHost(), config.getNodePort());
        return peer;
    }


    protected void onIndexLastBlocksComplete(AjaxRequestTarget target) {
        // Restore the UI
        progressionPanel.setVisibilityAllowed(false);
        getFeedbackPanel().setVisibilityAllowed(true);

        target.add(this);
    }
}