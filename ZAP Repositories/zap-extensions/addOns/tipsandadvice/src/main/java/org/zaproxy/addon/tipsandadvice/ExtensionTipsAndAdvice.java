/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2014 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.addon.tipsandadvice;

import org.parosproxy.paros.control.Control;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Enumeration;
import java.util.Dictionary;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.MenuFileControl;
import org.parosproxy.paros.control.AbstractControl;
import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.extension.AbstractDialog;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ExtensionLoader;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.help.ExtensionHelp;
import org.zaproxy.zap.utils.FontUtils;
import org.zaproxy.zap.utils.ZapTextArea;
import org.zaproxy.zap.view.LayoutHelper;
import org.zaproxy.zap.view.ZapMenuItem;

//import com.formdev.flatlaf.ui.FlatListCellBorder.Default;

/**
 * An example ZAP extension which adds a top level menu item, a pop up menu item and a status panel.
 *
 * <p>{@link ExtensionAdaptor} classes are the main entry point for adding/loading functionalities
 * provided by the add-ons.
 *
 * @see #hook(ExtensionHook)
 */
public class ExtensionTipsAndAdvice extends ExtensionAdaptor {

    // The name is public so that other extensions can access it
    public static final String NAME = "ExtensionTipsAndAdvice";

    // The i18n prefix, by default the package name - defined in one place to make it easier
    // to copy and change this example
    protected static final String PREFIX = "tipsandadvice";

    /**
     * Relative path (from add-on package) to load add-on resources.
     *
     * @see Class#getResource(String)
     */
    private static final String RESOURCES = "resources";
    private List<String> tips = null;

    private ZapMenuItem menuTipsAndAdvice;
    private RightClickMsgMenu popupMsgMenuExample;
    private AbstractPanel statusPanel;

    private TipsAndAdviceAPI api;

    private static final Logger LOGGER = LogManager.getLogger(ExtensionTipsAndAdvice.class);

    public ExtensionTipsAndAdvice() {
        super(NAME);
        setI18nPrefix(PREFIX);
    }

    private List<String> getTips()
    {
        //Code inspired by TipsAndTrick's ExtensionTipsAndTricks.java file (lines 57-76)
        if (this.tips == null ) {
            this.tips = new ArrayList<String>();
            ResourceBundle rb = Constant.messages.getMessageBundle("tipsandadvice");
            Enumeration<String> enm = rb.getKeys();
   
            while(enm.hasMoreElements()) {
               String key = enm.nextElement();
               if (key.startsWith("tipsandadvice.tip.")) {
                  //this.tips.add(rb.getString(key));
                  this.tips.add(key); //Stores as a key rather than a string (for categorisation)
               }
            }
         }
   
         return this.tips;
    }

    private String getRandomCategory()
    {
        String category = null;

        Dictionary<Integer, String> cat = new Hashtable<>();

        cat.put(0, "gen");
        cat.put(1, "ui");
        cat.put(2, "add");

        int random = (int)(Math.random() * (cat.size()));

        category = cat.get(random);
        
        return category;
    }

    private String getRandomTip()
    {
        List<String> tempList = new ArrayList<>();
        String category = getRandomCategory();

        for (String i : this.tips)
        {
            if (i.startsWith(PREFIX + ".tip." + category + "."))
            {
                tempList.add(i);
            }
        }

        String selectedTipKey = tempList.get((int)(Math.random() * tempList.size()));

        return Constant.messages.getString(selectedTipKey);
    }

    private void displayRandomTip()
    {
        View.getSingleton()
                .showMessageDialog(
                        Constant.messages.getString(getRandomTip()));
    }

    private String getTip(String category, int number)
    {
        return (PREFIX + ".tip." + category + "." + number);
    }

