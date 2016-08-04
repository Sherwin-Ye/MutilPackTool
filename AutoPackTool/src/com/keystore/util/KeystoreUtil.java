package com.keystore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;

/**
 * @author Sherwin.Ye
 * @data 2016年7月1日 下午2:03:20
 * @desc KeystoreUtil.java
 */
public class KeystoreUtil {

	/**
	 * 执行命令
	 * @param arstringCommand string[]
	 * @throws Exception
	 */
	public static void execCommand(String[] arstringCommand) throws Exception {
		for (int i = 0; i < arstringCommand.length; i++) {
			System.out.print(arstringCommand[i] + " ");
		}
		System.out.println();
		Process process;
		process = Runtime.getRuntime().exec(arstringCommand);
		if (process.waitFor() != 0) {
			throw new RuntimeException("命令行执行错误");
		}

	}

	/**
	 * 执行命令
	 * @param strCommand string
	 * @throws Exception
	 */
	public static void execCommand(String strCommand) throws Exception {
		System.out.println(strCommand);
		Process process;
		process = Runtime.getRuntime().exec(strCommand);
		if (process.waitFor() != 0) {
			throw new RuntimeException("命令行执行错误");
		}

	}

	/**
	 * 生成签名文件
	 * @param params
	 */
	public static void genKeystore(KeyStoreParams params) throws Exception {
		if (params == null) {
			throw new RuntimeException("KeyStoreParams 参数不能为空");
		}
		String[] arstringCommand = null;
		//判断创建文件夹
		File targetFile = new File(params.getTargetFilePath());
		if (targetFile.exists()) {
			targetFile.delete();
		} else {
			File targetDir = targetFile.getParentFile();
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
		}
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			arstringCommand = new String[] { "cmd ", "/c", "start", // cmd Shell命令

					"keytool", "-genkey", // -genkey表示生成密钥
					"-validity", "36000", // -validity指定证书有效期(单位：天)，这里是36000天
					"-keysize", "1024", //     指定密钥长度
					"-alias", params.getAlias(), // -alias指定别名，这里是
					"-keyalg", "RSA", // -keyalg 指定密钥的算法 (如 RSA DSA（如果不指定默认采用DSA）)
					"-keystore", params.getTargetFilePath(), // -keystore指定存储位置，这里是d:/demo.keystore
					"-dname",
					"CN=(" + params.getName() + "), OU=(" + params.getOrganizationUnit() + "), O=(" + params.getOrganizationName() + "), L=(" + params.getCity() + "), ST=("
							+ params.getProvince() + "), C=(" + params.getCountryCode() + ")",
					// CN=(名字与姓氏), OU=(组织单位名称), O=(组织名称), L=(城市或区域名称),
					// ST=(州或省份名称), C=(单位的两字母国家代码)"
					"-storepass", params.getStorepass(), // 指定密钥库的密码(获取keystore信息所需的密码)
					"-keypass", params.getKeypass(), // 指定别名条目的密码(私钥的密码)
					"-v"// -v 显示密钥库中的证书详细信息
			};
		} else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			arstringCommand = new String[] { "keytool", "-genkey", // -genkey表示生成密钥
					"-validity", "36000", // -validity指定证书有效期(单位：天)，这里是36000天
					"-keysize", "1024", //     指定密钥长度
					"-alias", params.getAlias(), // -alias指定别名，这里是
					"-keyalg", "RSA", // -keyalg 指定密钥的算法 (如 RSA DSA（如果不指定默认采用DSA）)
					"-keystore", params.getTargetFilePath(), // -keystore指定存储位置，这里是d:/demo.keystore
					"-dname",
					"CN=(" + params.getName() + "), OU=(" + params.getOrganizationUnit() + "), O=(" + params.getOrganizationName() + "), L=(" + params.getCity() + "), ST=("
							+ params.getProvince() + "), C=(" + params.getCountryCode() + ")",
					// CN=(名字与姓氏), OU=(组织单位名称), O=(组织名称), L=(城市或区域名称),
					// ST=(州或省份名称), C=(单位的两字母国家代码)"
					"-storepass", params.getStorepass(), // 指定密钥库的密码(获取keystore信息所需的密码)
					"-keypass", params.getKeypass(), // 指定别名条目的密码(私钥的密码)
					"-v"// -v 显示密钥库中的证书详细信息
			};
		} else {
			throw new RuntimeException("暂时只支持windows 和 linux 平台");
		}

		execCommand(arstringCommand);

	}

	/**
	 * 签名apk
	 * @param keyStoreFilePath 签名文件路径
	 * @param storepass 密码
	 * @param alias keystore别名
	 * @param keypass 别名密码
	 * @param apkFilePath 原apk 路径
	 * @param resultPath 签名后存放文件夹
	 * @param fileName 签名后的名字
	 * @return
	 */
	public static void signApk(SignParams params) throws Exception {

		//		//------签名
		//		String javaHome = System.getProperty("java.home");
		//		String javaBin = new File(javaHome).getParent() + "/bin";
		//		File javaBinFile = new File(javaBin);
		//判断创建文件夹
		File targetFile = new File(params.getSignedApkFilePath());
		File targetDir = targetFile.getParentFile();
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		StringBuffer buffer = new StringBuffer();
		//组合签名命令
		buffer.setLength(0);
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			buffer.append("cmd /c ");
		}
		buffer.append("jarsigner -keystore ").append(params.getKeyStoreFilePath()).append(" -storepass ").append(params.getStorepass()).append(" ").append(" -keypass ")
				.append(params.getKeypass()).append(" -signedjar ").append(params.getSignedApkFilePath()).append(" ") //签名保存路径应用名称
				.append(params.getUnsignedApkFilePath()).append(" ") //打包保存路径应用名称
				.append(params.getAlias());
		//利用命令调用JDK工具命令进行签名
		execCommand(buffer.toString());
		//		Process process;
		//		System.out.println(buffer.toString());
		//		process = Runtime.getRuntime().exec(buffer.toString(), null, javaBinFile);
		//		if (process.waitFor() != 0) {
		//			return false;
		//		}
	}

	/**
	 * 获取签名文件的别名
	 * @param keyStorePath
	 * @param storePass
	 * @return
	 */
	public static Enumeration<String> getAliases(String keyStorePath, String storePass) throws Exception{
		FileInputStream in = null;
		try {
			in = new FileInputStream(keyStorePath);
			KeyStore ks = KeyStore.getInstance("JKS");// JKS: Java KeyStoreJKS，可以有多种类型  

			ks.load(in, storePass.toCharArray());
			Enumeration<String> aliases = ks.aliases();
			return aliases;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		final KeyStoreParams keyStoreParams = new KeyStoreParams();
		keyStoreParams.setTargetFilePath("D:/resolve/result/testdemo.keystore");
		keyStoreParams.setStorepass("123456");
		keyStoreParams.setAlias("demo.keystore");
		keyStoreParams.setKeypass("12345678");
		keyStoreParams.setCity("杭州");
		keyStoreParams.setProvince("浙江");
		keyStoreParams.setCountryCode("CN");
		keyStoreParams.setName("叶晓伟");
		keyStoreParams.setOrganizationName("平台部");
		keyStoreParams.setOrganizationUnit("旭航网络科技有限公司");

		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				genKeystore(keyStoreParams);
			} else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
				keyStoreParams.setTargetFilePath("/root/demodemo.keystore");
				genKeystore(keyStoreParams);
			} else {
				return;
			}
			System.out.println("生成签名文件成功");
		} catch (Exception e) {
			System.out.println("生成签名文件失败:" + e.getMessage());
			e.printStackTrace();
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				File keystoreFile = new File(keyStoreParams.getTargetFilePath());
				while (!keystoreFile.exists()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("keystorePath:" + keyStoreParams.getTargetFilePath() + " " + keystoreFile.exists());

				}

				SignParams signParams = new SignParams();
				signParams.setAlias(keyStoreParams.getAlias());
				signParams.setKeypass(keyStoreParams.getKeypass());
				signParams.setKeyStoreFilePath(keyStoreParams.getTargetFilePath());
				signParams.setSignedApkFilePath("D:/resolve/testapk/xhmarket.apk");
				signParams.setStorepass(keyStoreParams.getStorepass());
				signParams.setUnsignedApkFilePath("D:/resolve/target/xhmarket.apk");
				try {
					signApk(signParams);
					File file = new File("D:/resolve/result/xhmarket.apk");
					System.out.println("file exists:" + file.exists());

					Enumeration<String> aliases = getAliases(keyStoreParams.getTargetFilePath(), "123456");
					while (aliases.hasMoreElements()) {
						System.out.println(aliases.nextElement());
					}
					System.out.println("签名成功");
				} catch (Exception e) {
					System.out.println("签名失败:" + e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();

	}

}
