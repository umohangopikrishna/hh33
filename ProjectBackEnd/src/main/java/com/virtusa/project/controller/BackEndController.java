package com.virtusa.project.controller;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.virtusa.project.been.Mail;
import com.virtusa.project.models.Managerdefinedtb;
import com.virtusa.project.models.UserInfotb;
import com.virtusa.project.models.Usertb;
import com.virtusa.project.models.managertb;
import com.virtusa.project.repository.ManagerdefinedInterface;
import com.virtusa.project.repository.Repouserinfotb;
import com.virtusa.project.repository.Repousetb;
import com.virtusa.project.repository.managerinfointerface;
import com.virtusa.project.services.mailInterface;

@CrossOrigin(origins = "*")
@RestController
public class BackEndController extends TimerTask {
    @Autowired
	private Repousetb repotb_obj;
    @Autowired
    private Repouserinfotb repouserinfo_obj;
    
    @Autowired
    private ManagerdefinedInterface repo_managerdefinedocc;
    
    @Autowired
    private managerinfointerface repo_managerinfo;
    
    @PostMapping("/reguser")
    public Usertb adduser(@RequestBody Usertb userobj)
    {  System.out.println(userobj);
    	try
    	{
    	return repotb_obj.save(userobj);
    	
    	}
      catch(Exception e)
    {
    	  return userobj;
    }
    
    }
    
