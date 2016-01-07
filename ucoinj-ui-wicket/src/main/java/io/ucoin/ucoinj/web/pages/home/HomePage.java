package io.ucoin.ucoinj.web.pages.home;

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


//import com.googlecode.wicket.jquery.ui.form.autocomplete.AutoCompleteTextField;

import com.googlecode.wicket.jquery.ui.panel.JQueryFeedbackPanel;
import io.ucoin.ucoinj.elasticsearch.model.Currency;
import io.ucoin.ucoinj.elasticsearch.model.SearchResult;
import io.ucoin.ucoinj.elasticsearch.service.CurrencyIndexerService;
import io.ucoin.ucoinj.elasticsearch.service.ServiceLocator;
import io.ucoin.ucoinj.web.pages.BasePage;
import io.ucoin.ucoinj.web.pages.registry.CurrencyPage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HomePage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(HomePage.class);
    private static final long serialVersionUID = 1L;

    private Form<HomePage> form;
    private AutoCompleteTextField<String> searchTextField;
    private String searchQuery = "";
    private WebMarkupContainer resultParent;
    private ListView<SearchResult> resultListView;

    public HomePage(final PageParameters parameters) {
        super(parameters);
        setUseGlobalFeedback(false);

        form = new Form<HomePage>("searchForm");
        form.setOutputMarkupId(true);
        add(form);

        // FeedbackPanel
        final FeedbackPanel feedback = new JQueryFeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        IAutoCompleteRenderer autoCompleteRenderer = new IAutoCompleteRenderer<String>() {
            private static final long serialVersionUID = 1L;

            public void renderHeader(Response response) {
                response.write("<ul>");
            }

            public void render(String choice, Response response, String criteria) {
                response.write("<li textvalue=\"" + choice + "\"");
                response.write(">");

                // Put the substring after the criteria in bold
                if (choice.startsWith(criteria) && choice.length() > criteria.length()) {
                    choice = criteria + "<b>" + choice.substring(criteria.length()) + "</b>";
                }

                response.write(choice);
                response.write("</li>");
            }

            public void renderFooter(Response response, int count) {
                response.write("</ul>");
            }
        };

        // Search text
        searchTextField = new AutoCompleteTextField<String>("searchText", new PropertyModel<String>(this, "searchQuery"), autoCompleteRenderer) {
            private static final long serialVersionUID = 1L;

            @Override
            protected  Iterator<String> getChoices(String input) {
                return doSuggestions(input).iterator();
            }
        };
        searchTextField.add(new AjaxFormComponentUpdatingBehavior("keyup")
        {
            @Override
            protected void onUpdate(AjaxRequestTarget target){
                // do search
                searchTextField.updateModel();
            }

            @Override
            protected void onError(AjaxRequestTarget target, RuntimeException e){
                // Here the Component's model object will remain unchanged,
                // so that it doesn't hold invalid input
            }
        });
        searchTextField.setRequired(true);
        form.add(searchTextField);

        // Submit button
        {
            Button searchButton = new AjaxButton("searchButton") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    // do search
                    doSearch(target, searchQuery);

                    // Force to hide autocomplete choices
                    target.appendJavaScript(
                            "$('div.wicket-aa-container').hide();"
                            + "$('#"+searchTextField.getMarkupId()+"').attr('autocomplete', 'off');");
                }
            };
            searchButton.setDefaultFormProcessing(false);
            searchButton.setOutputMarkupId(true);
            form.add(searchButton);
            form.setDefaultButton(searchButton);
        }

        //form.add(new AjaxFormValidatingBehavior("keyup", Duration.ONE_SECOND));


        // Search result
        {
            // Parent container
            resultParent = new WebMarkupContainer("resultParent");
            resultParent.setOutputMarkupId(true);
            resultParent.setOutputMarkupPlaceholderTag(true);
            resultParent.setVisible(false);
            add(resultParent);

            // History items
            {
                resultListView = new ListView<SearchResult>("resultItems", new ListModel<SearchResult>()) {
                    protected void populateItem(ListItem<SearchResult> item) {
                        SearchResult result = item.getModelObject();

                        // link
                        PageParameters pageParameters = new PageParameters();
                        pageParameters.add(CurrencyPage.CURRENCY_PARAMETER, result.getId());
                        BookmarkablePageLink link = new BookmarkablePageLink("openCurrencyLink", CurrencyPage.class, pageParameters);
                        item.add(link);

                        // Currency name
                        Label label = new Label("currencyName", result.getValue());
                        label.setEscapeModelStrings(false);
                        label.setOutputMarkupPlaceholderTag(false);
                        link.add(label);
                    }
                };
                resultListView.setReuseItems(true);
                resultListView.setOutputMarkupId(true);
                resultParent.add(resultListView);
            }
        }
    }

    /** -- Internal methods -- */

    protected void doSearch(AjaxRequestTarget target, final String searchQuery) {

        if (StringUtils.isBlank(searchQuery)) {
            resultListView.removeAll();
            resultListView.setVisibilityAllowed(false);
            resultParent.setVisible(false);
        }
        else {

            CurrencyIndexerService service = ServiceLocator.instance().getCurrencyIndexerService();
            List<SearchResult> result = service.searchCurrenciesAsVO(searchQuery);

            if (CollectionUtils.isNotEmpty(result)) {
                resultListView.removeAll();
                resultParent.setVisible(true);
                resultParent.modelChanged();
                resultListView.setVisibilityAllowed(true);
                resultListView.setDefaultModelObject(result);
                resultListView.modelChanged();

            } else {
                resultListView.removeAll();
                resultListView.setVisibilityAllowed(false);
                resultParent.setVisible(false);
            }
        }
        // submit actual list after reordering
        form.process(null);

        target.add(resultParent);
    }

    protected List<String> doSuggestions(final String input) {
        List<String> suggestions;
        if (StringUtils.isEmpty(input)){
            suggestions = Collections.<String>emptyList();
        }
        else {
            CurrencyIndexerService service = ServiceLocator.instance().getCurrencyIndexerService();
            suggestions = service.getSuggestions(input);

            if (CollectionUtils.isEmpty(suggestions)) {
                suggestions = Collections.<String>emptyList();
            }
        }

        return suggestions;
    }

}