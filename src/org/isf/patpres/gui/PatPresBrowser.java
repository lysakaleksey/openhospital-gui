package org.isf.patpres.gui;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.Context;
import org.isf.patient.model.Patient;
import org.isf.patpres.manager.PatPresManager;
import org.isf.patpres.model.Bp;
import org.isf.patpres.model.PatientPresentation;
import org.isf.patpres.model.Vitals;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.isf.utils.jobjects.CustomJDateChooser;
import org.isf.utils.jobjects.ModalJFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PatPresBrowser extends ModalJFrame {
	private static final long serialVersionUID = 1L;
	private static final String VERSION = MessageBundle.getMessage("angal.versione");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private JPanel jContentPane = null;
	private JPanel jButtonPanel = null;
	private JButton buttonEdit = null;
	private JButton buttonNew = null;
	private JButton buttonDelete = null;
	private JButton buttonClose = null;
	private JButton filterButton = null;

	private JPanel jSelectionPanel = null;
	private JTextField jPatientIdField = null;
	private JTextField jPatientNameField = null;
	private CustomJDateChooser jDateFrom = null;
	private CustomJDateChooser jDateTo = null;
	private JTextField jReferredFromField = null;
	private JTextField jSpecificSymptomsField = null;
	private JTextField jPrescribedField = null;
	private JTextField jReferredToField = null;

	//private String sexSelect = MessageBundle.getMessage("angal.patpres.all");
	private JLabel rowCounter = null;
	private String rowCounterText = MessageBundle.getMessage("angal.patpres.count") + ": ";
	private JTable jTable = null;
	private ArrayList<PatientPresentation> lPatPres;
	private int pfrmHeight;

	private String[] pColums = {
		MessageBundle.getMessage("angal.patpres.column.presentationdate"),
		MessageBundle.getMessage("angal.patpres.column.patientcode"),
		MessageBundle.getMessage("angal.patpres.column.patientname"),
		MessageBundle.getMessage("angal.patpres.column.sexm"),
		MessageBundle.getMessage("angal.patpres.column.agem"),
		MessageBundle.getMessage("angal.patpres.column.summary")
	};
	private int[] pColumWidth = {150, 100, 300, 60, 75, 800};
	private PatPresManager manager;
	private PatPresBrowsingModel model;
	private PatientPresentation patientPresentation;
	private int selectedrow;
	private final JFrame myFrame;

	public PatPresBrowser() {
		super();
		myFrame = this;
		manager = Context.getApplicationContext().getBean(PatPresManager.class);
		initialize();
		setVisible(true);
	}

	/**
	 * This method initializes this Frame, sets the correct Dimensions
	 *
	 * @return void
	 */
	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		final int pfrmBase = 20;
		final int pfrmWidth = 17;
		final int pfrmHeight = 12;
		this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase) / 2,
			(screensize.height - screensize.height * pfrmHeight / pfrmBase) / 2,
			screensize.width * pfrmWidth / pfrmBase + 50,
			screensize.height * pfrmHeight / pfrmBase + 20);
		setTitle(MessageBundle.getMessage("angal.patpres.patientpresentationbrowsing") + " (" + VERSION + ")");
		this.setContentPane(getJContentPane());
		updateRowCounter();
		validate();
		this.setLocationRelativeTo(null);
	}

	/**
	 * This method initializes jContentPane, adds the main parts of the frame
	 *
	 * @return jContentPanel (JPanel)
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJSelectionPanel(), java.awt.BorderLayout.WEST);
			jContentPane.add(new JScrollPane(getJTable()), java.awt.BorderLayout.CENTER);
			updateRowCounter();
		}
		return jContentPane;
	}


	/**
	 * This method initializes JButtonPanel, that contains the buttons of the
	 * frame (on the bottom)
	 *
	 * @return JButtonPanel (JPanel)
	 */
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			if (MainMenu.checkUserGrants("btnpatientvaccinenew")) jButtonPanel.add(getButtonNew(), null);
			if (MainMenu.checkUserGrants("btnpatientvaccineedit")) jButtonPanel.add(getButtonEdit(), null);
			if (MainMenu.checkUserGrants("btnpatientvaccinedel")) jButtonPanel.add(getButtonDelete(), null);
			jButtonPanel.add((getCloseButton()), null);
		}
		return jButtonPanel;
	}

	/**
	 * This method initializes buttonNew, that loads patientPresentationEdit Mask
	 *
	 * @return buttonNew (JButton)
	 */
	private JButton getButtonNew() {
		if (buttonNew == null) {

			final ActionListener callback = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PatientPresentation last = new PatientPresentation(0, new Patient() {{ setCode(0);}}, new Vitals() {{ setBp(new Bp());}},
						null, null, null,
						null, null,
						null, null,
						null, null, null,
						null, null, null, null
					);

					if (!last.equals(patientPresentation)) {
						lPatPres.add(0, patientPresentation);
						((PatPresBrowser.PatPresBrowsingModel) jTable.getModel()).fireTableDataChanged();
						updateRowCounter();
						if (jTable.getRowCount() > 0)
							jTable.setRowSelectionInterval(0, 0);
					}
				}
			};

			buttonNew = new JButton(MessageBundle.getMessage("angal.common.new"));
			buttonNew.setMnemonic(KeyEvent.VK_N);
			buttonNew.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					patientPresentation = new PatientPresentation(0, new Patient() {{ setCode(0);}}, new Vitals() {{ setBp(new Bp());}},
						null, null, null,
						null, null,
						null, null,
						null, null, null,
						null, null, null, null
					);
					new PatPresEdit(patientPresentation, true, callback).showAsModal(myFrame);
				}
			});
		}
		return buttonNew;
	}

	/**
	 * This method initializes buttonEdit, that loads patientPresentationEdit Mask
	 *
	 * @return buttonEdit (JButton)
	 */
	private JButton getButtonEdit() {
		if (buttonEdit == null) {

			buttonEdit = new JButton(MessageBundle.getMessage("angal.common.edit"));
			buttonEdit.setMnemonic(KeyEvent.VK_S);
			buttonEdit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
							MessageBundle.getMessage("angal.common.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
							JOptionPane.PLAIN_MESSAGE);
						return;
					}

					selectedrow = jTable.getSelectedRow();
					patientPresentation = (PatientPresentation)model.getValueAt(selectedrow, -1);

					final PatientPresentation last = new PatientPresentation(patientPresentation.getCode(), patientPresentation.getPatient(), patientPresentation.getVitals(),
						patientPresentation.getPresentationDate(), patientPresentation.getConsultationEnd(), patientPresentation.getPresentationDate(),
						patientPresentation.getReferredFrom(), patientPresentation.getPatientAilmentDescription(), patientPresentation.getDoctorsAilmentDescription(),
						patientPresentation.getSpecificSymptoms(), patientPresentation.getDiagnosis(), patientPresentation.getPrognosis(), patientPresentation.getPatientAdvice(),
						patientPresentation.getPrescribed(), patientPresentation.getFollowUp(), patientPresentation.getReferredTo(), patientPresentation.getSummary());

					final ActionListener callback = new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (!last.equals(patientPresentation)) {
								((PatPresBrowser.PatPresBrowsingModel) jTable.getModel()).fireTableDataChanged();
								updateRowCounter();
								if ((jTable.getRowCount() > 0) && selectedrow > -1)
									jTable.setRowSelectionInterval(selectedrow, selectedrow);
							}
						}
					};

					new PatPresEdit(patientPresentation, false, callback).setVisible(true);
				}
			});
		}
		return buttonEdit;
	}


	/**
	 * This method initializes buttonDelete, that loads patientPresentationEdit Mask
	 *
	 * @return buttonDelete (JButton)
	 */
	private JButton getButtonDelete() {
		if (buttonDelete == null) {
			buttonDelete = new JButton(MessageBundle.getMessage("angal.common.delete"));
			buttonDelete.setMnemonic(KeyEvent.VK_D);
			buttonDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
							MessageBundle.getMessage("angal.common.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
							JOptionPane.PLAIN_MESSAGE);
						return;
					}
					selectedrow = jTable.getSelectedRow();
					patientPresentation = (PatientPresentation) model.getValueAt(selectedrow, -1);
					String message = MessageBundle.getMessage("angal.patpres.deleteselectedpatpresrow");
					int n = JOptionPane.showConfirmDialog(null, message, MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);

					if (n == JOptionPane.YES_OPTION) {
						boolean deleted;
						try {
							deleted = manager.deletePatientPresentation(patientPresentation);
						} catch (OHServiceException e) {
							deleted = false;
							OHServiceExceptionUtil.showMessages(e);
						}
						if (deleted) {
							lPatPres.remove(jTable.getSelectedRow());
							model.fireTableDataChanged();
							jTable.updateUI();
						}
					}
				}
			});
		}
		return buttonDelete;
	}

	/**
	 * This method initializes buttonClose
	 *
	 * @return buttonClose (JButton)
	 */
	private JButton getCloseButton() {
		if (buttonClose == null) {
			buttonClose = new JButton(MessageBundle.getMessage("angal.common.close"));
			buttonClose.setMnemonic(KeyEvent.VK_C);
			buttonClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		return buttonClose;
	}


	/**
	 * This method initializes JSelectionPanel, that contains the filter objects
	 *
	 * @return JSelectionPanel (JPanel)
	 */
	private JPanel getJSelectionPanel() {
		if (jSelectionPanel == null) {
			jSelectionPanel = new JPanel();
			jSelectionPanel.setPreferredSize(new Dimension(220, pfrmHeight));
			jSelectionPanel.setLayout(new BoxLayout(jSelectionPanel, BoxLayout.Y_AXIS));

			jSelectionPanel.add(getPatientIdPanel());
			jSelectionPanel.add(getPatientNamePanel());
			jSelectionPanel.add(getDatePanel());
			jSelectionPanel.add(getReferredFromPanel());
			jSelectionPanel.add(getSpecificSymptomsPanel());
			jSelectionPanel.add(getPrescribedPanel());
			jSelectionPanel.add(getReferredToPanel());

			jSelectionPanel.add(getFilterPanel());
			jSelectionPanel.add(getRowCounterPanel());
		}
		return jSelectionPanel;
	}

	private JPanel getReferredToPanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel(MessageBundle.getMessage("angal.patpres.referredto")));
		filterPanel.add(label1Panel);

		if (jReferredToField == null) {
			jReferredToField = new JTextField(15);
		}

		JPanel label2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label2Panel.add(jReferredToField);
		filterPanel.add(label2Panel, null);

		return filterPanel;
	}

	private JPanel getPrescribedPanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel(MessageBundle.getMessage("angal.patpres.prescribed")));
		filterPanel.add(label1Panel);

		if (jPrescribedField == null) {
			jPrescribedField = new JTextField(15);
		}

		JPanel label2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label2Panel.add(jPrescribedField);
		filterPanel.add(label2Panel, null);

		return filterPanel;
	}

	private JPanel getSpecificSymptomsPanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel(MessageBundle.getMessage("angal.patpres.symptoms")));
		filterPanel.add(label1Panel);

		if (jSpecificSymptomsField == null) {
			jSpecificSymptomsField = new JTextField(15);
		}

		JPanel label2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label2Panel.add(jSpecificSymptomsField);
		filterPanel.add(label2Panel, null);

		return filterPanel;
	}

	private JPanel getReferredFromPanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel(MessageBundle.getMessage("angal.patpres.referredfrom")));
		filterPanel.add(label1Panel);

		if (jReferredFromField == null) {
			jReferredFromField = new JTextField(15);
		}

		JPanel label2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label2Panel.add(jReferredFromField);
		filterPanel.add(label2Panel, null);

		return filterPanel;
	}

	private JPanel getPatientIdPanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel(MessageBundle.getMessage("angal.patpres.patientcode")));
		filterPanel.add(label1Panel);

		if (jPatientIdField == null) {
			jPatientIdField = new JTextField(15);
		}

		JPanel label2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label2Panel.add(jPatientIdField);
		filterPanel.add(label2Panel, null);

		return filterPanel;
	}

	/**
	 * This method initializes getVaccineTypePanel
	 *
	 * @return vaccineTypePanel  (JPanel)
	 */
	private JPanel getPatientNamePanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel(MessageBundle.getMessage("angal.patpres.patientname")));
		filterPanel.add(label1Panel);

		if (jPatientNameField == null) {
			jPatientNameField = new JTextField(15);
		}

		JPanel label2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label2Panel.add(jPatientNameField);
		filterPanel.add(label2Panel, null);

		return filterPanel;
	}

	/**
	 * This method initializes getDatePanel
	 *
	 * @return datePanel  (JPanel)
	 */
	private JPanel getDatePanel() {

		JPanel datePanel = new JPanel();

		datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));

		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel(MessageBundle.getMessage("angal.common.date") + ": " + MessageBundle.getMessage("angal.common.from")), null);
		datePanel.add(label1Panel);

		label1Panel.add(getDateFromPanel());
		datePanel.add(label1Panel, null);

		label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel(MessageBundle.getMessage("angal.common.date") + ": " + MessageBundle.getMessage("angal.common.to") + "     "), null);
		datePanel.add(label1Panel);

		label1Panel.add(getDateToPanel());
		datePanel.add(label1Panel, null);

		return datePanel;
	}

	/**
	 * This method initializes getFilterPanel
	 *
	 * @return filterPanel  (JPanel)
	 */
	private JPanel getFilterPanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(getSearchButton());
		filterPanel.add(label1Panel);
		return filterPanel;
	}

	/**
	 * This method initializes getRowCounterPanel
	 *
	 * @return rowCounterPanel  (JPanel)
	 */
	private JPanel getRowCounterPanel() {

		JPanel rowCounterPanel = new JPanel();

		rowCounterPanel.setLayout(new BoxLayout(rowCounterPanel, BoxLayout.Y_AXIS));
		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		rowCounter = new JLabel(MessageBundle.getMessage("angal.patpres.rowcounter"));
		label1Panel.add(rowCounter, null);
		rowCounterPanel.add(label1Panel);
		return rowCounterPanel;
	}

	/**
	 * This method initializes dateFrom, which is the Panel that contains the
	 * date (From) input for the filtering
	 *
	 * @return dateFrom (JPanel)
	 */

	private CustomJDateChooser getDateFromPanel() {
		if (jDateFrom == null) {
			GregorianCalendar now = new GregorianCalendar();
			if (!GeneralData.ENHANCEDSEARCH) now.add(GregorianCalendar.WEEK_OF_YEAR, -1);
			java.util.Date myDate = now.getTime();
			jDateFrom = new CustomJDateChooser(myDate, "dd/MM/yy");
			jDateFrom.setDate(myDate);
			jDateFrom.setLocale(new Locale(GeneralData.LANGUAGE));
			jDateFrom.setDateFormatString("dd/MM/yy");
		}
		return jDateFrom;
	}

	/**
	 * This method initializes dateTo, which is the Panel that contains the
	 * date (To) input for the filtering
	 *
	 * @return dateFrom (JPanel)
	 */
	private CustomJDateChooser getDateToPanel() {
		if (jDateTo == null) {
			GregorianCalendar now = new GregorianCalendar();
			java.util.Date myDate = now.getTime();
			jDateTo = new CustomJDateChooser(myDate, "dd/MM/yy");
			jDateTo.setLocale(new Locale(GeneralData.LANGUAGE));
			jDateTo.setDateFormatString("dd/MM/yy");
			jDateTo.setDate(myDate);
		}
		return jDateTo;
	}

	/**
	 * This method initializes filterButton, which is the button that perform
	 * the filtering and calls the methods to refresh the Table
	 *
	 * @return filterButton (JButton)
	 */
	private JButton getSearchButton() {
		if (filterButton == null) {
			filterButton = new JButton(MessageBundle.getMessage("angal.patpres.search"));
			filterButton.setMnemonic(KeyEvent.VK_S);
			filterButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int patId = -1;
					try {
						patId = Integer.parseInt(jPatientIdField.getText());
					} catch (Exception ex) {
						patId = -1;
					}
					String patientName = jPatientNameField.getText();

					if (jDateFrom.getDate() == null) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.pleaseinsertvaliddatefrom"));
						return;
					}

					if (jDateTo.getDate() == null) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.patpres.pleaseinsertvaliddateto"));
						return;
					}

					GregorianCalendar gcFrom = new GregorianCalendar();
					gcFrom.setTime(jDateFrom.getDate());
					GregorianCalendar gcTo = new GregorianCalendar();
					gcTo.setTime(jDateTo.getDate());

					model = new PatPresBrowser.PatPresBrowsingModel(patId, patientName, gcFrom, gcTo,
						jReferredFromField.getText(), jSpecificSymptomsField.getText(), jPrescribedField.getText(), jReferredToField.getText());
					model.fireTableDataChanged();
					jTable.updateUI();
					updateRowCounter();
				}
			});
		}
		return filterButton;
	}


	/**
	 * This method initializes jTable, that contains the information about the
	 * patient's vaccines
	 *
	 * @return jTable (JTable)
	 */
	private JTable getJTable() {
		if (jTable == null) {
			model = new PatPresBrowser.PatPresBrowsingModel();
			jTable = new JTable(model);
			TableColumnModel columnModel = jTable.getColumnModel();
			columnModel.getColumn(0).setMinWidth(pColumWidth[0]);
			columnModel.getColumn(1).setMinWidth(pColumWidth[1]);
			columnModel.getColumn(2).setMinWidth(pColumWidth[2]);
			columnModel.getColumn(3).setMinWidth(pColumWidth[3]);
			columnModel.getColumn(4).setMinWidth(pColumWidth[4]);
			columnModel.getColumn(5).setMinWidth(pColumWidth[5]);
		}
		return jTable;
	}


	/**
	 * This class defines the model for the Table
	 */
	class PatPresBrowsingModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		private final PatPresManager manager = Context.getApplicationContext().getBean(PatPresManager.class);

		public PatPresBrowsingModel() {
			PatPresManager manager = Context.getApplicationContext().getBean(PatPresManager.class);
			try {
				lPatPres = manager.getPatientPresentation(!GeneralData.ENHANCEDSEARCH);
			} catch (OHServiceException e) {
				lPatPres = null;
				OHServiceExceptionUtil.showMessages(e);
			}
		}

		public PatPresBrowsingModel(int patId, String patName, GregorianCalendar dateFrom, GregorianCalendar dateTo,
									String referredFrom, String specificSymptoms, String prescribed, String referredTo) {
			try {
				lPatPres = manager.getPatientPresentation(patId, patName, dateFrom, dateTo, referredFrom, specificSymptoms, prescribed, referredTo);
			} catch (OHServiceException e) {
				lPatPres = null;
				OHServiceExceptionUtil.showMessages(e);
			}
		}

		public int getRowCount() {
			if (lPatPres == null)
				return 0;
			return lPatPres.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		/**
		 * Note: We must get the objects in a reversed way because of the query
		 *
		 * @see org.isf.patpres.service.PatPresIoOperations
		 */
		public Object getValueAt(int r, int c) {
			PatientPresentation patPres = lPatPres.get(r);
			if (c == -1) {
				return patPres;
			} else if (c == 0) {
				return dateFormat.format(patPres.getPresentationDate().getTime());
			} else if (c == 1) {
				return patPres.getPatient().getCode();
			} else if (c == 2) {
				return patPres.getPatient().getName();
			} else if (c == 3) {
				return patPres.getPatient().getSex();
			} else if (c == 4) {
				return patPres.getPatient().getAge();
			} else if (c == 5) {
				return patPres.getSummary();
			}
			return null;
		}


		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

	}//PatPresBrowsingModel

//	public void actionPerformed(ActionEvent e) {
//		sexSelect = e.getActionCommand();
//	}

	private void updateRowCounter() {
		rowCounter.setText(rowCounterText + lPatPres.size());
	}
}

