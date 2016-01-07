package io.ucoin.ucoinj.core.client.model.bma.gson;

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



import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType {

    private final Type rawType;
    private final Type ownerType;
    private final Type[] typeArguments;

    public ParameterizedTypeImpl(final Type rawType, final Type ownerType, final Type... typeArguments) {
        this.rawType = rawType;
        this.ownerType = ownerType;
        this.typeArguments = typeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

}