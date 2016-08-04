package com.yeliu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.keystore.util.KeystoreUtil;
import com.keystore.util.SignParams;
import com.sinaapp.msdxblog.apkUtil.entity.ApkInfo;
import com.sinaapp.msdxblog.apkUtil.utils.ApkUtil;
import com.sinaapp.msdxblog.apkUtil.utils.XmlUtil;
import com.view.ConfigViewHolder;
import com.view.MetaSelectDialog;
import com.view.MetaSelectDialog.OnSelectCallback;
import com.view.VFlowLayout;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then
 * you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of
 * Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO
 * JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class Main extends javax.swing.JFrame {
	private static final long serialVersionUID = 6341520019159072965L;
	
	private JPanel jPanelContent;

	// =====apk 选择=====
	
	private JPanel jPanelApkSelect = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JLabel jLabelApkSelect;
	private JTextField jTextFieldApkSelect;
	private JButton jButtonApkSelect;
	private JButton jButtonMetaRefresh;

	// =====JDK===
	private JPanel jPanelJdk = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JComboBox jComboBoxJdk;
	// =====多渠道打包=====
	private JPanel jPanelAddChannel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JButton jButtonAddChannel;// 多渠道打包添加配置按钮

	//=== 输出=======
	private JPanel jPanelOut = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JButton jButtonOutClear;
	private JTextField jTextFieldOut;
	private JButton jButtonOut;
	private JLabel jLabelOut;

	// =====签名====
	private JPanel jPanelSign = new JPanel(new VFlowLayout());
	private JPanel jPanelKeyStore = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JCheckBox jCheckBoxSign;
	private JButton jButtonSign;
	private JTextField jTextFieldSignFile;
	private JPanel jPanelKeyStorePwd = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JTextField jTextFieldSignPwd;
	private JPanel jPanelAlias = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JComboBox jComboBoxAlias;//选择别名
	private JPanel jPanelAliasPwd = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JTextField jTextFieldAliaPwd;//别名密码

	//===开始执行=====
	private JPanel jPanelStart = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JButton jButtonStart;

	private static final String BAT = "start.bat";
	private static final String CONFIG_FILE = "/config.txt";

	private String rootDirPath = null;
	private String processDirPath = null; // 进程路径，当前进程运行时临时文件的位置
	String batPath; // bat路径
	String oriApkPath; // 原apk路径
	String keystoreFilePath; // 签名文件路径
	String apkName; // apk名称，不带后缀
	String rootDir; // 目录盘
	String outputDir; // 输出apk路径
	String versionName;
	String packageName;
	String keystorePasswd;
	String apktoolPath;

	private Document xmlDoc;

	private String decompile_add;
	private String compile_add;
	private String sign_add;

	private List<ConfigViewHolder> configViews = new ArrayList<ConfigViewHolder>(); // 多渠道打包配置

	private Map<String, Element> metaDataMap = new HashMap<String, Element>(); //metaData节点

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main inst = new Main();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public Main() {
		super();
		initGUI();
	}

	/**
	 * 初始化UI
	 */
	private void initGUI() {
		try {
			setTitle("批量渠道打包&签名工具");
			jPanelContent=new JPanel(new VFlowLayout());
			JScrollPane jScrollPane=new JScrollPane();
			jScrollPane.setViewportView(jPanelContent);
			setContentPane(jScrollPane);
			// ========== 选择JDK版本======
			{
				JLabel jLabelJdk = new JLabel();
				jLabelJdk.setText("JDK版本");
				jPanelJdk.add(jLabelJdk);
				ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(new String[] { "1.7+", "1.6" });
				jComboBoxJdk = new JComboBox();
				jComboBoxJdk.setModel(jComboBox1Model);
				jPanelJdk.add(jComboBoxJdk);
				jPanelContent.add(jPanelJdk);
			}
			// ========== 选择需要多渠道打包的APK======
			{
				jLabelApkSelect = new JLabel();
				jPanelContent.add(jLabelApkSelect);
				jLabelApkSelect.setText("选择APK路径，apk路径和文件名不能带中文");
				jLabelApkSelect.setForeground(Color.red);
			}
			{
				
				jTextFieldApkSelect = new JTextField();
				jButtonApkSelect = new JButton("浏览");
				jButtonMetaRefresh = new JButton("刷新");
				jPanelContent.add(jPanelApkSelect);
				jPanelApkSelect.add(jTextFieldApkSelect);
				jPanelApkSelect.add(jButtonApkSelect);
				jPanelApkSelect.add(jButtonMetaRefresh);
				
				jTextFieldApkSelect.setPreferredSize(new Dimension(450, 30));
				jTextFieldApkSelect.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
				jTextFieldApkSelect.setEditable(false);
				
				jButtonApkSelect.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						openApkFile();
					}
				});
				
				jButtonMetaRefresh.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						//						jTextFieldApkSelect.setText(null);
						// 刷新meta 内容
						File file = new File(processDirPath + File.separator + apkName + "\\AndroidManifest.xml");
						if (file.exists()) {
							checkParams();
							StringBuffer content = new StringBuffer();
							content.append(rootDir).append("\n").append("cd ").append(rootDirPath).append("\n");//切换到指定目录下
							content.append("cmd /c mkdir ").append(processDirPath).append("\\tmp\n")//创建文件夹
									.append("cmd /c copy /y ").append(processDirPath + File.separator + apkName).append("\\AndroidManifest.xml ").append(processDirPath)
									.append("\\tmp\\AndroidManifest.xml \n").append("del /q ").append(processDirPath + File.separator + apkName).append("\\AndroidManifest.xml ").append("\n")//删除解析出来的AndroidManifest
									.append("exit");//文件解压完成
							write(batPath, content.toString());
							compile();
							destroyBat();
						}
						File fileTemp = new File(processDirPath + "\\tmp\\AndroidManifest.xml ");
						if (fileTemp.exists()) {
							getMetaData();
						}
					}
				});

			}
			{
				jButtonAddChannel = new JButton();
				jPanelAddChannel.add(jButtonAddChannel);
				jPanelContent.add(jPanelAddChannel);
				jButtonAddChannel.setText("添加渠道配置");
				jButtonAddChannel.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if (metaDataMap.size() == 0) {
							showErrorDialog("还没选择Apk,或Manifest中无meta-data标签");
						} else {
							addChannelPanel();
						}
					}
				});

			}

			// ========== 选择APK输出路径======
			{
				jLabelOut = new JLabel();
				jPanelContent.add(jLabelOut);
				jLabelOut.setText("选择APK输出路径");
				jLabelOut.setForeground(Color.red);
			}

			{
				jTextFieldOut = new JTextField();
				jButtonOut = new JButton("浏览");
				jButtonOutClear = new JButton("清空");
				jPanelOut.add(jTextFieldOut);
				jPanelOut.add(jButtonOut);
				jPanelOut.add(jButtonOutClear);
				jPanelContent.add(jPanelOut);
				jTextFieldOut.setPreferredSize(new Dimension(450, 30));
				jTextFieldOut.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
				jTextFieldOut.setEditable(false);
				jButtonOut.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						openOutputDir();
					}
				});
				jButtonOutClear.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						jTextFieldOut.setText(null);
						outputDir = null;
					}
				});
			}
			{
				jCheckBoxSign = new JCheckBox("签名");
				jCheckBoxSign.setSelected(true);
				jPanelContent.add(jCheckBoxSign);
				jCheckBoxSign.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
//						jPanelSign.setVisible(jCheckBoxSign.isSelected());
						boolean selected=jCheckBoxSign.isSelected();
						jTextFieldSignPwd.setEnabled(selected);
						jTextFieldAliaPwd.setEnabled(selected);
						jComboBoxAlias.setEnabled(selected);
						jButtonSign.setEnabled(selected);
					}
				});
				
				jPanelContent.add(jPanelSign);
				jPanelKeyStore.setBorder(null);
				jPanelSign.add(jPanelKeyStore);
				jPanelSign.add(jPanelKeyStorePwd);
				jPanelSign.add(jPanelAlias);
				jPanelSign.add(jPanelAliasPwd);
				
				jTextFieldSignFile = new JTextField();
				jTextFieldSignFile.setEditable(false);
				jTextFieldSignFile.setPreferredSize(new Dimension(450, 30));
				jPanelKeyStore.add(jTextFieldSignFile);
				jButtonSign = new JButton("浏览");
				jPanelKeyStore.add(jButtonSign);
				jButtonSign.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						openKeystoreFile();
					}
				});
				jTextFieldSignPwd = new JTextField();
				jTextFieldSignPwd.setPreferredSize(new Dimension(450, 30));
				jPanelKeyStorePwd.add(jTextFieldSignPwd);
				jTextFieldSignPwd.setText("请输入签名密码(storepass)");
				jTextFieldSignPwd.addFocusListener(new FocusAdapter() {
					public void focusGained(java.awt.event.FocusEvent e) {
						if ("请输入签名密码(storepass)".equals(jTextFieldSignPwd.getText().trim())) {
							jTextFieldSignPwd.setText("");
						}
					};

					public void focusLost(java.awt.event.FocusEvent e) {
						if ("".equals(jTextFieldSignPwd.getText().trim())) {
							jTextFieldSignPwd.setText("请输入签名密码(storepass)");
							return;
						}
						keystorePasswd = null;
						if (keystoreFilePath != null && !"".equals(keystoreFilePath.trim())) {
							try {
								Enumeration<String> eAliases = KeystoreUtil.getAliases(keystoreFilePath, jTextFieldSignPwd.getText());
								if (eAliases != null) {
									String[] aliases = Collections.list(eAliases).toArray(new String[0]);
									ComboBoxModel jComboBoxModel = new DefaultComboBoxModel<String>(aliases);
									jComboBoxAlias.setModel(jComboBoxModel);
									keystorePasswd = jTextFieldSignPwd.getText();
									return;
								}
							} catch (Exception e2) {
								if (!"".equals(jTextFieldSignPwd.getText()) && !"".equals(keystoreFilePath)) {
									showErrorDialog(e2.getMessage());
								}
							}
						}
						ComboBoxModel jComboBoxModel = new DefaultComboBoxModel<String>();
						jComboBoxAlias.setModel(jComboBoxModel);

					};
				});
				jComboBoxAlias = new JComboBox();
				jComboBoxAlias.setPreferredSize(new Dimension(450, 30));
				jTextFieldAliaPwd = new JTextField();
				jTextFieldAliaPwd.setPreferredSize(new Dimension(450, 30));
				jPanelAlias.add(jComboBoxAlias);
				jPanelAliasPwd.add(jTextFieldAliaPwd);
				
				jTextFieldAliaPwd.setText("请输入别名密码(keypass)");
				jTextFieldAliaPwd.addFocusListener(new FocusAdapter() {
					public void focusGained(java.awt.event.FocusEvent e) {
						if ("请输入别名密码(keypass)".equals(jTextFieldAliaPwd.getText().trim())) {
							jTextFieldAliaPwd.setText("");
						}
					};
					public void focusLost(java.awt.event.FocusEvent e) {
						if ("".equals(jTextFieldAliaPwd.getText().trim())) {
							jTextFieldAliaPwd.setText("请输入别名密码(keypass)");
							return;
						}
					};
				});

			}
			{
				jButtonStart = new JButton();
				jPanelStart.add(jButtonStart);
				jPanelContent.add(jPanelStart);
				jButtonStart.setText("开始");
				jButtonStart.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						start();
					}
				});
			}
			this.setSize(713, 550);
			// 初始化参数
			initConfigProperties();
