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
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hsqldb.rights.Right;
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
    private static int pageIndex = 0;
    private static JButton prev = new JButton();
    private static JButton next = new JButton(); //Source: https://www.geeksforgeeks.org/difference-between-static-and-non-static-method-in-java/

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
               if (key.startsWith("tipsandadvice.tip.")) { //Source: https://www.w3schools.com/java/ref_string_startswith.asp
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

        Dictionary<Integer, String> cat = new Hashtable<>(); //Source: https://www.geeksforgeeks.org/java-util-dictionary-class-java/

        cat.put(0, "gen");
        cat.put(1, "ui");
        cat.put(2, "add");
        cat.put(3, "feat");

        int random = (int)(Math.random() * (cat.size())); //Sources: https://www.geeksforgeeks.org/java-program-to-find-the-length-size-of-an-arraylist/ and https://www.w3schools.com/java/java_howto_random_number.asp

        category = cat.get(random); //Source: https://stackoverflow.com/questions/9652812/how-would-use-a-get-method-to-access-an-element-in-an-arraylist
        
        return category;
    }

    private String getRandomTip()
    {
        List<String> tempList = new ArrayList<>();
        String category = getRandomCategory();

        for (String i : this.tips)
        {
            if (i.startsWith(PREFIX + ".tip." + category + ".") && !(i.endsWith(".a") || i.endsWith(".b"))) //Source: https://www.w3schools.com/java/ref_string_endswith.asp#:~:text=The%20endsWith()%20method%20checks,the%20specified%20character(s).
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
                        getRandomTip());
    }

    // private int countFeaturedTips()
    // {
    //     List<String> tempList = new ArrayList<>();
    //     String category = "feat";

    //     for (String i : this.tips)
    //     {
    //         if (i.startsWith(PREFIX + ".tip." + category + "."))
    //         {
    //             tempList.add(i);
    //         }
    //     }

    // }

    private String getRandomFeaturedTip()
    {
        List<String> tempList = new ArrayList<>();
        String category = "feat";

        for (String i : this.tips)
        {
            if (i.startsWith(PREFIX + ".tip." + category + ".") && !(i.endsWith(".a") || i.endsWith(".b")))
            {
                tempList.add(i);
            }
        }

        String selectedTipKey = tempList.get((int)(Math.random() * tempList.size()));

        return Constant.messages.getString(selectedTipKey);
    }

    private String getFeaturedTip(boolean isConverted, int number)
    {
        if (isConverted == true)
        {
            return Constant.messages.getString(PREFIX + ".tip." + "feat" + "." + number);  
        }
        else
        {
            return PREFIX + ".tip." + "feat" + "." + number;
        }
    }

    private String getFeaturedTipDescA(String featTip)
    {        
        return Constant.messages.getString(featTip + ".a");
    }

    private String getFeaturedTipDescB(String featTip)
    {
        return Constant.messages.getString(featTip + ".b");
    }

    private String getTip(String category, int number)
    {
        return Constant.messages.getString(PREFIX + ".tip." + category + "." + number);
    }

    private void displaySpecificTip(String category, int number)
    {
        View.getSingleton()
                .showMessageDialog(
                        Constant.messages.getString(getTip(category, number)));
    }

    private void newTipOfTheDay()
    {
        //Planned to run when a new tip of the day is made available (on a 24-hr basis)
        View.getSingleton()
                .showMessageDialog("New daily tip available in Status Panel >> Tips and Advice!");

    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        this.api = new TipsAndAdviceAPI();
        extensionHook.addApiImplementor(this.api);

        getTips();
        newTipOfTheDay();

        // As long as we're not running as a daemon
        if (hasView()) {
            extensionHook.getHookMenu().addHelpMenuItem(getMenuTipsAndAdvice());
            extensionHook.getHookMenu().addPopupMenuItem(getPopupMsgMenuExample());
            extensionHook.getHookView().addStatusPanel(getStatusPanel());
        }
        
    }

    @Override
    public boolean canUnload() {
        return true;
    }

    @Override
    public void unload() {
        super.unload();
    }

    private RightClickMsgMenu getPopupMsgMenuExample() {
        if (popupMsgMenuExample == null) {
            popupMsgMenuExample =
                    new RightClickMsgMenu(
                            this, Constant.messages.getString(PREFIX + ".popup.title"));
        }
        return popupMsgMenuExample;
    }

    private void setPaneText(JTextPane pane, JButton prev, JButton next)
    {

        ExtensionTipsAndAdvice.pageIndex = 0;
        Icon prevIconGrey = new ImageIcon(getClass().getResource(RESOURCES + "/LeftButtonGrey.png")); //Source: https://www.tutorialspoint.com/how-to-add-icon-to-jbutton-in-java#:~:text=To%20add%20icon%20to%20a,an%20image%20to%20the%20button.&text=Icon%20icon%20%3D%20new%20ImageIcon(%22,button7%20%3D%20new%20JButton(icon)%3B
        ExtensionTipsAndAdvice.prev.setIcon(prevIconGrey);
        Icon nextIcon = new ImageIcon(getClass().getResource(RESOURCES + "/RightButton.png"));
        ExtensionTipsAndAdvice.next.setIcon(nextIcon);
        
        pane.setText("<html>" + 
            "<b>Tip of the Day:</b><br><br>" + 
            getFeaturedTip(true, 0) + 
            "<br><br>Click the right arrow to learn more." + 
        "</html>");  //Sources: https://stackoverflow.com/questions/9335604/java-change-font-in-a-jtextpane-containing-html/9335955#9335955 and https://stackoverflow.com/questions/9071389/setting-jtextpane-to-content-type-html-and-using-string-builders#:~:text=Every%20time%20JTextPane.,created%2C%20in%20your%20case%20HTMLDocument.

    }

    private void featuredTipPartA(JTextPane pane, JButton prev, JButton next)
    {
        
        ExtensionTipsAndAdvice.pageIndex = 1;
        Icon prevIcon = new ImageIcon(getClass().getResource(RESOURCES + "/LeftButton.png"));
        ExtensionTipsAndAdvice.prev.setIcon(prevIcon);
        Icon nextIcon = new ImageIcon(getClass().getResource(RESOURCES + "/RightButton.png"));
        ExtensionTipsAndAdvice.next.setIcon(nextIcon);

        pane.setText("<html>" + 
        getFeaturedTipDescA(getFeaturedTip(false, 0)) + 
        "</html>"); 
        
    }

    private void featuredTipPartB(JTextPane pane, JButton prev, JButton next)
    {
        ExtensionTipsAndAdvice.pageIndex = 2;
        Icon prevIcon = new ImageIcon(getClass().getResource(RESOURCES + "/LeftButton.png"));
        ExtensionTipsAndAdvice.prev.setIcon(prevIcon);
        Icon nextIconGrey = new ImageIcon(getClass().getResource(RESOURCES + "/RightButtonGrey.png"));
        ExtensionTipsAndAdvice.next.setIcon(nextIconGrey);

        pane.setText("<html>" + 
            getFeaturedTipDescB(getFeaturedTip(false, 0)) + 
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
            pane.setFont(FontUtils.getFont("Inter", Font.PLAIN)); //Sources: https://www.tutorialspoint.com/how-to-set-font-for-text-in-jtextpane-with-java and https://www.tutorialspoint.com/how-to-set-style-for-jtextpane-in-java
            pane.setContentType("text/html");

            //setPaneText(pane);

            Icon prevIconGrey = new ImageIcon(getClass().getResource(RESOURCES + "/LeftButtonGrey.png"));
            Icon nextIcon = new ImageIcon(getClass().getResource(RESOURCES + "/RightButton.png"));
            
            ExtensionTipsAndAdvice.pageIndex = 0;

            ExtensionTipsAndAdvice.prev.setIcon(prevIconGrey);
            ExtensionTipsAndAdvice.next.setIcon(nextIcon);
            ExtensionTipsAndAdvice.prev.setBounds(0, 100, 40, 40);
            ExtensionTipsAndAdvice.next.setBounds(40, 100, 40, 40);  
            pane.add(ExtensionTipsAndAdvice.prev);
            pane.add(ExtensionTipsAndAdvice.next); //Source: https://stackoverflow.com/questions/15819006/adding-a-jbutton-to-a-jtextpane 

            setPaneText(pane, ExtensionTipsAndAdvice.prev, ExtensionTipsAndAdvice.next);
            
            ExtensionTipsAndAdvice.prev.addActionListener(new ActionListener() { //Source: https://www.tutorialspoint.com/how-to-add-action-listener-to-jbutton-in-java
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (ExtensionTipsAndAdvice.pageIndex == 1)
                    {
                        setPaneText(pane, ExtensionTipsAndAdvice.prev, ExtensionTipsAndAdvice.next);
                    }
                }
            });
            ExtensionTipsAndAdvice.prev.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (ExtensionTipsAndAdvice.pageIndex == 2)
                    {
                        featuredTipPartA(pane, ExtensionTipsAndAdvice.prev, ExtensionTipsAndAdvice.next);
                    }
                }
            });
            ExtensionTipsAndAdvice.next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (ExtensionTipsAndAdvice.pageIndex == 0)
                    {
                        featuredTipPartA(pane, ExtensionTipsAndAdvice.prev, ExtensionTipsAndAdvice.next);
                    }
                }
            });
            ExtensionTipsAndAdvice.next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (ExtensionTipsAndAdvice.pageIndex == 1)
                    {
                        featuredTipPartB(pane, ExtensionTipsAndAdvice.prev, ExtensionTipsAndAdvice.next);
                    }
                }
            });

            pane.setBackground(new java.awt.Color(255, 254, 192)); //Sources: https://stackoverflow.com/questions/1081486/setting-background-color-for-a-jframe and https://docs.oracle.com/javase/8/docs/api/java/awt/Color.html
            statusPanel.setBorder(BorderFactory.createLineBorder(Color.black,3)); //Source: https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
            statusPanel.add(pane);
        }
        return statusPanel;
    }

   private String createTipsMessage(String prefix)
   {
        List<String> parts = new ArrayList<>();
        String message = "";
        if (prefix == "feat")
        {
            parts.add("<html><b>Featured Tips: </b><br><br>");
        }
        if (prefix == "gen")
        {
            parts.add("<html><b>General Tips: </b><br><br>");
        }
        if (prefix == "ui")
        {
            parts.add("<html><b>User Interface Tips: </b><br><br>");
        }      
        if (prefix == "add")
        {
            parts.add("<html><b>Add-on Tips: </b><br><br>");
        }         
        //parts.add("<html><b>General Tips: </b><br><br>");
        for (String i : this.tips)
        {
            if ((i.startsWith(PREFIX + ".tip." + prefix + ".")) && !(i.endsWith(".a") || i.endsWith(".b")))
            {
                parts.add(Constant.messages.getString(i));
                parts.add("<br>");
            }
        }
        parts.add("</html>");

        for (int i = 0; i < parts.size(); i++)
        {
            message += parts.get(i);
        }

        return message;
   }

   private String displayTips(DefaultMutableTreeNode category)
   {
        String message = "";
        String categoryPrefix = "";
        if (category.toString() == "Featured: ")
        {
            categoryPrefix = "feat";
            message = createTipsMessage(categoryPrefix);
        }
        if (category.toString() == "General: ")
        {
            categoryPrefix = "gen";
            message = createTipsMessage(categoryPrefix);
        }
        if (category.toString() == "User Interface: ")
        {
            categoryPrefix = "ui";
            message = createTipsMessage(categoryPrefix);
        }
        if (category.toString() == "Add-ons: ")
        {
            categoryPrefix = "add";
            message = createTipsMessage(categoryPrefix);
        }

        return message;
   }

   private JTextPane createTipsDisplay()
   {
        JTextPane tipsSide = new JTextPane();
        tipsSide.setEditable(false);
        tipsSide.setFont(FontUtils.getFont("Inter", Font.PLAIN));
        tipsSide.setContentType("text/html");
        tipsSide.setSize(400, 500);
        tipsSide.setText("<html><b>Tips: </b></html>");
        
        // String message = "<html><b>Random Tip:</b><br><br>"
        //  + getRandomTip() 
        //  +"</html>";

        return tipsSide;
   }

   private void openAllTipsWindow()
    {
        JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(10,5));
        frame.setTitle("Tips and Tricks");
        frame.setSize(800,500);
        frame.setBackground(new java.awt.Color(255, 254, 192));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextPane newTipsDisplay = createTipsDisplay();
        frame.add(newTipsDisplay, BorderLayout.CENTER); //Sources: https://www.youtube.com/watch?v=1G4lBJW1vfM&ab_channel=JavaCodeJunkie and https://docs.oracle.com/javase/7/docs/api/java/awt/BorderLayout.html

        DefaultMutableTreeNode categories = new DefaultMutableTreeNode("Categories: ");
        DefaultMutableTreeNode featured = new DefaultMutableTreeNode("Featured: ");
        DefaultMutableTreeNode general = new DefaultMutableTreeNode("General: ");
        DefaultMutableTreeNode ui = new DefaultMutableTreeNode("User Interface: ");
        DefaultMutableTreeNode addon = new DefaultMutableTreeNode("Add-ons: ");
        
        categories.add(featured);
        categories.add(general);
        categories.add(ui);
        categories.add(addon);
    
        JTree allTips = new JTree(categories); //Source: https://www.youtube.com/watch?v=ZIzRav8mmvY
        allTips.setSize(400, 500);
        frame.add(allTips, BorderLayout.WEST);

        MouseListener ml = new MouseAdapter() //Sources: https://www.geeksforgeeks.org/mouselistener-mousemotionlistener-java/ and https://docs.oracle.com/javase/tutorial/uiswing/events/mouselistener.html
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) allTips.getSelectionPath().getLastPathComponent();
                if (node == featured)
                {
                    newTipsDisplay.setText(displayTips(featured));
                }
                if (node == general)
                {
                    newTipsDisplay.setText(displayTips(general));
                }
                if (node == ui)
                {   
                    newTipsDisplay.setText(displayTips(ui));
                }
                if (node == addon)
                {   
                    newTipsDisplay.setText(displayTips(addon));
                }
                //displayRandomTip();
            }
        };

        allTips.addMouseListener(ml);

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
    JPanel helpWindow = new JPanel(); //Source: https://www.youtube.com/watch?v=4PfDdJ8GFHI&ab_channel=JavaCodeJunkie
    helpWindow.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
    helpWindow.setBackground(new java.awt.Color(255, 254, 192));
    helpWindow.setBorder(BorderFactory.createLineBorder(Color.black,3));
    helpWindow.setSize(750, 450);

    JPanel textSpace = new JPanel();
    textSpace.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
    textSpace.setBackground(new java.awt.Color(255, 254, 192));
    textSpace.setSize(350, 250);

    JTextPane text = new JTextPane();
    text.setContentType("text/html");
    text.setFont(FontUtils.getFont("Inter", Font.PLAIN));
    text.setLayout(new FlowLayout(FlowLayout.CENTER));
    text.setBackground(new java.awt.Color(255, 254, 192));
    text.setText(setPanelText());
    text.setBounds(0, 0, 300, 60);
    textSpace.add(text);
    helpWindow.add(textSpace);

    JPanel buttons = new JPanel();

    JButton allTips = new JButton();
    allTips.setText(Constant.messages.getString("tipsandadvice.button.allTips"));
    //allTips.setBounds(20, 200, 80, 40);
    buttons.add(allTips);
    allTips.addActionListener((e) -> {
        openAllTipsWindow();
    });
    
    JButton next = new JButton();  
    next.setText(Constant.messages.getString("tipsandadvice.button.next"));
    //next.setBounds(40, 200, 30, 30);
    buttons.add(next);
    next.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            text.setText(setPanelText());
        }
    });


    helpWindow.add(buttons, BorderLayout.SOUTH);

    return helpWindow;

   }

   private void openHelpWindow()
   {
        JFrame frame = new JFrame();
        frame.setResizable(false); //Source: https://stackoverflow.com/questions/10157235/how-do-i-disable-a-jpanel-so-that-it-cant-change-size-in-java
        frame.setLayout(new BorderLayout(10,5));
        frame.setTitle("Tips and Advice");
        frame.setSize(600,250);
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
