package com.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dom4j.Element;

/**
 * @author Sherwin.Ye
 * @data 2016年7月8日 下午3:26:20
 * @desc ConfigViewHolder.java
 */
public class ConfigViewHolder {
	public int key;
	public Set<String> metaSet;
	
	public JPanel jPanelItemContent=new JPanel(new VFlowLayout());
	public JPanel jPanelMeta=new JPanel(new FlowLayout(FlowLayout.LEFT));
	public JPanel jPanelChannel=new JPanel(new FlowLayout(FlowLayout.LEFT));
	
	public JLabel jLabelConfig;
	public JTextField jTextFieldMates;
	public JButton jButtonMetaSelect;
	public JTextField jTextFieldChannel;
	public JButton jButtonDeleteConfig;

	public ConfigViewHolder(int key) {
		this.key = key;
		jLabelConfig = new JLabel("多渠道配置" + key);
		jLabelConfig.setForeground(Color.blue);
		jTextFieldMates = new JTextField();
		jTextFieldMates.setPreferredSize(new Dimension(450, 30));
		jTextFieldMates.setEditable(false);
		jButtonMetaSelect = new JButton("meta");
		jTextFieldChannel = new JTextField();
		jTextFieldChannel.setPreferredSize(new Dimension(450, 30));
		jButtonDeleteConfig = new JButton("删除");
		
		jPanelItemContent.add(jLabelConfig);
		jPanelItemContent.add(jPanelMeta);
		jPanelItemContent.add(jPanelChannel);
		jPanelMeta.add(jTextFieldMates);
		jPanelMeta.add(jButtonMetaSelect);
		jPanelChannel.add(jTextFieldChannel);
		jPanelChannel.add(jButtonDeleteConfig);
	}
	
	/**
	 * 设置meta-data
	 * @param metaDataMap
	 */
	public void setMetaData(Map<String, Element> metaDataMap) {
		for (String key : metaDataMap.keySet()) {
			jTextFieldMates.setText(jTextFieldMates.getText() + "," + key);
		}
		if (jTextFieldMates.getText().startsWith(",")) {
			jTextFieldMates.setText(jTextFieldMates.getText().substring(1));
		}
	}

	/**
	 * 更新位置，相对于 jComponent
	 * @param jComponent
	 */
	public void updatePosition(int key) {
		this.key=key;
		jLabelConfig.setText("多渠道配置" + key);
	}

	private Container container;

	/**
	 * 设置容器
	 * @param container
	 */
	public void setContainer(Container container,int index) {
		this.container = container;
		container.add(jPanelItemContent,3+index);
		container.validate();
	}
	/**
	 * 移除控件
	 */
	public void removeAll() {
		container.remove(jPanelItemContent);
		container.validate();
	}
}
