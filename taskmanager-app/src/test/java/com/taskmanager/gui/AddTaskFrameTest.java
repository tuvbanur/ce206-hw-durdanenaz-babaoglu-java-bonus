package com.taskmanager.gui;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AddTaskFrameTest {
    private AddTaskFrame addTaskFrame;
    private MainMenuFrame mainMenuFrame;

@Before
public void setUp() {
    // Kategori veritabanına eklenmeli
    com.naz.taskmanager.model.Category category = new com.naz.taskmanager.model.Category("Test Category");
    com.naz.taskmanager.service.CategoryService categoryService = new com.naz.taskmanager.service.CategoryService();
    categoryService.addCategory(category);
    // Test kullanıcısı ile TaskService oluştur
    com.naz.taskmanager.service.TaskService taskService = new com.naz.taskmanager.service.TaskService("testUser");
    // En az bir görev ekle
    taskService.createTask("Test Task", "Test Description", category);

    mainMenuFrame = new MainMenuFrame("testUser");
    addTaskFrame = new AddTaskFrame(mainMenuFrame);
    // addTaskFrame.setVisible(true); // Headless modda pencere açma
}
    @Test
    public void testFrameInitialization() {
        assertNotNull("AddTaskFrame should not be null", addTaskFrame);
        assertEquals("Frame title should be 'Add New Task'", "Add New Task", addTaskFrame.getTitle());
    }

    @Test
    public void testComponentsInitialization() {
        // Test if all required components are initialized
        Component[] components = addTaskFrame.getContentPane().getComponents();
        assertTrue("Frame should have components", components.length > 0);
        
        // Test if the frame has the correct size
        assertEquals("Frame width should be 600", 600, addTaskFrame.getWidth());
        assertEquals("Frame height should be 500", 500, addTaskFrame.getHeight());
    }

    @Test
    public void testFormFields() {
        // Not: Bu testi çalıştırmak için AddTaskFrame sınıfında aşağıdaki getter metodları eklenmelidir:
        // getTaskTitleField(), getTaskDescriptionArea(), getDateSpinner(), getCategoryComboBox(), getPriorityComboBox()
        
        // Form alanlarının null olmaması gerekir
        assertNotNull("Task title field should not be null", addTaskFrame.getTaskTitleField());
        assertNotNull("Task description area should not be null", addTaskFrame.getTaskDescriptionArea());
        assertNotNull("Date spinner should not be null", addTaskFrame.getDateSpinner());
        assertNotNull("Category combo box should not be null", addTaskFrame.getCategoryComboBox());
        assertNotNull("Priority combo box should not be null", addTaskFrame.getPriorityComboBox());
        
        // ComboBox'ların doğru seçenekleri içermesi gerekir
        assertTrue("Category combo box should have items", addTaskFrame.getCategoryComboBox().getItemCount() > 0);
        assertTrue("Priority combo box should have items", addTaskFrame.getPriorityComboBox().getItemCount() > 0);
    }

    @Test
    public void testFrameDisposal() {
        addTaskFrame.dispose();
        assertFalse("Frame should not be visible after disposal", addTaskFrame.isVisible());
    }

    @Test
    public void testValidateForm_emptyFields() {
        addTaskFrame.getTaskTitleField().setText("");
        addTaskFrame.getTaskDescriptionArea().setText("");
        // Tarih ve comboboxlar zaten default değer alıyor
        // validateForm private olduğu için reflection ile çağırıyoruz
        try {
            java.lang.reflect.Method method = AddTaskFrame.class.getDeclaredMethod("validateForm");
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(addTaskFrame);
            assertFalse("Boş alanlarla form doğrulaması başarısız olmalı", result);
        } catch (Exception e) {
            fail("validateForm çağrısı başarısız: " + e.getMessage());
        }
    }

    @Test
    public void testValidateForm_validFields() {
        addTaskFrame.getTaskTitleField().setText("Başlık");
        addTaskFrame.getTaskDescriptionArea().setText("Açıklama");
        try {
            java.lang.reflect.Method method = AddTaskFrame.class.getDeclaredMethod("validateForm");
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(addTaskFrame);
            assertTrue("Dolu alanlarla form doğrulaması başarılı olmalı", result);
        } catch (Exception e) {
            fail("validateForm çağrısı başarısız: " + e.getMessage());
        }
    }

    @Test
    public void testSaveTask_withValidForm() {
        addTaskFrame.getTaskTitleField().setText("Başlık");
        addTaskFrame.getTaskDescriptionArea().setText("Açıklama");
        try {
            java.lang.reflect.Method method = AddTaskFrame.class.getDeclaredMethod("saveTask");
            method.setAccessible(true);
            method.invoke(addTaskFrame);
            // Burada bir Task kaydedildi mi kontrolü yapılabilir, ancak doğrudan veri tabanı/mock yoksa sadece hata olmamasını test edebiliriz
        } catch (Exception e) {
            fail("saveTask çağrısı başarısız: " + e.getMessage());
        }
    }

    @Test
    public void testButtonActions() {
        JButton saveButton = (JButton) getPrivateField(addTaskFrame, "saveButton");
        JButton cancelButton = (JButton) getPrivateField(addTaskFrame, "cancelButton");
        assertNotNull(saveButton);
        assertNotNull(cancelButton);
        // Save butonuna tıklama simülasyonu
        for (ActionListener al : saveButton.getActionListeners()) {
            al.actionPerformed(new java.awt.event.ActionEvent(saveButton, ActionEvent.ACTION_PERFORMED, ""));
        }
        // Cancel butonuna tıklama simülasyonu
        for (ActionListener al : cancelButton.getActionListeners()) {
            al.actionPerformed(new java.awt.event.ActionEvent(cancelButton, ActionEvent.ACTION_PERFORMED, ""));
        }
    }

    // Yardımcı: private alanlara erişim
    private Object getPrivateField(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }
} 