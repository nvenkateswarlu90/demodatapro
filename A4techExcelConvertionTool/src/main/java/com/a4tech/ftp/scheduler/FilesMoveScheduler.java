package com.a4tech.ftp.scheduler;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class FilesMoveScheduler  extends QuartzJobBean{
	   
	private Logger _LOGGER = Logger.getLogger(FilesMoveScheduler.class);
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		_LOGGER.info("Files Move scheduler started");
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
			
			String ftpUploadfilePath ="D:\\Upload Files";
			FileUtils.cleanDirectory(new File(ftpUploadfilePath));
		} catch (IOException e) {
			_LOGGER.error("unable to remove files from source folder: "+e.getCause());
		}
		
				
		
	}
	
	/*public void filesMove() {
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
		
				
	}
*/
}
