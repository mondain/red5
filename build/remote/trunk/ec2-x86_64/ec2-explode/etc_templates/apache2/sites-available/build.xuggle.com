<VirtualHost *>
  
  ServerAdmin info@xuggle.com
  ServerName build.xuggle.com
  ServerAlias build-x64.xuggle.com www.build.xuggle.com *.build.xuggle.com

  ErrorLog /var/log/apache2/build.xuggle.com-error.log
  CustomLog /var/log/apache2/build.xuggle.com-access.log combined

  LogLevel warn
  ServerSignature Off

  ProxyPass         / http://localhost:8080/
  ProxyPassReverse  / http://localhost:8080/
  ProxyRequests     Off

  <Proxy http://localhost:8080/*>
      Order deny,allow
      Allow from all
  </Proxy>

</VirtualHost>
