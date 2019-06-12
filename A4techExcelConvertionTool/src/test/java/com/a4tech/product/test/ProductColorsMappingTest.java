package com.a4tech.product.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.a4tech.product.dao.entity.SupplierProductColors;
import com.a4tech.product.test.service.IProductDaoTest;

@WebAppConfiguration
@ContextConfiguration(locations = "classpath:application-config-test.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductColorsMappingTest {
	@Autowired
	private IProductDaoTest productDao;

	//@Test
	public void createColorMapping() {
		FileInputStream inputStream;
		Workbook workbook;
		try {//D:\A4 ESPUpdate\Supplier Productcolor Files\colors
			inputStream = new FileInputStream(new File("D:\\A4 ESPUpdate\\Supplier Productcolor Files\\SupplierProductColors.xlsx"));
			workbook = new XSSFWorkbook(inputStream);
			Sheet firstSheet = workbook.getSheetAt(0);
		    Integer asiNumber = Integer.parseInt(firstSheet.getSheetName());
			List<SupplierProductColors> productColorsList = getProductColorMapping(firstSheet.iterator(),asiNumber);
			
			productDao.saveSupplierColors(productColorsList);
			System.out.println("product colors has been saved successfully in DB");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	/*@Test
	public void getSupplierColors() {
		List<SupplierProductColors> colorsList = productDao.getSupplierColorsById(57653);
		assertEquals(colorsList.size(), 3);
		
		List<ProductColors> colorsList1 = productDao.getSupplierColorsById(456);
		assertEquals(colorsList1.size(), 2);
		
	}*/

	
	private List<SupplierProductColors> getProductColorMapping(Iterator<Row> iterator,Integer asiNumber){
		List<SupplierProductColors> productColorsList = new ArrayList<>();
		SupplierProductColors productColorObj = null;
		while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            if(nextRow.getRowNum() == 0)
            	continue;
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            String colorName = "";
            String colorGroup = "";
            productColorObj = new SupplierProductColors();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
				int columnIndex = cell.getColumnIndex();
                switch (columnIndex+1) {
                    case 1:
                         String colorVal = cell.getStringCellValue();
                         if(colorVal.contains(",")) {
                        	 String[] colors = colorVal.split(",");
                        	 colorName = colors[0];
                        	 colorGroup = colors[1];
                         }
                        break;
                }
               
            }
            productColorObj.setColorName(colorName);
            productColorObj.setColorGroup(colorGroup);
            productColorObj.setAsiNumber(asiNumber);
            productColorsList.add(productColorObj);
        }
		return productColorsList;
	}

}
