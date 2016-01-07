package io.ucoin.ucoinj.elasticsearch.service.task;

/*
 * #%L
 * SIH-Adagio :: Synchro Server WebApp
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


import java.io.Serializable;

public class JobVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;

    private final String issuer;

    public JobVO(String id, String issuer) {
        this.id = id;
        this.issuer = issuer;
    }

    public String getId() {
        return id;
    }

}
