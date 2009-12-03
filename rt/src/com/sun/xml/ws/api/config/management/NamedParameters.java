/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.ws.api.config.management;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.config.management.ManagementMessages;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides type-safe named parameters.
 *
 * @author Fabian Ritzmann
 */
public class NamedParameters {

    private static final Logger LOGGER = Logger.getLogger(NamedParameters.class);

    private final HashMap<String, Object> nameToInstance = new HashMap<String, Object>();

    /**
     * Add parameter with the given name.
     *
     * @param <T> The type of the parameter.
     * @param name The name of the parameter.
     * @param parameter The parameter.
     * @return This instance of NamedParameters (so that you can chain multiple put calls).
     */
    public <T> NamedParameters put(String name, T parameter) {
        this.nameToInstance.put(name, parameter);
        return this;
    }

    /**
     * Add all parameters from the given map.
     *
     * @param <T> The type of the parameters.
     * @param mappings Maps the name of the parameters to the actual parameter.
     * @return This instance of NamedParameters.
     */
    public <T> NamedParameters putAll(Map<? extends String, ? extends T> mappings) {
        this.nameToInstance.putAll(mappings);
        return this;
    }

    /**
     * Get parameter with the given name and type. Returns null if a parameter
     * with the name exists but has a different type.
     *
     * @param <T> The type of the parameter
     * @param name The name of the parameter
     * @return The parameter with the given name or null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        try {
            return (T) this.nameToInstance.get(name);
        } catch (ClassCastException e) {
            LOGGER.warning(ManagementMessages.WSM_5017_FAILED_PARAMETERS_CAST(name), e);
            return null;
        }
    }

    @Override
    public String toString() {
        final StringBuilder output = new StringBuilder("NamedParameters: {");
        boolean firstEntry = true;
        for (String name : nameToInstance.keySet()) {
            if (firstEntry) {
                firstEntry = false;
            }
            else {
                output.append(", ");
            }
            output.append("\"").append(name).append("\" => ").append(nameToInstance.get(name));
        }
        output.append("}");
        return output.toString();
    }
    
}