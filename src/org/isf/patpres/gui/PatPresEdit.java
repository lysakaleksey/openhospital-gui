package org.isf.patpres.gui;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patpres.manager.PatPresManager;
import org.isf.patpres.model.PatientPresentation;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.isf.utils.jobjects.CustomJDateChooser;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.utils.time.RememberDates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PatPresEdit extends JDialog {
	private static final long serialVersionUID = -4271389493861772053L;
	private static final String VERSION = MessageBundle.getMessage("angal.versione");
	private boolean insert = false;

	private PatientPresentation patPres = null;
	private JPanel contentPanel = null;
	private JPanel mainDataPanel = null;
	private JPanel vitalsDataPanel = null;
	private JPanel patientDataPanel = null;
	private JPanel patientSearchPanel = null;
	private JPanel buttonPanel = null;

	private JLabel presentDateLabel = null;
	private JLabel consultDateLabel = null;
	private JLabel previousDateLabel = null;
	private JLabel progrLabel = null;

	private GregorianCalendar presentDateIn = null;
	private CustomJDateChooser presentDateCal = null;
	private CustomJDateChooser consultDateCal = null;
	private CustomJDateChooser previousDateCal = null;


	// patient
	private JLabel patientLabel = null;
	private JLabel nameLabel = null;
	private JLabel ageLabel = null;
	private JLabel sexLabel = null;
	private VoLimitedTextField patTextField = null;
	private VoLimitedTextField ageTextField = null;
	private VoLimitedTextField sexTextField = null;
	private JComboBox patientComboBox = null;
	private JTextField patientSourceField;

	// buttons
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JButton jSearchButton = null;

	private VoLimitedTextField progrTextField = null;

	private Patient selectedPatient = null;
	private String lastKey;
	private String s;
	private ArrayList<Patient> pat = null;

	private static final Integer panelWidth = 600;
	private static final Integer labelWidth = 50;
	private static final Integer calendarWidth = 110;
	private static final Integer dataPanelHeight = 300;
	private static final Integer dataVitalsHeight = 200;
	private static final Integer dataPatientHeight = 100;
	private static final Integer buttonPanelHeight = 40;
	private static final Integer deltaBetweenLabels = 40;

	public PatPresEdit(JFrame myFrameIn, PatientPresentation patPresIn, boolean action) {
		super(myFrameIn, true);
		insert = action;
		patPres = patPresIn;
		selectedPatient = patPresIn.getPatient();
		initialize();
	}

	private int getPatientPresentationYMaxProg() {
//		PatPresManager manager = Context.getApplicationContext().getBean(PatPresManager.class);
//		try {
//			return manager.getProgYear(0);
//		} catch (OHServiceException e) {
//			OHServiceExceptionUtil.showMessages(e);
//			return 0;
//		}
		return 0;
	}

	/**
	 * This method initializes this Frame, sets the correct Dimensions
	 */
	private void initialize() {
		this.setBounds(30, 100, panelWidth, dataPanelHeight + dataPatientHeight + dataVitalsHeight + buttonPanelHeight + 30);
		this.setContentPane(getJContentPane());
		this.setResizable(false);
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.patvac.newpatientvaccine") + "(" + VERSION + ")");
		} else {
			this.setTitle(MessageBundle.getMessage("angal.patvac.edipatientvaccine") + "(" + VERSION + ")");
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * This method initializes jContentPane, adds the main parts of the frame
	 *
	 * @return jContentPanel (JPanel)
	 */
	private JPanel getJContentPane() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.add(getMainDataPanel());
			contentPanel.add(getVitalsDataPanel());
			contentPanel.add(getPatientDataPanel());
			contentPanel.add(getButtonPanel());
		}
		return contentPanel;
	}

	/**
	 * This method initializes dataPanel. This panel contains all items (combo
	 * boxes,calendar) to define a vaccine
	 *
	 * @return dataPanel (JPanel)
	 */
	private JPanel getMainDataPanel() {
		if (mainDataPanel == null) {
			// initialize data panel
			mainDataPanel = new JPanel();
			mainDataPanel.setLayout(null);
			mainDataPanel.setBounds(0, 0, panelWidth, dataPanelHeight);

			// presentation date
			int next_x = 5;
			int next_y = 10;
			presentDateLabel = new JLabel(MessageBundle.getMessage("angal.patpres.presentationdate"));
			presentDateLabel.setBounds(next_x, next_y, 200, 20);
			next_x += 200;
			presentDateCal = getPresentationDateFieldCal();
			presentDateCal.setLocale(new Locale(GeneralData.LANGUAGE));
			presentDateCal.setDateFormatString("dd/MM/yy");
			presentDateCal.setBounds(next_x, next_y, calendarWidth, 20);

			// consultation end date
			next_x = 5;
			next_y += 30;
			consultDateLabel = new JLabel(MessageBundle.getMessage("angal.patpres.consultenddate"));
			consultDateLabel.setBounds(next_x, next_y, 200, 20);
			next_x += 200;
			consultDateCal = getConsultationEndDateFieldCal();
			consultDateCal.setLocale(new Locale(GeneralData.LANGUAGE));
			consultDateCal.setDateFormatString("dd/MM/yy");
			consultDateCal.setBounds(next_x, next_y, calendarWidth, 20);

			// previous consult date
			next_x = 5;
			next_y += 30;
			previousDateLabel = new JLabel(MessageBundle.getMessage("angal.patpres.previousconsdate"));
			previousDateLabel.setBounds(next_x, next_y, 200, 20);
			next_x += 200;
			previousDateCal = getPreviousDateCalFieldCal();
			previousDateCal.setLocale(new Locale(GeneralData.LANGUAGE));
			previousDateCal.setDateFormatString("dd/MM/yy");
			previousDateCal.setBounds(next_x, next_y, calendarWidth, 20);



			// add all to the data panel
			mainDataPanel.add(presentDateLabel, null);
			mainDataPanel.add(presentDateCal, null);
			mainDataPanel.add(consultDateLabel, null);
			mainDataPanel.add(consultDateCal, null);
			mainDataPanel.add(previousDateLabel, null);
			mainDataPanel.add(previousDateCal, null);
			mainDataPanel.add(getPatientSearchPanel(), null);
		}
		return mainDataPanel;
	}

	/**
	 * This method initializes getPatientSearchPanel
	 *
	 * @return JPanel
	 */

	private JPanel getPatientSearchPanel() {
		if (patientSearchPanel == null) {
			patientSearchPanel = new JPanel();
			patientSearchPanel.setLayout(null);
			patientSearchPanel.setBounds(0, 0, 600, 100);

			// patient code label
			int next_x = 330;
			int next_y = 10;
			patientLabel = new JLabel(MessageBundle.getMessage("angal.patvac.patientcode"));
			patientLabel.setBounds(next_x, next_y, 150, 20);
			next_y += 30;
			// patient code box
			patientSourceField = new JTextField();
			patientSourceField.setBounds(next_x, next_y, 50, 20);
			if (GeneralData.ENHANCEDSEARCH) {
				patientSourceField.addKeyListener(new KeyListener() {
					public void keyPressed(KeyEvent e) {
						int key = e.getKeyCode();
						if (key == KeyEvent.VK_ENTER) {
							jSearchButton.doClick();
						}
					}
					public void keyReleased(KeyEvent e) {
					}
					public void keyTyped(KeyEvent e) {
					}
				});
			} else {
				patientSourceField.addKeyListener(new KeyListener() {
					public void keyTyped(KeyEvent e) {
						lastKey = "";
						String s = "" + e.getKeyChar();
						if (Character.isLetterOrDigit(e.getKeyChar())) {
							lastKey = s;
						}
						s = patientSourceField.getText() + lastKey;
						s = s.trim();
						filterPatient(s);
					}
					public void keyPressed(KeyEvent e) {
					}
					public void keyReleased(KeyEvent e) {
					}
				});
			}
			patientSearchPanel.add(patientLabel, null);
			patientSearchPanel.add(patientSourceField, null);

			// patient data
			next_y += 30;
			patientComboBox = new JComboBox();
			patientComboBox.setBounds(next_x, next_y, 250, 20);
			patientComboBox.addItem(MessageBundle.getMessage("angal.patvac.selectapatient"));

			if (GeneralData.ENHANCEDSEARCH) {
				next_x += patientSourceField.getBounds().getWidth() + 5;
				next_y -= 30;
				patientSearchPanel.add(getJSearchButton(next_x, next_y), null);
				s = (insert ? "-" : patPres.getPatient().getName());
			}
			patientComboBox = getPatientComboBox(s);

			if (!insert) {
				patientComboBox.setEnabled(false);
				patientSourceField.setEnabled(false);
			}

			patientSearchPanel.add(patientComboBox, null);
		}
		return patientSearchPanel;
	}

	/**
	 * This method initializes getJSearchButton
	 *
	 * @return JButton
	 */
	private JButton getJSearchButton(int next_x, int next_y) {
		if (jSearchButton == null) {
			jSearchButton = new JButton();
			jSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			jSearchButton.setPreferredSize(new Dimension(20, 20));
			jSearchButton.setBounds(next_x, next_y, 20, 20);
			if (!insert) {
				jSearchButton.setEnabled(false);
			}
			jSearchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					patientComboBox.removeAllItems();
					resetPatVacPat();
					getPatientComboBox(patientSourceField.getText());
				}
			});
		}
		return jSearchButton;
	}

	/**
	 * This method initializes getPresentationDateFieldCal
	 *
	 * @return JDateChooser
	 */
	private CustomJDateChooser getPresentationDateFieldCal() {
		java.util.Date myDate = null;
		if (insert) {
			presentDateIn = RememberDates.getLastPatientPresentationDate();
		} else {
			presentDateIn = patPres.getPresentationDate();
		}
		if (presentDateIn != null) {
			myDate = presentDateIn.getTime();
		}

		return (new CustomJDateChooser(myDate, "dd/MM/yy"));
	}

	/**
	 * This method initializes getConsultationEndDateFieldCal
	 *
	 * @return JDateChooser
	 */
	private CustomJDateChooser getConsultationEndDateFieldCal() {
		java.util.Date myDate = null;
		if (patPres.getConsultationEnd() != null) {
			myDate = patPres.getConsultationEnd().getTime();
		}
		return (new CustomJDateChooser(myDate, "dd/MM/yy"));
	}

	/**
	 * This method initializes getPreviousDateCalFieldCal
	 *
	 * @return JDateChooser
	 */
	private CustomJDateChooser getPreviousDateCalFieldCal() {
		java.util.Date myDate = null;
		if (patPres.getPreviousConsult() != null) {
			myDate = patPres.getPreviousConsult().getTime();
		}
		return (new CustomJDateChooser(myDate, "dd/MM/yy"));
	}

	/**
	 * This method filter patient based on search string
	 *
	 * @return void
	 */
	private void filterPatient(String key) {
		patientComboBox.removeAllItems();

		if (key == null || key.compareTo("") == 0) {
			patientComboBox.addItem(MessageBundle.getMessage("angal.patvac.selectapatient"));
			resetPatVacPat();
		}

		for (Patient elem : pat) {
			if (key != null) {
				// Search key extended to name and code
				StringBuilder sbName = new StringBuilder();
				sbName.append(elem.getSecondName().toUpperCase());
				sbName.append(elem.getFirstName().toUpperCase());
				sbName.append(elem.getCode());
				String name = sbName.toString();

				if (name.toLowerCase().contains(key.toLowerCase())) {
					patientComboBox.addItem(elem);
				}
			} else {
				patientComboBox.addItem(elem);
			}
		}

		if (patientComboBox.getItemCount() == 1) {
			selectedPatient = (Patient) patientComboBox.getSelectedItem();
			setPatient(selectedPatient);
		}

		if (patientComboBox.getItemCount() > 0) {
			if (patientComboBox.getItemAt(0) instanceof Patient) {
				selectedPatient = (Patient) patientComboBox.getItemAt(0);
				setPatient(selectedPatient);
			} else
				selectedPatient = null;
		} else
			selectedPatient = null;
	}

	/**
	 * This method reset patient's additonal data
	 *
	 * @return void
	 */
	private void resetPatVacPat() {
		patTextField.setText("");
		ageTextField.setText("");
		sexTextField.setText("");
		selectedPatient = null;
	}

	/**
	 * This method sets patient's additonal data
	 *
	 * @return void
	 */
	private void setPatient(Patient selectedPatient) {
		patTextField.setText(selectedPatient.getName());
		ageTextField.setText(selectedPatient.getAge() + "");
		sexTextField.setText(selectedPatient.getSex() + "");
	}

	/**
	 * This method initializes patientComboBox. It used to display available
	 * patients
	 *
	 * @return patientComboBox (JComboBox)
	 */
	private JComboBox getPatientComboBox(String regExp) {

		Patient patSelected = null;
		PatientBrowserManager patBrowser = Context.getApplicationContext().getBean(PatientBrowserManager.class);

		if (GeneralData.ENHANCEDSEARCH) {
			try {
				pat = patBrowser.getPatientWithHeightAndWeight(regExp);
			} catch (OHServiceException ex) {
				OHServiceExceptionUtil.showMessages(ex);
				pat = new ArrayList<Patient>();
			}
		} else {
			try {
				pat = patBrowser.getPatient();
			} catch (OHServiceException e) {
				OHServiceExceptionUtil.showMessages(e);
			}
		}
		if (pat != null) {
			for (Patient elem : pat) {
				if (!insert) {
					if (elem.getCode().equals(patPres.getPatient().getCode())) {
						patSelected = elem;
					}
				}
				patientComboBox.addItem(elem);
			}
		}
		if (patSelected != null) {
			patientComboBox.setSelectedItem(patSelected);
			selectedPatient = (Patient) patientComboBox.getSelectedItem();
		} else {
			if (patientComboBox.getItemCount() > 0 && GeneralData.ENHANCEDSEARCH) {
				if (patientComboBox.getItemAt(0) instanceof Patient) {
					selectedPatient = (Patient) patientComboBox.getItemAt(0);
					setPatient(selectedPatient);
				} else
					selectedPatient = null;
			} else
				selectedPatient = null;
		}
		patientComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (patientComboBox.getSelectedIndex() > 0) {
					selectedPatient = (Patient) patientComboBox.getSelectedItem();
					setPatient(selectedPatient);
				} else
					selectedPatient = null;
			}
		});

		return patientComboBox;
	}

	/**
	 * This method initializes dataVitals. This panel contains vitals data
	 *
	 * @return dataVitals (JPanel)
	 */
	private JPanel getVitalsDataPanel() {
		if (vitalsDataPanel == null) {
			vitalsDataPanel = new JPanel();
			vitalsDataPanel.setLayout(null);
			vitalsDataPanel.setBounds(0, dataPanelHeight, panelWidth, dataVitalsHeight);
			vitalsDataPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.patpres.datavitals")));

		}
		return vitalsDataPanel;
	}

	/**
	 * This method initializes dataPatient. This panel contains patient's data
	 *
	 * @return dataPatient (JPanel)
	 */
	private JPanel getPatientDataPanel() {
		if (patientDataPanel == null) {
			patientDataPanel = new JPanel();
			patientDataPanel.setLayout(null);
			patientDataPanel.setBounds(0, dataPanelHeight + dataVitalsHeight, panelWidth, dataPatientHeight);
			patientDataPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.patvac.datapatient")));
			nameLabel = new JLabel(MessageBundle.getMessage("angal.patvac.name"));
			nameLabel.setBounds(10, deltaBetweenLabels, labelWidth, 20);
			patTextField = getPatientTextField();
			patTextField.setBounds(labelWidth + 5, deltaBetweenLabels, 180, 20);
			ageLabel = new JLabel(MessageBundle.getMessage("angal.patvac.age"));
			ageLabel.setBounds(255, deltaBetweenLabels, 35, 20);
			ageTextField = getAgeTextField();
			ageTextField.setBounds(295, deltaBetweenLabels, 50, 20);
			sexLabel = new JLabel(MessageBundle.getMessage("angal.patvac.sex"));
			sexLabel.setBounds(370, deltaBetweenLabels, 80, 20);
			sexTextField = getSexTextField();
			sexTextField.setBounds(440, deltaBetweenLabels, 50, 20);

			// add all elements
			patientDataPanel.add(nameLabel, null);
			patientDataPanel.add(patTextField, null);
			patientDataPanel.add(ageLabel, null);
			patientDataPanel.add(ageTextField, null);
			patientDataPanel.add(sexLabel, null);
			patientDataPanel.add(sexTextField, null);
			patTextField.setEditable(false);
			ageTextField.setEditable(false);
			sexTextField.setEditable(false);
		}
		return patientDataPanel;
	}

	/**
	 * This method initializes getPatientTextField about patient name
	 *
	 * @return patTextField (VoLimitedTextField)
	 */
	private VoLimitedTextField getPatientTextField() {
		if (patTextField == null) {
			patTextField = new VoLimitedTextField(100);
			if (!insert) {
				patTextField.setText(patPres.getPatient().getName());
			}
		}
		return patTextField;
	}

	/**
	 * This method initializes getAgeTextField about patient
	 *
	 * @return ageTextField (VoLimitedTextField)
	 */
	private VoLimitedTextField getAgeTextField() {
		if (ageTextField == null) {
			ageTextField = new VoLimitedTextField(3);
			if (insert) {
				ageTextField.setText("");
			} else {
				try {
					Integer intAge = patPres.getPatient().getAge();
					ageTextField.setText(intAge.toString());
				} catch (Exception e) {
					ageTextField.setText("");
				}
			}
		}
		return ageTextField;
	}

	/**
	 * This method initializes getSexTextField about patient
	 *
	 * @return sexTextField (VoLimitedTextField)
	 */
	private VoLimitedTextField getSexTextField() {
		if (sexTextField == null) {
			sexTextField = new VoLimitedTextField(1);
			if (!insert) {
				sexTextField.setText("" + patPres.getPatient().getSex());
			}
		}
		return sexTextField;
	}

	/**
	 * This method initializes buttonPanel, that contains the buttons of the
	 * frame (on the bottom)
	 *
	 * @return buttonPanel (JPanel)
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setBounds(0, dataPanelHeight + dataVitalsHeight + dataPatientHeight, panelWidth, buttonPanelHeight);
			buttonPanel.add(getOkButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes okButton. It is used to update db data
	 *
	 * @return okButton (JPanel)
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(MessageBundle.getMessage("angal.common.ok"));
			okButton.setMnemonic(KeyEvent.VK_O);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					GregorianCalendar gregDate = new GregorianCalendar();
					gregDate.setTime(presentDateCal.getDate());
					//patPres.setProgr(Integer.parseInt(progrTextField.getText()));

					// check on patient
					if (selectedPatient == null) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patvac.pleaseselectapatient"));
						return;
					}

					//patPres.setVaccineDate(gregDate);
					//patPres.setVaccine((Vaccine) vaccineComboBox.getSelectedItem());
					patPres.setPatient(selectedPatient);
					//patPres.setLock(0);
					//patPres.setPatName(selectedPatient.getName());
					//patPres.setPatSex(selectedPatient.getSex());

					boolean result;
					PatPresManager manager = Context.getApplicationContext().getBean(PatPresManager.class);
					// handling db insert/update
					if (insert) {
						//patPres.setPatAge(selectedPatient.getAge());
						try {
							result = manager.newPatientPresentation(patPres);
						} catch (OHServiceException e1) {
							OHServiceExceptionUtil.showMessages(e1);
							result = false;
						}
					} else {
						try {
							result = manager.updatePatientPresentation(patPres);
						} catch (OHServiceException e1) {
							OHServiceExceptionUtil.showMessages(e1);
							result = false;
						}
					}

					if (!result)
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patvac.thedatacouldnobesaved"));
					else {
						patPres = new PatientPresentation();//(0, 0, new GregorianCalendar(), new Patient(), new Vaccine("", "", new VaccineType("", "")), 0);
						patPres.setPatient(new Patient());
						patPres.setPatient(new Patient());
						dispose();
					}
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes cancelButton.
	 *
	 * @return cancelButton (JPanel)
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(MessageBundle.getMessage("angal.common.cancel"));
			cancelButton.setMnemonic(KeyEvent.VK_C);
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}
}
