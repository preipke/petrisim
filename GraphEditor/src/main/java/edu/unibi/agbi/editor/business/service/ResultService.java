/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.editor.business.service;

import edu.unibi.agbi.editor.core.data.dao.ResultsDao;
import edu.unibi.agbi.editor.core.data.entity.data.IDataArc;
import edu.unibi.agbi.editor.core.data.entity.data.IDataNode;
import edu.unibi.agbi.editor.core.data.entity.result.Simulation;
import edu.unibi.agbi.editor.core.data.entity.result.ResultSet;
import edu.unibi.agbi.editor.business.exception.ResultsException;
import edu.unibi.agbi.editor.core.util.Utility;
import edu.unibi.agbi.petrinet.entity.IElement;
import edu.unibi.agbi.petrinet.entity.abstr.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author PR
 */
@Service
public class ResultService
{
    private final ResultsDao resultsDao;

    @Autowired private MessengerService messengerService;

    @Value("${regex.value.fire}") private String valueFire;
    @Value("${regex.value.speed}") private String valueSpeed;
    @Value("${regex.value.token}") private String valueToken;
    @Value("${regex.value.tokenIn.actual}") private String valueTokenInActual;
    @Value("${regex.value.tokenIn.total}") private String valueTokenInTotal;
    @Value("${regex.value.tokenOut.actual}") private String valueTokenOutActual;
    @Value("${regex.value.tokenOut.total}") private String valueTokenOutTotal;

