package com.a4tech.product.service.imple;

import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import com.a4tech.product.service.IMailService;
import com.a4tech.util.ApplicationConstants;

public class MailServiceImpl implements IMailService{
	
	private static Logger _LOGGER = Logger.getLogger(MailServiceImpl.class);
	@Autowired
    private JavaMailSender mailSender;
    private String  senderMailName;
    /*
     * (non-Javadoc)
     * @see com.a4tech.product.service.IMailService#sendMail(java.lang.String, int)
     * @description this method is send mails to supplier based on supplierASI No
     * @return boolean, if mail has been send success then return true else return false
     */
	@Override
	public boolean sendMail(String supplierId, int batchId) {
		String fileName= batchId+ApplicationConstants.CONST_STRING_DOT_TXT;
		try {
				FileSystemResource  file = new FileSystemResource(ApplicationConstants.CONST_STRING_DOWNLOAD_FILE_PATH+ fileName);
		      MimeMessage mimeMessage = mailSender.createMimeMessage();
		      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		      helper.setFrom(senderMailName);
		      //helper.setTo("venkateswarlu.nidamanuri@a4technology.com");
		      String[] toAddress = {"venkateswarlu.nidamanuri@a4technology.com"};
		      helper.setTo(toAddress);
		     // helper.setCc(ApplicationConstants.SUPPLIER_EMAIL_ID_MAP.get(supplierId));
		      helper.setSubject(supplierId +" "+ "Supplier Error Report");
		      helper.setText("Kindly find the attached " +batchId +".txt Product Error File"
		             + "\n\n\n\n Note: This is a System Generated Message Kindly Do not reply back");
		       helper.addAttachment(file.getFilename(), file);
			_LOGGER.info("Sending Email to : "
					+ Arrays.toString(toAddress));
			   mailSender.send(mimeMessage);
		       _LOGGER.info("Mail Sent Successfully !!!");
		      } catch (MessagingException e) {
			    _LOGGER.error("Mail Not Sent Successfully,Error Msg:"+e.toString());
			    	return false;
			 }catch (Exception e) {
			   _LOGGER.error("Mail Not Sent Successfully,Error Msg:"+e.toString());
			     return false;
			}
		return true;
	}
	@Override
	public void supplierLoginFailureMail(String supplierNo, String body,String subject) {
		try {
		      MimeMessage mimeMessage = mailSender.createMimeMessage();
		      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		      helper.setFrom(senderMailName);
		     String[] toAddress = {"venkateswarlu.nidamanuri@a4technology.com"};
/*		      String[] toAddress = { "venkateswarlu.nidamanuri@a4technology.com", "sharvari.patil@a4technology.com",
				"amey.more@a4technology.com" };
*/		     // helper.setCc(toAddress);
		      helper.setCc(toAddress);
		      helper.setSubject(subject);
		      helper.setText(body);
		      helper.setPriority(ApplicationConstants.CONST_INT_VALUE_ONE);
			_LOGGER.info("Sending Email to : "+ Arrays.toString(toAddress));
			   mailSender.send(mimeMessage);
		       _LOGGER.info("Login Failure Mail Sent Successfully !!!");
		      } catch (MessagingException e) {
			    _LOGGER.error("Login Failure Mail Not Sent Successfully,Error Msg:"+e.toString());
			 }catch (Exception e) {
			   _LOGGER.error("Login Failure Mail Not Sent Successfully,Error Msg:"+e.toString());
			}
	}
	@Override
	public void fileProcessStart(String body,String subject) {
		try {
		      MimeMessage mimeMessage = mailSender.createMimeMessage();
		      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
		      helper.setFrom(senderMailName);
		      String[] bccmails = {"venkateswarlu.nidamanuri@a4technology.com"};
		     // String[] bccmails1 = {"venkateswarlu.nidamanuri@a4technology.com",};
			/*String[] ccAddress = { "venkateswarlu.nidamanuri@a4technology.com", "sharvari.patil@a4technology.com",
					"amey.more@a4technology.com","azam.rizvi@a4technology.com" };*/
			//helper.setTo(toAddress); 
    //         helper.setTo("SPullins@asicentral.com");
		     helper.setTo("integrations@asicentral.com");
			helper.setBcc(bccmails);
		      helper.setSubject(subject);
		      helper.setText(body);
			_LOGGER.info("Sending Email to : "+ Arrays.toString(bccmails));
			   mailSender.send(mimeMessage);
		       _LOGGER.info("Process Status Mail Sent Successfully !!!");
		      } catch (MessagingException e) {
			    _LOGGER.error("Process Status Mail Not Sent Successfully,Error Msg:"+e.toString());
			 }catch (Exception e) {
			   _LOGGER.error("Process Status Mail Not Sent Successfully,Error Msg:"+e.toString());
			}
	}
	
