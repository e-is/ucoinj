package io.ucoin.ucoinj.web.components.behavior;

/*
 * #%L
 * SIH-Adagio Extractor web UI
 * %%
 * Copyright (C) 2012 - 2013 Ifremer
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


import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class FocusOnLoadBehavior extends AbstractDefaultAjaxBehavior {
	/**
	 * Focus the component after loading
	 */
	private static final long serialVersionUID = -4369132303242175903L;

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		String javascript = "setTimeout(\"$('#" + component.getMarkupId() + "').focus()\", 100);" ;
		response.render(OnDomReadyHeaderItem.forScript(javascript));
	}
	
	@Override
	protected void respond(AjaxRequestTarget target) {
	}

	@Override
	public boolean isTemporary(Component component) {
		return true;
	}
}
