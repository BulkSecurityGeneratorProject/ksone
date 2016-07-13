package org.crossfit.app.mail;


public interface CrossfitMailSender {

	void sendEmail(String from, String to, String subject, String content,
			boolean isMultipart, boolean isHtml);
}
