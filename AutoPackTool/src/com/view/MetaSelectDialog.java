package com.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.dom4j.Element;

/**
 * @author Sherwin.Ye
 * @data 2016年7月11日 下午2:11:56
 * @desc MetaSelectDialog.java
 */
public class MetaSelectDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jPanelMates;
	private List<JCheckBox> checkboxList = new ArrayList<JCheckBox>();
	private Set<String> metaDataSet = new HashSet<String>();

	private String configDirPath = null;
	private Properties matchProperties;
	private List<String> ignoreMates;

	/**
	 * 构造器
	 * 
	 * @param owner
	 */
	public MetaSelectDialog(JFrame parent, Map<String, Element> metaDataMap, List<ConfigViewHolder> configViews, ConfigViewHolder currentConfig, final OnSelectCallback callback) {
		super(parent, true);
		initConfigProperties();
		setSize(450, 400);
		setResizable(true);
		setTitle("选择需要配置meta-data");
		// 实例化面板
		jPanelMates = new JPanel(new FlowLayout());

		for (String key : metaDataMap.keySet()) {
			boolean isExist = false;
			for (ConfigViewHolder configViewHolder : configViews) {
				if (currentConfig != configViewHolder && configViewHolder.metaSet != null && configViewHolder.metaSet.contains(key)) {
					isExist = true;
				}
			}
			if (!isExist && isNeedShow(key)) {
				String[] keyConfigs = translateKey(key);
				String newKey = key;
				Color color = null;
				if (keyConfigs != null) {
					if (keyConfigs.length > 0) {
						newKey = keyConfigs[0] + "(" + key + ")";
						if (keyConfigs.length > 1) {
							color = new Color(Integer.parseInt(keyConfigs[1].substring(1), 16));
						} else {
							color = Color.RED;
						}
					}
				}

				JCheckBox jCheckBox = new JCheckBox(newKey);
				jCheckBox.setToolTipText(key);
				jCheckBox.setSelected(currentConfig.metaSet == null ? false : currentConfig.metaSet.contains(key));
				if (keyConfigs != null) {
					jCheckBox.setForeground(color);
				}
				checkboxList.add(jCheckBox);
				jPanelMates.add(jCheckBox);
			}
		}

		// 将面板添加到帧窗口
		add(BorderLayout.CENTER, jPanelMates);
		JButton jButtonOk = new JButton("确定");
		Panel panel2 = new Panel(new FlowLayout(FlowLayout.CENTER));
		panel2.add(jButtonOk);
		add(BorderLayout.SOUTH, panel2);
		jButtonOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (callback != null) {
					for (JCheckBox jCheckBox : checkboxList) {
						if (jCheckBox.isSelected()) {
							metaDataSet.add(jCheckBox.getToolTipText());
						}
					}
					callback.onSelected(metaDataSet);
				}
				hide();
			}
		});
		setLocationRelativeTo(null);
		show();
	}

	/**
	 * 对已知的渠道号翻译
	 * @param key
	 */
	private String[] translateKey(String key) {
		if (key != null) {
			//			if (key.toLowerCase().contains("umeng")) {
			//				return "友盟("+key+")";
			//			} else if (key.equals("CHANNEL")) {
			//				return "动漫("+key+")";
			//			} else if (key.equals("qsh_pid")) {
			//				return "多义乐("+key+")";
			//			} else if (key.equals("MILI_PAY_CHANNELID")) {
			//				return "米粒("+key+")";
			//			} else if (key.equals("PZ_CNO")) {
			//				return "平治("+key+")";
			//			} else if (key.equals("channel_id")||key.equals("channel_id_sub")) {
			//				return "搜影("+key+")";
			//			} else if (key.equals("subchannelid")) {
			//				return "易迅("+key+")";
			//			} else if (key.equals("ZHANGZHIFU_PAY_CHANNELID")){
			//				return "掌支付("+key+")";
			//			}
			String metaName = matchProperties.getProperty(key);
			if (metaName != null && !"".equals(metaName.trim())) {
				return metaName.split(",");
			}
		}
		return null;
	}

	/**
	 * 对已知的渠道号翻译
	 * @param key
	 */
	private boolean isNeedShow(String key) {
		if (key == null || ignoreMates.contains(key)) {
			return false;
		} else {
			return true;
		}
		//		if (key!=null) {
		//			if (key.equals("rushId")) {// 多义乐
		//				return false;
		//			} else if (key.equals("MILI_PAY_APPID")) {// 米粒
		//				return false;
		//			} else if (key.equals("qsh_pid")) {// 平治
		//				return false;
		//			} else if (key.equals("sy_merchant_sercret")||key.equals("sy_appid")||key.equals("sy_merchant_id")) { // 搜影
		//				return false;
		//			}
		//		}
		//		return true;
	}

	/**
	 * 初始化配置参数
	 */
	private void initConfigProperties() {
		if (configDirPath == null) {
			//			apktoolPath = getClass().getResource("").getPath().substring(1);
			configDirPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath().substring(1);
			File file = new File(configDirPath);
			//			if (file.isFile()) {
			configDirPath = file.getParent() + File.separator;
			//			}
		}
		matchProperties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(configDirPath + "matching.properties");
			BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			matchProperties.load(bf);

			//			matchProperties.load(new FileInputStream(configDirPath + "matching.txt"));

			String ignoreStr = matchProperties.getProperty("IGNORE_META", null);
			if (ignoreStr != null) {
				String[] ignoreStrs = ignoreStr.split(",");
				ignoreMates = Arrays.asList(ignoreStrs);
			}
			//				oriApkPath = matchProperties.getProperty("apkFile");
		} catch (Exception e) {
			//			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 关闭流

		}

	}

	public static interface OnSelectCallback {
		public void onSelected(Set<String> metaDataSet);
	}
}
