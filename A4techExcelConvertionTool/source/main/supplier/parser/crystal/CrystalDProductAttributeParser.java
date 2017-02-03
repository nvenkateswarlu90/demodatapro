package parser.crystal;

import java.util.ArrayList;
import java.util.List;

import com.a4tech.product.model.Dimension;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Option;
import com.a4tech.product.model.OptionValue;
import com.a4tech.product.model.Personalization;
import com.a4tech.product.model.Size;
import com.a4tech.product.model.Value;
import com.a4tech.product.model.Values;
import com.a4tech.product.model.Volume;

public class CrystalDProductAttributeParser {

	public  Volume getItemWeight(String ItemWT) {
		
		Volume itemWeight=new Volume();
		List<Values> valuesList = new ArrayList<Values>(); 
		List<Value> valueList = new ArrayList<Value>(); 

		  Value valueObj=new Value(); 
		  Values valuesObj=new Values(); 
		  valueObj.setValue(ItemWT);
		  valueObj.setUnit("lbs");
		  valueList.add(valueObj);
		  valuesObj.setValue(valueList);
		  valuesList.add(valuesObj);
		  itemWeight.setValues(valuesList); 

		return itemWeight;
	}

	

	public Size getSizes(StringBuilder shippingDimension)
	{
	  Size sizeObj=new Size();
	  String ShippingDimensionValue=shippingDimension.toString();
	  ShippingDimensionValue=ShippingDimensionValue.replaceAll("\"", "").replaceAll("Height includes chain and key ring", "").replaceAll("Width includes chain and key ring", "");
	
	  String DimValueArr[]=ShippingDimensionValue.split(",");
	  
	  Dimension dimensionObj= new Dimension();
	  List<Values> valuesList = new ArrayList<Values>();
	  List<Value> valuelist =  new ArrayList<Value>();
	  Values valuesObj = new Values();
	  Value valObj=null;
		 
	  for (String OutreLoop:DimValueArr) {
		//  dimensionObj= new Dimension();
		  valuesObj=new  Values();
		  valuelist =  new ArrayList<Value>();
		  
	   String SizeValueArr[]=OutreLoop.split("x");
	
	   for (String Value : SizeValueArr) {
			 valObj=new Value();
		   
		if(Value.contains("W"))
		{
			valObj.setAttribute("Width");
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		else if(Value.contains("H"))
		{
			valObj.setAttribute("Height");
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		else if(Value.contains("Dia"))
		{
			valObj.setAttribute("Dia");
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		else if(Value.contains("D"))
		{
			valObj.setAttribute("Depth");
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		else
		{
			Value=Value.replaceAll("[^//0-9x-]", "").replaceAll("-", " ");
			valObj.setValue(Value);
		}
		valObj.setUnit("in");
		valuelist.add(valObj);   
	     valuesObj.setValue(valuelist);
	    
	     }
	   
	     valuesList.add(valuesObj);
	     dimensionObj.setValues(valuesList);
	  }
	  sizeObj.setDimension(dimensionObj);
	return sizeObj;
	}



	public List<ImprintMethod> getImprintMethod(String imprintMethodValue, List<ImprintMethod> exstimprintMethodsList) {
		
		if(imprintMethodValue.contains("Blank"))
		{
			imprintMethodValue=imprintMethodValue.replaceAll("Blank-Imprint Extra","UNIMPRINTED");
		}
		//List<ImprintMethod> listOfImprintMethod = new ArrayList<>();
		ImprintMethod imprintMethodObj= new ImprintMethod();
		String ImprintValue[]=imprintMethodValue.split(",");
		
		for (String Value : ImprintValue) {
			imprintMethodObj = new ImprintMethod();
			imprintMethodObj.setType(CrystalDApplicationConstant.IMPRINT_METHOD_MAP.get(Value.trim()));
			imprintMethodObj.setAlias(Value);
			//imprintMethodObj.setAlias(CrystalDApplicationConstant.IMPRINT_METHOD_MAP.get(Value.trim()));
			exstimprintMethodsList.add(imprintMethodObj);
		}
	
		return exstimprintMethodsList;
	}



	public List<Option> getImprintOption(String tempImprintOptionValue) {
		List<Option> optionList = new ArrayList<Option>();
		List<OptionValue> valuesList = new ArrayList<OptionValue>();
		OptionValue optionValueObj=new OptionValue();
		Option optionObj=new Option();
		
		optionObj.setOptionType("Imprint");
		optionObj.setName("Additional Imprinting Option");
		optionValueObj.setValue(tempImprintOptionValue);
		valuesList.add(optionValueObj);
		optionObj.setValues(valuesList);
		optionList.add(optionObj);
		
		return optionList;
	}



	public List<Personalization> getPeronalization(String allNotes) {
		
		List<Personalization> listPersonlization=new ArrayList<Personalization>();
		if(allNotes.contains("Personalization")){
			Personalization perslznObj=new Personalization();
			perslznObj.setAlias("PERSONALIZATION");
			perslznObj.setType("PERSONALIZATION");
			listPersonlization.add(perslznObj);
		}
		return listPersonlization;
	}
	
	
	
	
	
	
}
