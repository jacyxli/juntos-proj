package org.vamosjuntos.juntos;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;

public class ContactAsyncTask extends AsyncTask<Void, Void, Boolean> {
	final AboutUsContact abt_us_contact;
	final ArrayList<Contact> arr;

	ContactAsyncTask(AboutUsContact abt_us_contact){
		this.abt_us_contact = abt_us_contact;
		this.arr = abt_us_contact.arr;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		abt_us_contact.numContacts++;
	}


	@Override
	protected final Boolean doInBackground(Void... ignored) {
		try {
			String about_us_xml = abt_us_contact.getString(R.string.xml_about_us);
			URL url = new URL(about_us_xml);

			URLConnection connection;
			connection = url.openConnection();

			HttpURLConnection httpConnection = (HttpURLConnection)connection;
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				// Parse the about us mission feed.
				Document dom = db.parse(in);
				Element docEle = dom.getDocumentElement();

				arr.clear();

				// Get a list of each missions entry.
				NodeList nl = docEle.getElementsByTagName("staff");
				Log.d("REFRESH ", "here");

				if (nl != null && nl.getLength() > 0) {
					for (int i = 0 ; i < nl.getLength(); i++) {
						Element contact_node = (Element)nl.item(i);
						Element name = (Element)contact_node.getElementsByTagName("name").item(0);
						Element title = (Element)contact_node.getElementsByTagName("title").item(0);
						Element email = (Element)contact_node.getElementsByTagName("email").item(0);

						String name_txt = name.getFirstChild().getNodeValue();
						String title_txt = title.getFirstChild().getNodeValue();
						String email_txt = email.getFirstChild().getNodeValue();

						Log.d("Name", name_txt);

						Contact c = new Contact(name_txt, title_txt, email_txt);
						arr.add(c);				
					}
				}
			}
			return true;
		} catch (MalformedURLException e) {
			Log.d(abt_us_contact.TAG, "MalformedURLException", e);
		} catch (IOException e) {
			Log.d(abt_us_contact.TAG, "IOException", e);
		} catch (ParserConfigurationException e) {
			Log.d(abt_us_contact.TAG, "Parser Configuration Exception", e);
		} catch (SAXException e) {
			Log.d(abt_us_contact.TAG, "SAX Exception", e);
		} finally {
		}
		return false;
	}

	@Override
	protected final void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		if (success) {
			abt_us_contact.refreshContacts();
		}
	}

	public static void run(AboutUsContact c) {
		new ContactAsyncTask(c).execute();
	}
}
