/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gnius.business.controller.menu;

import edu.unibi.agbi.gnius.business.controller.editor.TabsController;
import edu.unibi.agbi.gnius.business.controller.MainController;
import edu.unibi.agbi.gnius.core.io.XmlModelConverter;
import edu.unibi.agbi.gnius.core.model.dao.DataDao;
import edu.unibi.agbi.gnius.core.service.DataService;
import edu.unibi.agbi.gnius.core.service.MessengerService;
import edu.unibi.agbi.petrinet.util.OpenModelicaExporter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author PR
 */
@Controller
public class FileMenuController implements Initializable
{
    @Autowired private MessengerService messengerService;
    @Autowired private DataService dataService;
    
    @Autowired private MainController mainController;
    @Autowired private TabsController editorTabsController;

    @Autowired private XmlModelConverter xmlModelConverter;
    @Autowired private OpenModelicaExporter omExporter;

    @FXML private Menu menuOpenRecent;

    private final ExtensionFilter typeAll;
    private final ExtensionFilter typeXml;
    private final ExtensionFilter typeSbml;
    private final ExtensionFilter typeOm;

    private final FileChooser fileChooser;
    private final ObservableList<File> latestFiles;
    private ExtensionFilter latestFilter;

    public FileMenuController() {

        typeAll = new ExtensionFilter("All files", "*");
        typeXml = new ExtensionFilter("XML file(s) (*.xml)", "*.xml", "*.XML");
        typeSbml = new ExtensionFilter("SBML file(s) (*.sbml)", "*.sbml", "*.SBML");
        typeOm = new ExtensionFilter("OpenModelica file(s) (*.om)", "*.om", "*.OM");

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(typeXml);
//        fileChooser.getExtensionFilters().add(typeSbml);
        fileChooser.getExtensionFilters().add(typeOm);

        latestFiles = FXCollections.observableArrayList();
    }

    private void Open(File file) throws Exception {

        DataDao dataDao = xmlModelConverter.importXml(file);
        dataDao.setModelFile(file);
        editorTabsController.CreateModelTab(dataDao);
        
        if (latestFiles.contains(file)) {
            menuOpenRecent.getItems().remove(latestFiles.indexOf(file));
            latestFiles.remove(latestFiles.indexOf(file));
        }
        if (latestFiles.size() == 5) {
            menuOpenRecent.getItems().remove(4);
            latestFiles.remove(4);
        }
        latestFiles.add(0, file);
    }

    private boolean SaveFile(DataDao dao, File file, ExtensionFilter filter) {
        if (file == null || filter == null) {
            return false;
        }
        if (typeXml == filter) {
            return SaveXml(dao, file);
        } else if (typeSbml == filter) {
            return SaveSbml(dao, file);
        } else if (typeOm == filter) {
            return SaveOm(dao, file);
        } else {
            return false;
        }
    }
    
    private boolean SaveXml(DataDao dao, File file) {
        try {
            xmlModelConverter.exportXml(file, dao);
            messengerService.setTopStatus("XML export complete!", null);
            dataService.getActiveDao().setModelFile(file);
            dataService.getActiveDao().setHasChanges(false);
            return true;
        } catch (FileNotFoundException | ParserConfigurationException | TransformerException ex) {
            messengerService.setTopStatus("XML export failed!", ex);
            return false;
        }
    }

    private boolean SaveSbml(DataDao dao, File file) {
        messengerService.setTopStatus("SBML export is not yet implemented!", null);
        SaveAs();
        return false;
    }

    private boolean SaveOm(DataDao dao, File file) {
        try {
            omExporter.exportMO(dao.getModel(), file);
            messengerService.setTopStatus("OpenModelica export complete!", null);
        } catch (IOException ex) {
            messengerService.setTopStatus("OpenModelica export failed!", ex);
        }
        return false;
    }
    
    public File ShowFileChooser(DataDao dao) {
        if (dao != null) {
            fileChooser.setSelectedExtensionFilter(typeXml);
            fileChooser.setTitle("Save model '" + dao.getModel().getName() + "'");
            if (dao.getModelFile() != null) {
                fileChooser.setInitialDirectory(dao.getModelFile().getParentFile());
                fileChooser.setInitialFileName(dao.getModelFile().getName());
            } else {
                fileChooser.setInitialFileName(dao.getModel().getName());
            }
        }
        File file =  fileChooser.showSaveDialog(mainController.getStage());
        if (file != null) {
            latestFilter = fileChooser.getSelectedExtensionFilter();
        }
        return file;
    }
    
    @FXML
    public void New() {
        editorTabsController.CreateModelTab(null);
        messengerService.setTopStatus("New model created!", null);
    }

    @FXML
    public void Open() {
        if (!fileChooser.getExtensionFilters().contains(typeAll)) {
            fileChooser.getExtensionFilters().add(typeAll);
        }
        fileChooser.setSelectedExtensionFilter(typeAll);
        fileChooser.setTitle("Open model");
        File file = ShowFileChooser(null);
        if (file != null) {
            try {
                Open(file);
            } catch (Exception ex) {
                messengerService.setTopStatus("File import failed!", ex);
            }
        }
    }
    
    @FXML
    public void Quit() {
        mainController.ShowDialogExit(null);
    }

    @FXML
    public void Save() {
        if (dataService.getActiveDao().getModelFile() != null && latestFilter != null) {
            SaveFile(dataService.getActiveDao(), dataService.getActiveDao().getModelFile(), latestFilter);
        } else {
            SaveAs();
        }
    }
    
    /**
     * 
     * @param dao
     * @return indicates wether the model has been saved or not
     */
    public boolean SaveAs(DataDao dao) {
        File file = ShowFileChooser(dao);
        return SaveFile(dao, file, latestFilter);
    }

    @FXML
    public void SaveAs() {
        File file = ShowFileChooser(dataService.getActiveDao());
        SaveFile(dataService.getActiveDao(), file, latestFilter);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        latestFiles.addListener(new ListChangeListener()
        {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                if (change.next() && change.wasAdded()) {
                    change.getAddedSubList().forEach(f -> {
                        File file = (File) f;
                        MenuItem item = new MenuItem(file.getName() + " (" + file.getAbsolutePath() + ")");
                        item.setOnAction(e -> {
                            try {
                                Open(file);
                            } catch (Exception ex) {
                                messengerService.setTopStatus("File import failed!", ex);
                            }
                        });
                        menuOpenRecent.getItems().add(0, item);
                    });
                }
            }
        });
    }
}
