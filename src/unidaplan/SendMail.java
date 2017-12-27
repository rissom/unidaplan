package unidaplan;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
 
public class SendMail {
 
	public static void sendEmail(String recipient, String subject, String body) {
 
		String username;
		String password;
		int smtpport = 0;
		String smtpserver = null;
		
		InitialContext initialContext;
		try {
			initialContext = new InitialContext();
			Context environmentContext = (Context) initialContext.lookup("java:/comp/env");
			smtpserver = (String) environmentContext.lookup("smtpserver");
			smtpport = (Integer) environmentContext.lookup("smtpport");
			username = (String) environmentContext.lookup("username");
			password = (String) environmentContext.lookup("password");
			
			Properties props = new Properties();
			props.put("mail.smtp.ssl.trust", smtpserver);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", smtpserver);
			props.put("mail.smtp.port", smtpport);
						
			Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(recipient));
			message.setSubject(subject);
			message.setText(body);
 
			Transport.send(message);
			
		} catch (NamingException e1) {
			e1.printStackTrace();
		} catch (MessagingException e) {
		    throw new RuntimeException(e);
		}
	}
}
	

