package vin.kell;

import java.io.IOException;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class USCIS {
	public static final String prefix = "YSC";
	
	private Date minDate = new Date();
	private long close_approved = 0;
	
	public long getClose_approved() {
		return close_approved;
	}

	public void setClose_approved(long close_approved) {
		this.close_approved = close_approved;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public enum Status{
		Received, Approved, Rejected, Mailed, Delivered, Other
	}
	
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		if(args.length<3) {
			System.err.println("Usage: Case_ID_without_Lettes num_of_cases_prior_to num_of_cases_after");
			System.exit(-1);
		}
		USCIS uscis = new USCIS();
		int received = 0;
		int approved = 0;
		int mailed = 0;
		int delivered = 0;
		int other = 0;
		long total = 0;
		long yourcase,start,end;
		yourcase=start=end=0;
		try {
			yourcase = Integer.parseInt(args[0]);
			start = yourcase - Integer.parseInt(args[1]);
			end = yourcase + Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			System.err.println("Parameters should be numbers only!");
			System.exit(-2);
		}

		
		for(long i=start;i<=end;i++) {
			Status status = uscis.getStatus(i);
			Thread.sleep(2000);
			if(status==Status.Received)
				received++;
			else if (status==Status.Mailed)
				mailed++;
			else if (status==Status.Approved) {
				if(i>uscis.getClose_approved())
					uscis.setClose_approved(i);
				approved++;
			}
			else if (status==Status.Delivered) {
				delivered++;
			}
			else {
				other++;
			}
			total++;
			System.out.println("Processing "+total);
		}
		
		System.out.println(String.format("From %s, total %d cases. %d are pending(%.2f%%); %d are approved(%.2f%%); %d are mailed(%.2f%%); %d are delivred(%.2f%%).", 
				uscis.getMinDate().toString(),total,received,(1.0*received)/(1.0*total)*100.0,approved,(1.0*approved)/(1.0*total)*100.0,mailed,(1.0*mailed)/(1.0*total)*100.0,delivered,(1.0*delivered)/(1.0*total)*100.0));
		System.out.println("Most recent case is YSC"+uscis.close_approved);
	}
	
	public Status getStatus(long case_num) {
		try {
			Document doc = Jsoup.connect(
					"https://egov.uscis.gov/casestatus/mycasestatus.do?appReceiptNum="+prefix+case_num)
					.get();
			//System.out.println(doc.title());
			Elements status = doc.select("div[class=rows text-center]");
			Elements status_text = status.select("h1");
			String text = status_text.get(0).text();
			//System.out.println(status_text.get(0).text());
			if(text.contains("Received")) {
				String bodytext = status.select("p").get(0).text();
				String[] tokens = bodytext.split(" ");
				String date = tokens[1] + " " + tokens[2] + " " + tokens[3].replace(',', '\0');
				
				Date tDate = new Date(date);
				if(tDate.compareTo(minDate)<0)
					minDate = tDate;
				//System.out.println(minDate);
				
				return Status.Received;
			}
			else if(text.contains("Produced"))
				return Status.Approved;
			else if(text.contains("Mailed"))
				return Status.Mailed;
			else if(text.contains("Delievered"))
				return Status.Delivered;
			else {
				return Status.Other;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Status.Other;
		
	}

}
