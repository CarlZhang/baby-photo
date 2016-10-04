package com.wintone.httpClient;

import java.io.File;
import java.io.InputStream;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;


/*
 * 作者:北京文通科技有限公司-移动事业开发部
 * 版本信息:V1.0.0.1
 * 接口调用说明:
 * 1. http接口地址="http://netocr.com/api/recog.do";
 *    http接口调用方法必须是post
 * 2. http接口接收参数说明:
	    MultipartFile file	 //上传的文件(上传文件的字段名必须是“file”)
		String key			 //用户ocrKey
		String secret		 //用户ocrSecret
		Integer typeId		 //证件类型(例如:二代证正面为"2"。详见文档说明)
		String format		 //返回格式(xml或者json)；如果format为空，则默认返回xml
 * 3.本demo需要httpClient4.3的jar包，见lib文件夹
 */

public class Client {

	public static void main(String[] args) {
		File file = new File("D:\\erdai.jpg");				//上传的待识别图片
		String key = "Fw7xopTQ9WeXXXXdBfSt4S";				//用户ocrKey
		String secret = "39cf11bcc8924e3xXXXc15d611c31fa7";	//用户ocrSecret
		String typeId = "2";								//证件类型(例如:二代证正面为"2"。详见文档说明)
		String format = "xml";
//		String format = "json"; //(返回的格式可以是xml，也可以是json)
		String url = "http://netocr.com/api/recog.do";		//http接口调用地址

		String resultback = doPost(url, file, key, secret, typeId, format); 
		
		System.out.println(resultback);						//控制台打印输出识别结果

	}

	public static String doPost(String url, File file, String key,String secret, String typeId, String format) {
		String result = "";
		try {

				CloseableHttpClient client = HttpClients.createDefault(); 										// 1.创建httpclient对象
				HttpPost post = new HttpPost(url); 																// 2.通过url创建post方法
				
				if("json".equalsIgnoreCase(format)){
					post.setHeader("accept", "application/json");
				}else if("xml".equalsIgnoreCase(format)||"".equalsIgnoreCase(format)) {
					post.setHeader("accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				}
				
				//***************************************<向post方法中封装实体>************************************//3.向post方法中封装实体
				/* post方式实现文件上传则需要使用multipart/form-data类型表单，httpclient4.3以后需要使用MultipartEntityBuilder来封装
				 * 对应的html页面表单：
					 <form name="input" action="http://netocr.com/api/recog.do" method="post" enctype="multipart/form-data">
				        	请选择要上传的文件<input  type="file" NAME="file"><br />
							key:<input type="text" name="key" value="W8Nh5AU2xsTYzaduwkzEuc" />	<br />
							secret:<input type="text" name="secret" value="9646d012210a4ba48b3ba16737d6f69f" /><br />
							typeId:<input type="text" name="typeId" value="2"/><br />
							format:<input type="text" name="format" value=""/><br />
							<input type="submit" value="提交">
					</form>
				 */
				
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();									//实例化实体构造器
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);												//设置浏览器兼容模式
	
				builder.addPart("file", new FileBody(file));														//添加"file"字段及其值；此处注意字段名称必须是"file"
				builder.addPart("key", new StringBody(key, ContentType.create("text/plain", Consts.UTF_8)));		//添加"key"字段及其值
				builder.addPart("secret", new StringBody(secret, ContentType.create("text/plain", Consts.UTF_8)));	//添加"secret"字段及其值
				builder.addPart("typeId", new StringBody(typeId, ContentType.create("text/plain", Consts.UTF_8)));	//添加"typeId"字段及其值
				builder.addPart("format", new StringBody(format, ContentType.create("text/plain", Consts.UTF_8)));	//添加"format"字段及其值
	
				HttpEntity reqEntity = builder.setCharset(CharsetUtils.get("UTF-8")).build();						//设置请求的编码格式，并构造实体
				
	
				post.setEntity(reqEntity);
				//**************************************</向post方法中封装实体>************************************
	
				CloseableHttpResponse response = client.execute(post);												 // 4.执行post方法，返回HttpResponse的对象
				if (response.getStatusLine().getStatusCode() == 200) {		// 5.如果返回结果状态码为200，则读取响应实体response对象的实体内容，并封装成String对象返回
					result = EntityUtils.toString(response.getEntity(), "UTF-8"); 
				} else {
					 System.out.println("服务器返回异常");
				}
	
				try {
					HttpEntity e = response.getEntity();					 // 6.关闭资源
					if (e != null) {
						InputStream instream = e.getContent();
						instream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					response.close();
				}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		return result;														//7.返回识别结果
		
	}

}