	@Override
	public void fileProcessCompleted(String body, String subject, int batchNo) {
		String fileName= batchNo+ApplicationConstants.CONST_STRING_DOT_TXT;
		try {
			boolean flag=false;
			  FileSystemResource  file = new FileSystemResource(ApplicationConstants.CONST_STRING_DOWNLOAD_FILE_PATH+ fileName);
			  String[] bccmails = { "venkateswarlu.nidamanuri@a4technology.com"};
		      MimeMessage mimeMessage = mailSender.createMimeMessage();
		      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		     helper.setTo("integrations@asicentral.com");
		      helper.setBcc(bccmails);
		      helper.setSubject(subject);
		      helper.setFrom(senderMailName);
		      /*String[] toAddress = {"venkateswarlu.nidamanuri@a4technology.com","sharvari.patil@a4technology.com",
		    		  "azam.rizvi@a4technology.com","amey.more@a4technology.com"};*/
		     
		     // helper.setTo(toAddress);
      //        helper.setTo("SPullins@asicentral.com");
		     
		     
		      
		      
		      if (file.getFile().length() != 0) {
		    	  flag=true;
		      }
		      if(flag){
		    	       helper.setText(body);
				       helper.addAttachment(file.getFilename(), file);
				
		      }else{
		    	  helper = new MimeMessageHelper(mimeMessage, false);
			      helper.setBcc(bccmails);
			      helper.setSubject(subject);
			      helper.setFrom(senderMailName);
		    	  helper.setText("There are no errors present in the given supplier file."
		    			  + "\n"
		    	  		+ " All Products are saved successfully and Active.");
				  
		      }
		      //helper.addAttachment(file.getFilename(), file);
			_LOGGER.info("Sending Email to : "+ Arrays.toString(bccmails));
			   mailSender.send(mimeMessage);
		       _LOGGER.info("Process Status Mail Sent Successfully !!!");
		      } catch (MessagingException e) {
			    _LOGGER.error("Process Status Mail Not Sent Successfully,Error Msg:"+e.toString());
			 }catch (Exception e) {
			   _LOGGER.error("Process Status Mail Not Sent Successfully,Error Msg:"+e.toString());
			}
	}
	@Override
	public void ftpServerFailure() {
		try {
		      MimeMessage mimeMessage = mailSender.createMimeMessage();
		      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
		      helper.setFrom(senderMailName);
		      String[] toAddress = {"venkateswarlu.nidamanuri@a4technology.com"};
			helper.setTo(toAddress); 
		    helper.setSubject("FTP Server is Down/Not Working");
		      helper.setText("Hi Team,"
		      		+ "\n\n\n"
		      		+ "FTP Server is not working,Kindly check in server side"
		      		+ "\n\n\n\n\n\n"
		      		+ "Thanks & Regards,"
		      		+ "A4DataPro Team"
		      		+ "\n\n"
		      		+ "Note: This is Computer Generated Mail. No need to reply.*");
			_LOGGER.info("Sending Email to : "+ Arrays.toString(toAddress));
			   mailSender.send(mimeMessage);
		       _LOGGER.info("FTP Server failure mail has been sent Successfully !!!");
		      } catch (MessagingException e) {
			    _LOGGER.error("FTP Server failure mail Not Sent Successfully,Error Msg:"+e.toString());
			 }catch (Exception e) {
			   _LOGGER.error("FTP Server failure mail Not Sent Successfully,Error Msg:"+e.toString());
			}		
	}
	@Override
	public void fileProcessFail(String fileName) {
		try {
		      MimeMessage mimeMessage = mailSender.createMimeMessage();
		      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		      helper.setFrom(senderMailName);
		      String[] toAddress = {"venkateswarlu.nidamanuri@a4technology.com"};
			helper.setTo(toAddress); 
		    helper.setSubject("Suppliler File Not Processed "+fileName);
		      helper.setText("Hi Team,"
		      		+ "\n\n"
		      		+ fileName + "  Supplier file not processed,Please check the log."
		      				+ "\n\n\nThanks & Regards,"
		      				+ "\nA4DataPro Process System");
			_LOGGER.info("Sending Email to : "+ Arrays.toString(toAddress));
			   mailSender.send(mimeMessage);
		       _LOGGER.info("Process Status Mail Sent Successfully !!!");
		      } catch (MessagingException e) {
			    _LOGGER.error("Process Status Mail Not Sent Successfully,Error Msg:"+e.toString());
			 }catch (Exception e) {
			   _LOGGER.error("Process Status Mail Not Sent Successfully,Error Msg:"+e.toString());
			}
	}
	@Override
	public void numberOfFileProcess(String subject,String fileNames) {
		try {
		      MimeMessage mimeMessage = mailSender.createMimeMessage();
		      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
		      helper.setFrom(senderMailName);
		      String[] toAddress = {"venkateswarlu.nidamanuri@a4technology.com"};
			helper.setTo(toAddress); 
		    helper.setSubject(subject);
		      if(StringUtils.isEmpty(fileNames)){
		    	  helper.setText("Hi Team,"
				      		+ "\n\n"
				      		+ "There are no supplier files availble on FTP Server for file processing."
				      		+ "\n\n\nThanks & Regards,"
				      				+ "\nA4DataPro Process System");
		      } else {
		    	  helper.setText("Hi Team,"
				      		+ "\n\n"
				      		+ "Following supplier files will be proccessed "
				      		+ "\n"
				      		+ fileNames
				      				+ "\n\n\nThanks & Regards,"
				      				+ "\nA4DataPro Process System");  
		      }
		      
			_LOGGER.info("Sending Email to : "+ Arrays.toString(toAddress));
			   mailSender.send(mimeMessage);
		       _LOGGER.info("Process Status Mail Sent Successfully !!!");
		      } catch (MessagingException e) {
			    _LOGGER.error("Process Status Mail Not Sent Successfully,Error Msg:"+e.toString());
			 }catch (Exception e) {
			   _LOGGER.error("Process Status Mail Not Sent Successfully,Error Msg:"+e.toString());
			}
	}
	public String getSenderMailName() {
		return senderMailName;
	}
	public void setSenderMailName(String senderMailName) {
		this.senderMailName = senderMailName;
	}
}
