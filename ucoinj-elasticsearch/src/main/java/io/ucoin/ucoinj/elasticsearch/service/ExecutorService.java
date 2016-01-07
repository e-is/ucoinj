package io.ucoin.ucoinj.elasticsearch.service;

/*
 * #%L
 * UCoin Java Client :: ElasticSearch Indexer
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


import io.ucoin.ucoinj.core.beans.Service;
import io.ucoin.ucoinj.core.model.ProgressionModel;
import io.ucoin.ucoinj.elasticsearch.service.task.Job;
import io.ucoin.ucoinj.elasticsearch.service.task.JobVO;

import java.util.List;
import java.util.Locale;

public interface ExecutorService extends Service{

    void stop(String jobId);

    Job execute(Runnable runnable, String jobId, String issuer, Locale locale, ProgressionModel progression);

    void execute(Runnable runnable);

    ProgressionModel getProgressionByJobId(String jobId);

    List<JobVO> getAllJobs();
}
