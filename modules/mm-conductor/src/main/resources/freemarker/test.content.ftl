<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <title>Mail Server Configuration Test</title>
	<style type="text/css">
		body {background-color:#F4F4F4; font-family:Verdana, Arial, sans-serif; font-size:12px}
		h1 {font-size:20px; color: #0D294E}
		
	</style>
</head>
<body>
	<h1>Mail Server Configuration Test</h1>
	Hello,<br/>
	you have successfully setup a new mail server configuration:
	
	<br/><br/>
	
    <table>
        <tr>
            <td>Sender Name:</td>
            <td>${name}</td>
        </tr>
        <tr>
            <td>SMTP Host:</td>
            <td>${hostName}</td>
        </tr>
        <tr>
            <td>SMTP Port:</td>
            <td>${port}</td>
        </tr>
        <tr>
            <td>SMTP User:</td>
            <td>${username}</td>
        </tr>   
    </table>            
                
    <br/>
    <h2>Your test message was:</h2>           
	<p>${message}</p>
    <hr/>
    Your MediaMagpie System
    <br />
         
</body>
</html>
