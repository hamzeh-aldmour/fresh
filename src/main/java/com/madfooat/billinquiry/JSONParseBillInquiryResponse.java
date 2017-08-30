package com.madfooat.billinquiry;

import com.madfooat.billinquiry.domain.Bill;
import com.madfooat.billinquiry.exceptions.InvalidBillInquiryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.json.*;

public class JSONParseBillInquiryResponse implements ParseBillInquiryResponse {
    @Override
    public List<Bill> parse(String billerResponse) throws InvalidBillInquiryResponse { 
        // Write your implementation

     	ArrayList<Bill> electric_Bill_List=new ArrayList<Bill>();
     		
     	try {
     		
     		LocalDate today_Date=LocalDate.now();
       		DateTimeFormatter d1=DateTimeFormatter.ofPattern("dd-MM-yyyy");

     		
 	    	JSONArray jsonarray = new JSONArray(billerResponse);
 	    	for (int i = 0; i < jsonarray.length(); i++) {
 	    	    JSONObject jsonobject = jsonarray.getJSONObject(i);
 	    	    
 	        	Bill bill_List=new Bill();
 	    	    /*
 	    	     ** 1 Validate if fields "Bill Due Date, Amount" is exists 
 	    	     */
 	    	    if(jsonobject.has("dueAmount")&&jsonobject.has("dueDate")){
 	    	    	
 				    	    String dueDate = jsonobject.getString("dueDate");	    	    	    	    
 				    	    LocalDate Due_Date=LocalDate.parse(dueDate,d1);
 				    	    /*
 				    	     ** 2 Validate that Bill due date should not be future date.
 				    	     */
 				      		if(Due_Date.isBefore(today_Date)){//Due Date not in Future
 					      		
 				      			System.out.println(Due_Date);
 				      			bill_List.setDueDate(Due_Date);
 					      		
 				      		}//end if
 				      		else{//if Due_Date in Future
 				      			System.out.println("Bill due date is future date.");
 				      			throw new InvalidBillInquiryResponse();
 				      		}//end else

 				      		
 				    	    String dueAmount = jsonobject.getString("dueAmount");			    	    
 				    	    
 				      		/*
 				      		 * 3 Validate Amount should be of valid format in Jordainian Dinar.
 				      		 */
 					       // if (due_Amount.compareTo(BigDecimal.ZERO) > 0){
 					    	if(is_valid_Jordainian_Dinar(dueAmount)){
 					            BigDecimal due_Amount = new BigDecimal(dueAmount);

 					    		System.out.println(due_Amount);
 					    		bill_List.setDueAmount(due_Amount);
 	    	    	        }//end if
 					        else{
 				      			System.out.println("Amount should be of valid format in Jordainian Dinar.");
 				      			throw new InvalidBillInquiryResponse();

 					        }//end else
 				    	    /*
 				    	     * 4  Fees is optional and incase its thier it should be valid format in Jordainian Dinar and less than Amount.
 				    	     */
 				    	    if(jsonobject.has("fees")){//if fees found
 				    	    	
 					    	    String fees = jsonobject.getString("fees");
 					    	    
 						    	if(is_valid_Jordainian_Dinar(fees)){
 						            BigDecimal fees_Decimal = new BigDecimal(fees);
 						            //compareTo return [-1 0 1]
 						    		if(fees_Decimal.compareTo(bill_List.getDueAmount())<0){
 						    			    bill_List.setFees(fees_Decimal);
 								    	    System.out.println(fees_Decimal);
 						            }//end if   
 						            else{
 						      			System.out.println("fees is larger than Amount!!");
 		 				      			throw new InvalidBillInquiryResponse();

 						            }//end else

 		    	    	        }//end if is_valid_Jordainian_Dinar
 						        else{
 					      			System.out.println("fees should be of valid format in Jordainian Dinar.");
 	 				      			throw new InvalidBillInquiryResponse();

 						        }//end else				    	    
 					            

 				    	    }//end if
 	    		}//end if
 	    	    else{//if dueDate && dueAmount does not exist
 	    	    	
 	      			System.out.println("Fields 'Bill Due Date', 'Amount' does not exists");
 	    	    	throw new InvalidBillInquiryResponse();
 	    	    }//end else
 	    	    electric_Bill_List.add(bill_List);
 	    	    System.out.println("-----------");
 	    	 }//end for
 	    	
 	     	return electric_Bill_List;

    	}//end try block
     	catch (InvalidBillInquiryResponse e) {

            throw new InvalidBillInquiryResponse();
        }//end catch	
     	catch(Exception e){
     	        e.printStackTrace();
     	        return null;

     	}//end catch
     	

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
