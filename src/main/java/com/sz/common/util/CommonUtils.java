package com.sz.common.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanGenerator;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

/**
 * @author sjz
 */
public class CommonUtils {

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 格式化日期
     * @param date
     * @param pattern 如果为null则默认格式为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String dateFormat(Date date, String pattern) {
        if (pattern == null) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 格式化日期
     * @param date
     * @param pattern 如果为null则默认格式为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String dateFormat(Timestamp date, String pattern) {
        if (pattern == null) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(pattern).format(date);
    }
    
    /**
     * 格式化日期
     * @param date
     * @param pattern 如果为null则默认格式为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String dateFormat(String date, String pattern) throws ParseException {
        if (pattern == null) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        Date d=stringToDate(date,pattern);
        return new SimpleDateFormat(pattern).format(d);
    }

    /**
     * 将String类型的日期格式化为Date类型
     * @param date
     * @param pattern 如果为null则默认格式为yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String date, String pattern) throws ParseException {
        if (pattern == null) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat formater = new SimpleDateFormat(pattern);
        return formater.parse(date);
    }

    /**
     * 格式化数字 自动省去末尾0
     * @param num
     * @param value 保留的小数位
     * @return
     */
    public static String numberFormat(double num, int value) {
        NumberFormat formater = DecimalFormat.getInstance();
        formater.setMaximumFractionDigits(value);
        formater.setGroupingUsed(false);
        return formater.format(num);
    }

    /**
     * 格式化数字 自动省去末尾0
     * @param str
     * @param value 保留的小数位
     * @return
     */
    public static String numberFormat(String str, int value) {
        double num = Double.parseDouble(str);
        NumberFormat formater = DecimalFormat.getInstance();
        formater.setMaximumFractionDigits(value);
        formater.setGroupingUsed(false);
        return formater.format(num);
    }
    
    /**
     * 格式化数字 自动省去末尾0
     * @param str
     * @param value 保留的小数位
     * @return
     */
    public static String numberFormat(Object str, int value) {
        NumberFormat formater = DecimalFormat.getInstance();
        formater.setMaximumFractionDigits(value);
        formater.setGroupingUsed(false);
        return formater.format(str);
    }

    /**
     * 格式化数字
     * @param str
     * @param formatStyle
     *            传入null时默认为0.00
     * @return
     */
    public static String numberFormat(String str, String formatStyle) {
        if (str == null || ("").equals(str)) {
            return "";
        }
        if (formatStyle == null) {
            formatStyle = "0.00";
        }
        Double num = Double.parseDouble(str);
        DecimalFormat formater = new DecimalFormat(formatStyle);
        formater.setRoundingMode(RoundingMode.HALF_UP);
        return formater.format(num);
    }

    /**
     * 格式化数字
     * @param num
     * @param formatStyle
     *            传入null时默认为0.00
     * @return
     */
    public static String numberFormat(double num, String formatStyle) {
        if (formatStyle == null) {
            formatStyle = "0.00";
        }
        DecimalFormat formater = new DecimalFormat(formatStyle);
        formater.setRoundingMode(RoundingMode.HALF_UP);
        return formater.format(num);
    }

    /**
     * util.Date转换为sql.Date
     * @param date
     * @return
     */
    public static java.sql.Date utilToSql(Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * 返回传入的日期的前N天或后N天的日期
     * @param appDate
     * @param format
     * @param days
     * @return
     */
    public static String getFutureDay(String appDate, String format, int days) {
        String future = "";
        try {
            Calendar calendar = GregorianCalendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(appDate);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, days);
            date = calendar.getTime();
            future = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return future;
    }
    
    /**
     * 返回传入的日期的前N个月或后N个月的日期
     * @param appDate
     * @param format
     * @param months
     * @return
     */
    public static String getFutureMonth(String appDate, String format, int months) {
        String future = "";
        try {
            Calendar calendar = GregorianCalendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(appDate);
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, months);
            date = calendar.getTime();
            future = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return future;
    }

    /**
     * 返回当前的时间戳
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }


    /**
     * 取得随机字符串
     * @param len
     *            长度
     * @param type
     *            类型 1:数字+字母混合 2:字母 3:数字
     * @return
     */
    public static String getRandomStr(int len, int type) {
        String str = "";
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            if (type == 1) {
                String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
                if (("char").equals(charOrNum)) {
                    str += (char) (choice + random.nextInt(26));
                } else if (("num").equals(charOrNum)) {
                    str += String.valueOf(random.nextInt(10));
                }
            } else if (type == 2) {
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                str += (char) (choice + random.nextInt(26));
            } else if (type == 3) {
                str += String.valueOf(random.nextInt(10));
            } else {
                str = "";
            }
        }
        return str;
    }

    /**
     * 根据长度和给定的字符数组生成随机字符串
     * @param len 字符串长度
     * @param charStr 字符数据组
     * @return
     */
    public static String getRandomStr(int len, String charStr) {
        String result = "";
        if (charStr == null) {
            return result;
        }
        int max = charStr.length();
        if (max == 0) {
            return result;
        }
        
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            int choice = random.nextInt(max) % max;
            result += charStr.charAt(choice);
        }
        return result;
    }

