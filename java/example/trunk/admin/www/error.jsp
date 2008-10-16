<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="/spring" %>
<%@ taglib prefix="spring-form" uri="/spring-form" %>
<head>
  <title>Red5 Admin</title>
  <meta content="text/html; charset=iso-8859-1" http-equiv="Content-Type" />
  <style type="text/css" media="screen">
html, body, #containerA, #containerB { height: 100%;
}
.formbg { background-color: rgb(238, 238, 238);
}
.formtable { border: 2px solid rgb(183, 186, 188);
}

.formtext { font-family: Arial,Helvetica,sans-serif;
    font-size: 12px;
    color: rgb(11, 51, 73);
}

body { margin: 0pt;
padding: 0pt;
overflow: hidden;
background-color: rgb(250, 250, 250);
}
.error { 
	font-family: Arial,Helvetica,sans-serif;
	font-size: 12px;
	color: red; 
}
  </style>
</head>
<body>
<table style="text-align: left; width: 100%; height: 100%;" border="0" cellpadding="0" cellspacing="10">
  <tbody>
    <tr>
      <td height="54"><img style="width: 136px; height: 54px;" alt="" src="assets/logo.png" /></td>
    </tr>
    <tr class="formbg">
      <td valign="top" class="formtext">
      <table width="600">
      <tr>
      	<td class="formtext">
      <p>An error has occured and you may be here because you are loading the admin demo application for the first time.
      
      <p>You may be required to do the following and then restart the server to load the application correctly for the mean time. 
      
      <p>The following instructions are adapted from <a href="http://gregoire.org/2008/10/01/red5-admin/">Paul Gregoire's Blog Article</a> 
      
<p>I just spent many hours trying to get the Admin (demo) application working properly; it was quite painful. Trying to get JNDI and Spring to cooperate in an Embedded Tomcat instance is not what I call fun, but I have it working alright for now. There still seem to be some underlying classloader issues in the server, because I cannot self-contain the web applications. No matter how I configure the server or application, there are always jars that must be in the shared lib directory; if anyone has any experience with this I would love to hear it.  So without further rambling, here are the steps to take to get it working:</p>
<p>1. Move the following jars from your webapps lib directory red5/webapps/admin/WEB-INF/lib to your shared lib directory (<em>red5/lib</em>)</p>
<ul>
<li>derby-10.4.2.0.jar</li>
<li>spring-jdbc-2.5.5.jar</li>
<li>spring-tx-2.5.5.jar</li>
</ul>
<p>4. Restart Red5</p>
<p>5. Go to http://localhost:5080/admin/register.html to add new users</p>
<p>I hope this helps those of you new to red5, since I know it can be difficult to get things going at times.
		</td>
	   </tr>
	  </table>
      </td>
      </tr>
      </table>
      </td>
    </tr>

  </tbody>
</table>
<br />
</body>
</html>