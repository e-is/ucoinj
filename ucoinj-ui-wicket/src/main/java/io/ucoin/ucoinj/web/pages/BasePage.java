package io.ucoin.ucoinj.web.pages;

/*
 * #%L
 * SIH-Adagio Extractor web UI
 * %%
 * Copyright (C) 2012 - 2013 Ifremer
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
import io.ucoin.ucoinj.web.application.Application;
import io.ucoin.ucoinj.web.application.WebSession;
import io.ucoin.ucoinj.web.config.WebConfiguration;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class BasePage extends WebPage {

	private static final long serialVersionUID = 2589483412605551035L;

	private FeedbackPanel feedback = null;

    public BasePage(final PageParameters parameters) {
        WebSession session = (WebSession)getSession();

        // page title
        add(new Label("pageTitle", new StringResourceModel("base.pageTitle", this, null)));

        // contentLanguage
        WebMarkupContainer contentLanguage = new WebMarkupContainer("contentLanguage");
        contentLanguage.add(new AttributeModifier("content", session.getLocale().toString()));
        add(contentLanguage);

        feedback = new JQueryFeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        // In NOT compact mode : update version
        String version = getConfiguration().getVersionAsString();
        if (version == null) {
            version = "";
        }
        add(new Label("version", version));
    }

	
	public final WebSession getWebSession() {
	    return (WebSession)getSession();
	}
	
	public final Application getWebApplication() {
        return (Application)getApplication();
    }
	
	public final WebConfiguration getConfiguration() {
        return getWebApplication().getConfiguration();
    }


    public void setUseGlobalFeedback(boolean useGlobalFeedback) {
        feedback.setVisibilityAllowed(useGlobalFeedback);
    }

    protected FeedbackPanel getFeedbackPanel() {
        return feedback;
    }
}
