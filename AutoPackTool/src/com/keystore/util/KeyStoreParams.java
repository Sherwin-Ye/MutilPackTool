package com.keystore.util;
/**
 * 签名文件相关参数
 * @author Sherwin.Ye
 * @data 2016年7月6日 上午11:48:11
 * @desc KeyStoreParams.java
 */
public class KeyStoreParams{
	/**
	 * keystore存放路径
	 */
	private String targetFilePath;
	/**
	 * 指定密钥库的密码
	 */
	private String storepass;
	/**
	 * 签名别名
	 */
	private String alias;
	/**
	 * 别名密码
	 */
	private String keypass;
	/**
	 * 名字与姓氏
	 */
	private String name;
	/**
	 * 组织单位名称
	 */
	private String organizationUnit;
	/**
	 * 组织名称
	 */
	private String organizationName;
	/**
	 * 城市或区域名称
	 */
	private String city;
	/**
	 * 州或省份名称
	 */
	private String province;
	/**
	 * 单位的两字母国家代码,默认CN（中国）
	 */
	private String countryCode = "CN";
	public String getTargetFilePath() {
		return targetFilePath;
	}
	public void setTargetFilePath(String targetFilePath) {
		this.targetFilePath = targetFilePath;
	}
	public String getStorepass() {
		return storepass;
	}
	public void setStorepass(String storepass) {
		this.storepass = storepass;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getKeypass() {
		return keypass;
	}
	public void setKeypass(String keypass) {
		this.keypass = keypass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrganizationUnit() {
		return organizationUnit;
	}
	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
}
