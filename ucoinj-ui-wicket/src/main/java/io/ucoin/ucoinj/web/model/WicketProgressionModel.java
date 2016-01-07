package io.ucoin.ucoinj.web.model;

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