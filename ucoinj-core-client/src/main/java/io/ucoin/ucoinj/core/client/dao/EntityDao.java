package io.ucoin.ucoinj.core.client.dao;

import io.ucoin.ucoinj.core.beans.Bean;
import io.ucoin.ucoinj.core.client.model.local.LocalEntity;

/**
 * Created by blavenie on 29/12/15.
 */
public interface EntityDao<B extends LocalEntity> extends Bean{

        B create(B entity);

        B update(B entity);

        B getById(long id);

        void remove(B entity);

}
