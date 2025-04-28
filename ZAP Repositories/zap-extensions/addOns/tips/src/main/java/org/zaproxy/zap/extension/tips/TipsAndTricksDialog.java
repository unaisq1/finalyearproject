package org.zaproxy.zap.extension.tips;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Dialog.ModalityType;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.AbstractDialog;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.help.ExtensionHelp;
import org.zaproxy.zap.utils.ZapTextArea;
import org.zaproxy.zap.view.LayoutHelper;

@SuppressWarnings("serial")
public class TipsAndTricksDialog extends AbstractDialog {
   private static final long serialVersionUID = -1L;
   private ExtensionTipsAndTricks ext;
   private JPanel jPanel = null;
   private JButton btnAllTips = null;
   private JButton btnNextTip = null;
   private JButton btnClose = null;
   private ZapTextArea txtTip = null;
   private JScrollPane scrollPane = null;
   private JPanel jPanel1 = null;
   private String lastTip = null;

   public TipsAndTricksDialog(ExtensionTipsAndTricks ext, Frame parent) throws HeadlessException {
      super(parent, true);
      this.ext = ext;
      this.initialize();
   }

   private void initialize() {
      this.setVisible(false);
      this.setResizable(false);
      this.setModalityType(ModalityType.DOCUMENT_MODAL);
      this.setTitle(Constant.messages.getString("tips.dialog.title"));
      this.setContentPane(this.getJPanel());
      if (Model.getSingleton().getOptionsParam().getViewParam().getWmUiHandlingOption() == 0) {
         this.setSize(300, 235);
      }

      this.centreDialog();
      this.getRootPane().setDefaultButton(this.btnNextTip);
      this.pack();
   }

   public void displayTip() {
      String tip;
      for(tip = this.ext.getRandomTip(); tip.equals(this.lastTip); tip = this.ext.getRandomTip()) {
      }

      this.getTxtTip().setText(tip);
      this.getScrollPane().getViewport().setViewPosition(new Point(0, 0));
      this.lastTip = tip;
      this.setVisible(true);
   }

   private JPanel getJPanel() {
      if (this.jPanel == null) {
         this.jPanel = new JPanel();
         this.jPanel.setLayout(new GridBagLayout());
         this.jPanel.add(this.getScrollPane(), LayoutHelper.getGBC(0, 0, 3, 1.0D, 1.0D));
         this.jPanel.add(this.getAllTipsButton(), LayoutHelper.getGBC(0, 2, 1, 0.0D, 0.0D));
         this.jPanel.add(new JLabel(), LayoutHelper.getGBC(1, 2, 1, 1.0D, 0.0D));
         this.jPanel.add(this.getButtonPanel(), LayoutHelper.getGBC(2, 2, 1, 0.0D, 0.0D));
      }

      return this.jPanel;
   }

   private JButton getAllTipsButton() {
      if (this.btnAllTips == null) {
         this.btnAllTips = new JButton();
         this.btnAllTips.setText(Constant.messages.getString("tips.button.allTips"));
         this.btnAllTips.addActionListener((e) -> {
            ExtensionHelp.showHelp("tips");

         });
      }

      return this.btnAllTips;
   }

   private JButton getNextTipButton() {
      if (this.btnNextTip == null) {
         this.btnNextTip = new JButton();
         this.btnNextTip.setText(Constant.messages.getString("tips.button.nextTip"));
         this.btnNextTip.addActionListener((e) -> {
            this.displayTip();
         });
      }

      return this.btnNextTip;
   }

   private JButton getCloseButton() {
      if (this.btnClose == null) {
         this.btnClose = new JButton();
         this.btnClose.setText(Constant.messages.getString("all.button.close"));
         this.btnClose.addActionListener((e) -> {
            this.setVisible(false);
         });
      }

      return this.btnClose;
   }

   private JScrollPane getScrollPane() {
      if (this.scrollPane == null) {
         this.scrollPane = new JScrollPane();
         this.scrollPane.setHorizontalScrollBarPolicy(31);
         this.scrollPane.setVerticalScrollBarPolicy(20);
         this.scrollPane.setMinimumSize(new Dimension(300, 200));
         this.scrollPane.setPreferredSize(new Dimension(300, 200));
         this.scrollPane.setViewportView(this.getTxtTip());
      }

      return this.scrollPane;
   }

   private ZapTextArea getTxtTip() {
      if (this.txtTip == null) {
         this.txtTip = new ZapTextArea();
         this.txtTip.setEditable(false);
         this.txtTip.setLineWrap(true);
         this.txtTip.setWrapStyleWord(true);
      }

      return this.txtTip;
   }

   private JPanel getButtonPanel() {
      if (this.jPanel1 == null) {
         this.jPanel1 = new JPanel();
         this.jPanel1.setMinimumSize(new Dimension(300, 35));
         this.jPanel1.add(this.getCloseButton(), (Object)null);
         this.jPanel1.add(this.getNextTipButton(), (Object)null);
      }

      return this.jPanel1;
   }
}
