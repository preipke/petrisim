package service.datagraph;

import main.TestFXBase;
import edu.unibi.agbi.gnius.core.model.entity.data.IDataArc;
import edu.unibi.agbi.gnius.core.model.entity.data.IDataNode;
import edu.unibi.agbi.gnius.core.model.entity.graph.IGraphArc;
import edu.unibi.agbi.gnius.core.model.entity.graph.IGraphNode;
import edu.unibi.agbi.gnius.core.service.exception.DataGraphServiceException;
import edu.unibi.agbi.gravisfx.entity.IGravisConnection;
import edu.unibi.agbi.gravisfx.entity.IGravisNode;
import edu.unibi.agbi.petrinet.entity.IArc;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author PR
 */
public class DaoTests extends TestFXBase {

    int placeCount = 5;
    int transitionCount = 3;
    IGraphArc tmpArc;
    IGraphNode tmpNode;

    @Test
    public void CreateNodes() throws DataGraphServiceException {

        List<IGraphNode> places = CreatePlaces(placeCount);
        List<IGraphNode> transitions = CreateTransitions(transitionCount);

        Assert.assertEquals(placeCount, places.size());
        Assert.assertEquals(transitionCount, transitions.size());

        Assert.assertEquals(
                places.size() + transitions.size(),
                dataDao.getPlacesAndTransitions().size(),
                graphDao.getNodes().size());
        Assert.assertEquals(
                places.size(),
                dataDao.getPlaces().size());
        Assert.assertEquals(
                transitions.size(),
                dataDao.getTransitions().size());
        Assert.assertEquals(
                0,
                dataDao.getArcs().size(),
                graphDao.getConnections().size());
    }

    @Test
    public void ConnectAndValidateNodes() throws DataGraphServiceException {

        List<IGraphNode> places = CreatePlaces(placeCount);
        List<IGraphNode> transitions = CreateTransitions(transitionCount);
        
        CreateConnections(places, transitions);

        Assert.assertEquals(
                places.size() * transitions.size() * 2,
                dataDao.getArcs().size(),
                graphDao.getConnections().size());
        
        IGraphArc arc;
        for (IGravisConnection connection : graphDao.getConnections()) {
            arc = (IGraphArc) connection;
            Assert.assertEquals(true, arc.getSource().getGraphConnections().contains(arc));
            Assert.assertEquals(true, arc.getTarget().getGraphConnections().contains(arc));
            Assert.assertEquals(true, arc.getDataElement().getSource().getArcsOut().contains(arc.getDataElement()));
            Assert.assertEquals(true, arc.getDataElement().getTarget().getArcsIn().contains(arc.getDataElement()));
        }
        
        for (IGraphNode place : places) {
            
            for (IGraphNode transition : transitions) {
                place.getChildren().contains(transition);
                place.getParents().contains(transition);
            }
            
            Assert.assertEquals(
                    place.getGraphConnections().size() / 2,
                    place.getDataElement().getArcsIn().size(),
                    transitions.size());
            Assert.assertEquals(
                    place.getGraphConnections().size() / 2,
                    place.getDataElement().getArcsOut().size(),
                    transitions.size());
        }
        
        for (IGraphNode transition : transitions) {
            
            for (IGraphNode place : places) {
                transition.getChildren().contains(place);
                transition.getParents().contains(place);
            }
            
            Assert.assertEquals(
                    transition.getGraphConnections().size() / 2,
                    transition.getDataElement().getArcsIn().size(),
                    places.size());
            Assert.assertEquals(
                    transition.getGraphConnections().size() / 2,
                    transition.getDataElement().getArcsOut().size(),
                    places.size());
        }
    }

    @Test
    public void RemoveNodesAndValidate() throws DataGraphServiceException {

        List<IGraphNode> places = CreatePlaces(placeCount);
        List<IGraphNode> transitions = CreateTransitions(transitionCount);
        CreateConnections(places, transitions);

        IGraphNode node;
        IDataNode nodeData;
        
        while (!graphDao.getNodes().isEmpty()) {

            node = (IGraphNode) graphDao.getNodes().remove(getRandomIndex(graphDao.getNodes()));
            nodeData = node.getDataElement();
            RemoveNode(node);

            Assert.assertEquals(false, graphDao.contains(node));
            Assert.assertEquals(false, dataDao.getPlaces().contains(nodeData));
            Assert.assertEquals(false, dataDao.getTransitions().contains(nodeData));
            Assert.assertEquals(false, dataDao.getPlacesAndTransitions().contains(nodeData));
            
            // removed node can neither be parent nor child of any other node
            for (IGravisNode parent : node.getParents()) {
                Assert.assertEquals(false, parent.getChildren().contains(node));
            }
            for (IGravisNode child : node.getChildren()) {
                Assert.assertEquals(false, child.getParents().contains(node));
            }

            // no connection to the removed node is to be found!
            List<IGravisConnection> connections = graphDao.getConnections();
            for (IGravisConnection connection : connections) {
                Assert.assertNotEquals(node, connection.getSource());
                Assert.assertNotEquals(node, connection.getTarget());
            }
            Collection<IArc> arcs = dataDao.getArcs();
            for (IArc arc : arcs) {
                Assert.assertNotEquals(nodeData, arc.getSource());
                Assert.assertNotEquals(nodeData, arc.getTarget());
            }
        }
    }

    @Test
    public void RemoveConnectionsAndValidate() throws DataGraphServiceException {

        List<IGraphNode> places = CreatePlaces(placeCount);
        List<IGraphNode> transitions = CreateTransitions(transitionCount);
        
        CreateConnections(places, transitions);
        
        IGraphArc arc;
        IDataArc arcData;
        
        while (!graphDao.getConnections().isEmpty()) {
            
            arc = (IGraphArc) graphDao.getConnections().remove(getRandomIndex(graphDao.getConnections()));
            arcData = arc.getDataElement();
            RemoveArc(arc);

            Assert.assertEquals(false, graphDao.contains(arc));
            Assert.assertEquals(false, dataDao.getArcs().contains(arcData));
            
            Assert.assertEquals(false, arc.getSource().getGraphConnections().contains(arc));
            Assert.assertEquals(false, arc.getTarget().getGraphConnections().contains(arc));
            
            Assert.assertEquals(false, arcData.getSource().getArcsOut().contains(arcData));
            Assert.assertEquals(false, arcData.getTarget().getArcsIn().contains(arcData));
        }
    }
}
