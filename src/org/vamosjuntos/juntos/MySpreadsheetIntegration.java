package org.vamosjuntos.juntos;

import android.util.Log;
import android.widget.Toast;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;
import java.io.IOException;
import java.net.*;
import java.util.List;

public class MySpreadsheetIntegration {

	private static final String USERNAME = "vamosjuntosphilly@gmail.com";
	private static final String PASSWORD = "20292029";
	private static final String FIRSTNAME = "FirstName";
	private static final String LASTNAME = "LastName";
	private static final String EMAIL = "Email";
	private static final String PHONE = "Phone";
	
	private static final String SPREADSHEETNAME = "JuntosApp";
	private static final String WORKSHEETNAME = "UserInfo";

	private static String fn, ln, email, phone;

	private static URL SPREADSHEET_FEED_URL;
	private static SpreadsheetService service;

	public boolean ifAddedRow, ifUpdatedRow;

	public MySpreadsheetIntegration(String _fn, String _ln, String _e, String _p) {
		fn = _fn;
		ln = _ln;
		email = _e;
		phone = _p;
		ifAddedRow = false;
		ifUpdatedRow = false;
	}

	public void addRow() {
		Thread t = new Thread(new Runnable() {
			public void run() { 
				addListRow();
			}
		});
		t.start();	
	}

	public void updateRow() {
		Thread t = new Thread(new Runnable() {
			public void run() { 
				updateListRow();
			}
		});
		t.start();	
	}

	private void updateListRow() {
		service = new SpreadsheetService("juntos-mobileapp-v1");
		SpreadsheetFeed feed;
		try {
			service.setUserCredentials(USERNAME, PASSWORD);
			SPREADSHEET_FEED_URL = new URL ("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			if (spreadsheets.size() == 0) 
				Log.d("SpreadSheet", "There were no spreadsheets");
		    for (SpreadsheetEntry spreadsheet : spreadsheets) {
		    	// find the designated spreadsheet
		    	if (spreadsheet.getTitle().getPlainText().equals(SPREADSHEETNAME)) {
					Log.d("DEBUG", "spreadsheet title: "+ spreadsheet.getTitle().getPlainText());
					WorksheetFeed worksheetFeed = service.getFeed(
							spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
					List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
					WorksheetEntry worksheet = worksheets.get(0);
					Log.d("DEBUG", "worksheet title: " +worksheet.getTitle().getPlainText());
					// fetch the list feed 
					URL listFeedUrl = worksheet.getListFeedUrl();
					ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
					// iterate through each row
					boolean fn_match = false;
					boolean ln_match = false;
					for (ListEntry row : listFeed.getEntries()) {
						int i=0;
						for (String tag : row.getCustomElements().getTags()) {
							if (i==0 && row.getCustomElements().getValue(tag).equals(fn)) 
								fn_match = true;
							if (i==1 && row.getCustomElements().getValue(tag).equals(ln)) 
								ln_match = true;
							i++;
							if (i>1) break;
						}
						if (fn_match && ln_match) {
							row.getCustomElements().setValueLocal(EMAIL, email);
							row.getCustomElements().setValueLocal(PHONE, phone);
							row.update();
							ifUpdatedRow = true;
						}
					}
					// If not updated, create a new row
					if (!ifUpdatedRow) {
						ListEntry row = new ListEntry();
						row.getCustomElements().setValueLocal(FIRSTNAME, fn);
						row.getCustomElements().setValueLocal(LASTNAME, ln);
						row.getCustomElements().setValueLocal(EMAIL, email);
						row.getCustomElements().setValueLocal(PHONE, phone);
						row = service.insert(listFeedUrl, row);
					}
					break;
		    	}
		    }


		} catch (AuthenticationException e) {
			Log.e("USERINFO: setUserCredentials", e.toString());
		} catch (MalformedURLException e) {
			Log.e("USERINFO: SpreadsheetFeedURL", e.toString());
		} catch (IOException e) {
			Log.e("USERINFO: IOException", e.toString());
		} catch (ServiceException e) {
			Log.e("USERINFO: ServiceGetFeed", e.toString());
		}
	}

	private void addListRow() {
		service = new SpreadsheetService("juntos-mobileapp-v1");
		SpreadsheetFeed feed;
		try {
			service.setUserCredentials(USERNAME, PASSWORD);
			SPREADSHEET_FEED_URL = new URL ("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();
			if (spreadsheets.size() == 0) 
				Log.d("SpreadSheet", "There were no spreadsheets");
			
		    for (SpreadsheetEntry spreadsheet : spreadsheets) {
		    	// find the designated spreadsheet
		    	if (spreadsheet.getTitle().getPlainText().equals(SPREADSHEETNAME)) {
					Log.d("DEBUG", "spreadsheet title: "+spreadsheet.getTitle().getPlainText());
					WorksheetFeed worksheetFeed = service.getFeed(
							spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
					List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
					WorksheetEntry worksheet = worksheets.get(0);
					Log.d("DEBUG", "worksheet title: " +worksheet.getTitle().getPlainText());
					
					// fetch the list feed of worksheets
					URL listFeedUrl = worksheet.getListFeedUrl();
					ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
					
					// create a new row
					ListEntry row = new ListEntry();
					row.getCustomElements().setValueLocal(FIRSTNAME, fn);
					row.getCustomElements().setValueLocal(LASTNAME, ln);
					row.getCustomElements().setValueLocal(EMAIL, email);
					row.getCustomElements().setValueLocal(PHONE, phone);
					// insert the new row
					row = service.insert(listFeedUrl, row);
					
		    		break;
		    	}
		    }


		} catch (AuthenticationException e) {
			Log.e("USERINFO: setUserCredentials", e.toString());
		} catch (MalformedURLException e) {
			Log.e("USERINFO: SpreadsheetFeedURL", e.toString());
		} catch (IOException e) {
			Log.e("USERINFO: IOException", e.toString());
		} catch (ServiceException e) {
			Log.e("USERINFO: ServiceGetFeed", e.toString());
		}
	}


}