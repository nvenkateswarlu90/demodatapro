package com.a4tech.supplier.mapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.AdditionalColor;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.ImprintLocation;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.ImprintSize;
import com.a4tech.product.model.Origin;
import com.a4tech.product.model.OtherSize;
import com.a4tech.product.model.Packaging;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.ProductNumber;
import com.a4tech.product.model.ProductionTime;
import com.a4tech.product.model.RushTime;
import com.a4tech.product.model.ShippingEstimate;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import parser.gillstudios.GillStudiosAttributeParser;
import parser.gillstudios.GillStudiosImprintMethodParser;
import parser.gillstudios.GillStudiosLookupData;
import parser.gillstudios.GillStudiosPriceGridParser;


public class GillStudiosMapping implements ISupplierParser{
	
	private static final Logger _LOGGER = Logger.getLogger(GillStudiosMapping.class);
	
	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	private GillStudiosAttributeParser gillStudiosAttributeParser;
	private GillStudiosPriceGridParser gillStudiosPriceGridParser;
	private GillStudiosImprintMethodParser gillStudiosImprintMethodParser;
	@Autowired
	ObjectMapper mapperObj;
	
	@SuppressWarnings("unused")
	public String readExcel(String accessToken,Workbook workbook ,Integer asiNumber ,int batchId, String environmentType){
		
		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		String finalResult = null;
		Set<String>  productXids = new HashSet<String>();
		String asiProdNo="";
		  Product productExcelObj = new Product();   
		  ProductConfigurations productConfigObj=new ProductConfigurations();
		  String productId = null;
		  List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		  List<String> repeatRows = new ArrayList<>();
		  StringBuilder dimensionValue = new StringBuilder();
		  StringBuilder dimensionUnits = new StringBuilder();
		  StringBuilder dimensionType = new StringBuilder();
		  Dimension finalDimensionObj=new Dimension();
		  StringBuilder listOfQuantity = new StringBuilder();
		  StringBuilder listOfPrices = new StringBuilder();
		  StringBuilder priceIncludes = new StringBuilder();
		  StringBuilder pricesPerUnit = new StringBuilder();
		  StringBuilder ImprintSizevalue = new StringBuilder();
		  StringBuilder ImprintSizevalue2 = new StringBuilder();
		  boolean criteriaFlag=false;
			List<Color> colorList = new ArrayList<Color>();
			List<ImprintLocation> listImprintLocation = new ArrayList<ImprintLocation>();
			List<ImprintMethod> listOfImprintMethods = new ArrayList<ImprintMethod>();
			List<ProductionTime> listOfProductionTime = new ArrayList<ProductionTime>();
			List<String> productKeywords = new ArrayList<String>();
			List<Theme> themeList = new ArrayList<Theme>();
			//List<Catalog> catalogList = new ArrayList<Catalog>();
			List<String> complianceList = new ArrayList<String>();
			List<Image> listOfImages= new ArrayList<Image>();
		    List<ImprintSize> imprintSizeList =new ArrayList<ImprintSize>();
		    //List<com.a4tech.lookup.model.Catalog> catalogsList=new ArrayList<>();
		    List<Values> valuesList =new ArrayList<Values>();
			RushTime rushTime  = new RushTime();
			Size size=new Size();
			StringBuilder additionalClrRunChrgPrice = new StringBuilder();
			String        additionalClrRunChrgCode = "";
			String        additionalColorPriceVal = "";
			String        additionalColorCode     = "";
			HashMap<String, String>  productPricePriceMap=new HashMap<String, String>();
			 String xid = null;
		try{
			List<ProductNumber> pnumberList = new ArrayList<ProductNumber>(); 
		_LOGGER.info("Total sheets in excel::"+workbook.getNumberOfSheets());
	    Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = sheet.iterator();
		_LOGGER.info("Started Processing Product");
		String priceCode = null;
		String productName = null;
		String basePriceName=null;
		String quoteUponRequest  = null;
		String quantity = null;
		String cartonL = null;
		String cartonW = null;
		String cartonH = null;
		String weightPerCarton = null;
		String unitsPerCarton = null;
		String decorationMethod =null;
		 Date  priceConfirmedThru =null;
		String FirstImprintsize1=null;
		String FirstImprintunit1=null;
		String FirstImprinttype1=null;
		String FirstImprintsize2=null;
		String FirstImprintunit2=null;
		String FirstImprinttype2=null;
		String SecondImprintsize1=null;
		String SecondImprintunit1=null;
		String SecondImprinttype1=null;
		String SecondImprintsize2=null;
		String SecondImprintunit2=null;
		String SecondImprinttype2=null;
		String CatYear=null;
		Cell cell2Data = null;
		String prodTimeLo = null;
		String FOBValue= null;
		String themeValue=null;
		String priceIncludesValue=null;
		String imprintLocation = null;
		String ProductStatus=null;
		boolean Prod_Status;
		Product existingApiProduct = null;
		int columnIndex = 0;
		Map<String, String>  imprintMethodUpchargeMap = new LinkedHashMap<>();
		HashMap<String, String>  pnumMap = new HashMap<>();
		HashSet<String>  colorSet = new HashSet<>();
		String firstValue="";
		/*while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if (nextRow.getRowNum() < 1)
				continue;
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(productId);
				repeatRows.add(productId);
			}
			 boolean checkXid  = false;
		
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String xid = null;
				 columnIndex = cell.getColumnIndex();
				  cell2Data =  nextRow.getCell(2);
				if(columnIndex + 1 == 1){
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						xid = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						xid = String.valueOf((int)cell.getNumericCellValue());
					}else {
						  String ProdNo=CommonUtility.getCellValueStrinOrInt(cell2Data);
						  xid=ProdNo;

						}
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){*/
while (iterator.hasNext()) {
			
			try{
			Row nextRow = iterator.next();
			if(nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO){
				continue;
			}
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			if(xid != null){
				productXids.add(xid);
				repeatRows.add(xid);
			}
			 boolean checkXid  = false;
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				
				 columnIndex = cell.getColumnIndex();
				 cell2Data =  nextRow.getCell(2);
				if(columnIndex + 1 == 1){
					//xid = CommonUtility.getCellValueStrinOrInt(cell);//getProductXid(nextRow);
					if(cell.getCellType() == Cell.CELL_TYPE_STRING){
						xid = cell.getStringCellValue();
					}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						xid = String.valueOf((int)cell.getNumericCellValue());
					}else {
						  String ProdNo=CommonUtility.getCellValueStrinOrInt(cell2Data);
						  xid=ProdNo;

						}
					xid=xid.trim();
					checkXid = true;
				}else{
					checkXid = false;
				}
				if(checkXid){
					 if(!productXids.contains(xid)){
						 if(nextRow.getRowNum() != 1){
							 System.out.println("Java object converted to JSON String, written to file");
								
							 
							 if(!CollectionUtils.isEmpty(pnumMap) && pnumMap.size()>1){
									pnumberList=gillStudiosAttributeParser.getProductNumer(pnumMap);
									productExcelObj.setProductNumbers(pnumberList);
								}
							 
							 if(!CollectionUtils.isEmpty(colorSet) ){
								 for (String colorValue : colorSet) {
										colorList=gillStudiosAttributeParser.getProductColors(colorValue,colorList);
								}
								 productConfigObj.setColors(colorList);
							 }
							 
								/*productPricePriceMap.put(basePriceName, listOfPrices.toString()+"@@@@@"+listOfQuantity.toString()+"@@@@@"+priceCode
										+"@@@@@"+priceIncludesValue+"@@@@@"+quoteUponRequest+"@@@@@"+tempCriteria+""+pricesPerUnit.toString());
								*/
							 if(!CollectionUtils.isEmpty(productPricePriceMap) ){
								 int mapSize=productPricePriceMap.size();
							 Iterator mapItr = productPricePriceMap.entrySet().iterator();
							    while (mapItr.hasNext()) {
							    	
							        Map.Entry values = (Map.Entry)mapItr.next();
							        String basePRIC= values.getKey().toString();
							        String mapValue	=values.getValue().toString();
							    	String mapValueArr[]=mapValue.split("@@@@@");
							    	
							    	String listOfPric=mapValueArr[0];//listOfPrices
							    	String listOfQuan=mapValueArr[1];//listOfQuantity
							    	String priceCD=mapValueArr[2];//priceCode
							    	String priceINC=mapValueArr[3];//priceIncludesValue
							    	String quR=mapValueArr[4];//quoteUponRequest
							    	String tempCRI=mapValueArr[5];//tempCriteria
							    	String priceUNI=mapValueArr[6];//pricesPerUnit
							    	
							    	//basePriceName="AAAAA";
							    	if(basePRIC.equals("AAAAA")){
							    		basePRIC="";
							    	}
									//tempCriteria="BBBBB";
							    	if(tempCRI.equals("BBBBB")){
							    		tempCRI="";
							    	}
							    	///////////////jan 22 2018
							    	if(mapSize==1){
							    		priceGrids = gillStudiosPriceGridParser.getPriceGrids(listOfPric,listOfQuan, 
							    				priceCD,ApplicationConstants.CONST_STRING_CURRENCY_USD,
							    				priceINC,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
												"","",priceUNI,priceGrids);
							    		
							    		/*(String listOfPrices,
							    			    String listOfQuan, String discountCodes,
							    				String currency, String priceInclude, boolean isBasePrice,
							    				String qurFlag, String priceName, String criterias,String priceUnitArr,
							    				List<PriceGrid> existingPriceGrid)*/
							    		
							    	}else{
							    	priceGrids = gillStudiosPriceGridParser.getPriceGrids(listOfPric, 
							    			listOfQuan, priceCD, "USD",
							    			priceINC, true, quR, basePRIC,tempCRI,priceUNI,priceGrids);
							    	}
							    	//////////////
							    }
							 }
								/*priceGrids = gillStudiosPriceGridParser.getPriceGrids(listOfPrices.toString(), 
										         listOfQuantity.toString(), priceCode, "USD",
										         priceIncludesValue, true, quoteUponRequest, basePriceName,tempCriteria,pricesPerUnit.toString(),priceGrids);*/	
							 
							    productExcelObj.setPriceGrids(priceGrids);
							    ////
							    List<ImprintMethod> imprintMethods=productConfigObj.getImprintMethods();
								if(CollectionUtils.isEmpty(imprintMethods)){
									// i have to keep unimprinted over here
									  List<ImprintMethod> imprintMethodList = new ArrayList<>();
									  ImprintMethod imprintMethodObj = new ImprintMethod();
									  imprintMethodObj.setAlias("Unimprinted");
									  imprintMethodObj.setType("Unimprinted");
									  imprintMethodList.add(imprintMethodObj);
									  productConfigObj.setImprintMethods(imprintMethodList);
								}
							    ////
							    
							 	productExcelObj.setProductConfigurations(productConfigObj);
							 //	if(Prod_Status = false){
							 	_LOGGER.info("Product Data : "
										+ mapperObj.writeValueAsString(productExcelObj));
							 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber ,batchId, environmentType);
							 	if(num ==1){
							 		numOfProductsSuccess.add("1");
							 	}else if(num == 0){
							 		numOfProductsFailure.add("0");
							 	}else{
							 		
							 	}
							 	//}
							 	_LOGGER.info("list size>>>>>>>"+numOfProductsSuccess.size());
							 	_LOGGER.info("Failure list size>>>>>>>"+numOfProductsFailure.size());
							 	repeatRows.clear();
								priceGrids = new ArrayList<PriceGrid>();
								listOfPrices = new StringBuilder();
							    listOfQuantity = new StringBuilder();
								productConfigObj = new ProductConfigurations();
								themeList = new ArrayList<Theme>();
								finalDimensionObj = new Dimension();
								 valuesList = new ArrayList<>();
								productKeywords = new ArrayList<String>();
								listOfProductionTime = new ArrayList<ProductionTime>();
								rushTime = new RushTime();
								listImprintLocation = new ArrayList<ImprintLocation>();
								listOfImprintMethods = new ArrayList<ImprintMethod>();
								listOfImages= new ArrayList<Image>();
								imprintSizeList =new ArrayList<ImprintSize>();
								ImprintSizevalue = new StringBuilder();
								ImprintSizevalue2 = new StringBuilder();
								size=new Size();
								colorList = new ArrayList<Color>();
								 dimensionValue = new StringBuilder();
								 dimensionUnits = new StringBuilder();
								 dimensionType = new StringBuilder();
								 priceIncludes = new StringBuilder();
								 imprintMethodUpchargeMap = new LinkedHashMap<>();
								 priceIncludesValue=new String();
								 priceIncludes = new StringBuilder();
								 imprintMethodUpchargeMap = new LinkedHashMap<>();
								 additionalClrRunChrgPrice = new StringBuilder();
							     additionalClrRunChrgCode = "";
							     additionalColorPriceVal = "";
						         additionalColorCode     = "";
						         pricesPerUnit=new StringBuilder();
						         //productSizePriceMap=new HashMap<String, String>();
						         pnumMap=new HashMap<String, String>();
						         colorSet=new HashSet<String>();
						         productPricePriceMap=new HashMap<String, String>();
						         firstValue="";
						         System.out.println("");
						 }
						 if(!productXids.contains(xid)){
						    	productXids.add(xid.trim());
						    	repeatRows.add(xid.trim());
						    }
						    existingApiProduct = postServiceImpl.getProduct(accessToken, xid=xid.replace("\t",""), environmentType);
						     if(existingApiProduct == null){
						    	 _LOGGER.info("Existing Xid is not available,product treated as new product");
						    	 productExcelObj = new Product();
						     }else{
						    	 productExcelObj = gillStudiosAttributeParser.getExistingProductData(existingApiProduct, existingApiProduct.getProductConfigurations());
						    	 productConfigObj=productExcelObj.getProductConfigurations();
						     }
					 }
				//}
				}else{
					if(productXids.contains(xid) && repeatRows.size() != 1){
						 if(isRepeateColumn(columnIndex+1)){
							 continue;
						 }
					}
				}

				switch (columnIndex + 1) {
			
				case 1://xid
					   productExcelObj.setExternalProductId(xid.trim());
					
					 break;
				
				case 2://ProductID
					
					 break;
				case 3://ItemNum
					 //String asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
					     asiProdNo=CommonUtility.getCellValueStrinOrInt(cell);
					     productExcelObj.setAsiProdNo(asiProdNo);		
					  break;
				case 4://Name
					 productName =  CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(productName)){
						 Size sizeTempObj=null;
					 //productName = CommonUtility.getStringLimitedChars(productName, 60);
					 try{
					 if(productName.contains("(") && productName.contains(")")){
						 basePriceName=CommonUtility.extractValueSpecialCharacter("(",")",productName);
						 basePriceName=basePriceName.trim();
						// Size sizeTempObj=productConfigObj.getSizes();
						  sizeTempObj=productConfigObj.getSizes();
						 List<Value> otherListTemp=new ArrayList<Value>();
						 if(sizeTempObj!=null){
							 OtherSize ohtObj=sizeTempObj.getOther();
							 if(ohtObj!=null){
						  otherListTemp=sizeTempObj.getOther().getValues();
							 }else{
								 sizeTempObj=new Size();
								 otherListTemp=new ArrayList<Value>();
							 }
						 }
						 else if(sizeTempObj==null){
							 sizeTempObj=new Size();
							 otherListTemp=new ArrayList<Value>();
						 }
						 Size sizeObjNew=gillStudiosAttributeParser.getSizes(basePriceName.trim(), sizeTempObj,otherListTemp);
						 criteriaFlag=true;
						 productConfigObj.setSizes(sizeObjNew);
						 if(productName.length()>60){
						 productName = CommonUtility.getStringLimitedChars(productName, 60);
						 }
					 }else{
						 if(productName.length()>60){
							 productName = CommonUtility.getStringLimitedChars(productName, 60);
							 }
						 basePriceName = productName;
						 basePriceName=basePriceName.trim();
					 }
					 
					 productExcelObj.setName(productName);
					 /*String temp=productExcelObj.getDescription();
					 if(StringUtils.isEmpty(temp)){
							productExcelObj.setDescription(productName);
					 }*/
					/* if(!StringUtils.isEmpty(colorValue)&&!StringUtils.isEmpty(productNumber)){
							productNumberMap.put(productNumber, colorValue);
						}*/
					 }
					 catch(Exception e){
						 _LOGGER.error("Error while processing name"+e.getLocalizedMessage() +"productName:"+productName+"basePriceName:"+basePriceName+"size obj:"+sizeTempObj);
					 }
					 
					 }
						break;
				case 5://CatYear(Not used)
					 CatYear=CommonUtility.getCellValueStrinOrInt(cell);
					
					
				    break;
					
				case 6://ExpirationDate / PriceConfirmedThru
					//String verified= CommonUtility.getCellValueStrinOrInt(cell);
					//if(verified.equalsIgnoreCase("True")){
					String priceConfimedThruString="2019-12-31T00:00:00";
					  productExcelObj.setPriceConfirmedThru(priceConfimedThruString);
					//}
					break;
					
				case 7: //  product status ,discontinued
					
					// ProductStatus=cell.getStringCellValue();
					// Prod_Status=cell.getBooleanCellValue();
					 break;
					
				case 8://Cat1Name // not used
					
					break;
					
				case 9: // Cat2Name // not used

					break;
					
				case 10: //Catalogs page number, Page1 needs to ignored
					/*String PageNO=CommonUtility.getCellValueStrinOrInt(cell);
					String value=null;
					//catalogsList = lookupServiceDataObj.getCatalog(accessToken);
					if(StringUtils.isEmpty("")){
						PageNO="";
					}
					if(CatYear.contains("2017")){
						catlogObj.setCatalogName("2017 Gill Line Catalog");
						catlogObj.setCatalogPage(PageNO);
						catalogList.add(catlogObj);
						productExcelObj.setCatalogs(catalogList);
					}
					*/

					break;
					
				case 11:  
						//Catalogs(Not used),Page2
					
					break;
					
				case 12:  //Description
					String description =CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(description)){
					description=description.replaceAll("™", "");
					description=description.replaceAll("®", "");
					description=description.replaceAll("soft touch", "");
					description=description.replaceAll("\"", "");
					description = CommonUtility.removeRestrictSymbols(description);
					description = CommonUtility.getStringLimitedChars(description, 800);
					productExcelObj.setDescription(description.trim());
					//Str.indexOf( 'o' )
					if(description.contains(".")){
						int postn=description.indexOf(".");
						String tempStr=description.substring(0, postn);
						if(tempStr.length()<=130){
							productExcelObj.setSummary(tempStr);
						}else{
							tempStr=tempStr.substring(0, 130);
							  productExcelObj.setSummary(tempStr);
						}
						}else if(!StringUtils.isEmpty(productExcelObj.getName())){
							String tempSum=productExcelObj.getName();
							if(tempSum.length()<=130){
								productExcelObj.setSummary(tempSum);
							}else{
								tempSum=tempSum.substring(0, 130);
								  productExcelObj.setSummary(tempSum);
							}
						
					}
					}else{
						String temp=productExcelObj.getName();
						 if(!StringUtils.isEmpty(temp)){
								productExcelObj.setDescription(temp);
								if(temp.length()<=130){
									productExcelObj.setSummary(temp);
								}else{
									  temp=temp.substring(0, 130);
									  productExcelObj.setSummary(temp);
								}
						 }
					}			
					break;
				
				case 13:  // keywords
					String productKeyword =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(productKeyword)){
					String productKeywordArr[] = productKeyword.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
					for (String string : productKeywordArr) {
						productKeywords.add(string);
					}
					productExcelObj.setProductKeywords(productKeywords);
					}
					break;

