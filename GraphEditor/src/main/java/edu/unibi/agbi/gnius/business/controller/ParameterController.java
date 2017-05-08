/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gnius.business.controller;

import edu.unibi.agbi.gnius.core.exception.DataGraphServiceException;
import edu.unibi.agbi.gnius.core.exception.InputValidationException;
import edu.unibi.agbi.gnius.core.exception.ParameterServiceException;
import edu.unibi.agbi.gnius.core.model.entity.data.IDataElement;
import edu.unibi.agbi.gnius.core.model.entity.data.impl.DataPlace;
import edu.unibi.agbi.gnius.core.model.entity.data.impl.DataTransition;
import edu.unibi.agbi.gnius.core.service.DataGraphService;
import edu.unibi.agbi.gnius.core.service.MessengerService;
import edu.unibi.agbi.gnius.core.service.ParameterService;
import edu.unibi.agbi.petrinet.entity.INode;
import edu.unibi.agbi.petrinet.model.Parameter;
import edu.unibi.agbi.petrinet.model.Token;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

/**
 *
 * @author PR
 */
@Controller
public class ParameterController implements Initializable {

    @Autowired
    private ParameterService parameterService;

    @FXML
    private TextField inputParamName;
    @FXML
    private TextField inputParamNote;
    @FXML
    private TextField inputParamValue;
    @FXML
    private ChoiceBox<Parameter.Type> choiceParamScope;
    @FXML
    private Button buttonParamCreate;

    @FXML
    private TableView paramTableLocal;
    @FXML
    private TableColumn<Parameter, String> paramNameLocal;
    @FXML
    private TableColumn<Parameter, String> paramValueLocal;
    @FXML
    private TableColumn<Parameter, String> paramNoteLocal;
    @FXML
    private TableColumn<Parameter, Button> paramDeleteLocal;

    @FXML
    private TableView paramTableGlobal;
    @FXML
    private TableColumn<Parameter, String> paramNameGlobal;
    @FXML
    private TableColumn<Parameter, String> paramValueGlobal;
    @FXML
    private TableColumn<Parameter, String> paramNoteGlobal;
    @FXML
    private TableColumn<Parameter, Number> paramReferencesGlobal;
    @FXML
    private TableColumn<Parameter, Button> paramDeleteGlobal;

    @FXML
    private Label statusParamCreate;
    @FXML
    private Label statusParamDeleteGlobal;
    @FXML
    private Label statusParamDeleteLocal;

    private final ObservableList<Parameter> localParameters;
    private final ObservableList<Parameter> globalParameters;
    private IDataElement selectedElement;
    
    public ParameterController() {
        this.localParameters = FXCollections.observableArrayList();
        this.globalParameters = FXCollections.observableArrayList();
    }

    public void ShowParameterDetails(IDataElement elem) {
        ClearInputsAndLabel();
        RefreshLocalParameters(elem);
        RefreshGlobalParameterReferences();
    }

    /**
     * Clears inputs and sets the choices for places to choose from as value
     * references.
     */
    private void ClearInputsAndLabel() {
        inputParamName.clear();
        inputParamNote.clear();
        inputParamValue.clear();
        choiceParamScope.getSelectionModel().select(0);
        statusParamCreate.setText("");
        statusParamDeleteLocal.setText("");
        statusParamDeleteGlobal.setText("");
    }

    /**
     * Refreshes the available local parameters for an element.
     *
     * @param elem
     */
    private void RefreshLocalParameters(IDataElement elem) {
        
        List<Parameter> parameters = parameterService.getLocalParameters(elem);
        selectedElement = elem;
        
        localParameters.clear();
        if (elem != null) {
            parameters.addAll(elem.getParameters().values());
            parameters.sort(Comparator.comparing(Parameter::toString));
            localParameters.addAll(parameters);
            localParameters.stream().forEach(param -> {
                if (!parameterService.isElementReferencingParameter(elem, param)) {
                    param.removeReferingNode(elem);
                }
            });
        }
    }

    public void setStatus(String msg, boolean isError, Label label) {
        if (isError) {
            label.setTextFill(Color.RED);
        } else {
            label.setTextFill(Color.GREEN);
        }
        label.setText(msg);
    }

    /**
     * Creates a new parameter. Uses the given values to create either a local
     * or global parameter, getting name and value from the input textfields.
     *
     * @param id
     * @param type
     * @return
     * @throws DataGraphServiceException thrown in case of empty name and
     * misformatted value inputs or id conflicts while storing the new parameter
     */
    private void CreateParameter() {

        Parameter param;
        param = new Parameter(
                inputParamName.getText(),
                inputParamNote.getText(),
                inputParamValue.getText(),
                choiceParamScope.getSelectionModel().getSelectedItem());
        try {
            parameterService.add(param);
            setStatus("Created parameter '" + param.getId() + "'! [" + param.getType() + "]", false, statusParamCreate);
            inputParamName.clear();
            inputParamNote.clear();
            inputParamValue.clear();
        } catch (ParameterServiceException ex) {
            setStatus(ex.getMessage(), true, statusParamCreate);
        }
    }

