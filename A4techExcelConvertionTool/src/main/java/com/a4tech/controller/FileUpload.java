package com.a4tech.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.a4tech.core.model.FileBean;
import com.a4tech.core.supplierMapping.SupplierFactory;
import com.a4tech.product.dao.service.ProductDao;
import com.a4tech.product.service.ILoginService;
import com.a4tech.product.service.imple.PostServiceImpl;
import com.a4tech.supplier.service.ISupplierParser;
import com.a4tech.util.ApplicationConstants;
import com.a4tech.util.CommonUtility;
import com.a4tech.util.ConvertCsvToExcel;

@Controller
@RequestMapping({ "/", "/uploadFile.htm" })
public class FileUpload {

	@Autowired
	private ILoginService       loginService;
	private ProductDao          productDao;
	private ConvertCsvToExcel   convertCsvToExcel;
	private SupplierFactory     supplierFactory;
	private PostServiceImpl     postServiceImpl;

	private static Logger _LOGGER = Logger.getLogger(Class.class);

	@RequestMapping(method = RequestMethod.GET)
	public String welcomePage(Map<String, Object> model) {
		FileBean fileBean = new FileBean();
		model.put("filebean", fileBean);
		return ApplicationConstants.CONST_STRING_HOME;
		/*
		 * FtpLoginBean ftpLogin = new FtpLoginBean(); //uncommented code while using
		 * ftp model.put("ftpLoginBean", ftpLogin); return "ftpLogin";
		 */
	}

	@RequestMapping(method = RequestMethod.POST)
	public String fileUpload(@ModelAttribute("filebean") FileBean fileBean, final RedirectAttributes redirectAttributes,
			Model model, HttpServletRequest request) {
		_LOGGER.info("Enter Controller Class");
		String finalResult = null;
		String accessToken = null;
		String asiNumber = fileBean.getAsiNumber();
		String fileExtenion = CommonUtility.getFileExtension(fileBean.getFile().getOriginalFilename());
		if (!CommonUtility.isValidFormat(fileExtenion)) {
			model.addAttribute(ApplicationConstants.CONST_STRING_INVALID_UPLOAD_FILE,
					ApplicationConstants.CONST_STRING_EMPTY);
			return ApplicationConstants.CONST_STRING_HOME;
		}
		try {
			accessToken = loginService.doLogin(fileBean.getAsiNumber().trim(), fileBean.getUserName().trim(),
					fileBean.getPassword().trim(), "Prod");
			if (accessToken != null) {
				if (ApplicationConstants.CONST_STRING_UN_AUTHORIZED.equals(accessToken)) {
					accessToken = null;
					model.addAttribute(ApplicationConstants.CONST_STRING_INVALID_DETAILS,
							ApplicationConstants.CONST_STRING_EMPTY);
					return ApplicationConstants.CONST_STRING_HOME;
				}
			} else {
				return ApplicationConstants.CONST_STRING_ERROR_PAGE;
			}
			Workbook workbook = convertCsvToExcel.getWorkBook(fileBean.getFile());
			if (workbook == null) {
				return ApplicationConstants.CONST_STRING_ERROR_PAGE;
			}
			int batchId = productDao.createBatchId(Integer.parseInt(asiNumber));
			request.getSession().setAttribute("batchId", String.valueOf(batchId));
			ISupplierParser parserObject = supplierFactory.getExcelParserObject(asiNumber);
			if (parserObject != null) {
				finalResult = parserObject.readExcel(accessToken, workbook, Integer.valueOf(asiNumber), batchId,
						"Prod");
				if (finalResult != null) {
					parseFinalData(finalResult, asiNumber, batchId, redirectAttributes);
				}
				return ApplicationConstants.CONST_REDIRECT_URL;
			}
		} catch (Exception e) {
			_LOGGER.error("Error In FileUpload: " + e.getMessage());
		}
		return ApplicationConstants.CONST_STRING_HOME;
	}

