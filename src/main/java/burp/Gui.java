package burp;

import burp.bean.Config;
import burp.constant.ConfigKey;
import burp.core.UserAgentCore;
import burp.ui.main.MainPanel;
import burp.ui.droppacket.DropPacketPanel;
import burp.ui.useragent.UserAgentPanel;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

@Data
public class Gui extends JPanel {

    private MainPanel       mainPanel;
    private UserAgentPanel  userAgentPanel;
    private DropPacketPanel dropPacketPanel;
    private JTabbedPane     tabbedPane = new JTabbedPane();

    private JLabel saveConfigLabel = new JLabel("保存配置(SaveConfig)");

    private static final float X = 1.0f;
    private static final float Y = 0.0f;

    private final Config config = new Config();

    private String configFilePath;

    public static final String CONFIG_FILE_NAME = "config2.json";

    public Gui(final IBurpExtenderCallbacks iBurpExtenderCallbacks) {
        setLayout(new OverlayLayout(this));

        String pluginJarFilePath = iBurpExtenderCallbacks.getExtensionFilename();
        this.configFilePath = pluginJarFilePath.substring(0, pluginJarFilePath.lastIndexOf(File.separator)) + File.separator + CONFIG_FILE_NAME;

        mainPanel = new MainPanel(iBurpExtenderCallbacks);
        userAgentPanel = new UserAgentPanel(iBurpExtenderCallbacks);
        dropPacketPanel = new DropPacketPanel();

        saveConfigLabel.setOpaque(false);
        saveConfigLabel.setAlignmentX(X);
        saveConfigLabel.setAlignmentY(Y);
        saveConfigLabel.setBorder(new CompoundBorder(saveConfigLabel.getBorder(), new EmptyBorder(3, 0, 0, 3)));
        saveConfigLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        saveConfigLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                saveConfig();
            }
        });

        tabbedPane.setAlignmentX(X);
        tabbedPane.setAlignmentY(Y);
        tabbedPane.addTab("主面板", mainPanel);
        tabbedPane.addTab("UA面板", userAgentPanel);
        tabbedPane.addTab("丢弃数据包面板", dropPacketPanel);

        add(saveConfigLabel);
        add(tabbedPane);
    }

    private void saveConfig() {
        // 主面板配置
        config.getMainPanelConfig().put(ConfigKey.RULE_TABLE_KEY, MainPanel.table.getTableData());
        config.getMainPanelConfig().put(ConfigKey.COMPARER_TOOL_KEY, mainPanel.getComparerToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.DECODER_TOOL_KEY, mainPanel.getDecoderToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.EXTENDER_TOOL_KEY, mainPanel.getExtenderToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.INTRUDER_TOOL_KEY, mainPanel.getIntruderToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.PROXY_TOOL_KEY, mainPanel.getProxyToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.REPEATER_TOOL_KEY, mainPanel.getRepeaterToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.SCANNER_TOOL_KEY, mainPanel.getScannerToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.SEQUENCER_TOOL_KEY, mainPanel.getSequencerToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.SPIDER_TOOL_KEY, mainPanel.getSpiderToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.SUITE_TOOL_KEY, mainPanel.getSuiteToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.TARGET_TOOL_KEY, mainPanel.getTargetToolCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.RANDOM_UA_KEY, mainPanel.getRandomUserAgentCheckBox().isSelected());
        config.getMainPanelConfig().put(ConfigKey.RP_AD_KEY, mainPanel.getRepeaterResponseAutoDecodeCheckBox().isSelected());


        // UA面板配置
        String pcTextAreaText = userAgentPanel.getPcTextArea().getText();
        UserAgentCore.pcUserAgent.clear();
        UserAgentCore.pcUserAgent.addAll(Arrays.asList(pcTextAreaText.split("\n")));

        String mobileTextAreaText = userAgentPanel.getMobileTextArea().getText();
        UserAgentCore.mobileUserAgent.clear();
        UserAgentCore.mobileUserAgent.addAll(Arrays.asList(mobileTextAreaText.split("\n")));

        config.getUserAgentPanelConfig().put(ConfigKey.PC_UA_KEY, userAgentPanel.getPcCheckBox().isSelected());
        config.getUserAgentPanelConfig().put(ConfigKey.MOBILE_UA_KEY, userAgentPanel.getMobileCheckBox().isSelected());
        config.getUserAgentPanelConfig().put(ConfigKey.PC_UA_LIST_KEY, UserAgentCore.pcUserAgent);
        config.getUserAgentPanelConfig().put(ConfigKey.MOBILE_UA_LIST_KEY, UserAgentCore.mobileUserAgent);

        String configJson = JSONUtil.toJsonStr(config);

        try (FileWriter fileWriter = new FileWriter(configFilePath)) {
            fileWriter.write(configJson);
            fileWriter.flush();
            JOptionPane.showMessageDialog(this, "保存成功(Save Success)!", "提示(Tip)", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "配置文件保存失败(Config File Save Fail!)", "提示(Tip)", JOptionPane.WARNING_MESSAGE);
        }
    }
}