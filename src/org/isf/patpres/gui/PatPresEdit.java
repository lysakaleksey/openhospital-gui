package org.isf.patpres.gui;

import org.isf.bsunit.manager.BsUnitBrowserManager;
import org.isf.bsunit.model.BsUnit;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patpres.manager.PatPresManager;
import org.isf.patpres.model.Bp;
import org.isf.patpres.model.PatientPresentation;
import org.isf.patpres.model.Vitals;
import org.isf.tempunit.manager.TempUnitBrowserManager;
import org.isf.tempunit.model.TempUnit;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.isf.utils.jobjects.CustomJDateChooser;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.utils.time.RememberDates;
import org.springframework.util.StringUtils;

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

	private GregorianCalendar presentDateIn = null;
	private CustomJDateChooser presentDateCal = null;
	private CustomJDateChooser consultDateCal = null;
	private CustomJDateChooser previousDateCal = null;

	private JTextField referredFromField;
	private JTextArea patientAilmentField;
	private JTextArea doctorsAilmentField;
	private JTextArea specificSymptomsField;
	private JTextArea diagnosisField;
	private JTextArea prognosisField;
	private JTextArea patientAdviceField;
	private JTextArea prescribedField;
	private JTextArea followUpField;
	private JTextField referredToField;
	private JTextArea summaryField;
	private JTextField vitalsWeightField;
	private JTextField vitalsHeightField;
	private JTextField vitalsBloodSugarField;
	private JTextField vitalsTemperatureField;
	private JTextField vitalsSystoleField;
	private JTextField vitalsDiastoleField;
	private JComboBox vitalsBsUnitField;
	private JComboBox vitalsTempUnitField;

	private VoLimitedTextField patTextField = null;
	private VoLimitedTextField ageTextField = null;
	private VoLimitedTextField sexTextField = null;
	private JComboBox patientComboBox = null;
	private JTextField patientSourceField;

	// buttons
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JButton jSearchButton = null;

	private ArrayList<Patient> patients = null;
	private Patient selectedPatient = null;
	private String lastKey;
	private String s;

	private static final Integer panelWidth = 800;
	private static final Integer labelWidth = 50;
	private static final Integer calendarWidth = 110;
	private static final Integer dataPanelHeight = 300;
	private static final Integer dataVitalsHeight = 150;
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

	/**
	 * This method initializes this Frame, sets the correct Dimensions
	 */
	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		final int pfrmBase = 20;
		final int pfrmWidth = 17;
		final int pfrmHeight = 12;
		this.setBounds((screensize.width - (int) (screensize.width * 0.75)) / 2, 0,
			screensize.width * pfrmWidth / pfrmBase + 50, screensize.height);

		JScrollPane scrollPane = new JScrollPane();
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(getJContentPane());
		this.setResizable(true);
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.patpres.newpatientpresentation") + "(" + VERSION + ")");
		} else {
			this.setTitle(MessageBundle.getMessage("angal.patpres.editpatientpresentation") + "(" + VERSION + ")");
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}

	/**
	 * This method initializes jContentPane, adds the main parts of the frame
	 *
	 * @return jContentPanel (JPanel)
	 */
	private JPanel getJContentPane() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
			contentPanel.add(getPatientSearchPanel(), null);
			contentPanel.add(getPatientDataPanel(), null);
			contentPanel.add(getMainDataPanel(), null);
			contentPanel.add(getVitalsDataPanel(), null);
			contentPanel.add(getButtonPanel(), null);
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
			mainDataPanel.setLayout(new BoxLayout(mainDataPanel, BoxLayout.Y_AXIS));
			mainDataPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.patpres.presentationdata")));

			// Presentation dates
			mainDataPanel.add(getMainDatesPanel(), null);

			// Referred From
			JLabel referredFromLabel = new JLabel(MessageBundle.getMessage("angal.patpres.referredfrom"));
			referredFromField = new JTextField(100);
			referredFromField.setMaximumSize(referredFromField.getPreferredSize());
			referredFromField.setText(patPres.getReferredFrom());
			mainDataPanel.add(referredFromLabel, null);
			mainDataPanel.add(referredFromField, null);

			// Referred to
			JLabel referredToLabel = new JLabel(MessageBundle.getMessage("angal.patpres.referredto"));
			referredToField = new JTextField(100);
			referredToField.setMaximumSize(referredToField.getPreferredSize());
			referredToField.setText(patPres.getReferredTo());
			mainDataPanel.add(referredToLabel, null);
			mainDataPanel.add(referredToField, null);

			// Patient ailment
			JLabel patientAilmentLabel = new JLabel(MessageBundle.getMessage("angal.patpres.patientailment"));
			patientAilmentField = new JTextArea();
			patientAilmentField.setRows(5);
			patientAilmentField.setText(patPres.getPatientAilmentDescription());
			mainDataPanel.add(patientAilmentLabel, null);
			mainDataPanel.add(new JScrollPane(patientAilmentField), null);

			// Doctors ailment
			JLabel doctorsAilmentLabel = new JLabel(MessageBundle.getMessage("angal.patpres.doctorsailment"));
			doctorsAilmentField = new JTextArea();
			doctorsAilmentField.setRows(5);
			doctorsAilmentField.setText(patPres.getDoctorsAilmentDescription());
			mainDataPanel.add(doctorsAilmentLabel, null);
			mainDataPanel.add(new JScrollPane(doctorsAilmentField), null);

			// Specific symptoms
			JLabel symptomsLabel = new JLabel(MessageBundle.getMessage("angal.patpres.symptoms"));
			specificSymptomsField = new JTextArea();
			specificSymptomsField.setRows(5);
			specificSymptomsField.setText(patPres.getSpecificSymptoms());
			mainDataPanel.add(symptomsLabel, null);
			mainDataPanel.add(new JScrollPane(specificSymptomsField), null);

			// Diagnosis
			JLabel diagnosisLabel = new JLabel(MessageBundle.getMessage("angal.patpres.diagnosis"));
			diagnosisField = new JTextArea();
			diagnosisField.setRows(5);
			diagnosisField.setText(patPres.getDiagnosis());
			mainDataPanel.add(diagnosisLabel, null);
			mainDataPanel.add(new JScrollPane(diagnosisField), null);

			// Prognosis
			JLabel prognosisLabel = new JLabel(MessageBundle.getMessage("angal.patpres.prognosis"));
			prognosisField = new JTextArea();
			prognosisField.setRows(5);
			prognosisField.setText(patPres.getPrognosis());
			mainDataPanel.add(prognosisLabel, null);
			mainDataPanel.add(new JScrollPane(prognosisField), null);

			// Patient advice
			JLabel patientAdviceLabel = new JLabel(MessageBundle.getMessage("angal.patpres.patientadvice"));
			patientAdviceField = new JTextArea();
			patientAdviceField.setRows(5);
			patientAdviceField.setText(patPres.getPatientAdvice());
			mainDataPanel.add(patientAdviceLabel, null);
			mainDataPanel.add(new JScrollPane(patientAdviceField), null);

			// Prescribed
			JLabel prescribedAdviceLabel = new JLabel(MessageBundle.getMessage("angal.patpres.prescribed"));
			prescribedField = new JTextArea();
			prescribedField.setRows(5);
			prescribedField.setText(patPres.getPrescribed());
			mainDataPanel.add(prescribedAdviceLabel, null);
			mainDataPanel.add(new JScrollPane(prescribedField), null);

			// Follow Up
			JLabel followupLabel = new JLabel(MessageBundle.getMessage("angal.patpres.followup"));
			followUpField = new JTextArea();
			followUpField.setRows(5);
			followUpField.setText(patPres.getFollowUp());
			mainDataPanel.add(followupLabel, null);
			mainDataPanel.add(new JScrollPane(followUpField), null);

			// Summary
			JLabel summaryLabel = new JLabel(MessageBundle.getMessage("angal.patpres.summary"));
			summaryField = new JTextArea();
			summaryField.setRows(3);
			summaryField.setText(patPres.getSummary());
			mainDataPanel.add(summaryLabel, null);
			mainDataPanel.add(new JScrollPane(summaryField), null);
		}

		return mainDataPanel;
	}

	private JPanel getMainDatesPanel() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new FlowLayout());

		// presentation date
		JLabel presentDateLabel = new JLabel(MessageBundle.getMessage("angal.patpres.presentationdate"));
		presentDateCal = getPresentationDateFieldCal();
		presentDateCal.setLocale(new Locale(GeneralData.LANGUAGE));
		presentDateCal.setDateFormatString("dd/MM/yy");
		jPanel.add(presentDateLabel, null);
		jPanel.add(presentDateCal, null);

		// consultation end date
		JLabel consultDateLabel = new JLabel(MessageBundle.getMessage("angal.patpres.consultenddate"));
		consultDateCal = getConsultationEndDateFieldCal();
		consultDateCal.setLocale(new Locale(GeneralData.LANGUAGE));
		consultDateCal.setDateFormatString("dd/MM/yy");
		jPanel.add(consultDateLabel, null);
		jPanel.add(consultDateCal, null);

		// previous consult date
		JLabel previousDateLabel = new JLabel(MessageBundle.getMessage("angal.patpres.previousconsdate"));
		previousDateCal = getPreviousDateCalFieldCal();
		previousDateCal.setLocale(new Locale(GeneralData.LANGUAGE));
		previousDateCal.setDateFormatString("dd/MM/yy");
		jPanel.add(previousDateLabel, null);
		jPanel.add(previousDateCal, null);

		return jPanel;
	}


	/**
	 * This method initializes getPatientSearchPanel
	 *
	 * @return JPanel
	 */
	private JPanel getPatientSearchPanel() {
		if (patientSearchPanel == null) {
			patientSearchPanel = new JPanel();
			patientSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			patientSearchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.patpres.searchpatient")));

			// patient code label
			JLabel patientLabel = new JLabel(MessageBundle.getMessage("angal.patpres.patientcode"));
			patientSearchPanel.add(patientLabel, null);

			// patient code box
			patientSourceField = new JTextField(5);
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
			patientSearchPanel.add(patientSourceField, null);

			// patient combo box
			patientComboBox = new JComboBox();
			patientComboBox.setMinimumSize(new Dimension(200, 20));
			patientComboBox.setMinimumSize(new Dimension(patientComboBox.getMaximumSize().width, patientComboBox.getMaximumSize().height));
			patientComboBox.setMaximumSize(new Dimension(patientComboBox.getMaximumSize().width, patientComboBox.getMaximumSize().height));
			patientComboBox.addItem(MessageBundle.getMessage("angal.patpres.searchpatient"));

			if (GeneralData.ENHANCEDSEARCH) {
				if (jSearchButton == null) {
					jSearchButton = new JButton();
					jSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
					jSearchButton.setPreferredSize(new Dimension(20, 20));
					if (!insert) {
						jSearchButton.setEnabled(false);
					}
					jSearchButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							patientComboBox.removeAllItems();
							resetPatPresPat();
							getPatientComboBox(patientSourceField.getText());
						}
					});
				}
				patientSearchPanel.add(jSearchButton, null);
				s = (insert ? "-" : patPres.getPatient().getName());
			}
			getPatientComboBox(s);
			if (!insert) {
				patientComboBox.setEnabled(false);
				patientSourceField.setEnabled(false);
			}

			patientSearchPanel.add(patientComboBox, null);
		}
		return patientSearchPanel;
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
			resetPatPresPat();
		}

		for (Patient elem : patients) {
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
	private void resetPatPresPat() {
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
				patients = patBrowser.getPatientWithHeightAndWeight(regExp);
			} catch (OHServiceException ex) {
				OHServiceExceptionUtil.showMessages(ex);
				patients = new ArrayList<Patient>();
			}
		} else {
			try {
				patients = patBrowser.getPatient();
			} catch (OHServiceException e) {
				OHServiceExceptionUtil.showMessages(e);
			}
		}
		if (patients != null) {
			for (Patient elem : patients) {
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
			GridLayout layout = new GridLayout(2, 8);

			vitalsDataPanel = new JPanel();
			vitalsDataPanel.setLayout(layout);
			vitalsDataPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.patpres.datavitals")));

			JLabel weightLabel = new JLabel(MessageBundle.getMessage("angal.patpres.vitals.weight"));
			vitalsWeightField = new JTextField();
			vitalsDataPanel.add(weightLabel, null);
			vitalsDataPanel.add(vitalsWeightField, null);
			if (patPres.getVitals() != null && patPres.getVitals().getWeight() != null)
				vitalsWeightField.setText(String.valueOf(patPres.getVitals().getWeight()));

			JLabel heightLabel = new JLabel(MessageBundle.getMessage("angal.patpres.vitals.height"));
			vitalsHeightField = new JTextField();
			vitalsDataPanel.add(heightLabel, null);
			vitalsDataPanel.add(vitalsHeightField, null);
			if (patPres.getVitals() != null && patPres.getVitals().getHeight() != null)
				vitalsHeightField.setText(String.valueOf(patPres.getVitals().getHeight()));

			JLabel bloodSugarLabel = new JLabel(MessageBundle.getMessage("angal.patpres.vitals.bloodsugar"));
			vitalsBloodSugarField = new JTextField();
			vitalsDataPanel.add(bloodSugarLabel, null);
			vitalsDataPanel.add(vitalsBloodSugarField, null);
			if (patPres.getVitals() != null && patPres.getVitals().getBloodSugar() != null)
				vitalsBloodSugarField.setText(String.valueOf(patPres.getVitals().getBloodSugar()));

			JLabel temperatureLabel = new JLabel(MessageBundle.getMessage("angal.patpres.vitals.temperature"));
			vitalsTemperatureField = new JTextField();
			vitalsDataPanel.add(temperatureLabel, null);
			vitalsDataPanel.add(vitalsTemperatureField, null);
			if (patPres.getVitals() != null && patPres.getVitals().getTemperature() != null)
				vitalsTemperatureField.setText(String.valueOf(patPres.getVitals().getTemperature()));

			JLabel bsUnitLabel = new JLabel(MessageBundle.getMessage("angal.bsunit.name"));
			vitalsBsUnitField = new JComboBox();
			vitalsBsUnitField.addItem(MessageBundle.getMessage("angal.patpres.vitals.selectunit"));
			BsUnitBrowserManager bsUnitManager = Context.getApplicationContext().getBean(BsUnitBrowserManager.class);
			try {
				for (BsUnit unit : bsUnitManager.getBsUnit()) {
					vitalsBsUnitField.addItem(unit.getCode());
				}
			} catch (OHServiceException ex) {
				OHServiceExceptionUtil.showMessages(ex);
			}
			vitalsDataPanel.add(bsUnitLabel, null);
			vitalsDataPanel.add(vitalsBsUnitField, null);
			if (patPres.getVitals() != null && StringUtils.hasText(patPres.getVitals().getBsUnit()))
				vitalsBsUnitField.setSelectedItem(patPres.getVitals().getBsUnit());

			JLabel tempUnitLabel = new JLabel(MessageBundle.getMessage("angal.tempunit.name"));
			vitalsTempUnitField = new JComboBox();
			TempUnitBrowserManager tempUnitManager = Context.getApplicationContext().getBean(TempUnitBrowserManager.class);
			vitalsTempUnitField.addItem(MessageBundle.getMessage("angal.patpres.vitals.selectunit"));
			try {
				for (TempUnit unit : tempUnitManager.getTempUnit()) {
					vitalsTempUnitField.addItem(unit.getCode());
				}
			} catch (OHServiceException ex) {
				OHServiceExceptionUtil.showMessages(ex);
			}
			vitalsDataPanel.add(tempUnitLabel, null);
			vitalsDataPanel.add(vitalsTempUnitField, null);
			if (patPres.getVitals() != null && StringUtils.hasText(patPres.getVitals().getTempUnit()))
				vitalsTempUnitField.setSelectedItem(patPres.getVitals().getTempUnit());

			JLabel systoleLabel = new JLabel(MessageBundle.getMessage("angal.patpres.vitals.systole"));
			vitalsSystoleField = new JTextField();
			vitalsDataPanel.add(systoleLabel, null);
			vitalsDataPanel.add(vitalsSystoleField, null);
			if (patPres.getVitals() != null && patPres.getVitals().getBp() != null && patPres.getVitals().getBp().getSystole() != null)
				vitalsSystoleField.setText(String.valueOf(patPres.getVitals().getBp().getSystole()));

			JLabel diastoleLabel = new JLabel(MessageBundle.getMessage("angal.patpres.vitals.diastole"));
			vitalsDiastoleField = new JTextField();
			vitalsDataPanel.add(diastoleLabel, null);
			vitalsDataPanel.add(vitalsDiastoleField, null);
			if (patPres.getVitals() != null && patPres.getVitals().getBp() != null && patPres.getVitals().getBp().getDiastole() != null)
				vitalsDiastoleField.setText(String.valueOf(patPres.getVitals().getBp().getDiastole()));
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
			patientDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			patientDataPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.patvac.datapatient")));

			JLabel nameLabel = new JLabel(MessageBundle.getMessage("angal.patvac.name"));
			patientDataPanel.add(nameLabel, null);

			patTextField = getPatientTextField();
			patTextField.setEditable(false);
			patTextField.setColumns(30);
			patientDataPanel.add(patTextField, null);

			JLabel ageLabel = new JLabel(MessageBundle.getMessage("angal.patvac.age"));
			patientDataPanel.add(ageLabel, null);

			ageTextField = getAgeTextField();
			ageTextField.setEditable(false);
			ageTextField.setColumns(3);
			patientDataPanel.add(ageTextField, null);

			JLabel sexLabel = new JLabel(MessageBundle.getMessage("angal.patvac.sex"));
			patientDataPanel.add(sexLabel, null);

			sexTextField = getSexTextField();
			sexTextField.setEditable(false);
			sexTextField.setColumns(3);
			patientDataPanel.add(sexTextField, null);
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
					// check on patient
					if (selectedPatient == null) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.pleaseselectpatient"));
						return;
					} else if (presentDateCal.getDate() == null) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.pleaseinsertpresentationdate"));
						return;
					} else if (StringUtils.isEmpty(summaryField.getText())) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.pleaseselectsummary"));
						return;
					}

					patPres.setPatient(selectedPatient);
					if (presentDateCal.getDate() != null) {
						GregorianCalendar gregDate = new GregorianCalendar();
						gregDate.setTime(presentDateCal.getDate());
						patPres.setPresentationDate(gregDate);
					} else {
						patPres.setPresentationDate(null);
					}
					if (consultDateCal.getDate() != null) {
						GregorianCalendar gregDate = new GregorianCalendar();
						gregDate.setTime(consultDateCal.getDate());
						patPres.setConsultationEnd(gregDate);
					} else {
						patPres.setConsultationEnd(null);
					}
					if (previousDateCal.getDate() != null) {
						GregorianCalendar gregDate = new GregorianCalendar();
						gregDate.setTime(previousDateCal.getDate());
						patPres.setPreviousConsult(gregDate);
					} else {
						patPres.setPreviousConsult(null);
					}
					// Vitals
					if (patPres.getVitals() == null) {
						patPres.setVitals(new Vitals());
					}
					if (StringUtils.hasText(vitalsHeightField.getText())) {
						patPres.getVitals().setHeight(Float.parseFloat(vitalsHeightField.getText()));
					} else {
						patPres.getVitals().setHeight(null);
					}
					if (StringUtils.hasText(vitalsWeightField.getText())) {
						patPres.getVitals().setWeight(Float.parseFloat(vitalsWeightField.getText()));
					} else {
						patPres.getVitals().setWeight(null);
					}
					if (StringUtils.hasText(vitalsBloodSugarField.getText())) {
						patPres.getVitals().setBloodSugar(Float.parseFloat(vitalsBloodSugarField.getText()));
					} else {
						patPres.getVitals().setBloodSugar(null);
					}
					if (StringUtils.hasText(vitalsTemperatureField.getText())) {
						patPres.getVitals().setTemperature(Float.parseFloat(vitalsTemperatureField.getText()));
					} else {
						patPres.getVitals().setTemperature(null);
					}
					if (vitalsBsUnitField.getSelectedIndex() > 0) {
						patPres.getVitals().setBsUnit((String) vitalsBsUnitField.getSelectedItem());
					} else {
						patPres.getVitals().setBsUnit(null);
					}
					if (vitalsTempUnitField.getSelectedIndex() > 0) {
						patPres.getVitals().setTempUnit((String) vitalsTempUnitField.getSelectedItem());
					} else {
						patPres.getVitals().setTempUnit(null);
					}
					// Vitals Bp
					if (patPres.getVitals().getBp() == null) {
						patPres.getVitals().setBp(new Bp());
					}
					if (StringUtils.hasText(vitalsSystoleField.getText())) {
						patPres.getVitals().getBp().setSystole(Integer.parseInt(vitalsSystoleField.getText()));
					} else {
						patPres.getVitals().getBp().setSystole(null);
					}
					if (StringUtils.hasText(vitalsDiastoleField.getText())) {
						patPres.getVitals().getBp().setDiastole(Integer.parseInt(vitalsDiastoleField.getText()));
					} else {
						patPres.getVitals().getBp().setDiastole(null);
					}
					// Remaining fields
					patPres.setReferredFrom(referredFromField.getText());
					patPres.setReferredTo(referredToField.getText());
					patPres.setPatientAilmentDescription(patientAilmentField.getText());
					patPres.setDoctorsAilmentDescription(doctorsAilmentField.getText());
					patPres.setSpecificSymptoms(specificSymptomsField.getText());
					patPres.setDiagnosis(diagnosisField.getText());
					patPres.setPrognosis(prognosisField.getText());
					patPres.setPatientAdvice(patientAdviceField.getText());
					patPres.setPrescribed(prescribedField.getText());
					patPres.setFollowUp(followUpField.getText());
					patPres.setSummary(summaryField.getText());

					boolean result;
					PatPresManager manager = Context.getApplicationContext().getBean(PatPresManager.class);
					// handling db insert/update
					if (insert) {
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
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.thedatacouldnobesaved"));
					else {
						patPres = new PatientPresentation(0, new Patient(), new Vitals() {{
							setBp(new Bp());
						}},
							new GregorianCalendar(), null, null,
							null, null,
							null, null,
							null, null, null,
							null, null, null, null
						);
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
