package org.isf.tempunit.gui;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.tempunit.manager.TempUnitBrowserManager;
import org.isf.tempunit.model.TempUnit;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.jobjects.VoLimitedTextField;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.EventListener;

public class TempUnitBrowserEdit extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final EventListenerList tempUnitListeners = new EventListenerList();

	public interface TempUnitListener extends EventListener {
		public void tempUnitUpdated(AWTEvent e);

		public void tempUnitInserted(AWTEvent e);
	}

	public void addTempUnitListener(TempUnitListener l) {
		tempUnitListeners.add(TempUnitListener.class, l);
	}

	public void removeTempUnitListener(TempUnitListener listener) {
		tempUnitListeners.remove(TempUnitListener.class, listener);
	}

	private void fireDiseaseInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = tempUnitListeners.getListeners(TempUnitListener.class);
		for (int i = 0; i < listeners.length; i++)
			((TempUnitListener) listeners[i]).tempUnitInserted(event);
	}

	private void fireDiseaseUpdated() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = tempUnitListeners.getListeners(TempUnitListener.class);
		for (int i = 0; i < listeners.length; i++)
			((TempUnitListener) listeners[i]).tempUnitUpdated(event);
	}

	private JPanel jContentPane = null;
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JTextField descriptionTextField = null;
	private VoLimitedTextField codeTextField = null;
	private String lastdescription;
	private TempUnit tempUnit = null;
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
	public TempUnitBrowserEdit(JFrame owner, TempUnit old, boolean inserting) {
		super(owner, true);
		insert = inserting;
		tempUnit = old;//disease will be used for every operation
		lastdescription = tempUnit.getDescription();
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
					TempUnitBrowserManager manager = Context.getApplicationContext().getBean(TempUnitBrowserManager.class);

					try {
						if (descriptionTextField.getText().equals(lastdescription)) {
							dispose();
						}
						tempUnit.setDescription(descriptionTextField.getText());
						tempUnit.setCode(codeTextField.getText());
						boolean result = false;
						if (insert) {      // inserting
							result = manager.newTempUnit(tempUnit);
							if (result) {
								fireDiseaseInserted();
							}
							if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.sql.thedatacouldnotbesaved"));
							else dispose();
						} else {                          // updating
							if (descriptionTextField.getText().equals(lastdescription)) {
								dispose();
							} else {
								result = manager.updateTempUnit(tempUnit);
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
				descriptionTextField.setText(tempUnit.getDescription());
				lastdescription = tempUnit.getDescription();
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
			codeTextField = new VoLimitedTextField(2);
			if (!insert) {
				codeTextField.setText(tempUnit.getCode());
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
			jCodeLabel.setText(MessageBundle.getMessage("angal.tempunit.codemaxchars"));
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