    /**
     * 将字符串的指定位置替换成相同的字符
     * @param str
     * @param startindex 开始替换的位置
     * @param endindex 结束替换的位置
     * @param newChar 替换成的字符
     * @return
     * @throws Exception
     */
    public static String strReplace(String str, int startindex, int endindex, String newChar) throws Exception {
        String s1 = "";
        String s2 = "";
        try {
            s1 = str.substring(0, startindex - 1);
            s2 = str.substring(endindex, str.length());
        } catch (Exception ex) {
            throw new Exception("替换的位数大于字符串的位数");
        }
        Integer length = endindex - startindex;
        String charTmp = newChar;
        for (int i = 0; i < length; i++) {
            newChar += charTmp;
        }
        return s1 + newChar + s2;
    }

    /**
     * 检测密码强度
     * @param pwd
     * @return
     */
    public static int checkPwd(String pwd) {
        String regex = "^(?:([a-z])|([A-Z])|([0-9])|(.)){6,}|(.)+$";
        return pwd.replaceAll(regex, "$1$2$3$4$5").length();
    }
    
    public static Map<String,Object> doParameters(HttpServletRequest request){
		Map<String,Object> param=new HashMap<String,Object>();
		Map<String,String[]> paramMap=request.getParameterMap();
		for(String key : paramMap.keySet()){
			String[] value=paramMap.get(key);
			if(value!=null && value[0]!=null){
				param.put(key, StringUtils.join(value));
			}
		}
		return param;
	}
    
    /**
     * 获取客户端真实IP
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {  
        String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if(ip.equals("127.0.0.1")){  
				InetAddress inet=null;
			try {
				inet = InetAddress.getLocalHost();
		    }catch (UnknownHostException e) {
		    	e.printStackTrace();
		    }
		    	ip= inet.getHostAddress();
			}
		} 
		if(ip!=null && ip.length()>15){ 
			if(ip.indexOf(",")>0){
				ip = ip.substring(0,ip.indexOf(","));
			}
		}
        return ip;  
    }
    
    public static boolean isBaseClass(Class<?> clz) {
		return clz != null && clz.getClassLoader() == null;
	}
    
    /**
     * 判断是否为数值
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }
    
    /**
     * 动态添加属性值
     * @param t 实体
     * @param columns 属性数组
     * @return
     */
    public static <T> T generateBean(T t,String... columns){
		BeanGenerator generator = new BeanGenerator();
		generator.setSuperclass(t.getClass());
		for(String str : columns){
			generator.addProperty(str, String.class); 
		}
		Object o=generator.create();
		BeanUtils.copyProperties(t, o);
		return (T)o;
	}
    
    /**
     * 下载文件
     * @param request
     * @param response
     * @param realPath 文件真实路径
     * @param fileName 给下载的文件起个名字,需要带后缀
     * @throws IOException
     */
    public static void downFile(HttpServletRequest request,HttpServletResponse response,String realPath,String fileName) throws IOException{
    	String agent = request.getHeader("USER-AGENT");
		response.setContentType("application/x-download");
		if(agent!=null && (agent.indexOf("Firefox")>-1 || agent.indexOf("Safari")>-1)){
			response.setHeader("Content-disposition", "attachment; filename="+ new String(fileName.getBytes("UTF-8"),"ISO8859-1"));
		}else{
			response.setHeader("Content-disposition", "attachment; filename="+ URLEncoder.encode(fileName, "UTF-8"));
		}
		InputStream in=new BufferedInputStream(new FileInputStream(realPath));
		OutputStream os=response.getOutputStream();
		byte[] bit=new byte[1024];
		int i=0;
		while (-1 != (i = in.read(bit, 0, bit.length))){
			os.write(bit,0,i);
		}
		os.flush();
		os.close();
		in.close();
    }
    
