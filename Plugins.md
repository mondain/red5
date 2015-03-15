# Plug-ins #

## The plug-in commandments ##

  1. A plugin's existence or lack thereof shall not affect the server in a negative way
  1. The server shall not concern itself with a plugin's existence
  1. All plugins shall be loaded by the PluginLauncher, except server-wide services
  1. All plugin main classes shall implement IRed5Plugin or extend Red5Plugin
  1. Plugins shall be contained within a jar or zip
  1. A plugin shall only live within the red5/plugins directory

## Projects ##
The majority of the plugins created by the red5 team are available here:
https://github.com/Red5/red5-plugins

### Other plugins ###

[HLS](https://github.com/Red5/red5-hls-plugin)

[WebSocket](https://github.com/Red5/red5-websocket)

## Plug-in downloads ##

[Admin 1.0](http://red5.googlecode.com/files/AdminPlugin-1.0.zip) Unzip and place all the files in your red5/plugins directory.

## Fixing plug-in startup errors ##

### Admin Plugin ###

If you see this error:
```
2009-11-03 14:54:57,776 [main] ERROR o.red5.server.plugin.PluginLauncher - Error loading plugin: org.red5.server.plugin.admin.AdminPlugin; Method: null; Exception: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'authClientRegistry' defined in class path resource [admin-security.xml]: Instantiation of bean failed; nested exception is java.lang.NoClassDefFoundError: org/springframework/security/BadCredentialsException
```

It means you are missing some library jars required by the admin. To resolve this, just place the following jars in the red5/plugins directory:

```
derby-10.5.3.0.jar
commons-dbcp-1.2.2.jar
spring-jdbc-2.5.6.jar
spring-orm-2.5.6.jar
spring-security-core-2.5.6.jar
spring-tx-2.5.6.jar
```

If you are using a security manager with Red5 and the admin plug-in, you must add the following entries to your policy file:

```
grant codeBase "file:plugins/adminplugin.jar" {
  permission java.lang.RuntimePermission "getenv.DERBY_HOME";
  permission java.util.PropertyPermission "derby.*", "read";
  permission java.io.FilePermission "${derby.system.home}","read";
  permission java.io.FilePermission "${derby.system.home}${/}*", "read,write,delete";
  permission java.io.FilePermission "${user.dir}${/}-", "read,write,delete";
  permission java.io.FilePermission "${derby.system.home}${/}Admin${/}-", "read,write,delete";
};

grant codeBase "file:plugins/derby-10.5.3.0.jar" {
  permission java.lang.RuntimePermission "createClassLoader";
  permission java.util.PropertyPermission "derby.*", "read";
  permission java.io.FilePermission "${derby.system.home}","read";
  permission java.io.FilePermission "${derby.system.home}${/}*", "read,write,delete";
  permission java.io.FilePermission "${user.dir}${/}-", "read,write,delete";
  permission java.io.FilePermission "${derby.system.home}${/}Admin${/}-", "read,write,delete";
  permission java.util.PropertyPermission "derby.storage.jvmInstanceId", "write"; 
};
```

**Adding or modifying admin users**

This page will allow you to add or modify users, make sure you remove it when you are done since it allows anyone to add users to your admin.
Instructions:


1. Get the "helper" page here:  http://red5.googlecode.com/files/admin.jsp

2. Start your server

3. Put the admin.jsp page in red5/webapps/root directory

4. Request the page via browser -  http://localhost:5080/admin.jsp

5. Enter your info and submit

6. Go to the admin panel and login