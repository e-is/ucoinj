package io.ucoin.ucoinj.web.rest;

/*
 * #%L
 * Reef DB :: Quadrige2 Synchro server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2015 Ifremer
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

import io.ucoin.ucoinj.core.exception.BusinessException;
import io.ucoin.ucoinj.core.exception.TechnicalException;
import io.ucoin.ucoinj.elasticsearch.service.exception.InvalidSignatureException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    /* Logger */
    private static final Log log = LogFactory.getLog(RestResponseEntityExceptionHandler.class);

    public RestResponseEntityExceptionHandler() {
        super();
    }

    /**
     * Transform an exception on bad ... TODO doc
     */
    @ExceptionHandler(value = { TechnicalException.class })
    protected ResponseEntity<Object> handleTechnicalException(RuntimeException ex, WebRequest request) {
        String message = "internal technical error (TODO i18n)";
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
        return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {BusinessException.class })
    protected ResponseEntity<Object> handleBusinessException(RuntimeException ex, WebRequest request) {
        String message = "internal business error (TODO i18n)";
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
        return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {InvalidSignatureException.class })
    protected ResponseEntity<Object> handleInvalidSignatureExceptionk(RuntimeException ex, WebRequest request) {
        String message = "Bad signature (not correspond to given pubkey)";
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
        return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    /* -- protected methods -- */

    private Throwable getCause(Throwable e, Class... classes) {
        for (Class clazz: classes) {
            if (clazz.isInstance(e)) {
                return e;
            }
        }
        if (e.getCause() != null) {
            return getCause(e.getCause(), classes);
        }
        return null;
    }
}
