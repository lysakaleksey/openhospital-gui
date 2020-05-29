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
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.VoLimitedTextArea;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PatPresEdit extends ModalJFrame {
	private static final long serialVersionUID = -4271389493861772055L;
	private static final String VERSION = MessageBundle.getMessage("angal.versione");
	private boolean insert = false;

	private PatientPresentation patPres = null;
	private JPanel contentPanel = null;
	private JPanel mainDataPanel = null;
	private JPanel vitalsDataPanel = null;
	private JPanel buttonPanel = null;

	private GregorianCalendar presentDateIn = null;
	private CustomJDateChooser presentDateCal = null;
	private CustomJDateChooser consultDateCal = null;
	private CustomJDateChooser previousDateCal = null;

	private VoLimitedTextField referredFromField;
	private VoLimitedTextArea patientAilmentField;
	private VoLimitedTextArea doctorsAilmentField;
	private VoLimitedTextArea specificSymptomsField;
	private VoLimitedTextArea diagnosisField;
	private VoLimitedTextArea prognosisField;
	private VoLimitedTextArea patientAdviceField;
	private VoLimitedTextArea prescribedField;
	private VoLimitedTextArea followUpField;
	private VoLimitedTextField referredToField;
	private VoLimitedTextArea summaryField;
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

	private ActionListener callback;
	private ArrayList<Patient> patients = null;
	private Patient selectedPatient = null;
	private String lastKey;
	private String s;

	public PatPresEdit(PatientPresentation patPresIn, boolean action, ActionListener callback) {
		super();
		insert = action;
		patPres = patPresIn;
		selectedPatient = patPresIn.getPatient();
		this.callback = callback;
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
		this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase) / 2, 0, screensize.width * pfrmWidth / pfrmBase + 50, screensize.height);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(getJContentPane());
		setContentPane(scrollPane);
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.patpres.newpatientpresentation") + "(" + VERSION + ")");
		} else {
			this.setTitle(MessageBundle.getMessage("angal.patpres.editpatientpresentation") + "(" + VERSION + ")");
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		validate();
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
			contentPanel.add(getMainDataPanel(), null);
			contentPanel.add(getVitalsDataPanel(), null);
			contentPanel.add(getButtonPanel(), null);
			contentPanel.add(new JPanel());
			contentPanel.add(new JPanel());
			contentPanel.add(new JPanel());
			contentPanel.add(new JPanel());
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
			referredFromField = new VoLimitedTextField(100);
			referredFromField.setColumns(100);
			referredFromField.setMaximumSize(referredFromField.getPreferredSize());
			referredFromField.setText(patPres.getReferredFrom());
			mainDataPanel.add(referredFromLabel, null);
			mainDataPanel.add(referredFromField, null);

			// Referred to
			JLabel referredToLabel = new JLabel(MessageBundle.getMessage("angal.patpres.referredto"));
			referredToField = new VoLimitedTextField(100);
			referredToField.setColumns(100);
			referredToField.setMaximumSize(referredToField.getPreferredSize());
			referredToField.setText(patPres.getReferredTo());
			mainDataPanel.add(referredToLabel, null);
			mainDataPanel.add(referredToField, null);

			// Patient ailment
			JLabel patientAilmentLabel = new JLabel(MessageBundle.getMessage("angal.patpres.patientailment"));
			patientAilmentField = new VoLimitedTextArea(65535, 5, 0);
			patientAilmentField.setText(patPres.getPatientAilmentDescription());
			mainDataPanel.add(patientAilmentLabel, null);
			mainDataPanel.add(new JScrollPane(patientAilmentField), null);

			// Doctors ailment
			JLabel doctorsAilmentLabel = new JLabel(MessageBundle.getMessage("angal.patpres.doctorsailment"));
			doctorsAilmentField = new VoLimitedTextArea(65535, 5, 0);
			doctorsAilmentField.setText(patPres.getDoctorsAilmentDescription());
			mainDataPanel.add(doctorsAilmentLabel, null);
			mainDataPanel.add(new JScrollPane(doctorsAilmentField), null);

			// Specific symptoms
			JLabel symptomsLabel = new JLabel(MessageBundle.getMessage("angal.patpres.symptoms"));
			specificSymptomsField = new VoLimitedTextArea(65535, 5, 0);
			specificSymptomsField.setText(patPres.getSpecificSymptoms());
			mainDataPanel.add(symptomsLabel, null);
			mainDataPanel.add(new JScrollPane(specificSymptomsField), null);

			// Diagnosis
			JLabel diagnosisLabel = new JLabel(MessageBundle.getMessage("angal.patpres.diagnosis"));
			diagnosisField = new VoLimitedTextArea(65535, 5, 0);
			diagnosisField.setText(patPres.getDiagnosis());
			mainDataPanel.add(diagnosisLabel, null);
			mainDataPanel.add(new JScrollPane(diagnosisField), null);

			// Prognosis
			JLabel prognosisLabel = new JLabel(MessageBundle.getMessage("angal.patpres.prognosis"));
			prognosisField = new VoLimitedTextArea(65535, 5, 0);
			prognosisField.setText(patPres.getPrognosis());
			mainDataPanel.add(prognosisLabel, null);
			mainDataPanel.add(new JScrollPane(prognosisField), null);

			// Patient advice
			JLabel patientAdviceLabel = new JLabel(MessageBundle.getMessage("angal.patpres.patientadvice"));
			patientAdviceField = new VoLimitedTextArea(65535, 5, 0);
			patientAdviceField.setText(patPres.getPatientAdvice());
			mainDataPanel.add(patientAdviceLabel, null);
			mainDataPanel.add(new JScrollPane(patientAdviceField), null);

			// Prescribed
			JLabel prescribedAdviceLabel = new JLabel(MessageBundle.getMessage("angal.patpres.prescribed"));
			prescribedField = new VoLimitedTextArea(65535, 5, 0);
			prescribedField.setText(patPres.getPrescribed());
			mainDataPanel.add(prescribedAdviceLabel, null);
			mainDataPanel.add(new JScrollPane(prescribedField), null);

			// Follow Up
			JLabel followupLabel = new JLabel(MessageBundle.getMessage("angal.patpres.followup"));
			followUpField = new VoLimitedTextArea(65535, 5, 0);
			followUpField.setText(patPres.getFollowUp());
			mainDataPanel.add(followupLabel, null);
			mainDataPanel.add(new JScrollPane(followUpField), null);

			// Summary
			JLabel summaryLabel = new JLabel(MessageBundle.getMessage("angal.patpres.summary"));
			summaryField = new VoLimitedTextArea(1000, 3, 0);
			summaryField.setText(patPres.getSummary());
			mainDataPanel.add(summaryLabel, null);
			mainDataPanel.add(new JScrollPane(summaryField), null);
		}

		//mainDataPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
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
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		// Patient search panel
		JPanel patientSearchPanel = new JPanel();
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
		patientComboBox.setPreferredSize(new Dimension(300, 20));
		patientComboBox.setMinimumSize(new Dimension(patientComboBox.getPreferredSize().width, patientComboBox.getPreferredSize().height));
		patientComboBox.setMaximumSize(new Dimension(patientComboBox.getPreferredSize().width, patientComboBox.getPreferredSize().height));
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
		jPanel.add(patientSearchPanel, null);

		// Patient data
		JPanel patientDataPanel = new JPanel();
		patientDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		patientDataPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), MessageBundle.getMessage("angal.patpres.datapatient")));

		JLabel nameLabel = new JLabel(MessageBundle.getMessage("angal.patpres.name"));
		patientDataPanel.add(nameLabel, null);

		patTextField = getPatientTextField();
		patTextField.setEditable(false);
		patTextField.setColumns(30);
		patientDataPanel.add(patTextField, null);

		JLabel ageLabel = new JLabel(MessageBundle.getMessage("angal.patpres.age"));
		patientDataPanel.add(ageLabel, null);

		ageTextField = getAgeTextField();
		ageTextField.setEditable(false);
		ageTextField.setColumns(3);
		patientDataPanel.add(ageTextField, null);

		JLabel sexLabel = new JLabel(MessageBundle.getMessage("angal.patpres.sex"));
		patientDataPanel.add(sexLabel, null);

		sexTextField = getSexTextField();
		sexTextField.setEditable(false);
		sexTextField.setColumns(3);
		patientDataPanel.add(sexTextField, null);

		jPanel.add(patientDataPanel, null);
		return jPanel;
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
			presentDateIn = new GregorianCalendar();
			presentDateIn.setTime(patPres.getPresentationDate());
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
			myDate = patPres.getConsultationEnd();
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
			myDate = patPres.getPreviousConsult();
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
			patientComboBox.addItem(MessageBundle.getMessage("angal.patpres.searchpatient"));
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
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
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
						patPres.setPresentationDate(presentDateCal.getDate());
					} else {
						patPres.setPresentationDate(null);
					}
					if (consultDateCal.getDate() != null) {
						patPres.setConsultationEnd(consultDateCal.getDate());
					} else {
						patPres.setConsultationEnd(null);
					}
					if (previousDateCal.getDate() != null) {
						patPres.setPreviousConsult(previousDateCal.getDate());
					} else {
						patPres.setPreviousConsult(null);
					}
					// Vitals
					if (patPres.getVitals() == null) {
						patPres.setVitals(new Vitals());
					}
					if (StringUtils.hasText(vitalsWeightField.getText())) {
						float value;
						try {
							value = Float.parseFloat(vitalsWeightField.getText());
						} catch (Exception ignore) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.invalidvalue"));
							vitalsWeightField.setText(null);
							vitalsWeightField.grabFocus();
							return;
						}
						patPres.getVitals().setWeight(value);
					} else {
						patPres.getVitals().setWeight(null);
					}
					if (StringUtils.hasText(vitalsHeightField.getText())) {
						float value;
						try {
							value = Float.parseFloat(vitalsHeightField.getText());
						} catch (Exception ignore) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.invalidvalue"));
							vitalsHeightField.setText(null);
							vitalsHeightField.grabFocus();
							return;
						}
						patPres.getVitals().setHeight(value);
					} else {
						patPres.getVitals().setHeight(null);
					}
					if (StringUtils.hasText(vitalsBloodSugarField.getText())) {
						float value;
						try {
							value = Float.parseFloat(vitalsBloodSugarField.getText());
						} catch (Exception ignore) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.invalidvalue"));
							vitalsBloodSugarField.setText(null);
							vitalsBloodSugarField.grabFocus();
							return;
						}
						patPres.getVitals().setBloodSugar(value);
					} else {
						patPres.getVitals().setBloodSugar(null);
					}
					if (StringUtils.hasText(vitalsTemperatureField.getText())) {
						float value;
						try {
							value = Float.parseFloat(vitalsTemperatureField.getText());
						} catch (Exception ignore) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.invalidvalue"));
							vitalsTemperatureField.setText(null);
							vitalsTemperatureField.grabFocus();
							return;
						}
						patPres.getVitals().setTemperature(value);
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
						int value;
						try {
							value = Integer.parseInt(vitalsSystoleField.getText());
						} catch (Exception ignore) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.invalidvalue"));
							vitalsSystoleField.setText(null);
							vitalsSystoleField.grabFocus();
							return;
						}
						patPres.getVitals().getBp().setSystole(value);
					} else {
						patPres.getVitals().getBp().setSystole(null);
					}
					if (StringUtils.hasText(vitalsDiastoleField.getText())) {
						int value;
						try {
							value = Integer.parseInt(vitalsDiastoleField.getText());
						} catch (Exception ignore) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.invalidvalue"));
							vitalsDiastoleField.setText(null);
							vitalsDiastoleField.grabFocus();
							return;
						}
						patPres.getVitals().getBp().setDiastole(value);
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
					if (callback != null)
						callback.actionPerformed(null);

					if (!result)
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.thedatacouldnobesaved"));
					else {
						patPres = new PatientPresentation(0, new Patient() {{ setCode(0);}}, new Vitals() {{
							setBp(new Bp());
						}},
							new Date(), null, null,
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
