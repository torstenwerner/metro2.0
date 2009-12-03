/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.ws.assembler;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.policy.PolicyMap;

/**
 * @author Arun Gupta
 */
public class ClientPipelineHook extends com.sun.xml.ws.api.client.ClientPipelineHook {
    /**
     * Called during the client-side pipeline construction process once to allow a
     * container to register a pipe for security.
     *
     * This pipe will be injected to a point very close to the transport, allowing
     * it to do some security operations.
     *
     * @param policyMap {@link PolicyMap} holding policies for a scope
     *
     * @param ctxt
     *      Represents abstraction of SEI, WSDL abstraction etc. Context can be used
     *      whether add a new pipe to the head or not.
     *
     * @param tail
     *      Head of the partially constructed pipeline. If the implementation
     *      wishes to add new pipes, it should do so by extending
     *      {@link com.sun.xml.ws.api.pipe.helper.AbstractFilterPipeImpl} and making sure that this {@link com.sun.xml.ws.api.pipe.Pipe}
     *      eventually processes messages.
     *
     * @return
     *      The default implementation just returns <tt>tail</tt>, which means
     *      no additional pipe is inserted. If the implementation adds
     *      new pipes, return the new head pipe.
     */
    public @NotNull
    Pipe createSecurityPipe(@Nullable PolicyMap policyMap, ClientPipeAssemblerContext ctxt, @NotNull Pipe tail) {
        return tail;
    }
   
    /**
     * Called during the client-side tubeline construction process once to allow a
     * container to register a tube for security.
     *
     * This tube will be injected to a point very close to the transport, allowing
     * it to do some security operations.
     * <p>
     * If the implementation wishes to add new tubes, it should do so by extending
     * {@link com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl} and making sure that this {@link Tube}
     * eventually processes messages.
     *
     * @param context
     *      Represents abstraction of PolicyMap, SEI, WSDL abstraction etc. Context can be used
     *      whether add a new tube to the head or not.
     *
     * @return
     *      The default implementation just returns <tt>tail</tt>, which means
     *      no additional tube is inserted. If the implementation adds
     *      new tubes, return the new head tube.
     */
    public @NotNull
    Tube createSecurityTube(ClientTubelineAssemblyContext context) {
        return context.getTubelineHead();
    }}