    private void displaySpecificTip(String category, int number)
    {
        View.getSingleton()
                .showMessageDialog(
                        Constant.messages.getString(getTip(category, number)));
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        this.api = new TipsAndAdviceAPI();
        extensionHook.addApiImplementor(this.api);

        getTips();

        // As long as we're not running as a daemon
        if (hasView()) {
            extensionHook.getHookMenu().addHelpMenuItem(getMenuTipsAndAdvice());
            extensionHook.getHookMenu().addPopupMenuItem(getPopupMsgMenuExample());
            extensionHook.getHookView().addStatusPanel(getStatusPanel());
        }
        
    }

    @Override
    public boolean canUnload() {
        // The extension can be dynamically unloaded, all resources used/added can be freed/removed
        // from core.
        return true;
    }

    @Override
    public void unload() {
        super.unload();
        // In this example it's not necessary to override the method, as there's nothing to unload
        // manually, the components added through the class ExtensionHook (in hook(ExtensionHook))
        // are automatically removed by the base unload() method.
        // If you use/add other components through other methods you might need to free/remove them
        // here (if the extension declares that can be unloaded, see above method).
    }

    private RightClickMsgMenu getPopupMsgMenuExample() {
        if (popupMsgMenuExample == null) {
            popupMsgMenuExample =
                    new RightClickMsgMenu(
                            this, Constant.messages.getString(PREFIX + ".popup.title"));
        }
        return popupMsgMenuExample;
    }

    private void setPaneText(JTextPane pane)
    {

        pane.setText("<html>" + 
            "<b>Tip of the Day:</b><br><br>" + 
            getRandomTip() + 
            "<br><br>Would you like to know more about ... ?" + 
        "</html>");
    }

    private AbstractPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new AbstractPanel();
            statusPanel.setLayout(new CardLayout());
            statusPanel.setName(Constant.messages.getString(PREFIX + ".panel.title"));
            statusPanel.setIcon(new ImageIcon(getClass().getResource(RESOURCES + "/tipsandadvice.png")));
            JTextPane pane = new JTextPane();
            pane.setEditable(false);
            // Obtain (and set) a font with the size defined in the options
            pane.setFont(FontUtils.getFont("Inter", Font.PLAIN));
            pane.setContentType("text/html");

            setPaneText(pane);

            Icon prevIcon = new ImageIcon(getClass().getResource(RESOURCES + "/LeftButton.png"));
            Icon nextIcon = new ImageIcon(getClass().getResource(RESOURCES + "/RightButton.png"));
            JButton prev = new JButton(prevIcon);
            JButton next = new JButton(nextIcon);
            prev.setBounds(0, 100, 40, 40);
            pane.add(prev);
            prev.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setPaneText(pane);
                }
            });
            next.setBounds(40, 100, 40, 40);
            pane.add(next);
            next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setPaneText(pane);
                }
            });

            JButton allTips = new JButton();
            allTips.setText(Constant.messages.getString("tips.button.allTips"));
            allTips.setBounds(80, 100, 80, 40);
            pane.add(allTips);
            allTips.addActionListener((e) -> {
                ExtensionHelp.showHelp("simple");

            });

            pane.setBackground(new java.awt.Color(255, 254, 192));
            statusPanel.setBorder(BorderFactory.createLineBorder(Color.black,3));
            statusPanel.add(pane);
        }
        return statusPanel;
    }

//    private JTextPane displayTip()
//    {

