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
package com.sun.xml.ws.tx.webservice.member.coord;

import com.sun.xml.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.ws.developer.Stateful;
import com.sun.xml.ws.developer.StatefulWebServiceManager;
import com.sun.xml.ws.tx.common.TxLogger;
import com.sun.xml.ws.tx.coordinator.RegistrationManager;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.util.logging.Level;

/**
 * This class handles the register web service method
 *
 * @author Ryan.Shoemaker@Sun.COM
 * @version $Revision: 1.4 $
 * @since 1.0
 */
@MemberSubmissionAddressing
@Stateful
@WebService(serviceName = RegistrationCoordinatorPortTypeImpl.serviceName,
        portName = RegistrationCoordinatorPortTypeImpl.portName,
        endpointInterface = "com.sun.xml.ws.tx.webservice.member.coord.RegistrationCoordinatorPortType",
        targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor",
        wsdlLocation = "WEB-INF/wsdl/wscoor.wsdl")
public class RegistrationCoordinatorPortTypeImpl implements RegistrationCoordinatorPortType {

    public static final String serviceName = "Coordinator";
    public static final String portName = "RegistrationCoordinator";

    private static final TxLogger logger = TxLogger.getLogger(RegistrationCoordinatorPortTypeImpl.class);

    @Resource
    private WebServiceContext wsContext;

    /* stateful fields */
    private static StatefulWebServiceManager<RegistrationCoordinatorPortTypeImpl> manager;
    private String activityId;

    public RegistrationCoordinatorPortTypeImpl() {
    }

    /**
     * Constructor for maintaining state
     *
     * @param activityId
     */
    public RegistrationCoordinatorPortTypeImpl(String activityId) {
        this.activityId = activityId;
    }

    /**
     * Handle ws:coor &lt;Register> messages.
     * <p/>
     * <pre>
     * &lt;Register ...>
     *   &lt;ProtocolIdentifier> ... &lt;/ProtocolIdentifier>
     *   &lt;ParticipantProtocolService> ... &lt;/ParticipantProtocolService>
     *   ...
     * &lt;/Register>
     * </pre>
     *
     * @param parameters contains the &lt;register> message
     */
    public void registerOperation(RegisterType parameters) {
        if (logger.isLogging(Level.FINER)) {
            logger.entering("wscoor:register", parameters);
        }
        RegistrationManager.getInstance().register(wsContext, activityId, parameters);
        if (logger.isLogging(Level.FINER)) {
            logger.exiting("wscoor:register");
        }
    }

    public static StatefulWebServiceManager<RegistrationCoordinatorPortTypeImpl> getManager() {
        return manager;
    }

    public static void setManager(StatefulWebServiceManager<RegistrationCoordinatorPortTypeImpl> aManager) {
        manager = aManager;
    }
}
