package io.ucoin.ucoinj.elasticsearch.action;

/*
 * #%L
 * SIH-Adagio :: Shared
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

import io.ucoin.ucoinj.core.util.CommandLinesUtils;
import io.ucoin.ucoinj.core.util.StringUtils;
import io.ucoin.ucoinj.elasticsearch.config.Configuration;
import io.ucoin.ucoinj.elasticsearch.service.CurrencyIndexerService;
import io.ucoin.ucoinj.elasticsearch.service.ElasticSearchService;
import io.ucoin.ucoinj.elasticsearch.service.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NodeAction {
	/* Logger */
	private static final Logger log = LoggerFactory.getLogger(NodeAction.class);

	public void start() {

        Configuration config = Configuration.instance();
        //config.setNodeElasticSearchLocal(false);

        // Starting ES node
        ElasticSearchService esService = ServiceLocator.instance().getElasticSearchService();
        esService.startNode();

        // Wait 5s, to avoid error on existsIndex()
        try {
            Thread t = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(5000);
                    }
                    catch(InterruptedException e) {
                        // continue
                    }
                    catch(IllegalMonitorStateException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            t.join();
        }
        catch(InterruptedException e) {
            // continue
        }


        // Create indexed if need
        CurrencyIndexerService currencyIndexerService = ServiceLocator.instance().getCurrencyIndexerService();
        currencyIndexerService.createIndexIfNotExists();
	}

    /*public void stop() {
        // Starting ES node
        ElasticSearchService esService = ServiceLocator.instance().getElasticSearchService();
        esService.stopNode();
    }*/

    /* -- protected methods -- */

}
