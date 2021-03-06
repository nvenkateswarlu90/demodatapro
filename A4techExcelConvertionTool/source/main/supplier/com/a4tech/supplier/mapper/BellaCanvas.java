package com.a4tech.supplier.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.model.Availability;
import com.a4tech.product.model.Color;
import com.a4tech.product.model.Image;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Theme;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;

import parser.bellaCanvas.BellaCanvasPriceGridParser;
import parser.bellaCanvas.BellaCanvasProductAttributeParser;

public class BellaCanvas implements ISupplierParser {
	private static final Logger _LOGGER = Logger
			.getLogger(BellaCanvas.class);

	private PostServiceImpl postServiceImpl;
	private ProductDao productDaoObj;
	private LookupServiceData lookupServiceDataObj;
	private BellaCanvasProductAttributeParser bellaProductsParser;
	private BellaCanvasPriceGridParser bellaPricegrid;


	public BellaCanvasPriceGridParser getBellaPricegrid() {
		return bellaPricegrid;
	}

	public void setBellaPricegrid(BellaCanvasPriceGridParser bellaPricegrid) {
		this.bellaPricegrid = bellaPricegrid;
	}

	@SuppressWarnings("unused")
	public String readExcel(String accessToken, Workbook workbook,
			Integer asiNumber, int batchId, String environmentType) {

		List<String> numOfProductsSuccess = new ArrayList<String>();
		List<String> numOfProductsFailure = new ArrayList<String>();
		Set<String> productXids = new HashSet<String>();
		List<String> repeatRows = new ArrayList<>();
		List<Color> colorlist = new ArrayList<Color>();	
		List<Material> materiallist = new ArrayList<Material>();	
		List<PriceGrid> priceGrids = new ArrayList<PriceGrid>();
		List<Availability> listOfavaibility = new ArrayList<Availability>();

		
		Product productExcelObj = new Product();
		ProductConfigurations productConfigObj = new ProductConfigurations();

		String productName = null;
		String productId = null;
		String finalResult = null;
		Product existingApiProduct = null;
		int columnIndex = 0;
		String xid = null;
		Cell cell2Data = null;
		String ProdNo = null;
		String Description=null;
		String Exstngsummary=null;
		StringBuilder colorMapping = new StringBuilder();
		StringBuilder description = new StringBuilder();
		Size sizeObj = new Size();



		try {

			_LOGGER.info("Total sheets in excel::"
					+ workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			_LOGGER.info("Started Processing Product");

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() < 1)
						continue;
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					if (productId != null) {
						productXids.add(productId);
					}
					boolean checkXid = false;

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						/* int */columnIndex = cell.getColumnIndex();
						cell2Data = nextRow.getCell(3);
						if (columnIndex + 1 == 1) {
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								xid = cell.getStringCellValue();
							} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								xid = String.valueOf((int) cell
										.getNumericCellValue());
							} else {
								ProdNo = CommonUtility
										.getCellValueStrinOrInt(cell2Data);
								ProdNo = ProdNo.substring(0, 14);
								xid = ProdNo;
							}
							checkXid = true;
						} else {
							checkXid = false;
						}
						if (checkXid) {
							if (!productXids.contains(xid)) {
								if (nextRow.getRowNum() != 1) {
									System.out
											.println("Java object converted to JSON String, written to file");

									colorlist = bellaProductsParser
											.getColorCriteria(colorMapping);
									productConfigObj.setColors(colorlist);
							    	listOfavaibility=bellaProductsParser.getAvailability(sizeObj,colorlist);
									productExcelObj.setAvailability(listOfavaibility);
									productExcelObj.setPriceGrids(priceGrids);
									Description=description.toString();
									String descArr[]=Description.split(",");
									productExcelObj.setName(descArr[0]);
									Description=Description.replace(descArr[0], "").replace(",", "");
									productExcelObj.setDescription(Description);	
									productExcelObj.setProductConfigurations(productConfigObj);
									productExcelObj
											.setProductConfigurations(productConfigObj);

									int num = postServiceImpl.postProduct(
											accessToken, productExcelObj,
											asiNumber, batchId, environmentType);
									if (num == 1) {
										numOfProductsSuccess.add("1");
									} else if (num == 0) {
										numOfProductsFailure.add("0");
									} else {

									}
									_LOGGER.info("list size>>>>>>>"
											+ numOfProductsSuccess.size());
									_LOGGER.info("Failure list size>>>>>>>"
											+ numOfProductsFailure.size());

									productConfigObj = new ProductConfigurations();
									 colorMapping = new StringBuilder();
									 description = new StringBuilder();
									 colorlist = new ArrayList<Color>();	
									 materiallist = new ArrayList<Material>();	
									 sizeObj = new Size();

								}
								if (!productXids.contains(xid)) {
									productXids.add(xid.trim());
								}
								existingApiProduct = postServiceImpl
										.getProduct(accessToken,
												xid = xid.replace("\t", ""), environmentType);
								if (existingApiProduct == null) {
									_LOGGER.info("Existing Xid is not available,product treated as new product");
									productExcelObj = new Product();
								} else {
									// productExcelObj=existingApiProduct;
									// productConfigObj=existingApiProduct.getProductConfigurations();

									List<Image> Img = existingApiProduct
											.getImages();
									productExcelObj.setImages(Img);

									List<Theme> themeList = productConfigObj
											.getThemes();
									productConfigObj.setThemes(themeList);

									List<String> categoriesList = existingApiProduct
											.getCategories();
									productExcelObj
											.setCategories(categoriesList);
									
									
									Exstngsummary=existingApiProduct.getSummary();
									productExcelObj.setSummary(Exstngsummary);

								}
								// productExcelObj = new Product();
							}
							
							if(productXids.contains(xid) && repeatRows.size() != 1){
								 if(isRepeateColumn(columnIndex+1)){
									 continue;
								 }
							}
						}

