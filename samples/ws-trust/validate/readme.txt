
Run the sample:

1. Copy the directory /wsit/wsit/samples/ws-trust/certs/xws-security to 
<GLASSFISH_HOME> or <TOMCAT_HOME>.

2. Edit /wsit/wsit/samples/ws-trust/validate/src/fs/build.properties to set java.home and
   tomcat.home/glassfish.home.

3. Edit
   /wsit/wsit/samples/ws-trust/validate/src/fs/etc/service/PingService.wsdl,
   /wsit/wsit/samples/ws-trust/validate/src/fs/etc/client-config/wsit-client.xml
   and
   /wsit/wsit/samples/ws-trust/validate/src/fs/etc/sts/sts.wsdl,
   to replace $WSIT_HOME with your Glassfish/Tomcat location.

4. Start tomcat or glassfish.

5. To run the sample, go to
   /wsit/wsit/samples/ws-trust/validate/src/fs, and run "ant run-sample".

6. You will be prompted to enter the username/password. Two pairs alice/alice, bob/bob
   are pre-configured to use with this sample.
