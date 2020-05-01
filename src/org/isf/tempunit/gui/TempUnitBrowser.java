package org.isf.tempunit.gui;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.tempunit.gui.TempUnitBrowserEdit.TempUnitListener;
import org.isf.tempunit.manager.TempUnitBrowserManager;
import org.isf.tempunit.model.TempUnit;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.jobjects.ModalJFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Browsing of table TempUnit
 *
 * @author Oleksiy Lysak
 */

public class TempUnitBrowser extends ModalJFrame implements TempUnitListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<TempUnit> pTempUnit;
	private String[] pColums = {
		MessageBundle.getMessage("angal.common.codem"),
		MessageBundle.getMessage("angal.common.descriptionm")
	};
	private int[] pColumwidth = {80, 200};
	private JPanel jContainPanel = null;
	private JPanel jButtonPanel = null;
	private JButton jNewButton = null;
	private JButton jEditButton = null;
	private JButton jCloseButton = null;
	private JButton jDeteleButton = null;
	private JTable jTable = null;
	private TempUnitBrowserModel model;
	private int selectedrow;
	private TempUnitBrowserManager manager = Context.getApplicationContext().getBean(TempUnitBrowserManager.class);
	private TempUnit tempUnit = null;
	private final JFrame myFrame;


	/**
	 * This method initializes
	 */
	public TempUnitBrowser() {
		super();
		myFrame = this;
		initialize();
		setVisible(true);
	}


	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		final int pfrmBase = 10;
		final int pfrmWidth = 5;
		final int pfrmHeight = 4;
		this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase) / 2, (screensize.height - screensize.height * pfrmHeight / pfrmBase) / 2,
			screensize.width * pfrmWidth / pfrmBase, screensize.height * pfrmHeight / pfrmBase);
		this.setTitle(MessageBundle.getMessage("angal.tempunit.tempunitbrowsing"));
		this.setContentPane(getJContainPanel());
		//pack();	
	}


	private JPanel getJContainPanel() {
		if (jContainPanel == null) {
			jContainPanel = new JPanel();
			jContainPanel.setLayout(new BorderLayout());
			jContainPanel.add(getJButtonPanel(), BorderLayout.SOUTH);
			jContainPanel.add(new JScrollPane(getJTable()),
				BorderLayout.CENTER);
			validate();
		}
		return jContainPanel;
	}

	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.add(getJNewButton(), null);
			jButtonPanel.add(getJEditButton(), null);
			jButtonPanel.add(getJDeteleButton(), null);
			jButtonPanel.add(getJCloseButton(), null);
		}
		return jButtonPanel;
	}


	private JButton getJNewButton() {
		if (jNewButton == null) {
			jNewButton = new JButton();
			jNewButton.setText(MessageBundle.getMessage("angal.common.new"));
			jNewButton.setMnemonic(KeyEvent.VK_N);
			jNewButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					tempUnit = new TempUnit("", "");
					TempUnitBrowserEdit newrecord = new TempUnitBrowserEdit(myFrame, tempUnit, true);
					newrecord.addTempUnitListener(TempUnitBrowser.this);
					newrecord.setVisible(true);
				}
			});
		}
		return jNewButton;
	}

	/**
	 * This method initializes jEditButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJEditButton() {
		if (jEditButton == null) {
			jEditButton = new JButton();
			jEditButton.setText(MessageBundle.getMessage("angal.common.edit"));
			jEditButton.setMnemonic(KeyEvent.VK_E);
			jEditButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
							MessageBundle.getMessage("angal.common.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
							JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						selectedrow = jTable.getSelectedRow();
						tempUnit = (TempUnit) (((TempUnitBrowserModel) model)
							.getValueAt(selectedrow, -1));
						TempUnitBrowserEdit newrecord = new TempUnitBrowserEdit(myFrame, tempUnit, false);
						newrecord.addTempUnitListener(TempUnitBrowser.this);
						newrecord.setVisible(true);
					}
				}
			});
		}
		return jEditButton;
	}

	/**
	 * This method initializes jCloseButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton.setText(MessageBundle.getMessage("angal.common.close"));
			jCloseButton.setMnemonic(KeyEvent.VK_C);
			jCloseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
		}
		return jCloseButton;
	}

	/**
	 * This method initializes jDeteleButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJDeteleButton() {
		if (jDeteleButton == null) {
			jDeteleButton = new JButton();
			jDeteleButton.setText(MessageBundle.getMessage("angal.common.delete"));
			jDeteleButton.setMnemonic(KeyEvent.VK_D);
			jDeteleButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
							MessageBundle.getMessage("angal.common.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
							JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						TempUnit dis = (TempUnit) (((TempUnitBrowserModel) model)
							.getValueAt(jTable.getSelectedRow(), -1));
						int n = JOptionPane.showConfirmDialog(null,
							MessageBundle.getMessage("angal.tempunit.deletetempunit") + " \" " + dis.getDescription() + "\" ?",
							MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);
						try {
							if ((n == JOptionPane.YES_OPTION)
								&& (manager.deleteTempUnit(dis))) {
								pTempUnit.remove(jTable.getSelectedRow());
								model.fireTableDataChanged();
								jTable.updateUI();
							}
						} catch (OHServiceException ex) {
							if (ex.getMessages() != null) {
								for (OHExceptionMessage msg : ex.getMessages()) {
									JOptionPane.showMessageDialog(null, msg.getMessage(), msg.getTitle() == null ? "" : msg.getTitle(), msg.getLevel().getSwingSeverity());
								}
							}
						}
					}
				}

			});
		}
		return jDeteleButton;
	}

	public JTable getJTable() {
		if (jTable == null) {
			model = new TempUnitBrowserModel();
			jTable = new JTable(model);
			jTable.getColumnModel().getColumn(0).setMinWidth(pColumwidth[0]);
			jTable.getColumnModel().getColumn(1).setMinWidth(pColumwidth[1]);
		}
		return jTable;
	}


	class TempUnitBrowserModel extends DefaultTableModel {


		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private TempUnitBrowserManager manager = Context.getApplicationContext().getBean(TempUnitBrowserManager.class);

		public TempUnitBrowserModel() {
			try {
				pTempUnit = manager.getTempUnit();
			} catch (OHServiceException e) {
				if (e.getMessages() != null) {
					for (OHExceptionMessage msg : e.getMessages()) {
						JOptionPane.showMessageDialog(null, msg.getMessage(), msg.getTitle() == null ? "" : msg.getTitle(), msg.getLevel().getSwingSeverity());
					}
				}
			}
		}

		public int getRowCount() {
			if (pTempUnit == null)
				return 0;
			return pTempUnit.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == 0) {
				return pTempUnit.get(r).getCode();
			} else if (c == -1) {
				return pTempUnit.get(r);
			} else if (c == 1) {
				return pTempUnit.get(r).getDescription();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			//return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

	public void tempUnitUpdated(AWTEvent e) {
		pTempUnit.set(selectedrow, tempUnit);
		//System.out.println("line -> " + selectedrow);
		((TempUnitBrowserModel) jTable.getModel()).fireTableDataChanged();
		jTable.updateUI();
		if ((jTable.getRowCount() > 0) && selectedrow > -1)
			jTable.setRowSelectionInterval(selectedrow, selectedrow);
	}

	public void tempUnitInserted(AWTEvent e) {
		pTempUnit.add(0, tempUnit);
		((TempUnitBrowserModel) jTable.getModel()).fireTableDataChanged();
		if (jTable.getRowCount() > 0)
			jTable.setRowSelectionInterval(0, 0);
	}

}