	@RequestMapping(value = "/redirect.htm", method = RequestMethod.GET)
	public String submit(Model model) {
		String noOfSucc = (String) model.asMap().get(ApplicationConstants.SUCCESS_PRODUCTS_COUNT);
		String noOfFail = (String) model.asMap().get(ApplicationConstants.FAILURE_PRODUCTS_COUNT);
		if (noOfSucc == null) {
			model.addAttribute(ApplicationConstants.SUCCESS_PRODUCTS_COUNT, ApplicationConstants.CONST_STRING_ZERO);

		}
		if (noOfFail == null) {
			model.addAttribute(ApplicationConstants.FAILURE_PRODUCTS_COUNT, ApplicationConstants.CONST_STRING_ZERO);
		}
		return ApplicationConstants.CONST_STRING_SUCCESS;
	}

	// @Author Venkat

	public void parseFinalData(String result, String asiNumber, int batchId, RedirectAttributes redirectAttributes) {
		String[] splitFinalResult = result.split(ApplicationConstants.CONST_STRING_COMMA_SEP);
		String noOfProductsSuccess = splitFinalResult[ApplicationConstants.CONST_NUMBER_ZERO];
		String noOfProductsFailure = splitFinalResult[ApplicationConstants.CONST_INT_VALUE_ONE];
		redirectAttributes.addFlashAttribute(ApplicationConstants.SUCCESS_PRODUCTS_COUNT, noOfProductsSuccess);
		redirectAttributes.addFlashAttribute(ApplicationConstants.FAILURE_PRODUCTS_COUNT, noOfProductsFailure);
	}

	public List<String> getAllXids(Workbook workbook) {
		List<String> xidsList = new ArrayList<>();
		try {
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			String xid = null;

			while (iterator.hasNext()) {

				try {
					Row nextRow = iterator.next();
					if (nextRow.getRowNum() == ApplicationConstants.CONST_NUMBER_ZERO) {
						continue;
					}
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();

						int columnIndex = cell.getColumnIndex();
						if (columnIndex == 0) {
							Cell xidCell = nextRow.getCell(0);
							xid = CommonUtility.getCellValueStrinOrInt(xidCell);
						}
						switch (columnIndex + 1) {
						case 1:// xid
							if (!StringUtils.isEmpty(xid)) {
								xidsList.add(xid);
							}
							break;

						} // end inner while loop

					}

				} catch (Exception e) {
				}
			}
			workbook.close();
			return xidsList;
		} catch (Exception e) {
			_LOGGER.error("Error while Processing excel sheet " + e.getMessage());
			return xidsList;
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				_LOGGER.error("Error while Processing excel sheet" + e.getMessage());

			}
			_LOGGER.info("Complted processing of excel sheet ");

		}

	}

	private String deleteProducts(List<String> xidsList, String accessToken, String environmentType, int asiNumber,
			int batchId) {
		List<String> numOfProductsSuccess = new ArrayList<String>();
		for (String xid : xidsList) {
			int num;
			try {
				num = postServiceImpl.deleteProduct(accessToken, xid, asiNumber, batchId, environmentType);
				if (num == 1) {
					numOfProductsSuccess.add("1");
				}
			} catch (IOException e) {
				_LOGGER.error("Unable to delete xid:" + xid);
			}

		}
		String finalResult = numOfProductsSuccess.size() + ",0";
		return finalResult;
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	public ConvertCsvToExcel getConvertCsvToExcel() {
		return convertCsvToExcel;
	}

	public void setConvertCsvToExcel(ConvertCsvToExcel convertCsvToExcel) {
		this.convertCsvToExcel = convertCsvToExcel;
	}

	public SupplierFactory getSupplierFactory() {
		return supplierFactory;
	}

	public void setSupplierFactory(SupplierFactory supplierFactory) {
		this.supplierFactory = supplierFactory;
	}

	public PostServiceImpl getPostServiceImpl() {
		return postServiceImpl;
	}

	public void setPostServiceImpl(PostServiceImpl postServiceImpl) {
		this.postServiceImpl = postServiceImpl;
	}

}