    /**
     * 将\r\n替换为<br>
     * @param str
     * @return
     */
    public static String replaceRN(String str){
    	return str.replaceAll("\r\n", "<br>").replaceAll("\n\r", "<br>").replaceAll("\n", "<br>").replaceAll("\r", "<br>");
    }
    
    /**
     * 判断浏览器版本
     * @param agent
     * @return
     */
    public static String getBrowserName(String agent){
    	agent=agent.toLowerCase();
    	String browser="other";
    	if(agent.indexOf("msie 6")>-1){
    		browser="ie6";
    	}else if(agent.indexOf("msie 7")>-1){
			browser="ie7";
		}else if(agent.indexOf("msie 8")>-1){
			browser="ie8";
		}else if(agent.indexOf("msie 9")>-1){
			browser="ie9";
		}else if(agent.indexOf("msie 10")>-1){
			browser="ie10";
		}else if(agent.indexOf("gecko")>-1 && agent.indexOf("rv:11")>-1){
			browser="ie11";
		}else if(agent.indexOf("chrome")>-1){
			browser="chrome";
		}else if(agent.indexOf("firefox")>-1){
			browser="firefox";
		}
		return browser;
    }
    
    /**
     * 判断是否为空
     * <pre>
     * isBlank(null)      = true
     * isBlank("")        = true
     * isBlank(" ")       = true
     * isBlank("bob")     = false
     * isBlank("  bob  ") = false
     * </pre>
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 判断是否为空
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    public static String sendHttpPost(String url,Map<String,Object> param) throws HttpException, IOException{
    	HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(url);
		if(param!=null){
			for(Entry<String, Object> entry : param.entrySet()){
				post.addParameter(entry.getKey(),String.valueOf(entry.getValue()));
			}
		}
		post.getParams().setContentCharset("UTF-8");
		client.executeMethod(post);
		String rspStr=post.getResponseBodyAsString();
		return rspStr;
    }
    
    public static JSON xml2json(String xml){
    	XMLSerializer xmlSerializer = new XMLSerializer();
    	xmlSerializer.setForceTopLevelObject(true);
    	JSON json=xmlSerializer.read(xml);
    	return json;
    }
    
    public static String json2xml(String rootName,String jsonStr){
    	XMLSerializer xmlSerializer = new XMLSerializer();
    	JSON json=JSONSerializer.toJSON(jsonStr);
    	xmlSerializer.setRootName(rootName);
    	String xml=xmlSerializer.write(json);
    	return xml;
    }
    
    /**
     * 实体转Map
     * @param bean
     * @return
     */
    public static Map bean2map(Object bean){
    	Map map=new HashMap();
    	try{
	    	Class type=bean.getClass();
	    	BeanInfo beanInfo=Introspector.getBeanInfo(type);
	    	PropertyDescriptor[] propertyDescriptor=beanInfo.getPropertyDescriptors();
	    	for(PropertyDescriptor descriptor : propertyDescriptor){
	    		String propertyName=descriptor.getName();
	    		if(!propertyName.equals("class")){
	    			Method readMethod=descriptor.getReadMethod();
	    			Object result=readMethod.invoke(bean);
	    			map.put(propertyName, result);
	    		}
	    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return map;
    }
    
    /**
     * Map转实体
     * @param type
     * @param map
     * @return
     */
    public static Object map2bean(Class type,Map map){
    	Object obj=null;
    	try{
    		obj=type.newInstance();
    		BeanInfo beanInfo=Introspector.getBeanInfo(type);
    		PropertyDescriptor[] propertyDescriptor=beanInfo.getPropertyDescriptors();
	    	for(PropertyDescriptor descriptor : propertyDescriptor){
	    		String propertyName=descriptor.getName();
	    		if(map.containsKey(propertyName)){
	    			Object value=map.get(propertyName);
	    			descriptor.getWriteMethod().invoke(obj, value);
	    		}
	    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return obj;
    }
    
    /**
     * 取得最小值和最大值之间的随机整数
     * @param min
     * @param max
     * @return
     */
    public static int getRandomInteger(int min,int max) {
    	Random rand=new Random();
		return rand.nextInt(max-min+1)+min;
    }
    
    public static long getRandomID() {
    	int size=18;
    	String uuid=UUID.randomUUID().toString();
		int hashCodeVal=uuid.hashCode();
		if(hashCodeVal<0) {
			hashCodeVal=-hashCodeVal;
		}
		String str=String.valueOf(hashCodeVal);
		if(str.length()<size) {
			String randomStr=getRandomStr(size-str.length(),3);
			str=str+randomStr;
		}
		return Long.parseLong(str);
    }
    
    public static void doZip(OutputStream outputStream,String folderName,List<File> files) throws IOException {
		if(folderName==null) {
			folderName="";
		}
		ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
		byte[] buf = new byte[2048];
		int len;
		for(File file : files) {
			zipOutputStream.putNextEntry(new ZipEntry(folderName+"/"+file.getName()));
			BufferedInputStream bufferedInputStream=new BufferedInputStream(new FileInputStream(file));
			while ((len = bufferedInputStream.read(buf)) > 0) {
				zipOutputStream.write(buf, 0, len);
			}
			bufferedInputStream.close();
		}
		zipOutputStream.close();
	}
    
    
    /**
	 * 获取指定日期的星期
	 * @param date
	 * @return
	 */
	public static int getWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int week=calendar.get(Calendar.DAY_OF_WEEK)-1;
		if(week==0) {//周日
			week=7;
		}
		return week;
	}
	
	/**
	 * 获取指定日期的年和第几周
	 * @param date
	 * @return
	 */
	public static String getWeekOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(7);
		calendar.setTime(date);
		int week=calendar.get(Calendar.WEEK_OF_YEAR);
		int weekYear=calendar.get(Calendar.YEAR);
		int weekMonth=calendar.get(Calendar.MONTH)+1;
		if(week>40 && weekMonth==1) {//指定年的第一周如果跨年 则记为前一年的最后一周
			weekYear-=1;
		}
		String str=weekYear+","+week;
		return str;
		
	}
	
	/**
	 * 获取指定年的第几周的开始日期
	 * @param weekYear 年
	 * @param weekOfYear 指定年的第几周
	 * @return
	 */
	public static Date getBeginDateByWeekofYear(int weekYear,int weekOfYear) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(7);
		calendar.setWeekDate(weekYear, weekOfYear, Calendar.MONDAY);
		Date date=calendar.getTime();
		return date;
	}
	
	/**
	 * 获取指定年的第几周的结束日期
	 * @param weekYear 年
	 * @param weekOfYear 指定年的第几周
	 * @return
	 */
	public static Date getEndDateByWeekofYear(int weekYear,int weekOfYear) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(7);
		calendar.setWeekDate(weekYear, weekOfYear, Calendar.SUNDAY);
		Date date=calendar.getTime();
		return date;
	}
	
