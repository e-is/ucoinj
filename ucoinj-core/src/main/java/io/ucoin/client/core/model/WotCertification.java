package io.ucoin.client.core.model;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
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



/**
 * A certification, return by <code>/wot/certified-by/[uid]</code> or <code>/wot/certifiers-of/[uid]</code>
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 *
 */
public class WotCertification extends BasicIdentity{

    private static final long serialVersionUID = 2204517069552693026L;

    public WotCertificationTime cert_time;

    /**
     * Indicate whether the certification is written in the blockchain or not.
     */
    public boolean written;

    public WotCertificationTime getCert_time() {
        return cert_time;
    }

    public void setCert_time(WotCertificationTime cert_time) {
        this.cert_time = cert_time;
    }

    /**
     * Indicate whether the certification is written in the blockchain or not.
     */
    public boolean isWritten() {
        return written;
    }

    public void setWritten(boolean written) {
        this.written = written;
    }

}
