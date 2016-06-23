package com.github.liyiorg.springtagi18n;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 *
 * 自动生成国际化中文属性文件
 */
public class AutoSetPropertyFile {

	private static Pattern pattern = Pattern.compile("<spring:message.*code=\"([^\"]*)\".*text=\"([^\"]*)\"[^/>]*/?>");

	private static String ROOT_FILE_PATH = "D://Workspaces/WebContent/aa_view";
	private static String OUTPUT_FILE_PATH = "D://Workspaces/i18n/i18n_zh.properties";

	private static Set<String> properties = new LinkedHashSet<String>();



	public static void main(String[] args) {
		File root_file = new File(ROOT_FILE_PATH);
		loadFile(root_file);
		outToFile();
	}

	private static void loadFile(File file){
		if(!file.isDirectory()&&file.getName().endsWith("jsp")){
			try {
				String fileText = FileUtils.readFileToString(file,"utf-8");
				getPropertis(file.getName(),fileText);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}else if(file.isDirectory()){
			for(File f : file.listFiles()){
				loadFile(f);
			}
		}
	}

	/**
	 * 获取 i18 message 键值对
	 * @param fileName
	 * @param text
	 * @return
	 */
	private static void getPropertis(String fileName,String text){
		List<String> errorList = new ArrayList<String>();
		Matcher matcher = pattern.matcher(text);
		int count = 0;
		while(matcher.find()){
			/*for(int i=0;i<=matcher.groupCount();i++){
				System.out.println(matcher.group(i));
			}*/
			if(count == 0){
				properties.add("\n####"+fileName);
			}
			if(matcher.groupCount()==2){
				try {
					String t = Native2AsciiUtils.native2Ascii(matcher.group(2));
					properties.add(matcher.group(1)+"="+t);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				errorList.add(matcher.group(0));
			}
			count++;
		}


		System.out.println("========================================");
		System.out.println("page:"+fileName);
		System.out.println("count:"+count);
		if(errorList.size()>0){
			System.out.println("error:"+errorList.size());
			System.out.println(collectionToDelimitedString(errorList,"\t\n"));
		}
	}

	private static  void outToFile(){
		try {
			FileUtils.writeStringToFile(new File(OUTPUT_FILE_PATH), collectionToDelimitedString(properties, "\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String collectionToDelimitedString(Collection<?> arr, String delim) {
		if (arr == null || arr.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(Iterator<?> it = arr.iterator();it.hasNext();){
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(it.next());
			i++;
		}
		return sb.toString();
	}

}
