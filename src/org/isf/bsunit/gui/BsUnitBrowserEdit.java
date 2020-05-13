package org.isf.bsunit.gui;

import org.isf.bsunit.manager.BsUnitBrowserManager;
import org.isf.bsunit.model.BsUnit;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.jobjects.VoLimitedTextField;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.EventListener;

public class BsUnitBrowserEdit extends JDialog {
	private static final long serialVersionUID = 1L;
	private final EventListenerList bsUnitListeners = new EventListenerList();

	public interface BsUnitListener extends EventListener {
		public void bsUnitUpdated(AWTEvent e);

		public void bsUnitInserted(AWTEvent e);
	}

	public void addBsUnitListener(BsUnitListener l) {
		bsUnitListeners.add(BsUnitListener.class, l);
	}

	public void removeBsUnitListener(BsUnitListener listener) {
		bsUnitListeners.remove(BsUnitListener.class, listener);
	}

	private void fireDiseaseInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = bsUnitListeners.getListeners(BsUnitListener.class);
		for (int i = 0; i < listeners.length; i++)
			((BsUnitListener) listeners[i]).bsUnitInserted(event);
	}

	private void fireDiseaseUpdated() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = bsUnitListeners.getListeners(BsUnitListener.class);
		for (int i = 0; i < listeners.length; i++)
			((BsUnitListener) listeners[i]).bsUnitUpdated(event);
	}

	private JPanel jContentPane = null;
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JTextField descriptionTextField = null;
	private VoLimitedTextField codeTextField = null;
	private String lastdescription;
	private BsUnit bsUnit = null;
	private boolean insert;
	private JPanel jDataPanel = null;
	private JLabel jCodeLabel = null;
	private JPanel jCodeLabelPanel = null;
	private JPanel jDescriptionLabelPanel = null;
	private JLabel jDescripitonLabel = null;

	/**
	 * This is the default constructor; we pass the arraylist and the selectedrow
	 * because we need to update them
	 */
	public BsUnitBrowserEdit(JFrame owner, BsUnit old, boolean inserting) {
		super(owner, true);
		insert = inserting;
		bsUnit = old;//disease will be used for every operation
		lastdescription = bsUnit.getDescription();
		initialize();
	}


	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {

//		this.setBounds(300,300,350,180);
		this.setContentPane(getJContentPane());
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.bsunit.newrecord"));
		} else {
			this.setTitle(MessageBundle.getMessage("angal.bsunit.editingrecord"));
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getDataPanel(), BorderLayout.NORTH);  // Generated
			jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);  // Generated
		}
		return jContentPane;
	}

	/**
	 * This method initializes dataPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getDataPanel() {
		if (dataPanel == null) {
			dataPanel = new JPanel();
			//dataPanel.setLayout(new BoxLayout(getDataPanel(), BoxLayout.Y_AXIS));  // Generated
			dataPanel.add(getJDataPanel(), null);
		}
		return dataPanel;
	}

	/**
	 * This method initializes buttonPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton(), null);  // Generated
			buttonPanel.add(getCancelButton(), null);  // Generated
		}
		return buttonPanel;
	}

	/**
	 * This method initializes cancelButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setMnemonic(KeyEvent.VK_C);
			cancelButton.setText(MessageBundle.getMessage("angal.common.cancel"));  // Generated
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes okButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(MessageBundle.getMessage("angal.common.ok"));  // Generated
			okButton.setMnemonic(KeyEvent.VK_O);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					BsUnitBrowserManager manager = Context.getApplicationContext().getBean(BsUnitBrowserManager.class);

					try {
						if (descriptionTextField.getText().equals(lastdescription)) {
							dispose();
						}
						bsUnit.setDescription(descriptionTextField.getText());
						bsUnit.setCode(codeTextField.getText());
						boolean result = false;
						if (insert) {      // inserting
							result = manager.newBsUnit(bsUnit);
							if (result) {
								fireDiseaseInserted();
							}
							if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.sql.thedatacouldnotbesaved"));
							else dispose();
						} else {                          // updating
							if (descriptionTextField.getText().equals(lastdescription)) {
								dispose();
							} else {
								result = manager.updateBsUnit(bsUnit);
								if (result) {
									fireDiseaseUpdated();
								}
								if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.sql.thedatacouldnotbesaved"));
								else dispose();
							}

						}
					} catch (OHServiceException ex) {
						if (ex.getMessages() != null) {
							for (OHExceptionMessage msg : ex.getMessages()) {
								JOptionPane.showMessageDialog(null, msg.getMessage(), msg.getTitle() == null ? "" : msg.getTitle(), msg.getLevel().getSwingSeverity());
							}
						}
					}
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes descriptionTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDescriptionTextField() {
		if (descriptionTextField == null) {
			descriptionTextField = new JTextField(20);
			if (!insert) {
				descriptionTextField.setText(bsUnit.getDescription());
				lastdescription = bsUnit.getDescription();
			}
		}
		return descriptionTextField;
	}

	/**
	 * This method initializes codeTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getCodeTextField() {
		if (codeTextField == null) {
			codeTextField = new VoLimitedTextField(10);
			if (!insert) {
				codeTextField.setText(bsUnit.getCode());
				codeTextField.setEnabled(false);
			}
		}
		return codeTextField;
	}

	/**
	 * This method initializes jDataPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJDataPanel() {
		if (jDataPanel == null) {
			jDataPanel = new JPanel();
			jDataPanel.setLayout(new BoxLayout(getJDataPanel(), BoxLayout.Y_AXIS));
			jDataPanel.add(getJCodeLabelPanel(), null);
			jDataPanel.add(getCodeTextField(), null);
			jDataPanel.add(getJDescriptionLabelPanel(), null);
			jDataPanel.add(getDescriptionTextField(), null);
		}
		return jDataPanel;
	}

	/**
	 * This method initializes jCodeLabel
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getJCodeLabel() {
		if (jCodeLabel == null) {
			jCodeLabel = new JLabel();
			jCodeLabel.setText(MessageBundle.getMessage("angal.bsunit.codemaxchars"));
		}
		return jCodeLabel;
	}

	/**
	 * This method initializes jCodeLabelPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJCodeLabelPanel() {
		if (jCodeLabelPanel == null) {
			jCodeLabelPanel = new JPanel();
			//jCodeLabelPanel.setLayout(new BorderLayout());
			jCodeLabelPanel.add(getJCodeLabel(), BorderLayout.CENTER);
		}
		return jCodeLabelPanel;
	}

	/**
	 * This method initializes jDescriptionLabelPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJDescriptionLabelPanel() {
		if (jDescriptionLabelPanel == null) {
			jDescripitonLabel = new JLabel();
			jDescripitonLabel.setText(MessageBundle.getMessage("angal.common.description"));
			jDescriptionLabelPanel = new JPanel();
			jDescriptionLabelPanel.add(jDescripitonLabel, null);
		}
		return jDescriptionLabelPanel;
	}


}  //  @jve:decl-index=0:visual-constraint="146,61"

