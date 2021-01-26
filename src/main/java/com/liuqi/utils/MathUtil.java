package com.liuqi.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 BigDecimal.setScale()方法用于格式化小数点
 setScale(1)表示保留一位小数，默认用四舍五入方式
 setScale(1,BigDecimal.ROUND_DOWN)直接删除多余的小数位，如2.35会变成2.3
 setScale(1,BigDecimal.ROUND_UP)进位处理，2.35变成2.4
 setScale(1,BigDecimal.ROUND_HALF_UP)四舍五入，2.35变成2.4
 setScaler(1,BigDecimal.ROUND_HALF_DOWN)四舍五入，2.35变成2.3，如果是5则向下舍
 setScaler(1,BigDecimal.ROUND_CEILING)接近正无穷大的舍入
 setScaler(1,BigDecimal.ROUND_FLOOR)接近负无穷大的舍入，数字>0和ROUND_UP作用一样，数字<0和ROUND_DOWN作用一样
 setScaler(1,BigDecimal.ROUND_HALF_EVEN)向最接近的数字舍入，如果与两个相邻数字的距离相等，则向相邻的偶数舍入。
 */
public class MathUtil {

	/**
	 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精 确的浮点数运算，包括加减乘除和四舍五入。
	 */
	private static final int SCALE = 12; // 这个类不能实例化

	private MathUtil() {
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static BigDecimal add(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return MathUtil.add(b1,b2);
	}
	/**
	 * 提供精确的加法运算。
	 *
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
		return v1.add(v2).setScale(SCALE,BigDecimal.ROUND_DOWN);
	}
	public static BigDecimal add(BigDecimal v1, BigDecimal v2, int scale) {
		return v1.add(v2).setScale(scale,BigDecimal.ROUND_DOWN);
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	public static BigDecimal sub(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return MathUtil.sub(b1,b2);
	}
	public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
		return v1.subtract(v2).setScale(SCALE,BigDecimal.ROUND_DOWN);
	}
	public static BigDecimal sub(BigDecimal v1, BigDecimal v2, int scale) {
		return v1.subtract(v2).setScale(scale,BigDecimal.ROUND_DOWN);
	}
	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static BigDecimal mul(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return MathUtil.mul(b1,b2);
	}
	public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
		return v1.multiply(v2).setScale(SCALE,BigDecimal.ROUND_DOWN);
	}
	public static BigDecimal mul(BigDecimal v1, BigDecimal v2, int scale) {
		return v1.multiply(v2).setScale(scale,BigDecimal.ROUND_DOWN);
	}
	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字截取。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static BigDecimal div(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return MathUtil.div(b1,b2,MathUtil.SCALE);
	}
	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字截取。
	 *
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
		return v1.divide(v2, MathUtil.SCALE, BigDecimal.ROUND_DOWN);
	}
	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字截取。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
		return v1.divide(v2, scale, BigDecimal.ROUND_DOWN);
	}
	/**
	 * 获取截取后的字符串
	 * @param v1
	 * @return
	 */
	public static String getString(BigDecimal v1) {
		return v1.setScale(MathUtil.SCALE,BigDecimal.ROUND_DOWN).toString();
	}

	/**
	 * 获取百分比  v1*100
	 * @param v1
	 * @return
	 */
	public static BigDecimal mulPercent(BigDecimal v1) {
		return MathUtil.mul(v1,new BigDecimal("100"));
	}

	/**
	 * 获取百分比  v1/100
	 * @param v1
	 * @return
	 */
	public static BigDecimal divPercent(BigDecimal v1) {
		return MathUtil.div(v1,new BigDecimal("100"));
	}

	/**
	 * 获取负数 0-v1
	 * @param v1
	 * @return
	 */
	public static BigDecimal zeroSub(BigDecimal v1) {
		return MathUtil.sub(BigDecimal.ZERO,v1);
	}

	/**
	 * 获取小数位后的位数
	 * 		0.0001返回4
	 * 		0.00010 返回4
	 * @param v1
	 * @return
	 */
	public static  int getDigits(BigDecimal v1){
		int scale=0;
		if(v1!=null){
			String value= v1.stripTrailingZeros().toPlainString();
			String[] v=value.split("\\.");
			if(v.length>1){
				scale=v[1].length();
			}
		}
		return scale;
	}
	public static  BigDecimal getBaseByDigits(int digits,int base){
		return MathUtil.mul(new BigDecimal(base),new BigDecimal(Math.pow(10,0-digits))).setScale(digits,BigDecimal.ROUND_DOWN);
	}

	static DecimalFormat decimalFormat = new DecimalFormat("0.00000000##");
	public static String format(BigDecimal quantity){
		return decimalFormat.format(quantity);
	}
	public static void main(String[] args){
	     System.out.println(getDigits(new BigDecimal("100")));
	     System.out.println(getDigits(new BigDecimal("100.0000")));
	     System.out.println(getDigits(new BigDecimal("100.0001")));
	     System.out.println(getDigits(new BigDecimal("100.000100")));
	}
}