						switch (columnIndex + 1) {
						case 1://XID
							productExcelObj.setExternalProductId(xid);
							break;

						case 2:// Group
							break;
							
						case 3:// Style
						    ProdNo=cell.getStringCellValue();
							productExcelObj.setAsiProdNo(ProdNo);
							break;
							
						case 4:// Style Description
						  Description=cell.getStringCellValue();
							if (!StringUtils.isEmpty(Description)) {
								description=description.append(Description +". ,");	
							}
							if (StringUtils.isEmpty(Exstngsummary) || Exstngsummary.contains("null")) {
								String Newsummary=null;
								String summayArr[]=description.toString().split("\\.");
								if(summayArr[0].length()>130)
								{
								 Newsummary=summayArr[0].substring(0, 130);
								}else {
									Newsummary=	summayArr[0];
								}
								productExcelObj.setSummary(Newsummary);
							}
							
							break;
						case 5:// Fabric
							String Material=cell.getStringCellValue();
							if (!StringUtils.isEmpty(Material)&& !Material.equalsIgnoreCase("")) {
								materiallist=bellaProductsParser.getMaterialValue(Material);							
								productConfigObj.setMaterials(materiallist);
							}
							break;

						case 6:// Color
							String colorValue = CommonUtility
									.getCellValueStrinOrInt(cell);
							//colorValue=colorValue.replace("SOLID,", "");
							if(!colorValue.contains("SOLID") && !colorValue.equalsIgnoreCase("")){
			    	        colorMapping=colorMapping.append(colorValue +",");	
							}
							break;

						case 7:// Size Range
							String Sizevalue = CommonUtility
							.getCellValueStrinOrInt(cell);
							if(!Sizevalue.equalsIgnoreCase("")){
							sizeObj=bellaProductsParser
									.getSize(Sizevalue);
							productConfigObj.setSizes(sizeObj);
							}
							break;
							
						case 8: // SKU
							break;				

						} // end inner while loop

					}
					// set product configuration objects

					// end inner while loop
					productExcelObj.setPriceType("L");
					
					priceGrids = bellaPricegrid.getPriceGrids(
							"",
							"", "", "USD",
							"", true, "true",
							productName, "", priceGrids);
									

				} catch (Exception e) {
					_LOGGER.error("Error while Processing ProductId and cause :"
							+ productExcelObj.getExternalProductId()
							+ " "
							+ e.getMessage() + "case" + columnIndex);
				}
			}
			workbook.close();
			colorlist = bellaProductsParser
					.getColorCriteria(colorMapping);
			productConfigObj.setColors(colorlist);
			listOfavaibility=bellaProductsParser.getAvailability(sizeObj,colorlist);
			productExcelObj.setAvailability(listOfavaibility);
			productExcelObj.setPriceGrids(priceGrids);
			Description=description.toString();
			String descArr[]=Description.split(",");
			productExcelObj.setName(descArr[0]);
			Description=Description.replace(descArr[0], "").replace(",", "");
			productExcelObj.setDescription(Description);
			productExcelObj.setProductConfigurations(productConfigObj);
			int num = postServiceImpl.postProduct(accessToken, productExcelObj,
					asiNumber, batchId, environmentType);
			if (num == 1) {
				numOfProductsSuccess.add("1");
			} else if (num == 0) {
				numOfProductsFailure.add("0");
			} else {

			}
			_LOGGER.info("list size>>>>>>" + numOfProductsSuccess.size());
			_LOGGER.info("Failure list size>>>>>>"
					+ numOfProductsFailure.size());
			finalResult = numOfProductsSuccess.size() + ","
					+ numOfProductsFailure.size();
			productDaoObj.saveErrorLog(asiNumber, batchId);
			productConfigObj = new ProductConfigurations();
			colorMapping = new StringBuilder();
			description = new StringBuilder();
			colorlist = new ArrayList<Color>();	
			materiallist = new ArrayList<Material>();	
			sizeObj = new Size();

			return finalResult;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet "
					+ e.getMessage());
			return finalResult;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet "
						+ e.getMessage());

			}
			_LOGGER.info("Complted processing of excel sheet ");
			_LOGGER.info("Total no of product:" + numOfProductsSuccess.size());
		}

	}
	
	  public boolean isRepeateColumn(int columnIndex){
			
			if(columnIndex != 1 &&  columnIndex!= 6 && columnIndex!= 4/* && columnIndex!= 5*/){
				return ApplicationConstants.CONST_BOOLEAN_TRUE;
			}
			return ApplicationConstants.CONST_BOOLEAN_FALSE;
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

	public BellaCanvasProductAttributeParser getBellaProductsParser() {
		return bellaProductsParser;
	}

	public void setBellaProductsParser(
			BellaCanvasProductAttributeParser bellaProductsParser) {
		this.bellaProductsParser = bellaProductsParser;
	}


}
