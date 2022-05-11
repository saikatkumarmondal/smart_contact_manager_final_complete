package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.springframework.stereotype.Service;



@Service
public class EmailService {
	

	public boolean sendEmail(String subject,String message,String to) {
		boolean f=false;
		String from="imsaikatmondal11@gmail.com";
		String host="smtp.gmail.com";
		Properties properties = System.getProperties();
	
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		Session session=Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("imsaikatmondal11@gmail.com", "OmNamahShivay");
			}
		
		});
		session.setDebug(true);
		MimeMessage mimeMessage = new MimeMessage(session);
		try {
			mimeMessage.setFrom(from);
			mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
			mimeMessage.setSubject(subject);
			//mimeMessage.setText(message);
			mimeMessage.setContent(message, "text/html");
			Transport.send(mimeMessage);
		f=true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	
	}
}
