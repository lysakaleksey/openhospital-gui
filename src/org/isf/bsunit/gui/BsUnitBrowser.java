package org.isf.bsunit.gui;

import org.isf.bsunit.gui.BsUnitBrowserEdit.BsUnitListener;
import org.isf.bsunit.manager.BsUnitBrowserManager;
import org.isf.bsunit.model.BsUnit;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
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
 * Browsing of table BsUnit
 *
 * @author Oleksiy Lysak
 */

public class BsUnitBrowser extends ModalJFrame implements BsUnitListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<BsUnit> pBsUnit;
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
	private BsUnitBrowserModel model;
	private int selectedrow;
	private BsUnitBrowserManager manager = Context.getApplicationContext().getBean(BsUnitBrowserManager.class);
	private BsUnit bsUnit = null;
	private final JFrame myFrame;


	/**
	 * This method initializes
	 */
	public BsUnitBrowser() {
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
		this.setTitle(MessageBundle.getMessage("angal.bsunit.bsunitbrowsing"));
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
					bsUnit = new BsUnit("", "");
					BsUnitBrowserEdit newrecord = new BsUnitBrowserEdit(myFrame, bsUnit, true);
					newrecord.addBsUnitListener(BsUnitBrowser.this);
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
						bsUnit = (BsUnit) (((BsUnitBrowserModel) model)
							.getValueAt(selectedrow, -1));
						BsUnitBrowserEdit newrecord = new BsUnitBrowserEdit(myFrame, bsUnit, false);
						newrecord.addBsUnitListener(BsUnitBrowser.this);
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
						BsUnit dis = (BsUnit) (((BsUnitBrowserModel) model)
							.getValueAt(jTable.getSelectedRow(), -1));
						int n = JOptionPane.showConfirmDialog(null,
							MessageBundle.getMessage("angal.bsunit.deletebsunit") + " \" " + dis.getDescription() + "\" ?",
							MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);
						try {
							if ((n == JOptionPane.YES_OPTION)
								&& (manager.deleteBsUnit(dis))) {
								pBsUnit.remove(jTable.getSelectedRow());
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
			model = new BsUnitBrowserModel();
			jTable = new JTable(model);
			jTable.getColumnModel().getColumn(0).setMinWidth(pColumwidth[0]);
			jTable.getColumnModel().getColumn(1).setMinWidth(pColumwidth[1]);
		}
		return jTable;
	}


	class BsUnitBrowserModel extends DefaultTableModel {


		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private BsUnitBrowserManager manager = Context.getApplicationContext().getBean(BsUnitBrowserManager.class);

		public BsUnitBrowserModel() {
			try {
				pBsUnit = manager.getBsUnit();
			} catch (OHServiceException e) {
				if (e.getMessages() != null) {
					for (OHExceptionMessage msg : e.getMessages()) {
						JOptionPane.showMessageDialog(null, msg.getMessage(), msg.getTitle() == null ? "" : msg.getTitle(), msg.getLevel().getSwingSeverity());
					}
				}
			}
		}

		public int getRowCount() {
			if (pBsUnit == null)
				return 0;
			return pBsUnit.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == 0) {
				return pBsUnit.get(r).getCode();
			} else if (c == -1) {
				return pBsUnit.get(r);
			} else if (c == 1) {
				return pBsUnit.get(r).getDescription();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			//return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

	public void bsUnitUpdated(AWTEvent e) {
		pBsUnit.set(selectedrow, bsUnit);
		//System.out.println("line -> " + selectedrow);
		((BsUnitBrowserModel) jTable.getModel()).fireTableDataChanged();
		jTable.updateUI();
		if ((jTable.getRowCount() > 0) && selectedrow > -1)
			jTable.setRowSelectionInterval(selectedrow, selectedrow);
	}

	public void bsUnitInserted(AWTEvent e) {
		pBsUnit.add(0, bsUnit);
		((BsUnitBrowserModel) jTable.getModel()).fireTableDataChanged();
		if (jTable.getRowCount() > 0)
			jTable.setRowSelectionInterval(0, 0);
	}

}
