package com.liuqi.utils;

import cn.hutool.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpUtils {

	/**
	 * 获取当前网络ip
	 *
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		//先获取nginx传的
		String ipAddress = request.getHeader("X-Real-IP");
		if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("x-forwarded-for");
			if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ipAddress)|| "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
				if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
					//根据网卡取本机配置的IP
					InetAddress inet = null;
					try {
						inet = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					ipAddress = inet.getHostAddress();
				}
			}
		}
		//对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}

	public static String getCity(String ip){
		String city="";
		HttpRequest request=HttpRequest.get("http://ip.ws.126.net/ipquery?ip="+ip);
		String result=request.timeout(3000).execute().body();
		String regex="var lo=\"(.*)\", lc=\"(.*)\";";
		Pattern r=Pattern.compile(regex);
		Matcher ma=r.matcher(result);
		while(ma.find()){
			city=ma.group(1)+ " "+ma.group(2);
		}
		return city;
	}

	public static void main(String[] args) {
		System.out.println(IpUtils.getCity("219.140.227.73"));
	}
}