    /**
     * Attempts to remove the given parameter.
     *
     * @param param
     * @param element
     * @throws DataGraphServiceException thrown in case the given parameter can
     * not be deleted from the dao - this is usually when there is elements
     * referring to this parameter
     */
    private void DeleteParameter(Parameter param, IDataElement element) throws DataGraphServiceException {
        parameterService.remove(param);
        if (element != null) {
            element.getParameters().remove(param.getId());
        }
        if (param.getType() == Parameter.Type.LOCAL) {
            localParameters.remove(param);
        } else {
            globalParameters.remove(param);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Callback<TableColumn<Parameter, Button>, TableCell<Parameter, Button>> paramDeleteLocalCellFactory;
        Callback<TableColumn<Parameter, Button>, TableCell<Parameter, Button>> paramDeleteGlobalCellFactory;
        Callback<TableColumn<Parameter, Number>, TableCell<Parameter, Number>> paramReferencesGlobalCellFactory;

        choiceParamScope.getItems().clear();
        choiceParamScope.getItems().add(Parameter.Type.GLOBAL);
        choiceParamScope.getItems().add(Parameter.Type.LOCAL);
        buttonParamCreate.setOnAction(e -> CreateParameter());

        /**
         * Table local parameters.
         */
        paramTableLocal.setItems(localParameters);
        paramTableLocal.setEditable(true);

        paramNameLocal.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getId()));
        paramValueLocal.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getValue()));
        paramValueLocal.setCellFactory(TextFieldTableCell.<Parameter>forTableColumn());
        paramValueLocal.setOnEditCommit(
                (CellEditEvent<Parameter, String> t) -> {
                    if (!t.getNewValue().matches(regexFunctionNumber)) {
                        setStatus("Invalid value specified!", true, statusParamDeleteLocal);
                    } else {
                        ((Parameter) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                                .setValue(t.getNewValue());
                    }
//                    String value = parseValue(t.getNewValue());
//                    if (value == null) {
//                        messengerService.setRightStatus("Given value cannot be parsed!");
//                    } else {
//                        ((Parameter) t.getTableView()
//                                .getItems()
//                                .get(t.getTablePosition().getRow()))
//                                .setValue(t.getNewValue());
//                    }
                });
        paramNoteLocal.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNote()));
        paramNoteLocal.setCellFactory(TextFieldTableCell.<Parameter>forTableColumn());
        paramNoteLocal.setOnEditCommit(
                (CellEditEvent<Parameter, String> t) -> {
                    ((Parameter) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                            .setNote(t.getNewValue());
                });
        paramDeleteLocal.setCellValueFactory(cellData -> {
            CheckBox cb = new CheckBox();
            cb.setAllowIndeterminate(true);
            cb.setIndeterminate(true);
            cb.setOnAction(e -> {
                try {
                    DeleteParameter(cellData.getValue(), selectedElement);
                    setStatus("Parameter '" + cellData.getValue().getId() + "' deleted! [LOCAL]", false, statusParamDeleteLocal);
                } catch (DataGraphServiceException ex) {
                    setStatus("Cannot delete '" + cellData.getValue().getId() + "'! [" + ex.getMessage() + "]", true, statusParamDeleteLocal);
                }
            });
            return new ReadOnlyObjectWrapper(cb);
        });
        paramDeleteLocalCellFactory = paramDeleteLocal.getCellFactory();
        paramDeleteLocal.setCellFactory(c -> {
            TableCell cell = paramDeleteLocalCellFactory.call(c);
            Tooltip tooltip = new Tooltip("Removes this parameter");
            cell.setTooltip(tooltip);
            return cell;
        });

        /**
         * Table global parameters.
         */
        paramTableGlobal.setItems(globalParameters);
        paramTableGlobal.setEditable(true);

        paramNameGlobal.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getId()));
        paramValueGlobal.setCellValueFactory(new PropertyValueFactory("value"));
        paramValueGlobal.setCellFactory(TextFieldTableCell.<Parameter>forTableColumn());
        paramValueGlobal.setOnEditCommit(
                (CellEditEvent<Parameter, String> t) -> {
                    if (!t.getNewValue().matches(regexFunctionNumber)) {
                        setStatus("Invalid value specified!", true, statusParamDeleteGlobal);
                    } else {
                        ((Parameter) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                                .setValue(t.getNewValue());
                    }
                });
        paramNoteGlobal.setCellValueFactory(new PropertyValueFactory("note"));
        paramNoteGlobal.setCellFactory(TextFieldTableCell.<Parameter>forTableColumn());
        paramNoteGlobal.setOnEditCommit(
                (CellEditEvent<Parameter, String> t) -> {
                    ((Parameter) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                            .setNote(t.getNewValue());
                });
        paramReferencesGlobal.setCellValueFactory(cellData -> cellData.getValue().getReferingNodesCountProperty());
        paramReferencesGlobalCellFactory = paramReferencesGlobal.getCellFactory();
        paramReferencesGlobal.setCellFactory(c -> {
            TableCell cell = paramReferencesGlobalCellFactory.call(c);
            Tooltip tooltip = new Tooltip();
            tooltip.textProperty().bind(cell.itemProperty().asString().concat(" elements are using this parameter."));
            cell.setTooltip(tooltip);
            return cell;
        });
        paramDeleteGlobal.setCellValueFactory(cellData -> {
            CheckBox cb = new CheckBox();
            cb.setAllowIndeterminate(true);
            cb.setIndeterminate(true);
            cb.setOnAction(e -> {
                try {
                    DeleteParameter(cellData.getValue(), null);
                    setStatus("Parameter '" + cellData.getValue().getId() + "' deleted! [GLOBAL]", false, statusParamDeleteGlobal);
                } catch (DataGraphServiceException ex) {
                    setStatus("Cannot delete '" + cellData.getValue().getId() + "'! [" + ex.getMessage() + "]", true, statusParamDeleteGlobal);
                }
            });
            return new ReadOnlyObjectWrapper(cb);
        });
        paramDeleteGlobalCellFactory = paramDeleteGlobal.getCellFactory();
        paramDeleteGlobal.setCellFactory(c -> {
            TableCell cell = paramDeleteGlobalCellFactory.call(c);
            Tooltip tooltip = new Tooltip("Removes this parameter");
            cell.setTooltip(tooltip);
            return cell;
        });
    }
}