    @PostMapping("/verifyuser")
    public String verifyuser(@RequestBody Usertb userobj)
    {
    	List<Usertb> userlist = repotb_obj.findAll();
    	String flat_no="\"0,";
    	String email = "0,";
    	String phonenumber = "0\"";
    	for(int i=0;i<userlist.size();i++)
    	{
    		if(userlist.get(i).getFlat_no().equals(userobj.getFlat_no()))
    		{
    			flat_no="\"1,";
    		}
    		if(userlist.get(i).getEmailId().equals(userobj.getEmailId())) 
    		{
    			email ="1,";
    		}
    		if(userlist.get(i).getPhoneNumber().equals(userobj.getPhoneNumber()))
    		{
    			phonenumber = "1\"";
    		}
    	}
    	return(flat_no+email+phonenumber);
    }
     
 
    
	
//-----------------------------------------------------------------------------------------------

 
    

@RequestMapping("/userauth")
public boolean finduser( String flat_id , String password)
    {
	
	List<Usertb> userlist = repotb_obj.findAll();
	
	for(int i=0;i<userlist.size();i++)
	{
		if(userlist.get(i).getFlat_no().equals(flat_id) && userlist.get(i).getPassword().equals(password))
			return true;
	}
	
	return false;
    	
    }

@DeleteMapping("/userdelet/{flat_id}")
public boolean deletuser(@PathVariable String flat_id)
{
    List<Usertb> userlist = repotb_obj.findAll();
    boolean fg1=false;
    boolean fg2=false;
    if(flat_id==null)
    	return(false);
    for(int i=0;i<userlist.size();i++)
    {   if(userlist.get(i).getFlat_no() == null)
    	continue;
    	if(userlist.get(i).getFlat_no().equals(flat_id))
    	{  fg1 = true;
    		repotb_obj.delete(userlist.get(i));
    	}
    }
    
    List<UserInfotb> userinfolist = repouserinfo_obj.findAll();
    for(int i=0;i<userinfolist.size();i++)	
    { if(userinfolist.get(i).getFlat_id().equals(flat_id))
    	{ fg2 = true;
    	repouserinfo_obj.delete(userinfolist.get(i));
    	}
    }

    
    return fg1;
}

@GetMapping("/delet_manager")
public boolean delet_manager()
{
try {
    repo_managerinfo.deleteAll();
return(true);   
}
catch(Exception e)
{
return(false);
}


}


@GetMapping("/payverfication/{flat_id}")
public int payverify(@PathVariable String flat_id)
{   
	String pattern = "MM-dd-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());
	System.out.println(date);
  	List<UserInfotb> userinfolist = repouserinfo_obj.findAll();
  	try {
  	for(int i=0;i<userinfolist.size();i++)
  	{
  		if(userinfolist.get(i).getDatatopay().startsWith(date.substring(0, 2))&&userinfolist.get(i).getDatatopay().endsWith(date.substring(6, 10)) && userinfolist.get(i).getFlat_id().equals(flat_id))
  		{
  			return(userinfolist.get(i).getPaymode());
  		}	
  		
  	}
  	}
  	catch(Exception e)
  	{
  		return(3);
  	}
  	return(3);
}

@GetMapping("/adduserinfo/{flat_id}/{fund}")
public boolean adduserinfo(@PathVariable String flat_id ,@PathVariable int fund)
{
	List<UserInfotb> userinfo_list = repouserinfo_obj.findAll();
	String pattern = "MM-dd-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());
	try
	{
       
		for(int i=0;i<userinfo_list.size();i++)
		{
			if(userinfo_list.get(i).getDatatopay().startsWith(date.substring(0,2))&&userinfo_list.get(i).getDatatopay().endsWith(date.substring(6,10))&&userinfo_list.get(i).getFlat_id().equals(flat_id))
			{
				System.out.println(userinfo_list.get(i).getFlat_id());
				
				UserInfotb userinfotb_obj =  repouserinfo_obj.findById(userinfo_list.get(i).getId()).get();
				userinfotb_obj.setFlat_id(flat_id);
				userinfotb_obj.setPaydata(date);
				userinfotb_obj.setPaymode(1);
				userinfotb_obj.setPaymoney(fund);
				repouserinfo_obj.save(userinfotb_obj);
				
			}
		}
		return true ;
	}
  catch(Exception e)
{
	  return false ;
}
	
}
/*public Product updateProduct(Product product)
{
	Product exi=repo.findById(product.getId()).orElse(null);
	exi.setName(product.getName());
	exi.setPrice(product.getPrice());
	exi.setQuantity(product.getQuantity());
	return repo.save(exi);
	
}
*/

@GetMapping("/viewuserdata/{date}/{flat_id}")
public List<UserInfotb> viewuserdata(@PathVariable String date , @PathVariable String flat_id)
{
	List<UserInfotb> userrecordlist = repouserinfo_obj.findAll();
	List<UserInfotb>userrecord = new ArrayList<UserInfotb>();
	for(int i=0;i<userrecordlist.size();i++)
	{
		if(userrecordlist.get(i).getDatatopay().startsWith(date.substring(0, 2)) && userrecordlist.get(i).getDatatopay().endsWith(date.substring(6, 10)) && userrecordlist.get(i).getFlat_id().equals(flat_id))
		{   userrecord.add(userrecordlist.get(i));
			return(userrecord);
		}
	}
	return(userrecord);
	
}

@GetMapping("/viewoccuation_and_fund")
public String getoccusionname_and_fund()
{
	String pattern = "MM-dd-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());
  	List<Managerdefinedtb> Managerdefinedtb_objs = repo_managerdefinedocc.findAll();
  	
  	String occuation_and_fund = repo_managerdefinedocc.findById(Managerdefinedtb_objs.get(Managerdefinedtb_objs.size()-1).getId()).get().getOccuation();
  	
  	occuation_and_fund = occuation_and_fund +"||"+repo_managerdefinedocc.findById(Managerdefinedtb_objs.get(Managerdefinedtb_objs.size()-1).getId()).get().getFund();
  		
  	
    return("\""+occuation_and_fund+"\"");
	
}

  
@PostMapping("/regmanager")
public managertb addmanager(@RequestBody managertb manager_reg_obj)
{  System.out.println(manager_reg_obj);
	try
	{
	return repo_managerinfo.save(manager_reg_obj);
	
	}
  catch(Exception e)
{
	  return manager_reg_obj;
}

}

@GetMapping("/verifymanager")
public boolean verifymanager()
{
	int manager_activate = repo_managerinfo.findAll().size();
	return(manager_activate==0);
	
}
	
@GetMapping("/verify_month_info")
public boolean verify_month_fund()
{
	String pattern = "MM-dd-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());
	List<Managerdefinedtb>month_list =  repo_managerdefinedocc.findAll();
	for(int i=0;i<month_list.size();i++)
	{
		if(month_list.get(i).getFund_day().startsWith(date.substring(0,2))&&month_list.get(i).getFund_day().endsWith(date.substring(6,10)))
			return true;
	}
	return false;
	
}

// if verify_month_fund is true then we not go to add_moth_info

@PostMapping("/add_month_info")
public Managerdefinedtb add_month_info(@RequestBody Managerdefinedtb managerdef )
{  System.out.println(managerdef);
        
       List<Usertb> userlist = repotb_obj.findAll();
       int record_len =userlist.size();

	//List<UserInfotb> userinfolist = repouserinfo_obj.findAll();

	for(int i=0;i<record_len;i++)
	{
	        	UserInfotb UserInfotb_obj = new UserInfotb();
				UserInfotb_obj.setDatatopay(managerdef.getFund_day());
		       UserInfotb_obj.setPaydata("");
		       UserInfotb_obj.setFlat_id(userlist.get(i).getFlat_no());
		       UserInfotb_obj.setOccuation(managerdef.getOccuation());
		       UserInfotb_obj.setPaymode(0);
		       UserInfotb_obj.setPaymoney(0);
		       repouserinfo_obj.save(UserInfotb_obj);
		       
	}

	try
	{
	return repo_managerdefinedocc.save(managerdef);
	
	}
  catch(Exception e)
{
	  return managerdef;
}

}

@GetMapping("/mode_use_pay/{date}")
public List<String> users_paymode(@PathVariable String date)
{
	
	System.out.println(date);
	List<String> user_paymode= new ArrayList<String>(); 
  	List<UserInfotb> userinfolist = repouserinfo_obj.findAll();
  	int record_len = repotb_obj.findAll().size();
  	int c=0;
  	
  	for(int i=0;i<userinfolist.size();i++)
  	{
  		if(userinfolist.get(i).getDatatopay().startsWith(date.substring(0, 2))&&userinfolist.get(i).getDatatopay().endsWith(date.substring(6, 10)))
  		{ 
  			String st = userinfolist.get(i).getFlat_id()+","+userinfolist.get(i).getPaymode()+","+userinfolist.get(i).getId();
  			user_paymode.add(st);
  			c++;
  		}	
  		
  	}
  	return(user_paymode);
	
}

@GetMapping("/manager_mode_change/{id}")
public boolean change_mode_pay(@PathVariable int id)
{ try {
    UserInfotb userinfotb_obj = repouserinfo_obj.findById(id).get();
    userinfotb_obj.setPaymode(2);
    repouserinfo_obj.save(userinfotb_obj);
    return(true);
    }
catch(Exception e)
    {
	return(false);
	}
    
}
@GetMapping("/update_password/{psd}/{flat_id}")
public boolean set_password(@PathVariable String psd , @PathVariable String flat_id )
{
	List<Usertb> user_list = repotb_obj.findAll();
	
	for(int i=0;i<user_list.size();i++)
	{
		if(user_list.get(i).getFlat_no().equals(flat_id))
		{
			
			 Usertb	temp_user_obj = user_list.get(i);
			 temp_user_obj.setPassword(psd);
			 repotb_obj.save(temp_user_obj);
			return(true);
			
		}
	}
	return(false);
	
}
@GetMapping("/check_detail_send_otp/{flat_id}/{email}")
public boolean verfiy_to_send_otp(@PathVariable String flat_id , @PathVariable String email)
{
	List<Usertb> user_list = repotb_obj.findAll();
	
	for(int i=0;i<user_list.size();i++)
	{
		if(user_list.get(i).getFlat_no().equals(flat_id))
		{
			return(user_list.get(i).getEmailId().equals(email));
		}
	}
	return(false);
	
	
}

@GetMapping("/user_info_visualization/{falt_id}/{year}")
public List<String> user_info_visualization(@PathVariable String falt_id , @PathVariable String year )
{
	List<UserInfotb> user_year_info = repouserinfo_obj.findAll();
	List<String> user_year_month_list = new ArrayList<String>();
	
	for(int i=0;i<user_year_info.size();i++)
	{
		if(user_year_info.get(i).getFlat_id().equals(falt_id) && user_year_info.get(i).getDatatopay().endsWith(year) && user_year_info.get(i).getPaymode() == 2)
		{
			String st = user_year_info.get(i).getOccuation() +"||"+user_year_info.get(i).getPaydata()+"||"+user_year_info.get(i).getPaymoney();
			user_year_month_list.add(st);
		}
		
	}
	
return(user_year_month_list);	
}

@GetMapping("/total_fund")
public List<String> total_fund()
{
	List<UserInfotb> userinfo_list = repouserinfo_obj.findAll();
	String pattern = "MM-dd-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());
	int total_count = 0;
	int payed_count = 0;
	int unconformed = 0;
	int total_amount =0;
	String payed_user_list=new String("");
	String unconformed_user_list=new String("");
	String not_payed_user_list=new String("");

	for(int i=0;i<userinfo_list.size();i++)
	{
		//System.out.println(userinfo_list.get(i).getDatatopay());
		if(userinfo_list.get(i).getDatatopay().startsWith(date.substring(0,2)) && userinfo_list.get(i).getDatatopay().endsWith(date.substring(6,10)))
		{
			if(userinfo_list.get(i).getPaymode()==2)
			{
				payed_count++;
				payed_user_list=payed_user_list+","+userinfo_list.get(i).getFlat_id();
				total_amount = total_amount+userinfo_list.get(i).getPaymoney();
			}else if(userinfo_list.get(i).getPaymode()==1)
			{
				unconformed_user_list=unconformed_user_list+","+userinfo_list.get(i).getFlat_id();
				unconformed++;
			}else
			{
				not_payed_user_list=not_payed_user_list+","+userinfo_list.get(i).getFlat_id();

			}
			
				total_count++;	
		}
	}
	String count_data = new String("");
	count_data = total_count+","+payed_count+","+unconformed+","+total_amount;
	
	List<String> payment_description = new ArrayList<String>();
	
	payment_description.add(payed_user_list);
	payment_description.add(unconformed_user_list);
	payment_description.add(not_payed_user_list);
	payment_description.add(count_data);
	
	return(payment_description);
}


@GetMapping("/year_sending money/{year}")
public  String[] year_spend_analysis(@PathVariable int year)
{
  String[] year_analysis = new String[12];
  int[] amount = new int[12];
  for(int i=0;i<12;i++)
  { 
	  year_analysis[i]="";
      amount[i]=0;
  }
  
  List<UserInfotb> user_payment_description = repouserinfo_obj.findAll();
  
  for(int i=0;i<user_payment_description.size();i++)
  {
	  if(user_payment_description.get(i).getPaydata().endsWith(year+""))
	  {
		  int month = Integer.parseInt(user_payment_description.get(i).getPaydata().substring(0,2));
		  amount[month-1]=amount[month-1]+user_payment_description.get(i).getPaymoney();
	  }
  }
  
  List<Managerdefinedtb> manager_month_info =  repo_managerdefinedocc.findAll();
  
  for(int i=0;i<manager_month_info.size();i++)
  {
	  if(manager_month_info.get(i).getFund_day().endsWith(year+""))
	  {
		  int month = Integer.parseInt(manager_month_info.get(i).getFund_day().substring(0,2));
		  year_analysis[month-1]=manager_month_info.get(i).getOccuation();
		  
	  }
  }
  
  
  for(int i=0;i<12;i++)
  {
	  year_analysis[i]=year_analysis[i]+"||"+amount[i];
  }
	  
  
  
  return(year_analysis);
}

@GetMapping("/manager_auth/{manager_id}/{manager_psd}")
public boolean manager_auth(@PathVariable String manager_id , @PathVariable String manager_psd)
{
  List<managertb> managertb_list = repo_managerinfo.findAll();
  
  for(int i=0;i<managertb_list.size();i++)
  {
	  if(managertb_list.get(i).getManager_id().equals(manager_id) && managertb_list.get(i).getManager_password().equals(manager_psd))
	  {
		  return(true);
	  }
  }
  return(false);
	
}

@GetMapping("forget_data_manage_verfication/{manager_id}/{email_id}")
public boolean manager_forget_date_verfiy(@PathVariable String manager_id , @PathVariable String email_id )
{
  List<managertb> managertb_list = repo_managerinfo.findAll();

  for(int i=0;i<managertb_list.size();i++)
  {
	  if(managertb_list.get(i).getManager_id().equals(manager_id) && managertb_list.get(i).getManager_gmail().equals(email_id))
      {
    	  return(true);
      }
  }
  
  return(false);

}

@GetMapping("update_manager_psd/{manager_id}/{reset_psd}")
public boolean manager_password_update(@PathVariable String manager_id , @PathVariable String reset_psd)
{
 
	List<managertb> managertb_list = repo_managerinfo.findAll();
	
	for(int i=0;i<managertb_list.size();i++)
	{
		if(managertb_list.get(i).getManager_id().equals(manager_id))
		{
			managertb managertb_obj = managertb_list.get(i);
			managertb_obj.setManager_password(reset_psd);
			repo_managerinfo.save(managertb_obj);
			return(true);
		}
	}
	return(false);
	
}


@GetMapping("/verify_manager_month_info_updateion") // it will check wheather month info is add or not for the current month

public boolean verify_manager_month_info()
{

	String pattern = "MM-dd-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());
	
	List<Managerdefinedtb> manager_month_info_list = repo_managerdefinedocc.findAll();
	
	for(int i=0;i<manager_month_info_list.size();i++)
	{
		if(manager_month_info_list.get(i).getFund_day().startsWith(date.substring(0,2)) && manager_month_info_list.get(i).getFund_day().endsWith(date.substring(6,10)))
		{
			return(true);
		}
	}
	return(false);
	
}


@GetMapping("/manager_gmail")
public String get_manager_gmail()
{
  return("\""+repo_managerinfo.findAll().get(0).getManager_gmail()+"\"");
    
}

@GetMapping("/sendgmail/{otp}/{mode_of_user}/{emailid}")
public String sendgmaile(@PathVariable int otp , @PathVariable int mode_of_user , @PathVariable String emailid )
{   
	 Mail mail = new Mail();
     mail.setMailFrom("ramakrishna711999@gmail.com");
     mail.setMailTo(emailid);
     if(mode_of_user==0) // forget password
     {
    	 mail.setMailSubject("RESET YOUR SWEET HOME's PASSWORD");
    	 mail.setMailContent("<img src='https://www.brandcrowd.com/gallery/brands/pictures/picture13381230327004.jpg'>"
    	     		+ "<h3> OTP :-</h3><h4>"+otp+"</h4>");
     }else if(mode_of_user == 1)
     {
    	 mail.setMailSubject("CONFRIM OTP TO DELETE YOUR/'s ACCOUNT !!!!");
    	 mail.setMailContent("<img src='https://www.brandcrowd.com/gallery/brands/pictures/picture13381230327004.jpg'> <h1> WE MISS YOU ALOT :-(</h1>"
    	     		+ "<h3> OTP :-</h3><h4>"+otp+"</h4>");
     }else if(mode_of_user == 2)
     {
    	 mail.setMailSubject("RESET YOUR MANAGER SWEET HOME's PASSWORD");
    	 mail.setMailContent("<img src='https://www.brandcrowd.com/gallery/brands/pictures/picture13381230327004.jpg'>"
    	     		+ "<h3> OTP :-</h3><h4>"+otp+"</h4>");
     }else if(mode_of_user ==3)
     {
    	 mail.setMailSubject("Verfiy User Registration SWEET HOME's ");
    	 mail.setMailContent("<h1>My Dear Manager Show Otp To User, </h1><img src='https://www.brandcrowd.com/gallery/brands/pictures/picture13381230327004.jpg'>"
    	     		+ "<h2> OTP :-</h2><h2>"+otp+"</h2>");
     }
     
     ApplicationContext  context = mail.getContext(); 
     mailInterface mailService = (mailInterface) context.getBean("mailService");
     System.out.println(context);
     mailService.sendEmail(mail);
     
     return("\"mail is send\"");
     
}

@GetMapping("/user_mail_id/{flat_id}")
public String get_user_emailid(@PathVariable String flat_id)
{
List<Usertb> user_list = repotb_obj.findAll();

for(int i=0;i<user_list.size();i++)
{
if(user_list.get(i).getFlat_no().equals(flat_id))
{
return("\""+user_list.get(i).getEmailId()+"\"");
}
}
return("");
}
	




/*--------time scheudular ---------------*/


Timer timer;
static ArrayList<String> user_mail_id; 
static TimerTask  backend_controller_obj;

public void fun( TimerTask  obj,int seconds) {
    timer = new Timer();
    timer.schedule( obj , seconds*1000);
    System.out.println(":-)");
}

@GetMapping("/mail_schedualer")
public void main_caller() {
	TimerTask  obj= new BackEndController();
	backend_controller_obj = obj;
  System.out.println("Task scheduled.");
  List<Usertb> usertb_list = repotb_obj.findAll();
  user_mail_id = new ArrayList<String>();
  for(int i=0;i<usertb_list.size();i++)
  {
	  user_mail_id.add(usertb_list.get(i).getEmailId());
  }
  
     fun(obj,1);


}