				case 14: //Colors
				String colorValue= CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(colorValue)){
						//pnumMap.put(asiProdNo, colorValue); //og value
						colorValue=colorValue.replaceAll("\\|",",");
						String[] colors =getValuesOfArray(colorValue, ",");
						if(colors.length>1){
							for (String string : colors) {
								colorSet.add(string.trim());
								// done changes on 22nd JAn 2018 for revised colors
								String tempNUmVal=string;
								if(tempNUmVal.contains("/")){
									tempNUmVal=tempNUmVal.replace("/", "-");
								}
								pnumMap.put(asiProdNo, tempNUmVal);
							}
						}else{
						colorSet.add(colorValue);
						String tempNUmVal=colorValue;
						if(tempNUmVal.contains("/")){
							tempNUmVal=tempNUmVal.replace("/", "-");
						}
						pnumMap.put(asiProdNo, tempNUmVal);
						}
						//colorList=gillStudiosAttributeParser.getProductColors(colorValue);
						//productConfigObj.setColors(colorList);
					}	
					break;
					
				case 15: // Themes
					 themeValue= CommonUtility.getCellValueStrinOrInt(cell);
					 List<Theme> themeListTemp = productConfigObj.getThemes();
					 if(CollectionUtils.isEmpty(themeList)){
					 themeList = new ArrayList<Theme>();
					 }
					 if(!StringUtils.isEmpty(themeValue)){
						 themeList = gillStudiosAttributeParser.getProductTheme(themeValue);
						 if(!CollectionUtils.isEmpty(themeList)){
							 if(!CollectionUtils.isEmpty(themeListTemp)){
								 themeList.addAll(themeListTemp);
								 }
						 productConfigObj.setThemes(themeList);
						 }
					 }
					break;
					
				case 16://Dimension1//size --  value
						String dimensionValue1=CommonUtility.getCellValueStrinOrInt(cell);
						if(!StringUtils.isEmpty(dimensionValue1) && !dimensionValue1.equals("0")){
							criteriaFlag=false;
						   dimensionValue.append(dimensionValue1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
						 
					   }
					
					break;
				case 17: //  Dimension1Units// size -- Unit
					  String dimensionUnits1 = CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(dimensionUnits1) && !dimensionUnits1.equals("0")){
						 dimensionUnits.append(dimensionUnits1.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
					  break;
				
				case 18: //Dimension1Type//size -- type
					String dimensionType1 =CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(dimensionType1) && !dimensionType1.equals("0")){
						dimensionType.append(dimensionType1).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					  
					 
					break;
				
				 case 19: //Dimension2 // size
					 String dimensionValue2 =CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(dimensionValue2) && !dimensionValue2.equals("0")){
						 criteriaFlag=false;
						 dimensionValue.append(dimensionValue2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					 }
				
					break;
					
				case 20:  //Dimension2Units //size
					String dimensionUnits2 =CommonUtility.getCellValueStrinOrInt(cell);
					
					if(!StringUtils.isEmpty(dimensionUnits2) && !dimensionUnits2.equals("0")){
						dimensionUnits.append(dimensionUnits2.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					break;
					
				case 21: //Dimension2Type //size
					String  dimensionType2 = CommonUtility.getCellValueStrinOrInt(cell);

					if(!StringUtils.isEmpty(dimensionType2) && !dimensionType2.equals("0")){
						dimensionType.append(dimensionType2).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}
					
					break;
					
				case 22: //Dimension3 // size
					String dimensionValue3  =CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensionValue3) && !dimensionValue3.equals("0")){
						criteriaFlag=false;
						dimensionValue.append(dimensionValue3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else{
						dimensionValue=dimensionValue.append("");
					}
					break;
					
					
				case 23: //Dimension3Units // size
					String dimensionUnits3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensionUnits3) && !dimensionUnits3.equals("0")){
						 dimensionUnits.append(dimensionUnits3.trim()).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else
					{
						dimensionUnits=dimensionUnits.append("");
					}
				   break;
					
				case 24: //Dimension3Type // size
					String dimensionType3 = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dimensionType3) && !dimensionType3.equals("0")){
						dimensionType.append(dimensionType3).append(ApplicationConstants.CONST_DIMENSION_SPLITTER);
					}else
					{
						dimensionType=dimensionType.append("");
					}
					

					
				   break;
				   
				case 25:  // Quantities
				case 26: 
				case 27: 
				case 28: 
				case 29:
				case 30:
					try{
						if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							quantity = cell.getStringCellValue();
					         if(!StringUtils.isEmpty(quantity) && !quantity.equals("0")){
					        	 listOfQuantity.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							int quantity1 = (int)cell.getNumericCellValue();
					         if(!StringUtils.isEmpty(quantity1) && quantity1 !=0){
					        	 listOfQuantity.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else{
						}
					}catch (Exception e) {
						_LOGGER.info("Error in base price Quantity field "+e.getMessage());
					}
					
					   	break;
				case 31:  // prices --list price
				case 32:
				case 33:
				case 34:
				case 35:
				case 36:
				 try{
					 if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							quantity = cell.getStringCellValue();
					         if(!StringUtils.isEmpty(quantity)&& !quantity.equals("0")){
					        	 listOfPrices.append(quantity).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							double quantity1 = (double)cell.getNumericCellValue();
					         if(!StringUtils.isEmpty(quantity1)){
					        	 listOfPrices.append(quantity1).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
					         }
						}else{
						}  
				 }catch (Exception e) {
					_LOGGER.info("Error in base price prices field "+e.getMessage());
				}
					
					    break; 
				case 37: //PrCode// price code -- discount
					    
				priceCode =  CommonUtility.getCellValueStrinOrInt(cell);
					     break;
				case 38:       // PiecesPerUnit1,2,3,4,5,6 pricesPerUnit
				case 39:
				case 40:
				case 41:
				case 42:
				case 43:
					try{
						String priceUnit = null;
						priceUnit=CommonUtility.getCellValueStrinOrDecimal(cell);
						if(!StringUtils.isEmpty(priceUnit)&& !priceUnit.equals("0")){
							priceUnit=priceUnit.replace(".0", "");
							pricesPerUnit.append(priceUnit).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				         }
					}catch (Exception e) {
						_LOGGER.info("Error in pricePerUnit field "+e.getMessage());
					} 
					      break;
				case 44://QuoteUponRequest
					     quoteUponRequest =  CommonUtility.getCellValueStrinOrInt(cell);
					      break;
				case 45:  // priceIncludeClr
					    
					      priceIncludes.append(cell.getStringCellValue()).append(" ");
					     break;
				case 46: // priceIncludeSide
						
						priceIncludes.append(cell.getStringCellValue()).append(" ");
						
						break;
				case 47: // priceIncludeLoc
						priceIncludes.append(CommonUtility.getCellValueStrinOrInt(cell));
						int PriceIncludeLength=priceIncludes.length();
						if(PriceIncludeLength>100){
							
						priceIncludesValue=	priceIncludes.toString().substring(0,100);
						}else
						{
							priceIncludesValue=priceIncludes.toString();
						}
						
						break;
						
				case 48:   //Set-up Charge 
					String setupChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(setupChargePrice) && !setupChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("setupCharge", setupChargePrice);
					}
				  break;
				case 49://setup charge code					
					String setUpchargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(setUpchargeCode) && !setUpchargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("setupCharge");
						priceVal = priceVal+"_"+setUpchargeCode;
						imprintMethodUpchargeMap.put("setupCharge", priceVal);
					}
				  break;
				case 50://screen charge
					String screenChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(screenChargePrice) && !screenChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("screenCharge", screenChargePrice);
					}
							break;
				case 51://screen charge code
					String screenchargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(screenchargeCode) && !screenchargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("screenCharge");
						priceVal = priceVal+"_"+screenchargeCode;
						imprintMethodUpchargeMap.put("screenCharge", priceVal);
					}
							break;
				case 52:// plate charge
					String plateChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(plateChargePrice) && !plateChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("plateCharge", plateChargePrice);
					}

							break;
				case 53://plateCharge code
					String plateChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(plateChargeCode) && !plateChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("plateCharge");
						priceVal = priceVal+"_"+plateChargeCode;
						imprintMethodUpchargeMap.put("plateCharge", priceVal);
					}
							break;
				case 54:// dieCharge
					String dieChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(dieChargePrice) && !dieChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("dieCharge", dieChargePrice);
					}
							break;
				case 55:// dia Charge code
					String diaChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(diaChargeCode) && !diaChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("dieCharge");
						priceVal = priceVal+"_"+diaChargeCode;
						imprintMethodUpchargeMap.put("dieCharge", priceVal);
					}
							break;
				case 56://tooling charge
					String toolingChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(toolingChargePrice) && !toolingChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("toolingCharge", toolingChargePrice);
					}
							break;
				case 57:// tooling charge code
					String toolingChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(toolingChargeCode) && !toolingChargeCode.equals("0")){
						String priceVal = imprintMethodUpchargeMap.get("toolingCharge");
						priceVal = priceVal+"_"+toolingChargeCode;
						imprintMethodUpchargeMap.put("toolingCharge", priceVal);
					}
							break;
				case 58://repeate charge
					/*String repeateChargePrice = CommonUtility.getCellValueStrinOrInt(cell);
					if(!repeateChargePrice.equals("0")){
						imprintMethodUpchargeMap.put("repeateCharge", repeateChargePrice);
					}*/
					// no need to process this column since there is no vaild data
							break; 
				case 59://repeate charge code
							/*String repeateChargeCode = CommonUtility.getCellValueStrinOrInt(cell);
							if(!repeateChargeCode.equals("0")){
								String priceVal = imprintMethodUpchargeMap.get("repeateCharge");
								priceVal = priceVal+"_"+repeateChargeCode;
								imprintMethodUpchargeMap.put("repeateCharge", priceVal);
							}*/
					// no need to process this column since there is no vaild data
						break;
				case 60: // additioNal color
				    additionalColorPriceVal = CommonUtility.getCellValueStrinOrInt(cell);
					 break;
			case 61: // additional color code
				additionalColorCode =  CommonUtility.getCellValueStrinOrInt(cell);
					break;
			case 62: 	
			case 63:							
			case 64:	
			case 65:	
			case 66:	
			case 67:
			  String colorCharge = CommonUtility.getCellValueDouble(cell);
			  if(!colorCharge.equalsIgnoreCase("0.00") || !colorCharge.equalsIgnoreCase("0.0") ||
					  !colorCharge.equalsIgnoreCase("0"))
			  additionalClrRunChrgPrice.append(colorCharge).append(ApplicationConstants.PRICE_SPLITTER_BASE_PRICEGRID);
				break;
			case 68:// additional run charge code
			  String additionalClrRunCode =  CommonUtility.getCellValueStrinOrInt(cell);
			      if(!StringUtils.isEmpty(additionalClrRunCode)){
				       additionalClrRunChrgCode = additionalClrRunCode;
			      }    	
			break;  
				case 69:
					       break;
				case 70://IsEnvironmentallyFriendly

				String IsEnvironmentallyFriendly =  CommonUtility.getCellValueStrinOrInt(cell);
				boolean flag=true;
					if(IsEnvironmentallyFriendly.equalsIgnoreCase("true"))			
					{ 
						List<Theme> themeListTempp = productConfigObj.getThemes();
						 if(!CollectionUtils.isEmpty(themeListTempp)){
							 for (Theme theme : themeListTempp) {
								if(theme.getName().toUpperCase().contains("ECO")){
									flag=false;
								}
							}
						 }
						///
						 if(CollectionUtils.isEmpty(themeList)){
							 themeList = new ArrayList<Theme>();
							 }
						 if(flag){
						Theme themeObj1 = new Theme();
						themeObj1.setName("ECO & ENVIRONMENTALLY FRIENDLY");
						themeList.add(themeObj1);
						 if(!CollectionUtils.isEmpty(themeListTempp)){
							 themeList.addAll(themeListTempp);
						 }
						 }
					}
					break;
				case 71://IsNewProd

					break;
				case 72://NotSuitable

					break;
					
				
				case 73://Exclusive

					break;
					
				case 74://Hazardous
					String hazardous =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(hazardous)){
					if(hazardous.equalsIgnoreCase("true"))			
					{ 
						productExcelObj.setHazmat("TRUE");
					}
					}
					break;
					
				case 75://OfficiallyLicensed

					break;
				case 76://IsFood

					break;
				case 77://IsClothing

					break;
				case 78: // Imprint size1
					 FirstImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize1.trim()) && !FirstImprintsize1.equals("0")){
					 ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize1).append(" ");
					
					 }
					    break;
					    
				case 79: //// Imprint size1 unit
					FirstImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintunit1.trim()) && !FirstImprintunit1.equals("0")){
					FirstImprintunit1=GillStudiosLookupData.Dimension1Units.get(FirstImprintunit1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit1).append(" ");
					 }	 
					   	break;
					   	
				case 80:   // Imprint size1 Type
					FirstImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
				   if(!StringUtils.isEmpty(FirstImprinttype1.trim()) && !FirstImprinttype1.equals("0")){
					FirstImprinttype1=GillStudiosLookupData.Dimension1Type.get(FirstImprinttype1);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype1).append(" ").append("x");
				   }
						break;
						
				  
				case 81: // // Imprint size2
					FirstImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(FirstImprintsize2.trim()) && !FirstImprintsize2.equals("0")){
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintsize2).append(" ");
					 }

					  	break;
					  	
				case 82:	// Imprint size2 Unit
					FirstImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(FirstImprintunit2.trim()) && !FirstImprintunit2.equals("0")){
					FirstImprintunit2=GillStudiosLookupData.Dimension1Units.get(FirstImprintunit2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprintunit2).append(" ");
				    }
					    break;
					    
				case 83: // Imprint size2 Type
					FirstImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(FirstImprinttype2.trim()) && !FirstImprinttype2.equals("0")){
					FirstImprinttype2=GillStudiosLookupData.Dimension1Type.get(FirstImprinttype2);
					ImprintSizevalue=ImprintSizevalue.append(FirstImprinttype2).append(" ");
				    }
					break;
					  	
				case 84:  // Imprint location
					
					 imprintLocation =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(imprintLocation) && !imprintLocation.equals("0")){
						ImprintLocation locationObj = new ImprintLocation();
						locationObj.setValue(imprintLocation);
						listImprintLocation.add(locationObj);
					}
					 break;
					 
					 
				case 85:  // Second Imprintsize1
					
					SecondImprintsize1=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize1.trim()) && !SecondImprintsize1.equals("0")){
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprintsize1).append(" ");
				    }
					   	break;
					   	
				case 86:  // Second Imprintsize1 unit
					SecondImprintunit1=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintunit1.trim()) && !SecondImprintunit1.equals("0")){
					SecondImprintunit1=GillStudiosLookupData.Dimension1Units.get(SecondImprintunit1);
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprintunit1).append(" ");

					}
					
						break;
						
				case 87:  // Second Imprintsize1 type
					SecondImprinttype1=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprinttype1.trim())  && !SecondImprinttype1.equals("0")){
					SecondImprinttype1=GillStudiosLookupData.Dimension1Type.get(SecondImprinttype1);
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprinttype1).append(" ").append("x");

					}
				
					  break;
					  
				case 88: // Second Imprintsize2
					SecondImprintsize2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintsize2.trim())  && !SecondImprintsize2.equals("0")){
				    ImprintSizevalue2=ImprintSizevalue2.append(SecondImprintsize2).append(" ");
				    
				    }

					
					break;
					
				case 89: //Second Imprintsize2 Unit
					SecondImprintunit2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprintunit2.trim()) && !SecondImprintunit2.equals("0")){
					SecondImprintunit2=GillStudiosLookupData.Dimension1Units.get(SecondImprintunit2);
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprintunit2).append(" ");

					}

					break;
					
				case 90: // Second Imprintsize2 type	
					SecondImprinttype2=CommonUtility.getCellValueStrinOrInt(cell);
				    if(!StringUtils.isEmpty(SecondImprinttype2.trim()) && !SecondImprinttype2.equals("0")){
					SecondImprinttype2=GillStudiosLookupData.Dimension1Type.get(SecondImprinttype2);
					ImprintSizevalue2=ImprintSizevalue2.append(SecondImprinttype2).append(" ");

					}
					
				/*	  ImprintSizevalue=append(FirstImprintunit1).
					  append(" ").append(FirstImprinttype1).append("x").append(FirstImprintsize2).append(" ").
					  append(FirstImprintunit2).append(" ").append(FirstImprinttype2).append(",").
					  append(SecondImprintsize1).append(" ").append(SecondImprintunit1).
					  append(" ").append(SecondImprinttype1).append("x").append(SecondImprintsize2).append(" ").
					  append(SecondImprintunit2).append(" ").append(SecondImprinttype2);
					*/
					  break;
					  
				case 91: // Second Imprint location
					String imprintLocation2 =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(imprintLocation2) && !imprintLocation.equals("0")){
						ImprintLocation locationObj2 = new ImprintLocation();
						locationObj2.setValue(imprintLocation2);
						listImprintLocation.add(locationObj2);
					}
					break;
				case 92: // DecorationMethod
					 decorationMethod =  CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(decorationMethod)){
					listOfImprintMethods = gillStudiosImprintMethodParser.getImprintMethodValues(decorationMethod,listOfImprintMethods);
					 } 
					break; 
					   
				case 93: //NoDecoration
					String noDecoration = CommonUtility.getCellValueStrinOrInt(cell);//cell.getStringCellValue();
					if(!StringUtils.isEmpty(noDecoration)){
					if(noDecoration.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						firstValue="PRESENT";
						listOfImprintMethods = gillStudiosImprintMethodParser.getImprintMethodValues(noDecoration,
                                listOfImprintMethods);
					}else{
					firstValue="";	
					}
					}
					 break;
				case 94: //NoDecorationOffered
					String noDecorationOffered =  CommonUtility.getCellValueStrinOrInt(cell);//cell.getStringCellValue();
					if(!StringUtils.isEmpty(noDecorationOffered) && !firstValue.equals("PRESENT")){
					if(noDecorationOffered.equalsIgnoreCase(ApplicationConstants.CONST_STRING_TRUE)){
						listOfImprintMethods = gillStudiosImprintMethodParser.getImprintMethodValues(noDecorationOffered,
                                listOfImprintMethods);
					}
					}
					 break;
				case 95: //NewPictureURL
					/*String ImageValue1=cell.getStringCellValue();
					 Image image = new Image();
					 if(!StringUtils.isEmpty(ImageValue1)){
					// Image image = new Image();
				      image.setImageURL(ImageValue1);
				      image.setIsPrimary(true);
				      image.setRank(1);
				      listOfImages.add(image);
					  }*/
					break;
				case 96:  //NewPictureFile  -- not used
					break;
				case 97: //ErasePicture -- not used
					break;
					
				case 98: //NewBlankPictureURL
					/*String ImageValue2=cell.getStringCellValue();
					if(!StringUtils.isEmpty(ImageValue2))
					{
						Image	  image = new Image();
					      image.setImageURL(ImageValue2);
					      image.setIsPrimary(false);
					      image.setRank(2);
					      listOfImages.add(image);
					}*/
				
					break;
				case 99: //NewBlankPictureFile -- not used
					break;
				case 100://EraseBlankPicture  -- not used
					break;
			/*		 
				case 99: //PicExists   -- not used
					break;*/
				case 101: //NotPictured  -- not used
					break;
				case 102: //MadeInCountry
					
					String madeInCountry =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(madeInCountry)){
						List<Origin> listOfOrigin = gillStudiosAttributeParser.getOriginValues(madeInCountry);
						productConfigObj.setOrigins(listOfOrigin);
					}
					break;
					
				case 103:// AssembledInCountry
			     String additionalProductInfo =  CommonUtility.getCellValueStrinOrInt(cell);
			     if(!StringUtils.isEmpty(additionalProductInfo))
			       {
			    	productExcelObj.setAdditionalProductInfo(additionalProductInfo); 
			       }
				
					break;
				case 104: //DecoratedInCountry
					String additionalImprintInfo =  CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(additionalImprintInfo))
					   {
						 productExcelObj.setAdditionalImprintInfo(additionalImprintInfo);
					   }
					
					break;
				case 105: //ComplianceList  -- No data
					String complnceValuet= CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(complnceValuet))
					   {
				    	complianceList.add(complnceValuet);
				    	productExcelObj.setComplianceCerts(complianceList);
					   }
					break;
					
				case 106://ComplianceMemo  -- No data
					String productDataSheet= CommonUtility.getCellValueStrinOrInt(cell);
					 if(!StringUtils.isEmpty(productDataSheet))
					   {
						 productExcelObj.setProductDataSheet(productDataSheet);
					   }
					break;
					
				case 107: //ProdTimeLo
				   prodTimeLo = CommonUtility.getCellValueStrinOrInt(cell);
					/*ProductionTime productionTime = new ProductionTime();
					
					productionTime.setBusinessDays(prodTimeLo);
					listOfProductionTime.add(productionTime);*/
					break;
				case 108: //ProdTimeHi
					String prodTimeHi = CommonUtility.getCellValueStrinOrInt(cell);
					ProductionTime productionTime = new ProductionTime();
				

					if(prodTimeLo.equalsIgnoreCase(prodTimeHi))
					{
						productionTime.setBusinessDays(prodTimeHi);
						listOfProductionTime.add(productionTime);
					}
					else
					{
						String prodTimeTotal="";
						prodTimeTotal=prodTimeTotal.concat(prodTimeLo).concat("-").concat(prodTimeHi);
						productionTime.setBusinessDays(prodTimeTotal);
						listOfProductionTime.add(productionTime);
					
					}
					break;
				case 109://RushProdTimeLo
					String rushProdTimeLo  =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!rushProdTimeLo.equals(ApplicationConstants.CONST_STRING_ZERO)){
						rushTime = gillStudiosAttributeParser.getRushTimeValues(rushProdTimeLo, rushTime);
					}
					
					 break; 	 
				case 110://RushProdTimeH
					String rushProdTimeH  =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!rushProdTimeH.equals(ApplicationConstants.CONST_STRING_ZERO)){
						rushTime = gillStudiosAttributeParser.getRushTimeValues(rushProdTimeH, rushTime);
					}
					break;
					
				case 111://Packaging
				
					String pack  =  CommonUtility.getCellValueStrinOrInt(cell);
					if(!StringUtils.isEmpty(pack)){
					List<Packaging> listOfPackaging = gillStudiosAttributeParser.getPackageValues(pack);
					productConfigObj.setPackaging(listOfPackaging);
					}
					break;
					
				case 112: //CartonL
					 cartonL  = CommonUtility.getCellValueStrinOrInt(cell);
					
					break;
				case 113://CartonW
					cartonW  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
	
				case 114://CartonH
					cartonH  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 115: //WeightPerCarton
					weightPerCarton  =CommonUtility.getCellValueStrinOrInt(cell);
					break;
				case 116: //UnitsPerCarton
					unitsPerCarton  = CommonUtility.getCellValueStrinOrInt(cell);
					break;
					
				case 117: //ShipPointCountry

					break;
					
				case 118: //ShipPointZip
					
					break;
					
				case 119: //Comment // after review on 12 june
					String additionalProductValue=CommonUtility.getCellValueStrinOrInt(cell);
					productExcelObj.setAdditionalProductInfo(additionalProductValue);
					break;
					
				case 120: //Verified
					/*String verified= CommonUtility.getCellValueStrinOrInt(cell);
					if(verified.equalsIgnoreCase("True")){
					String priceConfimedThruString="2017-12-31T00:00:00";
					  productExcelObj.setPriceConfirmedThru(priceConfimedThruString);
					}*/
					break;
			
				case 121: //UpdateInventory
					
					break;
				
				case 122: //InventoryOnHand
					
					break;
					
				case 123: //InventoryOnHandAdd
					break;
					
				case 124: //InventoryMemo
				break;
			
			}  // end inner while loop
					 
		}// set  product configuration objects
		 // end inner while loop
			
			//ShippingEstimate // i have to work on this thing as well for empty ship obj
			ShippingEstimate shipping = gillStudiosAttributeParser.getShippingEstimateValues(cartonL, cartonW,
					                               cartonH, weightPerCarton, unitsPerCarton);
			if(shipping.getDimensions()!=null || shipping.getNumberOfItems()!=null || shipping.getWeight()!=null){
				productConfigObj.setShippingEstimates(shipping);	
			}
			
			
			//ImprintLocation
			if(!CollectionUtils.isEmpty(listImprintLocation)){
			productConfigObj.setImprintLocation(listImprintLocation);
			}
			//ImprintMethods
			if(!CollectionUtils.isEmpty(listOfImprintMethods)){
			productConfigObj.setImprintMethods(listOfImprintMethods);
			}
			//theme
			if(!CollectionUtils.isEmpty(themeList)){
				productConfigObj.setThemes(themeList);
			}
			//RushTime
			productConfigObj.setRushTime(rushTime);
			//ProductionTime
			if(!CollectionUtils.isEmpty(listOfProductionTime)){
			productConfigObj.setProductionTime(listOfProductionTime);
			}
			//sizes
			String DimensionRef=null;
			DimensionRef=dimensionValue.toString();
			if(!StringUtils.isEmpty(DimensionRef)){
			criteriaFlag=false;
			valuesList =gillStudiosAttributeParser.getValues(dimensionValue.toString(),
	                dimensionUnits.toString(), dimensionType.toString());
			
	        finalDimensionObj.setValues(valuesList);	
			size.setDimension(finalDimensionObj);
			productConfigObj.setSizes(size);
			}
			//ImprintSize	
			if(!StringUtils.isEmpty(ImprintSizevalue2.toString())){
				ImprintSizevalue=ImprintSizevalue.append("___").append(ImprintSizevalue2);
			}
			if(!StringUtils.isEmpty(ImprintSizevalue)){
			imprintSizeList=gillStudiosAttributeParser.getimprintsize(ImprintSizevalue);
			}
			// imprintSizeList.removeAll(Collections.singleton(null));
			if(!CollectionUtils.isEmpty(imprintSizeList)){
			productConfigObj.setImprintSize(imprintSizeList);
			}
			//impmtd upchrg
			if(!imprintMethodUpchargeMap.isEmpty()){
		    	priceGrids = gillStudiosAttributeParser.getImprintMethodUpcharges(imprintMethodUpchargeMap,
	                    listOfImprintMethods, priceGrids);
		    }
			// additional colors & upcgrg
			List<AdditionalColor> additionalColorList = null;
			if(!StringUtils.isEmpty(additionalClrRunChrgCode)){
				 additionalColorList = gillStudiosAttributeParser.getAdditionalColor("Additional Color");
					priceGrids = gillStudiosAttributeParser.getAdditionalColorRunUpcharge(additionalClrRunChrgCode,listOfQuantity.toString(),
							   additionalClrRunChrgPrice.toString(), priceGrids,"Run Charge");
					if(!CollectionUtils.isEmpty(additionalColorList)){
					productConfigObj.setAdditionalColors(additionalColorList);
					}
			}
			if(!StringUtils.isEmpty(additionalColorPriceVal) && !additionalColorPriceVal.equals("0")){
				if(additionalColorList == null){
					additionalColorList = gillStudiosAttributeParser.getAdditionalColor("Additional Color");
					productConfigObj.setAdditionalColors(additionalColorList);
				}
				priceGrids = gillStudiosAttributeParser.getAdditionalColorUpcharge(additionalColorCode,
						   additionalColorPriceVal, priceGrids,"Add. Color Charge");
			}
			productExcelObj.setPriceType("L");
			if( listOfPrices != null && !listOfPrices.toString().isEmpty()){
				String tempPriceUnit=pricesPerUnit.toString();
				if(StringUtils.isEmpty(tempPriceUnit)){
					tempPriceUnit="1___1___1___1___1___1___1___1___1___1";
				}
				String tempCriteria="";
				if(criteriaFlag){
					tempCriteria="Size";
				}else{
					basePriceName="AAAAA";
					tempCriteria="BBBBB";
				}
				
				productPricePriceMap.put(basePriceName, listOfPrices.toString()+"@@@@@"+listOfQuantity.toString()+"@@@@@"+priceCode
						+"@@@@@"+priceIncludesValue+"@@@@@"+quoteUponRequest+"@@@@@"+tempCriteria+"@@@@@"+pricesPerUnit.toString());
				
				
				/*priceGrids = gillStudiosPriceGridParser.getPriceGrids(listOfPrices.toString(), 
						         listOfQuantity.toString(), priceCode, "USD",
						         priceIncludesValue, true, quoteUponRequest, basePriceName,tempCriteria,pricesPerUnit.toString(),priceGrids);*/	
			}
			 	productExcelObj.setPriceGrids(priceGrids);
			 	criteriaFlag=false;
			 	listOfPrices=new StringBuilder();
			 	listOfQuantity=new StringBuilder();
			 	pricesPerUnit=new StringBuilder();
			 	 imprintMethodUpchargeMap = new LinkedHashMap<>();
				 additionalClrRunChrgPrice = new StringBuilder();
				 themeList = new ArrayList<Theme>();
				 listOfProductionTime = new ArrayList<ProductionTime>();
				 listImprintLocation = new ArrayList<ImprintLocation>();
				 listOfImprintMethods = new ArrayList<ImprintMethod>();
				 pricesPerUnit=new StringBuilder();
			     additionalClrRunChrgCode = "";
			     additionalColorPriceVal = "";
		         additionalColorCode     = "";
			}catch(Exception e){
			_LOGGER.error("Error while Processing ProductId and cause :"+productExcelObj.getExternalProductId() +" "+e.getMessage() +"at column+1="+columnIndex);		 
		}
		}
		workbook.close();	
		//productExcelObj.setPriceGrids(priceGrids);
		
		 if(!CollectionUtils.isEmpty(productPricePriceMap) ){
			 int mapSize=productPricePriceMap.size();
		 Iterator mapItr = productPricePriceMap.entrySet().iterator();
		    while (mapItr.hasNext()) {
		    	
		        Map.Entry values = (Map.Entry)mapItr.next();
		        String basePRIC= values.getKey().toString();
		        String mapValue	=values.getValue().toString();
		    	String mapValueArr[]=mapValue.split("@@@@@");
		    	
		    	String listOfPric=mapValueArr[0];//listOfPrices
		    	String listOfQuan=mapValueArr[1];//listOfQuantity
		    	String priceCD=mapValueArr[2];//priceCode
		    	String priceINC=mapValueArr[3];//priceIncludesValue
		    	String quR=mapValueArr[4];//quoteUponRequest
		    	String tempCRI=mapValueArr[5];//tempCriteria
		    	String priceUNI=mapValueArr[6];//pricesPerUnit
		    	
		    	//basePriceName="AAAAA";
		    	if(basePRIC.equals("AAAAA")){
		    		basePRIC="";
		    	}
				//tempCriteria="BBBBB";
		    	if(tempCRI.equals("BBBBB")){
		    		tempCRI="";
		    	}
		    	///////////////jan 22 2018
		    	if(mapSize==1){
		    		priceGrids = gillStudiosPriceGridParser.getPriceGrids(listOfPric,listOfQuan, 
		    				priceCD,ApplicationConstants.CONST_STRING_CURRENCY_USD,
		    				priceINC,ApplicationConstants.CONST_BOOLEAN_TRUE, ApplicationConstants.CONST_STRING_FALSE, 
							"","",priceUNI,priceGrids);
		    		
		    		/*(String listOfPrices,
		    			    String listOfQuan, String discountCodes,
		    				String currency, String priceInclude, boolean isBasePrice,
		    				String qurFlag, String priceName, String criterias,String priceUnitArr,
		    				List<PriceGrid> existingPriceGrid)*/
		    		
		    	}else{
		    	priceGrids = gillStudiosPriceGridParser.getPriceGrids(listOfPric, 
		    			listOfQuan, priceCD, "USD",
		    			priceINC, true, quR, basePRIC,tempCRI,priceUNI,priceGrids);
		    	}
		    	//////////////
		    }
		 }
		 if(!CollectionUtils.isEmpty(pnumMap) && pnumMap.size()>1){
				pnumberList=gillStudiosAttributeParser.getProductNumer(pnumMap);
				productExcelObj.setProductNumbers(pnumberList);
			}
		 
		 if(!CollectionUtils.isEmpty(colorSet) ){
			 for (String colorValue : colorSet) {
					colorList=gillStudiosAttributeParser.getProductColors(colorValue,colorList);
			}
			 productConfigObj.setColors(colorList);
		 }
		productExcelObj.setPriceGrids(priceGrids);
		 ////
	    List<ImprintMethod> imprintMethods=productConfigObj.getImprintMethods();
		if(CollectionUtils.isEmpty(imprintMethods)){
			// i have to keep unimprinted over here
			  List<ImprintMethod> imprintMethodList = new ArrayList<>();
			  ImprintMethod imprintMethodObj = new ImprintMethod();
			  imprintMethodObj.setAlias("Unimprinted");
			  imprintMethodObj.setType("Unimprinted");
			  imprintMethodList.add(imprintMethodObj);
			  productConfigObj.setImprintMethods(imprintMethodList);
		}
	    ////
		productExcelObj.setProductConfigurations(productConfigObj);
	
		 	_LOGGER.info("Product Data : "
					+ mapperObj.writeValueAsString(productExcelObj));
		 	//if(Prod_Status = false){
		 	productExcelObj.setPriceType("L");
		 	int num = postServiceImpl.postProduct(accessToken, productExcelObj,asiNumber,batchId, environmentType);
		 	if(num ==1){
		 		numOfProductsSuccess.add("1");
		 	}else if(num == 0){
		 		numOfProductsFailure.add("0");
		 	}else{
		 		
		 	}
		 //	}
		 	_LOGGER.info("list size>>>>>>"+numOfProductsSuccess.size());
		 	_LOGGER.info("Failure list size>>>>>>"+numOfProductsFailure.size());
	       finalResult = numOfProductsSuccess.size() + "," + numOfProductsFailure.size();
	       productDaoObj.saveErrorLog(asiNumber,batchId);
		    
		    priceGrids = new ArrayList<PriceGrid>();
			listOfPrices = new StringBuilder();
		    listOfQuantity = new StringBuilder();
			productConfigObj = new ProductConfigurations();
			themeList = new ArrayList<Theme>();
			finalDimensionObj = new Dimension();
			 valuesList = new ArrayList<>();
			productKeywords = new ArrayList<String>();
			listOfProductionTime = new ArrayList<ProductionTime>();
			rushTime = new RushTime();
			listImprintLocation = new ArrayList<ImprintLocation>();
			listOfImprintMethods = new ArrayList<ImprintMethod>();
			listOfImages= new ArrayList<Image>();
			imprintSizeList =new ArrayList<ImprintSize>();
			size=new Size();
			colorList = new ArrayList<Color>();
			ImprintSizevalue = new StringBuilder();
			//DimensionRef=null;
			 dimensionValue = new StringBuilder();
			 dimensionUnits = new StringBuilder();
			 dimensionType = new StringBuilder();
			 priceIncludesValue=new String();
			 priceIncludes = new StringBuilder();
			 imprintMethodUpchargeMap = new LinkedHashMap<>();
			 additionalClrRunChrgPrice = new StringBuilder();
			 pricesPerUnit=new StringBuilder();
		     additionalClrRunChrgCode = "";
		     additionalColorPriceVal = "";
	         additionalColorCode     = "";
	         pnumMap=new HashMap<String, String>();
	         colorSet=new HashSet<String>();
	         firstValue="";
	         productPricePriceMap=new HashMap<String, String>();
	         repeatRows.clear();
	       return finalResult;
		}catch(Exception e){
			_LOGGER.error("Error while Processing excel sheet "+e.getMessage());
			return finalResult;
		}finally{
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet "+e.getMessage());
	
			}
				_LOGGER.info("Complted processing of excel sheet ");
				_LOGGER.info("Total no of product:"+numOfProductsSuccess.size() );
		}
		
	}

	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}

	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
	}

	public ProductDao getProductDaoObj() {
		return productDaoObj;
	}

	public void setProductDaoObj(ProductDao productDaoObj) {
		this.productDaoObj = productDaoObj;
	}

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public LookupRestService getLookupRestServiceObj() {
		return lookupRestServiceObj;
	}

	public void setLookupRestServiceObj(LookupRestService lookupRestServiceObj) {
		this.lookupRestServiceObj = lookupRestServiceObj;
	}

	public GillStudiosAttributeParser getGillStudiosAttributeParser() {
		return gillStudiosAttributeParser;
	}

	public void setGillStudiosAttributeParser(
			GillStudiosAttributeParser gillStudiosAttributeParser) {
		this.gillStudiosAttributeParser = gillStudiosAttributeParser;
	}

	public GillStudiosPriceGridParser getGillStudiosPriceGridParser() {
		return gillStudiosPriceGridParser;
	}

	public void setGillStudiosPriceGridParser(
			GillStudiosPriceGridParser gillStudiosPriceGridParser) {
		this.gillStudiosPriceGridParser = gillStudiosPriceGridParser;
	}

	public GillStudiosImprintMethodParser getGillStudiosImprintMethodParser() {
		return gillStudiosImprintMethodParser;
	}

	public void setGillStudiosImprintMethodParser(
			GillStudiosImprintMethodParser gillStudiosImprintMethodParser) {
		this.gillStudiosImprintMethodParser = gillStudiosImprintMethodParser;
	}

	public boolean isRepeateColumn(int columnIndex){
		
		if(columnIndex != 1 && columnIndex != 3 && columnIndex != 4 && columnIndex != 14 && columnIndex != 25 &&columnIndex != 26 && columnIndex !=27 && columnIndex != 28 && columnIndex != 29 && columnIndex != 30  
				&& columnIndex != 31&&columnIndex != 32 && columnIndex !=33 && columnIndex != 34 && columnIndex != 35 && columnIndex != 36 && columnIndex != 37
				&& columnIndex != 38&&columnIndex != 39 && columnIndex !=40 && columnIndex != 41 && columnIndex != 42 && columnIndex != 43
				
				){
			return ApplicationConstants.CONST_BOOLEAN_TRUE;
		}
		return ApplicationConstants.CONST_BOOLEAN_FALSE;
	}
	public static String[] getValuesOfArray(String data,String delimiter){
		   if(!StringUtils.isEmpty(data)){
			   return data.split(delimiter);
		   }
		   return null;
	   }

}
