package com.keystore.util;

/**
 * @author Sherwin.Ye
 * @data 2016年7月6日 上午11:50:14
 * @desc SignParams.java
 */
public class SignParams {
	/**
	 * keyStore文件路径
	 */
	private String keyStoreFilePath;
	/**
	 * 签名文件库密码
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
	 * 未签名APK文件路径
	 */
	private String unsignedApkFilePath;
	/**
	 * 签名后APK文件路径
	 */
	private String signedApkFilePath;
	public String getKeyStoreFilePath() {
		return keyStoreFilePath;
	}
	public void setKeyStoreFilePath(String keyStoreFilePath) {
		this.keyStoreFilePath = keyStoreFilePath;
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
	public String getUnsignedApkFilePath() {
		return unsignedApkFilePath;
	}
	public void setUnsignedApkFilePath(String unsignedApkFilePath) {
		this.unsignedApkFilePath = unsignedApkFilePath;
	}
	public String getSignedApkFilePath() {
		return signedApkFilePath;
	}
	public void setSignedApkFilePath(String signedApkFilePath) {
		this.signedApkFilePath = signedApkFilePath;
	}
	
}