	/**
	 * 获取指定年的最大周数
	 * @param year
	 * @return
	 */
	public static int getMaxYearWeek(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(7);
		calendar.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
		int week=calendar.get(Calendar.WEEK_OF_YEAR);
		return week;
	}
	
	/***
	 * 根据年份和月份获取当前月份的最后一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, value);
		String dateStr=dateFormat(cal.getTime(),"yyyy-MM-dd");
		return dateStr;
	}
	
	/**
	 * 根据年、第几周、星期几获取日期
	 * @param weekYear 年
	 * @param weekOfYear 第几周
	 * @param week 星期几
	 * @return
	 */
	public static Date getDateByYearWeek(int weekYear,int weekOfYear,int week) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(7);
		calendar.setWeekDate(weekYear, weekOfYear, getCalendarWeek(week));
		Date date=calendar.getTime();
		return date;
	}
	
	public static int getCalendarWeek(int week) {
		switch(week){
			case 1:
				return Calendar.MONDAY;
			case 2:
				return Calendar.TUESDAY;
			case 3:
				return Calendar.WEDNESDAY;
			case 4:
				return Calendar.THURSDAY;
			case 5:
				return Calendar.FRIDAY;
			case 6:
				return Calendar.SATURDAY;
			case 7:
				return Calendar.SUNDAY;
			default:
				return Calendar.SUNDAY;
		}
	}
	
	/**
	 * 返回传入的日期的前N周或后N周
	 * @param date
	 * @param weeks
	 * @return
	 */
	public static String getFutureWeekOfYear(Date date, int weeks) {
        String future = "";
        try {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, weeks);
            date = calendar.getTime();
            future=getWeekOfYear(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return future;
    }

    public static void main(String[] args) {
        try {
        	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
