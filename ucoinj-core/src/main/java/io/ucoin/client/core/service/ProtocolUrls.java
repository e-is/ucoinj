package io.ucoin.client.core.service;

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


public interface ProtocolUrls {

    /* Block chain */
    
    public static final String BLOCKCHAIN_BASE = "/blockchain";
            
    public static final String BLOCKCHAIN_PARAMETERS = BLOCKCHAIN_BASE + "/parameters";

    public static final String BLOCKCHAIN_BLOCK = BLOCKCHAIN_BASE + "/block/%s";
    public static final String BLOCKCHAIN_BLOCKS = BLOCKCHAIN_BASE + "/blocks/%s/%s";
    public static final String BLOCKCHAIN_BLOCK_CURRENT = BLOCKCHAIN_BASE + "/current";


    public static final String BLOCKCHAIN_MEMBERSHIP = BLOCKCHAIN_BASE + "/membership";

    /* Web Of Trust */
    
    public static final String WOT_BASE = "/wot";
    
    public static final String WOT_ADD = WOT_BASE + "/add";
    
    public static final String WOT_LOOKUP = WOT_BASE + "/lookup/%s";
    
    public static final String WOT_CERTIFIED_BY = WOT_BASE + "/certified-by/%s";
    
    public static final String WOT_CERTIFIERS_OF = WOT_BASE + "/certifiers-of/%s";
   
    /* Transaction */
    
    public static final String TX_BASE = "/tx";

    public static final String TX_PROCESS = TX_BASE + "/process";
    
    public static final String TX_SOURCES = TX_BASE + "/sources/%s";

}
