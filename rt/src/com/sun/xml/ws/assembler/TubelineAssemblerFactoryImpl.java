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
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubelineAssembler;
import com.sun.xml.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.assembler.localization.LocalizationMessages;
import com.sun.xml.ws.dump.LoggingDumpTube;
import com.sun.xml.ws.policy.jaxws.xmlstreamwriter.documentfilter.WsdlDocumentFilter;
import java.util.Collection;
import java.util.logging.Level;

/**
 * WSIT Tubeline assembler factory
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class TubelineAssemblerFactoryImpl extends TubelineAssemblerFactory {

    private static class MetroTubelineAssembler implements TubelineAssembler {

        private static final String COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE = "com.sun.metro.soap.dump";

        private static enum Side {

            Client("client"),
            Endpoint("endpoint");
            private final String name;

            private Side(String name) {
                this.name = name;
            }

            @Override
            public String toString() {
                return name;
            }
        }

        private static class MessageDumpingInfo {

            final boolean dumpBefore;
            final boolean dumpAfter;
            final Level logLevel;

            MessageDumpingInfo(boolean dumpBefore, boolean dumpAfter, Level logLevel) {
                this.dumpBefore = dumpBefore;
                this.dumpAfter = dumpAfter;
                this.logLevel = logLevel;
            }
        }
        private static final Logger LOGGER = Logger.getLogger(MetroTubelineAssembler.class);
        private final BindingID bindingId;
        private final TubelineAssemblyController tubelineAssemblyController;

        MetroTubelineAssembler(final BindingID bindingId) {
            this.bindingId = bindingId;
            this.tubelineAssemblyController = new TubelineAssemblyController();
        }

        @NotNull
        public Tube createClient(@NotNull ClientTubeAssemblerContext jaxwsContext) {
            ClientTubelineAssemblyContext context = new ClientTubelineAssemblyContext(jaxwsContext);

            Collection<TubeCreator> tubeCreators = tubelineAssemblyController.getTubeCreators(context);

            for (TubeCreator tubeCreator : tubeCreators) {
                tubeCreator.updateContext(context);
            }

            for (TubeCreator tubeCreator : tubeCreators) {
                final MessageDumpingInfo msgDumpInfo = setupMessageDumping(tubeCreator.getMessageDumpPropertyBase(), Side.Client);

                final Tube oldTubelineHead = context.getTubelineHead();
                LoggingDumpTube afterDumpTube = null;
                if (msgDumpInfo.dumpAfter) {
                    afterDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.After, context.getTubelineHead());
                    context.setTubelineHead(afterDumpTube);
                }

                if (!context.setTubelineHead(tubeCreator.createTube(context))) { // no new tube has been created
                    if (afterDumpTube != null) {
                        context.setTubelineHead(oldTubelineHead); // removing possible "after" message dumping tube
                    }
                } else {
                    final String loggedTubeName = context.getTubelineHead().getClass().getName();
                    if (afterDumpTube != null) {
                        afterDumpTube.setLoggedTubeName(loggedTubeName);
                    }

                    if (msgDumpInfo.dumpBefore) {
                        final LoggingDumpTube beforeDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.Before, context.getTubelineHead());
                        beforeDumpTube.setLoggedTubeName(loggedTubeName);
                        context.setTubelineHead(beforeDumpTube);
                    }
                }
            }

            return context.getTubelineHead();
        }

        @NotNull
        public Tube createServer(@NotNull ServerTubeAssemblerContext jaxwsContext) {
            ServerTubelineAssemblyContext context = new ServerTubelineAssemblyContext(jaxwsContext);
            ServiceDefinition sd = context.getEndpoint().getServiceDefinition();
            if (sd != null) {
                sd.addFilter(new WsdlDocumentFilter());
            }

            // FIXME endpoint URI for provider case
            Collection<TubeCreator> tubeCreators = tubelineAssemblyController.getTubeCreators(context);
            for (TubeCreator tubeCreator : tubeCreators) {
                tubeCreator.updateContext(context);
            }

            for (TubeCreator tubeCreator : tubeCreators) {
                final MessageDumpingInfo msgDumpInfo = setupMessageDumping(tubeCreator.getMessageDumpPropertyBase(), Side.Endpoint);

                final Tube oldTubelineHead = context.getTubelineHead();
                LoggingDumpTube afterDumpTube = null;
                if (msgDumpInfo.dumpAfter) {
                    afterDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.After, context.getTubelineHead());
                    context.setTubelineHead(afterDumpTube);
                }

                if (!context.setTubelineHead(tubeCreator.createTube(context))) { // no new tube has been created
                    if (afterDumpTube != null) {
                        context.setTubelineHead(oldTubelineHead); // removing possible "after" message dumping tube
                    }
                } else {
                    final String loggedTubeName = context.getTubelineHead().getClass().getName();
                    if (afterDumpTube != null) {
                        afterDumpTube.setLoggedTubeName(loggedTubeName);
                    }

                    if (msgDumpInfo.dumpBefore) {
                        final LoggingDumpTube beforeDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.Before, context.getTubelineHead());
                        beforeDumpTube.setLoggedTubeName(loggedTubeName);
                        context.setTubelineHead(beforeDumpTube);
                    }
                }
            }

            return context.getTubelineHead();
        }

        private MessageDumpingInfo setupMessageDumping(String msgDumpSystemPropertyBase, Side side) {
            boolean dumpBefore = false;
            boolean dumpAfter = false;
            Level logLevel = Level.INFO;

            // checking common properties
            Boolean value = getBooleanValue(COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE);
            if (value != null) {
                dumpBefore = value.booleanValue();
                dumpAfter = value.booleanValue();
            }

            value = getBooleanValue(COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE + ".before");
            dumpBefore = (value != null) ? value.booleanValue() : dumpBefore;

            value = getBooleanValue(COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE + ".after");
            dumpAfter = (value != null) ? value.booleanValue() : dumpAfter;

            Level levelValue = getLevelValue(COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE + ".level");
            if (levelValue != null) {
                logLevel = levelValue;
            }

            // narrowing to proper communication side on common properties
            value = getBooleanValue(COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE + "." + side.toString());
            if (value != null) {
                dumpBefore = value.booleanValue();
                dumpAfter = value.booleanValue();
            }

            value = getBooleanValue(COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE + "." + side.toString() + ".before");
            dumpBefore = (value != null) ? value.booleanValue() : dumpBefore;

            value = getBooleanValue(COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE + "." + side.toString() + ".after");
            dumpAfter = (value != null) ? value.booleanValue() : dumpAfter;

            levelValue = getLevelValue(COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE + "." + side.toString() + ".level");
            if (levelValue != null) {
                logLevel = levelValue;
            }


            // checking general tube-specific properties
            value = getBooleanValue(msgDumpSystemPropertyBase);
            if (value != null) {
                dumpBefore = value.booleanValue();
                dumpAfter = value.booleanValue();
            }

            value = getBooleanValue(msgDumpSystemPropertyBase + ".before");
            dumpBefore = (value != null) ? value.booleanValue() : dumpBefore;

            value = getBooleanValue(msgDumpSystemPropertyBase + ".after");
            dumpAfter = (value != null) ? value.booleanValue() : dumpAfter;

            levelValue = getLevelValue(msgDumpSystemPropertyBase + ".level");
            if (levelValue != null) {
                logLevel = levelValue;
            }

            // narrowing to proper communication side on tube-specific properties
            msgDumpSystemPropertyBase += "." + side.toString();

            value = getBooleanValue(msgDumpSystemPropertyBase);
            if (value != null) {
                dumpBefore = value.booleanValue();
                dumpAfter = value.booleanValue();
            }

            value = getBooleanValue(msgDumpSystemPropertyBase + ".before");
            dumpBefore = (value != null) ? value.booleanValue() : dumpBefore;

            value = getBooleanValue(msgDumpSystemPropertyBase + ".after");
            dumpAfter = (value != null) ? value.booleanValue() : dumpAfter;

            levelValue = getLevelValue(msgDumpSystemPropertyBase + ".level");
            if (levelValue != null) {
                logLevel = levelValue;
            }

            return new MessageDumpingInfo(dumpBefore, dumpAfter, logLevel);
        }

        private Boolean getBooleanValue(String propertyName) {
            Boolean retVal = null;

            String stringValue = System.getProperty(propertyName);
            if (stringValue != null) {
                retVal = Boolean.valueOf(stringValue);
                LOGGER.fine(LocalizationMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(propertyName, retVal));
            }

            return retVal;
        }

        private Level getLevelValue(String propertyName) {
            Level retVal = null;

            String stringValue = System.getProperty(propertyName);
            if (stringValue != null) {
                // if value is not null => property is set, we will try to override the default logging level
                LOGGER.fine(LocalizationMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(propertyName, stringValue));
                try {
                    retVal = Level.parse(stringValue);
                } catch (IllegalArgumentException ex) {
                    LOGGER.warning(LocalizationMessages.MASM_0019_MSG_LOGGING_SYSTEM_PROPERTY_ILLEGAL_VALUE(propertyName, stringValue), ex);
                }
            }

            return retVal;
        }
    }

    @Override
    public TubelineAssembler doCreate(final BindingID bindingId) {
        return new MetroTubelineAssembler(bindingId);
    }
}