//    }

   private JTree createAllTipsList()
   {
        DefaultMutableTreeNode categories = new DefaultMutableTreeNode("Categories: ");
        DefaultMutableTreeNode general = new DefaultMutableTreeNode("General: ");
        DefaultMutableTreeNode ui = new DefaultMutableTreeNode("User Interface");
        DefaultMutableTreeNode addon = new DefaultMutableTreeNode("Add-ons");
        
        categories.add(general);
        categories.add(ui);
        categories.add(addon);
    
        JTree allTips = new JTree(categories);
        allTips.setSize(400, 500);

        MouseListener ml = new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) allTips.getSelectionPath().getLastPathComponent();
                if (node == general)
                {
                    displayRandomTip();
                }
            }
        };

        allTips.addMouseListener(ml);

        return allTips;
   }

   private void openAllTipsWindow()
    {
        JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(10,5));
        frame.setTitle("Tips and Tricks");
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JTree newHelpWindow = createAllTipsList();
        frame.add(newHelpWindow, BorderLayout.WEST);

        frame.setVisible(true);
    }
   

   private String setPanelText()
    {
        String message = "<html><b>Random Tip:</b><br><br>"
         + getRandomTip() 
         +"</html>";

        return message;
    }


   private JPanel createHelpWindow() 
   {
    JPanel helpWindow = new JPanel();
    helpWindow.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
    helpWindow.setBackground(new java.awt.Color(255, 254, 192));
    helpWindow.setBorder(BorderFactory.createLineBorder(Color.black,3));
    helpWindow.setSize(750, 450);

    JPanel textSpace = new JPanel();
    textSpace.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
    textSpace.setBackground(new java.awt.Color(255, 254, 192));
    textSpace.setBorder(BorderFactory.createLineBorder(Color.black,3));
    textSpace.setSize(350, 250);

    JTextPane text = new JTextPane();
    text.setContentType("text/html");
    text.setFont(FontUtils.getFont("Inter", Font.PLAIN));
    text.setLayout(new FlowLayout(FlowLayout.CENTER));
    //text.setBackground(new java.awt.Color(255, 254, 192));
    text.setText(setPanelText());
    text.setBounds(0, 0, 300, 60);
    textSpace.add(text);
    helpWindow.add(textSpace);

    JPanel buttons = new JPanel();

    Icon nextIcon = new ImageIcon(getClass().getResource(RESOURCES + "/RightButton.png"));
    JButton next = new JButton(nextIcon);  
    next.setBounds(40, 200, 30, 30);
    buttons.add(next);
    next.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            text.setText(setPanelText());
        }
    });

    JButton allTips = new JButton();
    allTips.setText(Constant.messages.getString("tips.button.allTips"));
    allTips.setBounds(20, 250, 80, 40);
    buttons.add(allTips);
    allTips.addActionListener((e) -> {
        openAllTipsWindow();
    });
    helpWindow.add(buttons, BorderLayout.SOUTH);

    return helpWindow;

   }

   private void openHelpWindow()
   {
        JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(10,5));
        frame.setTitle("Tips and Advice");
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel newHelpWindow = createHelpWindow();
        frame.add(newHelpWindow);
        

        frame.setVisible(true);
   }


    private ZapMenuItem getMenuTipsAndAdvice() {
        if (menuTipsAndAdvice == null) {
            menuTipsAndAdvice = new ZapMenuItem(PREFIX + ".topmenu.help.title");
            menuTipsAndAdvice.addActionListener(
                     e -> {
                        openHelpWindow();
                     });
        }
        return menuTipsAndAdvice;
    }

    private void displayFile(String file) {
        if (!View.isInitialised()) {
            // Running in daemon mode, shouldnt have been called
            return;
        }
        try {
            File f = new File(Constant.getZapHome(), file);
            if (!f.exists()) {
                // This is something the user should know, so show a warning dialog
                View.getSingleton()
                        .showWarningDialog(
                                Constant.messages.getString(
                                        ExtensionTipsAndAdvice.PREFIX + ".error.nofile",
                                        f.getAbsolutePath()));
                return;
            }
            // Quick way to read a small text file
            String contents = new String(Files.readAllBytes(f.toPath()));
            // Write to the output panel
            View.getSingleton().getOutputPanel().append(contents);
            // Give focus to the Output tab
            View.getSingleton().getOutputPanel().setTabFocus();
        } catch (Exception e) {
            // Something unexpected went wrong, write the error to the log
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return Constant.messages.getString(PREFIX + ".desc");
    }
}
