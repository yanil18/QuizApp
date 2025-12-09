package com.Bisag.QuizApp.utils;
// package com.anil.square.Utils;

// import javax.mail.Authenticator;
// import javax.mail.Message;
// import javax.mail.Multipart;
// import javax.mail.PasswordAuthentication;
// import javax.mail.Transport;
// import javax.mail.internet.InternetAddress;
// import javax.mail.internet.MimeBodyPart;
// import javax.mail.internet.MimeMessage;
// import javax.mail.internet.MimeMultipart;

// import org.json.JSONObject;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.client.RestTemplate;

// import java.util.Date;
// import java.util.Properties;

// public class SendMail {

// 	public static String sendMail(String subject, InternetAddress[] to, String msgcontent, String activeProfile) {
// 		try {
// 			String ip = "";
// 			if (activeProfile.equals("stagging")) {
// 				ip = "10.247.102.51";

// 			} else {
// 				ip = "10.194.81.45";
// 			}
// 			Properties properties = new Properties();
// 			// properties.put("mail.smtp.host", "10.247.102.51");
// 			properties.put("mail.smtp.host", ip);
// 			properties.put("mail.smtp.port", 25);
// 			properties.put("mail.smtp.auth", true);
// 			properties.put("mail.smtp.starttls.enable", true);
// 			properties.put("mail.user", "ncog@digitalindia.gov.in");
// 			properties.put("mail.password", "Ncog@1948");
// 			// properties.put("mail.smtp.proxy.host", ip);
// 			// properties.put("mail.smtp.proxy.port", 8080);
// 			Authenticator auth = new Authenticator() {
// 				public PasswordAuthentication getPasswordAuthentication() {
// 					return new PasswordAuthentication("ncog@digitalindia.gov.in", "Ncog@1948");
// 				}
// 			};
// 			javax.mail.Session session = javax.mail.Session.getInstance(properties, auth);
// 			Message msg = new MimeMessage(session);
// 			msg.setFrom(new InternetAddress("ncog@digitalindia.gov.in"));
// 			msg.setRecipients(Message.RecipientType.TO, to);
// 			msg.setSubject(subject);
// 			msg.setSentDate(new Date());
// 			MimeBodyPart messageBodyPart = new MimeBodyPart();
// 			messageBodyPart.setContent("<h4>" + msgcontent + "</h4>", "text/html");
// 			Multipart multipart = new MimeMultipart();
// 			multipart.addBodyPart(messageBodyPart);
// 			msg.setContent(multipart);
// 			Transport.send(msg);
// 			return "SUCCESS";
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 			return "FAIL";
// 		}
// 	}

// 	public static String sendDevMail(String subject, String to, String msgcontent) {

// 		// Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("11.11.11.11",
// 		// 8080));
// 		// SimpleClientHttpRequestFactory requestFactory = new
// 		// SimpleClientHttpRequestFactory();
// 		// requestFactory.setProxy(proxy);
// 		RestTemplate restTemplate = new RestTemplate();
// 		try {
// 			HttpHeaders headersT = new HttpHeaders();
// 			headersT.setContentType(MediaType.APPLICATION_JSON);

// 			JSONObject bodyT = new JSONObject();
// 			bodyT.put("username", "ocbis");
// 			bodyT.put("password", "ocbis");

// 			HttpEntity<String> requestEntityT = new HttpEntity<>(bodyT.toString(), headersT);
// 			ResponseEntity<String> responseT = restTemplate.postForEntity(
// 					"https://amritsarovar.gov.in/EmailSmsServer/authenticate", requestEntityT, String.class);

// 			if (responseT.getStatusCode() == HttpStatus.valueOf(200)) {
// 				HttpHeaders headersE = new HttpHeaders();
// 				headersE.setContentType(MediaType.APPLICATION_JSON);
// 				headersE.add("Authorization", "Bearer " + new JSONObject(responseT.getBody()).get("token"));
// 				JSONObject bodyE = new JSONObject();
// 				bodyE.put("subject", subject);
// 				bodyE.put("emailto", to);
// 				bodyE.put("message", msgcontent);
// 				HttpEntity<String> requestEntityE = new HttpEntity<>(bodyE.toString(), headersE);
// 				ResponseEntity<String> responseE = restTemplate.postForEntity(
// 						"https://amritsarovar.gov.in/EmailSmsServer/api/sendemail", requestEntityE, String.class);

// 			}
// 			return "SUCCESS";
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 			return "FAIL";
// 		}
// 	}

// }
