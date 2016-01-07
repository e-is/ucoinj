package io.ucoin.ucoinj.web.pages.login;

/*
 * #%L
 * UCoin Java Client :: Web
 * %%
 * Copyright (C) 2014 - 2015 EIS
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


import io.ucoin.ucoinj.web.application.Application;
import io.ucoin.ucoinj.web.application.WebSession;
import io.ucoin.ucoinj.web.pages.BasePage;
import io.ucoin.ucoinj.web.security.SecurityContextHelper;
import io.ucoin.ucoinj.web.service.ServiceLocator;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.LoggerFactory;

public class LoginPage extends BasePage {

    public LoginPage(final PageParameters parameters) {
        super(parameters);

        if (SecurityContextHelper.isAuthenticateNotAnonymous()) {
            setResponsePage(Application.get().getHomePage());
            return;
        }
        WebSession sesion = getWebSession();
        LoggerFactory.getLogger(LoginPage.class).info(""+ sesion.isSignedIn());

        // Challenge message
        WebMarkupContainer challengeMessageField = new WebMarkupContainer("challengeMessage") {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                tag.put("value", ServiceLocator.instance().getChallengeMessageStore().createNewChallenge());
            }
        };
        challengeMessageField.setMarkupId("challengeMessage");
        challengeMessageField.setOutputMarkupId(true);
        add(challengeMessageField);

        Form form = new Form("form");
        form.setMarkupId("form");
        form.setOutputMarkupId(true);
        add(form);
    }

}