package com.a4tech.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.util.StringUtils;

import com.a4tech.core.errors.ErrorMessage;
import com.a4tech.core.errors.ErrorMessageList;

public class CommonUtility {
	
   private static Logger _LOGGER = Logger.getLogger(CommonUtility.class);
	public static boolean isEmptyOrNull(String str) {
		return (str != null && !" ".equals(str));
	}
	
	public static String getFileExtension(String fileName){
		
		return  fileName.substring(fileName.lastIndexOf('.')+1);
	}
	
	public static List<String> getStringAsList(String value,String splitter){
		List<String> data = null;
		if(!StringUtils.isEmpty(value)){
			data = new ArrayList<String>();
			String[] values = value.split(splitter);
			for (String attribute : values) {
				if(!StringUtils.isEmpty(attribute)){
					data.add(attribute);
				}
			}
			return data;
		}
		return new ArrayList<String>();
	}
	
	public static String getCellValueDouble(Cell cell) {
		String value = "";
		try {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				value = cell.getStringCellValue();
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				double doubleValue = cell.getNumericCellValue();
				value = String.valueOf(doubleValue);
			}
		} catch (Exception e) {
			_LOGGER.error("Cell value convert into Double: " + e.getMessage());
		}

		return value;
	}
	
	public static String getCellValueStrinOrInt(Cell cell) {
		String value = "";
		try {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				value = cell.getStringCellValue();
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				int numericValue = (int) cell.getNumericCellValue();
				value = String.valueOf(numericValue);
			}
		} catch (Exception e) {
			_LOGGER.error("Cell value convert into String/Int format: "
					+ e.getMessage());
		}

		return value;
	}
	
	public static String getCellValueStrinOrDecimal(Cell cell){
		String value = "";
		try{
	if(cell.getCellType() == Cell.CELL_TYPE_STRING){
		value = cell.getStringCellValue();
		}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
			value = String.valueOf(BigDecimal.valueOf(cell.getNumericCellValue()));
		}
	}catch(Exception e){
		_LOGGER.error("Cell value convert into String/decimal: "+e.getMessage());
	}
		return value;
	}
	public static boolean isPriceQuantity(int indexNumber){
		if(indexNumber >=9 && indexNumber <=22){
			return true;
		}else{
			return false;
		}	
	}
   public static String[] getValuesOfArray(String data,String delimiter){
	   if(!StringUtils.isEmpty(data)){
		   return data.split(delimiter);
	   }
	   return null;
   }
   /* @Author  Venkat ,13/09/2016
    * @Param   String (Value) 
    * @Description This method is checking value is zero or blank
    * @ReturnType boolean
    */
	public static boolean checkZeroAndEmpty(String value) {

		if (ApplicationConstants.CONST_STRING_ZERO.equals(value)
				|| ApplicationConstants.CONST_STRING_EMPTY.equals(value)) {
			return true;
		}
		return false;
	}
   
   public static boolean isBlank(String value){
	   if(value.equals(" ")){
		   return true;
	   }
	   return false;
   }
   /*@author Venkat
    *@param String,it is file extension name i.e xls,csv..
    *@description This method is valid for file extension weather it is xls,xlsx ,csv format or
    *                                                                           any other format
    * @ return boolean ,if filename having xls,xlsx ,csv then return true else false
    */
	public static boolean isValidFormat(String fileName) {

		if (ApplicationConstants.CONST_STRING_XLS.equalsIgnoreCase(fileName)
				|| ApplicationConstants.CONST_STRING_XLSX.equalsIgnoreCase(fileName)
				|| ApplicationConstants.CONST_STRING_CSV.equalsIgnoreCase(fileName)) {
			return true;

		}
		return false;
	}
	/*
	 * @author Venkat
	 * @param String ,response message
	 * @description this method is design for converting error response message 
	 *                                   converting into errorMessageList format
	 * @return errorMessageList 
	 */
	public static ErrorMessageList responseconvertErrorMessageList(
			String response) {
		ErrorMessageList responseList = new ErrorMessageList();
		List<ErrorMessage> errorList = new ArrayList<ErrorMessage>();
		ErrorMessage errorMsgObj = new ErrorMessage();
		errorMsgObj.setMessage(response);
		errorList.add(errorMsgObj);
		if (response.contains("java.net.UnknownHostException")
				|| response.contains("java.net.NoRouteToHostException")) {
			errorMsgObj
					.setReason("Product is unable to process due to Internet service down");
		} else if (response.equalsIgnoreCase("500 Internal Server Error")) {
			errorMsgObj
					.setReason("Product is unable to process due to ASI server issue");
		} else if (response.contains("java.net.SocketTimeoutException")) {
			errorMsgObj
					.setReason("Product is unable to process due to ASI server not responding");
		}
		responseList.setErrors(errorList);
		return responseList;
	}
	/*
	 * author Venkat 13/10/2016
	 * @param String OriginalValue,String String SpecialSymbol
	 * @description This method is remove special symbol in given value
	 * @return String,it returns finalValue  
	 */
	public static String removeSpecialSymbols(String value,String symbol){
		String finalValue = value.replaceAll(symbol, "");
		return finalValue;
	}
	/*@author Venkat 18/10/2016
	 *@param String,String,String 
	 *@description This method design for concatenate two string by delimiter
	 *@return String 
	 */
	public static String appendStrings(String src, String destination ,String delimiter){
		  if(!StringUtils.isEmpty(destination)){
			  src = src.concat(ApplicationConstants.CONST_DELIMITER_HYPHEN).concat(destination);
			  return src;
		  }else {
			  return src;
		  }
	}
	
  public static String removeCurlyBraces(String source){
	  if(source.contains("["))
	  {
		  source = source.replace("[", "");
	  }
	  if(source.contains("]")){
		  source = source.replace("]", "");
	  }
	  
	  return source;
  }
}
