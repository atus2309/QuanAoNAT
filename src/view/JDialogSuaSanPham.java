/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view;

import dao.BrandDAO;
import dao.CategoryDAO;
import dao.ProductDAO;
import dao.ProductVariantDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Brand;
import model.Category;
import model.Product;
import model.ProductVariant;

/**
 *
 * @author Admin
 */
public class JDialogSuaSanPham extends javax.swing.JDialog {
    private ProductDAO productDAO = new ProductDAO();
    private ProductVariantDAO variantDAO = new ProductVariantDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();
    private Product currentProduct = new Product();
    private List<ProductVariant> variantList = new ArrayList<>();
    private Integer editingProductId = null;
    
    
    
    /**
     * Creates new form JDialogSuaSanPham
     */
    public JDialogSuaSanPham(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initLogic();
    }
    
    public JDialogSuaSanPham(java.awt.Frame parent, boolean modal, Integer productId) {
        super(parent, modal);
        this.editingProductId = productId;
        initComponents();
        initLogic();      
    }
    
     private void initLogic() {
        setLocationRelativeTo(getParent());
        
        // Thiết lập bảng biến thể
        tblVariants.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Mã SKU", "Màu sắc", "Kích thước", "Giá bán", "Số lượng tồn"}
        ));

        fillComboBoxes();
        
        if (editingProductId != null) {
            setTitle("Sửa thông tin sản phẩm");
            head.setText("SỬA SẢN PHẨM");
            jButton1.setText("Sửa sản phẩm");
            loadDataForEdit();
        } else {
            setTitle("Thêm sản phẩm mới");
            head.setText("THÊM SẢN PHẨM MỚI");
            jButton1.setText("Thêm sản phẩm");
            addVariantRow(); // Thêm dòng trống để người dùng bắt đầu nhập
        }
    }

    private void fillComboBoxes() {
        // Đổ dữ liệu cho ComboBox Danh mục
        DefaultComboBoxModel<Category> categoryModel = new DefaultComboBoxModel<>();
        cboCategory.setModel(categoryModel);
        categoryDAO.selectAll().forEach(categoryModel::addElement);

        // Đổ dữ liệu cho ComboBox Hãng
        DefaultComboBoxModel<Brand> brandModel = new DefaultComboBoxModel<>();
        cboBrand.setModel(brandModel);
        brandDAO.selectAll().forEach(brandModel::addElement);
    }
    
    private void loadDataForEdit() {
        currentProduct = productDAO.selectById(editingProductId);
        variantList = variantDAO.selectByProductId(editingProductId);

        if (currentProduct != null) {
            fillProductForm();
            fillVariantTable();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }
    
    private void fillProductForm() {
        txtProductName.setText(currentProduct.getProductName());
        txtDescription.setText(currentProduct.getDescription());

        if ("Nam".equalsIgnoreCase(currentProduct.getGender())) {
            radNam.setSelected(true);
        } else {
            radNu.setSelected(true);
        }

        cboSeason.setSelectedItem(currentProduct.getSeason());
        
        for (int i = 0; i < cboCategory.getModel().getSize(); i++) {
            if (cboCategory.getModel().getElementAt(i).getCategoryId() == currentProduct.getCategoryId()) {
                cboCategory.setSelectedIndex(i);
                break;
            }
        }
        
        for (int i = 0; i < cboBrand.getModel().getSize(); i++) {
            if (cboBrand.getModel().getElementAt(i).getBrandId() == currentProduct.getBrandId()) {
                cboBrand.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void fillVariantTable() {
        DefaultTableModel model = (DefaultTableModel) tblVariants.getModel();
        model.setRowCount(0);
        for (ProductVariant pv : variantList) {
            model.addRow(new Object[]{
                pv.getSkuCode(), pv.getColor(), pv.getSize(),
                pv.getRetailPrice(), pv.getStockQuantity()
            });
        }
    }
    
    private void addVariantRow() {
//        ((DefaultTableModel) tblVariants.getModel()).addRow(new Object[]{"", "", "", "0", "0"});
    }

    private void removeVariantRow() {
        int selectedRow = tblVariants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiên bản trong bảng để xoá.", "Chưa chọn phiên bản", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editingProductId != null && selectedRow < variantList.size()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa phiên bản này sẽ xóa nó vĩnh viễn khỏi CSDL.\nBạn có chắc chắn không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int variantIdToDelete = variantList.get(selectedRow).getVariantId();
                    variantDAO.delete(variantIdToDelete);
                    variantList.remove(selectedRow);
                    ((DefaultTableModel) tblVariants.getModel()).removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Đã xóa phiên bản khỏi CSDL.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa phiên bản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            ((DefaultTableModel) tblVariants.getModel()).removeRow(selectedRow);
        }
    }

    private void save() {
        try {
            if (txtProductName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!");
                return;
            }
            if (tblVariants.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Sản phẩm phải có ít nhất một phiên bản!");
                return;
            }

            currentProduct.setProductName(txtProductName.getText());
            currentProduct.setDescription(txtDescription.getText());
            currentProduct.setGender(radNam.isSelected() ? "Nam" : "Nữ");
            currentProduct.setSeason((String) cboSeason.getSelectedItem());
            currentProduct.setCategoryId(((Category) cboCategory.getSelectedItem()).getCategoryId());
            currentProduct.setBrandId(((Brand) cboBrand.getSelectedItem()).getBrandId());

            if (editingProductId == null) {
                productDAO.insertAndGetId(currentProduct);
                
            } else {
                productDAO.update(currentProduct);
            }

            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            this.dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá bán và Số lượng phải là số hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel3 = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        head = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVariants = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        cboSeason = new javax.swing.JComboBox<>();
        cboCategory = new javax.swing.JComboBox<>();
        radNu = new javax.swing.JRadioButton();
        radNam = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        cboBrand = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Tên sản phẩm");

        txtProductName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProductNameActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Danh mục");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Giới tính");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Mùa");

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane1.setViewportView(txtDescription);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("Mô tả");

        head.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        head.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        head.setText("Sửa sản phẩm");

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tblVariants.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblVariants);

        jButton2.setText("Thêm sản phẩm con");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Sửa sản phẩm con");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        cboSeason.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Xuân", "Hạ", "Thu", "Đông" }));

        buttonGroup1.add(radNu);
        radNu.setText("Nữ");

        buttonGroup1.add(radNam);
        radNam.setSelected(true);
        radNam.setText("Nam");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("Hãng");

        jButton4.setText("Xóa sản phẩm con");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Hủy");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(head, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel5))
                                        .addGap(45, 45, 45)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(radNam)
                                            .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel4)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel8)
                                                            .addGap(62, 62, 62)
                                                            .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel6)
                                                            .addGap(67, 67, 67)
                                                            .addComponent(cboSeason, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(radNu)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel7)
                                            .addGap(14, 14, 14)
                                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(head, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(radNu)
                            .addComponent(radNam)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cboSeason, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel8))
                            .addComponent(cboBrand, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtProductNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProductNameActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
       save();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        removeVariantRow();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    JDialogThemSuaSanPhamCon dialog = new JDialogThemSuaSanPhamCon(this, true, null);
    dialog.setVisible(true);

    // Sau khi dialog con đóng, kiểm tra xem người dùng có nhấn "Xác nhận" không
    if (dialog.isConfirmed()) {
        ProductVariant newVariant = dialog.getVariant();
        // Thêm dữ liệu mới vào bảng
        DefaultTableModel model = (DefaultTableModel) tblVariants.getModel();
        model.addRow(new Object[]{
            newVariant.getSkuCode(),
            newVariant.getColor(),
            newVariant.getSize(),
            newVariant.getRetailPrice(),
            newVariant.getStockQuantity()
        });
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblVariants.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiên bản trong bảng để sửa!");
        return;
    }

    // Lấy dữ liệu từ dòng đã chọn để tạo một đối tượng ProductVariant tạm
    DefaultTableModel model = (DefaultTableModel) tblVariants.getModel();
    ProductVariant variantToEdit = new ProductVariant();
    variantToEdit.setSkuCode(model.getValueAt(selectedRow, 0).toString());
    variantToEdit.setColor(model.getValueAt(selectedRow, 1).toString());
    variantToEdit.setSize(model.getValueAt(selectedRow, 2).toString());
    variantToEdit.setRetailPrice(new BigDecimal(model.getValueAt(selectedRow, 3).toString()));
    variantToEdit.setStockQuantity(Integer.parseInt(model.getValueAt(selectedRow, 4).toString()));

    // Mở dialog con ở chế độ SỬA (truyền đối tượng vừa tạo vào)
    JDialogThemSuaSanPhamCon dialog = new JDialogThemSuaSanPhamCon(this, true, variantToEdit);
    dialog.setVisible(true);

    // Sau khi dialog con đóng, kiểm tra và cập nhật lại bảng
    if (dialog.isConfirmed()) {
        ProductVariant editedVariant = dialog.getVariant();
        model.setValueAt(editedVariant.getSkuCode(), selectedRow, 0);
        model.setValueAt(editedVariant.getColor(), selectedRow, 1);
        model.setValueAt(editedVariant.getSize(), selectedRow, 2);
        model.setValueAt(editedVariant.getRetailPrice(), selectedRow, 3);
        model.setValueAt(editedVariant.getStockQuantity(), selectedRow, 4);
    }
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialogSuaSanPham.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialogSuaSanPham.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialogSuaSanPham.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialogSuaSanPham.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialogSuaSanPham dialog = new JDialogSuaSanPham(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<Brand> cboBrand;
    private javax.swing.JComboBox<Category> cboCategory;
    private javax.swing.JComboBox<String> cboSeason;
    private javax.swing.JLabel head;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton radNam;
    private javax.swing.JRadioButton radNu;
    private javax.swing.JTable tblVariants;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtProductName;
    // End of variables declaration//GEN-END:variables
}
