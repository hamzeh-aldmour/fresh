package com.madfooat.billinquiry;

import com.madfooat.billinquiry.domain.Bill;
import com.madfooat.billinquiry.exceptions.InvalidBillInquiryResponse;

import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.InputSource;
import java.io.*;
import java.math.BigDecimal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLParseBillInquiryResponse implements ParseBillInquiryResponse {
    @Override
    public List<Bill> parse(String billerResponse) throws InvalidBillInquiryResponse {
        // Write your implementation
    	ArrayList<Bill> water_Authority=new ArrayList<Bill>();
    	
    	try {
    		
	      		LocalDate today_Date=LocalDate.now();
	      		DateTimeFormatter d1=DateTimeFormatter.ofPattern("dd-MM-yyyy");
      		
	    		DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();
		        DocumentBuilder db = dbf.newDocumentBuilder();
		        InputSource is = new InputSource();
		        is.setCharacterStream(new StringReader(billerResponse));
	
		        Document doc = db.parse(is);
		        NodeList nodes = doc.getElementsByTagName("bill");
		        
		        
		        // iterate the bills
		        
		        for (int i = 0; i < nodes.getLength(); i++) {
		        	
		        	Bill bill_List=new Bill();
		    	    Element bill = (Element) nodes.item(i);

	 	    	    /*
	 	    	     ** 1 Validate if fields "Bill Due Date, Amount" is exists 
	 	    	     */
		    	    if((bill.getElementsByTagName("dueDate").item(0)==null)&&(bill.getElementsByTagName("dueAmount").item(0)==null)){
	 	      			System.out.println("Fields 'Bill Due Date', 'Amount' does not exists");
	 	    	    	throw new InvalidBillInquiryResponse();
		    	    }//end if
		    	    else{//Due Date, Amount" is exists
				    	    Node dueDate = bill.getElementsByTagName("dueDate").item(0);
				    	    String due_Date=dueDate.getTextContent();
				    	    //System.out.println(due_Date);		    	    
				    	    LocalDate Due_Date=LocalDate.parse(due_Date,d1);
 				    	    /*
 				    	     ** 2 Validate that Bill due date should not be future date.
 				    	     */
				      		if(Due_Date.isBefore(today_Date)){//Due Date not in Future
 					      		
 				      			System.out.println(Due_Date);
 				      			bill_List.setDueDate(Due_Date);
 					      		
 				      		}//end if
 				      		else{//if Due_Date is in Future
 				      			System.out.println("Bill due date is future date.");
 			 	    	    	throw new InvalidBillInquiryResponse();

 				      		}//end else
					    	    
				    	    
				    	    Node dueAmount = bill.getElementsByTagName("dueAmount").item(0);
				    	    String due_Amount=dueAmount.getTextContent();

				    	    /*
 				      		 * 3 Validate Amount should be of valid format in Jordainian Dinar.
 				      		 */
	 					    // if (due_Amount.compareTo(BigDecimal.ZERO) > 0){
 					    	if(is_valid_Jordainian_Dinar(due_Amount)){//check if dueAmount is valid Jordainian Dinar format
 					            BigDecimal due_Amount_Dec = new BigDecimal(due_Amount);

 					    		System.out.println(due_Amount_Dec);
 					    		bill_List.setDueAmount(due_Amount_Dec);
 	    	    	        }//end if
 					        else{//is not valid Jordainian Dinar format
 				      			System.out.println("Amount should be of valid format in Jordainian Dinar.");
 			 	    	    	throw new InvalidBillInquiryResponse();

 					        }//end else		    	    
 				    	    /*
 				    	     * 4  Fees is optional and incase its thier it should be valid format in Jordainian Dinar and less than Amount.
 				    	     */
 				    	    if((bill.getElementsByTagName("fees").item(0)!=null)){//if fees found
 				    	    	
 					    	    Node fees = bill.getElementsByTagName("fees").item(0);
 					    	    String fees_String=fees.getTextContent();
 						    	if(is_valid_Jordainian_Dinar(fees_String)){//check if fees is valid Jordainian Dinar format
 						            BigDecimal fees_Decimal = new BigDecimal(fees_String);

 						    		if(fees_Decimal.compareTo(bill_List.getDueAmount())<0){//fees is less than Amount.
 						    			    bill_List.setFees(fees_Decimal);
 								    	    System.out.println(fees_Decimal);
 						            }//end if   
 						            else{//fees is larger than Amount.
 						      			System.out.println("fees is larger than Amount!!");
 						      			throw new InvalidBillInquiryResponse();
 						            }//end else

 		    	    	        }//end if is_valid_Jordainian_Dinar
 						        else{//fees is not valid Jordainian Dinar
 					      			System.out.println("fees should be of valid format in Jordainian Dinar.");
 				 	    	    	throw new InvalidBillInquiryResponse();
 						        }//end else				    	    
 					            
 				    	    }//end if
		
				    	    water_Authority.add(bill_List);
				    	    System.out.println(water_Authority.size());
				    	    System.out.println(water_Authority);
		
				    	    System.out.println("---------");
				    	}//end else
		    	
		        }//end for
		    return water_Authority;

    	}//end try block
    	catch (InvalidBillInquiryResponse e) {

            throw new InvalidBillInquiryResponse();
        }
    	catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    	
    }//end parse method
    
  private static boolean is_valid_Jordainian_Dinar (String jd) {
    	
    	if(jd.matches("[0-9]*(\\.[0-9]*)?")){
    		return true;
    	}
    	else{
    		return false;
    	}
    }//end is_valid_Jordainian_Dinar
}