    public void run() {
        System.out.println("Time's up!");
        reminder_user_list();
        
        //Terminate the timer thread
        //fun(backend_controller_obj,24*60*60);
       //gmail_reminder("umgkrishna00@gmail.com");
    try {
        timer.cancel();
    }
    catch(Exception e)
    {
    	
    }
        System.out.println("==============================================================================");
    	TimerTask  obj= new BackEndController();
    	backend_controller_obj =obj;
        fun(backend_controller_obj,5*60);

    }
    
    public void reminder_user_list()
    {
     
     for(int i=0;i<user_mail_id.size();i++)
     {
    	// System.out.println(usertb_list.get(i).getEmailId());
    	 gmail_reminder(user_mail_id.get(i));
     }
          
    
    }
    
 
    public void gmail_reminder(String emailid )
    {   
    	try {
    	
    	 Mail mail = new Mail();
         mail.setMailFrom("ramakrishna711999@gmail.com");
         mail.setMailTo(emailid);
         
         
        	 mail.setMailSubject("PAY MONTHLY SERVICES MONEY");
        	 mail.setMailContent("<img src='https://www.brandcrowd.com/gallery/brands/pictures/picture13381230327004.jpg'>"
        	 		+ "<h1> Monthly Services Money Need To Be Payed</h1>"
        	 		+ "<h3 style='color:red'> 100 RS Fine To Be Pay For Every Day After The 10th day </h3>");
         
         
         ApplicationContext  context = mail.getContext(); 
         mailInterface mailService = (mailInterface) context.getBean("mailService");
         System.out.println(context);
         mailService.sendEmail(mail);
         
    	}
    	catch(Exception e)
    	{
    		//gmail_reminder(emailid);
    		TimerTask  obj= new BackEndController();
        	backend_controller_obj =obj;
            fun(backend_controller_obj,5*60);

    	}
    }

}