    @Autowired
    public ResultService(ResultsDao resultsDao) {
        this.resultsDao = resultsDao;
        this.resultsDao.getSimulationResults().addListener(new ListChangeListener()
        {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                change.next();
                if (change.wasAdded()) {
                    try {
                        Simulation simulation
                                = ResultService.this.resultsDao.getSimulationResults()
                                        .get(ResultService.this.resultsDao.getSimulationResults().size() - 1);
                        AutoAddData(simulation);
                    } catch (ResultsException ex) {
                        messengerService.addException("Exception while auto adding results data!", ex);
                    }
                }
            }
        });
    }

    /**
     * Adds data to the results dao.
     *
     * @param simulationResult
     * @return indicates wether data has been added or not
     */
    public synchronized boolean add(Simulation simulationResult) {
        if (resultsDao.contains(simulationResult)) {
            return false;
        }
        resultsDao.add(simulationResult);
        return true;
    }

    /**
     * Gets the data for all performed simulations.
     *
     * @return
     */
    public synchronized ObservableList<Simulation> getSimulationResults() {
        return resultsDao.getSimulationResults();
    }

    /**
     * Adds the given line chart and related table item list to the storage.
     *
     * @param lineChart
     * @param tableView
     * @throws ResultsException
     */
    public synchronized void add(LineChart lineChart, TableView tableView) throws ResultsException {
        if (resultsDao.contains(lineChart)) {
            throw new ResultsException("Line chart has already been stored! Cannot overwrite existing data list.");
        }
        resultsDao.add(lineChart, tableView);
    }

    /**
     * Attempts to add data to a line charts corresponding table.
     *
     * @param lineChart
     * @param data
     * @throws ResultsException
     */
    public synchronized void add(LineChart lineChart, ResultSet data) throws ResultsException {
        if (resultsDao.contains(lineChart, data)) {
            throw new ResultsException("Duplicate entry for line chart");
        }
        resultsDao.add(lineChart, data);
    }

    /**
     * Drops all data related to a line chart.
     *
     * @param lineChart the line chart that will be dropped
     */
    public synchronized void drop(LineChart lineChart) {
        resultsDao.remove(lineChart);
    }

    /**
     * Drops the given data from the given chart and the table.
     *
     * @param lineChart the line chart that will be modified
     * @param data      the data to be hidden and removed from the also given
     *                  chart
     */
    public synchronized void drop(LineChart lineChart, ResultSet data) {
        hide(lineChart, data);
        resultsDao.remove(lineChart, data);
    }

    /**
     * Get simulation data related to a line chart.
     *
     * @param lineChart
     * @return
     */
    public synchronized List<ResultSet> getChartData(LineChart lineChart) {
        return resultsDao.getChartResultsList(lineChart);
    }
    
    /**
     * 
     * @param simulation
     * @param element
     * @param variable
     * @return
     * @throws ResultsException 
     */
    public ResultSet getResultSet(Simulation simulation, IElement element, String variable) throws ResultsException {
        
        ResultSet result;
        result = new ResultSet(simulation, element, variable);
        
        if (resultsDao.contains(result)) {
            result = resultsDao.get(result);
        } else {
            resultsDao.add(result);
        }
        updateSeries(result);
        result.getSeries().setName(getValueName(variable, simulation) + " (" + simulation.toStringShort() + ")");
        
        return result;
    }
    
    /**
     * Get result sets directly related to a given simulation and element only.
     * Should only be used for showing results in the inspector, as the isShown
     * boolean will affect all viewers that have this data in their table.
     * 
     * @param simulation
     * @param element
     * @return 
     * @throws ResultsException 
     */
    public List<ResultSet> getResultSets(Simulation simulation, IElement element) throws ResultsException {
        
        List<ResultSet> sets;
        Set<String> variables;
        
        if (simulation == null || element == null) {
            return new ArrayList();
        }
        
        sets = new ArrayList();
        variables = simulation.getElementFilter(element);
        
        if (element.getElementType() == Element.Type.PLACE) {
            for (String var : variables) {
                if (var.matches(valueToken)) { // use only .t per default
                    variables = new HashSet();
                    variables.add(var);
                    break;
                }
            }
        }
        
        for (String variable : variables) {
            sets.add(getResultSet(simulation, element, variable));
        }
        
        return sets;
    }

    /**
     * Removes the given data from the given chart.
     *
     * @param lineChart
     * @param data
     */
    public synchronized void hide(LineChart lineChart, ResultSet data) {
        lineChart.getData().remove(data.getSeries());
        data.setShown(false);
    }

    /**
     * Shows the given data in the given chart.
     *
     * @param lineChart
     * @param data
     * @throws ResultsException
     */
    public synchronized void show(LineChart lineChart, ResultSet data) throws ResultsException {
        updateSeries(data);
        if (data.getSeries() != null) {
            if (!lineChart.getData().contains(data.getSeries())) {
                lineChart.getData().add(data.getSeries());
            }
            data.setShown(true);
        } else {
            throw new ResultsException("No chart data available");
        }
    }

    public synchronized void addForAutoAdding(LineChart lineChart, ResultSet data) {
        resultsDao.addForAutoAdding(lineChart, data);
    }

    public synchronized boolean containsForAutoAdding(LineChart lineChart, ResultSet data) {
        return resultsDao.containsForAutoAdding(lineChart, data);
    }

    public synchronized void removeFromAutoAdding(LineChart lineChart, ResultSet data) {
        resultsDao.removeFromAutoAdding(lineChart, data);
    }

    public synchronized void UpdateAutoAddedData() throws ResultsException {
        for (LineChart lineChart : resultsDao.getLineChartsWithAutoAdding()) {
            for (ResultSet data : resultsDao.getChartTable(lineChart).getItems()) {
                updateSeries(data);
            }
            resultsDao.getChartTable(lineChart).refresh();
        }
    }

    public String getValueName(String value, Simulation simulation) {
        IDataArc arc;
        IDataNode node;
        String indexStr;
        int index;
        if (value.matches(valueFire)) {
            return "Firing";
        } else if (value.matches(valueSpeed)) {
            return "Speed";
        } else if (value.matches(valueToken)) {
            return "Token";
        } else if (value.matches(valueTokenInActual)) {
            indexStr = Utility.parseSubstring(value, "[", "]");
            if (indexStr != null) {
                index = Integer.parseInt(indexStr) - 1;
                node = (IDataNode) simulation.getFilterElement(value);
                if (node.getArcsIn().isEmpty()) {
                    return "Token from <" + index + "> [ACTUAL]";
                } else {
                    arc = (IDataArc) node.getArcsIn().get(index);
                    return "Token from " + arc.getSource().toString() + " [ACTUAL]";
                }
            } else {
                return null;
            }
        } else if (value.matches(valueTokenInTotal)) {
            indexStr = Utility.parseSubstring(value, "[", "]");
            if (indexStr != null) {
                index = Integer.parseInt(indexStr) - 1;
                node = (IDataNode) simulation.getFilterElement(value);
                if (node.getArcsIn().isEmpty()) {
                    return "Token from <" + index + "> [TOTAL]";
                } else {
                    arc = (IDataArc) node.getArcsIn().get(index);
                    return "Token from " + arc.getSource().toString() + " [TOTAL]";
                }
            } else {
                return null;
            }
        } else if (value.matches(valueTokenOutActual)) {
            indexStr = Utility.parseSubstring(value, "[", "]");
            if (indexStr != null) {
                index = Integer.parseInt(indexStr) - 1;
                node = (IDataNode) simulation.getFilterElement(value);
                if (node.getArcsOut().isEmpty()) {
                    return "Token to <" + index + "> [ACTUAL]";
                } else {
                    arc = (IDataArc) node.getArcsOut().get(index);
                    return "Token to " + arc.getTarget().toString() + " [ACTUAL]";
                }
            } else {
                return null;
            }
        } else if (value.matches(valueTokenOutTotal)) {
            indexStr = Utility.parseSubstring(value, "[", "]");
            if (indexStr != null) {
                index = Integer.parseInt(indexStr) - 1;
                node = (IDataNode) simulation.getFilterElement(value);
                if (node.getArcsOut().isEmpty()) {
                    return "Token to <" + index + "> [TOTAL]";
                } else {
                    arc = (IDataArc) node.getArcsOut().get(index);
                    return "Token to " + arc.getTarget().toString() + " [TOTAL]";
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Map<String, List<String>> getSharedValues(Simulation results, List<IElement> elements) {

        Map<String, List<String>> valuesTmp, valuesShared = null;

        Set<String> values, valuesRemoved;
        String name;

        for (IElement element : elements) {

            values = results.getElementFilter(element);
            valuesTmp = new HashMap();

            for (String value : values) {

                name = getValueName(value, results);

                if (!valuesTmp.containsKey(name)) {
                    valuesTmp.put(name, new ArrayList());
                }
                valuesTmp.get(name).add(value);
            }

            if (valuesShared == null) {

                valuesShared = valuesTmp;

            } else {

                valuesRemoved = new HashSet();

                for (String key : valuesShared.keySet()) {
                    if (valuesTmp.containsKey(key)) {
                        valuesShared.get(key).addAll(valuesTmp.get(key));
                    } else {
                        valuesRemoved.add(key);
                    }
                }
                for (String key : valuesRemoved) {
                    valuesShared.remove(key);
                }
            }
        }
        return valuesShared;
    }

    private synchronized void AutoAddData(Simulation simulation) throws ResultsException {

        Map<String, Map<IElement, Set<String>>> modelsToAutoAdd;
        Map<IElement, Set<String>> elementsToAutoAdd;
        Set<String> valuesToAutoAdd;
        ResultSet data;

        // validate all active charts
        for (LineChart lineChart : resultsDao.getLineChartsWithAutoAdding()) {

            modelsToAutoAdd = resultsDao.getDataAutoAdd(lineChart);

            if (modelsToAutoAdd != null) {

                elementsToAutoAdd = modelsToAutoAdd.get(simulation.getDao().getModelId());

                if (elementsToAutoAdd != null) {

                    // validate elements chosen for auto adding to be available
                    for (IElement elem : elementsToAutoAdd.keySet()) {

                        valuesToAutoAdd = elementsToAutoAdd.get(elem);

                        if (valuesToAutoAdd != null) {

                            // validate values chosen for auto adding to be valid
                            for (String valueToAutoAdd : valuesToAutoAdd) {

                                if (simulation.getFilterElement(valueToAutoAdd) != null) {

                                    // create and add data to chart
                                    data = new ResultSet(simulation, elem, valueToAutoAdd);

                                    try {
                                        add(lineChart, data);
                                    } catch (ResultsException ex) {
                                        System.out.println("Duplicate results entry");
                                    }
                                    show(lineChart, data);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the series for the given data object. Loads data from the
     * simulation and adds all additional entries to the series. Updates the
     * related chart.
     *
     * @param resultSet
     * @throws ResultsException
     */
    public synchronized void updateSeries(ResultSet resultSet) throws ResultsException {

        List<Object> data = resultSet.getData();
        List<Object> time = resultSet.getSimulation().getTimeData();
        int indexDataProcessed = resultSet.getDataProcessedIndex();

        if (data == null) {
            throw new ResultsException("");
        }

        XYChart.Series seriesOld = resultSet.getSeries();
        if (seriesOld == null || data.size() > indexDataProcessed) { // update only if additional values available

            XYChart.Series seriesNew = new XYChart.Series();

            if (seriesOld != null) {
                seriesNew.getData().addAll(seriesOld.getData());
            }

            /**
             * Attach data to series. TODO replace by downsampling.
             */
            for (int i = indexDataProcessed; i < data.size(); i++) {
                seriesNew.getData().add(new XYChart.Data(
                        (Number) time.get(i),
                        (Number) data.get(i)
                ));
                indexDataProcessed++;
            }
            resultSet.setDataProcessedIndex(indexDataProcessed);

            // Create label
//            if (resultSet.getElement().getName() != null
//                    && !resultSet.getElement().getName().isEmpty()) {
//                seriesNew.setName("'" + resultSet.getElement().getName() + "' (" + resultSet.getSimulation().toStringShort() + ")");
//            } else {
                seriesNew.setName("'" + resultSet.getElement().getId() + "' (" + resultSet.getSimulation().toStringShort() + ")");
//            }

            // Replace in chart
            if (seriesOld != null) {
                XYChart chart = seriesOld.getChart();
                if (chart != null) {
                    chart.getData().remove(seriesOld);
                    chart.getData().add(seriesNew);
                }
            }
            resultSet.setSeries(seriesNew);
        }
    }
}
