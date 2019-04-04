package com.a4tech.ftp.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a4tech.ftp.FilesParsing;
import com.a4tech.ftp.service.FtpService;
import com.a4tech.product.service.IMailService;


public class FtpServiceImpl implements FtpService{
  private FTPClient 	ftpClient ;
  private String 		serveraddress ;
  private String 		username  ;
  private String 		password  ;
  private String 		portNo  ;
  private FilesParsing 	filesParsing;
  @Autowired
  private IMailService  mailService;
private Logger _LOGGER = Logger.getLogger(FtpServiceImpl.class);
	@Override
	public boolean uploadFile(File file ,String asiNumber,String environmentType) {
		_LOGGER.info("Enter the Upload file class");
		try {
			ftpClient.connect(serveraddress,Integer.parseInt(portNo));
			if(!ftpClient.login(username, password)){
				ftpClient.logout();
			}else{
			}
			int reply = ftpClient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				ftpClient.disconnect();
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			//InputStream inputStream =  new BufferedInputStream();
			String fileName = environmentType+"_"+asiNumber + "_"+file.getName();
			boolean fileStatus = ftpClient.storeFile(fileName, new FileInputStream(file));
			//inputStream.close();
			ftpServerDisconnect();
			return fileStatus;
		} catch (IOException exe) {
			_LOGGER.error("unable to save file in Ftp Server: "+exe.getMessage());
		}catch (Exception exe) {
			_LOGGER.error("unable to save file in Ftp Server: "+exe.getMessage());
		}
		
		return false;
	}
	@Override
	public void downloadFiles() {
		   _LOGGER.info("Download Files from Ftp Server");
			OutputStream output = null;
			try {
				ftpClient.connect(serveraddress,Integer.parseInt(portNo));
			if(!ftpClient.login(username, password)){
				ftpClient.logout();
			}else{
			}
			int reply = ftpClient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				ftpClient.disconnect();
			
			}
			ftpClient.enterLocalPassiveMode();
		FTPFile[] allFiles = ftpClient.listFiles();//C:\A4ESPUpdate\UploadFiles
		
		for(FTPFile ftpFile : allFiles) {
			if(!ftpFile.isFile()){
				continue;
			}
			if(ftpFile.isDirectory()){
				//ftpFile.get
			}
	         output = new FileOutputStream("D:\\A4 ESPUpdate\\FtpFiles" + "/" + ftpFile.getName());
	         //get the file from the remote system
	         ftpClient.retrieveFile(ftpFile.getName(), output);   
		}
		//listDirectory(fClient, "/", "", 0);
		ftpServerDisconnect();
		File[] listOfFiles = getAllFiles();
		_LOGGER.info("Ftp files Count::"+listOfFiles.length);
		if(listOfFiles.length == 0){
			mailService.numberOfFileProcess("There Is No Files In FTP Server", "");
		} else {
			StringBuilder fileNames = new StringBuilder();
			for (File file : listOfFiles) {
				fileNames.append(file.getName()).append(",");
			}
			mailService.numberOfFileProcess("Following Files Needs to be Process", fileNames.toString());
		}
		if(listOfFiles.length != 0){// check the number of files ,if there is no files in server no need to call fileParsing 
			filesParsing.ReadFtpFiles(listOfFiles);	
		}
		} catch (IOException e) {
			_LOGGER.error("unable to connect to Ftp server: "+e.getMessage());
		}catch (Exception e) {
	      _LOGGER.error("unable to connect to Ftp server: "+e.getMessage());
		}finally{
			if(output != null){
				try {
					output.close();
				} catch (Exception exce) {
					_LOGGER.error("unable to close output stream: "+exce.getMessage());
				}
			}
		}
	 }
	public File[] getAllFiles()
	{  
		File[] listOfFiles = null;
		try{
			File file = new File("D:\\A4 ESPUpdate\\FtpFiles");
			 listOfFiles =file.listFiles();
		}catch(Exception exce){
			_LOGGER.error("Ftp Files are not available : "+exce.getMessage());
			return listOfFiles;
		}
		return listOfFiles;
	}

	public void ftpServerDisconnect(){
		if(this.ftpClient.isConnected()){
			try {
				this.ftpClient.logout();
				this.ftpClient.disconnect();
			} catch (IOException exce) {
				_LOGGER.error("unable to close FTP server: "+exce.getMessage());
			}
		
		}
	}
	/*@Override
	public void filesMove() {
        File source = new File("D:\\A4 ESPUpdate\\FtpFiles");
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDate date = currentDate.toLocalDate();
        String dest = "D:\\A4 ESPUpdate\\FtpFilesBackUp\\"+date;
        File destination = new File(dest);
        if(!destination.isDirectory()){
        	destination.mkdir();
        }
        try {
            FileUtils.copyDirectory(source, destination);
            _LOGGER.info("All files has been copied successfully");
        } catch (IOException e) {
            _LOGGER.error("Unable to Copy file from source folder to destination folder: "+e.getCause());
        }
        
        try {
			FileUtils.cleanDirectory(source);
			_LOGGER.info("all files removed form src folder");
		} catch (IOException e) {
			_LOGGER.error("unable to remove files from source folder: "+e.getCause());
		}
		
				
	}*/
	/* private void listDirectory(FTPClient ftpClient, String parentDir,
	        String currentDir, int level) throws IOException {
		OutputStream output = null;
	    String dirToList = parentDir;
	    if (!currentDir.equals("")) {
	        dirToList += "/" + currentDir;
	    }
	    FTPFile[] subFiles = ftpClient.listFiles(dirToList);
	    if (subFiles != null && subFiles.length > 0) {
	        for (FTPFile ftpFile : subFiles) {
	            String currentFileName = ftpFile.getName();
	            if (currentFileName.equals(".")
	                    || currentFileName.equals("..")) {
	                // skip parent directory and directory itself
	                continue;
	            }
	            if (ftpFile.isDirectory()) {
	                listDirectory(ftpClient, dirToList, currentFileName, level + 1);
	            } else {
	            	 output = new FileOutputStream("D:\\A4 ESPUpdate\\FtpFiles" + "/" + ftpFile.getName());
	                 //get the file from the remote system
	            	 ftpClient.retrieveFile(ftpFile.getName(), output);
	            }
	        }
	    }
	}*/

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	public String getServeraddress() {
		return serveraddress;
	}

	public void setServeraddress(String serveraddress) {
		this.serveraddress = serveraddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getPortNo() {
		return portNo;
	}

	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}
	public FilesParsing getFilesParsing() {
		return filesParsing;
	}
	public void setFilesParsing(FilesParsing filesParsing) {
		this.filesParsing = filesParsing;
	}
	


}
