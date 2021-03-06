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

package com.sun.xml.ws.transport.tcp.grizzly;

import com.sun.enterprise.web.portunif.ProtocolFinder;
import com.sun.enterprise.web.portunif.util.ProtocolInfo;
import com.sun.istack.NotNull;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * A <code>ProtocolFinder</code> implementation that parse the available
 * SocketChannel bytes looking for the PROTOCOL_ID bytes. An SOAP/TCP request will
 * always start with: vnd.sun.ws.tcp
 *
 * This object shoudn't be called by several threads simultaneously.
 *
 * @author Jeanfrancois Arcand
 * @author Alexey Stashok
 */
public final class WSTCPProtocolFinder implements ProtocolFinder {
    
    public WSTCPProtocolFinder() {
    }
    
    
    /**
     * Try to find the protocol from the <code>SocketChannel</code> bytes.
     *
     * @param selectionKey The key from which the SocketChannel can be retrieved.
     * @return ProtocolInfo The ProtocolInfo that contains the information about the
     *                   current protocol.
     */
    public void find(@NotNull final ProtocolInfo protocolInfo) throws IOException {
        final SelectionKey key = protocolInfo.key;
        final SocketChannel socketChannel = (SocketChannel)key.channel();
        final ByteBuffer byteBuffer = protocolInfo.byteBuffer;
        
        int loop = 0;
        int count = -1;
        
        if (protocolInfo.bytesRead == 0) {
            try {
                while ( socketChannel.isOpen() &&
                        ((count = socketChannel.read(byteBuffer))> -1)){
                    
                    if ( count == 0 ){
                        loop++;
                        if (loop > 2){
                            break;
                        }
                    } else if (count > 0) {
                        protocolInfo.bytesRead += count;
                    }
                }
            } catch (IOException ex){
            } finally {
                if ( count == -1 ){
                    return;
                }
            }
        }

        final int curPosition = byteBuffer.position();
        final int curLimit = byteBuffer.limit();
        
        // Rule a - If read length < PROTOCOL_ID.length, return to the Selector.
        if (curPosition < TCPConstants.PROTOCOL_SCHEMA.length()){
            return;
        }
        
        byteBuffer.flip();
        
        // Rule b - check protocol id
        try {
            final byte[] protocolBytes = new byte[TCPConstants.PROTOCOL_SCHEMA.length()];
            byteBuffer.get(protocolBytes);
            final String incomeProtocolId = new String(protocolBytes);
            if (TCPConstants.PROTOCOL_SCHEMA.equals(incomeProtocolId)) {
                protocolInfo.protocol = TCPConstants.PROTOCOL_SCHEMA;
                protocolInfo.byteBuffer = byteBuffer;
                protocolInfo.socketChannel =
                        (SocketChannel)key.channel();
                protocolInfo.isSecure = false;
            }
        } catch (BufferUnderflowException bue) {
        } finally {
            byteBuffer.limit(curLimit);
            byteBuffer.position(curPosition);
        }
    }
    
}
