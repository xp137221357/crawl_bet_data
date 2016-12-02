package com.xixi.bet.utils;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MailUtil {

	private String to;
	private String from;
	private String smtpServer;
	private String username;
	private String password;
	private String subject;
	private String content;
	List<String> attachments = new ArrayList<String>();
	// 增加附件，将附件文件名添加的List集合中
	public void attachfile(String fname) {
		attachments.add(fname);
	}
	// 发送邮件
	public boolean send(String type) {
		// 创建邮件Session所需的Properties对象
		// 使用smtp：简单邮件传输协议,存储发送邮件服务器的信息,验证
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.auth", "true");
		// 创建Session对象,以匿名内部类的形式创建登录服务器的认证对象
		Session session = Session.getDefaultInstance(props
				, new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		//有他会打印一些调试信息
		//session.setDebug(true);
		try {
			// 构造MimeMessage并设置相关属性值
			MimeMessage msg = new MimeMessage(session);
			// 设置发件人
			msg.setFrom(new InternetAddress(from));
			// 设置收件人
			InternetAddress[] addresses = { new InternetAddress(to) };
			msg.setRecipients(RecipientType.TO, addresses);
			// 设置邮件主题
			subject = transferChinese(subject);
			msg.setSubject(subject);
			// 构造Multipart
			Multipart mp = new MimeMultipart();
			// 向Multipart添加正文
			MimeBodyPart mbpContent = new MimeBodyPart();
			mbpContent.setText(content);
			msg.setContent(content, "text/html;charset = gbk");
			// 将BodyPart添加到MultiPart中
			mp.addBodyPart(mbpContent);
			// 向Multipart添加MimeMessage
			// 设置发送日期
			msg.setSentDate(new Date());
			// 发送邮件
			Transport.send(msg);
		} catch (Exception mex) {
			mex.printStackTrace();
			return false;
		}
		return true;
	}
	// 把邮件主题转换为中文
	public String transferChinese(String strText) {
		try {
			strText = MimeUtility.encodeText(new String(strText.getBytes(), "GB2312"), "GB2312", "B");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strText;
	}

	public static void sendMail(String fromMail, String user, String password, String toMail, String mailTitle, String mailContent) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.163.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.transport.protocol", "smtp");
		Session session = Session.getInstance(props);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromMail));
		message.setRecipient(RecipientType.TO, new InternetAddress(toMail));
		message.setSubject(mailTitle);
		message.setContent(mailContent, "text/html;charset=gbk");
		message.setSentDate(new Date());
		message.saveChanges();
		Transport transport = session.getTransport();
		transport.connect(user, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	public static void sendErroeToMail(String title, String content) {
		try {
			String defaultFormMail = SysConfig.getProperty("mail.default.frommail");
			String defaultUser = SysConfig.getProperty("mail.default.user");
			String defaultPassword = SysConfig.getProperty("mail.default.password");
			sendMail(defaultFormMail, defaultUser, defaultPassword, "137221357@qq.com", title, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendTaskReportToMail(String title, String content) {
		sendErroeToMail(title,content);
	}
}