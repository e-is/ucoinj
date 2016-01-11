package io.ucoin.ucoinj.web.model;

/*
 * #%L
 * uCoinj :: UI Wicket
 * %%
 * Copyright (C) 2014 - 2016 EIS
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

import io.ucoin.ucoinj.elasticsearch.service.ServiceLocator;
import org.apache.wicket.model.LoadableDetachableModel;

public class WicketProgressionModel extends LoadableDetachableModel<io.ucoin.ucoinj.core.model.ProgressionModel> {

    private static final long serialVersionUID = 1L;
    
    private final String jobId;
    
    public WicketProgressionModel(String jobId) {
        this.jobId = jobId;
    }
    
    @Override
    protected io.ucoin.ucoinj.core.model.ProgressionModel load() {
        io.ucoin.ucoinj.core.model.ProgressionModel object = ServiceLocator.instance().getExecutorService().getProgressionByJobId(jobId);
        return object;
    }
    
    
}