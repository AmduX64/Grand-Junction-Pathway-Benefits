import com.graphhopper.*;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.Graph;
//import com.graphhopper.storage.
import com.graphhopper.storage.RAMDirectory;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.ui.MiniGraphUI;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.DataFlagEncoder;
import com.graphhopper.routing.util.MapcRider2WeightFlagEncoder;

import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.LocationIndexTree;

import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.details.PathDetail;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import com.graphhopper
public class RouteIvestigator {
    public static void main(String[] args) {
        new RouteIvestigator().start();
    }
    private void start(){
        GraphHopper hopper = new GraphHopperOSM().forDesktop();
        hopper.setCHEnabled(false);
        String osmFile = "/home/arminakvn/Google Drive/mapc_works/app/graphhopper/latest.osm.pbf";
        String graphFolder = "./graphhoper_graph43c";
        hopper.setGraphHopperLocation(graphFolder);
        hopper.setDataReaderFile(osmFile);
//            DataFlagEncoder df = new DataFlagEncoder();

        hopper.setEncodingManager(new EncodingManager("mapcrider2"));
        hopper.setElevation(true);
        String datareaderfile = hopper.getDataReaderFile();
//            hopper.;
        hopper.importOrLoad();


        LocationIndex index = hopper.getLocationIndex();
        Double from_lat = 42.318242;
        Double from_lon = -71.105022;
        Double to_lat = 42.355167;
        Double to_lon = -71.061524;
//  now you can fetch the closest edge via:
        QueryResult qr = index.findClosest(from_lat, from_lon, EdgeFilter.ALL_EDGES );
        EdgeIteratorState edge = qr.getClosestEdge();


        DataFlagEncoder encoder = new DataFlagEncoder();
        MapcRider2WeightFlagEncoder mapcencoder = new MapcRider2WeightFlagEncoder(5,1,0);


//        EncodingManager em = new EncodingManager(encoder);
//        String graphFolder = "/home/arminakvn/graphhopper/graphhoper_graph4c";
//        GraphBuilder gb = new GraphBuilder(em).setLocation(graphFolder);
////        GraphStorage graph = gb.create();
////        graph.flush();
//        Graph graph = gb.load();
//
//        Double lat = 42.355147;
//        Double lon = -71.061609;
//        LocationIndex index = new LocationIndexTree(graph.getBaseGraph(), new RAMDirectory(graphFolder, false));
//        QueryResult qr = index.findClosest(lat, lon, EdgeFilter.ALL_EDGES );
//        EdgeIteratorState edge = qr.getClosestEdge();

        PointList way = edge.fetchWayGeometry(2);
        System.out.println("edge from qr "+edge);
//        System.out.println("edge from qr "+mapcencoder.getSurface(edge.getFlags()));
//        mapcencoder.getSurfaceAsString(edge.getFlags())
        System.out.println("edge name "+edge.getName());
        System.out.println("flags "+edge.getFlags());
        System.out.println("distance "+edge.getDistance());
        System.out.println(edge.isForward(mapcencoder));
        System.out.println(edge.isBackward(mapcencoder));
        System.out.println(edge.getEdge());
        System.out.println(index);
        System.out.println(mapcencoder.getSurfaceAsString(edge.getFlags()));


        List<String> pathDetails = new ArrayList<>();
        pathDetails.add(Parameters.DETAILS.EDGE_ID);
        pathDetails.add(Parameters.DETAILS.WEIGHT_VALUE);
        pathDetails.add(Parameters.DETAILS.STREET_NAME);
        pathDetails.add(Parameters.DETAILS.AVERAGE_SPEED);
        pathDetails.add(Parameters.DETAILS.DISTANCE);
        pathDetails.add(Parameters.DETAILS.TIME);
//        pathDetails.add(Parameters.DETAILS.FACILITIES_OVERAL);
        GHRequest req = new GHRequest(from_lat, from_lon, to_lat, to_lon).
                setWeighting("fastest").
                setVehicle("mapcrider2").
                setLocale(Locale.US).
                setPathDetails(pathDetails).
                setAlgorithm(Parameters.Algorithms.ALT_ROUTE);
        GHResponse rsp = hopper.route(req);
        PathWrapper path = rsp.getBest();
        List<PathWrapper> allpath = rsp.getAll();
        InstructionList il = path.getInstructions();
        Map<String, List<PathDetail>> pdet = path.getPathDetails();
        double distance = path.getDistance();
        Map<String, List<PathDetail>> path_details = path.getPathDetails();
//        for (int i = 0; i <path_details.size() ; i++) {

            System.out.println("details   "+path_details.get("edge_id"));
            System.out.println("details   "+path_details.get("street_name"));
            System.out.println("details   "+path_details.get("weight_value"));
//        System.out.println("facilities_overal   "+path_details.get("facilities_overal"));
//        }


        long timeInMs = path.getTime();
        double weight = path.getRouteWeight();
        long alltimes = allpath.get(1).getTime();
        List<Trip.Leg> tlegs = path.getLegs();
        System.out.println("distance   "+distance);
        System.out.println("timeInMs   "+timeInMs);
        System.out.println("route weight   "+weight);
        System.out.println("pdet   "+pdet.size());
        for (int i = 0; i <allpath.size() ; i++) {
            System.out.println("ALTERNATIVE ROUTE " + i);
            PathWrapper p = allpath.get(i);
            Map<String, List<PathDetail>> p_details = p.getPathDetails();
            System.out.println("details edge ids "+p_details.get("edge_id").get(1));
            System.out.println("details street_names  "+p_details.get("street_name").get(1));
            System.out.println("details weight_values  "+p_details.get("weight_value").get(1));
            System.out.println("details average_speed  "+p_details.get("average_speed").get(1));
        }
        Table t = null;
        try {
            t = Table.read().csv("./resources/grand_blocks.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(t);

//        mapcencoder.getDouble()
//        mapcencoder.getSpeed()

//        index.
// Load index
//        LocationIndex index = new LocationIndexTree(graph.getBaseGraph(), new RAMDirectory("graphhopper_folder", true));
//        if (!index.loadExisting())
//            throw new IllegalStateException("location index cannot be loaded!");
//        GraphHopperStorage graph = gb.create();

//        GraphStorage graph = gb.load();
// Make a weighted edge between two nodes.
//        List<String> pathDetails = new ArrayList<>();
//            PathDetail pd = new PathDetail("weight_value");
//            List<String> det_reqs = Collections.emptyList();
//        pathDetails.add(Parameters.DETAILS.EDGE_ID);

//        EdgeIteratorState edge = graph.edge(98981,168134);
//        edge.setDistance(distance);
//        edge.setFlags(encoder.setProperties(speed, true, true));
// Flush to disc
//        graph.flush();

    }
}
