package net.pobbay.service;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 文件列表解析
 *
 * @author Yichou
 *
 */
public class ListParser extends DefaultHandler {
	private ArrayList<Item> items;
	private String path;
	private String title;

	private Item item;
	private boolean doing = false;
	private boolean finish = false;

	private static final String TAG_DIR0 = "dir0";
	private static final String TAG_DIR1 = "dir1";

	public ListParser(String path) {
		super();
		this.path = path;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public void startDocument() throws SAXException {
		items = new ArrayList<Item>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
							 Attributes attributes) throws SAXException {
		if (finish)
			return;

		if (!doing) {
			if (TAG_DIR0.equals(localName)
					&& attributes.getValue("name").equals(path)) {
				doing = true;
				title = attributes.getValue("title");
			}
		} else if (TAG_DIR1.equals(localName)) { // 解析列表
			item = new Item();
			item.setCount(Integer.valueOf(attributes.getValue("count")));
			item.setImgName(attributes.getValue("imgname"));
			item.setName(attributes.getValue("name"));
			item.setTitle(attributes.getValue("title"));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (doing) {
			if (TAG_DIR1.equals(localName)) {
				items.add(item);
				item = null;
			} else if (TAG_DIR0.equals(localName)) { // 终止解析
				doing = false;
			}
		}
	}
}