//			setDefaultCloseOperation(EXIT_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {

					deleteDir(processDirPath);
					//					deleteDir(new File(processDirPath));
					saveConfig();
					System.exit(0);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化配置参数
	 */
	private void initConfigProperties() {
		if (rootDirPath == null) {
			//			apktoolPath = getClass().getResource("").getPath().substring(1);
			rootDirPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath().substring(1);
			File file = new File(rootDirPath);
			//			if (file.isFile()) {
			rootDirPath = file.getParent() + File.separator;
			rootDir = rootDirPath.substring(0, 2);
			// 创建零时文件
			processDirPath = rootDirPath +"temp"+ File.separator+ System.currentTimeMillis() + File.separator;
			File processDir = new File(processDirPath);
			if (!processDir.exists()) {
				processDir.mkdirs();
			}
			//			}
			batPath = processDirPath + "\\" + BAT;
		}
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(rootDirPath + CONFIG_FILE));
			oriApkPath = p.getProperty("apkFile");
			outputDir = p.getProperty("outFile");
			keystoreFilePath = p.getProperty("keyFile");
			jTextFieldSignFile.setText(keystoreFilePath);
			jTextFieldApkSelect.setText("");
			jTextFieldOut.setText(outputDir);
		} catch (FileNotFoundException e) {
			//			e.printStackTrace();
		} catch (IOException e) {
			//			e.printStackTrace();
		}
	}

	/**
	 * 删除某个文件夹
	 * @param dirPath
	 */
	private void deleteDir(final String dirPath) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(dirPath);
				if (file.exists()) {
					String cmd = "cmd /c rd /s /q " + dirPath + " \n exit";
					exeCmd(cmd);
				}
			}
		}).start();
		
	}

	/**
	 * 保存路径等一些配置
	 */
	private void saveConfig() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Properties p = new Properties();
				try {
					File fOut = new File(rootDirPath + CONFIG_FILE);
					if (!fOut.exists()) {
						fOut.createNewFile();
					}

					p.load(new FileInputStream(rootDirPath + CONFIG_FILE));

					if (oriApkPath != null) {
						p.setProperty("apkFile", oriApkPath);
					} else {
						p.remove("apkFile");
					}
					if (outputDir != null) {
						p.setProperty("outFile", outputDir);
					} else {
						p.remove("outFile");
					}
					if (keystoreFilePath != null) {
						p.setProperty("keyFile", keystoreFilePath);
					} else {
						p.remove("keyFile");
					}

					p.store(new FileOutputStream(fOut), "");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * 添加一个渠道配置
	 */
	private void addChannelPanel() {
		// 一个配置
		final ConfigViewHolder viewHolder = new ConfigViewHolder(configViews.size() + 1);
		viewHolder.jButtonMetaSelect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MetaSelectDialog dialog = new MetaSelectDialog(Main.this, metaDataMap, configViews, viewHolder, new OnSelectCallback() {

					@Override
					public void onSelected(Set<String> metaDataSet) {
						if (metaDataSet.size() > 0) {
							viewHolder.metaSet = metaDataSet;
							String metas = "";
							for (String string : metaDataSet) {
								metas += "," + string;
							}
							viewHolder.jTextFieldMates.setText(metas.substring(1));
						} else {
							viewHolder.jTextFieldMates.setText("");
						}
					}
				});
			}
		});
		viewHolder.jButtonDeleteConfig.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				viewHolder.removeAll();
				configViews.remove(viewHolder);
				for (int i = 0, count = configViews.size(); i < count; i++) {
					ConfigViewHolder holder = configViews.get(i);
					holder.updatePosition(i + 1);
				}
			}
		});
		viewHolder.setContainer(jPanelContent,configViews.size());
		configViews.add(viewHolder);
		// 刷新页面
		validate();
	}

	/**
	 * 选择APK文件
	 */
	private void openApkFile() {
		JFileChooser jc = new JFileChooser(oriApkPath);
		jc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int value = jc.showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION) {
			File f = jc.getSelectedFile();
			if (f == null || !f.getName().endsWith("apk")) {
				return;
			}
			oriApkPath = f.getAbsolutePath();
			apkName = f.getName().replaceAll(".apk", "");
			jTextFieldApkSelect.setText(oriApkPath);
			resolveApk(f);
			getMetaData();
		}
	}

	/**
	 * 选择签名文件
	 */
	private void openKeystoreFile() {
		JFileChooser jc = new JFileChooser(keystoreFilePath);
		jc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int value = jc.showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = jc.getSelectedFile();
			if (file == null) {
				return;
			}
			keystoreFilePath = file.getAbsolutePath();
			jTextFieldSignFile.setText(keystoreFilePath);
		}
	}

	/**
	 * 获取Meta-data数据
	 */
	private void getMetaData() {
		metaDataMap.clear();
		xmlDoc = XmlUtil.getDocument(processDirPath + File.separator + "tmp" + File.separator + "AndroidManifest.xml");
		Element rootElement = xmlDoc.getRootElement();
		Element applicationElement = rootElement.element("application");
		List<Element> elements = XmlUtil.getChildElements(applicationElement, "meta-data");
		if (elements != null) {
			for (Element element : elements) {
				metaDataMap.put(XmlUtil.attrValue(element, "name"), element);
				System.out.println(element.getName() + ": " + XmlUtil.attrValue(element, "name") + " = " + XmlUtil.attrValue(element, "value"));
			}
		}
	}

	/**
	 * 生成bat前检查参数
	 */
	private void checkParams() {
		if (jComboBoxJdk.getSelectedIndex() == 1) {
			decompile_add = "";
			compile_add = "";
			sign_add = "";
		} else if (jComboBoxJdk.getSelectedIndex() == 0) {
			decompile_add = "-o ";
			compile_add = "-d ";
			sign_add = "-tsa https://timestamp.geotrust.com/tsa -digestalg SHA1 -sigalg MD5withRSA ";
		}
		apktoolPath = rootDirPath;
	}

	/**
	 * 解析apk,将AndroidManifest.xml拷贝到tmp下，并删除解析包中的AndroidManifest.xml
	 * @param apkFile
	 */
	private void resolveApk(File apkFile) {
		if (!apkFile.exists()) {
			return;
		}
		checkParams();

		StringBuffer content = new StringBuffer();
		content.append(rootDir).append("\n").append("cd ").append(rootDirPath).append("\n");//切换到指定目录下
		content.append("cmd /c java -jar ").append(apktoolPath).append("apktool.jar d -f ").append(apkFile.getAbsolutePath()).append(" ").append(decompile_add)
				.append(processDirPath + File.separator + apkName).append("\n")// 解析apk
				.append("mkdir ").append(processDirPath).append("\\tmp\n")//创建文件夹
				.append("cmd /c copy /y ").append(processDirPath + File.separator + apkName).append("\\AndroidManifest.xml ").append(processDirPath).append("\\tmp\\AndroidManifest.xml \n")
				.append("exit");//文件解压完成
		write(batPath, content.toString());
		compile();
		destroyBat();
	}

	private void openOutputDir() {
		JFileChooser jc = new JFileChooser(outputDir);
		jc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int value = jc.showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION) {
			File f = jc.getSelectedFile();
			if (f == null) {
				return;
			}
			outputDir = f.getAbsolutePath();
			jTextFieldOut.setText(outputDir);
		}
	}

	private void start() {
		long time = System.currentTimeMillis();
		if (oriApkPath == null) {
			showErrorDialog("APK路径不得为空!");
			return;
		}
		//		if (initKeystore()) {
		if (initApkInfo()) {
			multiChannelPackaging();
			showErrorDialog("总共用时：" + (System.currentTimeMillis() - time) / 1000 + "s");
			openDir(outputDir);
		} else {
			showErrorDialog("apk解析出错");
		}
	}

	/**
	 * 执行完成后打开目录
	 * @param dirPath
	 */
	private void openDir(String dirPath) {
		try {
			String[] cmd = new String[7];
			cmd[0] = "cmd";
			cmd[1] = "/c";
			cmd[2] = "start";
			cmd[3] = " ";
			cmd[4] = dirPath;
			cmd[5] = "\n";
			cmd[6] = "exit";
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 多渠道打包
	 */
	private void multiChannelPackaging() {

		if (configViews.size() == 0) {
			// 没有配置渠道时，直接打包
//			File file = new File(processDirPath + "\\tmp\\AndroidManifest.xml");
			File file = new File(processDirPath + File.separator + apkName + File.separator + "AndroidManifest.xml");
			if (file.exists()) {
				String unSignApkPath = packagingOneChannel(file, "default");
				if (isSignChecked()) {
					// 检查各项
					signApk(unSignApkPath);
				}
			}

			return;
		}
		// 记录所有渠道打包配置
		Map<String, Map<String, String>> allConfigs = new HashMap<String, Map<String, String>>();
		// 可以认为第一个配置的渠道数是总渠道数，作为key使用
		String[] allChannels = configViews.get(0).jTextFieldChannel.getText().split(",");
		// 解析多渠道配置，并检测合法性
		for (int i = 0; i < allChannels.length; i++) {
			Map<String, String> oneConfig = new HashMap<String, String>();
			for (int j = 0, count = configViews.size(); j < count; j++) {
				ConfigViewHolder viewHolder = configViews.get(j);
				String[] metas = viewHolder.jTextFieldMates.getText().split(",");
				String[] channels = viewHolder.jTextFieldChannel.getText().split(",");
				if (channels.length != allChannels.length) {
					showErrorDialog("多渠道配置错误，每一项配置的渠道数必须保持一致，且一一对应");
					return;
				}
				for (String meta : metas) {
					oneConfig.put(meta, channels[i]);
				}
			}
			allConfigs.put(allChannels[i], oneConfig);
		}
		// 生成多个AndroidManifest.xml
		for (String channel : allConfigs.keySet()) {
			Map<String, String> map = allConfigs.get(channel);
			for (String key : map.keySet()) {
				metaDataMap.get(key).attribute("value").setValue(map.get(key));
			}
			// 生成渠道AndroidManifest.xml 并打包
			String unSignApkPath = packagingOneChannel(saveChannelManifest(channel), channel);
			if (isSignChecked()) {
				// 检查各项
				signApk(unSignApkPath);
			}
		}

	}

	/**
	 * 检查签名信息是否已经填写
	 * @return
	 */
	private boolean isSignChecked() {
		if (jCheckBoxSign.isSelected()) {
			if ("".equals(jTextFieldSignFile.getText().trim())) {
				showErrorDialog("请先选择签名文件");
			} else if ("".equals(jTextFieldSignPwd.getText().trim())) {
				showErrorDialog("请先输入签名文件密码");
			} else if ("".equals(jTextFieldAliaPwd.getText().trim())) {
				showErrorDialog("请先输入别名密码");
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * 签名
	 * @param unSignApkPath
	 */
	private void signApk(String unSignApkPath) {
		SignParams signParams = new SignParams();
		signParams.setAlias(jComboBoxAlias.getSelectedItem().toString());
		signParams.setKeypass(jTextFieldAliaPwd.getText());
		signParams.setKeyStoreFilePath(jTextFieldSignFile.getText());
		signParams.setStorepass(keystorePasswd);
		signParams.setUnsignedApkFilePath(unSignApkPath);
		File unSignApk = new File(unSignApkPath);
		if (!unSignApk.exists()) {
			showErrorDialog("打包出错，未生成渠道包");
			return;
		}
		signParams.setSignedApkFilePath(unSignApk.getParent() + File.separator + unSignApk.getName().replace("_unsign.apk", "_sign.apk"));
		try {
			KeystoreUtil.signApk(signParams);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showErrorDialog(e.getMessage());
		}
	}

	/**
	 * 一个渠道打包
	 * @param xmlFile
	 * @param channelId
	 */
	private String packagingOneChannel(File xmlFile, String channelId) {
		// 移动xml
		File destFile = new File(processDirPath + File.separator + apkName + File.separator + "AndroidManifest.xml");
		if (destFile.exists()&&!destFile.getAbsolutePath().equals(xmlFile.getAbsolutePath())) {
			destFile.delete();
		}
		xmlFile.renameTo(destFile);
		File out;
		if (outputDir == null || "".equals(outputDir.trim())) {
			outputDir = rootDirPath + File.separator + "out" + File.separator + apkName;
		}
		out = new File(outputDir);
		if (!out.exists()) {
			out.mkdirs();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String newApkName = apkName + "_" + channelId + "_" + sdf.format(Long.valueOf(System.currentTimeMillis())) + "_" + versionName + "_unsign.apk";
		// 执行打包命令
		checkParams();
		StringBuffer content = new StringBuffer();
		content.append(rootDir).append("\n").append("cd ").append(rootDirPath).append("\n");//切换到指定目录下
		content.append("cmd /c java -jar ").append(apktoolPath).append("apktool.jar b -f ").append(processDirPath).append(File.separator + apkName).append(" ").append(decompile_add)
				.append(out.getPath() + File.separator + newApkName).append("\n")// 解析apk
				.append("exit");//打包完成
		//		// content = content + "@echo off\n";
		//		//	    exec(content.toString());
		write(batPath, content.toString());
		compile();
		destroyBat();
		return out.getPath() + File.separator + newApkName;
	}

	/**
	 * 保存渠道的manifest文件
	 * @param channel
	 * @return
	 */
	private File saveChannelManifest(String channel) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		//		format.setEncoding("GBK");
		// 输出全部原始数据，并用它生成新的我们需要的XML文件  
		File out = new File(processDirPath + File.separator + "manifesttemp" + File.separator + "AndroidManifest.xml");
		File outDir = out.getParentFile();
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileWriter(out), format);
			writer.write(xmlDoc); //输出到文件  
			return out;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void destroyBat() {
		File f = new File(batPath);
		try {
			f.delete();
		} catch (Exception e) {
		}
	}

	/**
	 * 执行
	 * @param cmd
	 */
	private void exeCmd(String cmd) {
		InputStream errIs = null;
		InputStream inIs = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			String errorString = baos.toString().toLowerCase();
			if (errorString.contains("fail")) {
				System.out.println("error!");
			}
			baos.write(10);
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1)
				baos.write(read);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null)
					inIs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
	}

	private void compile() {
		exeCmd("cmd /c start " + batPath + "\n exit");
		System.out.println("finish!");
	}

	private boolean initApkInfo() {
		ApkInfo apkInfo = null;
		try {
			apkInfo = new ApkUtil().getApkInfo(oriApkPath);
			packageName = apkInfo.getPackageName();
			versionName = apkInfo.getVersionName();
			System.out.println(apkInfo.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apkInfo != null;
	}

	public void write(String filePath, String content) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filePath));

			bw.write(content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					bw = null;
				}
		}
	}

	private void showErrorDialog(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	class MyDropTargetListener implements DropTargetListener {

		@Override
		public void dragEnter(DropTargetDragEvent arg0) {
			System.out.println("dragEnter:" + arg0.getSourceActions());
		}

		@Override
		public void dragExit(DropTargetEvent arg0) {
			System.out.println("dragExit");
		}

		@Override
		public void dragOver(DropTargetDragEvent arg0) {
			System.out.println("dragOver:" + arg0.getSourceActions());
		}

		@Override
		public void drop(DropTargetDropEvent arg0) {
			// JTextField field = (JTextField)arg0.getSource();
			// Transferable transferable = arg0.getTransferable();
			System.out.println("drop:" + arg0.getSourceActions());
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent arg0) {
			System.out.println("dropActionChanged:" + arg0.getSourceActions());
		}

	}
}
