package net.pobbay.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

/**
 * 祝福短信列表解析器
 *
 * @author Yichou
 *
 */
public class SmsParser extends DefaultHandler {
	public List<Sms> parse(Context context) {
		InputStream is = null;
		SAXParserFactory spf = SAXParserFactory.newInstance(); // 初始化sax解析器

		try {
			SAXParser sp = spf.newSAXParser(); // 创建sax解析器

			File file = context.getFileStreamPath("smss.xml");
			if (file.exists()) {
				is = new FileInputStream(file);
			} else {
				is = context.getAssets().open("smss.xml");
			}

			sp.parse(is, this);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return this.smsList;
	}

	private static final String TAG_SMS = "item";
	private List<Sms> smsList;

	@Override
	public void startDocument() throws SAXException {
		smsList = new ArrayList<Sms>(10);
	}

	private Sms temp = null;

	@Override
	public void startElement(String uri, String localName, String qName,
							 Attributes attributes) throws SAXException {
		if (TAG_SMS.equals(localName)) {
			temp = new Sms();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (temp != null) {
			temp.setContent(String.copyValueOf(ch, start, length));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (TAG_SMS.equals(localName)) {
			smsList.add(temp);
			temp = null;
		}
	}
}
