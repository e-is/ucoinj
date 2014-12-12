package io.ucoin.client.ui.application;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

public class UcoinSession extends AuthenticatedWebSession {

    private static final long serialVersionUID = 1L;

    public UcoinSession(Request request) {
        super(request);
    }

    @Override
    public boolean authenticate(String username, String password) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public Roles getRoles() {
        // TODO Auto-generated method stub
        return null;
    }
}