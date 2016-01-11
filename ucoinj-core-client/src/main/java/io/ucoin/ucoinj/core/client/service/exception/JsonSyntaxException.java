package io.ucoin.ucoinj.core.client.service.exception;

/*
 * #%L
 * UCoin Java :: Core Client API
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

import io.ucoin.ucoinj.core.exception.TechnicalException;

/**
 * Created by blavenie on 05/01/16.
 */
public class JsonSyntaxException extends TechnicalException {

    public JsonSyntaxException() {
        super();
    }

    public JsonSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonSyntaxException(String message) {
        super(message);
    }

    public JsonSyntaxException(Throwable cause) {
        super(cause);
    }
}
