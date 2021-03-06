package parser.riverend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.a4tech.product.model.Price;
import com.a4tech.product.model.PriceConfiguration;
import com.a4tech.product.model.PriceGrid;
import com.a4tech.product.model.PriceUnit;
import com.a4tech.util.ApplicationConstants;

public class RiverEndPriceGridParser {
	
	public List<PriceGrid> getPriceGrids(HashMap<String, String> priceGridMap,boolean flag) {
		List<PriceGrid> newPriceGridList=new ArrayList<>();
		List<PriceConfiguration> configuration = null;
		PriceGrid priceGrid = new PriceGrid();
		Integer sequence = 1;
		Iterator it = priceGridMap.entrySet().iterator();
		String priceName=null;
		String priceValue=null;
	    while (it.hasNext()) {
	    	 priceGrid = new PriceGrid();
	        Map.Entry pair = (Map.Entry)it.next();
	       // System.out.println(pair.getKey() + " = " + pair.getValue());
	       String pricegridNumber=(String) pair.getKey();
	       String priceStr=(String) pair.getValue();
	       String priceArr[]=priceStr.split("___");
	       priceName=priceArr[0];
	       priceValue=priceArr[1];
	       
	       String basePriceName=priceName;
	       
	       priceGrid.setIsBasePrice(ApplicationConstants.CONST_BOOLEAN_TRUE);
	       priceGrid.setIsQUR(ApplicationConstants.CONST_BOOLEAN_FALSE);
		  priceGrid.setCurrency(ApplicationConstants.CONST_STRING_CURRENCY_USD);
		  priceGrid.setProductNumber(pricegridNumber);
		  basePriceName=basePriceName.replaceAll("%%%",",");
		  basePriceName=basePriceName.replaceAll("&","/");
		  basePriceName=basePriceName.replaceAll(" w/","/");
		  basePriceName=basePriceName.replaceAll(" W/","/");
		  priceGrid.setDescription(basePriceName);
		priceGrid.setSequence(sequence);
		List<Price> listOfPrice = null;
			listOfPrice = getPrices(priceValue, "1", "P","1"); //imp step
		
		priceGrid.setPrices(listOfPrice);
		//if (criterias != null && !criterias.isEmpty()) {
		configuration = getConfigurations(priceName,flag);//imp code
		//}
		
		priceGrid.setPriceConfigurations(configuration);
		newPriceGridList.add(priceGrid);
		sequence++;
	    }
		return newPriceGridList;
	}

	public static List<Price> getPrices(String prices,
			String quantity, String discount,String sequence) {

		List<Price> listOfPrices = new ArrayList<Price>();
			Price price = new Price();
			PriceUnit priceUnit = new PriceUnit();
			price.setSequence(new Integer(sequence));
			try {
				price.setQty(Integer.valueOf(quantity));
			} catch (NumberFormatException nfe) {
				price.setQty(0);
			}
			//price.setPrice(prices);
			price.setNetCost(prices);
			price.setDiscountCode(discount);
			priceUnit.setItemsPerUnit(ApplicationConstants.CONST_STRING_VALUE_ONE);
			price.setPriceUnit(priceUnit);
			listOfPrices.add(price);
		
		return listOfPrices;
	}

	public static List<PriceConfiguration> getConfigurations(String criteriaValue,boolean flag) {
		List<PriceConfiguration> priceConfiguration = new ArrayList<PriceConfiguration>();
		//String[] config = null;
		if(!flag){
		PriceConfiguration configs = new PriceConfiguration();
			//config = criterias.split(ApplicationConstants.CONST_SIZE_DELIMITER);
			//String criteriaValue = LookupData.getCriteriaValue(config[0]);
			configs.setCriteria("Size");
			configs.setValue(Arrays.asList((Object) criteriaValue));
			//configs.setValue(Arrays.asList((Object) config[1]));
			priceConfiguration.add(configs);
		//return priceConfiguration;
		}else{
			String arr[]=criteriaValue.split("%%%");
			String colorValue=arr[0];
			String sizeValue=arr[1];
			
			if(colorValue.contains(",")){
				String tempArr[]=colorValue.split(",");
				for (String strColor : tempArr) {
					PriceConfiguration configColor = new PriceConfiguration();
					configColor.setCriteria("Product Color");
					strColor=strColor.replaceAll("&","/");
					strColor=strColor.replaceAll(" w/","/");
					strColor=strColor.replaceAll(" W/","/");
					configColor.setValue(Arrays.asList((Object) strColor));
					priceConfiguration.add(configColor);
				}

				PriceConfiguration configSize = new PriceConfiguration();
				configSize.setCriteria("Size");
				configSize.setValue(Arrays.asList((Object) sizeValue));
				priceConfiguration.add(configSize);
				
			}else{
				PriceConfiguration configColor = new PriceConfiguration();
				configColor.setCriteria("Product Color");
				colorValue=colorValue.replaceAll("&","/");
				colorValue=colorValue.replaceAll(" w/","/");
				colorValue=colorValue.replaceAll(" W/","/");
				configColor.setValue(Arrays.asList((Object) colorValue));
				priceConfiguration.add(configColor);
				
				PriceConfiguration configSize = new PriceConfiguration();
				configSize.setCriteria("Size");
				configSize.setValue(Arrays.asList((Object) sizeValue));
				priceConfiguration.add(configSize);
			}
			
		}
		return priceConfiguration;
	}

	
}
